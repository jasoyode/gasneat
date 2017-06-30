package experiment_builder.constants;

import java.lang.Thread.State;
import java.util.Arrays;


public class ConstantsMapping {

	
	public static enum SENSOR_TYPES {
		SOLTOGGIO_SENSOR, NAVIGATION_AND_RESOURCE_SENSOR, EACH_CELL_PLUS_FOOD
	}
	
	public static enum ACTION_MAPPER_TYPES {
		SOLTOGGIO_ACTION_MAP, MOVEMENT_AND_EATING_ACTION_MAP, MOVEMENT_ONLY_ACTION_MAP, MOVEMENT_EAT_REST_ACTION_MAP
	}

	
	public static String[] names() {
	    return Arrays.toString(State.values()).replaceAll("^.|.$", "").split(", ");
	}
	
	
	/*
	public static final HashMap<String, SENSOR_TYPES> SENSOR_MAPPING;
	static  {
		SENSOR_MAPPING = new HashMap<String, SENSOR_TYPES>();
		SENSOR_MAPPING.put("SOLTOGGIO_SENSOR", SENSOR_TYPES.SOLTOGGIO_SENSOR );
		SENSOR_MAPPING.put("NAVIGATION_AND_RESOURCE_SENSOR", SENSOR_TYPES.NAVIGATION_AND_RESOURCE_SENSOR );
	}
	*/
	
}
