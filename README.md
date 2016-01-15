# KicadSymbolToGEDA
A Kicad schematic symbol to GEDA gschema symbol conversion utility

Single and multiple slot Kicad symbols can be converted to gschem compatible symbols.

Libraries with multiple symbols are batch converted by the utility automatically.

In addition to the drawn symbol, pins, pin numbers, pin labels, refdes and device name, converted symbols (.sym) contain the following information derived from the Kicad library (.lib) file:

	pinseq=
	slot=
	numslots=
	slotdef=
	pinseq=
	pintype=
	footprint=

The footprint attribute defaults to footprint=unknown unless a footprint is specified in the FPLIST section of the library.

Some Kicad libraries use non ASCII characters in device descriptions and pin labels. The utility replaces these with underscores.

Remaining functionality to be refined includes the HTML summary file and author credit options.


Quickstart guide:

	Install java

	git clone https://github.com/erichVK5/KicadSymbolToGEDA.git

	cd KicadSymbolToGEDA

	mkdir Converted

	javac *.java

	java KicadSymbolToGEDA -l someKicadLibrary.lib

Options are:

	 -q QuietMode
		 Default is not quiet mode, with a simple summary of progress provided
	 -l kicadlibrary.lib
		 parses kicad symbols in .lib library files
	 -a "the Author"
		 includes "the Author" in symbol attributes
	 -U "usage licence i.e. GPL3"
		 includes usage licence in symbol attributes
	 -D "distribution licence i.e. unlimited"
		 includes distribution licence in symbol attributes
	 -F Inserts FOSS GPL3 distribution licence and
		unrestricted usage licence in symbol attributes
	 -h HTMLsummaryOutputFile.html
		 Default is: "HTMLsummary.html"
	 -v VerboseMode
		 Default is not verbose


Useful links:

	http://www.kicadlib.org/
	a good range of symbols (.lib libraries) and footprints (.mod modules)

	http://library.oshec.org/
	an enormous range of symbol libraries

	http://smisioto.no-ip.org/elettronica/kicad/kicad-en.htm
	an impressive collection of mostly Kicad footprints (.mod)
