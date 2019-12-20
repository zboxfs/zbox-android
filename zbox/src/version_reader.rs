use std::io::{Read, Seek, SeekFrom};

use jni::objects::{JByteBuffer, JObject, JValue};
use jni::sys::{jint, jlong};
use jni::JNIEnv;

use zbox::{Error, VersionReader};

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
        Err(err) => {
            throw(&env, Error::from(err));
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

    // get file content length
    let content_len = match rdr.version() {
        Ok(ver) => ver.content_len(),
        Err(err) => {
            let ret = JObject::null();
            throw(&env, err);
            return JByteBuffer::from(ret);
        }
    };

    // get current position of file
    let curr_pos = match rdr.seek(SeekFrom::Current(0)) {
        Ok(pos) => pos as usize,
        Err(err) => {
            let ret = JObject::null();
            throw(&env, Error::from(err));
            return JByteBuffer::from(ret);
        }
    };

    // calculate direct buffer length needed
    let len = if content_len > curr_pos {
        content_len - curr_pos
    } else {
        0
    };

    // allocate a direct byte buffer on Java side, this is to let JVM to handle
    // buffer release
    let buf_obj = env
        .call_static_method(
            "java/nio/ByteBuffer",
            "allocateDirect",
            "(I)Ljava/nio/ByteBuffer;",
            &[JValue::from(len as i32)],
        )
        .unwrap()
        .l()
        .unwrap();
    let buf = JByteBuffer::from(buf_obj);
    let dst = env.get_direct_buffer_address(buf).unwrap();

    // read file content to direct byte buffer
    let mut offset = 0;
    loop {
        match rdr.read(&mut dst[offset..]) {
            Ok(read) => {
                if read == 0 {
                    break;
                }
                offset += read;
            }
            Err(err) => {
                let ret = JObject::null();
                throw(&env, Error::from(err));
                return JByteBuffer::from(ret);
            }
        }
    }

    // set direct buffer limit
    env.call_method(
        *buf,
        "limit",
        "(I)Ljava/nio/Buffer;",
        &[JValue::from(offset as i32)],
    )
    .unwrap();

    buf
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
        Err(err) => {
            throw(&env, Error::from(err));
            0
        }
    }
}
