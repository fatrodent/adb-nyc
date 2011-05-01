/**
 *  A-Priori Implementation
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.util.*;

public class ItemSet implements Comparable<ItemSet> {
	private TreeSet<Item> items = new TreeSet<Item>();
	private float support;
	
	public ItemSet () {
		
	}
	
	public void add(Item i) {
		items.add(i);
	}

	public void setSupport(float supp) {
		this.support = supp;
	}
	public float getSupport() {
		return support;
	}

	public int getSize() {
		return items.size();
	}
	/**
	 * Sort by the textual representation of the items
	 * 
	 * @param ItemSet  ItemSet to compare with
	 */
	public int compareTo(ItemSet i) {
		return items.toString().compareTo(i.items.toString());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (!items.isEmpty()) {
			Iterator<Item> iter = items.iterator();
			sb.append(iter.next());
			while (iter.hasNext())
				sb.append(",").append(iter.next());
		}
		sb.append("]");	    
		return sb.toString();
	}
}
