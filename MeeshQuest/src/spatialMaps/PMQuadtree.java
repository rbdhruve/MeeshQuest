package spatialMaps;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import cmsc420.geom.*;
import spatialMaps.PRQuadTree.BlackNode;
import spatialMaps.PRQuadTree.GreyNode;
import spatialMaps.PRQuadTree.WhiteNode;

public class PMQuadtree<T extends Point2D.Float> {
	
	private Node root;
	private int height, width;
	
	public class BlackNode extends Node {
		ArrayList<Geometry2D> items;
		
		public BlackNode (int xmin, int xmax, int ymin, int ymax) {
			super.setXmin(xmin);
			super.setXmax(xmax);
			super.setYmin(ymin);
			super.setYmax(ymax);
		}
		
		private Node add(Geometry2D g, Node root) {
			
		}
		
		private Node remove(Geometry2D g, Node root) {
			
		}
		
		private Node partition(Geometry2D g, Node root) {
			
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
		
		private Node add(Geometry2D g, Node root) {
			
		}
		
		private Node remove(Geometry2D g, Node root) {
			
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
}
