# KicadSymbolToGEDA
A Kicad schematic symbol to GEDA gschema conversion utility

Single slot devices convert satisfactorily.

Support for conversion of multiple slot devices has been implemented.

pinseq= fields are to be implemented next.

Quickstart guide:

	Install java

	git clone https://github.com/erichVK5/KicadSymbolToGEDA.git

	cd KicadSymbolToGEDA

	mkdir Converted

	javac *.java

	java KicadSymbolToGEDA -l someKicadLibrary.lib
