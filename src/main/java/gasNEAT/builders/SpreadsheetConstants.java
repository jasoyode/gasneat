package gasNEAT.builders;

import java.util.HashMap;

import com.anji.neat.NeuronType;

import gasNEAT.view.Constants;

public class SpreadsheetConstants {
	public static final String XOR_NETWORK_NAME = Constants.DATA_LOCATION + "XORNetwork.xlsx";
	public static final String NOISE_FILTER_NETWORK_NAME = Constants.DATA_LOCATION + "NoiseFilterNetwork.xlsx";
	public static final String PLASTICITY_MODULATION_NETWORK_NAME = Constants.DATA_LOCATION + "PlasticityModulationNetwork.xlsx";
	
	public static final String HIDDEN_LAYER_SPREADSHEET_LABEL = "H";
	public static final String INPUT_LAYER_SPREADSHEET_LABEL = "I";
	public static final String OUTPUT_LAYER_SPREADSHEET_LABEL = "O";
	
	public static final String NEURON_ID_PREFIX = "N";
	public static final String SYNAPSE_ID_PREFIX = "S";
	public static final String RECEPTOR_ID_PREFIX = "R";
	public static final String FUNCTION_ID_PREFIX = "F";
	
	public static enum LAYER_TYPES {HIDDEN, INPUT, OUTPUT};
	
	public static final HashMap<String, NeuronType> TYPE_MAPPING;
	static {
		TYPE_MAPPING = new HashMap<String,NeuronType>();
		SpreadsheetConstants.TYPE_MAPPING.put(SpreadsheetConstants.HIDDEN_LAYER_SPREADSHEET_LABEL, NeuronType.HIDDEN);
		SpreadsheetConstants.TYPE_MAPPING.put(SpreadsheetConstants.INPUT_LAYER_SPREADSHEET_LABEL, NeuronType.INPUT);
		SpreadsheetConstants.TYPE_MAPPING.put(SpreadsheetConstants.OUTPUT_LAYER_SPREADSHEET_LABEL, NeuronType.OUTPUT);
	
	}
	
	public static final int INPUT_SHEET_INDEX = 6;
	public static final int OUTPUT_SHEET_INDEX = 7;
	public static final int EXPECTED_OUTPUT_COLUMN_NUMBER = 1;
	
	// constants for neurons
	public static final int NEURON_ID_COLUMN = 0;
	public static final int NEURON_X_COLUMN = 1;
	public static final int NEURON_Y_COLUMN = 2;
	public static final int NEURON_TYPE_COLUMN = 3;
	
	// Constants for the receptor
	public static final int RECEPTOR_ID_COLUMN = 0;
	public static final int RECEPTOR_ACTIVATION_TYPE_COLUMN = 1;
	public static final int RECEPTOR_GAS_COLUMN = 2;
	public static final int RECEPTOR_ACTIVATION_MOD_FUNCTION_COLUMN = 3;
	public static final int RECEPTOR_PLASTICITY_MOD_FUNCTION_COLUMN = 4;
	public static final int[] RECEPTOR_INFO = {RECEPTOR_ID_COLUMN, RECEPTOR_ACTIVATION_TYPE_COLUMN, RECEPTOR_GAS_COLUMN, RECEPTOR_ACTIVATION_MOD_FUNCTION_COLUMN, RECEPTOR_PLASTICITY_MOD_FUNCTION_COLUMN};
}
