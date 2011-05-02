/**
 *  A-Priori Implementation
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.util.*;

/**
 * Implementation of Lk -- Set of large k-itemsets
 * 
 *
 */
public class APrioriItemSet extends TreeSet<ItemSet> {
	private int k;  // @@@ Do we even care?

	public APrioriItemSet () {
		this.k = 0; // arbitrary value
	}
	public APrioriItemSet (int k) {
		this.k = k;
	}
	public int getK() {
		return k;
	}
	public void addAll(APrioriItemSet sets) {
		if (sets == null) return;
		for (ItemSet s: sets) {
			this.add(s);
		}
	}
	public APrioriItemSet joinTest () {
		//if (k==0) { return null; }  // not supported

		APrioriItemSet candidates = new APrioriItemSet(); 

		// join with itself...
		for (ItemSet p : this) {
			for (ItemSet q : this) {
				if (p.equals(q)) continue;

				Item pLast = p.last();
				SortedSet<Item> pHeadSet = p.headSet(pLast);
				
				Item qLast = q.last();
				SortedSet<Item> qHeadSet = q.headSet(qLast);

				System.out.println("pLast="+pLast+", qLast="+qLast + " pLast<qLast = " + (pLast.compareTo(qLast) < 0)); //@@@ DEBUG
				if (pHeadSet.equals(qHeadSet) && pLast.compareTo(qLast) < 0) {
					ItemSet c = new ItemSet();
					c.addAll(p);
					c.add(qLast);
					candidates.add(c);
				}
			}
		}
		
		return candidates;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		List<ItemSet> itemsetlist = new ArrayList<ItemSet>(this);
		Collections.sort(itemsetlist, SUPPORT_DESC_ORDER);
		for (ItemSet itemset: itemsetlist) {
			sb.append(itemset + ", " + (itemset.getSupport()*100) + "%\n");
		}
		return sb.toString();
	}
	
	// Order ItemSets in descending order of support
	static final Comparator<ItemSet> SUPPORT_DESC_ORDER = new Comparator<ItemSet>() {
		public int compare (ItemSet i1, ItemSet i2) {
			return Float.compare(i2.getSupport(), i1.getSupport());
		}
	};
}
