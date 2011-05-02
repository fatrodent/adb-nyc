/**
 *  A-Priori Implementation
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.io.IOException;

public class APriori {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Check if there are 4 or 5 arguments provided
		if (args.length != 3)
			usage("Invalid Arguments");

		int i = 0; // process each argument, starting from 0th
		
		String dataSetFile = args[i++]; // e.g. INTEGRATED-DATASET.csv
		
		float minSupport = 0;
	    try {
			minSupport  = Float.parseFloat(args[i++]);
	    } catch (NumberFormatException e) {
	        System.err.println("Invalid value for min_sup: " + e.getMessage());
	        System.exit(1);
	    }
	    if (minSupport < 0 || minSupport > 1) {
	    	System.err.println("min_sup must be between 0 and 1.");
	        System.exit(1);	    	
	    }
	    
		float minConfidence = 0;
	    try {
	    	minConfidence = Float.parseFloat(args[i++]);
	    } catch (NumberFormatException e) {
	        System.err.println("Invalid value for min_conf: " + e.getMessage());
	        System.exit(1);
	    }
	    if (minConfidence < 0 || minConfidence > 1) {
	    	System.err.println("min_conf must be between 0 and 1.");
	        System.exit(1);	    	
	    }

		System.out.println("DEBUG: data set = " + dataSetFile);
		System.out.println("DEBUG: min_sup  = " + minSupport);
		System.out.println("DEBUG: min_conf = " + minConfidence);
	    
		// Main Algorithm
		APrioriImpl apriori = new APrioriImpl(dataSetFile, minSupport, minConfidence);
		try {
			apriori.run();
		} catch (IOException e) {
			// This comes from error reading the CSV file...
			System.err.println("IO Error: " + e.getMessage());
			System.exit(1);
		}
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
        
        System.err.println("Usage: " + mainClass + " <dataset.csv> <min_sup> <min_conf>");
        System.exit(1);
    }

}
