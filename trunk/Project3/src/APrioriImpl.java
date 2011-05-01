/**
 *  A-Priori Implementation
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 *
 *
 *  Source Code References:
 * 
 *  Opencsv (Source Forge) - csv file parser
 *  Source: http://sourceforge.net/projects/opencsv/files/opencsv/2.3/opencsv-2.3-src-with-libs.tar.gz/download
 * 
 *  MutableInt - Implementation to increment HashMap value
 *  Source:  http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java
 */

import java.io.*;
import java.util.*;
import au.com.bytecode.opencsv.*;

public class APrioriImpl {

	private String file;
	private float min_sup;
	private float min_conf;
	
	// Large item sets
	TreeSet<ItemSet> largeItemSets = new TreeSet<ItemSet>();
	
	
	public APrioriImpl(String filePath, float min_sup, float min_conf){
		this.file = filePath;
		this.min_sup = min_sup;
		this.min_conf = min_conf;
	}
	
	/**
	 * Runs the A-Priori algorithm
	 */
	public void run() throws IOException {
		buildSet();  // initialize large itemsets from data set

		// @@@ TBI - the real stuff  

		toOutputFile(); // @@@ FIX THIS - save to output file, not STDOUT!
	}
	
	/**
	 * Reads the CSV file, and build the large 1-itemsets
	 * @throws IOException 
	 */
	public void buildSet() throws IOException {
		int transcnt = 0;  // Count the total number of transactions

		// Count the number of transactions containing an item
		HashMap<String,MutableInt> itemHash = new HashMap<String,MutableInt>();

		// use a library to parse the CSV file
		CSVReader reader = new CSVReader(new FileReader(file));
		String [] line;
		while((line = reader.readNext()) != null) {
			++transcnt;    // each csv line is one transaction

			//System.out.println("LINE: "); // @@@ DEBUG
			// count the items
			for (String i: line) {
				if (i == null) continue;     // being paranoid, don't think null can occur in a csv file
				i.trim();                    // eliminate leading and trailing spaces
				if (i.equals("")) continue;  // skip blank entries
				
				// Count the number of transactions that item occurs in
				// Assumes that items never appear more than once in a transaction
				MutableInt value = itemHash.get(i);
				if (value == null) {
					value = new MutableInt();
					itemHash.put(i, value);
				} else {
					value.inc();
				}
				//System.out.println("   ITEM "+i+" occurence=" + itemHash.get(i).get()); // @@@ DEBUG
			}
		}

		// determine the large item sets
		for (Map.Entry<String,MutableInt> entry: itemHash.entrySet()) {
			String i = entry.getKey();
			float item_occurence = entry.getValue().get();  // cast to float to perform fp arithmetic next
			float support = item_occurence / transcnt;
			if (support < min_sup) continue; // skip small item sets
			
			// Identify large 1-item sets
			Item item = new Item(i);
			ItemSet itemset = new ItemSet();
			itemset.add(item);
			itemset.setSupport(support);
			largeItemSets.add(itemset);
//			System.out.println("largeItemSets size=" + largeItemSets.size()); // @@@ DEBUG			
//			System.out.println("DEBUG: " + itemset + ", " + (support*100) + "%"); // @@@ DEBUG		
		}

	}

	// A relatively fast increment counter 
	// Source:  http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java
	class MutableInt {
		  int value = 1;
		  public void inc () { ++value; }
		  public int get () { return value; }
	}
	
	/** 
	 * Prints the item set and association rules to output.txt
	 */
	public void toOutputFile() {
		// 		Example:
		//		==Large itemsets (min_sup=70%)
		//		[pen], 100%
		//		[diary], 75%
		//		[diary,pen], 75%
		//		[ink], 75%
		//		[ink,pen], 75%
		//
		//		==High-confidence association rules (min_conf=80%)
		//		[diary] => [pen] (Conf: 100.0%, Supp: 75%)
		//		[ink] => [pen] (Conf: 100.0%, Supp: 75%)

		//@@@ print to output file, not STDOUT!!!
		System.out.println("==Large itemsets (min_sup=" + (min_sup*100) +"%)");

		List<ItemSet> itemlist = new ArrayList<ItemSet>(largeItemSets);
		Collections.sort(itemlist, SUPPORT_DESC_ORDER);
		for (ItemSet itemset: itemlist) {
			System.out.println(itemset + ", " + (itemset.getSupport()*100) + "%");
		}
	}

	// Order ItemSets in descending order of support
	static final Comparator<ItemSet> SUPPORT_DESC_ORDER = new Comparator<ItemSet>() {
		public int compare (ItemSet i1, ItemSet i2) {
			return Float.compare(i2.getSupport(), i1.getSupport());
		}
	};
	
}
