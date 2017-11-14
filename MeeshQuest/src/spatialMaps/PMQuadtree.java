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
		
		private Node add(Geometry g) {
			if (g.getType() == Geometry.POINT && containsCity()) {
				return partition(g);
			} else {
				items.add(g);
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
			return ((BlackNode) root).add(elem, root);
		} else if (root.getClass().equals(GreyNode.class)) {
			return ((GreyNode) root).add(elem, root);
		} else {
			return ((WhiteNode)root).add(elem);
		}
	}
}
