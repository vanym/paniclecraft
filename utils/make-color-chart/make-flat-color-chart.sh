#!/bin/bash

set -e

TMPDIR=$(mktemp -d)

pushd "$TMPDIR"

HTML="color-chart.html"
wget --compression=auto "https://htmlcolorcodes.com/color-chart/" -O "$HTML"
xmllint --html --xpath '(//section[@id="flat"][@class="chart"]/div[contains(@class, "chart-content")]//div[@class="js-color"]/@data-hex)' "$HTML" 2> /dev/null |
  grep -o -P "#[\w\d]*" | xargs -n10 echo | tee colors.txt
md5sum -c <<END
f61498e546cbfd63b3fdc0f6161c8b83  colors.txt
END

WIDTH=$(wc -l < colors.txt)
HEIGHT=$(head -1 colors.txt | wc -w)
SIZE="${WIDTH}x${HEIGHT}"

echo "${SIZE}"

awk '
{
  X=NR-1;
  for (i=1; i <= NF; ++i) {
    Y=i-1;
    print X" "Y" "$i;
  }
}
' < colors.txt > column.txt

popd

{
  echo "-size" "${SIZE}" "canvas:none"
  awk '{ printf "-fill "$3" -draw \"point "$1","$2"\" "}' < "$TMPDIR"/column.txt
  echo "+set" "date:create" "+set" "date:modify" "+set" "date:timestamp"
  echo "flat-color-chart.png"
} | xargs magick
