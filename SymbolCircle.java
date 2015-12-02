// KicadSymbolToGEDA - a utility for turning kicad modules to gEDA PCB footprints
// SymbolCircle.java v1.0
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
* This class is passed a Kicad Circle descriptor string 
* and implements a method which can generate a gschema definition for a gEDA symbol
*
*/

//C posx posy radius unit convert thickness cc
//With
//• posx posy = circle center position
//• unit = 0 if common to the parts; if not, number of part (1. .n).
//• convert = 0 if common to all parts. If not, number of the part (1. .n).
//• thickness = thickness of the outline.
//• cc = N F or F ( F = filled circle,; f = . filled circle, N = transparent background)

public class SymbolCircle extends SymbolElement
{

  String circleDescriptor = "";  
  String output = "";
   
  long xCoord = 0;
  long yCoord = 0;
  long radius = 0;
  int fillType = 0;
  long lineThickness = 0;


  public void SymbolCircle()
  {
    output = "#Hmm, the no arg symbol circle constructor didn't do much";
  }
  
  public void constructor(String arg)
  {
    circleDescriptor = arg;
    arg = arg.replaceAll("  "," ");
    String[] tokens = arg.split(" ");
        
    xCoord = Integer.parseInt(tokens[1]);
    yCoord = Integer.parseInt(tokens[2]);
    radius = Integer.parseInt(tokens[3]);
    lineThickness = Integer.parseInt(tokens[6]);
    if (tokens[7].startsWith("N")) {
      fillType = 0;
    } else {
      fillType = 1;
    } // could support more fill types here, but, meh...

    // we now update superclass min, max dimensions
    super.updateXdimensions(xCoord - radius);
    super.updateYdimensions(yCoord - radius);
    super.updateXdimensions(xCoord + radius);
    super.updateYdimensions(yCoord + radius);

  }


  public long localMinXCoord() {
    return (xCoord - radius);
  }

  public long localMinYCoord() {
    return (yCoord - radius);
  }

  public String toString(long xOffset, long yOffset) {
    int colorIndex = 3;
    return ("V "
            + (xCoord + xOffset) + " " 
            + (yCoord + yOffset) + " " 
            + radius + " "
            + colorIndex + " "
            + lineThickness + " "
            + "0 0 "   // for line capstyle (none) and dashstyle (solid)
            + "-1 -1 " // for dashlength and dashspace
            + fillType // 0 for hollow, 1 for solid
            + " -1 -1 -1 -1 -1"); // fill type and fill hatching options not used
  }
  

}
