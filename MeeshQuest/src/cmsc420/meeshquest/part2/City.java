package cmsc420.meeshquest.part2;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class City extends Geometry implements Comparable<City> {

	private String color, name;
	private int x, y, radius, hops;
	private Point2D.Float pt;
	private ArrayList<Road> roads = new ArrayList<Road>();
	private City prev = null;
	private double minDist = Double.MAX_VALUE;
	
	public City(String name, int x, int y, int radius, String color) {
		pt = new Point2D.Float(x, y);
		this.name = name;
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
	
	public Point2D.Float getPt() {
		return pt;
	}
	
	public String getName() {
		return name;
	}
	
	public City getPrev() {
		return prev;
	}
	
	public double getMinDist() {
		return minDist;
	}
	
	public ArrayList<Road> getRoads() {
		return roads;
	}
	
	public Road getRoadTo(City dest) {
		Road road;
		if (dest.getName().compareTo(name) > 0) {
			road = new Road(this, dest);
		} else {
			road = new Road(dest, this);
		}
		
		for (Road r : roads) {
			if (r.equals(road)) {
				return r;
			}
		}
		
		return null;
	}
	
	public int getHops() {
		return hops;
	}
	
	public void addRoad(Road r) {
		roads.add(r);
	}
	
	public void setPrev(City c) {
		prev = c;
	}
	
	public void setMinDist(double dist) {
		minDist = dist;
	}
	
	public void setHops(int x) {
		hops = x;
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
		return "City: name=" + name + " x=" + x + " y=" + y + " radius=" + radius + " color=" + color; 
	}

	@Override
	public int getType() {
		return POINT;
	}

	@Override
	public int compareTo(City o) {
		return o.name.compareTo(name);
	}
}
