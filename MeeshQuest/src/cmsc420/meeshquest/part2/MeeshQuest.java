package cmsc420.meeshquest.part2;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.drawing.CanvasPlus;
import cmsc420.xml.XmlUtility;
import dictionaries.CoordDataDictionary;
import dictionaries.NameDataDictionary;
import spatialMaps.Node;
import spatialMaps.PMQuadtree;
import spatialMaps.PRQuadTree;

public class MeeshQuest {

    public static void main(String[] args) {
    	
    	Document results = null;
    	CommandParser parser = new CommandParser();
    	NameDataDictionary ndd = new NameDataDictionary();
    	CoordDataDictionary cdd = new CoordDataDictionary();
    	PMQuadtree tree;
    	
        try {
        	Document doc = XmlUtility.validateNoNamespace(System.in);
        	results = XmlUtility.getDocumentBuilder().newDocument();
        	Element r = results.createElement("results");
        	results.appendChild(r);
        
        	Element commandNode = doc.getDocumentElement();
        	int spatialWidth = Integer.parseInt(commandNode.getAttribute("spatialWidth")); 
        	int spatialHeight = Integer.parseInt(commandNode.getAttribute("spatialHeight"));
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
        				Element cities = parser.listCities(commandNode, ndd, cdd, results);
        				r.appendChild(cities);
        				
        			} else if (commandNode.getNodeName().equals("clearAll")) {
        				ndd.clear();
        				cdd.clear();
        				tree.clearAll();
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
        				
        			} else if (commandNode.getNodeName().equals("mapCity")) {
        				String name = commandNode.getAttribute("name");
        				if (ndd.containsName(name)) {
        					City city = ndd.getCity(name);
        					int x = city.getXCoord();
        					int y = city.getYCoord();
        					if (x < spatialWidth && y < spatialHeight) {
        						if(!tree.containsGeom(city)) {
        							tree.insert(city);
        							        							
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
    						command.setAttribute("name", "printPRMuadtree");
    						String id = commandNode.getAttribute("id");
    						if (!id.isEmpty()) {
    							command.setAttribute("id", id);
    						}
    						
    						Element parameters = results.createElement("parameters");
    						
    						Element output = results.createElement("output");
    						
    						Element quadtree = parser.printTree(search, results, cdd);
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
        				
//        			} else if (commandNode.getNodeName().equals("saveMap")) {
//        				CanvasPlus canvas = parser.saveMap(spatialWidth, spatialHeight, tree.breadthFirst(), cdd);
//        				canvas.save(commandNode.getAttribute("name"));
//        				canvas.draw();
//        				
//        				Element success = results.createElement("success");
//						
//						Element command = results.createElement("command");
//						command.setAttribute("name", "saveMap");
//						
//						Element parameters = results.createElement("parameters");
//						Element paramName = results.createElement("name");
//						paramName.setAttribute("value", commandNode.getAttribute("name"));
//						parameters.appendChild(paramName);
//						
//						Element output = results.createElement("output");
//
//						success.appendChild(command);
//						success.appendChild(parameters);
//						success.appendChild(output);
//						
//						r.appendChild(success);
//        				
//        			} else if (commandNode.getNodeName().equals("rangeCities")) {
//        				int x = Integer.parseInt(commandNode.getAttribute("x"));
//        				int y = Integer.parseInt(commandNode.getAttribute("y"));
//        				int radius = Integer.parseInt(commandNode.getAttribute("radius"));
//        				String save = commandNode.getAttribute("saveMap");
//        				ArrayList<City> citiesInRange = parser.range(new ArrayList<City>(), cdd.getCities(), x, y, radius, tree);
//        				
//        				if (!citiesInRange.isEmpty()) {
//        					Element success = results.createElement("success");
//    						
//    						Element command = results.createElement("command");
//    						command.setAttribute("name", "rangeCities");
//    						
//    						Element parameters = results.createElement("parameters");
//    						Element paramX = results.createElement("x");
//    						Element paramY = results.createElement("y");
//    						Element paramRad = results.createElement("radius");
//    						paramX.setAttribute("value", ((Integer) x).toString());
//    						parameters.appendChild(paramX);
//    						paramY.setAttribute("value", ((Integer) y).toString());
//    						parameters.appendChild(paramY);
//    						paramRad.setAttribute("value", ((Integer) radius).toString());
//    						parameters.appendChild(paramRad);
//    						if (!save.isEmpty()) {
//    							Element paramSave = results.createElement("saveMap");
//    							paramSave.setAttribute("value", commandNode.getAttribute("saveMap"));
//        						parameters.appendChild(paramSave);
//        						
//        						CanvasPlus canvas = parser.saveMap(spatialWidth, spatialHeight, tree.breadthFirst(), cdd);
//        						canvas.addCircle(x, y, radius, Color.BLUE, false);
//        						canvas.save(save);
//        						canvas.draw();
//    						}
//    						
//    						Element output = results.createElement("output");
//    						Element cityList = results.createElement("cityList");
//    						for (City c : citiesInRange) {
//    							Element city =  results.createElement("city");
//    							city.setAttribute("name", cdd.getName(c));
//    							city.setAttribute("x", ((Integer) c.getXCoord()).toString());
//    							city.setAttribute("y", ((Integer) c.getYCoord()).toString());
//    							city.setAttribute("color", c.getColor());
//    							city.setAttribute("radius", ((Integer) c.getRadius()).toString());
//    							cityList.appendChild(city);
//    						}
//    						output.appendChild(cityList);
//
//    						success.appendChild(command);
//    						success.appendChild(parameters);
//    						success.appendChild(output);
//    						
//    						r.appendChild(success);
//        				} else {
//        					Element error = results.createElement("error");
//    						error.setAttribute("type", "noCitiesExistInRange");
//    						
//    						Element command = results.createElement("command");
//    						command.setAttribute("name", "rangeCities");
//    						
//    						Element parameters = results.createElement("parameters");
//    						Element paramX = results.createElement("x");
//    						Element paramY = results.createElement("y");
//    						Element paramRad = results.createElement("radius");
//    						paramX.setAttribute("value", ((Integer) x).toString());
//    						parameters.appendChild(paramX);
//    						paramY.setAttribute("value", ((Integer) y).toString());
//    						parameters.appendChild(paramY);
//    						paramRad.setAttribute("value", ((Integer) radius).toString());
//    						parameters.appendChild(paramRad);
//    						if (!save.isEmpty()) {
//    							Element paramSave = results.createElement("saveMap");
//    							paramSave.setAttribute("value", commandNode.getAttribute("saveMap"));
//        						parameters.appendChild(paramSave);
//    						}
//    						
//    						error.appendChild(command);
//    						error.appendChild(parameters);
//    						
//    						r.appendChild(error);
//        				}
//        				
//        			} else if (commandNode.getNodeName().equals("nearestCity")) {
//    					int x = Integer.parseInt(commandNode.getAttribute("x"));
//    					int y = Integer.parseInt(commandNode.getAttribute("y"));
//    					
//        				if (!tree.isEmpty() && cdd.size() > 0) {
//        					City closest = parser.nearest(cdd.getCities(), x, y, tree, cdd);
//        					
//        					Element success = results.createElement("success");
//    						
//    						Element command = results.createElement("command");
//    						command.setAttribute("name", "nearestCity");
//    						
//    						Element parameters = results.createElement("parameters");
//    						Element paramX = results.createElement("x");
//    						Element paramY = results.createElement("y");
//    						paramX.setAttribute("value", ((Integer) x).toString());
//    						parameters.appendChild(paramX);
//    						paramY.setAttribute("value", ((Integer) y).toString());
//    						parameters.appendChild(paramY);
//    						
//    						Element output = results.createElement("output");
//    						Element city = results.createElement("city");
//    						city.setAttribute("name", cdd.getName(closest));
//    						city.setAttribute("x", ((Integer) closest.getXCoord()).toString());
//    						city.setAttribute("y", ((Integer) closest.getYCoord()).toString());
//    						city.setAttribute("color", closest.getColor());
//    						city.setAttribute("radius", ((Integer) closest.getRadius()).toString());
//    						output.appendChild(city);
//
//    						success.appendChild(command);
//    						success.appendChild(parameters);
//    						success.appendChild(output);
//    						
//    						r.appendChild(success);
//        				} else {
//        					Element error = results.createElement("error");
//    						error.setAttribute("type", "mapIsEmpty");
//    						
//    						Element command = results.createElement("command");
//    						command.setAttribute("name", "nearestCity");
//    						
//    						Element parameters = results.createElement("parameters");
//    						Element paramX = results.createElement("x");
//    						Element paramY = results.createElement("y");
//    						paramX.setAttribute("value", ((Integer) x).toString());
//    						parameters.appendChild(paramX);
//    						paramY.setAttribute("value", ((Integer) y).toString());
//    						parameters.appendChild(paramY);
//    						
//    						error.appendChild(command);
//    						error.appendChild(parameters);
//    						
//    						r.appendChild(error);
//        				}
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
