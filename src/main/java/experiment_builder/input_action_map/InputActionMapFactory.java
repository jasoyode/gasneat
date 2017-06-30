package experiment_builder.input_action_map;

import experiment_builder.model.CellGrid;

public class InputActionMapFactory {
	
	public static InputActionMappable createInputActionMap(String actionMapType, CellGrid cg) {
		
		switch (actionMapType) 
		{
			case "SOLTOGGIO_ACTION_MAP":
				return new SoltoggioTanhInputActionMap( cg );
				//return new SoltoggioInputActionMap( cg );
				
			case "MOVEMENT_AND_EATING_ACTION_MAP":
				return new MovementAndEatingInputActionMap( cg );
			
			case "MOVEMENT_ONLY_ACTION_MAP":
				return new MovementOnlyInputActionMap( cg );
				
			case "MOVEMENT_EAT_REST_ACTION_MAP":
				return new MovementEatRestInputActionMap(cg);
				
			default:
				System.err.println("INVALID ACTIONMAP SPECIFIED");
				System.out.println("VALUE: "+  actionMapType  );
				System.exit(1);
				return null;		
		}
		
	}

}
