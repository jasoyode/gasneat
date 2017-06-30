package gasNEAT.model;
import java.util.ArrayList;

/** This class is used to store information about network state
 *  which will be used for forward stepping, backward stepping and replay 
 */
public class NetworkState {

	/** neural network instance at a particular time instant */
	private GasNeatNeuralNetwork neuralNetwork;

	/** activated path ( list of neuron ids) at a particular time instant */
	private ArrayList<String> activatedPath;
	
	/**
	 * Getter method for activated path
	 * 
	 * @return activatedPath	current activated path instance
	 */	
	public ArrayList<String> getActivatedPath() {
		return activatedPath;
	}

	/**
	 * Setter method for activated path
	 * 
	 * @param activatedPath	current activated path instance
	 */	
	public void setActivatedPath(ArrayList<String> activatedPath) {
		this.activatedPath = activatedPath;
	}
	
	/**
	 * Getter method for neural network
	 * 
	 * @return neuralNetwork	current neural network instance
	 */	
	public GasNeatNeuralNetwork getNeuralNetwork() {
		return neuralNetwork;
	}

	/**
	 * Setter method for neural network
	 * 
	 * @param neuralNetwork	current neural network instance
	 */	
	public void setNeuralNetwork(GasNeatNeuralNetwork neuralNetwork) {
		this.neuralNetwork = neuralNetwork;
	}

}
