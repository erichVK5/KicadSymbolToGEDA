# KicadSymbolToGEDA
A Kicad schematic symbol to GEDA gschema conversion utility

Single and multiple slot Kicad symbols can be converted to gschem compatible symbols.

In addition to the drawn symbol, pins, pin numbers, pin labels, refded and device name, converted symbols (.sym) contain the following information derived from the Kicad library (.lib) file.

pinseq=
slot=
numslots=
slotdef=
pinseq=
pintype=
footprint=

Remaining functionality to be refined includes the HTML summary file and author credit options.


Quickstart guide:

	Install java

	git clone https://github.com/erichVK5/KicadSymbolToGEDA.git

	cd KicadSymbolToGEDA

	mkdir Converted

	javac *.java

	java KicadSymbolToGEDA -l someKicadLibrary.lib
