package cmsc420.meeshquest.part2;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.geometry.oldComparators.CityNameComparator;
import cmsc420.geometry.oldComparators.RoadComparator;
import cmsc420.xml.XmlUtility;
import dictionaries.CoordDataDictionary;
import dictionaries.NameDataDictionary;
import spatialMaps.Node;
import spatialMaps.PMQuadtree;

public class MeeshQuest {

    public static void main(String[] args) {
    	
    	Document results = null;
    	CommandParser parser = new CommandParser();
    	NameDataDictionary ndd = new NameDataDictionary();
    	CoordDataDictionary cdd = new CoordDataDictionary();
    	ArrayList<Road> roads = new ArrayList<Road>();
    	ArrayList<City> isolated = new ArrayList<City>();
    	ArrayList<City> cities =  new ArrayList<City>();
    	PMQuadtree tree;
    	
        try {
        	Document doc = XmlUtility.validateNoNamespace(new File("tests/cmsc420/inputs/tests.input.xml"));
        	results = XmlUtility.getDocumentBuilder().newDocument();
        	Element r = results.createElement("results");
        	results.appendChild(r);
        
        	Element commandNode = doc.getDocumentElement();
        	int spatialWidth = Integer.parseInt(commandNode.getAttribute("spatialWidth")); 
        	int spatialHeight = Integer.parseInt(commandNode.getAttribute("spatialHeight"));
        	Rectangle2D.Float rect = new Rectangle2D.Float(0, 0, spatialWidth, spatialHeight);
        	tree = new PMQuadtree(spatialWidth, spatialHeight);

        	final NodeList nl = commandNode.getChildNodes();
        	for (int i = 0; i < nl.getLength(); i++) {
        		if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        			commandNode = (Element) nl.item(i);
        			
        			if (commandNode.getNodeName().equals("createCity")) {
        				Pair<String,City> p = parser.parseCreateCity(commandNode);
        				
        				if (!cdd.containsCoords(p.U())) {
        					if (!ndd.containsName(p.T())) {
                				ndd.put(p.T(), p.U());
                				cdd.put(p.U(), p.T());
                				
                				Element success = results.createElement("success");
        						
        						Element command = results.createElement("command");
        						command.setAttribute("name", "createCity");
        						String id = commandNode.getAttribute("id");
        						if (!id.isEmpty()) {
        							command.setAttribute("id", id);
        						}
        						
        						Element parameters = results.createElement("parameters");
        						
        						Element paramName = results.createElement("name");
        						paramName.setAttribute("value", p.T());
        						Element paramX = results.createElement("x");
        						paramX.setAttribute("value", ((Integer) p.U().getXCoord()).toString());
        						Element paramY = results.createElement("y");
        						paramY.setAttribute("value", ((Integer) p.U().getYCoord()).toString());
        						Element paramRadius = results.createElement("radius");
        						paramRadius.setAttribute("value", ((Integer) p.U().getRadius()).toString());
        						Element paramColor = results.createElement("color");
        						paramColor.setAttribute("value", p.U().getColor());
        						
        						parameters.appendChild(paramName);
        						parameters.appendChild(paramX);
        						parameters.appendChild(paramY);
        						parameters.appendChild(paramRadius);
        						parameters.appendChild(paramColor);
        						
        						Element output = results.createElement("output");
        						
        						success.appendChild(command);
        						success.appendChild(parameters);
        						success.appendChild(output);
        						
        						r.appendChild(success);
        						
        					} else {
        						Element error = results.createElement("error");
        						error.setAttribute("type", "duplicateCityName");
        						
        						Element command = results.createElement("command");
        						command.setAttribute("name", "createCity");
        						String id = commandNode.getAttribute("id");
        						if (!id.isEmpty()) {
        							command.setAttribute("id", id);
        						}
        						
        						Element parameters = results.createElement("parameters");
        						
        						Element paramName = results.createElement("name");
        						paramName.setAttribute("value", p.T());
        						Element paramX = results.createElement("x");
        						paramX.setAttribute("value", ((Integer) p.U().getXCoord()).toString());
        						Element paramY = results.createElement("y");
        						paramY.setAttribute("value", ((Integer) p.U().getYCoord()).toString());
        						Element paramRadius = results.createElement("radius");
        						paramRadius.setAttribute("value", ((Integer) p.U().getRadius()).toString());
        						Element paramColor = results.createElement("color");
        						paramColor.setAttribute("value", p.U().getColor());
        						
        						parameters.appendChild(paramName);
        						parameters.appendChild(paramX);
        						parameters.appendChild(paramY);
        						parameters.appendChild(paramRadius);
        						parameters.appendChild(paramColor);
        						
        						error.appendChild(command);
        						error.appendChild(parameters);
        						
        						r.appendChild(error);
        						
        					}
        				} else {
        					
        					Element error = results.createElement("error");
    						error.setAttribute("type", "duplicateCityCoordinates");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "createCity");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						
    						Element paramName = results.createElement("name");
    						paramName.setAttribute("value", p.T());
    						Element paramX = results.createElement("x");
    						paramX.setAttribute("value", ((Integer) p.U().getXCoord()).toString());
    						Element paramY = results.createElement("y");
    						paramY.setAttribute("value", ((Integer) p.U().getYCoord()).toString());
    						Element paramRadius = results.createElement("radius");
    						paramRadius.setAttribute("value", ((Integer) p.U().getRadius()).toString());
    						Element paramColor = results.createElement("color");
    						paramColor.setAttribute("value", p.U().getColor());
    						
    						parameters.appendChild(paramName);
    						parameters.appendChild(paramX);
    						parameters.appendChild(paramY);
    						parameters.appendChild(paramRadius);
    						parameters.appendChild(paramColor);
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        					
        				}
        				
//        			} else if (commandNode.getNodeName().equals("deleteCity")) {
//        				String name = commandNode.getAttribute("name");
//        				
//        				if (ndd.containsName(name)) {
//        					City city = ndd.getCity(name);
//        					
//        					Element success = results.createElement("success");
//    						
//    						Element command = results.createElement("command");
//    						command.setAttribute("name", "deleteCity");
//    						
//    						Element parameters = results.createElement("parameters");
//    						
//    						Element paramName = results.createElement("name");
//    						paramName.setAttribute("value", name);
//    						
//    						Element output = results.createElement("output");
//    						if (tree.containsElem(city)) {
//        						tree.delete(city);
//        						Element unmapped = results.createElement("cityUnmapped");
//        						unmapped.setAttribute("name", cdd.getName(city));
//        						unmapped.setAttribute("x", ((Integer) city.getXCoord()).toString());
//        						unmapped.setAttribute("y", ((Integer) city.getYCoord()).toString());
//        						unmapped.setAttribute("color", city.getColor());
//        						unmapped.setAttribute("radius", ((Integer) city.getRadius()).toString());
//        						output.appendChild(unmapped);
//        					}
//        					ndd.remove(name);
//        					cdd.remove(city);
//    						
//    						parameters.appendChild(paramName);
//
//    						success.appendChild(command);
//    						success.appendChild(parameters);
//    						success.appendChild(output);
//    						
//    						r.appendChild(success);
//        				} else {
//        					Element error = results.createElement("error");
//    						error.setAttribute("type", "cityDoesNotExist");
//    						
//    						Element command = results.createElement("command");
//    						command.setAttribute("name", "deleteCity");
//    						
//    						Element parameters = results.createElement("parameters");
//    						
//    						Element paramName = results.createElement("name");
//    						paramName.setAttribute("value", name);
//    						
//    						parameters.appendChild(paramName);
//    						error.appendChild(command);
//    						error.appendChild(parameters);
//    						
//    						r.appendChild(error);
//        				}
        				
        			} else if (commandNode.getNodeName().equals("listCities")) {
        				Element cityList = parser.listCities(commandNode, ndd, cdd, results);
        				r.appendChild(cityList);
        				
        			} else if (commandNode.getNodeName().equals("clearAll")) {
        				ndd.clear();
        				cdd.clear();
        				tree.clearAll();
        				isolated.clear();
        				roads.clear();
        				cities.clear();
        				Element success = results.createElement("success");
        				Element command = results.createElement("command");
        				command.setAttribute("name", "clearAll");
        				String id = commandNode.getAttribute("id");
						if (!id.isEmpty()) {
							command.setAttribute("id", id);
						}
        				Element parameters = results.createElement("parameters");
        				Element output = results.createElement("output");
        				
        				success.appendChild(command);
        				success.appendChild(parameters);
        				success.appendChild(output);
        				
        				r.appendChild(success);
        			} else if (commandNode.getLocalName().equals("mapRoad")) {
        				String start = commandNode.getAttribute("start");
        				String end = commandNode.getAttribute("end");
        				City startC = ndd.getCity(start);
        				City endC = ndd.getCity(end);
        				Road road;
        				if (ndd.containsName(start)) {
        					if (ndd.containsName(end)) {
                				if (cdd.getName(endC).compareTo(cdd.getName(startC)) > 0) {
                					road = new Road(startC, endC);
                				} else {
                					road = new Road(endC, startC);
                				}
        						if (!start.equals(end)) {
        							if (!tree.isIsolated(startC) && !tree.isIsolated(endC)) {
        								if (!tree.containsGeom(road)) {
        									if(Inclusive2DIntersectionVerifier.intersects(road.getLine(), rect)) {
        										
        										if(!tree.containsGeom(startC) && Inclusive2DIntersectionVerifier.intersects(startC.getPt(), rect)) {
        											tree.insert(startC);
        											cities.add(startC);
        										}
        										if(!tree.containsGeom(endC) && Inclusive2DIntersectionVerifier.intersects(endC.getPt(), rect)) {
        											tree.insert(endC);
        											cities.add(endC);
        										}
            									tree.insert(road);
            									startC.addRoad(road);
            									endC.addRoad(road);
            									roads.add(road);
            									Element success = results.createElement("success");
                        						
                        						Element command = results.createElement("command");
                        						command.setAttribute("name", "mapRoad");
                        						String id = commandNode.getAttribute("id");
                        						if (!id.isEmpty()) {
                        							command.setAttribute("id", id);
                        						}
                        						Element parameters = results.createElement("parameters");
                        						
                        						Element paramStart = results.createElement("start");
                        						Element paramEnd = results.createElement("end");
                        						paramStart.setAttribute("value", start);
                        						paramEnd.setAttribute("value", end);
                        						parameters.appendChild(paramStart);
                        						parameters.appendChild(paramEnd);
                        						
                        						Element output = results.createElement("output");
                        						
                        						Element created = results.createElement("roadCreated");
                        						created.setAttribute("start", start);
                        						created.setAttribute("end", end);
                        						output.appendChild(created);
                        						
                        						success.appendChild(command);
                        						success.appendChild(parameters);
                        						success.appendChild(output);
                        						
                        						r.appendChild(success);
                        						
        									} else {
        										Element error = results.createElement("error");
                        						error.setAttribute("type", "roadOutOfBounds");
                        						
                        						Element command = results.createElement("command");
                        						command.setAttribute("name", "mapRoad");
                        						String id = commandNode.getAttribute("id");
                        						if (!id.isEmpty()) {
                        							command.setAttribute("id", id);
                        						}
                        						
                        						Element parameters = results.createElement("parameters");
                        						
                        						Element paramStart = results.createElement("start");
                        						Element paramEnd = results.createElement("end");
                        						paramStart.setAttribute("value", start);
                        						paramEnd.setAttribute("value", end);
                        						parameters.appendChild(paramStart);
                        						parameters.appendChild(paramEnd);
                        						
                        						error.appendChild(command);
                        						error.appendChild(parameters);
                        						
                        						r.appendChild(error);
        									}
        								} else {
        									Element error = results.createElement("error");
                    						error.setAttribute("type", "roadAlreadyMapped");
                    						
                    						Element command = results.createElement("command");
                    						command.setAttribute("name", "mapRoad");
                    						String id = commandNode.getAttribute("id");
                    						if (!id.isEmpty()) {
                    							command.setAttribute("id", id);
                    						}
                    						
                    						Element parameters = results.createElement("parameters");
                    						
                    						Element paramStart = results.createElement("start");
                    						Element paramEnd = results.createElement("end");
                    						paramStart.setAttribute("value", start);
                    						paramEnd.setAttribute("value", end);
                    						parameters.appendChild(paramStart);
                    						parameters.appendChild(paramEnd);
                    						
                    						error.appendChild(command);
                    						error.appendChild(parameters);
                    						
                    						r.appendChild(error);
        								}
        							} else {
        								Element error = results.createElement("error");
                						error.setAttribute("type", "startOrEndIsIsolated");
                						
                						Element command = results.createElement("command");
                						command.setAttribute("name", "mapRoad");
                						String id = commandNode.getAttribute("id");
                						if (!id.isEmpty()) {
                							command.setAttribute("id", id);
                						}
                						
                						Element parameters = results.createElement("parameters");
                						
                						Element paramStart = results.createElement("start");
                						Element paramEnd = results.createElement("end");
                						paramStart.setAttribute("value", start);
                						paramEnd.setAttribute("value", end);
                						parameters.appendChild(paramStart);
                						parameters.appendChild(paramEnd);
                						
                						error.appendChild(command);
                						error.appendChild(parameters);
                						
                						r.appendChild(error);
        							}
        						} else {
        							Element error = results.createElement("error");
            						error.setAttribute("type", "startEqualsEnd");
            						
            						Element command = results.createElement("command");
            						command.setAttribute("name", "mapRoad");
            						String id = commandNode.getAttribute("id");
            						if (!id.isEmpty()) {
            							command.setAttribute("id", id);
            						}
            						
            						Element parameters = results.createElement("parameters");
            						
            						Element paramStart = results.createElement("start");
            						Element paramEnd = results.createElement("end");
            						paramStart.setAttribute("value", start);
            						paramEnd.setAttribute("value", end);
            						parameters.appendChild(paramStart);
            						parameters.appendChild(paramEnd);
            						
            						error.appendChild(command);
            						error.appendChild(parameters);
            						
            						r.appendChild(error);
        						}
        					} else {
        						Element error = results.createElement("error");
        						error.setAttribute("type", "endPointDoesNotExist");
        						
        						Element command = results.createElement("command");
        						command.setAttribute("name", "mapRoad");
        						String id = commandNode.getAttribute("id");
        						if (!id.isEmpty()) {
        							command.setAttribute("id", id);
        						}
        						
        						Element parameters = results.createElement("parameters");
        						
        						Element paramStart = results.createElement("start");
        						Element paramEnd = results.createElement("end");
        						paramStart.setAttribute("value", start);
        						paramEnd.setAttribute("value", end);
        						parameters.appendChild(paramStart);
        						parameters.appendChild(paramEnd);
        						
        						error.appendChild(command);
        						error.appendChild(parameters);
        						
        						r.appendChild(error);
        					}
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "startPointDoesNotExist");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "mapRoad");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						
    						Element paramStart = results.createElement("start");
    						Element paramEnd = results.createElement("end");
    						paramStart.setAttribute("value", start);
    						paramEnd.setAttribute("value", end);
    						parameters.appendChild(paramStart);
    						parameters.appendChild(paramEnd);
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
        			} else if (commandNode.getNodeName().equals("mapCity")) {
        				String name = commandNode.getAttribute("name");
        				if (ndd.containsName(name)) {
        					City city = ndd.getCity(name);
        					int x = city.getXCoord();
        					int y = city.getYCoord();
        					if (!tree.containsGeom(city)) {
        						if(x <= spatialWidth && y <= spatialHeight && x >= 0 && y >= 0) {
        							tree.insert(city);
        							isolated.add(city);
        							cities.add(city);
        							
        							Element success = results.createElement("success");
            						
            						Element command = results.createElement("command");
            						command.setAttribute("name", "mapCity");
            						String id = commandNode.getAttribute("id");
            						if (!id.isEmpty()) {
            							command.setAttribute("id", id);
            						}
            						
            						Element parameters = results.createElement("parameters");
            						
            						Element paramName = results.createElement("name");
            						paramName.setAttribute("value", name);
            						parameters.appendChild(paramName);
            						
            						Element output = results.createElement("output");

            						success.appendChild(command);
            						success.appendChild(parameters);
            						success.appendChild(output);
            						
            						r.appendChild(success);
        						} else {
        							Element error = results.createElement("error");
            						error.setAttribute("type", "cityOutOfBounds");
            						
            						Element command = results.createElement("command");
            						command.setAttribute("name", "mapCity");
            						String id = commandNode.getAttribute("id");
            						if (!id.isEmpty()) {
            							command.setAttribute("id", id);
            						}
            						
            						Element parameters = results.createElement("parameters");
            						
            						Element paramName = results.createElement("name");
            						paramName.setAttribute("value", name);
            						
            						parameters.appendChild(paramName);
            						error.appendChild(command);
            						error.appendChild(parameters);
            						
            						r.appendChild(error);
        						}
        					} else {
        						Element error = results.createElement("error");
        						error.setAttribute("type", "cityAlreadyMapped");
        						
        						Element command = results.createElement("command");
        						command.setAttribute("name", "mapCity");
        						String id = commandNode.getAttribute("id");
        						if (!id.isEmpty()) {
        							command.setAttribute("id", id);
        						}
        						
        						Element parameters = results.createElement("parameters");
        						
        						Element paramName = results.createElement("name");
        						paramName.setAttribute("value", name);
        						
        						parameters.appendChild(paramName);
        						error.appendChild(command);
        						error.appendChild(parameters);
        						
        						r.appendChild(error);
        					}
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "nameNotInDictionary");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "mapCity");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						
    						Element paramName = results.createElement("name");
    						paramName.setAttribute("value", name);
    						
    						parameters.appendChild(paramName);
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
//        			} else if (commandNode.getNodeName().equals("unmapCity")) {
//        				String name = commandNode.getAttribute("name");
//        				
//        				if (ndd.containsName(name)) {
//        					City city = ndd.getCity(name);
//    						if(tree.containsElem(city)) {
//    							tree.delete(city);
//    							        							
//    							Element success = results.createElement("success");
//        						
//        						Element command = results.createElement("command");
//        						command.setAttribute("name", "unmapCity");
//        						
//        						Element parameters = results.createElement("parameters");
//        						
//        						Element paramName = results.createElement("name");
//        						paramName.setAttribute("value", name);
//        						
//        						Element output = results.createElement("output");
//        						
//        						parameters.appendChild(paramName);
//
//        						success.appendChild(command);
//        						success.appendChild(parameters);
//        						success.appendChild(output);
//        						
//        						r.appendChild(success);
//    						} else {
//    							Element error = results.createElement("error");
//        						error.setAttribute("type", "cityNotMapped");
//        						
//        						Element command = results.createElement("command");
//        						command.setAttribute("name", "unmapCity");
//        						
//        						Element parameters = results.createElement("parameters");
//        						
//        						Element paramName = results.createElement("name");
//        						paramName.setAttribute("value", name);
//        						
//        						parameters.appendChild(paramName);
//        						error.appendChild(command);
//        						error.appendChild(parameters);
//        						
//        						r.appendChild(error);
//    						}
//        				} else {
//        					Element error = results.createElement("error");
//    						error.setAttribute("type", "nameNotInDictionary");
//    						
//    						Element command = results.createElement("command");
//    						command.setAttribute("name", "unmapCity");
//    						
//    						Element parameters = results.createElement("parameters");
//    						
//    						Element paramName = results.createElement("name");
//    						paramName.setAttribute("value", name);
//    						
//    						parameters.appendChild(paramName);
//    						error.appendChild(command);
//    						error.appendChild(parameters);
//    						
//    						r.appendChild(error);
//        				}
        			} else if (commandNode.getNodeName().equals("printPMQuadtree")) {
        				if (!tree.isEmpty()) {
        					ArrayList<Node> search = tree.breadthFirst();
        					Element success = results.createElement("success");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "printPMQuadtree");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						
    						Element output = results.createElement("output");
    						
    						Element quadtree = parser.printTree(search, isolated, results, cdd);
    						output.appendChild(quadtree);

    						success.appendChild(command);
    						success.appendChild(parameters);
    						success.appendChild(output);
    						
    						r.appendChild(success);
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "mapIsEmpty");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "printPMQuadtree");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
        				
        			} else if (commandNode.getNodeName().equals("saveMap")) {
        				CanvasPlus canvas = parser.saveMap(spatialWidth, spatialHeight, tree.breadthFirst(), cdd);
        				canvas.save(commandNode.getAttribute("name"));
        				canvas.draw();
        				
        				Element success = results.createElement("success");
						
						Element command = results.createElement("command");
						command.setAttribute("name", "saveMap");
						String id = commandNode.getAttribute("id");
						if (!id.isEmpty()) {
							command.setAttribute("id", id);
						}
						
						Element parameters = results.createElement("parameters");
						Element paramName = results.createElement("name");
						paramName.setAttribute("value", commandNode.getAttribute("name"));
						parameters.appendChild(paramName);
						
						Element output = results.createElement("output");

						success.appendChild(command);
						success.appendChild(parameters);
						success.appendChild(output);
						
						r.appendChild(success);
        				
        			} else if (commandNode.getNodeName().equals("rangeCities")) {
        				int x = Integer.parseInt(commandNode.getAttribute("x"));
        				int y = Integer.parseInt(commandNode.getAttribute("y"));
        				int radius = Integer.parseInt(commandNode.getAttribute("radius"));
        				String save = commandNode.getAttribute("saveMap");
        				TreeMap<String,City> citiesInRange = parser.rangeCities(new TreeMap<String,City>(new CityNameComparator()), 
        						cdd.getCities(), x, y, radius, tree, cdd);
        				
        				if (!citiesInRange.isEmpty()) {
        					Element success = results.createElement("success");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "rangeCities");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						Element paramRad = results.createElement("radius");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						paramRad.setAttribute("value", ((Integer) radius).toString());
    						parameters.appendChild(paramRad);
    						if (!save.isEmpty()) {
    							Element paramSave = results.createElement("saveMap");
    							paramSave.setAttribute("value", commandNode.getAttribute("saveMap"));
        						parameters.appendChild(paramSave);
        						
        						CanvasPlus canvas = parser.saveMap(spatialWidth, spatialHeight, tree.breadthFirst(), cdd);
        						canvas.addCircle(x, y, radius, Color.BLUE, false);
        						canvas.save(save);
        						canvas.draw();
    						}
    						
    						Element output = results.createElement("output");
    						Element cityList = results.createElement("cityList");
    						for (String str : citiesInRange.keySet()) {
    							Element city =  results.createElement("city");
    							City c = ndd.getCity(str);
    							city.setAttribute("name", str);
    							city.setAttribute("x", ((Integer) c.getXCoord()).toString());
    							city.setAttribute("y", ((Integer) c.getYCoord()).toString());
    							city.setAttribute("color", c.getColor());
    							city.setAttribute("radius", ((Integer) c.getRadius()).toString());
    							cityList.appendChild(city);
    						}
    						output.appendChild(cityList);

    						success.appendChild(command);
    						success.appendChild(parameters);
    						success.appendChild(output);
    						
    						r.appendChild(success);
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "noCitiesExistInRange");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "rangeCities");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						Element paramRad = results.createElement("radius");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						paramRad.setAttribute("value", ((Integer) radius).toString());
    						parameters.appendChild(paramRad);
    						if (!save.isEmpty()) {
    							Element paramSave = results.createElement("saveMap");
    							paramSave.setAttribute("value", commandNode.getAttribute("saveMap"));
        						parameters.appendChild(paramSave);
    						}
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				} 
        			} else if (commandNode.getNodeName().equals("rangeRoads")) {
        				int x = Integer.parseInt(commandNode.getAttribute("x"));
        				int y = Integer.parseInt(commandNode.getAttribute("y"));
        				int radius = Integer.parseInt(commandNode.getAttribute("radius"));
        				String save = commandNode.getAttribute("saveMap");
        				ArrayList<Road> roadsInRange = parser.rangeRoads(new ArrayList<Road>(), roads, x, y, radius, tree);
        				
        				if (!roadsInRange.isEmpty()) {
        					Element success = results.createElement("success");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "rangeRoads");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						Element paramRad = results.createElement("radius");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						paramRad.setAttribute("value", ((Integer) radius).toString());
    						parameters.appendChild(paramRad);
    						if (!save.isEmpty()) {
    							Element paramSave = results.createElement("saveMap");
    							paramSave.setAttribute("value", commandNode.getAttribute("saveMap"));
        						parameters.appendChild(paramSave);
        						
        						CanvasPlus canvas = parser.saveMap(spatialWidth, spatialHeight, tree.breadthFirst(), cdd);
        						canvas.addCircle(x, y, radius, Color.BLUE, false);
        						canvas.save(save);
        						canvas.draw();
    						}
    						
    						Element output = results.createElement("output");
    						Element roadList = results.createElement("roadList");
    						roadsInRange.sort(new RoadComparator(cdd));
    						for (Road ro : roadsInRange) {
    							Element road =  results.createElement("road");
    							road.setAttribute("start", cdd.getName(ro.getStart()));
    							road.setAttribute("end", cdd.getName(ro.getEnd()));
    							roadList.appendChild(road);
    						}
    						output.appendChild(roadList);

    						success.appendChild(command);
    						success.appendChild(parameters);
    						success.appendChild(output);
    						
    						r.appendChild(success);
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "noRoadsExistInRange");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "rangeRoads");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						Element paramRad = results.createElement("radius");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						paramRad.setAttribute("value", ((Integer) radius).toString());
    						parameters.appendChild(paramRad);
    						if (!save.isEmpty()) {
    							Element paramSave = results.createElement("saveMap");
    							paramSave.setAttribute("value", commandNode.getAttribute("saveMap"));
        						parameters.appendChild(paramSave);
    						}
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
        			} else if (commandNode.getNodeName().equals("nearestCity")) {
    					int x = Integer.parseInt(commandNode.getAttribute("x"));
    					int y = Integer.parseInt(commandNode.getAttribute("y"));

    					City closest = parser.nearest(ndd.getNames(), x, y, tree, ndd);
        				if (closest != null && !tree.isEmpty() && cdd.size() > 0) {
        					
        					Element success = results.createElement("success");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "nearestCity");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						
    						Element output = results.createElement("output");
    						Element city = results.createElement("city");
    						city.setAttribute("name", cdd.getName(closest));
    						city.setAttribute("x", ((Integer) closest.getXCoord()).toString());
    						city.setAttribute("y", ((Integer) closest.getYCoord()).toString());
    						city.setAttribute("color", closest.getColor());
    						city.setAttribute("radius", ((Integer) closest.getRadius()).toString());
    						output.appendChild(city);

    						success.appendChild(command);
    						success.appendChild(parameters);
    						success.appendChild(output);
    						
    						r.appendChild(success);
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "cityNotFound");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "nearestCity");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
        			} else if (commandNode.getNodeName().equals("nearestIsolatedCity")) {
    					int x = Integer.parseInt(commandNode.getAttribute("x"));
    					int y = Integer.parseInt(commandNode.getAttribute("y"));
    					
        				if (!tree.isEmpty() && isolated.size() > 0) {
        					City closest = parser.nearestIsolated(cdd.getCities(), x, y, tree, cdd);
        					
        					Element success = results.createElement("success");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "nearestIsolatedCity");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						
    						Element output = results.createElement("output");
    						Element city = results.createElement("isolatedCity");
    						city.setAttribute("name", cdd.getName(closest));
    						city.setAttribute("x", ((Integer) closest.getXCoord()).toString());
    						city.setAttribute("y", ((Integer) closest.getYCoord()).toString());
    						city.setAttribute("color", closest.getColor());
    						city.setAttribute("radius", ((Integer) closest.getRadius()).toString());
    						output.appendChild(city);

    						success.appendChild(command);
    						success.appendChild(parameters);
    						success.appendChild(output);
    						
    						r.appendChild(success);
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "cityNotFound");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "nearestIsolatedCity");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
        			} else if (commandNode.getNodeName().equals("nearestRoad")) {
    					int x = Integer.parseInt(commandNode.getAttribute("x"));
    					int y = Integer.parseInt(commandNode.getAttribute("y"));
        				if (!tree.isEmpty() && roads.size() > 0) {
        					Road closest = parser.nearestRoad(roads, x, y, tree, cdd);
        					Element success = results.createElement("success");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "nearestRoad");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						
    						Element output = results.createElement("output");
    						Element road = results.createElement("road");
    						road.setAttribute("start", cdd.getName(closest.getStart()));
    						road.setAttribute("end", cdd.getName(closest.getEnd()));
    						output.appendChild(road);

    						success.appendChild(command);
    						success.appendChild(parameters);
    						success.appendChild(output);
    						
    						r.appendChild(success);
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "roadNotFound");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "nearestRoad");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramX = results.createElement("x");
    						Element paramY = results.createElement("y");
    						paramX.setAttribute("value", ((Integer) x).toString());
    						parameters.appendChild(paramX);
    						paramY.setAttribute("value", ((Integer) y).toString());
    						parameters.appendChild(paramY);
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
        			} else if (commandNode.getNodeName().equals("nearestCityToRoad")) {
        				String start = commandNode.getAttribute("start");
        				String end = commandNode.getAttribute("end");
        				Road road;
        				if (end.compareTo(start) > 0) {
            				road = new Road(ndd.getCity(start), ndd.getCity(end));
        				} else {
        					road = new Road(ndd.getCity(end), ndd.getCity(start));
        				}
        				if (!start.equals(end) && tree.containsGeom(road)) {
        					if (tree.numCities() > 2) {
        						City closest = parser.nearestCityToRoad(cities, road, tree, cdd);
        						Element success = results.createElement("success");
        						
        						Element command = results.createElement("command");
        						command.setAttribute("name", "nearestCityToRoad");
        						String id = commandNode.getAttribute("id");
        						if (!id.isEmpty()) {
        							command.setAttribute("id", id);
        						}
        						
        						Element parameters = results.createElement("parameters");
        						Element paramStart = results.createElement("start");
        						Element paramEnd = results.createElement("end");
        						paramStart.setAttribute("value", start);
        						parameters.appendChild(paramStart);
        						paramEnd.setAttribute("value", end);
        						parameters.appendChild(paramEnd);
        						
        						Element output = results.createElement("output");
        						Element city = results.createElement("city");
        						city.setAttribute("name", cdd.getName(closest));
        						city.setAttribute("x", ((Integer) closest.getXCoord()).toString());
        						city.setAttribute("y", ((Integer) closest.getYCoord()).toString());
        						city.setAttribute("color", closest.getColor());
        						city.setAttribute("radius", ((Integer) closest.getRadius()).toString());
        						output.appendChild(city);

        						success.appendChild(command);
        						success.appendChild(parameters);
        						success.appendChild(output);
        						
        						r.appendChild(success);
        					} else {
        						Element error = results.createElement("error");
        						error.setAttribute("type", "noOtherCitiesMapped");
        						
        						Element command = results.createElement("command");
        						command.setAttribute("name", "nearestCityToRoad");
        						String id = commandNode.getAttribute("id");
        						if (!id.isEmpty()) {
        							command.setAttribute("id", id);
        						}
        						
        						Element parameters = results.createElement("parameters");
        						Element paramStart = results.createElement("start");
        						Element paramEnd = results.createElement("end");
        						paramStart.setAttribute("value", start);
        						parameters.appendChild(paramStart);
        						paramEnd.setAttribute("value", end);
        						parameters.appendChild(paramEnd);
        						
        						error.appendChild(command);
        						error.appendChild(parameters);
        						
        						r.appendChild(error);
        					}
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "roadIsNotMapped");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "nearestCityToRoad");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramStart = results.createElement("start");
    						Element paramEnd = results.createElement("end");
    						paramStart.setAttribute("value", start);
    						parameters.appendChild(paramStart);
    						paramEnd.setAttribute("value", end);
    						parameters.appendChild(paramEnd);
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
        			} else if (commandNode.getNodeName().equals("shortestPath")) {
        				String start = commandNode.getAttribute("start");
        				String end = commandNode.getAttribute("end");
        				String saveMap = commandNode.getAttribute("saveMap");
        				String saveHTML = commandNode.getAttribute("saveHTML");
        				City source = ndd.getCity(start);
        				City dest = ndd.getCity(end);
        				
        				if (source != null && tree.containsGeom(source)) {
        					if (dest != null && tree.containsGeom(dest)) {
        						parser.shortestPath(source, cdd);
        						if (dest.getPrev() != null || dest.equals(source)) {
        							Element success = results.createElement("success");
        							Element command = results.createElement("command");
            						command.setAttribute("name", "shortestPath");
            						ArrayList<City> pathTo = parser.pathTo(dest);
            						String id = commandNode.getAttribute("id");
            						if (!id.isEmpty()) {
            							command.setAttribute("id", id);
            						}
            						
            						Element parameters = results.createElement("parameters");
            						Element paramStart = results.createElement("start");
            						Element paramEnd = results.createElement("end");
            						paramStart.setAttribute("value", start);
            						parameters.appendChild(paramStart);
            						paramEnd.setAttribute("value", end);
            						parameters.appendChild(paramEnd);
            						
            						Element output = results.createElement("output");
            						Element path = results.createElement("path");
            						DecimalFormat df = new DecimalFormat("0.000");
            						path.setAttribute("length", df.format(dest.getMinDist()).toString());
            						path.setAttribute("hops", ((Integer) dest.getHops()).toString());
            						
            						City curr = source, next = null, nextNext = null;
            						if (pathTo.size() > 1)
            							next = pathTo.get(1);
            						if (pathTo.size() > 2) 
            							nextNext = pathTo.get(2);
        							String direction;
        	        				Point2D.Double p1, p2, p3;
        	        				int counter = 3;
        	        				double angle;
        	        				while(nextNext != null) {
        	        					Element road = results.createElement("road");
        	        					road.setAttribute("start", curr.getName());
        	        					road.setAttribute("end", next.getName());
        	        					p1 = new Point2D.Double(curr.getXCoord(), curr.getYCoord());
            							p2 = new Point2D.Double(next.getXCoord(), next.getYCoord());
            							p3 = new Point2D.Double(nextNext.getXCoord(), nextNext.getYCoord());
            	        				Arc2D.Double arc = new Arc2D.Double();
            	        				arc.setArcByTangent(p1,p2,p3,1);
            	        				angle = arc.getAngleExtent();
            	        				if (angle < 0) {
            	        					angle += 360;
            	        				}
            	        				if (angle < 180 && angle >= 45) {
            	        					direction = "right";
            	        				} else if (angle < 45 || angle >= 315) {
            	        					direction = "straight";
            	        				} else {
            	        					direction = "left";
            	        				}
            							Element turn = results.createElement(direction);
        	        					path.appendChild(road);
        	        					path.appendChild(turn);
        	        					curr = next;
        	        					next = nextNext;
        	        					if (counter < pathTo.size()) {
        	        						nextNext = pathTo.get(counter);
        	        					} else {
        	        						nextNext = null;
        	        					}
        	        					counter++;
        	        				}
        	        				if (next != null) {
	        	        				Element road = results.createElement("road");
	        	        				road.setAttribute("start", curr.getName());
	        	        				road.setAttribute("end", next.getName());
	        	        				path.appendChild(road);
        	        				}
            						output.appendChild(path);
            						
            						if (!saveMap.isEmpty()) {
            							Element paramSave = results.createElement("saveMap");
            							paramSave.setAttribute("value", saveMap);
                						parameters.appendChild(paramSave);
                						
                						CanvasPlus canvas = parser.shortestPathMap(spatialWidth, spatialHeight, pathTo);
                						canvas.save(saveMap);
                						canvas.draw();
            						}
            						
            						if (!saveHTML.isEmpty()) {
            							Element paramSave = results.createElement("saveHTML");
            							paramSave.setAttribute("value", saveHTML);
                						parameters.appendChild(paramSave);
                						success.appendChild(command);
                						success.appendChild(parameters);
                						success.appendChild(output);
                						CanvasPlus canvas = parser.shortestPathMap(spatialWidth, spatialHeight, pathTo);
                						canvas.save(saveHTML);
                						Document shortestPathDoc = XmlUtility.getDocumentBuilder().newDocument();
                						org.w3c.dom.Node spNode = shortestPathDoc.importNode(success, true);
            							shortestPathDoc.appendChild(spNode);
            							try {
											XmlUtility.transform(shortestPathDoc, new File("shortestPath.xsl"), new File(saveHTML + ".html"));
										} catch (TransformerException e) {
											e.printStackTrace();
										}
            						} else {
	            						success.appendChild(command);
	            						success.appendChild(parameters);
	            						success.appendChild(output);
            						}
        	        				r.appendChild(success);
        						} else {
        							Element error = results.createElement("error");
            						error.setAttribute("type", "noPathExists");
            						
            						Element command = results.createElement("command");
            						command.setAttribute("name", "shortestPath");
            						String id = commandNode.getAttribute("id");
            						if (!id.isEmpty()) {
            							command.setAttribute("id", id);
            						}
            						
            						Element parameters = results.createElement("parameters");
            						Element paramStart = results.createElement("start");
            						Element paramEnd = results.createElement("end");
            						paramStart.setAttribute("value", start);
            						parameters.appendChild(paramStart);
            						paramEnd.setAttribute("value", end);
            						parameters.appendChild(paramEnd);
            						if (!saveMap.isEmpty()) {
            							Element paramSave = results.createElement("saveMap");
            							paramSave.setAttribute("value", saveMap);
                						parameters.appendChild(paramSave);
            						}
            						if (!saveHTML.isEmpty()) {
            							Element paramSave = results.createElement("saveHTML");
            							paramSave.setAttribute("value", saveHTML);
                						parameters.appendChild(paramSave);
            						}
            						
            						error.appendChild(command);
            						error.appendChild(parameters);
            						
            						r.appendChild(error);
        						}
        					} else {
        						Element error = results.createElement("error");
        						error.setAttribute("type", "noExistentEnd");
        						
        						Element command = results.createElement("command");
        						command.setAttribute("name", "shortestPath");
        						String id = commandNode.getAttribute("id");
        						if (!id.isEmpty()) {
        							command.setAttribute("id", id);
        						}
        						
        						Element parameters = results.createElement("parameters");
        						Element paramStart = results.createElement("start");
        						Element paramEnd = results.createElement("end");
        						paramStart.setAttribute("value", start);
        						parameters.appendChild(paramStart);
        						paramEnd.setAttribute("value", end);
        						parameters.appendChild(paramEnd);
        						if (!saveMap.isEmpty()) {
        							Element paramSave = results.createElement("saveMap");
        							paramSave.setAttribute("value", saveMap);
            						parameters.appendChild(paramSave);
        						}
        						if (!saveHTML.isEmpty()) {
        							Element paramSave = results.createElement("saveHTML");
        							paramSave.setAttribute("value", saveHTML);
            						parameters.appendChild(paramSave);
        						}
        						
        						error.appendChild(command);
        						error.appendChild(parameters);
        						
        						r.appendChild(error);
        					}
        				} else {
        					Element error = results.createElement("error");
    						error.setAttribute("type", "noExistentStart");
    						
    						Element command = results.createElement("command");
    						command.setAttribute("name", "shortestPath");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						Element paramStart = results.createElement("start");
    						Element paramEnd = results.createElement("end");
    						paramStart.setAttribute("value", start);
    						parameters.appendChild(paramStart);
    						paramEnd.setAttribute("value", end);
    						parameters.appendChild(paramEnd);
    						if (!saveMap.isEmpty()) {
    							Element paramSave = results.createElement("saveMap");
    							paramSave.setAttribute("value", saveMap);
        						parameters.appendChild(paramSave);
    						}
    						if (!saveHTML.isEmpty()) {
    							Element paramSave = results.createElement("saveHTML");
    							paramSave.setAttribute("value", saveHTML);
        						parameters.appendChild(paramSave);
    						}
    						
    						error.appendChild(command);
    						error.appendChild(parameters);
    						
    						r.appendChild(error);
        				}
        			}
        		}
        	}
        	
        } catch (SAXException | IOException | ParserConfigurationException e) {
        	
        	try {
				results = XmlUtility.getDocumentBuilder().newDocument();
	        	Element fatal = results.createElement("fatalError");
	        	results.appendChild(fatal);
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			}
        	
		} finally {
            try {
				XmlUtility.print(results);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
        }
    }
}
