package spatialMaps;

public abstract class Node {
	private int xmin, xmax, ymin, ymax;

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
	
	
}
