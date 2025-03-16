[![CI](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/main.yml/badge.svg)](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/main.yml)
[![front](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/front.yml/badge.svg)](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/front.yml)

# Introduction

`Spring Boot` + `kotlin`/ `angular` application that leverages `Spring-AI` with `OpenAi`.
`Github actions` produce a `linux/arm64` ready docker image that can run on a Raspberry Pi for example.

Don't forget to export the `SPRING_AI_OPENAI_API_KEY` environment variable.  

# Steps

- Build the image : `mvn clean -Pnative spring-boot:build-image`.
- Run the image :
```bash
docker run -e SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL=gpt-4o-mini \
  -e SPRING_AI_OPENAI_API_KEY=$SPRING_AI_OPENAI_API_KEY \
  -e MPALOURDAI_DEFAULTSYSTEMFILEPATH=/container/defaultsystem.txt \
  -v /host/defaultsystem.txt:/container/defaultsystem.txt \
  -p 8080:8080 mpalourd-ai:X.X.X`
```
# Image ready to test ?

Just grab [this image generated from GitHub actions](https://mpalourdio.github.io/mpalourd-ai/mpalourdai.tar).

# Failed to create the main Isolate. (code 24) ?

Follow [these instructions](https://pimylifeup.com/raspberry-pi-page-size/) for Raspberry PI 5.
