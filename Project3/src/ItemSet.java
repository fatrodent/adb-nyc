/**
 *  A-Priori Implementation
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.util.*;

public class ItemSet extends TreeSet<Item> implements Comparable<ItemSet> {
	private float support;
	
	public void setSupport(float supp) {
		this.support = supp;
	}
	public float getSupport() {
		return support;
	}
	
	public void addAll(ItemSet set) {
		if (set == null) return;
		for (Item i: set) {
			this.add(i);
		}
	}

	/**
	 * Sort by lexical order of the items
	 * 
	 * @param ItemSet  ItemSet to compare with
	 */
	public int compareTo(ItemSet i) {
		return this.toString().compareTo(i.toString());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (! this.isEmpty()) {
			Iterator<Item> iter = this.iterator();
			sb.append(iter.next());
			while (iter.hasNext())
				sb.append(",").append(iter.next());
		}
		sb.append("]");	    
		return sb.toString();
	}
}
