package spatialMaps;

import java.awt.geom.Point2D;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class PRQuadTree<T extends Point2D.Float> {
	
	private Node root;
	private int height, width;
	
	public class BlackNode extends Node {
		private T data;
		
		public BlackNode(T data, int xmin, int xmax, int ymin, int ymax) {
			this.data = data;
			super.setXmin(xmin);
			super.setXmax(xmax);
			super.setYmin(ymin);
			super.setYmax(ymax);
		}
		
		public T getData() {
			return data;
		}
	}
	
	public class GreyNode extends Node {
		private Node NW, NE, SW, SE;		
		
		public GreyNode(int xmin, int xmax, int ymin, int ymax) {
			super.setXmin(xmin);
			super.setXmax(xmax);
			super.setYmin(ymin);
			super.setYmax(ymax);
			NW = new WhiteNode(xmin,(xmax+xmin)/2,(ymax+ymin)/2,ymax);
			NE = new WhiteNode(((xmax+xmin)/2),xmax,((ymax+ymin)/2),ymax);
			SW = new WhiteNode(xmin,(xmax+xmin)/2,ymin,(ymax+ymin)/2);
			SE = new WhiteNode((xmax+xmin)/2,xmax,ymin,(ymax+ymin)/2);
		}
		
		public boolean required() {
			int i = 0;
			if (NW.getClass().equals(BlackNode.class)) {
				i++;
			} else if (NW.getClass().equals(GreyNode.class)) {
				i = i + 2;
			}
			
			if (NE.getClass().equals(BlackNode.class)) {
				i++;
			} else if (NE.getClass().equals(GreyNode.class)) {
				i = i + 2;
			}
			
			if (SW.getClass().equals(BlackNode.class)) {
				i++;
			} else if (SW.getClass().equals(GreyNode.class)) {
				i = i + 2;
			}
			
			if (SE.getClass().equals(BlackNode.class)) {
				i++;
			} else if (SE.getClass().equals(GreyNode.class)) {
				i = i + 2;
			}
			
			return i >= 2;
		}
	}
	
	public class WhiteNode extends Node {
		public WhiteNode(int xmin, int xmax, int ymin, int ymax) {
			super.setXmin(xmin);
			super.setXmax(xmax);
			super.setYmin(ymin);
			super.setYmax(ymax);
		}
	}
	
	public PRQuadTree(int width, int height) {
		root = new WhiteNode(0, width, 0, height);
		this.height = height;
		this.width = width;
	}
	
	public void insert(T elem) {
		root = insertHelp(root, elem);
	}
	
	private Node insertHelp(Node root, T elem) {
		int x = (int) elem.getX();
		int y = (int) elem.getY();

		if (root.getClass().equals(BlackNode.class)) {
			T data = ((BlackNode) root).getData();
			int xmin = root.getXmin();
			int xmax = root.getXmax();
			int ymin = root.getYmin();
			int ymax = root.getYmax();
			root = new GreyNode(xmin, xmax, ymin, ymax);
			root = insertHelp(root, data);
			root = insertHelp(root, elem);
			
		} else if (root.getClass().equals(GreyNode.class)) {
			if (x >= ((root.getXmax()-root.getXmin())/2) + root.getXmin()) {
				if (y >= ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
					((GreyNode) root).NE = insertHelp(((GreyNode) root).NE, elem);
				} else {
					((GreyNode) root).SE = insertHelp(((GreyNode) root).SE, elem);
				}
			} else {
				if (y >= ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
					((GreyNode) root).NW = insertHelp(((GreyNode) root).NW, elem);
				} else {
					((GreyNode) root).SW = insertHelp(((GreyNode) root).SW, elem);
				}
			}
		} else {
			root = new BlackNode(elem, root.getXmin(), root.getXmax(), root.getYmin(), root.getYmax());
		}
		
		return root;
	}
	
	public void delete(T elem){
		root = deleteHelp(root, elem);
	}
	
	private Node deleteHelp(Node root, T elem) {
		int x = (int) elem.getX();
		int y = (int) elem.getY();
		
		if (root.getClass().equals(BlackNode.class)) {
			if (elem.equals(((BlackNode) root).getData())) {
				root = new WhiteNode(root.getXmin(), root.getXmax(), root.getYmin(), root.getYmax());
			}
		} else if (root.getClass().equals(GreyNode.class)) {
			if (x >= ((root.getXmax()-root.getXmin())/2) + root.getXmin()) {
				if (y >= ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
					((GreyNode) root).NE = deleteHelp(((GreyNode) root).NE, elem);
				} else {
					((GreyNode) root).SE = deleteHelp(((GreyNode) root).SE, elem);
				}
			} else {
				if (y >= ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
					((GreyNode) root).NW = deleteHelp(((GreyNode) root).NW, elem);
				} else {
					((GreyNode) root).SW = deleteHelp(((GreyNode) root).SW, elem);
				}
			}
			
			if (!((GreyNode) root).required()) {
				if (((GreyNode) root).NE.getClass().equals(BlackNode.class)) {
					T data = ((BlackNode) ((GreyNode) root).NE).getData();
					root = new BlackNode(data, root.getXmin(), root.getXmax(), root.getYmin(), root.getYmax());
					
				} else if (((GreyNode) root).NW.getClass().equals(BlackNode.class)) {
					T data = ((BlackNode) ((GreyNode) root).NW).getData();
					root = new BlackNode(data, root.getXmin(), root.getXmax(), root.getYmin(), root.getYmax());
					
				} else if (((GreyNode) root).SE.getClass().equals(BlackNode.class)) {
					T data = ((BlackNode) ((GreyNode) root).SE).getData();
					root = new BlackNode(data, root.getXmin(), root.getXmax(), root.getYmin(), root.getYmax());
					
				} else if (((GreyNode) root).SW.getClass().equals(BlackNode.class)) {
					T data = ((BlackNode) ((GreyNode) root).SW).getData();
					root = new BlackNode(data, root.getXmin(), root.getXmax(), root.getYmin(), root.getYmax());
				}
			}
		}
		
		return root;
	}
	
	public boolean containsElem(T elem) {
		return containsHelp(root, elem);
	}
	
	private boolean containsHelp(Node root, T elem) {
		if (root.getClass().equals(BlackNode.class)) {
			if (elem.equals(((BlackNode) root).getData())) {
				return true;
			}
		} else if (root.getClass().equals(GreyNode.class)) {
			int x = (int) elem.getX();
			int y = (int) elem.getY();
			if (x >= ((root.getXmax()-root.getXmin())/2) + root.getXmin()) {
				if (y >= ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
					return containsHelp(((GreyNode) root).NE, elem);
				} else {
					return containsHelp(((GreyNode) root).SE, elem);
				}
			} else {
				if (y >= ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
					return containsHelp(((GreyNode) root).NW, elem);
				} else {
					return containsHelp(((GreyNode) root).SW, elem);
				}
			}
		}
		return false;
	}
	
	public void clearAll() {
		int spatialWidth = root.getXmax();
		int spatialHeight = root.getYmax();
		root = new WhiteNode(0, spatialWidth, 0, spatialHeight);
	}
	
	public boolean isEmpty() {
		return root.getClass().equals(WhiteNode.class);
	}
	
	public ArrayList<Node> breadthFirst() {
		return breadthFirstHelp(root, new ArrayList<Node>());
	}
	
	private ArrayList<Node> breadthFirstHelp(Node root, ArrayList<Node> arr) {
		if (root.getClass().equals(BlackNode.class) || root.getClass().equals(WhiteNode.class)) {
			arr.add(root);
		} else if (root.getClass().equals(GreyNode.class)) {
			arr.add(root);
			breadthFirstHelp(((GreyNode) root).NW, arr);
			breadthFirstHelp(((GreyNode) root).NE, arr);
			breadthFirstHelp(((GreyNode) root).SW, arr);
			breadthFirstHelp(((GreyNode) root).SE, arr);
			//End of Grey Node
			arr.add(null);
		}
		
		return arr;
	}

	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}

}
