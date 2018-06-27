package cmsc420.sortedmap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GuideNode<K, V> extends Node<K, V> {
	protected LinkedList<K> guides;

	protected LinkedList<Node<K, V>> kids;

	protected static final int order = 12;

	public GuideNode(Comparator<K> comparator) {
		super(comparator, NodeType.GUIDE);
		this.guides = new LinkedList<K>();
		this.kids = new LinkedList<Node<K, V>>();
	}

	protected GuideNode(Comparator<K> comparator, List<K> guides,
			List<Node<K, V>> kids) {
		super(comparator, NodeType.GUIDE);
		this.guides = new LinkedList<K>(guides);
		this.kids = new LinkedList<Node<K, V>>(kids);
	}

	public int size() {
		return kids.size();
	}

	public boolean isFull() {
		return kids.size() == order;
	}

	protected boolean isOverFull() {
		return kids.size() > order;
	}

	public List<Node<K, V>> getKids() {
		return kids;
	}

	public void put(K key, V value) {
		int index = kidIndex(key);

		kids.get(index).put(key, value);
	}

	public V get(K key) {
		return kids.get(kidIndex(key)).get(key);
	}

	public V remove(K key) {
		throw new UnsupportedOperationException("Remove is not implemented");
	}

	protected void split() {
		int halfGuides = guides.size() / 2;
		int halfKids = halfGuides + 1;
		GuideNode<K, V> left = new GuideNode<K, V>(comparator, guides.subList(
				0, halfGuides), kids.subList(0, halfKids));
		GuideNode<K, V> right = new GuideNode<K, V>(comparator, guides.subList(
				halfGuides + 1, guides.size()), kids.subList(halfKids,
				kids.size()));
		left.setLeft(getLeft());
		left.setRight(right);
		getLeft().setRight(left);
		left.setParent(getParent());
		for (Node<K, V> kid : left.kids) {
			kid.setParent(left);
		}

		right.setLeft(left);
		right.setRight(getRight());
		getRight().setLeft(right);
		right.setParent(getParent());
		for (Node<K, V> kid : right.kids) {
			kid.setParent(right);
		}

		parent.insertKid(this, guides.get(halfGuides), left, right);
	}

	protected void insertKid(Node<K, V> old, K key, Node<K, V> left,
			Node<K, V> right) {
		int ind = kids.indexOf(old);
		if (ind < 0) {
			// when the root splits, the two new nodes are added to the dummy
			// new root
			kids.add(left);
			kids.add(right);
			guides.add(key);
			return;
		}

		kids.set(ind, left);
		kids.add(ind + 1, right);
		guides.add(ind, key);

		if (isOverFull()) {
			split();
		}
	}

	protected int kidIndex(K key) {
		int i = 0;
		for (K g : guides) {
			if (cmp(key, g) < 0) {
				break;
			}
			++i;
		}
		return i;
	}

	public void addToXmlDoc(Document doc, Element parent) {
		Element thisNode = doc.createElement("guide");
		Iterator<K> guideItr = guides.iterator();
		Iterator<Node<K, V>> kidItr = kids.iterator();
		while (guideItr.hasNext()) {
			assert (kidItr.hasNext());
			kidItr.next().addToXmlDoc(doc, thisNode);
			Element key = doc.createElement("key");
			key.setAttribute("value", guideItr.next().toString());
			thisNode.appendChild(key);
		}
		assert (kidItr.hasNext());
		// Remember to add the last child.
		kidItr.next().addToXmlDoc(doc, thisNode);
		parent.appendChild(thisNode);
	}
}