package spatialMaps;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.meeshquest.part2.City;
import cmsc420.meeshquest.part2.Geometry;
import cmsc420.meeshquest.part2.Road;

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
			super.setRegion(xmin, ymax, (xmax-xmin), (ymax-ymin));
		}
		
		private boolean containsCity() {
			for(Geometry g : items) {
				if (g.getType() == Geometry.POINT)
					return true;
			}
			return false;
		}
		
		private boolean containsCity(City c) {
			for(Geometry g : items) {
				if (g.getType() == Geometry.POINT)
					return ((City) g).equals(c);
			}
			return false;
		}
		
		private boolean containsRoad(Road r) {
			for(Geometry g : items) {
				if (g.getType() == Geometry.SEGMENT && ((Road) g).equals(r)) {
					return true;
				}
			}
			return false;
		}
		
		private boolean containsGeom(Geometry g) {
			if (g.getType() == Geometry.POINT) {
				return containsCity((City) g);
			} else {
				return containsRoad((Road) g);
			}
		}
		
		private Node add(Geometry g) {
			if (g.getType() == Geometry.POINT && containsCity()) {
				return partition(g);
			} else {
				if (g.getType() == Geometry.POINT) {
					items.add(0, g);
				} else {
					items.add(g);
				}
				return this;
			}
		}
		
		private Node partition(Geometry g) {
			Node n = new GreyNode(getXmin(), getXmax(), getYmin(), getYmax());
			for(Geometry geo : items) {
				n = ((GreyNode) n).add(geo);
			}
			n = ((GreyNode) n).add(g);
			return n;
		}
		
		private Node remove(Geometry g, Node root) {
			throw new UnsupportedOperationException("Not yet implemented");
		}
		
		public ArrayList<Geometry> getItems() {
			return items;
		}
	}
	
	public class GreyNode extends Node {
		private Node NW, NE, SW, SE;
		
		public GreyNode(int xmin, int xmax, int ymin, int ymax) {
			super.setXmin(xmin);
			super.setXmax(xmax);
			super.setYmin(ymin);
			super.setYmax(ymax);
			super.setRegion(xmin, ymax, (xmax-xmin), (ymax-ymin));
			NW = new WhiteNode(xmin,(xmax+xmin)/2,(ymax+ymin)/2,ymax);
			NE = new WhiteNode(((xmax+xmin)/2),xmax,((ymax+ymin)/2),ymax);
			SW = new WhiteNode(xmin,(xmax+xmin)/2,ymin,(ymax+ymin)/2);
			SE = new WhiteNode((xmax+xmin)/2,xmax,ymin,(ymax+ymin)/2);
		}
		
		private Node add(Geometry g) {
			if (g.isCity()) {
				City c = (City) g;
				if (Inclusive2DIntersectionVerifier.intersects(c.getPt(), NW.getRegion())) {
					NW = insertHelp(c, NW);
				}
				if (Inclusive2DIntersectionVerifier.intersects(c.getPt(), NE.getRegion())) {
					NE = insertHelp(c, NE);
				}
				if (Inclusive2DIntersectionVerifier.intersects(c.getPt(), SW.getRegion())) {
					SW = insertHelp(c, SW);
				}
				if (Inclusive2DIntersectionVerifier.intersects(c.getPt(), SE.getRegion())) {
					SE = insertHelp(c, SE);
				}
			} else {
				Road r = (Road) g;
				if (Inclusive2DIntersectionVerifier.intersects(r.getLine(), NW.getRegion())) {
					NW = insertHelp(r, NW);
				}
				if (Inclusive2DIntersectionVerifier.intersects(r.getLine(), NE.getRegion())) {
					NE = insertHelp(r, NE);
				}
				if (Inclusive2DIntersectionVerifier.intersects(r.getLine(), SW.getRegion())) {
					SW = insertHelp(r, SW);
				}
				if (Inclusive2DIntersectionVerifier.intersects(r.getLine(), SE.getRegion())) {
					SE = insertHelp(r, SE);
				}
			}
			
			return this;
		}
		
		private Node remove(Geometry g, Node root) {
			throw new UnsupportedOperationException("Not yet implemented");
		}
		
		private BlackNode containsGeom(Point2D pt) {
			if (Inclusive2DIntersectionVerifier.intersects(pt, NW.getRegion())) {
				if (NW.getClass().equals(BlackNode.class)) {
					return (BlackNode) NW;
				} else if (NW.getClass().equals(GreyNode.class)) {
					return ((GreyNode) NW).containsGeom(pt);
				}
			}
			if (Inclusive2DIntersectionVerifier.intersects(pt, NE.getRegion())) {
				if (NE.getClass().equals(BlackNode.class)) {
					return (BlackNode) NE;
				} else if (NE.getClass().equals(GreyNode.class)) {
					return ((GreyNode) NE).containsGeom(pt);
				}
			}
			if (Inclusive2DIntersectionVerifier.intersects(pt, SW.getRegion())) {
				if (SW.getClass().equals(BlackNode.class)) {
					return (BlackNode) SW;
				} else if (SW.getClass().equals(GreyNode.class)) {
					return ((GreyNode) SW).containsGeom(pt);
				}
			}
			if (Inclusive2DIntersectionVerifier.intersects(pt, SE.getRegion())) {
				if (SE.getClass().equals(BlackNode.class)) {
					return (BlackNode) SE;
				} else if (SE.getClass().equals(GreyNode.class)) {
					return ((GreyNode) SE).containsGeom(pt);
				}
			}
			
			return null;
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
			super.setRegion(xmin, ymax, (xmax-xmin), (ymax-ymin));
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
			return ((BlackNode) root).add(elem);
		} else if (root.getClass().equals(GreyNode.class)) {
			return ((GreyNode) root).add(elem);
		} else {
			return ((WhiteNode)root).add(elem);
		}
	}
	
	public boolean containsGeom(Geometry g) {
		if (root.getClass().equals(BlackNode.class)) {
			return ((BlackNode) root).containsGeom(g);
		} else if (root.getClass().equals(GreyNode.class)) {
			Point2D.Float coord;
			if (g.getType() == Geometry.POINT) {
				coord = ((City) g).getPt();
			} else {
				coord = ((Road) g).getStart().getPt();
			}
			
			BlackNode b = ((GreyNode) root).containsGeom(coord);
			return b.containsGeom(g);
			
		} else {
			return false;
		}
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
