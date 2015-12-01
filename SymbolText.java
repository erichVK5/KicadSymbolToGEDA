// KicadSymbolToGEDA - a utility for turning kicad modules to gEDA PCB footprints
// SymbolText.java v1.0
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
* This class is passed a Kicad Text descriptor string 
* and implements a method which can generate a gschema definition for a gEDA symbol
*
*/

//T orientation posx posy dimension unit convert Text
//With:
//• orientation = horizontal orientation (=0) or vertical (=1).
//• type = always 0.
//• unit = 0 if common to the parts. If not, the number of the part (1. .n).
//• convert = 0 if common to the representations, if not 1 or 2.

public class SymbolText extends SymbolElement
{

  String textDescriptor = "";  
  String output = "";
   
  long xCoord = 0;
  long yCoord = 0;
  long radius = 0;
  long defaultTextSize = 12;
  long textSize = 0;
  String textField = "";

  //  boolean isRefDes = false;
  //  boolean isDeviceDes = false;

  public void SymbolText()
  {
    output = "#Hmm, the no arg symbol circle constructor didn't do much";
  }
  
  public void constructor(String arg)
  {
    textDescriptor = arg;
    // herein lies a most peculiar danger, heretofore unimagined, that
    // the kicadians might perchance elect to have text fields with
    // spaces within double quote delimited text fields; oh, the horror 
    String[] tokens = arg.split(" ");
    
    //		System.out.print("#The passed string:" + arg + "\n");
    
    if (tokens[0].startsWith("F0")) {
      // isRefDes = true;
      if (tokens[1].charAt(0) == '"') {
        tokens[1] = tokens[1].substring(1);
      }
      if (tokens[1].charAt(tokens[1].length()-1) == '"') {
        tokens[1] = tokens[1].substring(0,tokens[1].length()-1);
      }
      textField = "refdes=" + tokens[1] + "?";  // we add newline in toString method 
    } else if (tokens[0].startsWith("F1")) {
      // isDeviceDes = true;
      if (tokens[1].charAt(0) == '"') {
        tokens[1] = tokens[1].substring(1);
      }
      if (tokens[1].charAt(tokens[1].length()-1) == '"') {
        tokens[1] = tokens[1].substring(0,tokens[1].length()-1);
      }
      textField = "device=" + tokens[1]; // we add newline in toString method 
    } else if (tokens[0].startsWith("T")) {
      textField = tokens[8]; 
    }

    xCoord = Integer.parseInt(tokens[2]);
    yCoord = Integer.parseInt(tokens[3]);
    textSize = Integer.parseInt(tokens[4]);
  }

  public String toString() {
    int colorIndex = 3;
    int visibility = 1;
    int textAngle = 0;
    int textAlignment = 0;
    int numLines = 1;
    return ("T "
            + xCoord + " " 
            + yCoord + " " 
            + colorIndex + " "
            + defaultTextSize + " "
            + "1 " // visibility on = 1
            + "0 " //attribute visibility off
            + textAngle + " " // not rotated = 0 
            + textAlignment + " " //default value
            + numLines + "\n"
            + textField);
  }
  

}
