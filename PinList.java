// KicadSymbolToGEDA - a utility for turning kicad modules to gEDA PCB footprints
// PinList.java v1.0
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
* This class stores a collection of SymbolPin objects and has methods to determine
* Slot definitions, to allow multiple slot devices to be converted from
* Kicad to gschem compatible symbols
*
*/

public class PinList {

  SymbolPin[][] slotArrays;
  int[] pinCounts;
  int numSlots = 1;
  int kicadSlots = 0;

  public PinList(int slotCount) {
    kicadSlots = slotCount;
    numSlots = slotCount + 1;
    //    if (slotCount == 1) { // deal with degenerate case
    //  numSlots = 1;
    //}
    System.out.println("New pinlist created with " + numSlots + " slots");
    int pinsPerSlot = 100;
    slotArrays = new SymbolPin[numSlots][pinsPerSlot];
    pinCounts = new int[numSlots];
  }

  public void addPin(SymbolPin newPin) {
    int currentSlot = newPin.slot();
    System.out.println("Added a pin from slot: " + currentSlot );
    slotArrays[currentSlot][pinCounts[currentSlot]] = newPin;
    pinCounts[currentSlot] = pinCounts[currentSlot] + 1;
  }

  public String toString(long xOffset, long yOffset) {
    String output = "";
    //    if ((numSlots == 0) || (numSlots == 1)) {
    for (int index = 0; index < pinCounts[0]; index++) {
      output = output + "\n" + slotArrays[0][index].toString(xOffset, yOffset); 
    }
    for (int index = 0; index < pinCounts[1]; index++) {
      output = output + "\n" + slotArrays[1][index].toString(xOffset, yOffset); 
    }
    //    } else if (numSlots >1) {
    //    for (int index = 0; index < pinCounts[1]; index++) {
    //  output = output + "\n" + slotArrays[1][index].toString(xOffset, yOffset); 
    // }
    //}
    output = output + slotSummary();
    return output;
  }

  private String slotSummary() {
    String summary = "";
    if (kicadSlots < 2) {
      summary = "\nT 0 300 5 7 0 1 0 1 1" + "\nnumslots=0";
    } else {
      summary = "\nT 0 300 5 7 0 1 0 1 1" + "\nnumslots=" + kicadSlots;
      // now we need to come up with some slotdefs
      for (int index = 1; index < numSlots; index++) {
        summary = summary + "\nT 0 300 5 7 0 1 0 1 1\n" + "slotdef=" + index + ":";
        for (int pin = 0 ; pin < pinCounts[index]; pin ++) {
          summary = summary + slotArrays[index][pin].pinNum();
          if (pin < (pinCounts[index] -1)) {
            summary = summary + ",";
          }
        }
        //        summary = summary + "\n";
      }
    }
    return summary;
  }
  
}
