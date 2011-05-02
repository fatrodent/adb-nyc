/**
 *  A-Priori Implementation
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
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
	private float numberOfTransactions;
	
	// All transactions from file ("market baskets")
	ArrayList<TreeSet<String>> transactions = new ArrayList<TreeSet<String>>();
	
	// All rules: left side => right side
	HashMap<ItemSet, ItemSet> rules = new HashMap<ItemSet, ItemSet>();

	// Large item sets - L1 ... Lk stored as separate sets in ArrayList
	ArrayList<APrioriItemSet> Lk = new ArrayList<APrioriItemSet>();

	// Large item sets - Union of L1 ... Lk
	APrioriItemSet largeItemSets = new APrioriItemSet();
	
	public APrioriImpl(String filePath, float min_sup, float min_conf){
		this.file = filePath;
		this.min_sup = min_sup;
		this.min_conf = min_conf;
		Lk.add(null); // L0
	}
	
	/**
	 * Runs the A-Priori algorithm
	 */
	public void run() throws IOException {
		// stores all transactions from file
		inputTransactions();
		
		// get large 1-itemsets
		buildInitialSet();	
		for (int k=2; ; k++) {  
			int k_minus_1 = k - 1;
			// generate new candidates
			APrioriItemSet candidates = apriori_gen(k_minus_1);
			// exit condition: no large k-itemsets found
			if (candidates.isEmpty()) break;
			// increase support for candidates contained in the transactions
			for (TreeSet<String> t : transactions)
				updateSupportForCandidates(candidates, t);
			// Get large k-item sets
			APrioriItemSet L_k = largeCandidates(candidates);
			// Add the set of large k-itemsets
			Lk.add(L_k);
		}
		 // Answer = Union of all Lk
		largeItemSets = new APrioriItemSet();
		for (APrioriItemSet i: Lk) {
			largeItemSets.addAll(i);
		}
		
		// build association rules
		for (int i = 1; i < Lk.size(); i++) {
			APrioriItemSet set = Lk.get(i);
			for (ItemSet itemset : set) {
				rules.putAll(generateRules(itemset));
			}
		}
		
		toOutputFile(); // @@@ FIX THIS - save to output file, not STDOUT!
	}
	
	/**
	 * Returns the superset of the set of all large k-itemsets
	 * @param k_minus_1
	 * @return candidates   set of potentially large k-itemsets
	 */
	private APrioriItemSet apriori_gen (int k_minus_1) {
		APrioriItemSet p = Lk.get(k_minus_1);
		// join
		APrioriItemSet candidates = p.buildCandidateSet(k_minus_1+1);
		// prune
		pruneCandidates(candidates, p);
		System.out.println("Candidates: \n" + candidates); // @@@ DEBUG
		return candidates;
	}
	
	/**
	 * Prunes the candidates so that only those contained in parent remain
	 * @param itemset
	 * @param p
	 */
	private void pruneCandidates (APrioriItemSet candidates, APrioriItemSet p) {
		for (ItemSet c : candidates) {
			for (Item i : c) {
				ItemSet s = (ItemSet) c.clone(); // (k-1)-subset
				s.remove(i);
				if (! p.contains(s)) { // s is not an element of Lk-1
					candidates.remove(c);
				}
			}
		}
	}

	/**
	 * Checks the subset of candidates that are contained in the transaction and increments support
	 * @param cand
	 * @param t
	 * @return
	 */
	private void updateSupportForCandidates (APrioriItemSet cand, TreeSet<String> t) {
		// Iterate over candidate item sets
		for (ItemSet itemset : cand) {
			if (isContained(itemset, t))
				increaseSupport(itemset);
		}
	}
	
	/**
	 * Return true if an item set's items are contained in the transaction
	 * @param itemset
	 * @param t
	 * @return
	 */
	private boolean isContained (ItemSet itemset, TreeSet<String> t) {
		for (Item i : itemset) {
			String name = i.getName();
			if (!t.contains(name)) 	// doesn't contain item, so false
				return false;
		}
		return true;	// otherwise, true
	}
	
	/**
	 * Increase support count for the item set
	 * @param itemset
	 */
	private void increaseSupport(ItemSet itemset) {
		itemset.incrementCount(); // increments transaction count
		itemset.updateSupport(numberOfTransactions);  // revise support
	}
	
	/**
	 * Returns set of candidates that have above minimum support
	 * @param cand
	 * @return
	 */
	private APrioriItemSet largeCandidates(APrioriItemSet cand) {
		APrioriItemSet supported = new APrioriItemSet();
		for (ItemSet c : cand) {
			if (c.getSupport() >= min_sup) {
				supported.add(c);
			}
		}
		return supported;
	}

	/**
	 * Reads the CSV file and stores into transactions
	 * @throws IOException 
	 */
	
	private void inputTransactions() throws IOException {
		// use a library to parse the CSV file
		CSVReader reader = new CSVReader(new FileReader(file));
		String [] line;
		while((line = reader.readNext()) != null) {
			// store each transaction
			TreeSet<String> transaction = new TreeSet<String>();
			for (String i: line) {
				if (i == null) continue;     // being paranoid, don't think null can occur in a csv file
				i.trim();                    // eliminate leading and trailing spaces
				if (i.equals("")) continue;  // skip blank entries
				// add to transaction
				transaction.add(i);
				//System.out.print(i+" ");
			}
			transactions.add(transaction);
			//System.out.println();
		}
		// store number for later
		numberOfTransactions = transactions.size();
	}
	
	/**
	 * Build the large 1-itemsets
	 */
	private void buildInitialSet() {
		// Count the number of transactions containing an item
		HashMap<String,MutableInt> itemHash = new HashMap<String,MutableInt>();
		// Iterate over transactions
		for (TreeSet<String> items : transactions) {
			// count the items
			for (String i: items) {
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
		APrioriItemSet L1 = new APrioriItemSet(1); // Set of large 1-itemsets
		// determine the large item sets
		for (Map.Entry<String,MutableInt> entry: itemHash.entrySet()) {
			String i = entry.getKey();
			int item_occurence = entry.getValue().get();  // cast to float to perform fp arithmetic next
			float support = item_occurence / numberOfTransactions;
			if (support < min_sup) continue; // skip small item sets
			
			// Identify large 1-item sets
			ItemSet itemset = new ItemSet();
			itemset.add(new Item(i));
			itemset.setCount(item_occurence);
			itemset.setSupport(support);
			
			L1.add(itemset);
		}
		// add to Lk
		Lk.add(L1);    // L1
	}
	
	
	/**
	 * Generates rules
	 * @param set
	 * @param i_plus_one
	 * @return
	 */
	private HashMap<ItemSet, ItemSet> generateRules(ItemSet set) {
		HashMap<ItemSet, ItemSet> rules = new HashMap<ItemSet, ItemSet>();
		// Left side is at least one item, right side is one item
		ItemSet leftSide, rightSide;
		// Iterate over each item in itemset
		for (int i = 0; i < set.size(); i++) {
			// Instantiate
			leftSide = new ItemSet();
			rightSide = new ItemSet();
			// store the count to use for conf (TODO: revise)
			rightSide.setCount(set.getCount());
			// Adding to the left and right side
			Iterator<Item> iter = set.descendingIterator();
			for (int k = 0; k < set.size(); k++) {
				Item item = iter.next();
				System.out.println("Looking at "+item);
				if (k == i) {
					rightSide.add(item);
				} else {
					leftSide.add(item);
				}
			}
			// store the count to use for conf (TODO: revise)
			leftSide.setCount(set.getCount());
			// store this rule
			if (!leftSide.isEmpty())
				rules.put(leftSide, rightSide);
		}
		// Remove rules below min_conf
		pruneRules(rules);
		
		return rules;
	}
	
	/**
	 * Gets the transaction count for item set // needs revision
	 * @param itemset
	 * @return
	 */
	private int countFor(ItemSet itemset) {
		int size = itemset.size();
		int count = Lk.get(size).get(itemset).getCount(); 
		return count;
	}
	
	/**
	 * Remove from rules those that are below min confidence
	 * @param rules
	 */
	private void pruneRules(HashMap<ItemSet, ItemSet> rules) {
		// Check if rule has confidence > min_conf
		// If not, remove it from hashmap
	}
	
	private String printRules() {
		StringBuffer str = new StringBuffer();
		for (Map.Entry<ItemSet,ItemSet> entry : rules.entrySet()) {
			str.append(entry.getKey());
			str.append(" => ");
			str.append(entry.getValue());
			str.append("(Conf: ##%, Supp: ##%)\n");
		}
		return str.toString();
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

		APrioriItemSet L1 = new APrioriItemSet(1); // Set of large 1-itemsets
		Lk.add(null);  // L0 -- force a null 0th element to the ArrayList
		Lk.add(L1);    // L1

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
			L1.add(itemset);
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
		System.out.println(largeItemSets);
		
		System.out.println("==High-confidence association rules (min_conf="+ (min_conf*100) +"%)");
		System.out.println(printRules());
	}
}
