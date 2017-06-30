package gasNEAT.view;

import java.util.HashMap;

import gasNEAT.command.functions.GasNeatActivationFunction;
import gasNEAT.command.functions.LinearActivationFunction;
import gasNEAT.command.functions.NullActivationFunction;
import gasNEAT.command.functions.SigmoidActivationFunction;
import gasNEAT.command.functions.StepActivationFunction;

public class Constants {
	

	public enum ActivationThresholdFunction {
		STEP_FUNCTION, NULL_FUNCTION, LOGARITHMIC_SIGMOID, LINEAR;
	}
	
	public static final HashMap<ActivationThresholdFunction, GasNeatActivationFunction> ACTIVATION_FUNCTION_MAP;
	static {
		ACTIVATION_FUNCTION_MAP = new HashMap<ActivationThresholdFunction, GasNeatActivationFunction>();
		ACTIVATION_FUNCTION_MAP.put(ActivationThresholdFunction.STEP_FUNCTION, new StepActivationFunction());
		ACTIVATION_FUNCTION_MAP.put(ActivationThresholdFunction.LOGARITHMIC_SIGMOID, new SigmoidActivationFunction());
		ACTIVATION_FUNCTION_MAP.put(ActivationThresholdFunction.NULL_FUNCTION, new NullActivationFunction());
		ACTIVATION_FUNCTION_MAP.put(ActivationThresholdFunction.LINEAR, new LinearActivationFunction());
	}
	
	public enum ModFunctionTarget{
		ACTIVATION, PLASTICITY;
	}

	
	///////////Controller///////////////////////
	public enum NetworkType	{
		FEEDFORWARD, RECURRENT;
	}

	public enum FeedForwardNetworkMode	{
		TIMED, NOT_TIMED;
	}
	
	
	
	////////////////View////////////////////
	public static final int FRAME_WIDTH = 500;
	public static final int FRAME_HEIGHT = 400;
	public static final int BUTTON_PANEL_HEIGHT = 90;
	
	public static final int SIMULATION_FRAME_WIDTH = 1200;
	public static final int SIMULATION_FRAME_HEIGHT = 700;
	

	public static final double EDGE_WIDTH_FACTOR = 1;
	public static final double EDGE_ARC_CURVATURE = 1;
	public static final double EDGE_ARROW_HEAD_WIDTH = 1;
	public static final double EDGE_ARROW_HEAD_HEIGHT = 1;
	
	public static final float GAS_BOUNDARY_THICKNESS = 1;
	public static final float GAS_RING_WIDTH_PARAMETER = 8;
	
	public static final float GAS_LEGEND_BOUNDARY_THICKNESS = 2;
	public static final int GAS_LEGEND_SWATCH_WIDTH = 30;
	public static final int GAS_LEGEND_SWATCH_HEIGHT = 20;

	public static final String DATA_LOCATION = "data/";
	public static final String LOG4J_FILE = "log4j2.xml";
	public static final String TRUE_VALUE = "T";
	
	public enum ImageFilePaths {
		RED_TRIANGLE("red_triangle.png");

		private String path;

		private ImageFilePaths(String path) {
			this.path = "img/" + path;
		}
		
		public String getPath() {
			return this.path;
		}
	}

	public enum VisualizationModes {
		TRANSLUCENT_GAS, GAS_RINGS, GAS_HIDDEN;
	}
	
	public static final int NEURON_CIRCLE_RADIUS = 15;

	// Timer delay for period between each time tick
	public static final int MIN_TIMER_DELAY = 0;
	public static final int AVG_TIMER_DELAY = 2500;
	public static final int MAX_TIMER_DELAY = 5000;

	
}
