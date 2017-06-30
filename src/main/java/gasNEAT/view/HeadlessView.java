package gasNEAT.view;

import gasNEAT.controller.NetworkSimulationController;

/**
 * View for command line simulation
 *
 */
public class HeadlessView {
	private NetworkSimulationController networkSimulationController;
	private final String runMode;
	private final String inputFileName;
	private final String defaultInputFile = "XORNetwork.xlsx";
	private final String defaultOutputFile = "defaultOutput.xlsx";
	
	/* Constructor for testing */
	public HeadlessView() {
		this.runMode = ViewConstants.SIMULATION_TEXT;
		this.inputFileName = this.formatFileLocation(this.defaultInputFile);
		this.runProgram();
	}
	
	/**
	 * @param input
	 * @return
	 */
	private String formatFileLocation(String input) {
		if(! input.contains(Constants.DATA_LOCATION)) {
			return Constants.DATA_LOCATION + input;
		}
		return input;
	}
	
	/* Headless version */
	/**
	 * Headless view
	 * @param networkSimulationController
	 * @param mode
	 * @param inputFileName
	 */
	public HeadlessView(NetworkSimulationController networkSimulationController, String mode, String inputFileName) {
		this.networkSimulationController = networkSimulationController;
		this.runMode = mode;
		this.inputFileName = this.formatFileLocation(inputFileName);
		this.runProgram();
	}
	
	/** 
	 * run the evolution simulation
	 */
	private void runProgram() {
		if(this.runMode.equals(ViewConstants.EVOLUTION_TEXT)) {
			this.networkSimulationController.evolve(this.inputFileName);
		} else if(this.runMode.equals(ViewConstants.SIMULATION_TEXT)) {
			this.networkSimulationController.simulate(this.inputFileName, false);
		}
	}
}
