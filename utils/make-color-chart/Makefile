
all: color-chart-gui.png

flat-color-chart.png: make-flat-color-chart.sh
	./make-flat-color-chart.sh

flat-color-chart-scale%.png: flat-color-chart.png
	magick flat-color-chart.png -interpolate Nearest -filter point -scale '$*00%' +set date:create +set date:modify +set date:timestamp flat-color-chart-scale$*.png

color-chart-gui.png: flat-color-chart-scale4.png
	magick -size 256x256 canvas:none -draw 'image SrcOver 76,17 0,0 flat-color-chart-scale4.png' +set date:create +set date:modify +set date:timestamp color-chart-gui.png

install: color-chart-gui.png
	$(eval GIT_ROOT:=$(shell git rev-parse --show-toplevel))
	install -m644 color-chart-gui.png $(GIT_ROOT)/src/main/resources/assets/paniclecraft/textures/guis/paletteGuiColorChart.png

.PHONY: all install
