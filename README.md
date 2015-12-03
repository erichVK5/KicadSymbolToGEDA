# KicadSymbolToGEDA
A Kicad schematic symbol to GEDA gschema conversion utility

Single and multiple slot Kicad symbols can be converted to gschem compatible symbols.

In addition to the drawn symbol, pins, pin numbers, pin labels, refdes and device name, converted symbols (.sym) contain the following information derived from the Kicad library (.lib) file:

	pinseq=
	slot=
	numslots=
	slotdef=
	pinseq=
	pintype=
	footprint=

The footprint currently defaults to footprint=unknown as the footprint option is optional in Kicad libraries, residing in the optional F2 field.

Some Kicad libraries use non ASCII characters in device descriptions and pin labels. The utility replaces these with underscores.

Remaining functionality to be refined includes the HTML summary file and author credit options.


Quickstart guide:

	Install java

	git clone https://github.com/erichVK5/KicadSymbolToGEDA.git

	cd KicadSymbolToGEDA

	mkdir Converted

	javac *.java

	java KicadSymbolToGEDA -l someKicadLibrary.lib

Useful links:

	http://www.kicadlib.org/
	a good range of symbols (.lib libraries) and footprints (.mod modules)

	http://smisioto.no-ip.org/elettronica/kicad/kicad-en.htm
	an impressive collection of mostly Kicad footprints (.mod)
