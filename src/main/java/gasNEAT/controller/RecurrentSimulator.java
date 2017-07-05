package gasNEAT.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Timer;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import com.anji.neat.NeuronType;

import gasNEAT.model.Gas;
import gasNEAT.model.GasDispersionSlot;
import gasNEAT.model.GasDispersionUnit;
import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.model.GasNeatReceptor;
import gasNEAT.model.GasNeatSynapse;
import gasNEAT.model.NetworkState;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.view.Constants;
import gasNEAT.view.ViewConstants;

/**
 * This class performs a timer based simulation of a recurrent neural network.
 */
public class RecurrentSimulator extends Simulator implements SimulatorInterface {
	
	
	private final boolean listsInitialized;
	/**  list of ids of activated neurons at each time instant that gets supplied to the view */
	private List<Long> currentActivatedNeurons;

	/**  clone of neural network instance, used to store in networkStateList */
	private GasNeatNeuralNetwork neuralNetworkClone;

	/**  list of outputs generated during network simulation */
	private ArrayList<Double> outputList;

	/**  list of neurons in the input layer */
	private List<Long> inputNeurons;

	/**  map representing input signals at each time instant */
	private Map<Integer, List<Double>> inputTimeSignalMap;

	/**  represents a time instant */
	private int tickCount = 0;

	/** list of input signals at a time instant */
	private ArrayList<Double> inputList;
	
	/** boolean to check if we are done simulating all input signals */
	private boolean doneSimulatingInputs = false;
	
	/** keeps track of the number of input signals simulated */
	private int inputCount = 0;
	
	/** just a number that determines when to stop the simulator. 
	 * So the simulator runs 20 times after reading the last input signal and then stops the 
	 * simulation process */
	private int stabilizingCounter = 20;
	
	/** indicates if we are in the replay mode or play mode */
	private boolean replaying = false;
	
	private boolean finishedSteps = false;
	
	/** represents the index of the network state list during replay */
	private int replayIndex = 0;
	
	protected String name = "Recurrent Simulator";
	
	private final int STEPS_PER_TICK;
	
	
	/** logger instance */
	//private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RecurrentSimulator.class);
	private static Logger logger = Logger.getLogger( RecurrentSimulator.class );
	
	/**
	 * Creates an instance of the recurrent simulator  
	 * 
	 * @param neuralNetwork neural network to simulate
	 * @param inputTimeSignalMap input signals supplied for simulation
	 */
	public RecurrentSimulator(GasNeatNeuralNetwork neuralNetwork, Map<Integer, List<Double>> inputTimeSignalMap, String runType) {
		super(runType);
		this.inputTimeSignalMap = inputTimeSignalMap;
		this.neuralNetwork = neuralNetwork;
		this.logBeginning(ViewConstants.RECURRENT_NETWORK_TEXT);
		this.listsInitialized = this.initLists();
		updateInputNeurons();
		
		this.createGasDispersionUnit();
		
		
		STEPS_PER_TICK = -1;
		
		System.out.println("MUST HANDLE STEPS PER TICK!");
		
		System.exit(1);
		/*
		for (GasNeatNeuron neuron: neuralNetwork.getNeuronMap().values() ) {
			if (neuron.isGasEmitter() ) {
				for (GasDispersionSlot slot: neuron.getGasDispersionUnit().getGasDispersionSlotList() ) {
					if (slot.getReceiverNeurons().size() > 0) {
						System.out.println(neuron.getId() + " :" + " "+slot.getReceiverNeurons() );
						System.out.println("XXXXXXX END" );
						System.exit(1);
					}
				}
			}
		}
		*/
		org.apache.logging.log4j.core.config.Configurator.initialize("test", Constants.LOG4J_FILE);
	}
	
	public RecurrentSimulator(GasNeatNeuralNetwork neuralNetwork, int stepsPerTick) {
		super(ViewConstants.RUN_TYPE_HEADLESS);
		this.inputTimeSignalMap = new HashMap<Integer, List<Double>>();
		this.neuralNetwork = neuralNetwork;
		this.listsInitialized = this.initLists();
		updateInputNeurons();
		this.createGasDispersionUnit();
		STEPS_PER_TICK = stepsPerTick;
		
	}
	
	private boolean initLists() {
		outputList = new ArrayList<Double>();
		inputList = new ArrayList<Double>();
		networkStateList = new ArrayList<NetworkState>();
		inputNeurons = new ArrayList<Long>();
		currentActivatedNeurons = new ArrayList<Long>();
		return true;
	}

	/**
	 * simulates the neural network
	 * Currently this should only be called when running the GasNEAT GUI launcher
	 */
	public void simulate() {
		
		System.out.println("SHOULD ONLY BE CALLED FROM GUI");
		System.exit(-1);
		
		this.updateInputNeurons();
		this.createGasDispersionUnit();
		timer = new Timer(Constants.AVG_TIMER_DELAY, timerListener);
		this.initPanel(this.runType);
		timer.setDelay(Constants.AVG_TIMER_DELAY);
		timer.start();
		
		if(this.runType == ViewConstants.RUN_TYPE_HEADLESS) {
			int i = this.getMinStartingTime();
			while(this.inputTimeSignalMap.containsKey(i)) {
				double[] array = this.getOutputArray(this.inputTimeSignalMap.get(i));
				try {
					double[] outputs = this.step(array);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		}
	}
	
	
	private int getMinStartingTime() {
		int min = Integer.MAX_VALUE;
		
		for(Integer key : this.inputTimeSignalMap.keySet()) {
			if(key < min) {
				min = key;
			}
		}
		return min;
	}
	
	private double[] getOutputArray(List<Double> ls) {
		double[] output = new double[ls.size()];
		
		for(int i = 0; i < ls.size(); i++) {
			output[i] = ls.get(i);
		}
		
		return output;
	}

	/**
	 * creates Gas Dispersion Unit for every gas emitting neuron
	 */
	private void createGasDispersionUnit() {
		Gas emittedGas;
		
		logger.debug("createGawDispersiodUnit called with " + neuralNetwork.getNeuronMap().entrySet().size()   );
		logger.debug("receiver map:" +  neuralNetwork.getGasReceiverNeuronsMap()   );
		
		
		for (Map.Entry<Long, GasNeatNeuron> entry : neuralNetwork.getNeuronMap().entrySet()) {
			GasNeatNeuron sourceNeuron = entry.getValue();

			// if neuron is gas emitter
			if (sourceNeuron.isGasEmitter()) {
				emittedGas = neuralNetwork.getGasMap().get(sourceNeuron.getGasProductionType());
				
				logger.debug("emittedGas " + emittedGas    );
				logger.debug("neuralNetwork.getGasMap() " + neuralNetwork.getGasMap()   );
				
			
				GasDispersionUnit newGasChannel = new GasDispersionUnit(sourceNeuron.getEmissionRadius(), sourceNeuron.getBaseProduction(), emittedGas);

				newGasChannel.createGasChannel();
				
				for (GasDispersionSlot slot: newGasChannel.getGasDispersionSlotList()) {
					this.logger.debug( slot.toString()  );
				}
				
				logger.debug( sourceNeuron.getGasProductionType()  );
				logger.debug( neuralNetwork.getGasReceiverNeuronsMap() );

				//#GASNEATMODEL
				List<GasNeatNeuron> targetNeuronsList = neuralNetwork.getGasReceiverNeuronsMap().get( sourceNeuron.getGasProductionType() );
				
				
				logger.debug( "targetlist: " + targetNeuronsList  );
				
				newGasChannel.addNeuron(targetNeuronsList, sourceNeuron);
				
				logger.debug( "setting new gasdisperion unit" + newGasChannel );
				sourceNeuron.setGasDispersionUnit(newGasChannel);
				logger.debug( "GAS PRODUCING NEURON CREATED WITH:"  );
				logger.debug( newGasChannel );
				
				if (logger.isDebugEnabled() ) {
					for (GasDispersionSlot slot: newGasChannel.getGasDispersionSlotList()) {
						logger.debug( slot.toString()  );
					}
				}
				
			} else {
				logger.debug("NON GAS NEURON" + sourceNeuron  );
			}
		}
	}

	/**
	 * fills the inputNeurons list with the list of neurons in the input layer
	 */
	public void updateInputNeurons() {
		// Get a set of the entries
		Set neuronMapSet = neuralNetwork.getNeuronMap().entrySet();
		// Get an iterator
		Iterator neuronIterator = neuronMapSet.iterator();
		// Display elements
		while (neuronIterator.hasNext()) {
			Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
			GasNeatNeuron currentNeuron = (GasNeatNeuron) currentEntry.getValue();
			//LOG.info(  currentNeuron.getLayerType()  );
			if (currentNeuron.getLayerType().equals(NeuronType.INPUT)) {
				inputNeurons.add( (Long)currentEntry.getKey() );
			}

		}
	}

	/**
	 *   Each tick of the timer executes the code within actionPerformed() of the timer, which
	 *   includes playing one step of network simulation per timer tick.
	 */  
	ActionListener timerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (replaying) {
				replayStep();
			} else {
				if (doneSimulatingInputs && !playAfterBackStepping)
					stabilizingCounter--;
				if (stabilizingCounter == 0) {
					timer.stop();
					/*TODO: MOVE THIS FUNCTIONALITY TO THE VIEW IN SOME WAY
					 * neuralNetworkFrame.getReplayButton().setEnabled(true);
					neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
					neuralNetworkFrame.getBackstepButton().setEnabled(true);
					neuralNetworkFrame.getForwardstepButton().setEnabled(false);*/
				}

				try {
					playStep();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * plays the networkStateList all over again, to display replay of simulation
	 */
	public void replayStep() {

		if (replayIndex <= networkStateList.size()) {

			if (replayIndex == networkStateList.size()) {
				replayIndex = 0;
				timer.stop();
				/*TODO: MOVE THIS FUNCTIONALITY TO THE VIEW IN SOME WAY
				 * neuralNetworkFrame.getReplayButton().setEnabled(true);
				neuralNetworkFrame.getPlayPauseButton().setEnabled(false);*/
			} else {
				neuralNetworkFrame.updateNeuralNetworkPanel( 
						ViewConstants.REPLAY_STATUS_TEXT, 
						networkStateList.get(replayIndex).getNeuralNetwork() );
				replayIndex++;
			}
		} else {
			replaying = false;
			timer.stop();
			replayIndex = 0;
			/*TODO: MOVE THIS FUNCTIONALITY TO THE VIEW IN SOME WAY
			 * neuralNetworkFrame.getReplayButton().setEnabled(true);
			neuralNetworkFrame.getPlayPauseButton().setEnabled(false);*/
		}
	}

	/**
	 * plays each step of network simulation 
	 * which includes simulating the network, modulating synaptic weights based on Hebbian Plasticity
	 * and then resetting concentrations of activated neurons
	 * 	 
	 * @throws CloneNotSupportedException
	 */
	public void playStep() throws CloneNotSupportedException {
		this.logger.info("Stepping forward (no args)");
		// if play button is hit after backstepping
		if (playAfterBackStepping) {
			backstepIndex++;
			neuralNetwork = networkStateList.get(backstepIndex).getNeuralNetwork();
			neuralNetworkFrame.updateNeuralNetworkPanel(ViewConstants.PLAY_TEXT, neuralNetwork);
		} else {
			// regular play
			tickCount++;
			if (inputTimeSignalMap.containsKey(tickCount)) {
				inputList = (ArrayList<Double>) inputTimeSignalMap.get(tickCount);
				logger.debug(inputList);
				activateInputsNeurons(inputList);
				inputCount++;
			}

			if (inputCount == inputTimeSignalMap.size()) {
				doneSimulatingInputs = true;
			}

			simulateNetwork();
			updateSynapticPlasticityAndWeights();
			updateActivationLevelsAndResetBuiltUpConcentrations();
			
			neuralNetworkFrame.updateNeuralNetworkPanel(ViewConstants.PLAY_TEXT, neuralNetwork);
			
			NetworkState networkState = new NetworkState();
			
			try {
				neuralNetworkClone = neuralNetwork.clone();

			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

			// adding network state to networkStateList for replay, forward and backward stepping
			networkState.setNeuralNetwork(neuralNetworkClone);
			networkStateList.add(networkState);
			backstepIndex = networkStateList.size() - 1;
		}

		if (backstepIndex == networkStateList.size() - 1) {
			backstepIndex--;
			playAfterBackStepping = false;
		}

	}
	
	public void printState() {
		if (logger.isDebugEnabled() ) {
			for ( long neuronID:  neuralNetwork.getNeuronMap().keySet()  ) {
				
				GasNeatNeuron neuron = neuralNetwork.getNeuronMap().get(neuronID);
				logger.debug("  " + neuronID +" |" +  
						"  bufCon: " + neuron.getActivationConcentrationBuffer() +
						"  actCon: " + neuron.getActivationConcentration() +
						"  actLvl: " +  neuron.getActivationLevel() +						
						"  applyFun "+  neuron.getFunction().apply( neuron.getActivationLevel() ) );
				for (long s: neuron.getOutgoingSynapses()) {
					GasNeatSynapse syn= neuralNetwork.getSynapseMap().get(s);
					logger.debug( "    "+syn.getSynapseID() +"w->  "+ syn.getSynapticWeight() );
				}
				logger.debug("");
				
				logger.debug("    " + neuronID +"buffer |" +  neuron.getReceptor().getBufferedConcentration() );
				logger.debug("    " + neuronID +"built^ |" +  neuron.getReceptor().getBuiltUpConcentrations() );
			}
		}
	}
	
	public void printWeightState() {
	/*	
		for ( String neuronID:  neuralNetwork.getNeuronMap().keySet()  ) {
			
			GasNeatNeuron neuron = neuralNetwork.getNeuronMap().get(neuronID);
				logger.debug("  " + neuronID +" |" );
				
				for (String s: neuron.getOutgoingSynapses()) {
					GasNeatSynapse syn= neuralNetwork.getSynapseMap().get(s);
					logger.debug( "    "+syn.getSynapseID() +"w->  "+ syn.getSynapticWeight() );
				}
		}//*/
	}

	
	/**
	 * plays each step of network simulation 
	 * which includes simulating the network, modulating synaptic weights based on Hebbian Plasticity
	 * and then resetting concentrations of activated neurons
	 * 	 
	 * @throws CloneNotSupportedException
	 */
	
	public double[] step( double[] inputs ) throws CloneNotSupportedException {
	
		//ONLY DO THIS FOR TARGETS THIS BREAKS RECURRENCE!
		//TODO decide if this should be done
		//neuralNetwork.clear();
		
		// #GASNEATMODEL
		int numberOfStepsToSimulate=STEPS_PER_TICK;
				
		//#TODO can recover performance if bad here
		//potentially use property to force exact steps per tick
		//numberOfStepsToSimulate=3;
		
		/////////////////////////////
		//Using tick to indicate an input entering the system and ultimately returns an output
		//the number of timesteps to increment in that time depends on the network its
		tickCount++;
		
		inputList = new ArrayList<Double>();
		
		//put inputs from array into arraylist to send to model
		for (int i = 0; i < inputs.length; i++) {
			inputList.add( inputs[i] );
		}
		
		printWeightState();
		logger.debug("--------------------------------STEP STARTED");
		//LOG.debug("Inputs" + inputs[0] +" " +inputs[1]+ " " + inputs[2] );
		logger.debug( "STATE FROM PREVIOUS CONCENTRATIONS" );
		
		//this.LOG.info("activateNeurons(inputList);");
		for (int i=0; i < numberOfStepsToSimulate; i++ ) {

			this.activateInputsNeurons(inputList);
			logger.debug( "STATE AFTER ACTIVATING INPUT NEURONS" );
			printState();
			
			logger.debug("this.simulateNetwork();");
			this.simulateNetwork();
			logger.debug( "STATE AFTER SIMULATING STEP" );
			printState();
			
		}
		
		
		//HERE   TEMP TURN OFF!!!!!
		//TODO FIXME #GASNEATMODEL
		//this.LOG.info("this.modulateSynapses();");
		//this.updateSynapticPlasticityAndWeights();
		

		logger.debug("--------------------------------STEP ENDED");
		
		printWeightState();
		
		double[] outputs = this.neuralNetwork.getOutputValues();
	
		return outputs;

	}
	
	

	/**
	 * Activates neurons based on incoming input signals
	 * 
	 * @param inputSignals
	 */
	public void activateInputsNeurons(List<Double> inputSignals) {
		
		// #GASNEATMODEL
		List<Double> currentSignal = inputSignals;
		for(int index = 0; index < currentSignal.size(); index++) {
			
			long currentNeuron = inputNeurons.get(index);
			
			GasNeatNeuron neuron = neuralNetwork.getNeuronMap().get(currentNeuron);
			//will add appropriate gas to receptor (may have buffered inputs from previous
			//timesteps that will get integrated with this in the buffer)
			neuron.addToActivationConcentrationGasBuffer( currentSignal.get(index)   );
		}
	}

	/**
	 * Getter for list of output signals
	 * @return
	 */
	public ArrayList<Double> getOutputList() {
		return outputList;
	}

	/**
	 * simulates the neural network
	 * 
	 */
	public void simulateNetwork() {
		// #GASNEATMODEL
		
		// We must take the current loaded values in buffer and update our activation levels
		// This is reverse from previous
		for (long neuronID : neuralNetwork.getNeuronMap().keySet()) {
			GasNeatNeuron tempNeuron = neuralNetwork.getNeuronMap().get(neuronID);
			
			/*
			if (!tempNeuron.isGasEmitter() ) {
				this.pushBufferedConcentrationsToBuiltUpConcentrations(tempNeuron);
			} else {
				//ULTRATODO not sure why this handle was here, but we might need to change...
				this.pushBufferedConcentrationsToBuiltUpConcentrations(tempNeuron);
			}
			*/
			this.pushBufferedConcentrationsToBuiltUpConcentrations(tempNeuron);
			
		}
		
		logger.debug("STATE AFTER pushBufferedSynapsesToBuiltUpConcentrations");
		printState();
		
		//need to update plasticity before the next step!
		logger.debug("this.modulateSynapses();");
		this.updateSynapticPlasticityAndWeights();
		
		
		//This should go here now
		this.updateActivationLevelsAndResetBuiltUpConcentrations();
		
		logger.debug("STATE AFTER updateActivationLevelsAndResetBuiltUpConcentrations");
		printState();
		
		
		//VIEW ONLY CODE
		currentActivatedNeurons.clear();
		
		// We should leave buffered concentrations to be present
		// For the next simulation 
		for (long neuronID : neuralNetwork.getNeuronMap().keySet()) {
			GasNeatNeuron tempNeuron = neuralNetwork.getNeuronMap().get(neuronID);
			// updating Target Neurons
			if (tempNeuron.isGasEmitter() ) {
				
				//System.out.println("updateGasConcentrationsAndDispersionUnitsAndEmitGas");
				
				//for (double d: tempNeuron.getReceptor().getBufferedConcentration()) {
				//	System.out.print( d +"  "  );
				//}
				//System.out.println("");
				
				//for (double d: tempNeuron.getReceptor().getBuiltUpConcentrations()) {
				//	System.out.print( d +"  "  );
				//}
				//System.out.println("");
				
				this.updateGasConcentrationsAndDispersionUnitsAndEmitGas(tempNeuron);
				//System.exit(1);
			} else {
				this.loadBufferedConcentrationsInSynapses(tempNeuron);
			}
		}
	}
	
	
	/**
	 * This method takes the given neuron, calculates its activation using its current
	 * concentration (gas or standard) multiplies it by the weight of the synapse
	 * and finally adds that concentration to the buffer of the receptor of the target neuron
	 * 
	 * @param tempNeuron
	 * 
	 */
	private void loadBufferedConcentrationsInSynapses(GasNeatNeuron tempNeuron) {
		
		// #GASNEATMODEL
		
		ArrayList<Long> outgoingSynapses = tempNeuron.getOutgoingSynapses();
		//String gasType = tempNeuron.getSynapseProductionType();
		
		int gasType = tempNeuron.getSynapseProductionTypeInt();
		
		//SLOWDOWN
		for (long synapseID : outgoingSynapses) {
			GasNeatSynapse tempSynapse = neuralNetwork.getSynapseMap().get(synapseID);
			long targetNeuronID = tempSynapse.getTargetNeuron();
			GasNeatNeuron targetNeuron = neuralNetwork.getNeuronMap().get(targetNeuronID);
			
			double concentrationToAdd = tempSynapse.getSynapticWeight() * tempNeuron.calculateActivation();
			/*
			System.out.println("BEFORE[0]:" + targetNeuron.getBufferedConcentration()[0] );
			System.out.println("BEFORE[1]:" + targetNeuron.getBufferedConcentration()[1] );
			System.out.println("BEFORE[2]:" + targetNeuron.getBufferedConcentration()[2] );
			System.out.println("BEFORE[3]:" + targetNeuron.getBufferedConcentration()[3] );
			
			System.out.println("tempSynapse.getSynapticWeight() " + tempSynapse.getSynapticWeight());
			System.out.println("tempNeuron.calculateActivation() " + tempNeuron.calculateActivation());
			System.out.println("concentrationToAdd " + concentrationToAdd);
			//*/
			targetNeuron.getReceptor().addBufferedConcentration(gasType, concentrationToAdd);
			/*
			System.out.println("target "+ targetNeuron);
			System.out.println("AFTER[0]:" + targetNeuron.getBufferedConcentration()[0] );
			System.out.println("AFTER[1]:" + targetNeuron.getBufferedConcentration()[1] );
			System.out.println("AFTER[2]:" + targetNeuron.getBufferedConcentration()[2] );
			System.out.println("AFTER[3]:" + targetNeuron.getBufferedConcentration()[3] );
			
			//*/
			
			GasNeatReceptor.checkReasonableValue(  concentrationToAdd  );
			
			
			
			
		}
		//System.out.println("tempNeuron " + tempNeuron );
		//System.out.println(targetNeuron.getReceptor());
		
		
		
	}
	
	
	/**
	 * @param tempNeuron
	 */
	private void pushBufferedConcentrationsToBuiltUpConcentrations(GasNeatNeuron tempNeuron) {
		
		// #GASNEATMODEL
		//System.out.println("pushBufferedConcentrationsToBuiltUpConcentrations for N" +tempNeuron.getId() );
		//neuralNetwork.getNeuron( "N"+tempNeuron.getId()  ).getReceptor().pushBufferedConcentrations();
		
		//ULTRATODO int ok?
		neuralNetwork.getNeuron( (int)tempNeuron.getId()  ).getReceptor().pushBufferedConcentrations();
	}
	
	/**
	 * This method takes the passed GasNeatNeuron and makes sure its dispersion is distributed
	 * around properly. It then advances the gas propagation/location- afterwards it updates the emission
	 * rate of
	 * @param tempNeuron
	 */
	private void updateGasConcentrationsAndDispersionUnitsAndEmitGas(GasNeatNeuron tempNeuron) {
		logger.debug("Managing gases");
		// #GASNEATMODEL

		tempNeuron.getGasDispersionUnit().updateTargetNeurons(neuralNetwork.getNeuronMap());
		tempNeuron.getGasDispersionUnit().advance();
		
		//#GASNEATMODEL
		//TODO: have to modify here if want to allow non-discrete levels of gas production
		//if (tempNeuron.getActivationConcentration() > tempNeuron.getThreshold()) {
			
		if (tempNeuron.getActivationLevel() > tempNeuron.getThreshold()) {
			logger.debug(tempNeuron.getId()+   "INCREASE");
			tempNeuron.getGasDispersionUnit().increaseStrength();
		} else {
			logger.debug(tempNeuron.getId()+  "DECREASE");
			tempNeuron.getGasDispersionUnit().decreaseStrength();
		}
		
		//gas must be emitted either way
		tempNeuron.getGasDispersionUnit().emitGas();
		
	}

	/**
	 * Performs the Hebbian learning algorithm
	 */
	private void updateSynapticPlasticityAndWeights() {
		
		// #GASNEATMODEL
		
		HashMap<Long, GasNeatSynapse> synapseMap = this.neuralNetwork.getSynapseMap();
		logger.debug("updateSynapticPlasticityAndWeights called"    );
		
		//SLOWDOWN
		for(long synapseName : synapseMap.keySet()) {
			GasNeatSynapse synapse = synapseMap.get(synapseName);
			
			//System.out.println( "  buffered concentrations: "+  neuralNetwork.getNeuronMap().get( synapse.getSourceNeuron() ).getBufferedConcentration()   ); 
			
			printState();
			synapse.updatePlasticity(this.neuralNetwork);
			
			GasNeatNeuron sourceNeuron = this.neuralNetwork.getNeuron(synapse.getSourceNeuron());
			GasNeatNeuron targetNeuron = this.neuralNetwork.getNeuron(synapse.getTargetNeuron());
			double a = synapse.getPriorActivation();
			double b = targetNeuron.calculateActivation();
			
			//if (a != 0) {
			//	System.out.println("a= " + a +" "+ sourceNeuron.getThreshold());
			//	System.out.println("b= " + b +" "+ targetNeuron.getThreshold());
			//}
			//System.out.println("     synapseWeight "+ synapse.getSynapticWeight() );
			
			//if(a >= sourceNeuron.getThreshold() && b >= targetNeuron.getThreshold()) {
				synapse.updateSynapticWeight(sourceNeuron.calculateActivation(a), targetNeuron.calculateActivation(b));
//			} else {
//				synapse.unlearn(sourceNeuron.calculateActivation(a), targetNeuron.calculateActivation(b));
//			}
			synapse.setPriorActivation(sourceNeuron.calculateActivation()   );
			//System.out.println("     synapseWeight "+ synapse.getSynapticWeight() );
		}
	}

	/**
	 * resets concentration of activated neurons 
	 */
	private void updateActivationLevelsAndResetBuiltUpConcentrations() {
		// #GASNEATMODEL
		
		HashMap<Long, GasNeatNeuron> neuronMap = this.neuralNetwork.getNeuronMap();
		
		// Display elements
		for(long neuronKey : neuronMap.keySet()) {
			GasNeatNeuron neuron = neuronMap.get(neuronKey);
			
			//VIEW ONLY CODE
			if(neuron.getActivationConcentration() >= neuron.getThreshold()) {
				this.currentActivatedNeurons.add(neuron.getNeuronID());
			}
			
			// resets the build-up concentration and moves it to the current
			// concentration to be checked for activation
			neuron.updateActivationLevelFromAndResetBuiltUpConcentrations();
		}
	}

	/* (non-Javadoc)
	 * @see gasNEAT.controller.SimulatorInterface#forwardStep()
	 */
	@Override
	public void forwardStep() {
		if (timer.isRunning()) {
			timer.stop();
		}

		if (backstepIndex < networkStateList.size() - 2) {
			backstepIndex++;
			NetworkState currentState = networkStateList.get(backstepIndex);
			neuralNetworkFrame.updateNeuralNetworkPanel(ViewConstants.FORWARDSTEP_TEXT, currentState.getNeuralNetwork());
		} else {
			try {
				playStep();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			if (doneSimulatingInputs) {
				stabilizingCounter--;
			}
			/*if (stabilizingCounter == 0) {
				neuralNetworkFrame.getForwardstepButton().setEnabled(false);
				neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
			}*/
		}

		if (backstepIndex == tickCount - 1)
			finishedSteps = true;
		else
			finishedSteps = false;

		if(finishedSteps) {
			/*TODO: MOVE THIS FUNCTIONALITY TO THE VIEW IN SOME WAY
			 * neuralNetworkFrame.getReplayButton().setEnabled(true);
			neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
			neuralNetworkFrame.getForwardstepButton().setEnabled(false);*/
		}
	}
}
