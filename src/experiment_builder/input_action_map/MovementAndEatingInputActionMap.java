package experiment_builder.input_action_map;

import experiment_builder.controller.AgentActions;
import experiment_builder.model.CellGrid;

public class MovementAndEatingInputActionMap implements InputActionMappable {

	
	public MovementAndEatingInputActionMap(CellGrid cg ) {
		
	}
	
	
	@Override
	public void actFromDoubleValue(double d) {
		
		if ( d >= 0 && d < 0.2  ) {
			AgentActions.eat();
		} else if ( d >= 0.2 && d < 0.4  ) {
			AgentActions.moveDirection("UP");
		} else  if ( d >= 0.4 &&  d < 0.6  ) {
			AgentActions.moveDirection("LEFT");
		} else if ( d >= 0.6 &&  d < 0.8  ) {
			AgentActions.moveDirection("RIGHT");
		} else if ( d >= 0.8 && d <= 1.0) {
			AgentActions.moveDirection("DOWN");
		} else {
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
		
		switch (bestIndex) 
		{
			case 0:
				AgentActions.eat();
				break;
			case 1:
				AgentActions.moveDirection("UP");
				break;
			case 2:
				AgentActions.moveDirection("LEFT");
				break;
			case 3:
				AgentActions.moveDirection("RIGHT");
				break;
			case 4:
				AgentActions.moveDirection("DOWN");
				break;
			default:
				System.err.println("Invalid input! Exiting");
				System.exit(1);
		}
	}
	
	@Override
	public String stringActionFromDoubleValue(double d) {
		
		if ( d >= 0 && d < 0.2  ) {
			return "EAT";
		} else if ( d >= 0.2 && d < 0.4  ) {
			return "UP";
		} else  if ( d >= 0.4 &&  d < 0.6  ) {
			return "LEFT";
		} else if ( d >= 0.6 &&  d < 0.8  ) {
			return "RIGHT";
		} else if ( d >= 0.8 && d <= 1.0) {
			return "DOWN";
		} else {
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
		
		switch (bestIndex) 
		{
			case 0:
				return "EAT";
			case 1:
				return "UP";
			case 2:
				return "DOWN";
			case 3:
				return "LEFT";
			case 4:
				return "RIGHT";
			default:
				System.err.println("BestIndex="+bestIndex);
				System.err.println("Invalid input! Exiting");
				System.exit(1);
		}
		
		return "ERROR";

	}
	
	
	
	
	
	
	
	
	

}
