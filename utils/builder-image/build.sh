#!/bin/env bash
set -e

TAG="2.7-1"

DIR=$(dirname "${BASH_SOURCE[0]}")
ROOT="$DIR"/../..

mkdir -p .gradle build
chown --reference=. .gradle build
docker run --rm -it \
    --network none \
    -v "$ROOT:/root/src:ro" \
    -w /root/src \
    --tmpfs /root/src/build \
    -v "$ROOT/out:/root/src/build/libs:rw" \
    --tmpfs /root/src/.gradle \
    ghcr.io/vanym/paniclecraft/builder:"$TAG" ./gradlew --offline assemble --no-daemon
chown --reference=. -R "$ROOT/out"
