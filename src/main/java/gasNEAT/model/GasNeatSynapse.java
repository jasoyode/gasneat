package gasNEAT.model;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import com.anji.neat.NeatConfiguration;
import com.anji.util.Properties;

import gasNEAT.builders.SynapseBuilder;
import gasNEAT.configurations.GasNeatConfiguration;

/**
 * This class contains information about synapse links in neural network it has
 * synapse ID, source neuron and target neuron to hold information about each
 * synapse link.
 * 
 * In addition to information of Synapse, it has methods to increase and
 * decrease plasticity of Synapse
 */
public class GasNeatSynapse implements Cloneable {

	/** Unique Id of Synapse*/
	private String synapseID;
	
	/** Source neuron of Synapse*/
	private final String sourceName;
	
	/** Target neuron of Synapse*/
	private final String targetName;
	
	/** Synaptic weight of Synapse*/
	private double synapticWeight;
	
	/** Prior activation of Synapse*/
	private double priorActivation = 0;
	
	/** Plasticity of a Synapse*/
	private double plasticity = -1;
	
	private static Logger logger = Logger.getLogger( GasNeatSynapse.class );
	
	private Properties props;
	private double plasticityParameterA; 
	private double plasticityParameterB; 
	private double plasticityParameterC; 
	private double plasticityParameterD; 
	private double plasticityParameterLR;
	
	private double plasticityDecayRate;
	private double maxSynapticWeight;
	private double minSynapticWeight;
	
	private boolean isModulatory;
	private boolean hasFrozenSynapses;
	
	/**
	 * @param synapseBuilder
	 */
	public GasNeatSynapse(SynapseBuilder synapseBuilder) {
		this.sourceName = synapseBuilder.getSourceNeuronName();
		this.targetName = synapseBuilder.getTargetNeuronName();
		this.synapticWeight = synapseBuilder.getWeight();
		this.synapseID = synapseBuilder.getSynapseID();
		this.isModulatory = synapseBuilder.isModulatory();
		plasticityParameterA = synapseBuilder.getA();
		plasticityParameterB = synapseBuilder.getB();
		plasticityParameterC = synapseBuilder.getC();
		plasticityParameterD = synapseBuilder.getD();
		plasticityParameterLR = synapseBuilder.getLr();
		
		init(  synapseBuilder.getProperties() );
		
	}
	
	public void init(Properties props) {
		this.props = props;
		
		
		//plasticityParameterA = props.getDoubleProperty(GasNeatConfiguration.PLASTICITY_PARAMETER_A_KEY);
		//plasticityParameterB = props.getDoubleProperty(GasNeatConfiguration.PLASTICITY_PARAMETER_B_KEY);
		//plasticityParameterC = props.getDoubleProperty(GasNeatConfiguration.PLASTICITY_PARAMETER_C_KEY);
		//plasticityParameterD = props.getDoubleProperty(GasNeatConfiguration.PLASTICITY_PARAMETER_D_KEY);
		//plasticityParameterLR= props.getDoubleProperty(GasNeatConfiguration.PLASTICITY_PARAMETER_LR_KEY);
		
		
		
		plasticityDecayRate = props.getDoubleProperty(GasNeatConfiguration.HEBBIAN_DECAY_PARAMETER_KEY);
		
		maxSynapticWeight = props.getDoubleProperty(NeatConfiguration.WEIGHT_MAX_KEY);
		minSynapticWeight = props.getDoubleProperty(NeatConfiguration.WEIGHT_MIN_KEY);

		
		boolean frozenModulatorySynapses = props.getBooleanProperty(GasNeatConfiguration.FROZEN_MODULATORY_SYNAPSES_KEY);
		
		
		if (isModulatory && frozenModulatorySynapses ) {
			hasFrozenSynapses = true;
		} else {
			hasFrozenSynapses = false;
		}
		
		
		//System.out.println( "maxSynapticWeight " + maxSynapticWeight   );
		//System.out.println( "minSynapticWeight " + minSynapticWeight   );
		//System.exit(0);
	}

	/**
	 * Updates the Plasticity of the calling Synapse based on the plasticity
	 * modulation function stored in its target neuron receptor i.e. Soltoggio
	 * 
	 * @param network
	 *            Neural Network object
	 */
	public void updatePlasticity(GasNeatNeuralNetwork network) {
		
		
		if ( hasFrozenSynapses ) {
			plasticity  =0.0;
			return;
		}
		
//		logger.debug( "updatePlasticity called"  );
//		logger.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXX");
		//changed to be target instead of source
		GasNeatReceptor receptor = network.getNeuronMap().get(targetName).getReceptor();
		
		if (targetName.equals("N3")) {
		
		logger.debug(  "built up concentration at target neuron: 0->" 
				+ receptor.getBuiltUpConcentrations()[0] + " 1->" 
				+ receptor.getBuiltUpConcentrations()[1] + " 2->" 
				+ receptor.getBuiltUpConcentrations()[2] + " 3->" 
				+ receptor.getBuiltUpConcentrations()[3] );
//		logger.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXX");
		logger.debug( "PRE plasticity = "+ plasticity );
		}
		
		receptor.modulatePlasticityFromConcentrations();
		
		plasticity = receptor.getPlasticity();
		
		if (targetName.equals("N3")) {
			logger.debug( "POST plasticity = "+ plasticity );
		}
	}

	/**
	 * Changes the synapticsWeight of the calling synapse based on Standard
	 * Hebbian
	 * 
	 * @param currentSynapsePriorActivation
	 *            Current Synapse Prior Activation
	 * @param targetNeuronActivation
	 *            Target Neuron Concentration
	 */
	public void updateSynapticWeight(double currentSynapsePriorActivation, double targetNeuronActivation) {
		
		if ( hasFrozenSynapses ) {
			//there is never any changes to synaptic weights if they are modulatory
			return;
		}
		
		/* original Polyworld style hebbian plasticity
		double synapticDiff1 = plasticity 
				* (currentSynapsePriorActivation - 0.5 )
				* (targetNeuronActivation - 0.5);
		*/
		
		synapticWeight = synapticWeight * (1.0 - plasticityDecayRate );
		

		double synapticDiff = plasticity 
				* plasticityParameterLR
				* (
						plasticityParameterA * currentSynapsePriorActivation * targetNeuronActivation + 
						plasticityParameterB * currentSynapsePriorActivation  +
						plasticityParameterC * targetNeuronActivation +
						plasticityParameterD );
		
		synapticWeight += synapticDiff;
		
		if (synapticWeight >= maxSynapticWeight)
			synapticWeight = maxSynapticWeight;
		
		if (synapticWeight <= minSynapticWeight) 
			synapticWeight = minSynapticWeight;
		
	}

	
	/**
	 * Set synapseID of a Synapse
	 * 
	 * @return synapseID Synapse ID
	 */
	public void setSynapseID(String synapseID) {
		this.synapseID = synapseID;
	}

	/**
	 * Return synapseID of a Synapse
	 * 
	 * @return synapseID Synapse ID
	 */
	public String getSynapseID() {
		return synapseID;
	}

	/**
	 * Returns source neuron of a Synapse
	 * 
	 * @return sourceNeuron Source Neuron of Synapse
	 */
	public String getSourceNeuron() {
		return sourceName;
	}

	/**
	 * Return target Neuron of a Synapse
	 * 
	 * @return targetNeuron Target Neuron of Synapse
	 */
	public String getTargetNeuron() {
		return targetName;
	}

	/**
	 * Return Synaptic weight of a Synapse
	 * 
	 * @return synapticWeight Weight a Synapse
	 */
	public double getSynapticWeight() {
		return synapticWeight;
	}

	/**
	 * Sets Synaptic weight of Synapse
	 * 
	 * @param synapticWeight Weight of Synapse
	 */
	public void setSynapticWeight(double synapticWeight) {
		this.synapticWeight = synapticWeight;
	}

	/**
	 * Returns priorActivation
	 * 
	 * @return priorActivation Prior Activation of Synapse
	 */
	public double getPriorActivation() {
		return priorActivation;
	}

	/**
	 * Sets priorActivation
	 * 
	 * @param priorActivation Prior Activation of Synapse
	 */
	public void setPriorActivation(double priorActivation) {
		this.priorActivation = priorActivation;
	}

	/**
	 * Returns clone of Synapse object
	 * 
	 * @return Synapse Clone a Synapse object
	 */
	public GasNeatSynapse clone() throws CloneNotSupportedException {
		return (GasNeatSynapse) super.clone();
	}



}
