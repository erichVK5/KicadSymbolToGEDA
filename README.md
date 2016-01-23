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

Kicad does not seem to require grid aligned pins. This utility can attempt to snap pins to a specified grid size, i.e. -g 100 or -g 200, in the commandline, to make net connections easier to perform on the symbol in gschem.

Remaining functionality to be refined includes the HTML summary file.

Author and licence credit attributes can be placed in a file and added to the AuthorCredits directory and specified at the command line during invocation of the utility.

For a library, i.e. kicadExample.lib, the symbol now has a source attribute appended, i.e.
	source=kicadExample.lib


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
	 -c AppendedElementAttributesFile.txt
		 Default is: ./AuthorCredits/DefaultSymbolAppendedAttributes.txt
	 -g 200
		 applies a snap to grid of size, i.e. 200, for pins
	 -U "usage licence text here, i.e. GPL3"
		 includes usage licence in symbol attributes
	 -D "distribution licence text here, i.e. unlimited"
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
