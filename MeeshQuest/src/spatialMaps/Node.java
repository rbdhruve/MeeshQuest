package spatialMaps;

import java.awt.geom.Rectangle2D;

public abstract class Node {
	private int xmin, xmax, ymin, ymax;
	private Rectangle2D.Float region;

	public int getXmin() {
		return xmin;
	}

	public int getXmax() {
		return xmax;
	}

	public int getYmin() {
		return ymin;
	}

	public int getYmax() {
		return ymax;
	}
	
	public Rectangle2D.Float getRegion() {
		return region;
	}

	public void setXmin(int xmin) {
		this.xmin = xmin;
	}

	public void setXmax(int xmax) {
		this.xmax = xmax;
	}

	public void setYmin(int ymin) {
		this.ymin = ymin;
	}

	public void setYmax(int ymax) {
		this.ymax = ymax;
	}
	
	public void setRegion(float x, float y, float w, float h) {
		region = new Rectangle2D.Float(x, y, w, h);
	}
}
