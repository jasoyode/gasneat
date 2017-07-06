package experiment_builder.sensor_models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;
import experiment_builder.model.Reward;

public class SoltoggioSensor implements SensorImpl {

	private static Logger logger = Logger.getLogger( SoltoggioSensor.class );
	
	private CellGrid cellGrid;
	
	public SoltoggioSensor(CellGrid cellGrid) {
		
		this.cellGrid= cellGrid;
	}
	
	@Override
	public double[] getSensorData() {
		
		double[] sensorData = new double[4];
		
		//bias node
		sensorData[3] = 1.0;
		
		//load references for shorthand
		Agent agent = cellGrid.getAgent();
		ArrayList<Integer> finalizedMazeCells = cellGrid.getFinalizedMazeCells();
		HashMap<Integer, Reward> reward  = cellGrid.getRewards();
		HashMap<Integer, HashSet<String>>  cellProperties = cellGrid.getCellProperties(); 
		
		int position = agent.getPos();
		
		logger.debug("cellProperties: "+   cellProperties  );

		if (cellProperties.containsKey(position)) {
			
			logger.debug("cellProperties contains "+   position  );
			
			//MAZE-END
			if ( cellProperties.get(position).contains( "Home" )  ) {
				sensorData[0] = 1.0;
				logger.debug("HOME SPACE!");
			}
			
			if ( cellProperties.get(position).contains( "Turn" )  ) {
				sensorData[1] = 1.0;
				logger.debug("TURN SPACE!");
			}
			
			if ( cellProperties.get(position).contains( "Maze-End" )  ) {
				sensorData[2] = 1.0;
				logger.debug("MAZE-END SPACE!");
			}
		}
		//REWARD FOOD
		if ( reward.containsKey( position )   ) {
			
			if (reward.get(position).getType().equals("FOOD"  )   ) {
				sensorData[3] = reward.get(position ).getValue()/10.0 ;
			}
		}
		return sensorData;
		
	}

	@Override
	public String getFormattedSensorData() {
		
		double[] data = getSensorData();
		
		String formattedData = "<html>SoltoggioSensor<br>" +
				"HOME: "+data[0]+"<br>" +
				"TURN: "+data[1]+"<br>" +
				"MAZE-END: "+data[2]+"<br>" +
				"REWARD: "+data[3]+"<br>";
		
		return formattedData;
	}

}
