#!/bin/bash

set -e

if [ $# -ne 3 ]; then
  NAME=$(basename "$0")
  echo "usage: $NAME width height image-file"
  exit 1
fi

WIDTH="$1"
HEIGHT="$2"
FILE="$3"

{
  echo "-size" "${WIDTH}x${HEIGHT}" "canvas:none"
  awk '{ printf "-fill "$3" -draw \"point "$1","$2"\" "}'
  echo "+set" "date:create" "+set" "date:modify" "+set" "date:timestamp"
  echo "$FILE"
} | xargs magick
