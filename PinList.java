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
  int pinsPerSlot = 10; //default value, but resizes automatically if needed

  public PinList(int slotCount) {
    kicadSlots = slotCount;
    numSlots = slotCount + 1;
    //    System.out.println("New pinlist created with " + numSlots + " slots");
    slotArrays = new SymbolPin[numSlots][pinsPerSlot];
    pinCounts = new int[numSlots];
  }

  public void addPin(SymbolPin newPin) {
    int currentSlot = newPin.slot();
    //   System.out.println("Added a pin from slot: " + currentSlot );
    slotArrays[currentSlot][pinCounts[currentSlot]] = newPin;
    pinCounts[currentSlot] = pinCounts[currentSlot] + 1;
    // we test to see if our pin storage structure is full.
    // If so, we create a new one twice the size, and copy
    // everything over to it
    if (pinCounts[currentSlot] == pinsPerSlot) {
      pinsPerSlot = pinsPerSlot*2;
      SymbolPin[][] biggerSlotArrays = new SymbolPin[numSlots][pinsPerSlot];
      for (int slot = 0; slot < numSlots; slot++) {
        for (int pin = 0; pin < pinCounts[slot]; pin++) {
          biggerSlotArrays[slot][pin] = slotArrays[slot][pin];
        }
      }
      slotArrays = biggerSlotArrays;
      // System.out.println("I just resized the pin data structure.");
    }
  }

  public String toString(long xOffset, long yOffset) {
    String output = "";
    for (int index = 0; index < pinCounts[0]; index++) {
      output = output + "\n" + slotArrays[0][index].toString(xOffset, yOffset); 
    }
    for (int index = 0; index < pinCounts[1]; index++) {
      // by default, for a multislot device, we only display slot 1
      output = output + "\n" + slotArrays[1][index].toString(xOffset, yOffset); 
    }
    output = output + slotSummary(xOffset, yOffset);
    return output;
  }

  private String slotSummary(long xOffset, long yOffset) {
    String summary = "";
    if (kicadSlots < 2) {
      summary = SymbolText.attributeString(xOffset, yOffset, "numslots=0");
    } else { // this is a multi-slot device
      // we summarise the number of slots
      summary = SymbolText.attributeString(xOffset, yOffset, "numslots=" + kicadSlots);
      // we explain which slot is implemented in the symbol
      summary = summary + SymbolText.attributeString(xOffset, yOffset, "slot=1");
      // then we generate some slotdefs
      for (int index = 1; index < numSlots; index++) {
        summary = summary + SymbolText.attributeString(xOffset, yOffset, "slotdef=" + index + ":");
        for (int pin = 0 ; pin < pinCounts[index]; pin ++) {
          summary = summary + slotArrays[index][pin].pinNum();
          if (pin < (pinCounts[index] -1)) {
            summary = summary + ",";
          }
        }
      }
    }
    return summary;
  }
  
}
