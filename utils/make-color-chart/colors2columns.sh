#!/bin/bash

awk '
{
  X=NR-1;
  for (i=1; i <= NF; ++i) {
    Y=i-1;
    print X" "Y" "$i;
  }
}
'
