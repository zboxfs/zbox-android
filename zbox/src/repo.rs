use jni::objects::{JClass, JObject, JString, JValue};
use jni::sys::{jboolean, jint, jobjectArray, JNI_FALSE};
use jni::JNIEnv;

use zbox::{MemLimit, OpsLimit, Repo};

use super::{
    metadata_to_jobject, throw, time_to_secs, versions_to_jobjects,
    RUST_OBJ_FIELD,
};

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniExists(
    env: JNIEnv,
    _cls: JClass,
    uri: JString,
) -> jboolean {
    let uri: String = env.get_string(uri).unwrap().into();
    match Repo::exists(&uri) {
        Ok(ret) => ret as u8,
        Err(ref err) => {
            let ret = JNI_FALSE;
            throw(&env, err);
            ret
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniInfo<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
) -> JObject<'a> {
    let repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();

    let info = repo.info();
    if let Err(ref err) = info {
        let ret = JObject::null();
        throw(&env, err);
        return ret;
    }
    let info = info.unwrap();

    let info_obj = env
        .new_object("io/zbox/zboxfs/RepoInfo", "()V", &[])
        .unwrap();

    let vol_id = env
        .byte_array_from_slice(info.volume_id().as_ref())
        .unwrap();
    let ver = env.new_string(info.version()).unwrap();
    let uri = env.new_string(info.uri()).unwrap();

    let ops_str = format!("{:?}", info.ops_limit()).to_uppercase();
    let ops_limit = env.new_string(ops_str).unwrap();
    let ops_obj = env
        .call_static_method(
            "io/zbox/zboxfs/OpsLimit",
            "valueOf",
            "(Ljava/lang/String;)Lio/zbox/zboxfs/OpsLimit;",
            &[JValue::Object(*ops_limit)],
        )
        .unwrap();

    let mem_str = format!("{:?}", info.mem_limit()).to_uppercase();
    let mem_limit = env.new_string(mem_str).unwrap();
    let mem_obj = env
        .call_static_method(
            "io/zbox/zboxfs/MemLimit",
            "valueOf",
            "(Ljava/lang/String;)Lio/zbox/zboxfs/MemLimit;",
            &[JValue::Object(*mem_limit)],
        )
        .unwrap();

    let cipher_str = format!("{:?}", info.cipher()).to_uppercase();
    let cipher = env.new_string(cipher_str).unwrap();
    let cipher_obj = env
        .call_static_method(
            "io/zbox/zboxfs/Cipher",
            "valueOf",
            "(Ljava/lang/String;)Lio/zbox/zboxfs/Cipher;",
            &[JValue::Object(*cipher)],
        )
        .unwrap();

    env.set_field(
        info_obj,
        "volumeId",
        "[B",
        JValue::Object(JObject::from(vol_id)),
    )
    .unwrap();
    env.set_field(
        info_obj,
        "version",
        "Ljava/lang/String;",
        JValue::Object(JObject::from(ver)),
    )
    .unwrap();
    env.set_field(
        info_obj,
        "uri",
        "Ljava/lang/String;",
        JValue::Object(JObject::from(uri)),
    )
    .unwrap();
    env.set_field(info_obj, "opsLimit", "Lio/zbox/zboxfs/OpsLimit;", ops_obj)
        .unwrap();
    env.set_field(info_obj, "memLimit", "Lio/zbox/zboxfs/MemLimit;", mem_obj)
        .unwrap();
    env.set_field(info_obj, "cipher", "Lio/zbox/zboxfs/Cipher;", cipher_obj)
        .unwrap();
    env.set_field(
        info_obj,
        "compress",
        "Z",
        JValue::Bool(info.compress() as u8),
    )
    .unwrap();
    env.set_field(
        info_obj,
        "versionLimit",
        "I",
        JValue::Int(i32::from(info.version_limit())),
    )
    .unwrap();
    env.set_field(
        info_obj,
        "dedupChunk",
        "Z",
        JValue::Bool(info.dedup_chunk() as u8),
    )
    .unwrap();
    env.set_field(
        info_obj,
        "isReadOnly",
        "Z",
        JValue::Bool(info.is_read_only() as u8),
    )
    .unwrap();
    env.set_field(
        info_obj,
        "createdAt",
        "J",
        JValue::Long(time_to_secs(info.created_at())),
    )
    .unwrap();

    info_obj
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniResetPassword(
    env: JNIEnv,
    obj: JObject,
    old_pwd: JString,
    new_pwd: JString,
    ops_limit: jint,
    mem_limit: jint,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let old_pwd: String = env.get_string(old_pwd).unwrap().into();
    let new_pwd: String = env.get_string(new_pwd).unwrap().into();
    if let Err(ref err) = repo.reset_password(
        &old_pwd,
        &new_pwd,
        OpsLimit::from(ops_limit),
        MemLimit::from(mem_limit),
    ) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniRepairSuperBlock(
    env: JNIEnv,
    _obj: JObject,
    uri: JString,
    pwd: JString,
) {
    let uri: String = env.get_string(uri).unwrap().into();
    let pwd: String = env.get_string(pwd).unwrap().into();
    if let Err(ref err) = Repo::repair_super_block(&uri, &pwd) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniPathExists(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) -> jboolean {
    let repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    match repo.path_exists(&path) {
        Ok(result) => result as u8,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniIsFile(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) -> jboolean {
    let repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    match repo.is_file(&path) {
        Ok(result) => result as u8,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniIsDir(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) -> jboolean {
    let repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    match repo.is_dir(&path) {
        Ok(result) => result as u8,
        Err(ref err) => {
            throw(&env, err);
            0
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniCreateFile<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
    path: JString,
) -> JObject<'a> {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();

    let path: String = env.get_string(path).unwrap().into();
    match repo.create_file(&path) {
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

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniOpenFile<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
    path: JString,
) -> JObject<'a> {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    match repo.open_file(&path) {
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

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniCreateDir(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    if let Err(ref err) = repo.create_dir(&path) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniCreateDirAll(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    if let Err(ref err) = repo.create_dir_all(&path) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniReadDir(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) -> jobjectArray {
    let repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    match repo.read_dir(&path) {
        Ok(ents) => {
            let objs = env
                .new_object_array(
                    ents.len() as i32,
                    "io/zbox/zboxfs/DirEntry",
                    JObject::null(),
                )
                .unwrap();

            for (i, ent) in ents.iter().enumerate() {
                let ent_obj = env
                    .new_object("io/zbox/zboxfs/DirEntry", "()V", &[])
                    .unwrap();
                let path_str =
                    env.new_string(ent.path().to_str().unwrap()).unwrap();
                let name_str = env.new_string(ent.file_name()).unwrap();
                let meta_obj = metadata_to_jobject(&env, ent.metadata());

                let path_obj =
                    env.new_object("io/zbox/zboxfs/Path", "()V", &[]).unwrap();
                env.set_field(
                    path_obj,
                    "path",
                    "Ljava/lang/String;",
                    JValue::Object(JObject::from(path_str)),
                )
                .unwrap();
                env.set_field(
                    ent_obj,
                    "path",
                    "Lio/zbox/zboxfs/Path;",
                    JValue::Object(path_obj),
                )
                .unwrap();
                env.delete_local_ref(path_obj).unwrap();

                env.set_field(
                    ent_obj,
                    "fileName",
                    "Ljava/lang/String;",
                    JValue::Object(JObject::from(name_str)),
                )
                .unwrap();
                env.set_field(
                    ent_obj,
                    "metadata",
                    "Lio/zbox/zboxfs/Metadata;",
                    JValue::Object(meta_obj),
                )
                .unwrap();

                env.set_object_array_element(objs, i as i32, ent_obj)
                    .unwrap();

                env.delete_local_ref(ent_obj).unwrap();
                env.delete_local_ref(*path_str).unwrap();
                env.delete_local_ref(*name_str).unwrap();
                env.delete_local_ref(meta_obj).unwrap();
            }

            objs
        }
        Err(ref err) => {
            let ret = env
                .new_object_array(0, "io/zbox/zboxfs/DirEntry", JObject::null())
                .unwrap();
            throw(&env, err);
            ret
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniMetadata<'a>(
    env: JNIEnv<'a>,
    obj: JObject,
    path: JString,
) -> JObject<'a> {
    let repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    match repo.metadata(&path) {
        Ok(meta) => metadata_to_jobject(&env, meta),
        Err(ref err) => {
            let ret = JObject::null();
            throw(&env, err);
            ret
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniHistory(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) -> jobjectArray {
    let repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    versions_to_jobjects(&env, repo.history(&path))
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniCopy(
    env: JNIEnv,
    obj: JObject,
    from: JString,
    to: JString,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let from: String = env.get_string(from).unwrap().into();
    let to: String = env.get_string(to).unwrap().into();
    if let Err(ref err) = repo.copy(&from, &to) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniCopyDirAll(
    env: JNIEnv,
    obj: JObject,
    from: JString,
    to: JString,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let from: String = env.get_string(from).unwrap().into();
    let to: String = env.get_string(to).unwrap().into();
    if let Err(ref err) = repo.copy_dir_all(&from, &to) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniRemoveFile(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    if let Err(ref err) = repo.remove_file(&path) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniRemoveDir(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    if let Err(ref err) = repo.remove_dir(&path) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniRemoveDirAll(
    env: JNIEnv,
    obj: JObject,
    path: JString,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let path: String = env.get_string(path).unwrap().into();
    if let Err(ref err) = repo.remove_dir_all(&path) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniRename(
    env: JNIEnv,
    obj: JObject,
    from: JString,
    to: JString,
) {
    let mut repo = env
        .get_rust_field::<&str, Repo>(obj, RUST_OBJ_FIELD)
        .unwrap();
    let from: String = env.get_string(from).unwrap().into();
    let to: String = env.get_string(to).unwrap().into();
    if let Err(ref err) = repo.rename(&from, &to) {
        throw(&env, err);
    }
}

#[no_mangle]
pub extern "system" fn Java_io_zbox_zboxfs_Repo_jniDestroy(
    env: JNIEnv,
    _obj: JObject,
    uri: JString,
) {
    let uri: String = env.get_string(uri).unwrap().into();
    if let Err(ref err) = Repo::destroy(&uri) {
        throw(&env, err);
    }
}
