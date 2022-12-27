
all: color-chart-gui.png

colors2image.sh: colors2columns.sh columns2image.sh

make-flat-color-chart.sh: colors2image.sh

flat-color-chart.png: make-flat-color-chart.sh
	./make-flat-color-chart.sh

flat-color-chart-scale%.png: flat-color-chart.png
	magick flat-color-chart.png -interpolate Nearest -filter point -scale '$*00%' \
	       +set date:create +set date:modify +set date:timestamp \
	       flat-color-chart-scale$*.png

minecraft-color-chart.png: minecraft-color-chart.txt colors2image.sh
	./colors2image.sh minecraft-color-chart.txt minecraft-color-chart.png

minecraft-color-chart-scale%.png: minecraft-color-chart.png
	magick minecraft-color-chart.png -interpolate Nearest -filter point -scale '$*00%' \
	       +set date:create +set date:modify +set date:timestamp \
	       minecraft-color-chart-scale$*.png

color-chart-gui.png: flat-color-chart-scale4.png minecraft-color-chart-scale5.png
	magick -size 256x256 canvas:none \
	       -draw 'image Src 76,17 0,0 minecraft-color-chart-scale5.png' \
	       -draw 'image Src 76,26 0,0 flat-color-chart-scale4.png' \
	       +set date:create +set date:modify +set date:timestamp \
               color-chart-gui.png

install: color-chart-gui.png
	$(eval GIT_ROOT:=$(shell git rev-parse --show-toplevel))
	install -m644 color-chart-gui.png $(GIT_ROOT)/src/main/resources/assets/paniclecraft/textures/guis/paletteGuiColorChart.png

.PHONY: all install
