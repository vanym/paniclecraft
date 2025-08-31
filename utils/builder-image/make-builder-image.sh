#!/bin/env bash
set -e

TAG="2.7-1"

DIR=$(dirname "${BASH_SOURCE[0]}")
docker build -t ghcr.io/vanym/paniclecraft/builder:"$TAG" -f Dockerfile "$DIR"/../..
