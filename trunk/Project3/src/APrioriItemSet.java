import java.util.*;
import java.io.*;

public class APrioriItemSet {

	String filePath;
	float min_sup;
	float min_conf;
	
	// Large item set
	TreeSet LargeItemSet = new TreeSet<Item>();
	
	
	public APrioriItemSet(String filePath, float min_sup, float min_conf) {
		this.filePath = filePath;
		this.min_sup = min_sup;
		this.min_conf = min_conf;
		buildSet();
	}
	
	/**
	 * Algorithm
	 */
	private void buildSet() {
		
		
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
	}
	
	/**
	 * Stores information about the items in the item set
	 */
	public class Item {
		float support;
		
	}
	
}
