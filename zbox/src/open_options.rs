use jni::objects::{JObject, JString};
use jni::sys::{jboolean, jint};
use jni::JNIEnv;

use zbox::{OpenOptions, Repo};

use super::{check_version_limit, throw, u8_to_bool, RUST_OBJ_FIELD};

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniRead(
    env: JNIEnv,
    obj: JObject,
    read: jboolean,
) {
    let mut opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opts.read(u8_to_bool(read));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniWrite(
    env: JNIEnv,
    obj: JObject,
    write: jboolean,
) {
    let mut opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opts.write(u8_to_bool(write));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniAppend(
    env: JNIEnv,
    obj: JObject,
    append: jboolean,
) {
    let mut opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opts.append(u8_to_bool(append));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniTruncate(
    env: JNIEnv,
    obj: JObject,
    truncate: jboolean,
) {
    let mut opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opts.truncate(u8_to_bool(truncate));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniCreate(
    env: JNIEnv,
    obj: JObject,
    create: jboolean,
) {
    let mut opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opts.create(u8_to_bool(create));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniCreateNew(
    env: JNIEnv,
    obj: JObject,
    create_new: jboolean,
) {
    let mut opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opts.create_new(u8_to_bool(create_new));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniVersionLimit(
    env: JNIEnv,
    obj: JObject,
    limit: jint,
) {
    let mut opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let limit = check_version_limit(limit);
    opts.version_limit(limit as u8);
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniDedupChunk(
    env: JNIEnv,
    obj: JObject,
    dedup: jboolean,
) {
    let mut opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opts.dedup_chunk(u8_to_bool(dedup));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_OpenOptions_jniOpen<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
    repo: JObject,
    path: JString,
) -> JObject<'a> {
    let opts = env
        .get_rust_field::<&str, OpenOptions>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let mut repo = env
        .get_rust_field::<&str, Repo>(repo, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    match opts.open(&mut repo, &path) {
        Ok(file) => {
            let file_obj =
                env.new_object("io/zbox/zboxfs/File", "()V", &[]).unwrap();
            env.set_rust_field(file_obj, RUST_OBJ_FIELD, file).unwrap();
            file_obj
        }
        Err(ref err) => {
            let ret = JObject::null();
            throw(&env, err);
            ret
        }
    }
}
