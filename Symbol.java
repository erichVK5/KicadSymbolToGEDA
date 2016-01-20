// KicadSymbolToGEDA - a utility for turning kicad modules to gEDA PCB footprints
// Symbol.java v1.0
// Copyright (C) 2015 Erich S. Heinzle, a1039181@gmail.com

//    see LICENSE-gpl-v2.txt for software license
//    see README.txt
//    
//    This program is free software; you can redistribute it and/or
//    modify it under the terms of the GNU General Public License
//    as published by the Free Software Foundation; either version 2
//    of the License, or (at your option) any later version.
//    
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//    
//    You should have received a copy of the GNU General Public License
//    along with this program; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
//    
//    KicadSymbolToGEDA Copyright (C) 2015 Erich S. Heinzle a1039181@gmail.com

/**
 *
 * This object coordinates the header, text descriptors, drawn lines, drawn arcs, drawn circles
 * and also connections for a given Kicad symbol, passed as a string array of the form (String[] args)
 * and is then able to produce a gEDA gschema symbol
 *
 */

import java.util.Scanner;

public class Symbol
{
  String footprintName = "newSymbol";
  String assembledGEDAelement = "";
  String passedString = "";

  SymbolElement symbolElements[] = new SymbolElement[200];
  PinList listOfPins;

  int moduleLineCountTotal = 0;	
  int padDefinitionLineCount = 0;

  String padDefinitionLines;

  int symFeatureCount = 0;

  int lineCount = 0;

  int pinCount = 0;
  int drawPolylineCount = 0;
  int drawArcCount = 0;
  int drawCircleCount = 0;
  int textDescriptorCount = 0;

  String reconstructedKicadSymbolAsString = ""; // we'll return this from the toString() 

  String symbolName = "";
  String suggestedFootprint = "unknown"; //default is "unknown"

  String referencePrefix = "";
  long pinNameOffset = 0;
  String drawPinNumber = "";
  String drawPinName = "";
  int unitCount = 0; // default, not a multislot device
  String unitsLocked = "";
  String optionFlag = "";
  long xTranslate = 0;
  long yTranslate = 0;

  boolean suppressTranslation = false;

  String deviceAliases;

  boolean metricSystem = false; //not really needed

  public Symbol(String args)
  {
    boolean symbolFinished = false;

    Scanner symbolDefinition = new Scanner(args);

    String parseString = "";
    String trimmedString = "";
    String[] tokens;

    //		System.out.println(args);

    while (symbolDefinition.hasNext() && !symbolFinished)
      {			
        parseString = symbolDefinition.nextLine();
        trimmedString = parseString.trim();
        tokens = trimmedString.split(" ");

        // we now move into the main parsing section
        // which decides what each line is all about and then deploys
        // it to construct the relevant symbol element object 

        if (tokens[0].startsWith("EESchema"))
          {
            //	System.out.println(footprintName); // we don't care about the header
          }
        else if (tokens[0].startsWith("DEF"))
          {       // it all starts here, with the symbol header

            symbolName = tokens[1];
            // now we get rid of characters ![a-zA-Z0-9.-] which
            // may be unacceptable for a filename
            symbolName = symbolName.replaceAll("[^a-zA-Z0-9.-]", "_");

            referencePrefix = tokens[2];
            pinNameOffset = Long.parseLong(tokens[4]);
            drawPinNumber = tokens[5];
            drawPinName = tokens[6];
            unitCount = Integer.parseInt(tokens[7]); // for multislot devices
            unitsLocked = tokens[8];
            optionFlag = tokens[9];
            // now that we know the number of slots, we can create the
            // data structure to hold the pin data
            listOfPins = new PinList(unitCount);
            //            listOfPins.resetXYExtents();
            // for batch processing we need to reset
            // for each new symbol, but not behaving as expected

            // we now step through the symbol definition line by line
            while (symbolDefinition.hasNext() && !symbolFinished)
              {
                parseString = symbolDefinition.nextLine();
                trimmedString = parseString.trim();
                // we tokenize the line
                tokens = trimmedString.split(" "); // may need to look out for "  " whitespace
					
                // and we then decide what to do with the tokenized lines
                // we start with F descriptor fields and plain text fields
                // System.out.println(tokens[0]);
                if (tokens[0].startsWith("F") || tokens[0].startsWith("T"))
                  {
                    symbolElements[symFeatureCount] = new SymbolText();
                    symbolElements[symFeatureCount].constructor(trimmedString);
                    textDescriptorCount++;
                    symFeatureCount++;
                  }
                else if (tokens[0].startsWith("$FPLIST")) {
                  // for now, we won't bother to use the list of
                  // footprints associated with the symbol
                  // just the first entry
                  parseString = symbolDefinition.nextLine();
                  trimmedString = parseString.trim();
                  tokens = trimmedString.split(" ");
                  if (!tokens[0].startsWith("$ENDFPLIST")) {
                    suggestedFootprint = tokens[0];
                  }
                  while (!tokens[0].startsWith("$ENDFPLIST")) {
                        parseString = symbolDefinition.nextLine();
                        trimmedString = parseString.trim();
                        // we tokenize the line
                        tokens = trimmedString.split(" ");
                      }
                  }
                else if (tokens[0].startsWith("S"))
                  {
                    symbolElements[symFeatureCount] = new SymbolRectangle();
                    symbolElements[symFeatureCount].constructor(trimmedString);
                    symFeatureCount++;
                  }
                else if (tokens[0].startsWith("C"))
                  {
                    symbolElements[symFeatureCount] = new SymbolCircle();
                    symbolElements[symFeatureCount].constructor(trimmedString);
                    symFeatureCount++;
                    drawCircleCount++;
                  }
                else if (tokens[0].startsWith("A") && !tokens[0].startsWith("AL"))
                  {
                    symbolElements[symFeatureCount] = new SymbolArc();
                    symbolElements[symFeatureCount].constructor(trimmedString);
                    symFeatureCount++;
                    drawArcCount++;
                  }
                else if (tokens[0].startsWith("P"))
                  {
                    symbolElements[symFeatureCount] = new SymbolPolyline();
                    symbolElements[symFeatureCount].constructor(trimmedString);
                    symFeatureCount++;
                    drawPolylineCount++;
                  }
                else if (tokens[0].startsWith("AL") && (tokens.length > 1))
                  {
                    deviceAliases = "comment=Equivalent devices: " + tokens[1];
                    for (int index = 2; index < (tokens.length-1); index++) {
                      deviceAliases = deviceAliases + ", " + tokens[index];
                    }
                    System.out.println(deviceAliases);
                  }
                else if (tokens[0].startsWith("X"))
                  {  // we have identified a pin definition in the symbol
                    SymbolPin newPin = new SymbolPin();
                    newPin.constructor(trimmedString);
                    listOfPins.addPin(newPin);
                    pinCount++;
                  }

              }  
          }

      }

    // we also create a single string version of the module for
    // use by the toString() method
    reconstructedKicadSymbolAsString = args;
  }

  public void updateXYTrans(PinList pins) {
    xTranslate = pins.minX();
    yTranslate = pins.minY();
    for (int index = 0; index < symFeatureCount; index++) {
      if (xTranslate > symbolElements[index].localMinXCoord()) {
        xTranslate = symbolElements[index].localMinXCoord();
      }
      if (yTranslate > symbolElements[index].localMinYCoord()) {
        yTranslate = symbolElements[index].localMinYCoord();
      }
    }
  }
  
  public void suppressTranslation(boolean option) {
    suppressTranslation = option;
  }

  public String generateGEDAsymbolFilename()
  {
    return symbolName + ".sym";
  }


  public String generateGEDAsymbol() {
    return generateGEDAsymbol(0); // don't tweak pin spacings
  }

  public String generateGEDAsymbol(int spacing)
  {
    String output = "";
    // first, we need to snap thing to the grid if spacing != 0
    // System.out.println("Spacing passed to symbol: " + spacing);
    // we then add symbol definitions for pin elements and features, and
    // get the listOfPins to also generate the associated slotdef, slot,
    // numslots attribute fields
    PinList temp = null;
    if (spacing != 0) {
      temp = listOfPins.pinsGridAligned(spacing);
    } else {
      temp  = listOfPins;
    }
;
    if (!suppressTranslation) {
      updateXYTrans(temp);
    }
    System.out.println("Symbol minX: " + xTranslate);
    System.out.println("Symbol minY: " + yTranslate);

    if (spacing != 0) { // to do - snap to grid routine.
      if (xTranslate < 0) {
        xTranslate
            = (long)Math.floor((xTranslate*1.0)/spacing)*spacing;
      } else if (xTranslate > 0) {
        xTranslate
            = (long)Math.ceil((xTranslate*1.0)/spacing)*spacing;
      }
      if (yTranslate < 0) {
        yTranslate
            = (long)Math.floor((yTranslate*1.0)/spacing)*spacing;
      } else if (xTranslate > 0) {
        yTranslate
            = (long)Math.ceil((yTranslate*1.0)/spacing)*spacing;
      }
    }

    //System.out.println("Have identified this many symbol features: " + symFeatureCount);
    // we first generate gschem symbol definitions for non-pin elements and features
    // before we do this, we need to reset the offsets used for text
    // invisible attributes by the SymbolText class, in case we
    // are batch processing lots of symbols
    SymbolText.resetSymbolTextAttributeOffsets();
    for (int index = 0; index < symFeatureCount; index++) {
      output = output + symbolElements[index].toString(-xTranslate, -yTranslate);
      if (index < (symFeatureCount - 1)) {
        output = output + "\n";
      }
    }

    if (spacing != 0) {
      output = output
          + temp.toString(-xTranslate, -yTranslate)
          + "\n"
          + temp.boundingBox(0,0).toString(-xTranslate, -yTranslate);
      //      System.out.println("Generated snapped to grid pins, bounding box");
    } else {
      output = output + temp.toString(-xTranslate, -yTranslate);
    }

    // have default footprint of unknown, since kicad does
    // necessarily specify a footprint
    output = output + SymbolText.attributeString(-xTranslate, -yTranslate, ("footprint=" + suggestedFootprint));
    // finally, we put in a comment field to show aliases/equivalent devices
    if (deviceAliases != null) {
      output = output + SymbolText.attributeString(-xTranslate, -yTranslate, deviceAliases);
    }
    return output;
  }

  public String getKicadSymbolName()
  {
    return symbolName;
  }

  public String toString()
  {
    return reconstructedKicadSymbolAsString;
  }

}
