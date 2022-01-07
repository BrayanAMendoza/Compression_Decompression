/*
The input to the decompressor is a coded binary file, and the output is the corresponding (original) text file. 
We assume that the name of the input file contains the extension 'zzz', but the program 
detects for incorrect formatting and will reprompt the user if it encounters such conditions.
This program produces the decompressed version of the input binary file and 
saves it to "text_file_(filename).txt". After one file is 
decompressed the program asks the user if they would like to 
rerun the program. 
*/


import java.util.*;
import java.io.*;

public class Decompress { 
    public static void main(String[]args) { // Declare variables 
        String p, outFile, file = args[0], yesVal = "y"; 
        
        HashTableChain<Integer, String> dict = new HashTableChain<>(); // the dictionary 
        ObjectInputStream inFile; 
        
        PrintWriter out; 
        int inputCode, prevCode, counter = 0; 
        double sec; 
        
        Scanner kbInput = new Scanner(System.in); 
        boolean firstPass = true, filePassed = true;
        while(yesVal.equalsIgnoreCase("y")) { 
            if (firstPass) // grab first file from command line. 
            { 
                file = args[0]; 
                firstPass = false; 
            } 
            else 
            { // remaining files come from prompting the user. 
                System.out.println("Please enter the next file you would like to decompress"); 
                
                file = kbInput.next(); 
            } // checks that the user entered a file. 
            if (args.length < 1){ 
                System.out.println("Please enter a file to decompress"); 
                System.exit(1); 
            } 
            while(!file.contains("zzz")) // checks the file was formatted correctly. 
            { 
                System.out.println("Please enter a filename with 'zzz' extension"); 
                file = kbInput.next(); 
                
                if (file.contains("zzz")) { 
                    filePassed = false; 
                    break; 
                } 
            } 
            
            if (filePassed) // file from command line was formatted correctly. 
            file = args[0]; // Initialize our dictionary with chars in range of ascii values from 32-127 
            for(int i = 32; i < 127; i++){
                char c = (char)i;
                dict.put(i, Character.toString(c)); 
            } 
            dict.put(127,"\n"); 
            dict.put(128,"\t"); 
            dict.put(129, "\r"); 
            try 
            {
                outFile = "text_file_" + file.subSequence(0, file.indexOf(".")); 
                inFile = new ObjectInputStream(new FileInputStream(file)); 
                out = new PrintWriter(new FileOutputStream(outFile));
                long start = System.currentTimeMillis(); // start our run time for log file. 
                
                prevCode = inFile.readInt(); // first code. 
                // Output the text corresponding to the first code. 
                out.print(dict.get(prevCode)); 
                out.flush(); 
                while((Integer)(inputCode = inFile.readInt()) != null){ 
                    // All remaining codes will be searched from the dictionary 
                    // and entered into the decompressed output file. 
                    if (dict.get(inputCode) != null) // value in the dictionary. 
                    { 
                        p = dict.get(inputCode);// get value of current code. 
                        out.print(p); 
                        out.flush(); 
                        dict.put(130+counter, dict.get(prevCode) + 
                        p.charAt(0)); // insert new (next code, and text into our dictionary). 
                        prevCode = inputCode; // save previous code before p. 
                    } 
                    else // not in the dictionary so we add it. 
                    { 
                        p = dict.get(prevCode) + dict.get(prevCode).charAt(0); // get previousCode value and create new entry. out.print(p); 
                        out.flush(); 
                        dict.put(inputCode, p);// insert new value into the dictionary. 
                        prevCode = inputCode; 
                    } 
                    counter++; 
                } 
                long end = System.currentTimeMillis(); // end time for log file. 
                inFile.close(); 
                out.close(); 
                sec = (end-start) / 1000.0; 
                //write to our log file the status of the decompression process. 
                PrintWriter logfile = new PrintWriter(new FileOutputStream(file.subSequence(0, file.indexOf(".")) + ".txt.log")); 
                logfile.println("Decompression of " + file); 
                logfile.println("Decompression took " + sec + " seconds"); 
                logfile.println("The dictionary contains " + dict.size() + " total entries");
                logfile.println("The table was rehashed " + dict.rehashCount() + " times"); 
                logfile.close(); 
            } 
            catch (FileNotFoundException e){ 
                System.out.println(e.getMessage()); 
                System.exit(1); 
            } 
            catch (IOException e) 
            { 
                System.out.println("Would you like to decompress another file? (y = yes & n = no)"); 
                yesVal = kbInput.next(); 
                continue; 
            } 
            System.out.println("Would you like to decompress another file? (y = yes & n = no)"); 
            yesVal = kbInput.next(); 
        } 
    } 
} 
