use jni::objects::{JObject, JString};
use jni::sys::{jboolean, jint};
use jni::JNIEnv;

use zbox::{Cipher, MemLimit, OpsLimit, RepoOpener};

use super::{check_version_limit, throw, u8_to_bool, RUST_OBJ_FIELD};

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniOpsLimit(
    env: JNIEnv,
    obj: JObject,
    limit: jint,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.ops_limit(OpsLimit::from(limit));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniMemLimit(
    env: JNIEnv,
    obj: JObject,
    limit: jint,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.mem_limit(MemLimit::from(limit));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniCipher(
    env: JNIEnv,
    obj: JObject,
    cipher: jint,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.cipher(Cipher::from(cipher));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniCreate(
    env: JNIEnv,
    obj: JObject,
    create: jboolean,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.create(u8_to_bool(create));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniCreateNew(
    env: JNIEnv,
    obj: JObject,
    create_new: jboolean,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.create_new(u8_to_bool(create_new));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniCompress(
    env: JNIEnv,
    obj: JObject,
    compress: jboolean,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.compress(u8_to_bool(compress));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniVersionLimit(
    env: JNIEnv,
    obj: JObject,
    limit: jint,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let limit = check_version_limit(limit);
    opener.version_limit(limit as u8);
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniDedupChunk(
    env: JNIEnv,
    obj: JObject,
    dedup: jboolean,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.dedup_chunk(u8_to_bool(dedup));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniReadOnly(
    env: JNIEnv,
    obj: JObject,
    read_only: jboolean,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.read_only(u8_to_bool(read_only));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniForce(
    env: JNIEnv,
    obj: JObject,
    force: jboolean,
) {
    let mut opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    opener.force(u8_to_bool(force));
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_RepoOpener_jniOpen<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
    uri: JString,
    pwd: JString,
) -> JObject<'a> {
    let opener = env
        .get_rust_field::<&str, RepoOpener>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let uri: String = env.get_string(uri).unwrap().into();
    let pwd: String = env.get_string(pwd).unwrap().into();
    match opener.open(&uri, &pwd) {
        Ok(repo) => {
            let repo_obj =
                env.new_object("io/zbox/zboxfs/Repo", "()V", &[]).unwrap();
            env.set_rust_field(repo_obj, RUST_OBJ_FIELD, repo).unwrap();
            repo_obj
        }
        Err(err) => {
            let ret = JObject::null();
            throw(&env, err);
            ret
        }
    }
}
