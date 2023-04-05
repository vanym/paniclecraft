#!/bin/bash

set -e

if [ $# -ne 2 ]; then
  NAME=$(basename "$0")
  echo "usage: $NAME colors-file image-file"
  exit 1
fi

TXTFILE="$1"
IMAGEFILE="$2"

WIDTH=$(wc -l < "$TXTFILE")
HEIGHT=$(head -1 "$TXTFILE" | wc -w)

< "$TXTFILE" ./colors2columns.sh | ./columns2image.sh "$WIDTH" "$HEIGHT" "$IMAGEFILE"
