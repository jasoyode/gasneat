package experiment_builder.sensor_models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;
import experiment_builder.model.Reward;

public class EachCellPlusFoodSensor implements SensorImpl {

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( EachCellPlusFoodSensor.class );
	private CellGrid cellGrid;
	private int numberOfCells;
	
	public EachCellPlusFoodSensor(CellGrid cellGrid) {
		this.cellGrid= cellGrid;
	}
	
	@Override
	public double[] getSensorData() {
		//load references for shorthand
		Agent agent = cellGrid.getAgent();
		ArrayList<Integer> finalizedMazeCells = cellGrid.getFinalizedMazeCells();
		HashMap<Integer, Reward> reward  = cellGrid.getRewards();
		HashMap<Integer, HashSet<String>>  cellProperties = cellGrid.getCellProperties(); 
		numberOfCells = 0;
		
		//only count the cells that are visible, aka accessible
		for (int cellPosition: finalizedMazeCells) {
			if ( cellGrid.getVisibility()[cellPosition] ) {
				numberOfCells++;
			}
		}
				
		
		
		
		//add bias node, add 1 reward sensor
		double[] sensorData = new double[ numberOfCells + 2 ];
		
		//last node is bias node
		sensorData[numberOfCells] = 1.0;
		
		int agentPosition = agent.getPos();
		int sensorIndex = 0;
		
		//Positions are integer values (which might not line up with 0-99etc)
		for (Integer cellPosition: finalizedMazeCells) {
			
			//if the agent is in cell, sensor is 1.0 otherwise its 0.0
			if (agentPosition == cellPosition) {
				
				logger.trace("agent at position: "+   cellPosition  );
				sensorData[sensorIndex] = 1.0;
				sensorIndex++;
				
			} else if (cellGrid.getVisibility()[cellPosition]){
				
				logger.trace("agent not at position: "+   cellPosition  );
				sensorData[sensorIndex] = 0.0;
				
				sensorIndex++;
			} else {
				//we do not want to increment sensorIndex because it is not included
				
			}
		}
		
		/*  - might want to include this, not sure!
		logger.trace("cellProperties: "+   cellProperties  );
		if (cellProperties.containsKey(agentPosition)) {
			logger.trace("cellProperties contains "+   agentPosition  );
		}
		*/
		
		//REWARD FOOD
		if ( reward.containsKey( agentPosition )   ) {
			
			if (reward.get(agentPosition).getType().equals("FOOD"  )   ) {
				sensorData[numberOfCells+1] = reward.get(agentPosition ).getValue()/10.0 ;
			}
		}
		return sensorData;
		
	}

	@Override
	public String getFormattedSensorData() {
		double[] data = getSensorData();
		StringBuilder formattedData = new StringBuilder( "<html>EachCellSensor<br>" );
		for (int i=0; i < numberOfCells+1; i++) {
			formattedData.append("Cell["+i+"]: " + data[i] + "<br>");
		}
		formattedData.append( "</html>" );
		return formattedData.toString();
	}

}
