use std::str::FromStr;

use android_logger::{init_once, Config};
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;
use log::Level;

use zbox::{init_env, zbox_version};

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Env_initEnv(
    env: JNIEnv,
    _class: JClass,
    level: JString,
) {
    let lvl_str: String = env.get_string(level).unwrap().into();
    let lvl = Level::from_str(&lvl_str).unwrap();

    init_once(Config::default().with_min_level(lvl).with_tag("zboxfs"));

    init_env(env);
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Env_version(
    env: JNIEnv,
    _class: JClass,
) -> jstring {
    let version = zbox_version();
    let output = env.new_string(version).unwrap();
    output.into_inner()
}
