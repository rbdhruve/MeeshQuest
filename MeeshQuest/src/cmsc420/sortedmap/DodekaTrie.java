package cmsc420.sortedmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.activation.UnsupportedDataTypeException;

import cmsc420.meeshquest.part1.Pair;

@SuppressWarnings("unchecked")
public class DodekaTrie<K, V> implements SortedMap<K, V> {
	
	final int BRANCHING_FACTOR = 12;
	Comparator<K> c = null;
	int leafOrder;
	Node root = null;
	int modCount = 0;
	
	public class BlackNode extends Node {
		private ArrayList<Pair<K,V>> bucket = new ArrayList<Pair<K,V>>();
		private Node parent;
		
		public void insert(K key, V value) {
			Pair<K,V> pair = new Pair<K,V>(key,value);
			if (c == null) {
				Comparable<? super K> k = (Comparable<? super K>) key;
				for (int i = 0; i < bucket.size(); i++) {
					if (k.compareTo(bucket.get(i).T()) < 0) {
						bucket.add(i, pair);
					} else if(k.compareTo(bucket.get(i).T()) == 0) {
						bucket.remove(i);
						bucket.add(i, pair);
					}
				}
				if (!bucket.contains(pair)) {
					bucket.add(pair);
				}
 			}
		}
	}

	public class GrayNode extends Node {
		private ArrayList<K> bucket = new ArrayList<K>();
		private Node c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12;
		
		public GrayNode() {
			c1 = new WhiteNode();
			c2 = new WhiteNode();
			c3 = new WhiteNode();
			c4 = new WhiteNode();
			c5 = new WhiteNode();
			c6 = new WhiteNode();
			c7 = new WhiteNode();
			c8 = new WhiteNode();
			c9 = new WhiteNode();
			c10 = new WhiteNode();
			c11 = new WhiteNode();
			c12 = new WhiteNode();
		}
	}
	
	public class WhiteNode extends Node {
		
	}
	
	protected class EntrySet<T> implements Set<T> {

		@Override
		public boolean add(T arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends T> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean contains(Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<T> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean remove(Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T[] toArray(T[] arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public DodekaTrie(int leafOrder) {
		this.leafOrder = leafOrder;
	}
	
	public DodekaTrie(Comparator<K> c, int leafOrder) {
		this.c = c;
		this.leafOrder = leafOrder;
	}

	@Override
	public void clear() {
		root = null;
		
	}

	@Override
	public boolean containsKey(Object key) {
		return getHelp(root, (K) key).T() != null;
	}
	
	private Pair<K,V> getHelp(Node root, K key) {
		if (root == null) {
			return null;
		} else if (key == null) {
			throw new NullPointerException("Argument 'key' is null");
		}

		if (root.getClass().equals(BlackNode.class)) {
			BlackNode b = (BlackNode) root;
			for (int i = 0; i < b.bucket.size(); i++) {
				if (c == null) {
					if (((Comparable<? super K>) key).compareTo(b.bucket.get(i).T()) == 0)
						return b.bucket.get(i);
				} else {
					if (c.compare((K) key, b.bucket.get(i).T()) == 0)
						return b.bucket.get(i);
				}
			}
			return null;
		} else {
			GrayNode g = (GrayNode) root;
			for (int i = 0; i < g.bucket.size(); i++) {
				if (c == null) {
					if (((Comparable<? super K>) key).compareTo(g.bucket.get(i)) < 0)
						return chooseBranch((i+1), g, (K) key);
				} else {
					if (c.compare((K) key, g.bucket.get(i)) == 0)
						return chooseBranch((i+1), g, (K) key);
				}
			}
		}
		return null;
	}
	
	private Pair<K,V> chooseBranch(int child, GrayNode g, K key) {
		switch (child) {
		case 1: return getHelp(g.c1, key);
		case 2: return getHelp(g.c2, key);
		case 3: return getHelp(g.c3, key);
		case 4: return getHelp(g.c4, key);
		case 5: return getHelp(g.c5, key);
		case 6: return getHelp(g.c6, key);
		case 7: return getHelp(g.c7, key);
		case 8: return getHelp(g.c8, key);
		case 9: return getHelp(g.c9, key);
		case 10: return getHelp(g.c10, key);
		case 11: return getHelp(g.c11, key);
		default: return getHelp(g.c12, key);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null || root == null) {
			return false;
		} else {
			return containsValueHelp(root, (V) value);
		}
	}
	
	private boolean containsValueHelp(Node root, V value) {
		if (root.getClass().equals(BlackNode.class)) {
			for (Pair<K,V> pair : ((BlackNode) root).bucket) {
				if (pair.U().equals(value)) {
					return true;
				}
			}
			return false;
		} else if (root.getClass().equals(GrayNode.class)) {
			GrayNode g = (GrayNode) root;
			return containsValueHelp(g.c1, value) || containsValueHelp(g.c2, value) || containsValueHelp(g.c3, value) || containsValueHelp(g.c4, value) || 
					containsValueHelp(g.c5, value) || containsValueHelp(g.c6, value) || containsValueHelp(g.c7, value) || containsValueHelp(g.c8, value) || 
					containsValueHelp(g.c9, value) || containsValueHelp(g.c10, value) || containsValueHelp(g.c11, value) || containsValueHelp(g.c12, value);
		} else {
			return false;
		}
	}

	@Override
	public V get(Object key) {
		return getHelp(root, (K) key).U();
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public V put(K key, V value) {
		Pair<Node,V> t = putHelp(key, value, root);
		root = t.T();
		return t.U();
	}

	private Pair<Node,V> putHelp(K key, V value, Node root) {
		if (!containsKey(key)) {
			if (root == null) {
				root = new BlackNode();
				((BlackNode) root).bucket.add(new Pair<K,V>(key,value));
				modCount++;
				return null;
			} else if (root.getClass().equals(BlackNode.class)) {
				if (((BlackNode) root).bucket.size() < leafOrder) {
					((BlackNode) root).insert(key, value);
				} else {
					
				}
			}
			return null;
		} else {
			
		}
		return null;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		return sizeHelp(root);
	}
	
	private int sizeHelp(Node root) {
		if (root.getClass().equals(BlackNode.class)) {
			return ((BlackNode) root).bucket.size();
		} else if (root.getClass().equals(GrayNode.class)) {
			GrayNode g = (GrayNode) root;
			return sizeHelp(g.c1) + sizeHelp(g.c2) + sizeHelp(g.c3) + sizeHelp(g.c4) + 
					sizeHelp(g.c5) + sizeHelp(g.c6) + sizeHelp(g.c7) + sizeHelp(g.c8) + 
					sizeHelp(g.c9) + sizeHelp(g.c10) + sizeHelp(g.c11) + sizeHelp(g.c12);
		} else {
			return 0;
		}
	}

	@Override
	public Comparator<? super K> comparator() {
		return c;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K firstKey() {
		return firstKeyHelp(root);
	}
	
	private K firstKeyHelp(Node root) {
		if (root.getClass().equals(BlackNode.class)) {
			return ((BlackNode) root).bucket.get(0).T();
		} else {
			return firstKeyHelp(((GrayNode) root).c1);
		}
	}

	@Override
	public K lastKey() {
		return lastKeyHelp(root);
	}
	
	private K lastKeyHelp(Node root) {
		if (root.getClass().equals(BlackNode.class)) {
			return ((BlackNode) root).bucket.get(((BlackNode) root).bucket.size()-1).T();
		} else {
			GrayNode g = (GrayNode) root;
			int numChild = g.bucket.size();
			switch(numChild) {
				case 1: return lastKeyHelp(((GrayNode) root).c2);
				case 2: return lastKeyHelp(((GrayNode) root).c3);
				case 3: return lastKeyHelp(((GrayNode) root).c4);
				case 4: return lastKeyHelp(((GrayNode) root).c5);
				case 5: return lastKeyHelp(((GrayNode) root).c6);
				case 6: return lastKeyHelp(((GrayNode) root).c7);
				case 7: return lastKeyHelp(((GrayNode) root).c8);
				case 8: return lastKeyHelp(((GrayNode) root).c9);
				case 9: return lastKeyHelp(((GrayNode) root).c10);
				case 10: return lastKeyHelp(((GrayNode) root).c11);
				case 11: return lastKeyHelp(((GrayNode) root).c12);
				default: return null;
			}
		}
	}

	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// The following methods do not need to be implemented
	@Override
	public SortedMap<K, V> headMap(K toKey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedMap<K, V> tailMap(K fromKey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

}
