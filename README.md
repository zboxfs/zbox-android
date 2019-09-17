# zbox-andriod

This package is Android binding for [ZboxFS].

ZboxFS is a zero-details, privacy-focused in-app file system. Its goal is
to help application store files securely, privately and reliably. Check more
details about [ZboxFS].

**This package is still WIP, do not use now**

# How to Build

You need [Docker](https://www.docker.com/) to build this package.

## Build Docker Image

```sh
./build-docker.sh
```

This will build Docker image which is used for building ZboxFS Android binding.

## Build ZboxFS Android Binding

```sh
./build-zboxfs.sh
```

This will build ZboxFS Android binding library for both x86_64 and aarch64
targets. The library files will be copied to Android project's `jniLibs`
directory.

## Build Android Library

```sh
./gradlew build
```

# License

This package is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE)
file for details.

[ZboxFS]: https://github.com/zboxfs/zbox
