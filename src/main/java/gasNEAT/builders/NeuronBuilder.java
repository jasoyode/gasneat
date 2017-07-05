package gasNEAT.builders;

import java.awt.Color;

import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunction;

import gasNEAT.model.GasNeatReceptor;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.view.Constants.ActivationThresholdFunction;


/**
 * Builds a neuron by assigning various properties from configuration file
 *
 */
public class NeuronBuilder {
	
	// required parameters
	/** Neuron ID */
	private final int neuronID;
	
	
	private final ActivationFunction activationFunction;
	
	/** Neuron Activation Function (Step or sigmoid)*/
	private final ActivationThresholdFunction activationThresholdFunction;
	private final NeuronType layerType;
	
	// non-required parameters with default values
	
	// randomly assigned variables
	private int x = 0;
	private int y = 0;
	
	/** Threshold for activation*/
	private double threshold = 0.0;
	
	private GasNeatReceptor receptor;
	
	// gas-related variables
	private Color gasColor = Color.white;
	
	private double gasEmissionRadius = 0.0;
	private double baseProduction = 0.0;

	private boolean isGasEmitter = false;
	private boolean isGasReceiver = false;
	
	//String to gas G0-> normal, G1-> modulatory
	//By default there is no gas produced and the synapse send a standard signal
	private String gasType = "G0";  //none
	private String synapticGasType = "G0";
	
	/**
	 * 
	 * @param activationFunction Type of activation function (Step or Sigmoid)
	 * @param neuronID Neuron's Unique ID
	 * @param layerType In which layer does the neuron belong to (Input/Output/Hidden)
	 */
	public NeuronBuilder(ActivationFunction activationFunction, ActivationThresholdFunction activationThresholdFunction, int neuronID, NeuronType layerType, String synapticTransmitterType ) {
		this.activationFunction = activationFunction;
		this.activationThresholdFunction = activationThresholdFunction;
		this.neuronID = neuronID;
		this.layerType = layerType;
		this.synapticGasType = synapticTransmitterType;
		
	}
	/**
	 * Set co-ordinates for neuron visualization
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @return Neuron builder
	 */
	public NeuronBuilder setXAndYCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	/**
	 * Set what type of gas the neuron emits
	 * @param gasType Gas Type
	 * @return Neuron builder
	 */
	public NeuronBuilder setGasType(String gasType) {
		this.gasType = gasType;
		return this;
	}
	
	/**
	 * Set what type of gas the neuron's synapses transmit
	 * @param gasType Gas Type
	 * @return Neuron builder
	 */
	public NeuronBuilder setSynapticGasType(String gasType) {
		this.synapticGasType = gasType;
		return this;
	}
	
	
	
	public NeuronBuilder setIsGasEmitter(boolean isGasEmitter) {
		this.isGasEmitter = isGasEmitter;
		return this;
	}
	
	public NeuronBuilder setGasColor(Color gasColor) {
		this.gasColor = gasColor;
		return this;
	}
	
	public NeuronBuilder setThreshold(double threshold) {
		this.threshold = threshold;
		return this;
	}
	
	public NeuronBuilder setIsGasReceiver(boolean isGasReceiver) {
		this.isGasReceiver = isGasReceiver;
		return this;
	}
	
	public NeuronBuilder setReceptor(GasNeatReceptor receptor) {
		this.receptor = receptor;
		return this;
	}
	
	public NeuronBuilder setGasEmissionRadius(double gasEmissionRadius) {
		this.gasEmissionRadius = gasEmissionRadius;
		return this;
	}
	
	public NeuronBuilder setBaseProduction(double baseProduction) {
		this.baseProduction = baseProduction;
		return this;
	}
	
	//public GasNeatNeuron build() {
	//	return new GasNeatNeuron(this);
	//}
	
	/**
	 * Builds neuron with random configuration
	 * @return Neuron
	 */
	public GasNeatNeuron buildRandomNeuron() {
		
		System.err.println("Not using spreadsheets");
		System.exit(-1);
		return null;
		
		//System.out.println("If you are using this, you need to fix randomization from seed");
		//System.exit(1);
		//this.x = (int) Math.random() * 25;
		//this.y = (int) Math.random() * 25;
		//this.threshold = Math.random();
		//return new GasNeatNeuron(this);
	}
	
	// Getters
	public int getNeuronID() { return this.neuronID; }
	
	public ActivationFunction getActivationFunction() { return this.activationFunction; } 
	
	public NeuronType getLayerType() { return this.layerType; }
	
	public int getX() { return this.x; }
	
	public int getY() { return this.y; }
	
	public boolean getIsGasEmitter()  { return this.isGasEmitter; }
	
	public Color getGasColor() { return this.gasColor; }
	
	public boolean getIsGasReceiver() { return this.isGasReceiver; }
	
	public GasNeatReceptor getReceptor() { return this.receptor; }
	
	public double getGasEmissionRadius() { return this.gasEmissionRadius; }
	
	public double getBaseProduction() { return this.baseProduction; }
	
	public double getThreshold() { return this.threshold; }
	
	public String getGasType() { return this.gasType; }
	
	public String getSynapticGasType() { return this.synapticGasType; }
	
	public ActivationThresholdFunction getActivationThresholdFunction() {
		return this.activationThresholdFunction;
	}
}
