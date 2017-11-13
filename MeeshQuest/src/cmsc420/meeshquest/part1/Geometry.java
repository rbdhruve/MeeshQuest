package cmsc420.meeshquest.part1;

import cmsc420.geom.Geometry2D;

public abstract class Geometry implements Geometry2D {
	public boolean isRoad() {
		return getType() == SEGMENT;
	}
	
	public boolean isCity() {
		return getType() == POINT;
	}
}
