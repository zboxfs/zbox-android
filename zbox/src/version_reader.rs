use std::io::{Read, Seek};

use jni::objects::{JByteBuffer, JObject};
use jni::sys::{jint, jlong};
use jni::JNIEnv;

use zbox::VersionReader;

use super::{throw, to_seek_from, RUST_OBJ_FIELD};

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_VersionReader_jniRead(
    env: JNIEnv,
    obj: JObject,
    dst: JByteBuffer,
) -> jlong {
    let mut rdr = env
        .get_rust_field::<&str, VersionReader>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let dst = env.get_direct_buffer_address(dst).unwrap();
    match rdr.read(dst) {
        Ok(read) => read as i64,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_VersionReader_jniReadAll<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
) -> JByteBuffer<'a> {
    let mut rdr = env
        .get_rust_field::<&str, VersionReader>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let mut dst = Vec::new();
    if let Err(ref err) = rdr.read_to_end(&mut dst) {
        throw(&env, err);
    }
    env.new_direct_byte_buffer(&mut dst).unwrap()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_VersionReader_jniSeek(
    env: JNIEnv,
    obj: JObject,
    offset: jlong,
    whence: jint,
) -> jlong {
    let mut rdr = env
        .get_rust_field::<&str, VersionReader>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let whence = to_seek_from(offset, whence);
    match rdr.seek(whence) {
        Ok(pos) => pos as i64,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}
