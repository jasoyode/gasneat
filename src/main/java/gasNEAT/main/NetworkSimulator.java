package gasNEAT.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;

import gasNEAT.builders.GasNeatTranscriber;
import gasNEAT.builders.NetworkBuilder;
import gasNEAT.builders.PrimaryIDManager;
import gasNEAT.builders.SpreadsheetConstants;
import gasNEAT.builders.SpreadsheetFreeNetworkBuilder;
import gasNEAT.controller.NetworkSimulationController;
import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.view.FileInputFrame;
import gasNEAT.view.HeadlessView;
import gasNEAT.view.ViewConstants;


//TODO FIXME would be nice to set this up to launch ANJI, but not needed now

/**
 * Main class for running simulation
 *
 */
public class NetworkSimulator {
	private final int numArgsForHeadlessMode = 2;
	private GasNeatNeuralNetwork neuralNetwork;
	private NetworkSimulationController networkSimulationController;
	private final String[] acceptableEvolutionInputs = {"e", "evolution", "evo", "evolve"};
	private final String[] acceptableSimulationInputs = {"s", "sim", "simulation"};
	private final String[] acceptableSandboxInputs = {"t", "test", "testing"};
	private String inputMode;
	private HashMap<String, HashMap<String, String> > legalArgumentsMap;
	
	/* Run in headless mode using the parameters that were passed into the program!
	 * They are of format: <Mode> <File Input> <File Output> 
	 * Mode's aren't case sensitive: "s", "sim", or "Simulate" for simulation OR "e", "evo", or "Evolve" for evolve
	 * File Input: File to read from (ex: ANDNetwork.xlsx, GasANDNetwork.xlsx, etc)
	 * File Output: Name of the file you want the outputs to be sent to.
	 */
	/**
	 * @param args Parameters for running simulation
	 */
	public NetworkSimulator(String[] args) {
		this.buildLegalArgsMap();
		// create model
		this.neuralNetwork = new GasNeatNeuralNetwork();
		
		// this is for headless running
		if(args.length == this.numArgsForHeadlessMode && this.areArgsLegal(args)) {
			args[0] = args[0].toLowerCase();
			this.inputMode = this.legalArgumentsMap.get(ViewConstants.MODE_PARAM_TEXT).get(args[0]);
			this.runHeadless(args);
		// for the normal mode running
		} else if(args.length == 0) {
			this.runWithGUI();
		} else if(args.length == 1 && this.areArgsLegal(args)) {
			// sandbox mode.  Put whatever you want in here to test as you create something!
			NetworkBuilder networkBuilder = new NetworkBuilder();
			Map<Integer, List<Double>> inputs = networkBuilder.extractTrainingSetData(SpreadsheetConstants.XOR_NETWORK_NAME);
			
			// build the network's materials
			PrimaryIDManager primaryIDManager = new PrimaryIDManager();
			SpreadsheetFreeNetworkBuilder spreadsheetFreeBuilder = new SpreadsheetFreeNetworkBuilder(primaryIDManager);
			ChromosomeMaterial chromosomeMaterial = spreadsheetFreeBuilder.buildBaseNetworkAlleles(inputs.get(0).size());
			Chromosome chromosome = new Chromosome(chromosomeMaterial, primaryIDManager.getLongID());
			
			// start transcribing the network from its parts
			GasNeatTranscriber gasNeatTranscriber = new GasNeatTranscriber();
			GasNeatNeuralNetwork network = gasNeatTranscriber.newGasNeatNeuralNetworkOld(chromosome);
			
			NetworkSimulationController controller = new NetworkSimulationController(network, ViewConstants.RUN_TYPE_HEADLESS);
			controller.simulateNetwork(network, inputs);
		}
	}
	
	/* Map that contains the legal inputs from the user */
	/**
	 * Build map that contains the legal inputs from the user
	 */
	private void buildLegalArgsMap() {
		this.legalArgumentsMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> modeMap = new HashMap<String, String>();
		for(String inputMode : this.acceptableEvolutionInputs) {
			modeMap.put(inputMode, ViewConstants.EVOLUTION_TEXT);
		}
		for(String inputMode : this.acceptableSimulationInputs) {
			modeMap.put(inputMode, ViewConstants.SIMULATION_TEXT);
		}
		for(String inputMode : this.acceptableSandboxInputs) {
			modeMap.put(inputMode, ViewConstants.SANDBOX_TEXT);
		}
		legalArgumentsMap.put(ViewConstants.MODE_PARAM_TEXT, modeMap);
	}
	
	/**
	 * @param args
	 * @return
	 */
	private boolean areArgsLegal(String[] args) {
		HashMap<String, String> modeOptionsMap = this.legalArgumentsMap.get(ViewConstants.MODE_PARAM_TEXT);
		if(modeOptionsMap.containsKey(args[0])) {
			return true;
		}
		return false;
	}
	
	/**
	 * Run simulation with GUI
	 */
	private void runWithGUI() {
		// create controller
		this.networkSimulationController = new NetworkSimulationController(this.neuralNetwork, ViewConstants.RUN_TYPE_GUI);
		// create view
		FileInputFrame fileInputScreen = new FileInputFrame(networkSimulationController);
		fileInputScreen.setVisible(true);
	}
	
	/**
	 * Performs simulation without UI
	 * @param args
	 */
	private void runHeadless(String[] args) {
		// create controller
		this.networkSimulationController = new NetworkSimulationController(this.neuralNetwork, ViewConstants.RUN_TYPE_HEADLESS);
		// create view
		HeadlessView headlessView = new HeadlessView(networkSimulationController, this.inputMode, args[1]);
	}
	
	/**
	 * @return
	 */
	public GasNeatNeuralNetwork getNeuralNetwork() {
		return neuralNetwork;
	}

	/**
	 * @param neuralNetwork
	 */
	public void setNeuralNetwork(GasNeatNeuralNetwork neuralNetwork) {
		this.neuralNetwork = neuralNetwork;
	}
	
	/**
	 * @return
	 */
	public NetworkSimulationController getNetworkSimulationController() {
		return networkSimulationController;
	}

	/**
	 * @param networkSimulationController
	 */
	public void setNetworkSimulationController(NetworkSimulationController networkSimulationController) {
		this.networkSimulationController = networkSimulationController;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkSimulator networkSimulator = new NetworkSimulator(args);
	}
}