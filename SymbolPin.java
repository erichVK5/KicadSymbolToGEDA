// KicadSymbolToGEDA - a utility for turning kicad modules to gEDA PCB footprints
// SymbolPin.java v1.0
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
* This class is passed a Kicad Pin descriptor string of the form "X Ni x1 y1 x2 y2 ... xi yi fill"
* and implements a method which can generate a gschema line definitions for a gEDA symbol
*
*/

//X name number posx posy length orientation Snum Snom unit convert Etype [shape].
//With:
//• orientation = U (up) D (down) R (right) L (left).
//• name = name (without space) of the pin. if ~: no name
//• number = n pin number (4 characters maximum).
//• length = pin length.
//• Snum = pin number text size.
//• Snom = pin name text size.
//• unit = 0 if common to all parts. If not, number of the part (1. .n).
//• convert = 0 if common to the representations, if not 1 or 2.
//• Etype = electric type (1 character)
//• shape = if present: pin shape (clock, inversion...).

public class SymbolPin extends SymbolElement
{
  // the following static variable keeps track
  // of how many pin descriptions have been generated
  // in multislot devices when the symbols is exported
  // in gschem format
  static int pinSeqTally = 0;

  String pinDescriptor = "";
  String output = "";
  
  String pinName = "";
  int pinNumber = 0;
  String pinDesc = "";
  long xCoord1 = 0;
  long yCoord1 = 0;
  long xCoord2 = 0;
  long yCoord2 = 0;
  long pinNumberX = 0;
  long pinNumberY = 0;
  long pinNameX = 0;
  long pinNameY = 0;
  long pinLength = 0;
  String pinDirection = "";
  int pinType = 0; // 0 = normal, and 1 = bus/unused
  int activeEnd = 0; // 1 = first end, 0 = second end
  int kicadUnit = 0; // equivalent to gschem "slot"

  // the following variables define the visual appearance of the
  // pin, plus its pin label, pin number, and pinseq attribute text
  // colours, size and visibility in gschem
  long textKerning = 15; // to make text sit adjacent to other elements
  int pinNumberAlignment = 3; // this centres pin number mid pin
  int pinNumberOrientation = 0; // degrees rotation from +ve x-axis
  int pinNameAlignment = 0; // default 0 => bottom left corner at (x,y)
  int pinNameOrientation = 0;
  int pinColourIndex = 3;
  int pinLabelColour = 5;
  int pinLabelSize = 5;
  int pinLabelVis = 1;
  int pinLabelShow = 1;
  int pinNumberColour = 5;
  int pinNumberSize = 7;
  int pinNumberVis = 1;
  int pinNumberShow = 1;
  int pinSeqColour = 5;
  int pinSeqTextSize = 7;
  int pinSeqVis = 0;
  int pinSeqShow = 1;

  public void SymbolPin()
  {
    output = "#Hmm, the no arg symbol polygon constructor didn't do much";
  }
  
  public void constructor(String arg)
  {
    pinDescriptor = arg;
    arg = arg.replaceAll("  "," ");    
    String[] tokens = arg.split(" ");

    pinName = tokens[1];
    pinNumber = Integer.parseInt(tokens[2]);
    pinDesc = tokens[2];
    xCoord1 = Integer.parseInt(tokens[3]);
    yCoord1 = Integer.parseInt(tokens[4]);
    super.updateXdimensions(xCoord1);
    super.updateYdimensions(yCoord1);
    pinLength = Integer.parseInt(tokens[5]);
    pinDirection = tokens[6];
    // we now sort out the orientation and position
    // of the pin number and pin label, based on the
    // direction of the pin & the pin location,
    // and add some kerning while we are at it
    // to prevent the text obscuring adjacent
    // elements/features
    if (pinDirection.startsWith("R")) {
      xCoord2 = xCoord1 + pinLength;
      pinNumberX = (xCoord1 + xCoord2)/2;
      pinNameX = xCoord2 + textKerning;
      yCoord2 = yCoord1;
      pinNameY = yCoord1;
      pinNumberY = yCoord1 + textKerning;
      pinNameAlignment = 1;
    } else if (pinDirection.startsWith("L")) {
      xCoord2 = xCoord1 - pinLength;
      pinNumberX = (xCoord1 + xCoord2)/2;
      pinNameX = xCoord2 - textKerning;
      yCoord2 = yCoord1;
      pinNameY = yCoord1;
      pinNumberY = yCoord1 + textKerning;
      pinNameAlignment = 7;
    } else if (pinDirection.startsWith("U")) {
      xCoord2 = xCoord1;
      pinNameX = xCoord1;
      pinNumberX = xCoord1 - textKerning;
      yCoord2 = yCoord1 + pinLength;
      pinNumberY = (yCoord1 + yCoord2)/2;
      pinNameY = yCoord2 + textKerning;
      pinNumberOrientation = pinNameOrientation = 90; // degrees from +ve x-axis
      pinNameAlignment = 1;
    } else if (pinDirection.startsWith("D")) {
      xCoord2 = xCoord1;
      pinNameX = xCoord1;
      pinNumberX = xCoord1 - textKerning;
      yCoord2 = yCoord1 - pinLength;
      pinNumberY = (yCoord1 + yCoord2)/2;
      pinNameY = yCoord2 - textKerning;
      pinNumberOrientation = pinNameOrientation = 90; // degrees from +ve x-axis
      pinNameAlignment = 7;
    } 
    // we keep track of the overall size of the drawn elements
    super.updateXdimensions(xCoord1);
    super.updateYdimensions(yCoord1);
    super.updateXdimensions(xCoord2);
    super.updateYdimensions(yCoord2);
    // the kicadUnit is equivalent to the slot in gschem... useful
    kicadUnit = Integer.parseInt(tokens[9]);
  }

  public long localMinXCoord() {
    if (xCoord1 < xCoord2) {
      return xCoord1;
    } else {
      return xCoord2;
    }
  }

  public long localMinYCoord() {
    if (yCoord1 < yCoord2) {
      return yCoord1;
    } else {
      return yCoord2;
    }
  }

  public String toString(long xOffset, long yOffset) {
    return ("P "
            + (xCoord1 + xOffset) + " "
            + (yCoord1 + yOffset) + " " 
            + (xCoord2 + xOffset) + " "
            + (yCoord2 + yOffset)  + " "
            + pinColourIndex + " "
            + pinType + " "
            + activeEnd  // one implies (xCoord1, yCoord1)
            + "\n{\n" 
            + attributeFieldPinNumber(pinDesc, pinNumberX + xOffset, pinNumberY + yOffset, pinNumberOrientation, pinNumberAlignment)
            + "\n"
            + attributeFieldPinLabel(pinName, pinNameX + xOffset, pinNameY + yOffset, pinNameOrientation, pinNameAlignment)
            + "\n"
            + attributeFieldPinSeq(pinDesc, pinNumberX + xOffset, pinNumberY + yOffset, pinNumberOrientation, pinNumberAlignment)
            + "\n}");
  }

  public int slot() {
    return kicadUnit; // kicadUnit is equivalent to slot in gschem
  }

  public int pinNum() {
    return pinNumber;
  }

  private String attributeFieldPinLabel(String pinLabel, long X, long Y, int orientation, int alignment)  {
    int numLines = 1;
    return SymbolText.toString(X, Y, pinLabelColour, pinLabelSize, pinLabelVis, pinLabelShow, orientation, alignment, numLines, ("pinlabel=" + pinLabel));
  }

  private String attributeFieldPinNumber(String pinDesc, long X, long Y, int orientation, int alignment)  {
    int numLines = 1;
    return SymbolText.toString(X, Y, pinNumberColour, pinNumberSize, pinNumberVis, pinNumberShow, orientation, alignment, numLines, ("pinnumber=" + pinDesc));
  }

  private String attributeFieldPinSeq(String pinDesc, long X, long Y, int orientation, int alignment)  {
    int numLines = 1;
    // we use the class static variable pinSeqTally to keep track
    // of how many rendered pins have been generated
    pinSeqTally++;
    return SymbolText.toString(X, Y, pinSeqColour, pinSeqTextSize, pinSeqVis, pinSeqShow, pinNameOrientation, pinNameAlignment, numLines, ("pinseq=" + pinSeqTally));
  }

}
