/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * created by Philip Tucker on Feb 22, 2003
 */
package gasNEAT.configurations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager; 
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jgap.ChromosomeMaterial;
import org.jgap.IdFactory;
import org.jgap.InvalidConfigurationException;
import org.jgap.NaturalSelector;
import org.jgap.event.EventManager;
import org.jgap.impl.CloneReproductionOperator;
import org.jgap.impl.WeightedRouletteSelector;

import com.anji.integration.SimpleSelector;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;
import com.anji.neat.SingleTopologicalMutationOperator;
import com.anji.nn.ActivationFunctionType;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

import gasNEAT.geneticEncoding.GasNeatConnectionAllele;
import gasNEAT.geneticEncoding.GasNeatConnectionGene;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import gasNEAT.geneticEncoding.GasNeatNeuronGene;
import gasNEAT.geneticOperators.GasNeatAddConnectionMutationOperator;
import gasNEAT.geneticOperators.GasNeatAddGasProducingNeuronMutationOperation;
import gasNEAT.geneticOperators.GasNeatAddNeuronMutationOperator;
import gasNEAT.geneticOperators.GasNeatAddSpatialModulatingNeuronMutationOperation;
import gasNEAT.geneticOperators.GasNeatAddTopologicalModulatingNeuronMutationOperation;
import gasNEAT.geneticOperators.GasNeatCrossoverReproductionOperator;
import gasNEAT.geneticOperators.GasNeatNeuronEmissionMutationOperator;
import gasNEAT.geneticOperators.GasNeatNeuronGasMutationOperator;
import gasNEAT.geneticOperators.GasNeatNeuronPositionMutationOperator;
import gasNEAT.geneticOperators.GasNeatNeuronReceptorMutationOperator;
import gasNEAT.geneticOperators.GasNeatNeuronSynapticGasMutationOperator;
import gasNEAT.geneticOperators.GasNeatNeuronThresholdMutationOperator;
import gasNEAT.geneticOperators.GasNeatPlasticityRulesMutationOperator;
import gasNEAT.geneticOperators.GasNeatPruneMutationOperator;
import gasNEAT.geneticOperators.GasNeatRemoveConnectionMutationOperator;
import gasNEAT.geneticOperators.GasNeatRemoveNeuronMutationOperator;
import gasNEAT.geneticOperators.GasNeatSingleTopologicalMutationOperator;
import gasNEAT.geneticOperators.GasNeatWeightMutationOperator;
import gasNEAT.persistence.GasNeatIdMap;
import lombok.Getter;

/**
 * Extension of JGAP configuration with NEAT-specific features added.
 * 
 * @author Philip Tucker
 */
public class GasNeatConfiguration  extends NeatConfiguration {

/**
 * 
 */
private static final long serialVersionUID = 1L;

private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( GasNeatConfiguration.class );

/**
 * properties key, file in which unique ID sequence number is stored
 */
public static final String ID_FACTORY_KEY = "gasneat.id.file";

private static final short DEFAULT_STIMULUS_SIZE = 3;
private static final short DEFAULT_INITIAL_HIDDEN_SIZE = 0;
private static final short DEFAULT_RESPONSE_SIZE = 3;

/**
 * default survival rate
 */
public static final float DEFAULT_SURVIVAL_RATE = 0.20f;
/**
 * default population size
 */
public static final int DEFAULT_POPUL_SIZE = 100;
/**
 * properties key, dimension of neural net stimulus
 */
public static final String STIMULUS_SIZE_KEY = "stimulus.size";
/**
 * properties key, dimension of neural net response
 */
public static final String RESPONSE_SIZE_KEY = "response.size";
/**
 * properties key, survival rate
 */
public static final String SURVIVAL_RATE_KEY = "survival.rate";
/**
 * properties key, topology mutation type; if true, use "classic" method where at most a single
 * topological mutation occurs per generation per individual
 */
public static final String TOPOLOGY_MUTATION_CLASSIC_KEY = "topology.mutation.classic";
/**
 * properties key, maximum connection weight
 */
public static final String WEIGHT_MAX_KEY = "weight.max";
/**
 * properties key, minimum connection weight
 */
public static final String WEIGHT_MIN_KEY = "weight.min";
/**
 * properties key, population size
 */
public static final String POPUL_SIZE_KEY = "popul.size";
/**
 * properties key, speciation chromosome compatibility excess coefficient
 */
public final static String CHROM_COMPAT_EXCESS_COEFF_KEY = "chrom.compat.excess.coeff";
/**
 * properties key, speciation chromosome compatibility disjoint coefficient
 */
public final static String CHROM_COMPAT_DISJOINT_COEFF_KEY = "chrom.compat.disjoint.coeff";
/**
 * properties key, speciation chromosome compatibility common coefficient
 */
public final static String CHROM_COMPAT_COMMON_COEFF_KEY = "chrom.compat.common.coeff";
/**
 * properties key, speciation threshold
 */
public final static String SPECIATION_THRESHOLD_KEY = "speciation.threshold";
/**
 * properties key, elitism enabled
 */
public final static String ELITISM_KEY = "selector.elitism";
/**
 * properties key, minimum size a specie must be to produce an elite member
 */
public final static String ELITISM_MIN_SPECIE_SIZE_KEY = "selector.elitism.min.specie.size";
/**
 * properties key, enable weighted selection process
 */
public final static String WEIGHTED_SELECTOR_KEY = "selector.roulette";
/**
 * properties key, enable fully connected initial topologies
 */
public final static String INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY = "initial.topology.fully.connected";
/**
 * properties key, number of hidden neurons in initial topology
 */
public final static String INITIAL_TOPOLOGY_NUM_HIDDEN_NEURONS_KEY = "initial.topology.num.hidden.neurons";
/**
 * properties key, activation function type of neurons
 */
public final static String INITIAL_TOPOLOGY_ACTIVATION_KEY = "initial.topology.activation";
/**
 * properties key, activation function type of input neurons
 */
public final static String INITIAL_TOPOLOGY_ACTIVATION_INPUT_KEY = "initial.topology.activation.input";
/**
 * properties key, activation function type of output neurons
 */
public final static String INITIAL_TOPOLOGY_ACTIVATION_OUTPUT_KEY = "initial.topology.activation.output";

/*
 *  GASNEAT SPECIFIC MODEL PROPERTIES
 */
public final static String GAS_COUNT_KEY = "gasneat.gas.count";
public final static String GAS_SPEED_KEY = "gasneat.gas.speed";
public final static String GAS_DECAY_KEY = "gasneat.gas.decay";
public final static String INIT_GAS_EMISSION_RADIUS_KEY = "gasneat.init.emission.radius";
public final static String CUSTOM_INIT_KEY = "gasneat.custom.init";

/**
 *  recurrent cycles properties key
 */
public final static String EXTRA_RECURRENT_CYCLES_KEY = "gasneat.extra.recurrent.cycles";
public final static String HARDCODE_CYCLES_KEY = "gasneat.hardcode.cycles.per.timestep";


//i=pre-synaptic activation level
//j=post-synaptic activation level
// A*i*j + B*i + C*j + D
public final static String PLASTICITY_PARAMETER_A_KEY = "gasneat.plasticity.parameter.a";
public final static String PLASTICITY_PARAMETER_B_KEY = "gasneat.plasticity.parameter.b";
public final static String PLASTICITY_PARAMETER_C_KEY = "gasneat.plasticity.parameter.c";
public final static String PLASTICITY_PARAMETER_D_KEY = "gasneat.plasticity.parameter.d";
public final static String PLASTICITY_PARAMETER_LR_KEY= "gasneat.plasticity.parameter.learning.rate";

public final static String EXCLUSIVE_NEUROMODULATED_PLASTICITY_KEY = "gasneat.neuromodulated.plasticity";

public final static String HEBBIAN_DECAY_PARAMETER_KEY = "gasneat.hebbian.decay.rate";
public final static String INITIAL_PLASTICITY_KEY = "gasneat.initial.plasticity";
public final static String MAXIMUM_PLASTICITY_KEY = "gasneat.maximal.plasticity";
public final static String MINIMUM_PLASTICITY_KEY = "gasneat.minimal.plasticity";

//these are just checked by the other rates
//public final static String SPATIAL_NEUROMODULATION_ENABLED_KEY = "gasneat.spatial.neuromodulation.enabled";
//public final static String TOPOLOGICAL_NEUROMODULATION_ENABLED_KEY = "gasneat.topological.neuronmodulation.enabled";

public static final String MAP_RECEPTOR_FILE = "gasneat.receptor.map.file";

public static final String FROZEN_MODULATORY_SYNAPSES_KEY = "gasneat.frozen.modulatory.synapses";
public static final String TANH_SQUASH_MODULATION_SIGNAL_KEY = "gasneat.tanh.squash.modulatory.signal";



public static final String FLAT_CONCENTRATION_GRADIENT_KEY = "gasneat.flat.concentration";



private static ArrayList<String> receptorList;
private static String receptorMapFilePath;
private static int numberGases;

private static @Getter boolean spatialNeuromodulationEnabled;
private static @Getter boolean topologicalNeuromodulationEnabled;

private static @Getter boolean flatConcentrationGradient;


private static @Getter int initialEmissionRadius;
private static @Getter double gasSpeed;


///////////////////////////
public final static String RANDOMIZE_INPUT_RECEPTORS_KEY = "gasneat.randomize.input.receptors.rate";
public final static String RANDOMIZE_RECEPTORS_KEY = "gasneat.randomize.receptors.rate";
private static @Getter double randomizeInputReceptorsRate;
private static @Getter double randomizeReceptorsRate;

public final static String RANDOMIZE_INPUT_GAS_EMITTED_KEY = "gasneat.randomize.input.gas.emitted.rate";
public final static String RANDOMIZE_INPUT_SYNAPTIC_GAS_KEY = "gasneat.randomize.synaptic.gas.rate";
private static @Getter double randomizeInputGasEmittedRate;
private static @Getter double randomizeInputSynapticGasRate;

public final static String RANDOMIZE_GAS_EMITTED_KEY = "gasneat.randomize.input.gas.emitted.rate";
public final static String RANDOMIZE_SYNAPTIC_GAS_KEY = "gasneat.randomize.synaptic.gas.rate";
private static @Getter double randomizeGasEmittedRate;
private static @Getter double randomizeSynapticGasRate;


////////////////////////
private static @Getter double defaultPlasticityA;
private static @Getter double defaultPlasticityB;
private static @Getter double defaultPlasticityC;
private static @Getter double defaultPlasticityD;
private static @Getter double defaultPlasticityLR;


//#ADDPROPS
private static @Getter double defaultTimingConstant;
private static @Getter double defaultReceptorStrength;

//TODO: can we use this as is?
private GasNeatIdMap gasNeatIdMap;

private Properties props;
private CloneReproductionOperator cloneOper = null;
private GasNeatCrossoverReproductionOperator crossoverOper = null;
private double maxConnectionWeight = Double.MAX_VALUE;
private double minConnectionWeight = -Double.MAX_VALUE;
private ActivationFunctionType inputActivationType;
private ActivationFunctionType outputActivationType;
private ActivationFunctionType hiddenActivationType;

/**
 * See <a href=" {@docRoot}/params.htm" target="anji_params">Parameter Details </a> for
 * specific property settings.
 * 
 * @param newProps
 * @see GasNeatConfiguration#init(Properties)
 * @throws InvalidConfigurationException
 */
public GasNeatConfiguration( Properties newProps ) throws InvalidConfigurationException {
	super(newProps, false);
	init( newProps );
}

public static void resetReceptorMap() {
	receptorList = null;
}

public static ArrayList<String> getReceptorMap() {
	
	if (receptorList == null) {
		receptorList = new ArrayList<String>(); 
		try {
			BufferedReader receptorFile = new BufferedReader( new FileReader( receptorMapFilePath ));
			String currentLine = receptorFile.readLine();
			
			
			
			for (int i=0; currentLine  != null; i++) {
				
				System.err.println( i + " currentLine " + currentLine);
				
				receptorList.add( currentLine );
				currentLine = receptorFile.readLine();
			}
			receptorFile.close();
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not read file: [" + receptorMapFilePath +"]");
			e.printStackTrace();
			System.exit(1);
		}
	}
	return receptorList;

}



/**
 * Initialize mutation operators.
 * 
 * @throws InvalidConfigurationException
 */
private void initMutation() throws InvalidConfigurationException {
	
	//TODO: MUST ADD MORE MUTATIONS
	
	// remove connection
	GasNeatRemoveConnectionMutationOperator removeOperator = (GasNeatRemoveConnectionMutationOperator) props
			.singletonObjectProperty( GasNeatRemoveConnectionMutationOperator.class );
	if ( ( removeOperator.getMutationRate() > 0.0f )
			&& ( removeOperator.getMaxWeightRemoved() > 0.0f ) )
		addMutationOperator( removeOperator );

	// add topology
	boolean isTopologyMutationClassic = props.getBooleanProperty( TOPOLOGY_MUTATION_CLASSIC_KEY,
			false );
	if ( isTopologyMutationClassic ) {
		System.out.println("NO: do not use classic topology mode");
		System.exit(1);
		SingleTopologicalMutationOperator singleOperator = (SingleTopologicalMutationOperator) props
				.singletonObjectProperty( SingleTopologicalMutationOperator.class );
		if ( singleOperator.getMutationRate() > 0.0f )
			addMutationOperator( singleOperator );
	}
	else {
		// add connection
		GasNeatAddConnectionMutationOperator addConnOperator = (GasNeatAddConnectionMutationOperator) props
				.singletonObjectProperty( GasNeatAddConnectionMutationOperator.class );
		if ( addConnOperator.getMutationRate() > 0.0f )
			addMutationOperator( addConnOperator );
		
		
		//add neuron
		GasNeatAddNeuronMutationOperator addNeuronOperator = (GasNeatAddNeuronMutationOperator) props
				.singletonObjectProperty( GasNeatAddNeuronMutationOperator.class );
		if ( addNeuronOperator.getMutationRate() > 0.0f )
			addMutationOperator( addNeuronOperator );

	}

	// modify weight
	GasNeatWeightMutationOperator weightOperator = (GasNeatWeightMutationOperator) props
			.singletonObjectProperty( GasNeatWeightMutationOperator.class );
	if ( weightOperator.getMutationRate() > 0.0f )
		addMutationOperator( weightOperator );

	//////////////////
	
	
	GasNeatNeuronPositionMutationOperator positionOperator = (GasNeatNeuronPositionMutationOperator) props
			.singletonObjectProperty( GasNeatNeuronPositionMutationOperator.class );
	if ( positionOperator.getMutationRate() > 0.0f )
		addMutationOperator( positionOperator );

	// change threshold of one neuron
	GasNeatNeuronThresholdMutationOperator thresholdOperator = (GasNeatNeuronThresholdMutationOperator) props
			.singletonObjectProperty( GasNeatNeuronThresholdMutationOperator.class );
	if ( thresholdOperator.getMutationRate() > 0.0f )
		addMutationOperator( thresholdOperator );
	
	
	//////////
	// change gas production of one neuron
	GasNeatNeuronGasMutationOperator gasOperator = (GasNeatNeuronGasMutationOperator) props
			.singletonObjectProperty( GasNeatNeuronGasMutationOperator.class );
	if ( gasOperator.getMutationRate() > 0.0f )
		addMutationOperator( gasOperator );
	//*/
	
	//changes Receptor type of a neuron
	GasNeatNeuronReceptorMutationOperator receptorOperator = (GasNeatNeuronReceptorMutationOperator) props
			.singletonObjectProperty( GasNeatNeuronReceptorMutationOperator.class );
	if ( receptorOperator.getMutationRate() > 0.0f )
		addMutationOperator( receptorOperator );
	
	//#GASNEATMODEL
	//add gas producing neuron - this is spatial or topological
	GasNeatAddGasProducingNeuronMutationOperation addGasNeuronOperator = (GasNeatAddGasProducingNeuronMutationOperation) props
			.singletonObjectProperty( GasNeatAddGasProducingNeuronMutationOperation.class );
	if ( addGasNeuronOperator.getMutationRate() > 0.0f )
		addMutationOperator( addGasNeuronOperator );

	
	
	//this is exclusively spatial, diffusive gas producing neuron
	GasNeatAddSpatialModulatingNeuronMutationOperation addSpatialModulatingNeuronOperator = (GasNeatAddSpatialModulatingNeuronMutationOperation) props
			.singletonObjectProperty( GasNeatAddSpatialModulatingNeuronMutationOperation.class );
	if ( addSpatialModulatingNeuronOperator.getMutationRate() > 0.0f )
		addMutationOperator( addSpatialModulatingNeuronOperator );
	
	//changes the strength of the gas emission of a neuron
	GasNeatNeuronEmissionMutationOperator emissionOperator = (GasNeatNeuronEmissionMutationOperator) props
			.singletonObjectProperty( GasNeatNeuronEmissionMutationOperator.class );
	if ( emissionOperator.getMutationRate() > 0.0f )
		addMutationOperator( emissionOperator );
	
	
	
	//changes the strength of the gas emission of a neuron
	GasNeatPlasticityRulesMutationOperator rulesOperator = (GasNeatPlasticityRulesMutationOperator) props
			.singletonObjectProperty( GasNeatPlasticityRulesMutationOperator.class );
	if ( rulesOperator.getMutationRate() > 0.0f )
		addMutationOperator( rulesOperator );
	
	// change gas produced via synaptic connections - used for topological neuromodulation
	GasNeatNeuronSynapticGasMutationOperator synapticGasOperator = (GasNeatNeuronSynapticGasMutationOperator) props
			.singletonObjectProperty( GasNeatNeuronSynapticGasMutationOperator.class );
	if ( synapticGasOperator.getMutationRate() > 0.0f )
		addMutationOperator( synapticGasOperator );
	
	GasNeatRemoveNeuronMutationOperator removeNeuronMutationOperator = (GasNeatRemoveNeuronMutationOperator) props
		.singletonObjectProperty( GasNeatRemoveNeuronMutationOperator.class );
	if ( removeNeuronMutationOperator.getMutationRate() > 0.0f ) {
		addMutationOperator( removeNeuronMutationOperator );
	}
	
	// prune - apparently this should be the final operation
	GasNeatPruneMutationOperator pruneOperator = (GasNeatPruneMutationOperator) props
		.singletonObjectProperty( GasNeatPruneMutationOperator.class );
	if ( pruneOperator.getMutationRate() > 0.0f )
		addMutationOperator( pruneOperator );
	
}


private void throwInvalidConfigurationException(String message) {
		try {
			throw new InvalidConfigurationException( message );
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
}


private void parameterSanityCheck( ) {
	
	
	ArrayList<String> receptors = getReceptorMap();
	int numReceptors = receptors.size();
	
	//set of all gases that can activate a receptor
	HashSet<Integer> gasesThatActivateReceptors = new HashSet<Integer>();
	
	//set of all gases that can modulate a receptor
	HashSet<Integer> gasesThatModulateReceptors = new HashSet<Integer>();
	
	//set of all gases that can modulate a neuron's synaptic plasticity
	HashSet<Integer> gasesThatModulatePlasticity = new HashSet<Integer>();
	
	//set of all gases that can modulate a neuron's neural activation
	HashSet<Integer> gasesThatModulateActivation = new HashSet<Integer>();
	
	System.err.println(  receptors  );
	
	for (String receptorType: receptors) {
		
		System.err.println(  receptorType );
		
		//#RECEPTORHARDCODE
		String activationType = receptorType.substring(1, 2);
		if ( !activationType.equals("0")) {
			int gasToTrack = Integer.parseInt( activationType );
			if (gasToTrack > numberGases) {
				throwInvalidConfigurationException("BAD CONFIG: receptor for gas that will never be created! ");
			}
			gasesThatActivateReceptors.add( gasToTrack );
		}
		
		//#RECEPTORHARDCODE
		for (int i=4; i <= 13; i+=3) {
			String gasType = receptorType.substring(i, i+1);
			if ( !gasType.equals("O") ) {
				int gasToTrack = Integer.parseInt( gasType );
				if (gasToTrack > numberGases) {
					System.out.println( gasToTrack  );
					throwInvalidConfigurationException("BAD CONFIG: receptor for gas ["+gasToTrack+"] that will never be created!");
				}
				gasesThatModulateReceptors.add( gasToTrack  );
			}
		}
		
		//#RECEPTORHARDCODE
		for (int i=4; i < 10; i+=3) {
			String gasType = receptorType.substring(i, i+1);
			if ( !gasType.equals("O") ) {
				int gasToTrack = Integer.parseInt( gasType );
				if (gasToTrack > numberGases) {
					throwInvalidConfigurationException("BAD CONFIG: receptor for gas that will never be created!");
				}
				gasesThatModulateActivation.add( gasToTrack  );
			}
		}
		
		//#RECEPTORHARDCODE
		for (int i=10; i <= 13; i+=3) {
			String gasType = receptorType.substring(i, i+1);
			if ( !gasType.equals("O") ) {
				int gasToTrack = Integer.parseInt( gasType );
				if (gasToTrack > numberGases) {
					throwInvalidConfigurationException("BAD CONFIG: receptor for gas that will never be created!");
				}
				gasesThatModulatePlasticity.add( gasToTrack  );
			}
		}
	}
	
	boolean modulationHappens = false;
	boolean activationModulationHappens = false;
	boolean plasticityModulationHappens = false;
	
	for (int i=1; i<= numberGases; i++) {
		if ( !gasesThatActivateReceptors.contains(i)
				&& !gasesThatModulateReceptors.contains(i)
			
				) {
			System.out.println( gasesThatActivateReceptors);
			System.out.println(gasesThatModulateReceptors);
			
			throwInvalidConfigurationException("BAD CONFIG: gases could be generated that don't do anything!: " + i);
		}
		
		if ( gasesThatModulateReceptors.contains(i) ) {
			modulationHappens = true;
		}
		
		if ( gasesThatModulateActivation.contains(i) ) {
			activationModulationHappens = true;
		}
		
		if ( gasesThatModulatePlasticity.contains(i) ) {
			plasticityModulationHappens = true;
		}
		
	}
	
	
	if ( spatialNeuromodulationEnabled ) {
		if ( gasSpeed <= 0 ) {
			throwInvalidConfigurationException("BAD CONFIG: gas speed should be > 0 if spatial enabled ");
		}
		if ( numberGases <= 0) {
			throwInvalidConfigurationException("BAD CONFIG: if spatial enabled, you must have at leas 1 gas ");
		}
		
	}
	
	if (topologicalNeuromodulationEnabled) {
		if ( numberGases <= 0) {
			throwInvalidConfigurationException("BAD CONFIG: if topological nm enabled must include at least 1 gas ");
		}
		if ( ! modulationHappens) {
			throwInvalidConfigurationException("Modulation cannot happen with these receptors!");
		}
	}
	
	if (randomizeInputReceptorsRate > 0 && randomizeReceptorsRate <= 0) {
		throwInvalidConfigurationException("WEIRD CONFIG: you only want to randomize receptor in input neurons?! ");
	}
	
	if (numReceptors == 0 ) {
		throwInvalidConfigurationException("BAD CONFIG: Must have at least one receptor! ");
	} else if (numReceptors == 1 ) {
		
		if (randomizeInputReceptorsRate > 0 || randomizeReceptorsRate > 0 ) {
			throwInvalidConfigurationException("BAD CONFIG: don't randomize receptors if you only have a single receptor! ");
		}
	}
	
	if (numberGases <= 0) { 
		if ( randomizeInputGasEmittedRate > 0 || randomizeInputSynapticGasRate > 0) {
			throwInvalidConfigurationException("BAD CONFIG: if randomizing gas should have more than 0!");
		}
		
		
	}

	
	if (numberGases ==0 ) {
		if (spatialNeuromodulationEnabled || topologicalNeuromodulationEnabled) {
			throwInvalidConfigurationException("Number of gases set to zero, which means neither form of neuromodulation (spatial or topological) should NOT be enabled!");
		}
		
	}
	
	if ( props.getIntProperty(HARDCODE_CYCLES_KEY) > 0 
			&& props.getIntProperty(EXTRA_RECURRENT_CYCLES_KEY) > 0) {
		
		System.err.println("You should only hardcode the number of cycles per timestep,");
		System.err.println("or set the number of extra cycles to run each step, which calculates");
		System.err.println("the minimum number of cycles needed to reach the output from input.");
		throwInvalidConfigurationException("Hardcoded value ovverrides the extra recurrent cycles.");
	}
	
	if (numReceptors > 1 &&
			0 >= props.getDoubleProperty(GasNeatNeuronReceptorMutationOperator.NEURON_RECEPTOR_MUTATE_RATE_KEY )){ 
		throwInvalidConfigurationException("You have multiple receptors but no mutation to allow them to change!");
	}
	
	if (numReceptors < 2 &&
			0 < props.getDoubleProperty(GasNeatNeuronReceptorMutationOperator.NEURON_RECEPTOR_MUTATE_RATE_KEY )){ 
		throwInvalidConfigurationException("You have only have a single receptor, so you should not mutate receptors!");
	}
	
	//standard
	double addConnRate = props.getDoubleProperty(GasNeatAddConnectionMutationOperator.ADD_CONN_MUTATE_RATE_KEY);
	double weightMutateRate = props.getDoubleProperty( GasNeatWeightMutationOperator.WEIGHT_MUTATE_RATE_KEY );
	double removeConnRate = props.getDoubleProperty(GasNeatRemoveConnectionMutationOperator.REMOVE_CONN_MUTATE_RATE_KEY );	
	double addNeuronRate = props.getDoubleProperty(GasNeatAddNeuronMutationOperator.GASNEAT_ADD_NEURON_MUTATE_RATE_KEY);

	if (addConnRate > 0 && weightMutateRate <= 0 ) {
		throwInvalidConfigurationException("If allow connections to be added, their weights should be able to change as well.");
	}
	
	
	//check for
	
	
	
	//experimental
	double removeNeuronRate = props.getDoubleProperty( GasNeatRemoveNeuronMutationOperator.REMOVE_NEURON_MUTATE_RATE_KEY );

	//must make sure that emission rate is on if we are adding spatial neuron
	double addSpatialNeuronRate = props.getDoubleProperty(GasNeatAddSpatialModulatingNeuronMutationOperation.GASNEAT_ADD_SPATIAL_MODULATING_NEURON_MUTATE_RATE_KEY);
	double emissionRate = props.getDoubleProperty(GasNeatNeuronEmissionMutationOperator.NEURON_EMISSION_RATE_MUTATE_RATE_KEY);

	if ( addSpatialNeuronRate > 0 && emissionRate <= 0) {
		throwInvalidConfigurationException("You should enabled emission mutations if you are allowing spatial modulation neurons!");
	}
	
	double addTopologicalRate = props.getDoubleProperty(GasNeatAddTopologicalModulatingNeuronMutationOperation.GASNEAT_ADD_TOPOLOGICAL_MODULATING_NEURON_MUTATE_RATE_KEY );
	if (addTopologicalRate > 0 && numberGases <= 0) {
		throwInvalidConfigurationException("You must allow at least 1 type of gas to enabled adding topological modulation neurons");
	}
	
	double mutateGasRate = props.getDoubleProperty(GasNeatNeuronGasMutationOperator.NEURON_GAS_MUTATE_RATE_KEY );
	double mutateSynGasRate = props.getDoubleProperty(GasNeatNeuronSynapticGasMutationOperator.NEURON_SYNAPTIC_GAS_MUTATE_RATE_KEY  );
	
	//force zero specification, because it is not obsolete!
	double addGasProducingRate = props.getDoubleProperty(GasNeatAddGasProducingNeuronMutationOperation.GASNEAT_ADD_GAS_NEURON_MUTATE_RATE_KEY );

	
	if ( addGasProducingRate > 0 && ( addTopologicalRate > 0 || 
										addSpatialNeuronRate > 0)
			) {
		throwInvalidConfigurationException("GasNeatAddGasProducingNeuronMutationOperation is legacy and should be the only addModulatoryNeuron mutation enabled if at all");
	}
	
	double plasticRuleRate = props.getDoubleProperty(GasNeatPlasticityRulesMutationOperator.PLASTICITY_RULES_MUTATE_RATE_KEY  );
	
	if (plasticityModulationHappens) {
		//if rules cant change
		if (plasticRuleRate <= 0
				//and learning rate is 0
				&& (defaultPlasticityLR == 0
						//or all parameters are set to zero
						|| (  defaultPlasticityA == 0
								&& defaultPlasticityB == 0
								&& defaultPlasticityC == 0
								&& defaultPlasticityD == 0
							)
					)
				) {
			throwInvalidConfigurationException("You have plasticity modulation possible, but disabled by your plasticity settings" );
		}
	}

	
	//MAKE SURE OF SOME THINGS!

	/*
	 * 1. read about balance between add and remove

	############################################# ENABLE GAS PRODUCED MUTATION
	gasneat.neuron.gas.mutation.rate=0.25
	
	############################################# Neuromodulation settings
	gasneat.plasticity.modulation.enabled=true
	gasneat.activation.modulation.enabled=true
	gasneat.frozen.modulatory.synapses=false
	gasneat.tanh.squash.modulatory.signal=false
	
	############################################# DESTRUCTIVE  - perhaps to be ommitted
	gasneat.neuron.synaptic.gas.mutation.rate=0.0
	gasneat.add.gas.neuron.mutation.rate=0.0
	
	############################################# SETUP
	gasneat.randomize.input.receptors.rate=0.0
	gasneat.randomize.receptors.rate=0.0
	gasneat.randomize.input.gas.emitted.rate=0.0
	gasneat.randomize.synaptic.gas.rate=0.0
	gasneat.randomize.input.gas.emitted.rate=0.0
	gasneat.randomize.synaptic.gas.rate=0.0
	gasneat.spatial.neuromodulation.enabled=true
	gasneat.topological.neuronmodulation.enabled=true
	############################################# GENERATIONS, ONLY INTRODUCE MUTATIONS AFTER GENERATION#
	gasneat.add.neuron.mutation.generation=0
	gasneat.add.spatial.modulating.neuron.generation=0
	gasneat.add.topological.modulating.neuron.mutation.generation=0
	gasneat.add.gas.neuron.mutation.generation=0
	####################
	# stimuli, targets, activations
	####################
	stimuli.file=experiments/temporary_mutable/stimuli.txt
	targets.file=experiments/temporary_mutable/targets.txt
	stimulus.size=1
	response.size=1
	*/
	
	
	
}



/**
 * See <a href=" {@docRoot}/params.htm" target="anji_params">Parameter Details </a> for
 * specific property settings.
 * 
 * @param newProps configuration parameters; newProps[SURVIVAL_RATE_KEY] should be < 0.50f
 * @throws InvalidConfigurationException
 */
private void init( Properties newProps ) throws InvalidConfigurationException {
	
	//always reset to start
	resetReceptorMap();
	
	props = newProps;
	
	//default to true for backcompat
	flatConcentrationGradient = props.getBooleanProperty( FLAT_CONCENTRATION_GRADIENT_KEY, true );
	
	
	defaultPlasticityA = props.getDoubleProperty( PLASTICITY_PARAMETER_A_KEY );
	defaultPlasticityB = props.getDoubleProperty( PLASTICITY_PARAMETER_B_KEY );
	defaultPlasticityC = props.getDoubleProperty( PLASTICITY_PARAMETER_C_KEY );
	defaultPlasticityD = props.getDoubleProperty( PLASTICITY_PARAMETER_D_KEY );
	defaultPlasticityLR = props.getDoubleProperty( PLASTICITY_PARAMETER_LR_KEY );
	
	numberGases = props.getIntProperty( GAS_COUNT_KEY );
	
	//default is turned on
	spatialNeuromodulationEnabled = 0 < props.getDoubleProperty( GasNeatAddSpatialModulatingNeuronMutationOperation.GASNEAT_ADD_SPATIAL_MODULATING_NEURON_MUTATE_RATE_KEY );
	topologicalNeuromodulationEnabled =  0 < props.getDoubleProperty(GasNeatAddTopologicalModulatingNeuronMutationOperation.GASNEAT_ADD_TOPOLOGICAL_MODULATING_NEURON_MUTATE_RATE_KEY );
	//
	randomizeInputReceptorsRate = props.getDoubleProperty( RANDOMIZE_INPUT_RECEPTORS_KEY, 0.0 );
	randomizeReceptorsRate = props.getDoubleProperty( RANDOMIZE_RECEPTORS_KEY, 0.5 );
	//
	randomizeInputGasEmittedRate = props.getDoubleProperty( RANDOMIZE_INPUT_GAS_EMITTED_KEY, 0 );
	randomizeInputSynapticGasRate = props.getDoubleProperty( RANDOMIZE_INPUT_SYNAPTIC_GAS_KEY, 0 );
	//
	randomizeGasEmittedRate = props.getDoubleProperty( RANDOMIZE_GAS_EMITTED_KEY, 0.5 );
	randomizeSynapticGasRate = props.getDoubleProperty( RANDOMIZE_SYNAPTIC_GAS_KEY, 0.5 );
	
	initialEmissionRadius = props.getIntProperty( INIT_GAS_EMISSION_RADIUS_KEY, 300 );
	gasSpeed = props.getDoubleProperty( GAS_SPEED_KEY, 33 );
	
	
	if (numberGases >0 ) {
		if (!spatialNeuromodulationEnabled && !topologicalNeuromodulationEnabled) {
			System.err.println("Number of gases greater than zero, which means one form of neuromodulation (spatial or topological) or the other should be enabled!");
			System.exit(1);
		}
		
	}
	
	receptorMapFilePath = props.getProperty( MAP_RECEPTOR_FILE, "");
	
	Randomizer r = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	setRandomGenerator( r.getRand() );
	setEventManager( new EventManager() );
	
	
	
	
	parameterSanityCheck();
	

	// id persistence
	String s = props.getProperty( ID_FACTORY_KEY, null );
	try {
		if ( s != null )
			setIdFactory( new IdFactory( s ) );
	}
	catch ( IOException e ) {
		String msg = "could not load IDs";
		logger.error( msg, e );
		throw new InvalidConfigurationException( msg );
	}

	// make sure numbers add up
	float survivalRate = props.getFloatProperty( SURVIVAL_RATE_KEY, DEFAULT_SURVIVAL_RATE );
	float crossoverSlice = 1.0f - ( 2.0f * survivalRate );
	if ( crossoverSlice < 0.0f )
		throw new InvalidConfigurationException( "survival rate too large: " + survivalRate );

	// selector
	NaturalSelector selector = null;
	if ( props.getBooleanProperty( WEIGHTED_SELECTOR_KEY, false ) )
		selector = new WeightedRouletteSelector();
	else
		selector = new SimpleSelector();
	selector.setSurvivalRate( survivalRate );
	selector.setElitism( props.getBooleanProperty( ELITISM_KEY, true ) );
	selector.setElitismMinSpecieSize( props.getIntProperty( ELITISM_MIN_SPECIE_SIZE_KEY, 6 ) );
	setNaturalSelector( selector );

	// reproduction
	cloneOper = new CloneReproductionOperator();
	crossoverOper = new GasNeatCrossoverReproductionOperator();
	
	getCloneOperator().setSlice( survivalRate );
	
	getCrossoverOperator().setSlice( crossoverSlice );
	
	addReproductionOperator( getCloneOperator() );
	addReproductionOperator( getCrossoverOperator() );

	// mutation
	initMutation();

	// population
	setPopulationSize( props.getIntProperty( POPUL_SIZE_KEY, DEFAULT_POPUL_SIZE ) );
	hiddenActivationType = ActivationFunctionType.valueOf( props.getProperty(
			INITIAL_TOPOLOGY_ACTIVATION_KEY, ActivationFunctionType.SIGMOID.toString() ) );
	inputActivationType = ActivationFunctionType.valueOf( props.getProperty(
			INITIAL_TOPOLOGY_ACTIVATION_INPUT_KEY, null ) );
	if ( inputActivationType == null )
		inputActivationType = hiddenActivationType;
	outputActivationType = ActivationFunctionType.valueOf( props.getProperty(
			INITIAL_TOPOLOGY_ACTIVATION_OUTPUT_KEY, null ) );
	if ( outputActivationType == null )
		outputActivationType = hiddenActivationType;
	load();
	
	ChromosomeMaterial sample = NeatChromosomeUtility.newSampleChromosomeMaterial( props
			.getShortProperty( STIMULUS_SIZE_KEY, DEFAULT_STIMULUS_SIZE ), props.getShortProperty(
			INITIAL_TOPOLOGY_NUM_HIDDEN_NEURONS_KEY, DEFAULT_INITIAL_HIDDEN_SIZE ), props
			.getShortProperty( RESPONSE_SIZE_KEY, DEFAULT_RESPONSE_SIZE ), this, props
			.getBooleanProperty( INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY, true ) );
	
	//Customizing for gas only evolution
	//*/
	SortedSet alleles = sample.getAlleles();	
	
	//REMOVE
	//List excess = sample.extractExcessAlleles(sample.getAlleles(), 7);
	//alleles.removeAll( excess );
	
	if (  props.getBooleanProperty( CUSTOM_INIT_KEY )) {
		initializeAlleles(alleles);
	}
	
	sample.setAlleles( alleles);
	//System.out.println( sample );
	
	setSampleChromosomeMaterial( sample );
	store();

	// weight bounds
	minConnectionWeight = props.getDoubleProperty( WEIGHT_MIN_KEY, -Double.MAX_VALUE );
	maxConnectionWeight = props.getDoubleProperty( WEIGHT_MAX_KEY, Double.MAX_VALUE );

	// speciation parameters
	initSpeciationParms();
}
	
//custom function for setting up initial alleles
public void initializeAlleles( SortedSet alleles ) {
	
	/*
	spatialNeuromodulationEnabled = props.getBooleanProperty(SPATIAL_NEUROMODULATION_ENABLED_KEY, true);
	topologicalNeuromodulationEnabled = props.getBooleanProperty(TOPOLOGICAL_NEUROMODULATION_ENABLED_KEY, true);
	randomizeInputReceptors = props.getBooleanProperty( RANDOMIZE_INPUT_RECEPTORS_KEY, false );
	randomizeReceptors = props.getBooleanProperty( RANDOMIZE_RECEPTORS_KEY, true );
	randomizeInputGasEmitted = props.getBooleanProperty( RANDOMIZE_INPUT_GAS_EMITTED_KEY, false );
	randomizeInputSynapticGas = props.getBooleanProperty( RANDOMIZE_INPUT_SYNAPTIC_GAS_KEY, false );
	randomizeGasEmitted = props.getBooleanProperty( RANDOMIZE_GAS_EMITTED_KEY, true );
	randomizeSynapticGas = props.getBooleanProperty( RANDOMIZE_SYNAPTIC_GAS_KEY, true );
	//*/
	
	Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
	Random rand = randomizer.getRand();
	logger.info("initializeAlleles called");
	
	for (Object allele: alleles)  {
		
		if ( allele.getClass().equals(GasNeatNeuronAllele.class) ) {
			GasNeatNeuronAllele neuronAllele = (GasNeatNeuronAllele)allele;
			
			//should all be standard speed for now
			neuronAllele.setGasSpeed( gasSpeed  );
			
			//radius mutation needs to be added, does not exist currently
			neuronAllele.setGasEmissionRadius(  initialEmissionRadius ); 
			
			//all standard neurons to start
			neuronAllele.setSynapticGasEmissionType( 0 );
			
			///for now all neurons have standard receptors
			neuronAllele.setReceptorTypeToDefault();
			
			if (  neuronAllele.getReceptorType().trim().equals("")) {
				System.err.println("WHY IS EMPTY?");
				System.exit(1);
			}
			//
			logger.info( neuronAllele.getReceptorType()  );
			
			
			if (neuronAllele.getType() == NeuronType.INPUT ) {
				
				//only change to gas type if this is > 0
				if ( randomizeInputGasEmittedRate > 0 ) {
					if ( rand.nextDouble() < randomizeInputGasEmittedRate ) {
						neuronAllele.setGasEmissionType( 1 + rand.nextInt( numberGases)  );
					}
				}
				//only change synaptic gas if this is > 0
				if ( randomizeInputSynapticGasRate > 0) {
					if ( rand.nextDouble() < randomizeInputSynapticGasRate ) {
						neuronAllele.setSynapticGasEmissionType( 1 + rand.nextInt( numberGases)  );
					}
				}
				//only change rceptor if this is great than 0
				if (randomizeInputReceptorsRate > 0) {
					if ( rand.nextDouble() < randomizeInputReceptorsRate ) {
						neuronAllele.setReceptorType(  receptorList.get( rand.nextInt(receptorList.size()) )  );
					}
				}
				
				
			} else if (neuronAllele.getType() == NeuronType.OUTPUT) {
				
				
				//only change to gas type if this is > 0
				if ( randomizeGasEmittedRate > 0 ) {
					if ( rand.nextDouble() < randomizeGasEmittedRate ) {
						neuronAllele.setGasEmissionType( 1 + rand.nextInt( numberGases)  );
					}
				}
				//only change synaptic gas if this is > 0
				if ( randomizeSynapticGasRate > 0) {
					if ( rand.nextDouble() < randomizeSynapticGasRate ) {
						neuronAllele.setSynapticGasEmissionType( 1 + rand.nextInt( numberGases)  );
					}
				}
				//only change rceptor if this is great than 0
				if (randomizeReceptorsRate > 0) {
					if ( rand.nextDouble() < randomizeReceptorsRate ) {
						neuronAllele.setReceptorType(  receptorList.get( rand.nextInt(receptorList.size()) ) );
					}
				}

			} else {
				System.err.println("Cannot have a hidden node in init!");
				System.exit(1);
			}
		} else  {
			System.err.println("Cannot have a non neuron allele in this mode!");
			System.exit(1);
		}
		
		
		
		
	}
}


//Using this feature 
private void initGasModulationModes() {
	
	try {
		getSpeciationParms().setSpecieCompatExcessCoeff(
				props.getDoubleProperty( CHROM_COMPAT_EXCESS_COEFF_KEY ) );
	} catch ( RuntimeException e ) {
		logger.info( "no speciation compatibility threshold specified", e );
	}
	
	
	
}


private void initSpeciationParms() {
	try {
		getSpeciationParms().setSpecieCompatExcessCoeff(
				props.getDoubleProperty( CHROM_COMPAT_EXCESS_COEFF_KEY ) );
	}
	catch ( RuntimeException e ) {
		logger.info( "no speciation compatibility threshold specified", e );
	}
	try {
		getSpeciationParms().setSpecieCompatDisjointCoeff(
				props.getDoubleProperty( CHROM_COMPAT_DISJOINT_COEFF_KEY ) );
	}
	catch ( RuntimeException e ) {
		logger.info( "no speciation compatibility threshold specified", e );
	}
	try {
		getSpeciationParms().setSpecieCompatCommonCoeff(
				props.getDoubleProperty( CHROM_COMPAT_COMMON_COEFF_KEY ) );
	}
	catch ( RuntimeException e ) {
		logger.info( "no speciation compatibility threshold specified", e );
	}
	try {
		getSpeciationParms().setSpeciationThreshold(
				props.getDoubleProperty( SPECIATION_THRESHOLD_KEY ) );
	}
	catch ( RuntimeException e ) {
		logger.info( "no speciation compatibility threshold specified", e );
	}
}

/**
 * factory method to construct new neuron allele with unique innovation ID of specified
 * <code>type</code>
 * 
 * @param type
 * @return NeuronAllele
 */
public NeuronAllele newNeuronAllele( NeuronType type ) {
	ActivationFunctionType act;
	if ( NeuronType.INPUT.equals( type ) )
		act = inputActivationType;
	else if ( NeuronType.OUTPUT.equals( type ) )
		act = outputActivationType;
	else
		act = hiddenActivationType;
	GasNeatNeuronGene gene = new GasNeatNeuronGene( type, nextInnovationId(), act );
	GasNeatNeuronAllele allele = new GasNeatNeuronAllele(gene);
	
	return allele;
}

/**
 * Factory method to construct new neuron allele which has replaced connection
 * <code>connectionId</code> according to NEAT add neuron mutation. If a previous mutation has
 * occurred adding a neuron on connection connectionId, returns a neuron with that id -
 * otherwise, a new id.
 * 
 * @param connectionId
 * @return NeuronAllele
 */
public NeuronAllele newNeuronAllele( Long connectionId ) {
	Long id = gasNeatIdMap.findNeuronId( connectionId );
	if ( id == null ) {
		id = nextInnovationId();
		gasNeatIdMap.putNeuronId( connectionId, id );
	}
	GasNeatNeuronGene gene = new GasNeatNeuronGene( NeuronType.HIDDEN, id, hiddenActivationType );
	GasNeatNeuronAllele allele = new GasNeatNeuronAllele( gene );
	
	allele.setToRandomValue( getRandomGenerator() ); 
	
	return allele;
}

/**
 * factory method to construct new connection allele from neuron <code>srcNeuronId</code> to
 * neuron <code>destNeuronId</code> according to NEAT add connection mutation; if a previous
 * mutation has occurred adding a connection between srcNeuronId and destNeuronId, returns
 * connection with that id; otherwise, new innovation id
 * 
 * @param srcNeuronId
 * @param destNeuronId
 * @return ConnectionAllele
 */
public ConnectionAllele newConnectionAllele( Long srcNeuronId, Long destNeuronId ) {
	Long id = gasNeatIdMap.findConnectionId( srcNeuronId, destNeuronId );
	if ( id == null ) {
		id = nextInnovationId();
		gasNeatIdMap.putConnectionId( srcNeuronId, destNeuronId, id );
	}
	GasNeatConnectionGene gene = new GasNeatConnectionGene(id, srcNeuronId, destNeuronId );
	return new GasNeatConnectionAllele( gene );
}

/**
 * @return clone reproduction operator used to create mutated asexual offspring
 */
public CloneReproductionOperator getCloneOperator() {
	return cloneOper;
}

/**
 * @return crossover reproduction operator used to create mutated sexual offspring
 */
public GasNeatCrossoverReproductionOperator getCrossoverOperator() {
	return crossoverOper;
}

/**
 * @return maximum conneciton weight
 */
public double getMaxConnectionWeight() {
	return maxConnectionWeight;
}

/**
 * @return minimum conneciton weight
 */
public double getMinConnectionWeight() {
	return minConnectionWeight;
}

/**
 * Load from persistence.
 * 
 * @throws InvalidConfigurationException
 */
public void load() throws InvalidConfigurationException {
	if ( gasNeatIdMap == null ) {
		gasNeatIdMap = new GasNeatIdMap( props );
		try {
			gasNeatIdMap.load();
		}
		catch ( IOException e ) {
			String msg = "error loading ID map";
			logger.error( msg, e );
			throw new InvalidConfigurationException( msg );
		}
	}
}

/**
 * Store to persistence.
 * 
 * @throws InvalidConfigurationException
 */
public void store() throws InvalidConfigurationException {
	try {
		getIdFactory().store();
		if ( gasNeatIdMap.store() )
			gasNeatIdMap = null;
	}
	catch ( IOException e ) {
		String msg = "error storing ID map";
		logger.error( msg, e );
		throw new InvalidConfigurationException( msg );
	}
}

/**
 * log stats for id maps
 * 
 * @param aLogger
 * @param pri priority
 */
public void logIdMaps( Logger aLogger, Priority pri ) {
	gasNeatIdMap.log( aLogger, pri );
}


public static int getNumberGases() {
	return numberGases;
}




}
