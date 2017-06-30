package experiment_builder.input_action_map;

import experiment_builder.controller.AgentActions;
import experiment_builder.model.CellGrid;

public class MovementOnlyInputActionMap implements InputActionMappable {

	
	public MovementOnlyInputActionMap(CellGrid cg ) {
		
	}
	
	
	@Override
	public void actFromDoubleValue(double d) {
		
		if ( d >= 0 && d < 0.25  ) {
			AgentActions.moveDirection("UP");
		} else  if ( d >= 0.25 &&  d < 0.5  ) {
			AgentActions.moveDirection("LEFT");
		} else if ( d >= 0.5 &&  d < 0.75  ) {
			AgentActions.moveDirection("RIGHT");
		} else if ( d >= 0.75 && d <= 1.0) {
			AgentActions.moveDirection("DOWN");
		} else {
			System.err.println("d="+d);
			System.err.println("Invalid input! Exiting");
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
		
		switch (bestIndex) {
			case 0:
				AgentActions.moveDirection("UP");
				break;
			case 1:
				AgentActions.moveDirection("LEFT");
				break;
			case 2:
				AgentActions.moveDirection("RIGHT");
				break;
			case 3:
				AgentActions.moveDirection("DOWN");
				break;
			default:
				System.err.println("BestIndex="+bestIndex);
				System.err.println("Invalid input! Exiting");
				System.exit(1);
		}
			

	}
	
	
	@Override
	public String stringActionFromDoubleValue(double d) {
		
		if ( d >= 0 && d < 0.25  ) {
			return "UP";
		} else  if ( d >= 0.25 &&  d < 0.5  ) {
			return "LEFT";
		} else if ( d >= 0.5 &&  d < 0.75  ) {
			return "RIGHT";
		} else if ( d >= 0.75 && d <= 1.0) {
			return "DOWN";
		} else {
			System.err.println("d="+d);
			System.err.println("Invalid input! Exiting");
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
		
		switch (bestIndex) {
			case 0:
				return "UP";
			case 1:
				return "LEFT";
			case 2:
				return "RIGHT";
			case 3:
				return "DOWN";
			default:
				System.err.println("BestIndex="+bestIndex);
				System.err.println("Invalid input! Exiting");
				System.exit(1);
		}
		
		return "ERROR";

	}
	
}
