package cmsc420.meeshquest.part1;

import java.awt.geom.Line2D;

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
	
	@Override
	public int getType() {
		return SEGMENT;
	}

}
