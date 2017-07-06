package experiment_builder.input_action_map;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.controller.AgentActions;
import experiment_builder.model.CellGrid;
import gasNEAT.controller.RecurrentSimulator;

public class SoltoggioTanhInputActionMap implements InputActionMappable {

	private static Logger logger = Logger.getLogger( SoltoggioTanhInputActionMap.class );
	
	//private static Logger logger = Logger.getLogger( RecurrentSimulator.class );
	
	private CellGrid cellGrid;
	
	
	final static double UPPER_LIMIT = 0.333;
	final static double LOWER_LIMIT = -0.333;
	

	public SoltoggioTanhInputActionMap(CellGrid cg) {
		cellGrid = cg;
	}
	
	@Override
	public void actFromDoubleValue(double d) {
		
		
		if ( d >= -1 && d < LOWER_LIMIT  ) {
			if (logger.isDebugEnabled() ) {
				logger.debug("Double value: " + d + " mapped to action TURN LEFT");
			}
			turnLeft();
		} else if ( d >= LOWER_LIMIT && d < UPPER_LIMIT  ) {
			if (logger.isDebugEnabled() ) {
				logger.debug("Double value: " + d + " mapped to action GO FORWARD");
			}
			goForward();
		} else  if ( d >= UPPER_LIMIT &&  d <= 1.0  ) {
			if (logger.isDebugEnabled() ) {
				logger.debug("Double value: " + d + " mapped to action TURN RIGHT");
			}
			turnRight();
		} else {
			System.err.println("Invalid input! Exiting");
			System.err.println("d: " + d);
			System.exit(1);
		}

	}

	private void turnRight() {
		
		if ( cellGrid.getAgent().validOrientation() ) 
		{
			if ( cellGrid.getAgent().getXOrientation() == 0) {
				if (cellGrid.getAgent().getYOrientation() == 1) {
					//WAS DOWN change to LEFT
					cellGrid.getAgent().setYOrientation(0);
					cellGrid.getAgent().setXOrientation(-1);
					AgentActions.moveDirection("LEFT");
				} else {
					//WAS UP change to RIGHT
					cellGrid.getAgent().setYOrientation(0);
					cellGrid.getAgent().setXOrientation(1);
					AgentActions.moveDirection("RIGHT");
				}
			} else {
				if (cellGrid.getAgent().getXOrientation() == 1) {
					//WAS RIGHT change to down
					cellGrid.getAgent().setYOrientation(1);
					cellGrid.getAgent().setXOrientation(0);
					AgentActions.moveDirection("DOWN");
					
				} else {
					//WAS LEFT change to UP
					cellGrid.getAgent().setYOrientation(-1);
					cellGrid.getAgent().setXOrientation(0);
					AgentActions.moveDirection("UP");
				}
			}
			
		} else {
			System.err.println("Logic error invalied orientation");
			System.exit(1);
		}
		
	}


	private void goForward() {
		
		if ( cellGrid.getAgent().validOrientation() ) 
		{
			if ( cellGrid.getAgent().getXOrientation() == 0) {
				if (cellGrid.getAgent().getYOrientation() == 1) {
					AgentActions.moveDirection("DOWN");
				} else {
					AgentActions.moveDirection("UP");
				}
			} else {
				if (cellGrid.getAgent().getXOrientation() == 1) {
					AgentActions.moveDirection("RIGHT");
				} else {
					AgentActions.moveDirection("LEFT");
				}
			}
			
		} else {
			System.err.println("Logic error invalied orientation");
			System.exit(1);
		}
	}


	private void turnLeft() {

		if ( cellGrid.getAgent().validOrientation() ) 
		{
			if ( cellGrid.getAgent().getXOrientation() == 0) {
				if (cellGrid.getAgent().getYOrientation() == 1) {
					//WAS DOWN change to RIGHT
					cellGrid.getAgent().setYOrientation(0);
					cellGrid.getAgent().setXOrientation(1);
					AgentActions.moveDirection("RIGHT");
					
				} else {
					//WAS UP change to LEFT
					cellGrid.getAgent().setYOrientation(0);
					cellGrid.getAgent().setXOrientation(-1);
					AgentActions.moveDirection("LEFT");
				}
			} else {
				if (cellGrid.getAgent().getXOrientation() == 1) {
					//WAS RIGHT change to UP
					cellGrid.getAgent().setYOrientation(-1);
					cellGrid.getAgent().setXOrientation(0);
					AgentActions.moveDirection("UP");
					
				} else {
					//WAS LEFT change to UP
					cellGrid.getAgent().setYOrientation(1);
					cellGrid.getAgent().setXOrientation(0);
					AgentActions.moveDirection("DOWN");
				}
			}
			
		} else {
			System.err.println("Logic error invalied orientation");
			System.exit(1);
		}
		
	}


	@Override
	public void actFromDoubleArrayValue(double[] array) {
		
		int bestIndex = -1;
		double maxValue = -1;
		for (int i=0; i < array.length; i++) {
			if (maxValue < array[i]) {
				maxValue = array[i];
				bestIndex = i;
			}
		}
		switch (bestIndex) 
		{
			case 0:
				turnLeft();
				break;
			case 1:
				goForward();
				break;
			case 2:
				turnRight();
				break;
			default:
				System.err.println("Invalid input! Exiting");
				System.out.println("bestIndex="+ bestIndex);
				System.exit(1);
		}
	}
	
	@Override
	public String stringActionFromDoubleValue(double d) {
		
		if ( d >= -1 && d < LOWER_LIMIT  ) {
			return "TURN LEFT";
		} else if ( d >= LOWER_LIMIT && d < UPPER_LIMIT  ) {
			return "FORWARD";
		} else  if ( d >= UPPER_LIMIT &&  d <= 1.0  ) {
			return "TURN RIGHT";
		} else {
			System.err.println("Invalid input! Exiting");
			System.err.println("d: " + d);
			System.exit(1);
		}
		return "ERROR";

	}

	@Override
	public String stringActionFromDoubleArrayValue(double[] array) {
		
		int bestIndex = -1;
		double maxValue = -1;
		for (int i=0; i < array.length; i++) {
			if (maxValue < array[i]) {
				maxValue = array[i];
				bestIndex = i;
			}
		}
		switch (bestIndex) 
		{
			case 0:
				return "TURN LEFT";
			case 1:
				return "GO FORWARD";
			case 2:
				return "TURN RIGHT";
			default:
				System.err.println("Invalid input! Exiting");
				System.exit(1);
		}
		return "ERROR";
	}

}
