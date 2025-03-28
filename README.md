[![CI](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/main.yml/badge.svg)](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/main.yml)
[![front](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/front.yml/badge.svg)](https://github.com/mpalourdio/mpalourd-ai/actions/workflows/front.yml)

# Introduction

`Spring Boot (kotlin)` + `angular` application that leverages `Spring-AI` with `OpenAi`.
`Github actions` produce a `linux/arm64` ready docker image that can run on a Raspberry Pi for example.

Don't forget to export the `SPRING_AI_OPENAI_API_KEY` environment variable on your host system.  

# Steps
- Create a docker network: `docker network create mpalourd-ai`
- Build the image: `mvn clean -Pnative spring-boot:build-image`.
- Run a Vector database
```bash
$ docker run -it --rm -d \
    --name pgvector \
    --network=mpalourd-ai \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=pgvector \
    -e PGDATA=/var/lib/postgresql/data/pgdata \
    -v ~/pgmount/pgvector:/var/lib/postgresql/data \
     pgvector/pgvector:pg16
```
- Run the image :
```bash
docker run \
  --network=mpalourd-ai
  -e SPRING_AI_OPENAI_API_KEY=$SPRING_AI_OPENAI_API_KEY \
  -e EXTERNAL_API_URL=https://xxxxxx \
  -e MPALOURDAI_DEFAULTSYSTEMFILEPATH=/container/defaultsystem.txt \
  -v /host/defaultsystem.txt:/container/defaultsystem.txt \
  -p 8080:8080 ghcr.io/mpalourdio/mpalourd-ai:latest
```

# Image ready to test ?

`docker pull ghcr.io/mpalourdio/mpalourd-ai:latest`.

# Failed to create the main Isolate. (code 24) ?

Follow [these instructions](https://pimylifeup.com/raspberry-pi-page-size/) for Raspberry PI 5.
