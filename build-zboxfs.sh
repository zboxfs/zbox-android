set -ex

# build zboxfs binding
docker run --rm -v $PWD/zboxfs:/root/zbox zboxfs/android /bin/bash -c "cargo build --target x86_64-linux-android --release && cargo build --target aarch64-linux-android --release"

# copy so files to Android library project
cp zboxfs/target/x86_64-linux-android/release/libzboxfs.so fs/src/main/jniLibs/x86_64
cp zboxfs/target/aarch64-linux-android/release/libzboxfs.so fs/src/main/jniLibs/arm64-v8a
