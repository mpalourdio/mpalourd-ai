[![CI](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/main.yml/badge.svg)](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/main.yml)
[![front](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/front.yml/badge.svg)](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/front.yml)

# Introduction

`Spring Boot` / `angular` application that leverages `Spring-AI` with `openai`.
It produces a `linux/arm64` ready docker image, and can be run on a Raspberry Pi for example.

Don't forget to export the `OPENAI_API_KEY` environment variable.

# Steps

- Install QEMU (is you do not build on `linux/arm64`) : `docker run --privileged --rm tonistiigi/binfmt --install all` / `docker run --rm --privileged multiarch/qemu-user-static --reset -p yes`.
- Validate that it works: `docker run --platform=linux/arm64 --rm -t arm64v8/ubuntu uname -m`. It should return `aarch64`.
- Build the image for `arm64` architecture : `mvn clean -Pnative,front spring-boot:build-image -Dspring-boot.build-image.imagePlatform=linux/arm64`.
- Drink some coffee, learn haskell, build linux from scratch, or just wait for the build to finish. It will take some time.
- Save the image : `docker save -o /tmp/mpalourdai.tar docker.io/library/mpalourd-ai:0.0.1-SNAPSHOT`.
- Move the image to the target host : `scp /tmp/mpalourdai.tar pi@192.168.X.X:wherever/`.
- On the target host : `docker load -i wherever/mpalourdai.tar`.
- Run the image : `docker run -e SPRING_AI_OPENAI_API_KEY=$SPRING_AI_OPENAI_API_KEY -p8080:8080 mpalourd-ai:0.0.1-SNAPSHOT`.

# Image ready to test ?

Just grab [this image generated from GitHub actions](https://mpalourdio.github.io/mpalourd-ai/mpalourdai.tar).

# Failed to create the main Isolate. (code 24) ?

Follow [these instructions](https://pimylifeup.com/raspberry-pi-page-size/) for Raspberry PI 5.
