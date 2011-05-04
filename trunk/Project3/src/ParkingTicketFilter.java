import java.io.*;
import java.util.*;

import au.com.bytecode.opencsv.CSVReader;


public class ParkingTicketFilter {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if (args.length < 2) 
			usage("Invalid Arguments");

		int i = 0; // process each argument, starting from 0th
		
		String csvFile = args[i++]; // e.g. parking.csv
		String outFile = args[i++]; // e.g. output.csv
		filter(csvFile, outFile);
		
	}
	
	/**
	 * Reads the CSV file and stores into transactions
	 * @throws IOException 
	 */
	
	static private void filter(String infile, String outfile) throws IOException {
		// use a library to parse the CSV file
		CSVReader reader = new CSVReader(new FileReader(infile));
		String [] prefix = {"SID:", "PLATE:", "", "","","","VC","$","LOC:","FOP:","HN-","","","SC1:","SC2:","SC3:"};
		String [] line;
		
		ArrayList<String[]> csvlines = new ArrayList<String[]>();              // primitive
		BufferedWriter out = new BufferedWriter(new FileWriter(outfile));

		int linenum = 0;
		while((line = reader.readNext()) != null) {
			if (++linenum == 1) {continue;} // skip first header line
			csvlines.add(line);  // store the line as-is
			StringBuilder sb = new StringBuilder();
			for (int i=0; i < line.length; i++) {
				switch(i) {
					// skip these fields:
					case 0:  // Summon ID
					case 1:  // Plate num
					case 9:  // Front Of Opposite
					case 10: // House Number
					//case 11: // Street Name
					case 12: // Intersect Street Name
						break;
					default:
						if (sb.length() != 0) { 
							sb.append(",");
						}
						if (i == 2) {    // State of license plate 
							line[i] = line[i] + "-PLATE";
						} if (i == 5) {  // Violation Description
							line[i] = line[i].replaceFirst("^[^-]+-", "");
							line[i] = "\"" + line[i].replaceFirst("\\s+Y015001003$", "").trim() + "\"";
						} else if (i == 7) {  // Fine field, truncate leading zero, prepend $
							line[i] = line[i].replaceFirst("^0+", "");
						} else if (i == 11) {  // capitalize Street names for consistency
							line[i] = "\"" + line[i].toUpperCase().trim() + "\"";
						}
						sb.append(prefix[i]+line[i].trim());
				}
			}
			System.out.println("line " + linenum +": "+sb);
			
			// Write to file
			sb.append("\n");
			out.write(sb.toString());

			//if (linenum > 10) break; // test
		}
		out.close();

	}

	/**
	 * Returns an error message and usage information
	 * 
	 * @param errMsg  an error string
	 */
    public static void usage(String errMsg) {
    	if (errMsg != null)
    		System.err.println(errMsg);
    	usage();
    }

    /**
     * Returns information about the expected usage
     */
    public static void usage() {
    	// This is like $0 in perl/shell
    	StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
        StackTraceElement main = stack[stack.length - 1];
        String mainClass = main.getClassName ();
        
        System.err.println("Usage: " + mainClass + " <parking.csv> <output.csv>");
        System.exit(1);
    }
}
