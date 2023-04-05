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

popd

./colors2image.sh "$TMPDIR"/colors.txt flat-color-chart.png
