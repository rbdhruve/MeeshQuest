package cmsc420.meeshquest.part2;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Road extends Geometry {

	private City start, end;
	private double length;
	private Line2D.Float line;
	
	public Road(City start, City end) {
		this.start = start;
		this.end = end;
		length = start.getPt().distance(end.getPt());
		line = new Line2D.Float(start.getPt(), end.getPt());
	}
	
	public City getStart() {
		return start;
	}
	
	public City getEnd() {
		return end;
	}
	
	public Point2D getStartCoord() {
		return start.getPt();
	}
	
	public Point2D.Float getEndCoord() {
		return end.getPt();
	}
	
	public Line2D.Float getLine() {
		return line;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Road)) {
			return false;
		}
		
		Road r = (Road) o;
		return r.start.equals(start) && r.end.equals(end);
	}
	
	@Override
	public int getType() {
		return SEGMENT;
	}

}
