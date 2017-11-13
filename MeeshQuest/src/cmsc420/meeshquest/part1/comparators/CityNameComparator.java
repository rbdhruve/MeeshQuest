package cmsc420.meeshquest.part1.comparators;

import java.util.Comparator;

public class CityNameComparator implements Comparator<String> {

	@Override
	public int compare(String name1, String name2) {
		return name2.compareTo(name1);
	}

}
