package experiment_builder.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.anji.util.Randomizer;

import org.apache.log4j.Logger;

import experiment_builder.input_action_map.InputActionMapFactory;
import experiment_builder.input_action_map.InputActionMappable;
import lombok.Getter;
import lombok.Setter;
import experiment_builder.sensor_models.SensorFactory;
import experiment_builder.sensor_models.SensorImpl;

public class CellGrid {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( CellGrid.class );
	
	private @Getter @Setter boolean activeInExperiment = false;
	private @Getter @Setter HashMap<Integer, HashSet<String>> cellEvents;
	private @Getter @Setter HashMap<Integer, HashSet<String>> cellProperties;
	private @Getter @Setter Rectangle[] boundingRectangle;
	private @Getter @Setter int rows, cols;
	private @Getter @Setter Agent agent;
	private @Getter @Setter ArrayList<Integer> finalizedMazeCells;
	private @Getter @Setter boolean[] visibility;
		
	private @Getter @Setter HashMap<Integer, Reward> startingRewards;
	private @Getter @Setter HashMap<Integer, Reward> rewards;
	
	private @Getter @Setter HashMap<Integer, Reward> backupRewards;
	
	private @Getter @Setter int startingPosition;
	private @Getter @Setter int startingPower;	
	
	//gets created when called the first time
	private SensorImpl sensor;
	private @Getter InputActionMappable actionMap;
	
	//used to set the 
	private @Getter String sensorType;
	private @Getter String actionMapType;
	
	private @Getter int trialNumber = 0;
	
	private @Getter boolean isTrialOver = false;
	
	private Randomizer randomizer;
	
	public CellGrid(){
		
	}
	
	//TODO why do we have both of these??
	public CellGrid(int total, String sensorName, String actionMapType) {
		finalizedMazeCells = new ArrayList<Integer>();
		agent = new Agent();
		boundingRectangle = new Rectangle[total];
		rewards = new HashMap<>();
		visibility = new boolean[total];
		Arrays.fill(visibility, false);
		
		this.sensorType = sensorType;
		sensor = SensorFactory.createSensorImpl(sensorName, this);
		
		this.actionMapType = actionMapType;
		actionMap = InputActionMapFactory.createInputActionMap(actionMapType, this);

	}

	public CellGrid(int rows, int cols, String sensorType, String actionMapType) {
		finalizedMazeCells = new ArrayList<Integer>();
		this.rows = rows;
		this.cols = cols;
		cellProperties = new HashMap<Integer, HashSet<String>>();
		cellEvents = new HashMap<Integer, HashSet<String>>();
		boundingRectangle = new Rectangle[rows * cols];
		rewards = new HashMap<>();
		visibility = new boolean[rows * cols];
		Arrays.fill(visibility, false);
		agent = new Agent();
		
		//setSensorFromString(sensorType);
		
		this.sensorType = sensorType;
		sensor = SensorFactory.createSensorImpl(sensorType, this);
		
		
		this.actionMapType = actionMapType;
		actionMap = InputActionMapFactory.createInputActionMap(actionMapType, this);
	}
	
	public void temporarilyRemoveRewards() {
		
		if ( rewards.size() != 0) {
			
			backupRewards = new HashMap<Integer, Reward>(); 
			for (Map.Entry<Integer, Reward> e : rewards.entrySet()) {
				backupRewards.put( e.getKey() , e.getValue().deepClone() );
			}
			logger.info("Reward copied");
			rewards.clear();
			logger.info("Reward removed");
			
		} else {
			logger.info("No reward on map, cannot temporarily remove!");
		}
	}
	
	public void replenishRemovedRewards() {
		if ( rewards.size() == 0) {
			
			rewards = new HashMap<Integer, Reward>(); 
			for (Map.Entry<Integer, Reward> e : backupRewards.entrySet()) 
			{
				rewards.put( e.getKey() , e.getValue().deepClone() );
			}
			logger.info("BackupReward copied");
			backupRewards.clear();
			logger.info("BackupReward removed");
			
		} else {
			logger.info("Reward on map, cannot replenish!");
		}
	}
	
	
	
	
	
	public void setDuplicateValues(CellGrid cg) {
		
		setCellEvents(cg.getCellEvents());
		setCellProperties(cg.getCellProperties());
		setRewards(cg.getRewards() );
		setRows(cg.getRows());
		setCols(cg.getCols() );
		setVisibility( cg.getVisibility() );
		setBoundingRectangle(cg.getBoundingRectangle() );
		setAgent(cg.getAgent() );
		setStartingPosition(cg.getStartingPosition());
		setStartingPower(cg.getStartingPower());
		setStartingRewards(cg.getStartingRewards());

		/////////should work
		
		setSensorFromString( cg.getSensorType()  );
		setInputActionMapFromString( cg.getActionMapType()  );
		
	}
	
	
	public void setSensorFromString(String sensorType) {
		this.sensorType = sensorType;
		
		sensor = SensorFactory.createSensorImpl(sensorType, this);
	}


	public void setFinalizedMazeCellFromVisibility() {
		
		finalizedMazeCells = new ArrayList<Integer>();

		
		for (int i=0; i < visibility.length; i++ ) {
			if (visibility[i]) {
				finalizedMazeCells.add(i);
			}
		}
		
	}




	public double[] getSensorData() {
		
		if (sensor == null) {
			sensor = SensorFactory.createSensorImpl(sensorType, this);
		}
		return sensor.getSensorData();
	
	}

	public void restartTrialCount() {
		trialNumber=0;
	}

	//reset the important things that may change during an experiment
	public void restartExperiment() {
		
		isTrialOver = false;
		
		//this can be used to know
		trialNumber++;
		
		
		rewards = new HashMap<Integer, Reward>();
		
		for (Map.Entry<Integer, Reward> entry: startingRewards.entrySet()) {
			rewards.put( entry.getKey(), entry.getValue() );			
		}
		
		agent.setPos( startingPosition );
		agent.setHealth( startingPower );
		
		
	}




	public String getFormattedSensorData() {
		
		if (sensor == null) {
			sensor = SensorFactory.createSensorImpl(sensorType, this);
		}
		return sensor.getFormattedSensorData();

	}

	public void setInputActionMapFromString(String actionMapType) {
		
		this.actionMapType = actionMapType;
		actionMap = InputActionMapFactory.createInputActionMap(actionMapType, this);
		
		
	}

	public void endExperiment() {
		
		isTrialOver = true;
		logger.debug("isTrialOver set to true!");
		
	}

	public void setRandomizer(Randomizer randomizer) {
		this.randomizer =  randomizer;
	}

	public Randomizer getRandomizer() {
		return randomizer;
	}


}

