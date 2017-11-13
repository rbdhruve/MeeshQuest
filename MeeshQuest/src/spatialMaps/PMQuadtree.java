package spatialMaps;

import java.awt.geom.Point2D;

public class PMQuadtree<T extends Point2D.Float> {
	
	private Node root;
	private int height, width;
	
	public class BlackNode extends Node {
		
	}
	
	public class GreyNode extends Node {
		
	}
	
	public class WhiteNode extends Node {
		
	}
}
