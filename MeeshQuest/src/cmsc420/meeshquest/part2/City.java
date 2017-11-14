package cmsc420.meeshquest.part2;

import java.awt.Point;
import java.awt.geom.Point2D;

public class City extends Geometry {

	private String color;
	private int x, y, radius;
	private Point2D pt;
	
	public City(int x, int y, int radius, String color) {
		pt = new Point(x, y);
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public int getXCoord() {
		return x;
	}

	public int getYCoord() {
		return y;
	}

	public int getRadius() {
		return radius;
	}
	
	public Point2D getPt() {
		return pt;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof City)) {
			return false;
		}
		
		City c = (City) o;
		return c.getColor().equals(color) && c.getRadius() == radius && c.getXCoord() == x && c.getYCoord() == y;
	}
	
	@Override
	public String toString() {
		return "City: x=" + x + " y=" + y + " radius=" + radius + " color=" + color; 
	}

	@Override
	public int getType() {
		return POINT;
	}
}
