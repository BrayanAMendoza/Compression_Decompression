/*
This class compresses a textfile chosen by the user, which is
then outputed as a binary coded file. The binary file will have the name of 
the original file with the extension "zzz". Moreover, this program
creates a log file for the user to read on it's performance. 

*/


import java.util.*;
import java.io.*;


class Compress{
    public static void main(String[] args){
        HashTableChain<String, Integer> dict = new HashTableChain<>();
        Scanner kb = new Scanner(System.in);
        String yesVal = "";

        String new_file;

        while(yesVal.equalsIgnoreCase("y") || yesVal.equalsIgnoreCase("")){
            for(int i = 32; i<127; i++){
                char c = (char) i;
                dict.put(Character.toString(c),i);
            }
            dict.put("\n",127); //adding extra, non-ASCII values to dictionary
            dict.put("\t",128);
            dict.put("\r",129);
            
            if(yesVal.equalsIgnoreCase("y")){
                System.out.println("Please Enter the name of the file you'd like to decompress: ");
                new_file = kb.next();
                args[0] = new_file;
            }

            try{
                BufferedReader in = new BufferedReader(new FileReader(args[0]));
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(args[0] + ".zzz"));
                long start = System.currentTimeMillis();
                String c = "";
                
                int n;
                String temp = "";
                int iter = 0;

                while ((n = in.read()) != -1){ //while loop = ends at EOF 
                    temp = c;
                    c = c + Character.toString((char)n);
                    if(dict.get(c) == null){ //if entry is not in dict
                        iter++;
                        dict.put(c,129+iter); //add entry to dict
                        int num = 129 + iter;
                        if(dict.get(temp) == null){ //avoid null entries
                            continue;
                        }
                        out.writeInt(dict.get(temp)); //add previous string
                        c = Character.toString((char)n); //replace c with most recent character                       
                    }
                    long end = System.currentTimeMillis();
                    in.close();
                    out.close();
                    File my_file = new File(args[0]);
                    File my_2_file = new File(args[0] + ".zzz"); //log file metrics on algorithmn performance
                    double sec = (end - start)/1000.0;

                    PrintWriter logfile = new PrintWriter(new FileOutputStream(args[0] + ".zzz.log"));

                    logfile.println("Compression of " + args[0]);

                    logfile.println("Compression from " + my_file.length()/1024.0 + "Kilobytes to " + my_2_file.length()/1024.0 + "Kilobytes");

                    logfile.println("Compression took" + sec + " seconds");
                    logfile.println("The dictionary contains " + dict.size() + "total entries");

                    logfile.println("The table was rehashed " + dict.rehashCount() + "times");
                    logfile.close();
                }}
                catch(FileNotFoundException e){
                    System.out.println(e.getMessage());
                    System.out.println("File not found, would you like to re-enter your file? (y/n): "); //lets user re-enter a file of their choice 


                    yesVal = kb.next();
                    if(yesVal.equalsIgnoreCase("y")){
                        continue;
                    }
                    else{
                        System.exit(1);
                    }
                }
                catch(IOException e ){
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
                System.out.println("Do you want to run the program again (y/n): ");
                yesVal = kb.next();
            }
        }
    }
