use std::path::{Path, PathBuf};

use jni::objects::{JClass, JObject, JString};
use jni::sys::{jboolean, jobjectArray, jstring};
use jni::JNIEnv;

use zbox::Error;

use super::throw;

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniValidate(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
) {
    if path.is_null() {
        throw(&env, Error::InvalidPath);
        return;
    }
    let path: String = env.get_string(path).unwrap().into();
    if !Path::new(&path).has_root() {
        throw(&env, Error::InvalidPath);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniParent(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let path = Path::new(&path);
    if !path.has_root() {
        throw(&env, Error::InvalidPath);
        return env.new_string(String::new()).unwrap().into_inner();
    }
    match path.parent() {
        Some(parent) => env.new_string(parent.to_str().unwrap()),
        None => env.new_string("/"),
    }
    .unwrap()
    .into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniFileName(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let path = Path::new(&path);
    match path.file_name() {
        Some(file_name) => env.new_string(file_name.to_str().unwrap()),
        None => env.new_string(""),
    }
    .unwrap()
    .into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniStripPrefix(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
    base: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let base: String = env.get_string(base).unwrap().into();
    let path = Path::new(&path);
    match path.strip_prefix(&base) {
        Ok(result) => env
            .new_string(result.to_str().unwrap())
            .unwrap()
            .into_inner(),
        Err(_) => *JObject::null(),
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniStartsWith(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
    base: JString,
) -> jboolean {
    let path: String = env.get_string(path).unwrap().into();
    let base: String = env.get_string(base).unwrap().into();
    let path = Path::new(&path);
    path.starts_with(&base) as u8
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniEndsWith(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
    child: JString,
) -> jboolean {
    let path: String = env.get_string(path).unwrap().into();
    let child: String = env.get_string(child).unwrap().into();
    let path = Path::new(&path);
    path.ends_with(&child) as u8
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniFileStem(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let path = Path::new(&path);
    match path.file_stem() {
        Some(stem) => env.new_string(stem.to_str().unwrap()),
        None => env.new_string(""),
    }
    .unwrap()
    .into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniExtension(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let path = Path::new(&path);
    match path.extension() {
        Some(ext) => env.new_string(ext.to_str().unwrap()),
        None => env.new_string(""),
    }
    .unwrap()
    .into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniJoin(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
    path2: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let path2: String = env.get_string(path2).unwrap().into();
    let path = Path::new(&path);
    let new_path = path.join(&path2);
    env.new_string(new_path.to_str().unwrap())
        .unwrap()
        .into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniPush(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
    other: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let other: String = env.get_string(other).unwrap().into();
    let mut path = PathBuf::from(&path);
    path.push(&other);
    env.new_string(path.to_str().unwrap()).unwrap().into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniPop(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let mut path = PathBuf::from(&path);
    path.pop();
    env.new_string(path.to_str().unwrap()).unwrap().into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniSetFileName(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
    file_name: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let file_name: String = env.get_string(file_name).unwrap().into();
    let path = Path::new(&path);
    let new_path = path.with_file_name(&file_name);
    env.new_string(new_path.to_str().unwrap())
        .unwrap()
        .into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniSetExtension(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
    ext: JString,
) -> jstring {
    let path: String = env.get_string(path).unwrap().into();
    let ext: String = env.get_string(ext).unwrap().into();
    let path = Path::new(&path);
    let new_path = path.with_extension(&ext);
    env.new_string(new_path.to_str().unwrap())
        .unwrap()
        .into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Path_jniComponents(
    env: JNIEnv,
    _cls: JClass,
    path: JString,
) -> jobjectArray {
    let path: String = env.get_string(path).unwrap().into();
    let path = Path::new(&path);
    let comps: Vec<String> = path
        .components()
        .map(|comp| comp.as_os_str().to_str().unwrap().to_owned())
        .collect();
    let objs = env
        .new_object_array(
            comps.len() as i32,
            "java/lang/String",
            JObject::null(),
        )
        .unwrap();
    for (i, comp) in comps.iter().enumerate() {
        let comp_str = env.new_string(comp).unwrap();
        env.set_object_array_element(objs, i as i32, *comp_str)
            .unwrap();
        env.delete_local_ref(*comp_str).unwrap();
    }
    objs
}
