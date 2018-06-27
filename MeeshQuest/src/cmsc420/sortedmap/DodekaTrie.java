package cmsc420.sortedmap;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DodekaTrie<K, V> implements SortedMap<K, V> {
	private RootNode root;

	private int size = 0;

	private final int leafOrder;

	private int height = 1;

	protected int modCount = Integer.MIN_VALUE;
	private int DEFAULT_LEAF_ORDER = 3;

	class DefaultComparator implements Comparator<K> {
		@SuppressWarnings("unchecked")
		public int compare(K o1, K o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			} else {
				if (o2 == null) {
					return 1;
				} else {
					return ((Comparable<K>) o1).compareTo(o2);
				}
			}
		}
	}

	public DodekaTrie(int leafOrder) {
		this.leafOrder = leafOrder;
		root = new RootNode(new DefaultComparator(), leafOrder);
	}

	public DodekaTrie(Comparator<K> comparator, int leafOrder) {
		this.leafOrder = leafOrder;
		if (comparator == null) {
			comparator = new DefaultComparator();
		}
		root = new RootNode(comparator, leafOrder);
	}

	public Comparator<? super K> comparator() {
		return root.getComparator();
	}

	public K firstKey() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}

		return root.getFirstLeaf().getKeys().get(0);
	}

	public SortedMap<K, V> headMap(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public K lastKey() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}

		LeafNode<K, V> l = root.getLastLeaf();
		return l.getKeys().get(l.size() - 1);
	}

	public SortedMap<K, V> subMap(K arg0, K arg1) {
		return new SubMap(this, arg0, arg1);
	}

	public SortedMap<K, V> tailMap(K arg0) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		root = new RootNode(root.getComparator(), leafOrder);
		size = 0;
		modCount++;
	}

	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}

		return root.contains((K) key);
	}

	public boolean containsValue(Object arg0) {
		for (Map.Entry<K, V> entry : entrySet()) {
			if (entry.getValue().equals(arg0)) {
				return true;
			}
		}
		return false;
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	@SuppressWarnings("unchecked")
	public V get(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}

		return root.get((K) key);
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	private int compare(Object k1, Object k2) {
		return root.comparator == null ? ((Comparable<? super K>) k1)
				.compareTo((K) k2) : root.comparator.compare((K) k1, (K) k2);
	}

	public V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException();
		}

		V oldVal = get(key);
		if (!root.contains(key) && size != Integer.MAX_VALUE) {
			size++;
		}

		root.put(key, value);
		modCount++;
		return oldVal;
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public V remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return size;
	}

	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	public boolean equals(Object arg0) {
		if (arg0 instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<K, V> m1 = (Map<K, V>) arg0;
			return m1.entrySet().equals(entrySet());
		}
		return false;
	}

	public int hashCode() {
		return entrySet().hashCode();
	}

	public String toString() {
		Iterator<Map.Entry<K,V>> i = entrySet().iterator();
		if (! i.hasNext()) {
			return "{}";
		}
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Map.Entry<K,V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key   == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value);
			if (! i.hasNext())
				return sb.append('}').toString();
			sb.append(", ");
		}
	}

	public void addToXmlDoc(Document doc, Element parentNode) {
		Element dodekaTrie = doc.createElement("DodekaTrie");
		dodekaTrie.setAttribute("cardinality", Integer.toString(size()));
		dodekaTrie.setAttribute("leafOrder", Integer.toString(leafOrder));
		root.addToXmlDoc(doc, dodekaTrie);
		parentNode.appendChild(dodekaTrie);
	}

	private class RootNode extends Node<K, V> {
		private Node<K, V> me;

		private EndNode<K, V> first;

		private EndNode<K, V> last;

		private int leafOrder;

		public RootNode(Comparator<K> comparator, int leafOrder) {
			super(comparator, NodeType.ROOT);
			this.leafOrder = leafOrder;
			LeafNode<K, V> tmp = new LeafNode<K, V>(comparator, this.leafOrder);
			this.first = new EndNode<K, V>();
			this.last = new EndNode<K, V>();
			this.first.setRight(tmp);
			this.last.setLeft(tmp);
			tmp.setLeft(first);
			tmp.setRight(last);
			this.me = tmp;
		}

		public V get(K key) {
			return me.get(key);
		}

		public V remove(K key) {
			throw new UnsupportedOperationException();
		}

		public void put(K key, V value) {
			if (me.isFull()) {
				// dummy new root
				GuideNode<K, V> newRoot = new GuideNode<K, V>(
						me.getComparator());
				newRoot.setLeft(new EndNode<K, V>());
				newRoot.setRight(new EndNode<K, V>());
				newRoot.getLeft().setRight(newRoot);
				newRoot.getRight().setLeft(newRoot);
				me.setParent(newRoot);
				me.put(key, value);
				if (newRoot.size() > 0) {
					me = newRoot;
					height++;
				} else {
					me.setParent(null);
				}
			} else {
				me.put(key, value);
			}
		}

		public LeafNode<K, V> getFirstLeaf() {
			return (LeafNode<K, V>) first.getRight();
		}

		public LeafNode<K, V> getLastLeaf() {
			return (LeafNode<K, V>) last.getLeft();
		}

		public void addToXmlDoc(Document doc, Element parent) {
			me.addToXmlDoc(doc, parent);
		}
	}

	private class Entry implements Map.Entry<K, V> {
		private K key;
		private PrivateNodeIterator srcItr;

		public Entry(K key, PrivateNodeIterator srcItr) {
			this.key = key;
			this.srcItr = srcItr;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return get(key);
		}

		public V setValue(V arg0) {
			if (srcItr == null) {
				throw new IllegalStateException(
						"this entry is readonly because it is not created by an iterator");
			}
			V ret = put(key, arg0);
			srcItr.setModCount(DodekaTrie.this.modCount);
			return ret;
		}

		public boolean equals(Object arg0) {
			if (arg0 instanceof Map.Entry) {
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> e2 = (Map.Entry<K, V>) arg0;
				return (key == null ? e2.getKey() == null : key.equals(e2
						.getKey()))
						&& (getValue() == null ? e2.getValue() == null
						: getValue().equals(e2.getValue()));
			}
			return false;
		}

		public int hashCode() {
			return (key == null ? 0 : key.hashCode())
					^ (getValue() == null ? 0 : getValue().hashCode());
		}

		public String toString() {
			return key + "=" + getValue();
		}
	}

	private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
		public boolean add(Map.Entry<K, V> arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(Collection<? extends Map.Entry<K, V>> arg0) {
			throw new UnsupportedOperationException();
		}

		public void clear() {
			DodekaTrie.this.clear();
		}

		public boolean contains(Object entry) {
			if (entry instanceof Map.Entry) {
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> e1 = (Map.Entry<K, V>) entry;
				Object value = DodekaTrie.this.get(e1.getKey());
				return e1.getValue() == null ? value == null : e1.getValue()
						.equals(value);
			}
			return false;
		}

		public boolean containsAll(Collection<?> entries) {
			boolean flag = true;
			for (Object o : entries) {
				flag = flag && contains(o);
			}
			return flag;
		}

		public boolean isEmpty() {
			return DodekaTrie.this.isEmpty();
		}

		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@SuppressWarnings("unchecked")
		public boolean remove(Object entry) {
			throw new UnsupportedOperationException();
		}

		public boolean removeAll(Collection<?> entries) {
			throw new UnsupportedOperationException();
		}

		public boolean retainAll(Collection<?> entries) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return DodekaTrie.this.size();
		}

		public Object[] toArray() {
			Object[] arr = new Object[size()];
			Iterator<Map.Entry<K, V>> it = new EntryIterator();
			for (int i = 0; it.hasNext(); i++) {
				arr[i] = it.next();
			}
			return arr;
		}

		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] array) {
			if (array.length < size())
				array = (T[]) Array.newInstance(array.getClass()
						.getComponentType(), size());

			Iterator<Map.Entry<K, V>> it = new EntryIterator();
			for (int i = 0; it.hasNext(); i++) {
				array[i] = (T) it.next();
			}
			return array;
		}

		public boolean equals(Object arg0) {
			if (arg0 instanceof Set) {
				@SuppressWarnings("unchecked")
				Set<Map.Entry<K, V>> s1 = (Set<Map.Entry<K, V>>) arg0;
				if (s1.size() != size()) {
					return false;
				}

				Iterator<Map.Entry<K, V>> it = s1.iterator();
				while (it.hasNext()) {
					if (!contains(it.next())) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

		public int hashCode() {
			int hashCode = 0;
			for (Map.Entry<K, V> entry : this) {
				hashCode += entry.hashCode();
			}
			return hashCode;
		}

		private class EntryIterator extends PrivateNodeIterator<Map.Entry<K, V>> {
			private Entry curr, prev;
			private LeafNode<K, V> currNode;
			private Iterator<K> it;

			public EntryIterator() {
				modCount = DodekaTrie.this.modCount;
				if (DodekaTrie.this.isEmpty())
					return;

				this.currNode = root.getFirstLeaf();
				this.it = currNode.leafKeyIterator();

				while (curr == null
						|| DodekaTrie.this.comparator().compare(curr.getKey(),
								DodekaTrie.this.firstKey()) < 0) {
					if (!it.hasNext()) {
						if (currNode.getRight() instanceof EndNode)
							return;

						currNode = (LeafNode<K, V>) currNode.getRight();
						it = currNode.leafKeyIterator();
						continue;
					}

					curr = new Entry(it.next(), this);
				}
			}

			public boolean hasNext() {
				if (modCount != DodekaTrie.this.modCount) {
					throw new ConcurrentModificationException();
				}
				return curr != null;
			}

			public Map.Entry<K, V> next() {
				if (modCount != DodekaTrie.this.modCount)
					throw new ConcurrentModificationException();

				if (curr == null)
					throw new NoSuchElementException();

				if (!it.hasNext() && !(currNode.getRight() instanceof EndNode)) {
					currNode = (LeafNode<K, V>) currNode.getRight();
					it = currNode.leafKeyIterator();
					return next();
				}
				prev = curr;
				if (it.hasNext() && curr != null
						&& DodekaTrie.this.lastKey() != null
						&& cmp(curr.getKey(), DodekaTrie.this.lastKey()) < 0)
					curr = new Entry(it.next(), this);
				else
					curr = null;

				return prev;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			private int cmp(K k1, K k2) {
				return DodekaTrie.this.comparator().compare(k1, k2);
			}
		}
	}

	abstract class PrivateNodeIterator<T> implements Iterator<T> {
		private int modCount;
		public void setModCount(int modCount) {
			this.modCount = modCount;
		}
	}

	final class SubMap<K, V> extends AbstractMap<K, V> implements SortedMap<K, V> {
		final DodekaTrie<K, V> m;
		final K low;
		final K high;
		EntrySetView entrySetView = null;

		SubMap(DodekaTrie<K, V> m, K low, K high) {
			if (low == null && high == null)
				throw new IllegalArgumentException();

			if (low != null && high != null)
				if (m.compare(low, high) > 0)
					throw new IllegalArgumentException();

			this.m = m;
			this.low = low;
			this.high = high;
		}

		public Comparator<? super K> comparator() {
			return m.comparator();
		}

		public final V put(K key, V value) {
			if (!inRange(key))
				throw new IllegalArgumentException("key out of range");
			return m.put(key, value);
		}

		public final V remove(Object key) {
			throw new UnsupportedOperationException();
		}

		// TODO FIX
		public K firstKey() {
			if (m.isEmpty()) {
				throw new NoSuchElementException();
			}

			if (low == null) {
				return m.firstKey();
			}

			Iterator<Entry<K, V>> iter = m.entrySet().iterator();
			Entry<K,V> curr = null;
			if (iter.hasNext()) {
				curr = iter.next();
			} else {
				return null;
			}
			while (iter.hasNext() && cmp(curr.getKey(), low) < 0) {
				curr = iter.next();
			}

			if (cmp(curr.getKey(), low) < 0 || (high != null && cmp(curr.getKey(), high) >= 0)) {
				throw new NoSuchElementException();
			} else {
				return curr.getKey();

			}			
		}

		// TODO FIX
		public K lastKey() {
			if (m.isEmpty()) {
				throw new NoSuchElementException();
			}
			if (high == null) {
				return m.lastKey();
			}

			Iterator<Entry<K, V>> iter = m.entrySet().iterator();
			Entry<K,V> curr = null, prev = null;
			if (iter.hasNext()) {
				curr = iter.next();
			} else {
				return null;
			}
			while (iter.hasNext() && cmp(curr.getKey(), high) < 0) {
				prev = curr;
				curr = iter.next();
			}

			if (!iter.hasNext() && cmp(curr.getKey(), high) < 0) {
				prev = curr;
			}

			if (cmp(prev.getKey(), low) < 0 || cmp(prev.getKey(), high) >= 0) {
				System.out.println(prev.getKey());
				throw new NoSuchElementException();
			} else {
				return prev.getKey();
			}			
		}

		private int cmp(K k1, K k2) {
			return m.root.comparator.compare(k1, k2);
		}

		public Set<Map.Entry<K, V>> entrySet() {
			EntrySetView esv = entrySetView;
			return (esv != null) ? esv : (entrySetView = new EntrySetView());
		}

		public SortedMap<K, V> headMap(K toKey) {
			if (!inRange(toKey))
				throw new IllegalArgumentException();

			return new SubMap<K, V>(m, low, toKey);
		}

		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			if (!inRange(fromKey) || !inRange(toKey))
				throw new IllegalArgumentException();

			return new SubMap<K, V>(m, fromKey, toKey);
		}

		public SortedMap<K, V> tailMap(K fromKey) {
			if (!inRange(fromKey))
				throw new IllegalArgumentException();

			return new SubMap<K, V>(m, fromKey, high);
		}

		final boolean tooLow(Object key) {
			if (low != null) {
				int c = m.compare(key, low);
				if (c < 0)
					return true;
			}
			return false;
		}

		final boolean tooHigh(Object key) {
			if (high != null) {
				int c = m.compare(key, high);
				if (c >= 0)
					return true;
			}
			return false;
		}

		final boolean inRange(Object key) {
			return !tooLow(key) && !tooHigh(key);
		}

		public boolean equals(final Object other) {
			if (other == this)
				return true;
			else if (other instanceof SubMap) {
				@SuppressWarnings("unchecked")
				SubMap<?, ?> otherMap = (SubMap<?, ?>) other;
				return otherMap.m.equals(m) && low == null
						^ low.equals(otherMap.low) && high == null
						^ high.equals(otherMap.low);
			} else if (other instanceof Map) {
				Map<?, ?> otherMap = (Map<?, ?>) other;
				return entrySet().containsAll(otherMap.entrySet())
						&& otherMap.size() == size();
			} else
				return false;
		}

		class EntrySetView extends AbstractSet<Map.Entry<K, V>> {
			public Iterator<Map.Entry<K, V>> iterator() {
				return new SubMapEntrySetIterator() ;
			}
			public int size() {
				int size = 0;
				Iterator<Entry<K, V>> i = iterator();
				while (i.hasNext()) {
					size++;
					i.next();
				}
				return size;
			}

			public boolean remove(Object o) {
				throw new UnsupportedOperationException();
			}
		}

		class SubMapEntrySetIterator extends PrivateNodeIterator<Map.Entry<K, V>> {
			int expectedModCount = m.modCount;
			private cmsc420.sortedmap.DodekaTrie.Entry curr, prev;
			private LeafNode<K, V> currNode;
			private Iterator<K> it;

			public SubMapEntrySetIterator() {

				if (m.isEmpty())
					return;

				this.currNode = m.root.getFirstLeaf();
				this.it = currNode.leafKeyIterator();

				try {
					while (curr == null	|| cmp((K) curr.getKey(), firstKey()) < 0) {
						if (!it.hasNext()) {
							if (currNode.getRight() instanceof EndNode)
								return;

							currNode = (LeafNode<K, V>) currNode.getRight();
							it = currNode.leafKeyIterator();
							continue;
						}

						curr = new DodekaTrie.Entry(it.next(), this);
					}
				}  catch (NoSuchElementException e) {}
			}

			public boolean hasNext() {
				if (curr != null)
					return inRange(curr.getKey());
				else
					return false;
			}

			public Map.Entry<K, V> next() {
				if (m.modCount != expectedModCount)
					throw new ConcurrentModificationException();

				if (curr == null)
					throw new NoSuchElementException();

				if (!it.hasNext() && !(currNode.getRight() instanceof EndNode) && cmp((K) curr.getKey(), high) < 0) {
					currNode = (LeafNode<K, V>) currNode.getRight();
					it = currNode.leafKeyIterator();
					return next();
				}
				prev = curr;
				if (it.hasNext() && curr != null
						&& m.lastKey() != null
						&& cmp((K) curr.getKey(), (K) lastKey()) < 0)
					curr = new DodekaTrie.Entry(it.next(), this);
				else
					curr = null;

				return prev;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public void setModCount(int modCount) {
				this.expectedModCount = modCount;
			}
		}
	}
}
