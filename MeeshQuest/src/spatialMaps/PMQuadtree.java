package spatialMaps;

import java.util.ArrayList;

import cmsc420.meeshquest.part1.City;
import cmsc420.meeshquest.part1.Geometry;
import cmsc420.meeshquest.part1.Road;
import spatialMaps.PRQuadTree.GreyNode;

@SuppressWarnings("unchecked")
public class PMQuadtree {
	
	private Node root;
	private int height, width;
	
	public class BlackNode extends Node {
		private ArrayList<Geometry> items;
		
		public BlackNode (int xmin, int xmax, int ymin, int ymax) {
			super.setXmin(xmin);
			super.setXmax(xmax);
			super.setYmin(ymin);
			super.setYmax(ymax);
		}
		
		private boolean containsCity() {
			for(Geometry g : items) {
				if (g.getType() == Geometry.POINT)
					return true;
			}
			return false;
		}
		
		private void add(Geometry g) {
			items.add(g);
		}
		
		private Node add(Geometry g, Node root) {
			if (g.getType() == Geometry.POINT && containsCity()) {
				root = partition(g, root);
				return root;
			} else {
				items.add(g);
				return this;
			}
		}
		
		private Node partition(Geometry g, Node root) {
			root = new GreyNode(root.getXmin(), root.getXmax(), root.getYmin(), root.getYmax());
			root = ((GreyNode) root).add(items, root);
			root = insertHelp(g, root);
			return root;
		}
		
		private Node remove(Geometry g, Node root) {
			throw new UnsupportedOperationException("Not yet implemented");
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
		
		private Node add(Geometry g, Node root) {
			if (g.isCity()) {
				City c = (City) g;
				int x = (int) c.getPt().getX();
				int y = (int) c.getPt().getY();
				if (x > ((root.getXmax()-root.getXmin())/2) + root.getXmin()) {
					if (y > ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
						((GreyNode) root).NE = insertHelp(g, ((GreyNode) root).NE);
					} else if (y == ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
						((GreyNode) root).NE = insertHelp(g, ((GreyNode) root).NE);
						((GreyNode) root).SE = insertHelp(g, ((GreyNode) root).SE);
					} else {
						((GreyNode) root).SE = insertHelp(g, ((GreyNode) root).SE);
					}
				} else if (x == ((root.getXmax()-root.getXmin())/2) + root.getXmin()) { 
					if (y > ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
						((GreyNode) root).NE = insertHelp(g, ((GreyNode) root).NE);
						((GreyNode) root).NW = insertHelp(g, ((GreyNode) root).NE);
					} else if (y == ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
						((GreyNode) root).NE = insertHelp(g, ((GreyNode) root).NE);
						((GreyNode) root).NW = insertHelp(g, ((GreyNode) root).NE);
						((GreyNode) root).SE = insertHelp(g, ((GreyNode) root).SE);
						((GreyNode) root).SW = insertHelp(g, ((GreyNode) root).SE);
					} else {
						((GreyNode) root).SE = insertHelp(g, ((GreyNode) root).SE);
						((GreyNode) root).SW = insertHelp(g, ((GreyNode) root).SE);
					}
				} else {
					if (y > ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
						((GreyNode) root).NW = insertHelp(g, ((GreyNode) root).NW);
					} else if (y == ((root.getYmax()-root.getYmin())/2) + root.getYmin()) {
						((GreyNode) root).NW = insertHelp(g, ((GreyNode) root).NW);
						((GreyNode) root).SW = insertHelp(g, ((GreyNode) root).SW);
					} else {
						((GreyNode) root).SW = insertHelp(g, ((GreyNode) root).SW);
					}
				}
			} else {
				Road r = (Road) g;
				
			}
			
			return root;
		}
		
		private Node add(ArrayList<Geometry> g, Node root) {
			
		}
		
		private Node remove(Geometry g, Node root) {
			throw new UnsupportedOperationException("Not yet implemented");
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
		
		private Node add(Geometry g) {
			BlackNode n = new BlackNode(getXmin(), getXmax(), getYmin(), getYmax());
			n.add(g);
			return n;
		}
	}
	
	public PMQuadtree(int width, int height) {
		root = new WhiteNode(0, width, 0, height);
		this.height = height;
		this.width = width;
	}
	
	public void insert(Geometry elem) {
		root = insertHelp(elem, root);
	}
	
	private Node insertHelp(Geometry elem, Node root) {
		if (root.getClass().equals(BlackNode.class)) {
			return ((BlackNode) root).add(elem, root);
		} else if (root.getClass().equals(GreyNode.class)) {
			return ((GreyNode) root).add(elem, root);
		} else {
			return ((WhiteNode)root).add(elem);
		}
	}
}
