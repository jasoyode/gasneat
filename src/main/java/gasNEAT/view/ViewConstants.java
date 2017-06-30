package gasNEAT.view;

import java.awt.Color;
import java.util.HashMap;

public class ViewConstants {
	
	public static final int SCALING_FACTOR = 4;
	public static final int CONTROL_POINT_SCALE = 5;
	
	public static final int STANDARD_MAJOR_SPACING = 1000; 
	public static final int STANDARD_MINOR_SPACING = 500; 
	
	public static final String NULL_TEXT = "";
	public static final String NULL_STATUS = "Status not found!";
	
	public static final String PAUSE_TEXT = "Pause";
	public static final String PLAY_TEXT = "Play";
	public static final String PLAY_STATUS_TEXT = "You are in Play Mode ...";
	
	public static final String REPLAY_TEXT = "Replay";
	public static final String REPLAY_STATUS_TEXT = "You are in Replay Mode ..";
	
	public static final String BACKSTEP_TEXT = "Back Step";
	public static final String BACKSTEP_STATUS_TEXT = "You are stepping back in the simulation ..";
	
	public static final String FORWARDSTEP_TEXT = "Forward Step";
	public static final String FORWARDSTEP_STATUS_TEXT = "You are stepping forward in the simulation ..";
	
	public static final String LOG4J_FILE = "log4j2.xml";
	public static final String LOG4J_TEXT = "test";
	
	public static final HashMap<String, String> STATUS_TEXT_MAPPING;
	static {
		STATUS_TEXT_MAPPING = new HashMap<String,String>();
		ViewConstants.STATUS_TEXT_MAPPING.put(ViewConstants.PLAY_TEXT, ViewConstants.PLAY_STATUS_TEXT);
		ViewConstants.STATUS_TEXT_MAPPING.put(ViewConstants.REPLAY_TEXT, ViewConstants.REPLAY_STATUS_TEXT);
		ViewConstants.STATUS_TEXT_MAPPING.put(ViewConstants.BACKSTEP_TEXT, ViewConstants.BACKSTEP_STATUS_TEXT);
		ViewConstants.STATUS_TEXT_MAPPING.put(ViewConstants.FORWARDSTEP_TEXT, ViewConstants.FORWARDSTEP_STATUS_TEXT);
	}
	// These are used for headless mode.  If you want to add more parameters later, modify this hashmap!
	public static final String MODE_PARAM_TEXT = "mode";
	public static final String SIMULATION_TEXT = "simulation";
	public static final String SANDBOX_TEXT = "sandbox";
	public static final String EVOLUTION_TEXT = "evolution";
	public static final String FEED_FORWARD_TEXT = "feed-forward network";
	public static final String RECURRENT_NETWORK_TEXT = "Recurrent network";
	
	public static final String RUN_TYPE_HEADLESS = "headless";
	public static final String RUN_TYPE_GUI = "GUI";
	
	public static final Color POSITIVE_SYNAPTIC_MODULATION = Color.GREEN;
	public static final Color NEGATIVE_SYNAPTIC_MODULATION = Color.RED;
	public static final Color POSITIVE_ACTIVATION_MODULATION = Color.ORANGE;
	public static final Color NEGATIVE_ACTIVATION_MODULATION = Color.YELLOW;
	
	
}
