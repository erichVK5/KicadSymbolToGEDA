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
  int pinNumberAlignment = 3;
  long pinNameX = 0;
  long pinNameY = 0;
  int pinNameAlignment = 0;
  int pinNumberOrientation = 0; // degrees rotation from +ve x-axis
  int pinNameOrientation = 0;
  long pinLength = 0;
  String pinDirection = "";
  int pinType = 0; // 0 = normal, and 1 = bus/unused
  int activeEnd = 0; // 1 = first end, 0 = second end
  int kicadUnit = 0; // equivalent to gschem "slot"

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
    if (pinDirection.startsWith("R")) {
      xCoord2 = xCoord1 + pinLength;
      pinNumberX = (xCoord1 + xCoord2)/2;
      pinNameX = xCoord2;
      yCoord2 = yCoord1;
      pinNumberY = pinNameY = yCoord1;
      pinNameAlignment = 1;
    } else if (pinDirection.startsWith("L")) {
      xCoord2 = xCoord1 - pinLength;
      pinNumberX = (xCoord1 + xCoord2)/2;
      pinNameX = xCoord2;
      yCoord2 = yCoord1;
      pinNumberY = pinNameY = yCoord1;
      pinNameAlignment = 7;
    } else if (pinDirection.startsWith("U")) {
      xCoord2 = xCoord1;
      pinNumberX = pinNameX = xCoord1;
      yCoord2 = yCoord1 + pinLength;
      pinNumberY = (yCoord1 + yCoord2)/2;
      pinNameY = yCoord2;
      pinNumberOrientation = pinNameOrientation = 90; // degrees from +ve x-axis
      pinNameAlignment = 1;
    } else if (pinDirection.startsWith("D")) {
      xCoord2 = xCoord1;
      pinNumberX = pinNameX = xCoord1;
      yCoord2 = yCoord1 - pinLength;
      pinNumberY = (yCoord1 + yCoord2)/2;
      pinNameY = yCoord2;
      pinNumberOrientation = pinNameOrientation = 90; // degrees from +ve x-axis
      pinNameAlignment = 7;
    } 
    super.updateXdimensions(xCoord1);
    super.updateYdimensions(yCoord1);
    super.updateXdimensions(xCoord2);
    super.updateYdimensions(yCoord2);

    kicadUnit = Integer.parseInt(tokens[9]);
      
  }

  public long localMinXCoord() {
    if (xCoord1 < xCoord2) {
      return xCoord1;
    } else {
      return xCoord2;
    }
  }

  public long locaclMinYCoord() {
    if (yCoord1 < yCoord2) {
      return yCoord1;
    } else {
      return yCoord2;
    }
  }

  public String toString(long xOffset, long yOffset) {
    int colorIndex = 3;
    return ("P "
            + (xCoord1 + xOffset) + " "
            + (yCoord1 + yOffset) + " " 
            + (xCoord2 + xOffset) + " "
            + (yCoord2 + yOffset)  + " "
            + colorIndex + " "
            + pinType + " "
            + activeEnd  // one implies (xCoord1, yCoord1)
            + "\n{\n" 
            + attributeFieldNumber(pinDesc, pinNumberX + xOffset, pinNumberY + yOffset, pinNumberOrientation, pinNumberAlignment)
            + "\n"
            + attributeFieldLabel(pinName, pinNameX + xOffset, pinNameY + yOffset, pinNameOrientation, pinNameAlignment)
            + "\n"
            + attributePinSeq(pinDesc, pinNumberX + xOffset, pinNumberY + yOffset, pinNumberOrientation, pinNumberAlignment)
            + "\n}");
    // it is here that the pin name could be added as an attribute
    // in curly braces {\nT x x x x x\npinnumber=3\n}" etc..
  }

  public int slot() {
    return kicadUnit;
  }

  public int pinNum() {
    return pinNumber;
  }

  private String attributeFieldLabel(String pinLabel, long X, long Y, int orientation, int alignment)  {
    int colour = 5;
    int textSize = 7;
    int textVisibility = 1;
    int showNameVal = 1;
    return ("T " + X + " " + Y + " " + colour + " " + textSize + " " + textVisibility + " "
            + showNameVal + " " + orientation + " " + alignment + " 1\npinlabel=" + pinLabel);
  }
  private String attributeFieldNumber(String pinDesc, long X, long Y, int orientation, int alignment)  {
    int colour = 5;
    int textSize = 7;
    int textVisibility = 1;
    int showNameVal = 1;
    return ("T " + X + " " + Y + " " + colour + " " + textSize + " " + textVisibility + " "
            + showNameVal + " " + orientation + " " + alignment + " 1\npinnumber=" + pinDesc);
  }
  private String attributePinSeq(String pinDesc, long X, long Y, int orientation, int alignment)  {
    int colour = 5;
    int textSize = 7;
    int textVisibility = 0;
    int showNameVal = 1;
    return ("T " + X + " " + Y + " " + colour + " " + textSize + " " + textVisibility + " "
            + showNameVal + " " + orientation + " " + alignment + " 1\npinseq=" + pinDesc);
  }


}
