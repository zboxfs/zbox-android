FROM zboxfs/base

RUN apt-get update && apt-get install -yq \
    python \
    unzip \
    zip

# add rust target for arm64 and x86_64
RUN rustup target add aarch64-linux-android x86_64-linux-android

# download Android NDK
RUN cd /usr/local && \
    wget -q http://dl.google.com/android/repository/android-ndk-r20-linux-x86_64.zip && \
    unzip -q android-ndk-r20-linux-x86_64.zip && \
    rm android-ndk-r20-linux-x86_64.zip
ENV ANDROID_NDK_HOME /usr/local/android-ndk-r20
ENV NDK_PLATFORM android-21

# build libsodium for arm64 and x86_64
WORKDIR ${LIBSODIUM_HOME}
RUN ./dist-build/android-armv8-a.sh && \
    ./dist-build/android-x86_64.sh && \
    mkdir /opt/ndk && \
    ln -s ${LIBSODIUM_HOME}/android-toolchain-armv8-a /opt/ndk/arm64 && \
    ln -s ${LIBSODIUM_HOME}/android-toolchain-westmere /opt/ndk/x86_64
ENV PATH $PATH:/opt/ndk/arm64/bin:/opt/ndk/x86_64/bin

# environment variables for static linking with libsodium
ENV SODIUM_STATIC=true

# setup for pkg-config
ENV PKG_CONFIG_ALLOW_CROSS 1
ENV PKG_CONFIG_PATH_aarch64_linux_android ${LIBSODIUM_HOME}/libsodium-android-armv8-a/lib/pkgconfig
ENV PKG_CONFIG_PATH_x86_64_linux_android ${LIBSODIUM_HOME}/libsodium-android-westmere/lib/pkgconfig

# make a dummy project and pre-build dependencies
RUN mkdir -p /tmp/zbox/.cargo
WORKDIR /tmp/zbox
COPY ./zboxfs/Cargo.toml ./
COPY ./zboxfs/.cargo/config .cargo/
RUN mkdir src && \
    echo "// dummy file" > src/lib.rs && \
    echo "fn main() {}" > build.rs && \
    cargo build --target x86_64-linux-android --release && \
    cargo build --target aarch64-linux-android --release && \
    rm -rf /tmp/zbox

# set work dir
WORKDIR /root/zbox
