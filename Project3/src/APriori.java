/**
 *  APriori Implementation
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

public class APriori {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Check if there are 4 or 5 arguments provided
		if (args.length != 3)
			usage("Invalid Arguments");

		String dataSetFile = args[0]; // (i.e. INTEGRATED-DATASET.csv)
		float minSupport  = Float.parseFloat(args[1]);
		float minConfidence = Float.parseFloat(args[2]);
		
		APrioriItemSet set = new APrioriItemSet(dataSetFile, minSupport, minConfidence);
		
//		set.toOutputFile();

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
        
        System.err.println("Usage: " + mainClass + " <workdir> <host> <t_es> <t_ec> [<yahoo appId>]");
        System.exit(1);
    }

}
