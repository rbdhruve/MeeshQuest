package cmsc420.meeshquest.part2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import cmsc420.meeshquest.part2.comparators.CityCoordComparator;
import cmsc420.meeshquest.part2.comparators.CityNameComparator;
import dictionaries.CoordDataDictionary;
import dictionaries.NameDataDictionary;
import spatialMaps.Node;
import spatialMaps.PRQuadTree;
import spatialMaps.PRQuadTree.BlackNode;
import spatialMaps.PRQuadTree.GreyNode;
import spatialMaps.PRQuadTree.WhiteNode;

public class CommandParser {
	
	public Pair<String, City> parseCreateCity(Element elem) {		
		String name = elem.getAttribute("name");
		int x = Integer.parseInt(elem.getAttribute("x"));
		int y = Integer.parseInt(elem.getAttribute("y"));
		int radius = Integer.parseInt(elem.getAttribute("radius"));
		String color = elem.getAttribute("color");
		City city = new City(x,y,radius,color);

		return new Pair<String,City> (name, city);
	}
	
	public Element listCities(Element elem, NameDataDictionary ndd, CoordDataDictionary cdd, Document results) {
		String sortBy = elem.getAttribute("sortBy");
		if (ndd.size() > 0) {
			if (sortBy.equals("name")) {
				Element success = results.createElement("success");
				
				Element command = results.createElement("command");
				command.setAttribute("name", "listCities");
				
				Element parameters = results.createElement("parameters");
				
				Element sort = results.createElement("sortBy");
				sort.setAttribute("value", sortBy);
				
				Element output = results.createElement("output");
				Element cityList = results.createElement("cityList");
				
				TreeMap <String, City> dictionary = ndd.getDictionary();
				for (Entry<String, City> entry : dictionary.entrySet()) {
					Element city = results.createElement("city");
					city.setAttribute("name", entry.getKey());
					city.setAttribute("x", ((Integer) entry.getValue().getXCoord()).toString());
					city.setAttribute("y", ((Integer) entry.getValue().getYCoord()).toString());
					city.setAttribute("radius", ((Integer) entry.getValue().getRadius()).toString());
					city.setAttribute("color", entry.getValue().getColor());
					cityList.appendChild(city);
				}
				
				output.appendChild(cityList);
				parameters.appendChild(sort);
				success.appendChild(command);
				success.appendChild(parameters);
				success.appendChild(output);
				
				return success;
			} else if (sortBy.equals("coordinate")) {
				Element success = results.createElement("success");
				
				Element command = results.createElement("command");
				command.setAttribute("name", "listCities");
				
				Element parameters = results.createElement("parameters");
				
				Element sort = results.createElement("sortBy");
				sort.setAttribute("value", sortBy);
				
				Element output = results.createElement("output");
				Element cityList = results.createElement("cityList");
				
				TreeMap<City, String> dictionary = cdd.getDictionary();
				for (Entry<City, String> entry : dictionary.entrySet()) {
					Element city = results.createElement("city");
					city.setAttribute("name", entry.getValue());
					city.setAttribute("x", ((Integer) entry.getKey().getXCoord()).toString());
					city.setAttribute("y", ((Integer) entry.getKey().getYCoord()).toString());
					city.setAttribute("radius", ((Integer) entry.getKey().getRadius()).toString());
					city.setAttribute("color", entry.getKey().getColor());
					cityList.appendChild(city);
				}
				
				output.appendChild(cityList);
				parameters.appendChild(sort);
				success.appendChild(command);
				success.appendChild(parameters);
				success.appendChild(output);
				
				return success;
			} else
				return null;
		} else {
			Element error = results.createElement("error");
			error.setAttribute("type", "noCitiesToList");
			
			Element command = results.createElement("command");
			command.setAttribute("name", "listCities");
			
			Element parameters = results.createElement("parameters");
			
			Element sort = results.createElement("sortBy");
			sort.setAttribute("value", sortBy);
			parameters.appendChild(sort);
			
			error.appendChild(command);
			error.appendChild(parameters);
			
			return error;
		}
	}
	
	public Element printTree(ArrayList<Node> arr, Document results, CoordDataDictionary cdd) {
		Element quadtree = results.createElement("quadtree");
		return printTreeHelp(quadtree, arr, results, cdd);
	}
	
	@SuppressWarnings("unchecked")
	private Element printTreeHelp(Element elem, ArrayList<Node> arr, Document results, CoordDataDictionary cdd) {
		ArrayList<Element> root = new ArrayList<Element>();
		root.add(elem);
		for (Node n : arr) {
			elem = root.get(root.size()-1);
			if (n == null) {
				root.remove(elem);
				root.get(root.size()-1).appendChild(elem);
			} else if (n.getClass().equals(BlackNode.class)) {
				City c = (City) ((PRQuadTree<City>.BlackNode) n).getData();
				Element black = results.createElement("black");
				black.setAttribute("name", cdd.getName(c));
				black.setAttribute("x", ((Integer) c.getXCoord()).toString());
				black.setAttribute("y", ((Integer) c.getYCoord()).toString());
				elem.appendChild(black);
			} else if (n.getClass().equals(WhiteNode.class)) {
				Element white = results.createElement("white");
				elem.appendChild(white);
			} else if (n.getClass().equals(GreyNode.class)) {
				Element e = results.createElement("gray");
				Integer x = ((Integer) (n.getXmax()+n.getXmin())/2);
				Integer y = ((Integer) (n.getYmax()+n.getYmin())/2);
				e.setAttribute("x", x.toString());
				e.setAttribute("y", y.toString());
				elem.appendChild(e);
				root.add(e);
			}
		}
		
		return root.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public CanvasPlus saveMap(int spatialWidth, int spatialHeight, ArrayList<Node> nodes, CoordDataDictionary cdd) {
		CanvasPlus canvas = new CanvasPlus("MeeshQuest");
		canvas.setFrameSize(spatialWidth, spatialHeight);
		canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.WHITE, true);
		canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.BLACK, false);
		canvas.addLine(0, spatialHeight/2, spatialWidth, spatialHeight/2, Color.BLACK);
		canvas.addLine(spatialWidth/2, 0, spatialWidth/2, spatialHeight, Color.BLACK);
		
		for (Node n : nodes) {
			if (n != null) {
				if (n.getClass().equals(PRQuadTree.BlackNode.class)) {
					PRQuadTree<City>.BlackNode node = (PRQuadTree<City>.BlackNode) n;
					City c = (City) node.getData();
					canvas.addPoint(cdd.getName(c), c.getXCoord(), c.getYCoord(), Color.BLACK);
				} else if (n.getClass().equals(PRQuadTree.GreyNode.class)) {
					PRQuadTree<City>.GreyNode node = (PRQuadTree<City>.GreyNode) n;
					canvas.addLine(node.getXmin(), (node.getYmax()+node.getYmin())/2, node.getXmax(), (node.getYmax()+node.getYmin())/2, Color.BLACK);
    				canvas.addLine((node.getXmax()+node.getXmin())/2, node.getYmin(), (node.getXmax()+node.getXmin())/2, node.getYmax(), Color.BLACK);
				}
			}
		}
		
		return canvas;
	}
	
	public ArrayList<City> range(ArrayList<City> arr, Set<City> cities, int x, int y, int radius, PRQuadTree<City> tree) {
		for (City c : cities) {
			if (tree.containsElem(c)) {
				int xCoord = c.getXCoord();
				int yCoord = c.getYCoord();
				if (Math.ceil(Math.sqrt((Math.pow(xCoord-x,2)) + (Math.pow(yCoord-y,2)))) <= radius) {
					arr.add(c);
				}
			}
		}
		
		return arr;
	}
	
	public City nearest(Set<City> cities, int x, int y, PRQuadTree<City> tree, CoordDataDictionary cdd) {
		int shortestDist = Integer.MAX_VALUE;
		City closest = null;
		CityNameComparator nameComp = new CityNameComparator();
		for (City c : cities) {
			if (tree.containsElem(c)) {
				int xCoord = c.getXCoord();
				int yCoord = c.getYCoord();
				int dist = (int) Math.ceil(Math.sqrt((Math.pow(xCoord-x,2)) + (Math.pow(yCoord-y,2))));
				if (dist < shortestDist) {
					shortestDist = dist;
					closest = c;
				} else if (dist == shortestDist) {
					if (nameComp.compare(cdd.getName(c), cdd.getName(closest)) < 0) {
						closest = c;
					}
				}
			}
		}
		
		return closest;
	}

}
