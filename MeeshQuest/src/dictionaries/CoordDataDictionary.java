package dictionaries;

import java.util.Set;
import java.util.TreeMap;

import cmsc420.meeshquest.part1.City;
import cmsc420.meeshquest.part1.comparators.CityCoordComparator;

public class CoordDataDictionary {

	private CityCoordComparator cc = new CityCoordComparator();
	private TreeMap<City, String> dataDictionaryCoords = new TreeMap<City, String>(cc);
	
	public void put(City c, String s) {
		dataDictionaryCoords.put(c, s);
	}
	
	public void remove(City c) {
		dataDictionaryCoords.remove(c);
	}
	
	public boolean containsCoords(City c) {
		return dataDictionaryCoords.get(c) != null;
	}
	
	public int size() {
		return dataDictionaryCoords.size();
	}
	
	public void clear() {
		dataDictionaryCoords.clear();
	}
	
	public TreeMap<City, String> getDictionary() {
		return dataDictionaryCoords;
	}
	
	public String getName(City c) {
		return dataDictionaryCoords.get(c);
	}
	
	public Set<City> getCities() {
		return dataDictionaryCoords.keySet();
	}	
}
