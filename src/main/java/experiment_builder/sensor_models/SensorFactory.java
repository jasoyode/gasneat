package experiment_builder.sensor_models;

import experiment_builder.model.CellGrid;

public class SensorFactory {
	
	public static SensorImpl createSensorImpl(String sensorType, CellGrid cg) {
		
		switch (sensorType) 
		{
			case "SOLTOGGIO_SENSOR":
				return new SoltoggioSensor(cg);
				
			case "EACH_CELL_PLUS_FOOD":
				return new EachCellPlusFoodSensor(cg);
				
			case "NAVIGATION_AND_RESOURCE_SENSOR":
				return new NavigationAndResourcesSensor(cg);
				
			default:
				System.err.println("INVALID SENSORTYPE SPECIFIED");
				System.out.println("VALUE: "+  sensorType  );
				System.exit(1);
				return null;		
		}
		
	}

}
