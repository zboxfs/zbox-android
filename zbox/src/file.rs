use std::io::{Read, Seek, Write};

use jni::objects::{JByteBuffer, JObject};
use jni::sys::{jint, jlong, jobjectArray};
use jni::JNIEnv;

use zbox::File;

use super::{
    metadata_to_jobject, throw, to_seek_from, versions_to_jobjects,
    RUST_OBJ_FIELD,
};

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniMetadata<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
) -> JObject<'a> {
    let file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    match file.metadata() {
        Ok(meta) => metadata_to_jobject(&env, meta),
        Err(ref err) => {
            let ret = JObject::null();
            throw(&env, err);
            ret
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniHistory(
    env: JNIEnv,
    obj: JObject,
) -> jobjectArray {
    let file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    versions_to_jobjects(&env, file.history())
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniCurrVersion(
    env: JNIEnv,
    obj: JObject,
) -> jlong {
    let file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    match file.curr_version() {
        Ok(ver) => ver as i64,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniVersionReader<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
    ver_num: jlong,
) -> JObject<'a> {
    let file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    match file.version_reader(ver_num as usize) {
        Ok(rdr) => {
            let rdr_obj = env
                .new_object("io/zbox/zboxfs/VersionReader", "()V", &[])
                .unwrap();
            env.set_rust_field(rdr_obj, RUST_OBJ_FIELD, rdr).unwrap();
            rdr_obj
        }
        Err(ref err) => {
            let ret = JObject::null();
            throw(&env, err);
            ret
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniFinish(
    env: JNIEnv,
    obj: JObject,
) {
    let mut file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    if let Err(ref err) = file.finish() {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniWriteOnce(
    env: JNIEnv,
    obj: JObject,
    buf: JByteBuffer,
) {
    let mut file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let buf = env.get_direct_buffer_address(buf).unwrap();
    if let Err(ref err) = file.write_once(buf) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniSetLen(
    env: JNIEnv,
    obj: JObject,
    len: jlong,
) {
    let mut file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    if let Err(ref err) = file.set_len(len as usize) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniRead(
    env: JNIEnv,
    obj: JObject,
    dst: JByteBuffer,
) -> jlong {
    let mut file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let dst = env.get_direct_buffer_address(dst).unwrap();
    match file.read(dst) {
        Ok(read) => read as i64,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniReadAll<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
) -> JByteBuffer<'a> {
    let mut file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let mut dst = Vec::new();
    if let Err(ref err) = file.read_to_end(&mut dst) {
        throw(&env, err);
    }
    env.new_direct_byte_buffer(&mut dst).unwrap()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniWrite(
    env: JNIEnv,
    obj: JObject,
    buf: JByteBuffer,
) -> jlong {
    let mut file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let buf = env.get_direct_buffer_address(buf).unwrap();
    match file.write(buf) {
        Ok(written) => written as i64,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_File_jniSeek(
    env: JNIEnv,
    obj: JObject,
    offset: jlong,
    whence: jint,
) -> jlong {
    let mut file = env
        .get_rust_field::<&str, File>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let whence = to_seek_from(offset, whence);
    match file.seek(whence) {
        Ok(pos) => pos as i64,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}
