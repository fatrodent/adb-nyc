/**
 *  The set of k-itemsets (Lk)
 *  
 *  COMS E6111 - Project 3  04/29/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.util.*;

public class APrioriItemSet extends TreeSet<ItemSet> {
	private static final long serialVersionUID = 1L; // ignore

	public APrioriItemSet () {
	}

	public void addAll(APrioriItemSet sets) {
		if (sets == null) return;
		for (ItemSet s: sets) {
			this.add(s);
		}
	}
	
	public ItemSet get(ItemSet set) {
		return null;
	}
	
	/**
	 * Self-join to build a set of potentially large (k+1)-itemsets
	 * 
	 * @return candidates   Candidate itemset with 1 more element than this
	 */
	public APrioriItemSet buildCandidateSet (int k) {
		//APrioriItemSet candidates = new APrioriItemSet(k); 
		APrioriItemSet candidates = new APrioriItemSet(); 		

		// self-join and create a candidate set of (k+1)-itemsets 
		for (ItemSet p : this) {
			for (ItemSet q : this) {
				if (p.equals(q)) continue;

				Item pLast = p.last();
				SortedSet<Item> pHeadSet = p.headSet(pLast);
				
				Item qLast = q.last();
				SortedSet<Item> qHeadSet = q.headSet(qLast);

				//System.out.println("pLast="+pLast+", qLast="+qLast + " pLast<qLast = " + (pLast.compareTo(qLast) < 0)); //@@@ DEBUG
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
