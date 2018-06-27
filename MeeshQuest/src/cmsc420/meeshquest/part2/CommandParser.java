package cmsc420.meeshquest.part2;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.geometry.oldComparators.CityNameComparator;
import cmsc420.geometry.oldComparators.GeometryComparator;
import dictionaries.CoordDataDictionary;
import dictionaries.NameDataDictionary;
import spatialMaps.Node;
import spatialMaps.PMQuadtree;
import spatialMaps.PMQuadtree.BlackNode;
import spatialMaps.PMQuadtree.GreyNode;
import spatialMaps.PMQuadtree.WhiteNode;

public class CommandParser {
	
	public Pair<String, City> parseCreateCity(Element elem) {		
		String name = elem.getAttribute("name");
		int x = Integer.parseInt(elem.getAttribute("x"));
		int y = Integer.parseInt(elem.getAttribute("y"));
		int radius = Integer.parseInt(elem.getAttribute("radius"));
		String color = elem.getAttribute("color");
		City city = new City(name, x,y,radius,color);

		return new Pair<String,City> (name, city);
	}
	
	public Element listCities(Element elem, NameDataDictionary ndd, CoordDataDictionary cdd, Document results) {
		String sortBy = elem.getAttribute("sortBy");
		if (ndd.size() > 0) {
			if (sortBy.equals("name")) {
				Element success = results.createElement("success");
				
				Element command = results.createElement("command");
				command.setAttribute("name", "listCities");
				String id = elem.getAttribute("id");
				if (!id.isEmpty()) {
					command.setAttribute("id", id);
				}
				
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
				String id = elem.getAttribute("id");
				if (!id.isEmpty()) {
					command.setAttribute("id", id);
				}
				
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
			String id = elem.getAttribute("id");
			if (!id.isEmpty()) {
				command.setAttribute("id", id);
			}
			
			Element parameters = results.createElement("parameters");
			
			Element sort = results.createElement("sortBy");
			sort.setAttribute("value", sortBy);
			parameters.appendChild(sort);
			
			error.appendChild(command);
			error.appendChild(parameters);
			
			return error;
		}
	}
	
	public Element printTree(ArrayList<Node> arr, ArrayList<City> isolated, Document results, CoordDataDictionary cdd) {
		Element quadtree = results.createElement("quadtree");
		quadtree.setAttribute("order", "3");
		return printTreeHelp(quadtree, arr, isolated, results, cdd);
	}
	
	private Element printTreeHelp(Element elem, ArrayList<Node> arr, ArrayList<City> isolated, Document results, CoordDataDictionary cdd) {
		ArrayList<Element> root = new ArrayList<Element>();
		root.add(elem);
		for (Node n : arr) {
			elem = root.get(root.size()-1);
			if (n == null) {
				root.remove(elem);
				root.get(root.size()-1).appendChild(elem);
			} else if (n.getClass().equals(BlackNode.class)) {
				ArrayList<Geometry> items = ((BlackNode) n).getItems();
				Element black = results.createElement("black");
				black.setAttribute("cardinality", ((Integer) items.size()).toString());
				items.sort(new GeometryComparator(cdd));
				for(Geometry g : items) {
					if (g.isCity()) {
						Element city;
						City c = (City) g;
						if (!isolated.contains(c)) {
							city = results.createElement("city");
						} else {
							city = results.createElement("isolatedCity");
						}
						city.setAttribute("name", cdd.getName(c));
						city.setAttribute("x", ((Integer) c.getXCoord()).toString());
						city.setAttribute("y", ((Integer) c.getYCoord()).toString());
						city.setAttribute("color", c.getColor());
						city.setAttribute("radius", ((Integer) c.getRadius()).toString());
						black.appendChild(city);
					} else {
						Road r = (Road) g;
						Element road = results.createElement("road");
						String start = cdd.getName(r.getStart());
						String end = cdd.getName(r.getEnd());
						if (end.compareTo(start) > 0) {
							road.setAttribute("start", start);
							road.setAttribute("end", end);
							black.appendChild(road);
						} else {
							road.setAttribute("start", end);
							road.setAttribute("end", start);
							black.appendChild(road);
						}
					}
				}
				
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
	
	public CanvasPlus saveMap(int spatialWidth, int spatialHeight, ArrayList<Node> nodes, CoordDataDictionary cdd) {
		CanvasPlus canvas = new CanvasPlus("MeeshQuest");
		canvas.setFrameSize(spatialWidth, spatialHeight);
		canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.WHITE, true);
		canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.BLACK, false);
		canvas.addLine(0, spatialHeight/2, spatialWidth, spatialHeight/2, Color.GRAY);
		canvas.addLine(spatialWidth/2, 0, spatialWidth/2, spatialHeight, Color.GRAY);
		
		for (Node n : nodes) {
			if (n != null) {
				if (n.getClass().equals(PMQuadtree.BlackNode.class)) {
					PMQuadtree.BlackNode node = (PMQuadtree.BlackNode) n;
					for (Geometry g : node.getItems()) {
						if (g.isCity()) {
							City c = (City) g;
							canvas.addPoint(cdd.getName(c), c.getXCoord(), c.getYCoord(), Color.BLACK);
						} else {
							Road r = (Road) g;
							canvas.addLine(r.getStart().getXCoord(), r.getStart().getYCoord(), 
									r.getEnd().getXCoord(), r.getEnd().getYCoord(), Color.BLACK);
						}
					}
				} else if (n.getClass().equals(PMQuadtree.GreyNode.class)) {
					PMQuadtree.GreyNode node = (PMQuadtree.GreyNode) n;
					canvas.addLine(node.getXmin(), (node.getYmax()+node.getYmin())/2, node.getXmax(), (node.getYmax()+node.getYmin())/2, Color.GRAY);
    				canvas.addLine((node.getXmax()+node.getXmin())/2, node.getYmin(), (node.getXmax()+node.getXmin())/2, node.getYmax(), Color.GRAY);
				}
			}
		}
		
		return canvas;
	}
	
	public CanvasPlus shortestPathMap(int spatialWidth, int spatialHeight, ArrayList<City> cities) {
		CanvasPlus canvas = new CanvasPlus("MeeshQuest");
		canvas.setFrameSize(spatialWidth, spatialHeight);
		canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.WHITE, true);
		canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.BLACK, false);
		int i = 0;
		canvas.addPoint(cities.get(i).getName(), cities.get(i).getXCoord(), cities.get(i).getYCoord(), Color.GREEN);
		canvas.addLine(cities.get(i).getXCoord(), cities.get(i).getYCoord(), cities.get(i+1).getXCoord(), cities.get(i+1).getYCoord(), Color.BLUE);
		for (i = 1; i < cities.size()-1; i++) {
			canvas.addPoint(cities.get(i).getName(), cities.get(i).getXCoord(), cities.get(i).getYCoord(), Color.BLUE);
			canvas.addLine(cities.get(i).getXCoord(), cities.get(i).getYCoord(), cities.get(i+1).getXCoord(), cities.get(i+1).getYCoord(), Color.BLUE);
		}
		canvas.addPoint(cities.get(i).getName(), cities.get(i).getXCoord(), cities.get(i).getYCoord(), Color.RED);
		
		return canvas;
	}
	
	public TreeMap<String,City> rangeCities(TreeMap<String,City> map, Set<City> cities, int x, int y, int radius, 
			PMQuadtree tree, CoordDataDictionary cdd) {
		Circle2D circle = new Circle2D.Float(x, y, radius);
		for (City c : cities) {
			if (tree.containsGeom(c)) {
				if (Inclusive2DIntersectionVerifier.intersects(c.getPt(), circle)) {
					map.put(cdd.getName(c), c);
				}
			}
		}
		
		return map;
	}
	
	public ArrayList<Road> rangeRoads(ArrayList<Road> arr, ArrayList<Road> roads, int x, int y, int radius, PMQuadtree tree) {
		if (radius == 0) {
			for (Road r : roads) {
				Point2D.Float pt = new Point2D.Float(x, y);
				if (Inclusive2DIntersectionVerifier.intersects(pt, r.getLine())) {
					arr.add(r);
				}
			}
		} else {
			Circle2D cir = new Circle2D.Float(x, y, radius);
			for (Road r : roads) {
				if(Inclusive2DIntersectionVerifier.intersects(r.getStartCoord(), cir) || 
						Inclusive2DIntersectionVerifier.intersects(r.getEndCoord(), cir)) {
					arr.add(r);
				} else {
					float ax = r.getStart().getXCoord() - x;
					float ay = r.getStart().getYCoord() - y;
					float bx = r.getEnd().getXCoord() - x;
					float by = r.getEnd().getYCoord() - y;
					double a = (Math.pow((bx - ax), 2) + Math.pow((by - ay), 2));
					double b = 2*(ax*(bx - ax) + ay*(by - ay));
					double c = (Math.pow(ax, 2) + Math.pow(ay, 2) - Math.pow(radius, 2));
					double dis = Math.pow(b, 2) - 4*a*c;
					if (dis > 0) {
						double sqrtDis = Math.sqrt(dis);
						double t1 = (-b + sqrtDis)/(2*a);
						double t2 = (-b - sqrtDis)/(2*a);
						if((0 < t1 && t1 < 1) || (0 < t2 && t2 < 1)) {
							arr.add(r);
						}
					}
				}
			}
		}
		
		return arr;
	}
	
	public City nearest(Set<String> cities, int x, int y, PMQuadtree tree, NameDataDictionary ndd) {
		int shortestDist = Integer.MAX_VALUE, xCoord, yCoord, dist;
		City closest = null, c;
		for (String str : cities) {
			c = ndd.getCity(str);
			if (tree.containsGeom(c) && !tree.isIsolated(c)) {
				xCoord = c.getXCoord();
				yCoord = c.getYCoord();
				dist = (int) Math.ceil(Math.sqrt((Math.pow(xCoord-x,2)) + (Math.pow(yCoord-y,2))));
				if (dist < shortestDist) {
					shortestDist = dist;
					closest = c;
				} else if (dist == shortestDist) {
					if (str.compareTo(closest.getName()) < 0)
					closest = c;
				}
			}
		}
		
		return closest;
	}

	public City nearestIsolated(Set<City> cities, int x, int y, PMQuadtree tree, CoordDataDictionary cdd) {
		int shortestDist = Integer.MAX_VALUE;
		City closest = null;
		for (City c : cities) {
			if (tree.containsGeom(c)) {
				int xCoord = c.getXCoord();
				int yCoord = c.getYCoord();
				int dist = (int) Math.ceil(Math.sqrt((Math.pow(xCoord-x,2)) + (Math.pow(yCoord-y,2))));
				if (dist < shortestDist && tree.isIsolated(c)) {
					shortestDist = dist;
					closest = c;
				} else if (dist == shortestDist) {
					if (c.getName().compareTo(closest.getName()) < 0) {
						closest = c;
					}
				}
			}
		}
		
		return closest;
	}
	
	public Road nearestRoad(ArrayList<Road> roads, int x, int y, PMQuadtree tree, CoordDataDictionary cdd) {
		double shortestDist = Integer.MAX_VALUE;
		double dist;
		double a1, a2, b1, b2, dotProd, norm, test, a, b, c;
		Road closest = null;
		CityNameComparator comp = new CityNameComparator();
		
		for(Road r : roads) {
			a1 = r.getEnd().getXCoord() - r.getStart().getXCoord();
			a2 = r.getEnd().getYCoord() - r.getStart().getYCoord();
			b1 = x - r.getStart().getXCoord();
			b2 = y - r.getStart().getYCoord();
			dotProd = (a1*b1)+(a2*b2);
			norm = Math.pow(a1, 2)+Math.pow(a2, 2);
			test = dotProd/norm;
			
			if (test >= 0 && test <= 1) {
				if (a1 == 0) {
					dist = Math.sqrt(Math.pow(x-r.getStart().getXCoord(), 2));
				} else {
					a = a2/a1;
					if (a < 0) {
						c = -(r.getEnd().getYCoord()-(a*r.getEnd().getXCoord()));
						a = -a;
						b = 1;
					} else {
						b = -1;
						c = r.getEnd().getYCoord()-(a*r.getEnd().getXCoord());
					}
					dist = (Math.abs((a*x)+(b*y)+c))/Math.sqrt(Math.pow(a, 2)+Math.pow(b, 2));
					if (dist < 0.000000001) {
						dist = 0;
					}
				}
			} else if (test < 0) {
				dist = Math.sqrt(Math.pow((x-r.getStart().getXCoord()),2) + Math.pow(y-r.getStart().getYCoord(), 2));
			} else {
				dist = Math.sqrt(Math.pow((x-r.getEnd().getXCoord()),2) + Math.pow(y-r.getEnd().getYCoord(), 2));
			}
			if (dist < shortestDist) {
				shortestDist = dist;
				closest = r;
			} else if (dist == shortestDist) {
				if (comp.compare(cdd.getName(r.getStart()), cdd.getName(closest.getStart())) < 0) {
					closest = r;
				} else if (comp.compare(cdd.getName(r.getStart()), cdd.getName(closest.getStart())) == 0) {
					if (comp.compare(cdd.getName(r.getEnd()), cdd.getName(closest.getEnd())) < 0) {
						closest = r;
					}
				}
			}
		}
		
		return closest;
	}
	
	public City nearestCityToRoad(ArrayList<City> cities, Road r, PMQuadtree tree, CoordDataDictionary cdd) {
		double shortestDist = Integer.MAX_VALUE;
		double dist;
		double a1, a2, b1, b2, dotProd, norm, test, a, b, c;
		int x, y;
		City closest = null;
		CityNameComparator comp = new CityNameComparator();
		
		for(City city : cities) {
			x = city.getXCoord();
			y = city.getYCoord();
			a1 = x - r.getStart().getXCoord();
			a2 = y - r.getStart().getYCoord();
			b1 = city.getXCoord() - r.getStart().getXCoord();
			b2 = city.getYCoord() - r.getStart().getYCoord();
			dotProd = (a1*b1)+(a2*b2);
			norm = Math.pow(a1, 2)+Math.pow(a2, 2);
			test = dotProd/norm;
			
			if (test >= 0 && test <= 1) {
				if (a1 == 0) {
					dist = Math.sqrt(Math.pow(x-r.getStart().getXCoord(), 2));
				} else {
					a = a2/a1;
					if (a < 0) {
						c = -(r.getEnd().getYCoord()-(a*r.getEnd().getXCoord()));
						a = -a;
						b = 1;
					} else {
						b = -1;
						c = r.getEnd().getYCoord()-(a*r.getEnd().getXCoord());
					}
					dist = (Math.abs((a*x)+(b*y)+c))/Math.sqrt(Math.pow(a, 2)+Math.pow(b, 2));
					if (dist < 0.000000001) {
						dist = 0;
					}
				}
			} else if (test < 0) {
				dist = Math.sqrt(Math.pow((x-r.getStart().getXCoord()),2) + Math.pow(y-r.getStart().getYCoord(), 2));
			} else {
				dist = Math.sqrt(Math.pow((x-r.getEnd().getXCoord()),2) + Math.pow(y-r.getEnd().getYCoord(), 2));
			}
			if (dist < shortestDist) {
				shortestDist = dist;
				closest = city;
			} else if (dist == shortestDist) {
				if (comp.compare(cdd.getName(city), cdd.getName(closest)) < 0) {
					closest = city;
				}
			}
		}
		
		return closest;
	}
	
	public void shortestPath(City source, CoordDataDictionary cdd) {
		for (City c : cdd.getCities()) {
			c.setPrev(null);
			c.setMinDist(Double.MAX_VALUE);
		}
		source.setMinDist(0);
		source.setHops(0);
		PriorityQueue<City> cities = new PriorityQueue<City>();
		cities.add(source);
		City c, v;
		Double dist, weight;
		
		while (!cities.isEmpty()) {
			c = cities.poll();
			
			for (Road r : c.getRoads()) {
				if (r.getStart().equals(c)) {
					v = r.getEnd();
				} else {
					v = r.getStart();
				}
				weight = r.getLength();
				dist = c.getMinDist() + weight;
				
				if (dist < v.getMinDist()) {
					cities.remove(v);
					v.setMinDist(dist);
					v.setPrev(c);
					v.setHops(v.getPrev().getHops()+1);
					cities.add(v);
				}
			}
		}
	}
	
	public ArrayList<City> pathTo(City dest) {
		ArrayList<City> path = new ArrayList<City>();
		City c = dest;
		while (c != null) {
			path.add(c);
			c = c.getPrev();
		}
		
		Collections.reverse(path);
		return path;
	}
	
	public void processPrintDodekaTrie(Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final Element outputNode = results.createElement("output");

        if (citiesByName.isEmpty()) {
            addErrorNode("emptyTree", commandNode, parametersNode);
        } else {
			citiesByName.addToXmlDoc(results, outputNode);
            addSuccessNode(commandNode, parametersNode, outputNode);
        }
    }
	
	private Element addSuccessNode(final Element command,
			final Element parameters, final Element output) {
		final Element success = results.createElement("success");
		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);
		resultsNode.appendChild(success);
		return success;
	}
	
	private void addErrorNode(final String type, final Element command,
			final Element parameters) {
		final Element error = results.createElement("error");
		error.setAttribute("type", type);
		error.appendChild(command);
		error.appendChild(parameters);
		resultsNode.appendChild(error);
	}

}
