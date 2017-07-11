package gasNEAT.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunction;
import com.anji.nn.ActivationFunctionFactory;
import com.anji.util.Properties;

import gasNEAT.builders.NetworkBuilder;
import gasNEAT.builders.SpreadsheetConstants;
import gasNEAT.builders.SpreadsheetConstants.LAYER_TYPES;
import gasNEAT.builders.SynapseBuilder;
import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.controller.RecurrentSimulator;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.targetSequence.TargetSequenceFitnessEvaluator;
import gasNEAT.view.Constants;
import gasNEAT.view.Constants.ActivationThresholdFunction;
import gasNEAT.view.Constants.NetworkType;
import gasNEAT.view.Constants.VisualizationModes;

/**
 * Neural network class contains fields to store wholistic information about a
 * neural network. It provides methods to build a neural network from MS Excel
 * file and cloning a neural network.
 */
public class GasNeatNeuralNetwork implements Cloneable {
	
	private Properties props;
	private int RECURRENT_STEPS;
	
	private boolean flatConcentrationGradient;
	
	/** Neural Network Neuron HashMap*/
	private HashMap<Long, GasNeatNeuron> neuronMap = new HashMap<Long, GasNeatNeuron>();
	
	/** Neural Network Synapse HashMap*/
	private HashMap<Long, GasNeatSynapse> synapseMap = new LinkedHashMap<Long, GasNeatSynapse>();
	
	/** Neural Network Gas HashMap*/
	private HashMap<Integer, Gas> gasMap = new HashMap<Integer, Gas>();
	
	/** Gas Receiving Neurons HashMap*/
	
	//THIS NEEDS TO BE SET OR ELSE WHEN CREATING DISPERSION UNIT,
	//NO GAS RECEIVERS WILL BE KNOWN TO NEED TO BE ADDED
	private HashMap<Integer, List<GasNeatNeuron>> gasReceiverNeuronsMap;
	
	/** Receptor Neurons HashMap*/
	private HashMap<String, GasNeatReceptor> receptorMap;
	
	/** Neural Network Polynomial Map*/
	private HashMap<String, PolynomialFunction> functionMap;
	
	/** Network Builder class object*/
	private NetworkBuilder networkBuilder;
	
	/** Neural Network Activation function*/
	private ActivationFunction activationFunction;
	
	/** Neural Network Activation Threshold function*/
	private ActivationThresholdFunction activationThresholdFunction;
	
	/** Boolean for setting Labels*/
	private boolean labeled;
	
	/** Neural Network Visualization mode*/
	private VisualizationModes mode;
	
	/** NetworkType object*/
	private NetworkType networkType;
	
	/** Keeps track of Neural Network generation*/
	private Integer generation;
	
	private int currentTimeTick = 0;

	/** logger instance */
	//private final static Logger logger = Logger.getLogger( GasNeatNeuralNetwork.class );
	private final static Logger logger = Logger.getLogger( TargetSequenceFitnessEvaluator.class );
	
	/** Keeps track of indices of IO neurons*/
	private ArrayList<GasNeatNeuron> inputNeurons;
	private ArrayList<GasNeatNeuron> outputNeurons;
	
	private RecurrentSimulator simulator;
	
	public RecurrentSimulator getSimulator() {
		return simulator;
	}

	/**
	 * Constructs a Neural Network
	 */
	public GasNeatNeuralNetwork() {
		neuronMap = new HashMap<Long, GasNeatNeuron>();
		synapseMap = new HashMap<Long, GasNeatSynapse>();
		gasMap = new HashMap<Integer, Gas>();
		setReceptorMap(new HashMap<String, GasNeatReceptor>());
		setFunctionMap(new HashMap<String, PolynomialFunction>());
		gasReceiverNeuronsMap = new HashMap<Integer, List<GasNeatNeuron>>();
		networkBuilder = new NetworkBuilder();
		
		//default -NOOOOOOOOO - 
		//TODO: need to determine activation threshold function somewhere...
		activationThresholdFunction = ActivationThresholdFunction.LOGARITHMIC_SIGMOID;
		activationFunction = ActivationFunctionFactory.getInstance().getSigmoid();
	}
	
	//*
	public GasNeatNeuralNetwork( Collection<GasNeatNeuron> someNeurons, List<GasNeatNeuron> someInNeurons, List<GasNeatNeuron> someOutNeurons,
			Collection someRecurrentConns, String aName, HashMap<Long, GasNeatSynapse> synapseMap, Properties props, int recurrentSteps ) {
		
		this.props = props;
		
		flatConcentrationGradient = props.getBooleanProperty( GasNeatConfiguration.FLAT_CONCENTRATION_GRADIENT_KEY, true );
		
		//ULTRATODO - find a way to short cut this when it happens, don't waste time simulating it
		if (recurrentSteps  > 99) {
			recurrentSteps = 1;
		}
		
		int hardCodedCyclesPerTimestep = props.getIntProperty( GasNeatConfiguration.HARDCODE_CYCLES_KEY, 0 );
		
		RECURRENT_STEPS = recurrentSteps + props.getIntProperty( GasNeatConfiguration.EXTRA_RECURRENT_CYCLES_KEY, 0 );
		
		if (hardCodedCyclesPerTimestep > 0) {
			RECURRENT_STEPS  = hardCodedCyclesPerTimestep;
			logger.debug("hardCodedCyclesPerTimestep set = " +hardCodedCyclesPerTimestep);
		} else {
			logger.debug("hardCodedCyclesPerTimestep not set, RECURRENT_STEPS = " + RECURRENT_STEPS);
		}
		
		
		
		//CAN BE SKIPPED FOR NOW
		//default
		//TODO: need to determine activation threshold function somewhere...
		activationThresholdFunction = ActivationThresholdFunction.LOGARITHMIC_SIGMOID;
		
		setReceptorMap(new HashMap<String, GasNeatReceptor>());
		setFunctionMap(new HashMap<String, PolynomialFunction>());
		
		
		gasMap = new HashMap<Integer, Gas>();
		gasReceiverNeuronsMap = new HashMap<Integer, List<GasNeatNeuron>>();
		networkBuilder = new NetworkBuilder();
		//////////////////
		
		neuronMap = new HashMap<Long, GasNeatNeuron>();
		this.synapseMap = synapseMap;

		//TODO: take this from the config file...
		ArrayList<Integer> gases = new ArrayList<Integer>();

		
		//ULTRATODO
		//TODO FIXME - need to pass through the configuration here so that we can determine number of gases
		int numberGases = 4;
		
		for (int i=0; i <= numberGases; i++ ) {
			//will include "G0" no matter what
			// only using numerical values for speed up
			gases.add( i );
		}
		
		for (int gas: gases) {
			gasReceiverNeuronsMap.put(gas, new ArrayList<GasNeatNeuron>() );
		}
		
		
		for (GasNeatNeuron neuron: someNeurons) {
			
			for ( Integer gas : neuron.getReceptor().getGasList()  ) {
				
				gasReceiverNeuronsMap.get(  gas ).add(neuron);
				//System.out.println( "END: neuron"+neuron.getId()+" had gas added: " + gas );
				//System.exit(1);
			}
			
			//TODO: populate gas map - hardcoded for now
			if (!gasMap.containsKey( neuron.getGasProductionTypeInt()) ) {
				Gas gas = new Gas();
				gas.setGasID( neuron.getGasProductionTypeInt()  );
				gas.setName( "Gas "+ neuron.getGasProductionTypeInt() );
				
				//TODO set in config
				
				if (flatConcentrationGradient) {
					gas.setGasDispersionType(  "FLAT" );					
				} else {
					gas.setGasDispersionType(  "GRADIENT" );
				}
				
				
				gas.setPropagationSpeed( props.getDoubleProperty( GasNeatConfiguration.GAS_SPEED_KEY ) );
				//gas.setDecayFactor(  props.getDoubleProperty( GasNeatConfiguration.GAS_DECAY_KEY )   );
				
				int number = neuron.getGasProductionTypeInt();
				
				float r = 0.0f;
				float g = 0.7f;
				float b = 0.9f;
				
				//ULTRATODO - set these colors in constants
				//#GASNEATVISUAL
				
				if (number == 0) {
					//do nothing
				} else if (number == 1) {
					r = 0.9f;
					g = 0.7f;
					b = 0.0f;
				} else if (number == 2) {
					r = 0.3f;
					g = 0.7f;
					b = 0.3f;
				} else if (number == 3) {
					r = 0.9f;
					g = 0.0f;
					b = 0.3f;
				} else if (number == 4) {
					r = 0.1f;
					g = 0.2f;
					b = 0.3f;
				} else {
					System.out.println("BAD EXIT");
					System.exit(1);
				}
				
				gas.setColor(r, g, b);
				gasMap.put(gas.getGasID(), gas);
			}
			
			if ( !receptorMap.containsKey( neuron.getReceptor().getReceptorID())  ) {
				receptorMap.put( neuron.getReceptor().getReceptorID()  , neuron.getReceptor() );
			}
			//ULTRATODO is int enough?
			neuronMap.put( neuron.getId(), neuron);
		}

		
		for (long synapseId: synapseMap.keySet() ) {
			//int startOfSecond = synapseName.substring(2).indexOf("N");
			
			long[] pair =  SynapseBuilder.elegantUnpairing( synapseId  );
			//synapseName.substring(1,startOfSecond+2);
			long source = pair[0];
			
			long destination = pair[1];
			
			//= synapseId.substring(startOfSecond+2);
			//LOG.info( source +" "+destination);
			neuronMap.get(source).addOutgoingConnection( synapseMap.get(synapseId) );
		}

		
		simulator = new RecurrentSimulator(this, RECURRENT_STEPS);
		
	}
	//*/
	
	
	//////////////////////////////////////////////////////

	/**
	 * Builds a Neural Network from an Excel file.
	 * 
	 * @param fileName
	 *            Excel File Name
	 * @param inputTimeSignalMap
	 *            Input Time Signal Map
	 */
	public void buildNetwork(String fileName, Map<Integer, List<Double>> inputTimeSignalMap) {
		networkBuilder.setInputTimeSignalMap(inputTimeSignalMap);
		networkBuilder.buildNetwork(fileName, this);
		updateIONeurons();
		
		
		System.out.println("network created using build network");
		System.exit(1);
		
	}
	/**
	 * Builds Neural Network from an Excel File. This builds a network with
	 * SynapaticWeights as labels of the synapses.
	 * 
	 * @param fileName
	 *            Excel File Name
	 * @param inputTimeSignalMap
	 *            Input Time Signal Map
	 * @param labeled
	 *            True if Labels are enabled
	 */
	public void buildNetwork(String fileName, Map<Integer, List<Double>> inputTimeSignalMap, boolean labeled) {
		networkBuilder.setInputTimeSignalMap(inputTimeSignalMap);
		this.labeled = labeled;
		networkBuilder.buildNetwork(fileName, this);
		updateIONeurons();
	}
	
	public void addSynapse(GasNeatSynapse synapse) {
		this.getSynapseMap().put(synapse.getSynapseID(), synapse);
		
		
		logger.info("synapse: "+ synapse );
		logger.info("synapseID: "+ synapse.getSynapseID() );
		
		// You also have to add any synapses to the neurons synapse list... may want to remove this later if possible
		//String[] ids = synapse.getSynapseID().split(SpreadsheetConstants.NEURON_ID_PREFIX);
		
		//int[] ids = SynapseBuilder.elegantUnpairing( synapse.getSynapseID() );
		
		
		
		//GasNeatNeuron currentNeuron = this.neuronMap.get(SpreadsheetConstants.NEURON_ID_PREFIX + ids[0]);
		//BUG the "S" at the start of the ID will not be an index and will break the creation
		GasNeatNeuron currentNeuron = this.neuronMap.get( synapse.getTargetNeuron() );
				
				//SpreadsheetConstants.NEURON_ID_PREFIX + ids[1]);
				
		//logger.info("SpreadsheetConstants.NEURON_ID_PREFIX:" + SpreadsheetConstants.NEURON_ID_PREFIX );
		logger.info("get: " + SpreadsheetConstants.NEURON_ID_PREFIX + synapse.getTargetNeuron()  );
		
		
		logger.info("currentNeuron:" + currentNeuron);
		
		
		
		
		if(currentNeuron == null) {
			
			if (logger.isInfoEnabled() ) {
				logger.info( "neuronMap");
				logger.info( neuronMap);
				this.logger.info("Tried to add synapse to neuron not yet created: " + synapse.getSynapseID() + SpreadsheetConstants.NEURON_ID_PREFIX + synapse.getSourceNeuron()  ) ;
			}
			
		} else {
			if (logger.isInfoEnabled() ) {
				this.logger.info("Adding synapse: " + synapse.getSynapseID() + SpreadsheetConstants.NEURON_ID_PREFIX + synapse.getSourceNeuron()  );
			}
		}
	}
	
	public void addNeuron(GasNeatNeuron neuron) {
		//and build the gas/receiver neurons map
		if (neuron.isGasReceiver()) {								
			ArrayList<Integer> gasList = neuron.getReceptor().getGasList();
			for (Integer gasID : gasList) {
				if (this.getGasReceiverNeuronsMap().keySet().contains(gasID)) {
					this.getGasReceiverNeuronsMap().get(gasID).add(neuron);
				} else {
					ArrayList<GasNeatNeuron> neurons = new ArrayList<GasNeatNeuron>();
					neurons.add(neuron);
					this.getGasReceiverNeuronsMap().put(gasID, neurons);
				}
			}
		}
		this.getNeuronMap().put(neuron.getNeuronID(), neuron);
		updateIONeurons();
	}
	
	public int getOutputDimension() {
		int outputs=0;
		for (GasNeatNeuron neuron: getNeuronMap().values() ) {
			if (neuron.getLayerType().equals(LAYER_TYPES.OUTPUT ) ) {
				outputs++;
			}
		}
		if (outputs == 0) {
			//This should never happen, but break it here so we can see it if it does
			assert(false);
		}
		
		return outputs;
	}
	public int getInputDimension() {
		int inputs=0;
		for (GasNeatNeuron neuron: getNeuronMap().values() ) {
			if (neuron.getLayerType().equals(LAYER_TYPES.INPUT ) ) {
				inputs++;
			}
		}
		if (inputs == 0) {
			//This should never happen, but break it here so we can see it if it does
			assert(false);
		}
		
		return inputs;
	}
		/**
	 * Updates the indexed input and output neurons
	 * Should be called anytime a neuron is added or removed to be safe
	 * 
	 * @param none
	 *            
	 */
	private void updateIONeurons() {
		Collection<GasNeatNeuron> neurons = getNeuronMap().values();
		inputNeurons = new ArrayList<GasNeatNeuron>();
		outputNeurons = new ArrayList<GasNeatNeuron>();
		
		for (GasNeatNeuron neuron: neurons) {

			if (neuron.getLayerType().equals(LAYER_TYPES.INPUT)) {
				inputNeurons.add(neuron);
			} else if (neuron.getLayerType().equals(LAYER_TYPES.OUTPUT)) {
				outputNeurons.add(neuron);
			}
		}
		//TODO: change to use getNeuronID when ready
		Collections.sort(inputNeurons, new Comparator<GasNeatNeuron>() {
		    public int compare(GasNeatNeuron obj1, GasNeatNeuron obj2) {
		        return obj1.toString().compareTo(obj2.toString() );
		    }
		});
		Collections.sort(outputNeurons, new Comparator<GasNeatNeuron>() {
		    public int compare(GasNeatNeuron obj1, GasNeatNeuron obj2) {
		        return obj1.toString().compareTo(obj2.toString() );
		    }
		});
		
	}
	
	/* Outputs a linkedlist of neurons of the input type that are contained in the network */
	private LinkedList<GasNeatNeuron> getNeuronsOfType(NeuronType type) {
		LinkedList<GasNeatNeuron> outputs = new LinkedList<GasNeatNeuron>();
		
		for(long neuronID : this.neuronMap.keySet()) {
			GasNeatNeuron neuron = this.neuronMap.get(neuronID);
			if(neuron.getLayerType() == type) {
				outputs.add(neuron);
			}
		}
		return outputs;
	}
	/* Our neural network has inputs, outputs, and values in the middle.  This gets the values that the neural network is outputting after running a step */
	public double[] getOutputValues() {
		
		LinkedList<GasNeatNeuron> outputNeurons = this.getNeuronsOfType(NeuronType.OUTPUT);
		double[] outputs = new double[outputNeurons.size()];
		
		for(int i = 0; i < outputs.length; i++) {
			outputs[i] = outputNeurons.get(i).calculateActivation();
		}
		
		return outputs;
	}
	public GasNeatNeuron getInputNeuron(int i) {
		if (i >= getInputDimension() ) {
			//This should never happen, but break it here so we can see it if it does
			assert(false);
			return null;
		} else {
			return inputNeurons.get(i);
		}
	}
	public GasNeatNeuron getOutputNeuron(int i) {
		if (i >= getOutputDimension() ) {
			//This should never happen, but break it here so we can see it if it does
			assert(false);
			return null;
		} else {
			return outputNeurons.get(i);
		}
	}

	//TODO: This needs to invoke the same stuff that the Controller currently does 
	//Could potentially instantiate a controller and call the step function on it.
	public double[] next(double[] inputs) throws CloneNotSupportedException {
		return simulator.step(   inputs  );
		
	}
	
	/**
	 * Creates a clone of Neural Network
	 * 
	 * @return NeuralNetwork Clone of calling return Neuralk Network object
	 * @throws CloneNotSupportedException
	 *             Clone not supported exception
	 */
	public GasNeatNeuralNetwork clone() throws CloneNotSupportedException {
		GasNeatNeuralNetwork neuralNetworkCopy = (GasNeatNeuralNetwork) super.clone();
		neuralNetworkCopy.setNeuronMap((HashMap<Long, GasNeatNeuron>) deepClone(neuronMap));
		neuralNetworkCopy.setSynapseMap((HashMap<Long, GasNeatSynapse>) deepClone(synapseMap));
		neuralNetworkCopy.setGasMap((HashMap<Integer, Gas>) deepClone(gasMap));
		neuralNetworkCopy.setGasReceiverNeuronsMap((HashMap<Integer, List<GasNeatNeuron>>) deepClone(gasReceiverNeuronsMap));
		return neuralNetworkCopy;
	}
	/**
	 * This method returns a deep copy of a Map
	 * 
	 * @param map
	 *            HashMap to be cloned
	 * @throws CloneNotSupportedException
	 *             Clone not supported exception
	 * @return mapCopy Clone of HashMap
	 */
	private Object deepClone(HashMap map) throws CloneNotSupportedException {
		HashMap mapCopy = new HashMap();
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			if (entry.getValue().getClass() == GasNeatNeuron.class)
				mapCopy.put(entry.getKey(), ((GasNeatNeuron) entry.getValue()).clone());
			else if (entry.getValue().getClass() == GasNeatSynapse.class)
				mapCopy.put(entry.getKey(), ((GasNeatSynapse) entry.getValue()).clone());
			else if (entry.getValue().getClass() == Gas.class)
				mapCopy.put(entry.getKey(), ((Gas) entry.getValue()).clone());
		}

		return mapCopy;
	}
	/**
	 * Returns HashMap of all Receptors in the Neural Network
	 * 
	 * @return receptorMap HashMap of Receptors
	 */
	public HashMap<String, GasNeatReceptor> getReceptorMap() {
		return receptorMap;
	}
	/**
	 * Sets HashMap of Receptors in the Neural Network
	 * 
	 * @param receptorMap
	 *            HashMap of Receptors
	 */
	public void setReceptorMap(HashMap<String, GasNeatReceptor> receptorMap) {
		this.receptorMap = receptorMap;
	}
	/**
	 * Returns HashMap of Polynomials
	 * 
	 * @return functionMap HashMap of Polynomials
	 */
	public HashMap<String, PolynomialFunction> getFunctionMap() {
		return functionMap;
	}
	
	public PolynomialFunction getFromFunctionMap(String key) {
		return this.functionMap.get(key);
	}

	/**
	 * Sets HashMap of Polynomials
	 * 
	 * @param functionMap
	 *            HashMap of Polynomials
	 */
	public void setFunctionMap(HashMap<String, PolynomialFunction> functionMap) {
		this.functionMap = functionMap;
	}
	/**
	 * Returns ActivationFunction of Neural Network
	 * 
	 * @return activationFunction Activation function of Neural Network
	 */
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}
	/**
	 * Sets ActivationFunction of Neural Network
	 * 
	 * @param activationFunction
	 *            Activation function of Neural Network
	 */
	public void setActivationFunction(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}
	/**
	 * Returns Visualization mode of Neural Network
	 * 
	 * @return mode Visualization mode
	 */
	public VisualizationModes getMode() {
		return mode;
	}
	/**
	 * Sets Visualization mode of Neural Network
	 * 
	 * @param mode
	 *            Visualization mode
	 */
	public void setMode(VisualizationModes mode) {
		this.mode = mode;
	}
	/**
	 * Returns Neural Network Type
	 * 
	 * @return networkType Neural Network Type
	 */
	public NetworkType getNetworkType() {
		return networkType;
	}
	/**
	 * Sets Neural Network Type
	 * 
	 * @param networkType
	 *            Neural Network Type
	 */
	public void setNetworkType(NetworkType networkType) {
		this.networkType = networkType;
	}
	/**
	 * Returns Network Builder class object
	 * 
	 * @return networkBuilder Network builder class object
	 */
	public NetworkBuilder getNetworkBuilder() {
		return networkBuilder;
	}
	/**
	 * Sets Network Builder class object
	 * 
	 * @param networkBuilder
	 *            Network builder class object
	 */
	public void setNetworkBuilder(NetworkBuilder networkBuilder) {
		this.networkBuilder = networkBuilder;
	}
	/**
	 * Returns NeuronMap Hashmap
	 * 
	 * @return neuronMap HashMap of Neurons
	 */
	public HashMap<Long, GasNeatNeuron> getNeuronMap() {
		return neuronMap;
	}
	public GasNeatNeuron getNeuron(long key) {
		return this.neuronMap.get(key);
	}
	/**
	 * Returns Synapse Hashmap
	 * 
	 * @return synapseMap HashMap of Synapses
	 */
	public HashMap<Long, GasNeatSynapse> getSynapseMap() {
		return synapseMap;
	}
	/**
	 * Returns True if Neural Network is labeled
	 * 
	 * @return labeled True if Neural Network is labeled
	 */
	public boolean isLabeled() {
		return labeled;
	}
	/**
	 * Sets boolean for labels of Neural Network
	 * 
	 * @param labeled
	 *            True if Neural Network is labeled
	 */
	public void setLabeled(boolean labeled) {
		this.labeled = labeled;
	}
	/**
	 * Sets NeuronMap HashMap
	 * 
	 * @param neuronMap
	 *            HashMap of neurons
	 */
	public void setNeuronMap(HashMap<Long, GasNeatNeuron> neuronMap) {
		this.neuronMap = neuronMap;
		updateIONeurons();
	}
	/**
	 * Sets SynapseMap HashMap
	 * 
	 * @param synapseMap
	 *            HashMap of synapses
	 */
	public void setSynapseMap(HashMap<Long, GasNeatSynapse> synapseMap) {
		this.synapseMap = synapseMap;
	}
	/**
	 * Sets Gas Receiver Neurons Map
	 * 
	 * @param gasReceiverNeuronsMap
	 *            HashMap of gas receiver Neurons
	 */
	public void setGasReceiverNeuronsMap(HashMap<Integer, List<GasNeatNeuron>> gasReceiverNeuronsMap) {
		this.gasReceiverNeuronsMap = gasReceiverNeuronsMap;
	}
	/**
	 * Returns HashMap of gases
	 * 
	 * @return gasMap HashMap of gases
	 */
	public HashMap<Integer, Gas> getGasMap() {
		return gasMap;
	}
	/**
	 * Sets HashMap of gases
	 * 
	 * @param gasMap
	 *            HashMap of gases
	 */
	public void setGasMap(HashMap<Integer, Gas> gasMap) {
		this.gasMap = gasMap;
	}
	/**
	 * Returns HashMap of gas receiver map
	 * 
	 * @return gasReceiverNeuronsMap HashMap of gas receiver neurons
	 */
	public HashMap<Integer, List<GasNeatNeuron>> getGasReceiverNeuronsMap() {
		return gasReceiverNeuronsMap;
	}
	public void addReceptor(GasNeatReceptor receptor) {
		if (receptor.getActivationModFunction() != null) {
			this.receptorMap.put(receptor.getReceptorID(), receptor);
		}
	}
	
	
	/**
	 * Sets generation of Neural Network
	 * 
	 * @param generation
	 *            Generation of Neural Network
	 */
	public void setGeneration(Integer generation) {
		this.generation = generation;
	}


	public void NOTNEEDEDfullyActivate() {
		
	}

	public void NOTNEEDEDreset() {
		this.neuronMap.clear();
		this.synapseMap.clear();
		this.gasMap.clear();
		this.receptorMap.clear();
		this.functionMap.clear();
		this.generation = 0;
		this.currentTimeTick = 0;
	}

	public String NOTNEEDEDtoXml() {
		// TODO does this need to be re-done? perhaps?
		return null;
	}

	public String getName() {
		return null;
	}

	public boolean isRecurrent() {
		return this.getNetworkType() == Constants.NetworkType.RECURRENT;
	}

	public ActivationThresholdFunction getActivationThresholdFunction() {
		return activationThresholdFunction;
	}

	public void clear() {
		for (GasNeatNeuron n: neuronMap.values() ){
			n.clear();
			
		}
		
	}

}
