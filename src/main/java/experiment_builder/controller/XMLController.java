package experiment_builder.controller;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j;
import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;
import experiment_builder.model.Reward;
import experiment_builder.view.CellGridPanel;

@Log4j
public class XMLController {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( XMLController.class );

	//pass in references to be set to values
	public static int loadLayoutXML(String filePath, CellGrid passedCellGrid ) {
		DocumentBuilderFactory factory;
		DocumentBuilder builder = null;

		CellGrid cellGrid = null;

		try {
			File inputFile = new File(filePath);
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();

			Document doc = builder.parse(inputFile);
			doc.getDocumentElement().normalize();

			String root = doc.getDocumentElement().getNodeName();

			if (!root.equals("GridCell")) {
				return -1;
			}
			
			int rowCount = Integer
					.parseInt(doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().item(0).getTextContent() );
			int colCount = Integer
					.parseInt(doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().item(1).getTextContent() );

			String sensorType = doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().item(2).getTextContent() ;
			String actionMapType = doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().item(3).getTextContent() ;
			
			
			log.info(rowCount + " " + colCount +" " + sensorType);
			cellGrid = new CellGrid(rowCount, colCount, sensorType, actionMapType );
			// Set Agent
			int position = Integer.parseInt(doc.getElementsByTagName("Agent").item(0).getFirstChild().getTextContent());
			int power = (int)Float
								.parseFloat(doc.getElementsByTagName("Agent").item(0).getLastChild().getTextContent());

			//possibly not needed, but include to be safe for now
			Agent singleAgent = new Agent();
			singleAgent.setPos( position );
			singleAgent.setHealth(power);
			cellGrid.setAgent(singleAgent);
			
			cellGrid.setStartingPosition(position);
			cellGrid.setStartingPower(power);
			
			
			// First parse Rectangle Array
			Element currentNode = (Element) doc.getElementsByTagName("Rectangle").item(0);
			NodeList nList = currentNode.getChildNodes();
			Rectangle rect[] = new Rectangle[rowCount * colCount];

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element innerElement = (Element) nNode;
					NodeList innerEleList = innerElement.getChildNodes();
					if (innerEleList.getLength() == 4) {
						// X co-ordinate
						Element firstTag = (Element) innerEleList.item(0);
						int x = Integer.parseInt(firstTag.getTextContent());

						// Y co-ordinate
						Element secondTag = (Element) innerEleList.item(1);
						int y = Integer.parseInt(secondTag.getTextContent());

						Element thirdTag = (Element) innerEleList.item(2);
						int w = Integer.parseInt(thirdTag.getTextContent());

						Element fourthTag = (Element) innerEleList.item(3);
						int h = Integer.parseInt(fourthTag.getTextContent());

						rect[i] = new Rectangle(x, y, w, h);
					}

				}
			}
			cellGrid.setBoundingRectangle(rect);

			// Parse Reward
			currentNode = (Element) doc.getElementsByTagName("Reward").item(0);
			nList = currentNode.getChildNodes();

			HashMap<Integer, Reward> reward= new HashMap<>();
			for(int i = 0 ; i < nList.getLength() ; i++){
				NodeList rewardList = nList.item(i).getChildNodes();
				String[] split = nList.item(i).getNodeName().split("_");
				int rowNo = Integer.parseInt(split[1]);
				Element rewardType = (Element) rewardList.item(0);
				Element rewardValue = (Element) rewardList.item(1);
				reward.put(rowNo, new Reward(rewardType.getTextContent(),Double.parseDouble(rewardValue.getTextContent())));

			}
			cellGrid.setStartingRewards(reward);

			
			// Parse Cell Events
			currentNode = (Element) doc.getElementsByTagName("CellEvents").item(0);
			nList = currentNode.getChildNodes();
			HashMap<Integer,HashSet<String>> cellEvents= new HashMap<Integer, HashSet<String>>();
			for(int i = 0 ; i < nList.getLength() ; i++){
				NodeList eventList = nList.item(i).getChildNodes();
				String[] split = nList.item(i).getNodeName().split("_");
				int rowNo = Integer.parseInt(split[1]);
				
				HashSet<String> events = new HashSet<String>();
				
				for (int j=0; j < eventList.getLength(); j++) {
					
					events.add( eventList.item(j).getTextContent() );
				}
				cellEvents.put(rowNo, events);
			}
			cellGrid.setCellEvents( cellEvents );
			
			logger.debug( "Cell events: "+ cellEvents  );
			
			
			// Parse Cell Types/Properties
			currentNode = (Element) doc.getElementsByTagName("CellTypes").item(0);
			nList = currentNode.getChildNodes();
			HashMap<Integer,HashSet<String>> cellTypes= new HashMap<Integer, HashSet<String>>();
			for(int i = 0 ; i < nList.getLength() ; i++){
				NodeList typesList = nList.item(i).getChildNodes();
				String[] split = nList.item(i).getNodeName().split("_");
				int rowNo = Integer.parseInt(split[1]);
				HashSet<String> types = new HashSet<String>();
				for (int j=0; j < typesList.getLength(); j++) {
					types.add( typesList.item(j).getTextContent() );
				}
				cellTypes.put(rowNo, types);
			}
			cellGrid.setCellProperties( cellTypes );
			
			

			// Parse Visibility
			currentNode = (Element) doc.getElementsByTagName("Visibility").item(0);
			nList = currentNode.getChildNodes();
			boolean[] visibilityArray = new boolean[rowCount * colCount];
			Arrays.fill(visibilityArray, false);
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				visibilityArray[i] = Boolean.parseBoolean(nNode.getTextContent());
			}
			cellGrid.setVisibility(visibilityArray);
			
			
			//gridView.setCellGrid(cellGrid);
			//gridView.setBoundingRectangle(cellGrid.getBoundingRectangle());
			//gridView.setVisibility(cellGrid.getVisibility());
			//gridView.mazeSetFlag = true;
			//gridView.mazeFinalized = true;
			
			AgentActions.setMazeLength(cellGrid.getRows());
			AgentActions.setMazeWidth(cellGrid.getCols());
			AgentActions.setCellGrid(cellGrid);
			
			logger.debug("Event value: "+cellGrid.getCellEvents().isEmpty());
			logger.debug("Event value: "+cellGrid.getCellProperties().isEmpty());
			logger.debug("Event value: "+cellGrid.getRewards().isEmpty());
			
			logger.debug( "Cell events: "+ cellGrid.getCellEvents()  );
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		passedCellGrid.setDuplicateValues(cellGrid);
		
		return 0;
	}

	
	
	public static int loadLayoutXML(String filePath, CellGridPanel gridView) {
		DocumentBuilderFactory factory;
		DocumentBuilder builder = null;

		CellGrid cellGrid = null;

		try {
			File inputFile = new File(filePath);
			
			//
			
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();

			Document doc = builder.parse(inputFile);
			doc.getDocumentElement().normalize();

			String root = doc.getDocumentElement().getNodeName();

			if (!root.equals("GridCell")) {
				return -1;
			}


			int rowCount = Integer
					.parseInt(doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().item(0).getTextContent() );
			int colCount = Integer
					.parseInt(doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().item(1).getTextContent() );
			
			
			String sensorType = "";
			
			if ( doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().getLength() < 4) {
				logger.error("No sensorType specified in file (IS THIS OLD?) using NAVIGATION_AND_RESOURCE_SENSOR so we don't crash  "); 
				sensorType = "NAVIGATION_AND_RESOURCE_SENSOR";
				System.exit(1);
			} 
			
			sensorType = doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().item(2).getTextContent() ;
			
			String actionMapType = "";
			actionMapType = doc.getElementsByTagName("MazeProperties").item(0).getChildNodes().item(3).getTextContent() ;
		

			
			logger.debug("SensorType: "+  sensorType + " actionMapType" + actionMapType  );
			cellGrid = new CellGrid(rowCount, colCount, sensorType, actionMapType);
			
			// First parse Rectangle Array
			Element currentNode = (Element) doc.getElementsByTagName("Rectangle").item(0);
			NodeList nList = currentNode.getChildNodes();
			Rectangle rect[] = new Rectangle[rowCount * colCount];

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element innerElement = (Element) nNode;
					NodeList innerEleList = innerElement.getChildNodes();
					if (innerEleList.getLength() == 4) {
						// X co-ordinate
						Element firstTag = (Element) innerEleList.item(0);
						int x = Integer.parseInt(firstTag.getTextContent());

						// Y co-ordinate
						Element secondTag = (Element) innerEleList.item(1);
						int y = Integer.parseInt(secondTag.getTextContent());

						Element thirdTag = (Element) innerEleList.item(2);
						int w = Integer.parseInt(thirdTag.getTextContent());

						Element fourthTag = (Element) innerEleList.item(3);
						int h = Integer.parseInt(fourthTag.getTextContent());

						rect[i] = new Rectangle(x, y, w, h);
					}

				}
			}
			cellGrid.setBoundingRectangle(rect);

			// Parse Reward
			currentNode = (Element) doc.getElementsByTagName("Reward").item(0);
			nList = currentNode.getChildNodes();

			HashMap<Integer, Reward> reward= new HashMap<>();
			for(int i = 0 ; i < nList.getLength() ; i++){
				NodeList rewardList = nList.item(i).getChildNodes();
				String[] split = nList.item(i).getNodeName().split("_");
				int rowNo = Integer.parseInt(split[1]);
				Element rewardType = (Element) rewardList.item(0);
				Element rewardValue = (Element) rewardList.item(1);
				reward.put(rowNo, new Reward(rewardType.getTextContent(),Double.parseDouble(rewardValue.getTextContent())));
			}
			
			cellGrid.setRewards(reward);
			
			HashMap<Integer, Reward> startingRewards = new HashMap<Integer, Reward>();
			
			for (Map.Entry<Integer, Reward> entry: reward.entrySet()) {
				startingRewards.put( entry.getKey(), entry.getValue() );			
			}
			
			cellGrid.setStartingRewards(startingRewards);

			
			// Parse Cell Events
			currentNode = (Element) doc.getElementsByTagName("CellEvents").item(0);
			nList = currentNode.getChildNodes();
			HashMap<Integer,HashSet<String>> cellEvents= new HashMap<Integer, HashSet<String>>();
			for(int i = 0 ; i < nList.getLength() ; i++){
				NodeList eventList = nList.item(i).getChildNodes();
				String[] split = nList.item(i).getNodeName().split("_");
				int rowNo = Integer.parseInt(split[1]);
				
				HashSet<String> events = new HashSet<String>();
				
				for (int j=0; j < eventList.getLength(); j++) {
					
					events.add( eventList.item(j).getTextContent() );
				}
				cellEvents.put(rowNo, events);
			}
			cellGrid.setCellEvents( cellEvents );
			
			logger.debug( "Cell events: "+ cellEvents  );
			
			// Parse Cell Types/Properties
			currentNode = (Element) doc.getElementsByTagName("CellTypes").item(0);
			nList = currentNode.getChildNodes();
			HashMap<Integer,HashSet<String>> cellTypes= new HashMap<Integer, HashSet<String>>();
			for(int i = 0 ; i < nList.getLength() ; i++){
				NodeList typesList = nList.item(i).getChildNodes();
				String[] split = nList.item(i).getNodeName().split("_");
				int rowNo = Integer.parseInt(split[1]);
				HashSet<String> types = new HashSet<String>();
				for (int j=0; j < typesList.getLength(); j++) 
				{
					types.add( typesList.item(j).getTextContent() );
				}
				cellTypes.put(rowNo, types);
			}
			cellGrid.setCellProperties( cellTypes );
			
			

			// Parse Visibility
			currentNode = (Element) doc.getElementsByTagName("Visibility").item(0);
			nList = currentNode.getChildNodes();
			boolean visible[] = new boolean[rowCount * colCount];
			Arrays.fill(visible, false);
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				visible[i] = Boolean.parseBoolean(nNode.getTextContent());
			}
			cellGrid.setVisibility(visible);

			gridView.setCellGrid(cellGrid);
			gridView.setBoundingRectangle(cellGrid.getBoundingRectangle());
			//gridView.getCellGrid().setVisibility(cellGrid.getVisibility());
			gridView.mazeSetFlag = true;
			gridView.mazeFinalized = true;
			AgentActions.setMazeLength(cellGrid.getRows());
			AgentActions.setMazeWidth(cellGrid.getCols());
			AgentActions.setCellGrid(cellGrid);
			
			logger.info("Cell Events Empty: "+gridView.getCellGrid().getCellEvents().isEmpty());
			logger.info("Cell Properties Empty: "+gridView.getCellGrid().getCellProperties().isEmpty());
			logger.info("Rewards Empty: "+gridView.getCellGrid().getRewards().isEmpty());
			logger.info("Cell events: "+ gridView.getCellGrid().getCellEvents()  );
			logger.info("GridView visibility: "+ gridView.getCellGrid().getVisibility()[0] );
			
			// Set Agent
			int position = Integer.parseInt(doc.getElementsByTagName("Agent").item(0).getFirstChild().getTextContent() );
			int power = ((int)Float.parseFloat(doc.getElementsByTagName("Agent").item(0).getLastChild().getTextContent() )) ;
			gridView.getCellGrid().setStartingPosition(position);
			gridView.getCellGrid().setStartingPower(power);
			
			Agent agent = new Agent();
			agent.setPos(position);
			agent.setHealth(power);
			gridView.getCellGrid().setAgent(agent);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void saveLayoutXML(CellGrid gridCell, String filePath, Agent singleAgent) {
		DocumentBuilderFactory factory;
		DocumentBuilder builder = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();

			Document doc = builder.newDocument();

			// Append root node as Mazecell
			Element rootElement = doc.createElement("GridCell");
			doc.appendChild(rootElement);

			// Save the Generic Properties
			Element mazeProperties = doc.createElement("MazeProperties");
			rootElement.appendChild(mazeProperties);
			
			// Saving rows
			Element rows = doc.createElement("Rows");
			rows.appendChild(doc.createTextNode(gridCell.getRows() + ""));
			mazeProperties.appendChild(rows);

			Element cols = doc.createElement("Cols");
			cols.appendChild(doc.createTextNode(gridCell.getCols() + ""));
			mazeProperties.appendChild(cols);
			
			Element sensorType = doc.createElement("SensorType");
			sensorType.appendChild(doc.createTextNode(gridCell.getSensorType().toString() ));
			mazeProperties.appendChild(sensorType);
			
			
			Element actionMapType = doc.createElement("ActionMapType");
			actionMapType.appendChild(doc.createTextNode(gridCell.getActionMapType().toString() ));
			mazeProperties.appendChild(actionMapType);


			// Adding Agent information
			Element agentInfo = doc.createElement("Agent");
			rootElement.appendChild(agentInfo);

			Element pos = doc.createElement("Position");
			pos.appendChild(doc.createTextNode(singleAgent.getPos() + ""));
			agentInfo.appendChild(pos);

			Element power = doc.createElement("Power");
			power.appendChild(doc.createTextNode(singleAgent.getHealth() + ""));
			agentInfo.appendChild(power);

			// Append Rectangle Properties
			Element rectangle = doc.createElement("Rectangle");
			rootElement.appendChild(rectangle);

			for (int i = 0; i < gridCell.getBoundingRectangle().length; i++) {
				Element rowNo = doc.createElement("Row_" + i);

				Element recX = doc.createElement("recX");
				recX.appendChild(doc.createTextNode(gridCell.getBoundingRectangle()[i].x + ""));

				Element recY = doc.createElement("recY");
				recY.appendChild(doc.createTextNode(gridCell.getBoundingRectangle()[i].y + ""));

				Element recW = doc.createElement("recW");
				recW.appendChild(doc.createTextNode(gridCell.getBoundingRectangle()[i].width + ""));

				Element recH = doc.createElement("recH");
				recH.appendChild(doc.createTextNode(gridCell.getBoundingRectangle()[i].height + ""));

				rowNo.appendChild(recX);
				rowNo.appendChild(recY);
				rowNo.appendChild(recW);
				rowNo.appendChild(recH);
				rectangle.appendChild(rowNo);
			}

			// Append Properties of Rewards
			Element reward = doc.createElement("Reward");
			rootElement.appendChild(reward);

			for (Integer i : gridCell.getRewards().keySet()) {
				Element rewardType = doc.createElement("RewardType");
				Element rewardValue = doc.createElement("RewardValue");
				Element rowNo = doc.createElement("Row_" + i);
				rowNo.appendChild(rewardType);
				rowNo.appendChild(rewardValue);
				rewardType.appendChild(doc.createTextNode(gridCell.getRewards().get(i).getType()));
				rewardValue.appendChild(doc.createTextNode(gridCell.getRewards().get(i).getValue()+""));
				reward.appendChild(rowNo);
			}
			
			
			//save cell events to file
			Element cellEvents = doc.createElement("CellEvents");
			for (Integer i : gridCell.getCellEvents().keySet()) {
				
				Element rowNo = doc.createElement("Row_" + i);
				//returns a hashmap which we can iterate through to get all events
				Iterator<String> eventIter = gridCell.getCellEvents().get(i).iterator();
				int count =0;
				for (String event: gridCell.getCellEvents().get(i)) {
					Element cellEvent = doc.createElement("CellEvent_"+count);
					cellEvent.appendChild( doc.createTextNode(event ) );
					rowNo.appendChild(cellEvent);
					count++;
				}
				cellEvents.appendChild(rowNo);
			}
			rootElement.appendChild( cellEvents );
			
			
			//save cell types/props to file
			Element cellTypes = doc.createElement("CellTypes");
			for (Integer i : gridCell.getCellProperties().keySet()) {
				
				Element rowNo = doc.createElement("Row_" + i);
				//returns a hashmap which we can iterate through to get all events
				Iterator<String> eventIter = gridCell.getCellProperties().get(i).iterator();
				int count =0;
				for (String event: gridCell.getCellProperties().get(i)) {
					Element cellType = doc.createElement("CellType_"+count);
					cellType.appendChild( doc.createTextNode(event ) );
					rowNo.appendChild(cellType);
					count++;
				}
				cellTypes.appendChild(rowNo);
			}
			rootElement.appendChild( cellTypes );
			

			// Append Properties of Visibility
			Element visibility = doc.createElement("Visibility");
			rootElement.appendChild(visibility);

			for (int i = 0; i < gridCell.getBoundingRectangle().length; i++) {
				Element rowNo = doc.createElement("Row_" + i);
				rowNo.appendChild(doc.createTextNode(gridCell.getVisibility()[i] + ""));
				visibility.appendChild(rowNo);
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			transformer.transform(source, result);
			log.info("File saved!");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}
