// KicadSymbolToGEDA - a utility for turning kicad modules to gEDA PCB footprints
// KicadSymbolToGEDA.java v1.0
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



import java.util.Scanner;
import java.io.*;


public class KicadSymbolToGEDA
{

  public static void main(String [] args) throws IOException
  {
    boolean verboseMode = false;
    boolean quietMode = false;
    boolean defaultHTMLsummary = true;
    boolean usingStdInForModule = false;

    // the following are default strings which can be changed to suit the user's needs,
    // particularly if usage is intended via stdin, as these will be the defaults used
    // when generating output files
    String htmlSummaryFileName = "HTMLsummary.html";
    String kicadSymbolLibName = "kicad.lib";
    String moduleDescriptionText = " converted kicad symbol";
    String convertedKicadModulePath = "Converted/";
    String htmlSummaryPathToConvertedModule = "kicad/symbols/";
    String tempStringArg = "";

    // first, we parse the command line arguments passed to the utility when started

    if (args.length == 0)
      {
        usingStdInForModule = true;
        quietMode = true;
      }
    else
      {
        for (int count = 0; count < args.length; count++)
          {
            if (verboseMode)
              {	
                System.out.println("\t" + args[count]);
              }
            //if (args[count].startsWith("-k") && (count < (args.length - 1)))
            //{
            //   count++;
            //  kicadModuleFileName = args[count];
            //  if (!quietMode)
            // {
            //                    System.out.println("Using " + args[count] +
            //                   " as input file");
            //  }
            //}
            if (args[count].startsWith("-l") && (count < (args.length - 1)))
              {
                count++;
                kicadSymbolLibName = args[count];
                if (!quietMode)
                  {
                    System.out.println("Using " + args[count] +
                                       " as input file");
                  }
              }
            else if (args[count].startsWith("-h") && (count < (args.length-1)))
              {
                count++;
                htmlSummaryFileName = args[count];
                if (verboseMode)
                  {
                    System.out.println("Using " + args[count] +
                                       " as HTML summary file");
                    defaultHTMLsummary = false;
                  }
              }
            else if (args[count].startsWith("-d") && (count < (args.length-1)))
              {
                count++;
                convertedKicadModulePath = args[count];
                if (verboseMode)
                  {
                    System.out.println("Using " + args[count] +
                                       " for converted modules");
                  }
              }
            else if (args[count].startsWith("-s") && (count < (args.length-1)))
              {
                count++;
                moduleDescriptionText = args[count];
                if (verboseMode)
                  {
                    System.out.println("Using " + args[count] +
                                       " for HTML description of converted modules");
                  }
              }
            else if (args[count].startsWith("-v"))
              {
                verboseMode = true;
                System.out.println("Verbose mode\n");
              }
            else if (args[count].startsWith("-q"))
              {
                quietMode = true;
              }
            else
              {
                System.out.println("\nUnknown option: " +
                                   args[count] + "\n\n");
                printHelpScreen();					
              }
          }
      }

    // having parsed the command line arguments, we proceed to process the data

    // we now come up with a more unique default HTML summary filename if a filename was
    // not specified at the command line

    if (defaultHTMLsummary)
      {
        // we replace any symbols in the Module path that will cause file IO conniptions
        htmlSummaryFileName = kicadSymbolLibName.replaceAll("[^a-zA-Z0-9-]", "_") +
            "-" +  htmlSummaryFileName;
        if (verboseMode)
          {
            System.out.println("Using: " + htmlSummaryFileName + 
                               " for HTML summary of converted modules");
          }
      }

    Scanner kicadLibraryFile;
    File file1 = new File(kicadSymbolLibName);
    if (!usingStdInForModule)
      {
        // if the user specified a kicad module with command line arguments
        // we will now look for the kicad module passed on the command line
        //	                File file1 = new File(kicadModuleFileName);
        if (!file1.exists())
          {
            System.out.println("Hmm, the library file " + kicadSymbolLibName + " was not found.");
            System.exit(0);
          }
        kicadLibraryFile = new Scanner(file1);
      }
    else // we are using StdIn for the module, and args is of length one
      {
        kicadLibraryFile = new Scanner(System.in);
      }


    // we get rid of the "kicad_libraries/" at the front of the converted module filename
    if (kicadSymbolLibName.startsWith("kicad_libraries"))
      {
        kicadSymbolLibName = kicadSymbolLibName.substring(16);
      }

    String[] loadedLibraryStringArray = new String[59999];

    int loadedLibraryLineCounter = 0;
    int symbolDefsInLibraryCount = 0;

    String tempString = "";
    Boolean legacyFlag = true;

    int extractedSymbolLineCounter = 0;
    int extractedSymbolCount = 0;
    Symbol[] symbolsInLibrary = new Symbol[100];

    boolean firstLine = true;

    // first of all, we load the library into a string array
    // and count the number of lines
    // and count the number of modules therein

    if (kicadLibraryFile.hasNext())
      {
        tempString = kicadLibraryFile.nextLine();
      }
                
    while (kicadLibraryFile.hasNext())
      { // we do this in case the very first line is $MODULE, but it shouldn't be usually
        //most modules start with an INDEX, so this should be safe
        if (firstLine)
          //			if (loadedLibraryLineCounter == 0)
          {
            loadedLibraryStringArray[loadedLibraryLineCounter] = tempString;
            firstLine = false;
          }
        else // we continue loading lines into our string array
          // maybe we can dispense with this preliminary counting business
          {
            loadedLibraryStringArray[loadedLibraryLineCounter] = kicadLibraryFile.nextLine();			
          }

        if (loadedLibraryStringArray[loadedLibraryLineCounter].startsWith("DEF"))
          {
            symbolDefsInLibraryCount++;
          } 
        //			System.out.println(loadedLibraryStringArray[loadedLibraryLineCounter]);
        loadedLibraryLineCounter++;
        //			System.out.println("Modules in library count: " + modulesInLibraryCount +
        //					"\nLoaded library line cout: " + loadedLibraryLineCounter );
      }

    // we create a string array to store individual module definitions

    String[] extractedSymbolDefinition = new String[3000];
    boolean inSymbolDef = false;

    for (int counter = 0; counter < loadedLibraryLineCounter; counter++)
      {

        if (loadedLibraryStringArray[counter].startsWith("DEF"))
          {
            inSymbolDef = true;
          }
        else if (loadedLibraryStringArray[counter].startsWith("ENDDEF"))
          {
            inSymbolDef = false;
            extractedSymbolDefinition[extractedSymbolLineCounter] =
                loadedLibraryStringArray[counter];
            // having found and extracted a symbol
            // we now store it in a symbol object
            if (verboseMode)
              {
                System.out.println("We've found " + extractedSymbolCount
                                   + " modules so far.");
              }
            // we convert the array of strings to one string
            // so that it can be passed to the Symbol object
            // we may be able to dispense with the array

            tempStringArg = "";
            for (int stringCounter = 0; stringCounter < extractedSymbolLineCounter; stringCounter++)
              {
                tempStringArg = tempStringArg + "\n" +
                    extractedSymbolDefinition[stringCounter];
              }
            symbolsInLibrary[extractedSymbolCount] = new Symbol(tempStringArg);
            extractedSymbolLineCounter = 0;
            extractedSymbolCount++;
				
          }

        if (inSymbolDef)
          {
            extractedSymbolDefinition[extractedSymbolLineCounter] =
                loadedLibraryStringArray[counter];
            extractedSymbolLineCounter++;
          }

      }	
    //	we close kicadLibaryFile, which wasn't used if stdin was the source of the module
    //      and wwould have been used if the user specified a module filename 
    kicadLibraryFile.close(); // we'll try it down here


    // we now have finished parsing the library file, and we have an array of footprint objects
    // that we can interogate, namely:  footprintsInLibrary[extractedModuleCount] 

    if (verboseMode)
      {
        System.out.println("Just closed the open file, now counting to: " + 
                           extractedSymbolCount + " - the extracted module count\n" +
                           "versus counted symbols in library: " + symbolDefsInLibraryCount);
      }

    // we can now step through the array of symbols we generated from the kicad library(s)
    // we generate a gschema format symbol for each of them, save each one to a symbol_name.fp,
    // and create a gedasymbols.org compatible HTML segment for inclusion in a user index 

    // we insert a heading for the HTML summary
    String HTMLsummaryOfConvertedSymbols = "<html><h2>" +
        kicadSymbolLibName + "</h2>\n";

    for (int counter = 0; counter < extractedSymbolCount; counter++)
      {
        if (verboseMode)
          {
            System.out.println("Footprint object array index: " + counter);
          }

        // we generate a string containing the GEDA footprint filename
        String outputFileName = symbolsInLibrary[counter].generateGEDAsymbolFilename();

        // we then append a listing for this particular footprint
        // to the HTML summary
        HTMLsummaryOfConvertedSymbols = HTMLsummaryOfConvertedSymbols +
            "<li><a href=\"" +
            htmlSummaryPathToConvertedModule +
            kicadSymbolLibName + "/" +
            outputFileName + "\"> " +
            outputFileName + " </a> - " +
            moduleDescriptionText +
            " </li>\n";

        if (!quietMode)
          {
            System.out.println(outputFileName);
          }

        // a String variable to contain the symbol data
        String symbolData = "v 20110115 1\n" +
            symbolsInLibrary[counter].generateGEDAsymbol();

        if (verboseMode)
          {
            System.out.println(symbolData);
            // and we now use the toString method to return the module text
            System.out.println("\n\n" + symbolsInLibrary[counter] + "\n\n");
          }

        // we now create a file with the name of the module and conversion directory
        // path prepended
        PrintWriter newGEDAsymbolFile = new PrintWriter(convertedKicadModulePath +
                                                           outputFileName);
        // we write the completed GEDA footprint to the filesystem
        newGEDAsymbolFile.println(symbolData);
        // and then close the file
        newGEDAsymbolFile.close();
      }

    // having populated footprint objects in an array
    // we now finish off the HTML summary of the created symbols

    HTMLsummaryOfConvertedSymbols = HTMLsummaryOfConvertedSymbols + "\n</ul></html>\n";
    if (verboseMode)
      {
        System.out.println(HTMLsummaryOfConvertedSymbols);
      }

    // and we pass the HTML to a subroutine to save the summary to disc, using either a user
    // supplied file name, or alternatively,  an auto generated name kicad_module_name-HTMLsummary.html

    generateHTMLsummaryFile(convertedKicadModulePath, htmlSummaryFileName, HTMLsummaryOfConvertedSymbols);
		
  }


  // we have a routine to put the completed HTML summary into a file in the same directory as
  // the converted modules
  // the HTML file is formatted to be easily inserted into a gedasymbols.org user's summary index

  private static void generateHTMLsummaryFile(String conversionDir, String HTMLfileName, String HTMLsummary) throws IOException
  {
    // we generate an appropriate HTML summary file name
    String fileName = conversionDir + HTMLfileName;
    PrintWriter HTMLsummaryFile = new PrintWriter(fileName);
    HTMLsummaryFile.println(HTMLsummary);
    HTMLsummaryFile.close();
  }


  public static void printHelpScreen()
  {
    System.out.println("\nUsage:\n\n" +
                       "user@box:~$ java KicadSymbolToGEDA " +
                       "-q quietMode " +
                       //                       "-k foo.mod " +
                       "-l bar.lib " +
                       "-c PrependedAuthorCreditsCommentsLicenceEtc.txt " +
                       "-h HTMLsummaryOfFootprintsOutputFileName.html " +
                       "-d destinationDirectoryPathForConvertedModuleDirectory " +
                       "-s summaryDescriptionOfmoduleOrModules " +
                       "-v verboseOutputToStdOut\n" );

    System.out.println("Options are:\n\n" +
                       "\t -q QuietMode\n" +
                       "\t\t Default is not quiet mode," +
                       " with a simple summary of progress provided\n" +
                       //                       "\t -k kicadmodule.mod\n" +
                       //"\t\t parses legacy & s-file format modules in decimil or mm units\n" +
                       "\t -l kicadlibrary.lib\n" +
                       "\t\t parses kicad symbols in .lib library files\n" +
                       "\t -h HTMLsummaryOutputFile.html\n" +
                       "\t\t Default is: \"HTMLsummary.html\"\n" + 
                       "\t -c PrependedElementComments.txt\n" +
                       "\t\t Default is:" +
                       "   ./AuthorCredits/DefaultPrependedCommentsFile.txt\n" +
                       "\t -d DestinationdirForConvertedModules\n" +
                       "\t\t Default is:   ./Converted/\n" +
                       "\t -s SummaryOfModuleOrModulesForHTML\n" +
                       "\t\t Default inserted in HTML is: \"converted Kicad module\"\n" +
                       "\t -v VerboseMode\n" +
                       "\t\t Default is not verbose\n" );

    System.out.println("Example:\n\n" +
                       "user@box~$ java KicadSymbolToGEDA -l " +
                       "kicad_libraries/SRA-1.lib -h mixer.html " +
                       "-c AuthorCredits/FootprintPreliminaryTextOSHEC.txt " +
                       "-s \"RF Mixer\" -d \"Converted/\"\n\n");
  }

}
