// KicadSymbolToGEDA - a utility for turning kicad modules to gEDA PCB footprints
// SymbolArc.java v1.0
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
* This class is passed a Kicad Arc descriptor string 
* and implements a method which can generate a gschema definition for a gEDA symbol
*
*/

//A posx posy radius start end part convert thickness cc start_pointX start_pointY end_pointX end_pointY.
//With:
//• posx posy = arc center position
//• start = angle of the starting point (in 0,1 degrees).
//• end = angle of the end point (in 0,1 degrees).
//• unit = 0 if common to all parts; if not, number of the part (1. .n).
//• convert = 0 if common to the representations, if not 1 or 2.
//• thickness = thickness of the outline or 0 to use the default line thickness.
//• cc = N F or F ( F = filled arc,; f = . filled arc, N = transparent background)
//• start_pointX start_pointY = coordinate of the starting point (role similar to start)

public class SymbolArc extends SymbolElement
{

  String arcDescriptor = "";  
  String output = "";
   
  long xCoord = 0;
  long yCoord = 0;
  long radius = 0;
  int startAngle = 0;
  int endAngle = 0;
  int fillType = 0;
  long lineThickness = 0;


  public void SymbolArc()
  {
    output = "#Hmm, the no arg symbol arc constructor didn't do much";
  }
  
  public void constructor(String arg)
  {
    arcDescriptor = arg;
    arg = arg.replaceAll("  "," ");    
    String[] tokens = arg.split(" ");
        
    xCoord = Integer.parseInt(tokens[1]);
    yCoord = Integer.parseInt(tokens[2]);
    radius = Integer.parseInt(tokens[3]);
    startAngle = Integer.parseInt(tokens[4])/10; //convert to whole degrees
    endAngle = Integer.parseInt(tokens[5])/10;   //convert to whole degrees
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
    return ("A "
            + (xCoord + xOffset) + " " 
            + (yCoord + yOffset) + " " 
            + radius + " "
            + startAngle + " " // to do, check direction of swept angle in kicad vs geda
            + (endAngle - startAngle) + " " // convert to swept angle 
            + colorIndex + " "
            + lineThickness + " "
            + "0 0 "   // for line capstyle (none) and dashstyle (solid)
            + "-1 -1"); // for dashlength and dashspace
  }
  

}
