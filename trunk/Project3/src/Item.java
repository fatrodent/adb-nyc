/**
 *  Item for ItemSet
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

public class Item implements Comparable<Item> {
	private String name;

	public Item (String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	/**
	 * Compares two items by its name
	 * 
	 * @param item  Item to compare with
	 */
	public int compareTo(Item i) {
		return this.name.compareTo(i.name);
	}
	
	public String toString() {
		return name;
	}
}
