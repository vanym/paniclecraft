#!/bin/env bash
set -e

TAG="2.14-0"

DEST=$(eval echo ~"${SUDO_USER:-$USER}")
GRADLE="$DEST"/.gradle

! mkdir "$GRADLE" || chown --reference "$DEST" "$GRADLE"
docker run --rm \
    --network=none \
    -v "$GRADLE:/root/dest:rw" \
    ghcr.io/vanym/paniclecraft/builder:"$TAG" \
    bash -c 'chown -R --reference ~/dest ~/.gradle && cp -Tnr --preserve=links,mode,ownership ~/.gradle ~/dest' _
