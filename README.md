# zbox-andriod

This package is Android binding for [ZboxFS].

ZboxFS is a zero-details, privacy-focused in-app file system. Its goal is
to help application store files securely, privately and reliably. Check out more
details about [ZboxFS].

# How to Use

Add below dependency snippet to your module's gradle file:

```gradle
implementation 'io.zbox:zboxfs:0.2.0'
```

# Documentation

Check the Java doc [reference documentation](https://docs.zbox.io/android).

# Sample Android App

[Treno] is a sample Android app using this package. It shows how to use [ZboxFS]
to build a Content Provider and save app data securely and privately.

# How to Build

You need [Docker] and [JDK] to build this package.

1. Build Docker Image

```sh
./build-docker.sh
```

This will build Docker image which is used for building ZboxFS Android binding.

2. Build ZboxFS Android Binding

```sh
./build-zboxfs.sh
```

This will build ZboxFS Android binding library for both `x86_64` and `aarch64`
targets. The library files will be copied to Android project's `jniLibs`
directory.

3. Build Zbox Android Library

```sh
./gradlew install
```

4. Upload to Bintray

```sh
./gradlew bintrayUpload
```

# License

This package is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE)
file for details.

[ZboxFS]: https://github.com/zboxfs/zbox
[Docker]: https://www.docker.com/
[JDK]: https://www.oracle.com/technetwork/java/javase/downloads/index.html
[Treno]: https://github.com/zboxfs/treno
