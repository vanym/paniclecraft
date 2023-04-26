#!/bin/bash

set -e

DEPS_VOLUME="minecraft-mods-build-deps"

mkdir -p .gradle build
chown --reference=. .gradle build
docker run --rm -it \
    -v "$PWD:/root/src:ro" \
    -w /root/src \
    --tmpfs /root/src/build \
    -v "$PWD/libs:/root/src/libs:rw" \
    -v "$PWD/out:/root/src/build/libs:rw" \
    --tmpfs /root/src/.gradle \
    -v "$DEPS_VOLUME":/root/.gradle/caches \
    -v "$DEPS_VOLUME":/root/.gradle/wrapper \
    openjdk:8u342-jdk-bullseye ./gradlew jar --no-daemon
chown --reference=. -R ./libs ./out
