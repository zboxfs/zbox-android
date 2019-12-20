#![allow(non_snake_case)]

extern crate android_logger;
extern crate jni;
//#[macro_use]
extern crate log;
extern crate zbox;

mod env;
mod file;
mod open_options;
mod path;
mod repo;
mod repo_opener;
mod version_reader;

use std::error::Error as StdError;
use std::io::SeekFrom;
use std::time::{SystemTime, UNIX_EPOCH};

use jni::objects::{JObject, JString, JThrowable, JValue};
use jni::sys::{jint, jobjectArray};
use jni::JNIEnv;

use zbox::{
    Error, File, Metadata, OpenOptions, Repo, RepoOpener, Result, Version,
    VersionReader,
};

// field name in Java class to hold its Rust object
const RUST_OBJ_FIELD: &str = "rustObj";

// field name in Java class to identify Rust object
// 100 - RepoOpener
// 101 - Repo
// 102 - OpenOptions
// 103 - File
// 104 - VersionReader
const RUST_OBJID_FIELD: &str = "rustObjId";

#[inline]
fn u8_to_bool(a: u8) -> bool {
    match a {
        0 => false,
        1 => true,
        _ => unreachable!(),
    }
}

#[inline]
fn time_to_secs(t: SystemTime) -> i64 {
    t.duration_since(UNIX_EPOCH).unwrap().as_secs() as i64
}

#[inline]
fn check_version_limit(limit: jint) -> jint {
    if 1 <= limit && limit <= 255 {
        limit
    } else {
        0
    }
}

#[inline]
fn to_seek_from(offset: i64, whence: jint) -> SeekFrom {
    match whence {
        0 => SeekFrom::Start(offset as u64),
        1 => SeekFrom::Current(offset),
        2 => SeekFrom::End(offset),
        _ => unimplemented!(),
    }
}

fn throw(env: &JNIEnv, err: Error) {
    let msg = if env.exception_check().unwrap() {
        // get exception on java side and re-throw it with its message
        let exception = env.exception_occurred().unwrap();
        env.exception_describe().unwrap();
        env.exception_clear().unwrap();
        let jval = env
            .call_method(*exception, "toString", "()Ljava/lang/String;", &[])
            .unwrap();
        let msg = JString::from(jval.l().unwrap());
        env.get_string(msg).unwrap().into()
    } else {
        err.description().to_string()
    };

    let err_no = err.into();
    let msg_obj = env.new_string(format!("{} ({})", msg, err_no)).unwrap();

    // throw customised exception object with error code
    let ex_obj = JThrowable::from(
        env.new_object(
            "io/zbox/zboxfs/ZboxException",
            "(ILjava/lang/String;)V",
            &[JValue::Int(err_no), JValue::Object(*msg_obj)],
        )
        .unwrap(),
    );
    let _ = env.throw(ex_obj);
}

fn metadata_to_jobject<'a>(env: &JNIEnv<'a>, meta: Metadata) -> JObject<'a> {
    let meta_obj = env
        .new_object("io/zbox/zboxfs/Metadata", "()V", &[])
        .unwrap();

    let ftype_str = format!("{:?}", meta.file_type()).to_uppercase();
    let ftype = env.new_string(ftype_str).unwrap();
    let ftype_obj = env
        .call_static_method(
            "io/zbox/zboxfs/FileType",
            "valueOf",
            "(Ljava/lang/String;)Lio/zbox/zboxfs/FileType;",
            &[JValue::Object(*ftype)],
        )
        .unwrap();

    env.set_field(meta_obj, "fileType", "Lio/zbox/zboxfs/FileType;", ftype_obj)
        .unwrap();
    env.set_field(
        meta_obj,
        "contentLen",
        "J",
        JValue::Long(meta.content_len() as i64),
    )
    .unwrap();
    env.set_field(
        meta_obj,
        "currVersion",
        "I",
        JValue::Int(meta.curr_version() as i32),
    )
    .unwrap();
    env.set_field(
        meta_obj,
        "createdAt",
        "J",
        JValue::Long(time_to_secs(meta.created_at())),
    )
    .unwrap();
    env.set_field(
        meta_obj,
        "modifiedAt",
        "J",
        JValue::Long(time_to_secs(meta.modified_at())),
    )
    .unwrap();

    env.delete_local_ref(*ftype).unwrap();
    env.delete_local_ref(ftype_obj.l().unwrap()).unwrap();

    meta_obj
}

fn versions_to_jobjects(
    env: &JNIEnv,
    history: Result<Vec<Version>>,
) -> jobjectArray {
    match history {
        Ok(vers) => {
            let objs = env
                .new_object_array(
                    vers.len() as i32,
                    "io/zbox/zboxfs/Version",
                    JObject::null(),
                )
                .unwrap();

            for (i, ver) in vers.iter().enumerate() {
                let ver_obj = env
                    .new_object("io/zbox/zboxfs/Version", "()V", &[])
                    .unwrap();

                env.set_field(
                    ver_obj,
                    "num",
                    "J",
                    JValue::Long(ver.num() as i64),
                )
                .unwrap();
                env.set_field(
                    ver_obj,
                    "contentLen",
                    "J",
                    JValue::Long(ver.content_len() as i64),
                )
                .unwrap();
                env.set_field(
                    ver_obj,
                    "createdAt",
                    "J",
                    JValue::Long(time_to_secs(ver.created_at())),
                )
                .unwrap();

                env.set_object_array_element(objs, i as i32, ver_obj)
                    .unwrap();

                env.delete_local_ref(ver_obj).unwrap();
            }

            objs
        }
        Err(err) => {
            let ret = env
                .new_object_array(0, "io/zbox/zboxfs/Version", JObject::null())
                .unwrap();
            throw(&env, err);
            ret
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RustObject_jniSetRustObj(
    env: JNIEnv,
    obj: JObject,
) {
    let cls = env.get_object_class(obj).unwrap();
    let cls = env.auto_local(*cls);
    let rust_obj_id =
        env.get_static_field(&cls, RUST_OBJID_FIELD, "I").unwrap();
    match rust_obj_id.i().unwrap() {
        100 => {
            let rust_obj = RepoOpener::new();
            env.set_rust_field(obj, RUST_OBJ_FIELD, rust_obj).unwrap();
        }
        102 => {
            let rust_obj = OpenOptions::new();
            env.set_rust_field(obj, RUST_OBJ_FIELD, rust_obj).unwrap();
        }
        _ => {}
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RustObject_jniTakeRustObj(
    env: JNIEnv,
    obj: JObject,
) {
    let cls = env.get_object_class(obj).unwrap();
    let cls = env.auto_local(*cls);
    let rust_obj_id =
        env.get_static_field(&cls, RUST_OBJID_FIELD, "I").unwrap();
    match rust_obj_id.i().unwrap() {
        100 => {
            env.take_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
                .unwrap();
        }
        101 => {
            env.take_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
                .unwrap();
        }
        102 => {
            env.take_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
                .unwrap();
        }
        103 => {
            env.take_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
                .unwrap();
        }
        104 => {
            env.take_rust_field::<&str, VersionReader>(obj, RUST_OBJ_FIELD)
                .unwrap();
        }
        _ => {}
    }
}
