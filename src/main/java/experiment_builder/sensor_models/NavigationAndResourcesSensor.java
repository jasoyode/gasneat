package experiment_builder.sensor_models;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.controller.AgentActions;
import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;
import experiment_builder.model.Reward;

public class NavigationAndResourcesSensor implements SensorImpl {

	private static Logger logger = Logger.getLogger( NavigationAndResourcesSensor.class );
	
	private CellGrid cellGrid;
	
	public NavigationAndResourcesSensor(CellGrid cellGrid) {
		
		this.cellGrid= cellGrid;
	}
	
	@Override
	public double[] getSensorData() {
		
		double[] sensorData = new double[6];
		
		//load references for shorthand
		Agent agent = cellGrid.getAgent();
		ArrayList<Integer> finalizedMazeCells = cellGrid.getFinalizedMazeCells();
		HashMap<Integer, Reward> reward  = cellGrid.getRewards();
		
		int position = agent.getPos();
		
		//TODO should really store in CellGrid instead of AgentActions fix later
		int mazeWidth = AgentActions.getMazeWidth();
		
		//UP
		if (finalizedMazeCells.contains(  position - mazeWidth  )  ) {
			sensorData[0] = 1.0;
			logger.debug("UP IS OPEN!");
		}
		//DOWN
		if (finalizedMazeCells.contains(  position + mazeWidth  )  ) {
			sensorData[1] = 1.0;
			logger.debug("DOWN IS OPEN!");
		}
		//LEFT
		if (finalizedMazeCells.contains(  position - 1  ) && (position % mazeWidth != 0)  ) {
			sensorData[2] = 1.0;
			logger.debug("LEFT IS OPEN!");
		}
		//RIGHT
		if (finalizedMazeCells.contains(  position + 1  ) && (position + 1) % mazeWidth != 0 ) {
			sensorData[3] = 1.0;
			logger.debug("RIGHT IS OPEN");
		}
		
		//REWARD
		if ( reward.containsKey( position )   ) {
			
			if (reward.get(position).getType().equals("FOOD"  )   ) {
				sensorData[4] = reward.get(position ).getValue()/10.0 ;
			}
			
			if (reward.get(position).getType().equals("WATER"  )   ) {
				sensorData[5] = reward.get(position ).getValue()/10.0 ;
			}
		}
		
		
		return sensorData;//new double[]{0.1,0.2};

	}

	@Override
	public String getFormattedSensorData() {
		double[] data = getSensorData();
		
		String formattedData = "<html>NavigationAndResourcesSensor<br>" +
				"UP: "+data[0]+"<br>" +
				"DOWN: "+data[1]+"<br>" +
				"LEFT: "+data[2]+"<br>" +
				"RIGHT: "+data[3]+"<br>"+
				"FOOD: "+data[4]+"<br>" +
				"WATER: "+data[5]+"<br>";
		
		return formattedData;
	}

}
