package dictionaries;

import java.util.Set;
import java.util.TreeMap;

import cmsc420.meeshquest.part1.City;
import cmsc420.meeshquest.part1.comparators.CityNameComparator;

public class NameDataDictionary {

	private CityNameComparator nc =  new CityNameComparator();
	private TreeMap<String, City> dataDictionaryNames = new TreeMap<String, City>(nc);
	
	public void put(String s, City c) {
		dataDictionaryNames.put(s, c);
	}
	
	public void remove(String name) {
		dataDictionaryNames.remove(name);
	}
	
	public boolean containsName(String name) {
		return dataDictionaryNames.containsKey(name);
	}
	
	public int size() {
		return dataDictionaryNames.size();
	}
	
	public void clear() {
		dataDictionaryNames.clear();
	}
	
	public TreeMap<String, City> getDictionary() {
		return dataDictionaryNames;
	}
	
	public City getCity(String name) {
		return dataDictionaryNames.get(name);
	}
	
	public Set<String> getNames() {
		return dataDictionaryNames.keySet();
	}
}
