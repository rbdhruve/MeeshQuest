package cmsc420.meeshquest.part2.comparators;

import java.util.Comparator;

import cmsc420.meeshquest.part2.City;

public class CityCoordComparator implements Comparator<City> {

	@Override
	public int compare(City c1, City c2) {
		if (c1.getYCoord() != c2.getYCoord()) {
			return c1.getYCoord() - c2.getYCoord();
		} else {
			return c1.getXCoord() - c2.getXCoord();
		}
	}
}
