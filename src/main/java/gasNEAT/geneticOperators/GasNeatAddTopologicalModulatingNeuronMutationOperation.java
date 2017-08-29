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
 * created by Philip Tucker on Feb 16, 2003
 */
package gasNEAT.geneticOperators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

import com.anji.integration.AnjiRequiredException;
import com.anji.neat.AddNeuronMutationOperator;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronType;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.geneticEncoding.GasNeatConnectionAllele;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;

/**
 * Implements add node like NEAT but with a gas producing neuron instead
 * 
 */
public class GasNeatAddTopologicalModulatingNeuronMutationOperation extends AddNeuronMutationOperator {

/**
 * properties key, add neuron mutation rate
 */
public static final String GASNEAT_ADD_TOPOLOGICAL_MODULATING_NEURON_MUTATE_RATE_KEY = "gasneat.add.topological.modulating.neuron.mutation.rate";
public static final String GASNEAT_ADD_TOPOLOGICAL_MODULATING_NEURON_MUTATE_GENERATION_RATE_KEY = "gasneat.add.topological.modulating.neuron.mutation.generation";
public static final String GASNEAT_MAX_HIDDEN_NEURONS_KEY =  "gasneat.max.hidden.neurons";

/**
 * default mutation rate
 */
public static final float DEFAULT_MUTATE_RATE = 0.01f;

private static final String GAS_COUNT_KEY = "gasneat.gas.count";

private int gasCount;
private ArrayList<String> receptorMap;
private String receptorMapFilePath;

private int maxHiddenNeurons = -1;

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	setMutationRate( props.getFloatProperty( GASNEAT_ADD_TOPOLOGICAL_MODULATING_NEURON_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	setMutationGeneration( props.getFloatProperty( GASNEAT_ADD_TOPOLOGICAL_MODULATING_NEURON_MUTATE_GENERATION_RATE_KEY, 0 ) );
	
	gasCount = props.getIntProperty(GAS_COUNT_KEY);
	receptorMap = GasNeatConfiguration.getReceptorMap();
	maxHiddenNeurons = props.getIntProperty(GASNEAT_MAX_HIDDEN_NEURONS_KEY, -1);
	
}


/**
 * @see AddNeuronMutationOperator#AddNeuronMutationOperator(float)
 */
public GasNeatAddTopologicalModulatingNeuronMutationOperation() {
	this( DEFAULT_MUTATE_RATE );
}

/**
 * @see MutationOperator#MutationOperator(float)
 */
public GasNeatAddTopologicalModulatingNeuronMutationOperation( float newMutationRate ) {
	super( newMutationRate );
}


/**
 * Mutates receptors in the vicinity to match the gas type (in some variety) if possible
 */
protected void mutate( Configuration jgapConfig, final ChromosomeMaterial target, Set allelesToAdd, Set allelesToRemove, int currentGeneration, int maxGenerations ) {
	
	//negative 1 means unlimited
	if ( maxHiddenNeurons != -1 ) {
		int hiddenNeurons = NeatChromosomeUtility.getNeuronMap(target.getAlleles(), NeuronType.HIDDEN).size();
		if (hiddenNeurons>= maxHiddenNeurons) {
			return;
		}
		
	}
	
	//only allow mutation when it allowed by config
	if (! doesMutationOccur(currentGeneration  ) ) {
		return;
	} 
	
	// get a list of the activation types from receptor_map
	// this is needed in order to ensure that our added neuron can actually be activated
	// and if it cannot then we cannot allow this mutation!
	// should throw a fatal error if that is the case 
	Set<Integer> receptorMapActivationTypes = new HashSet<Integer>();
	for (String receptorType: receptorMap) {
		// G0, G1, G2, G3, G4 etc
		receptorMapActivationTypes.add( Integer.parseInt(  receptorType.substring(1, 2)  ) );
	}
	
	// this mutation should run the follow algorithm
	//
	// 1. select one random neuron
	// 
	// 2. find out what the first (src) neuron produces (and how!) (we should allow gas activation if enabled)
	//    a. if there exists no receptor to be activated by the src's production
	//       then try again with different neuron
	//    b. if all neurons failed, throw an error and fatally crash - because this should never happen
	//    c. set newNeuron's receptor to be a random receptor -> which has the needed activationType 
	// 
	// 3. select a random second neuron (must not be same as original!) to be MODULATED   **NOT** activated
	//    a. set output gas to MODULATE target (requires checking receptor type)
	//    b. if dest neuron has no way of being MODULATED, 
	//       select different neuron (or change its receptor?)
	//    c. set new neurons position to be midpoint of src and dest
	//    d. if no modulation possible fatally crash
	// 
	// 4. create a new neuron which is activated by this signal
	//    a. if it is a diffusive neuron then just adjust receptor and add connection to target
	//    b. if it is a topological neuromodulator then adjust receptor AND
	//       add a connection to newNeuron and to dest
	
	
	
	if ( ( jgapConfig instanceof NeatConfiguration ) == false )
		throw new AnjiRequiredException( "com.anji.neat.NeatConfiguration" );
	
	GasNeatConfiguration config = (GasNeatConfiguration) jgapConfig;
	Map neuronsMap = NeatChromosomeUtility.getNeuronMap( target.getAlleles() );
	int numNeurons = numMutations( config.getRandomGenerator(), neuronsMap.size() );
	List neuronList = NeatChromosomeUtility.getNeuronList( target.getAlleles() );
	
	Collections.shuffle( neuronList, config.getRandomGenerator() );
	
	//grab one random neuron
	GasNeatNeuronAllele src = null;
	boolean validSourceNeuronFound = false;
	boolean needsConnectionToActivate = false;
	
	String newNeuronReceptorType = "";
	int newNeuronActivation = -1;
	
	for (Object sourceNeuron: neuronList ) {
		src = (GasNeatNeuronAllele)neuronsMap.get( ( (GasNeatNeuronAllele)sourceNeuron ).getInnovationId() );
		int receptorType = src.getGasEmissionType();
		//if 0 is emitted, it means that it is not a diffusive gas emitting neuron
		//it might however emit synaptic neuromodulators
		if (receptorType == 0) {
			receptorType  =  src.getSynapticGasEmissionType();
			needsConnectionToActivate = true;
		} else {
			needsConnectionToActivate = false;
		}
		if (receptorMapActivationTypes.contains(receptorType) ) {
			//we have found an appropriate src neuron
			//set flag to true and stop not
			validSourceNeuronFound = true;

			//select first occurrence which activates
			//TODO could improve if this was randomized, but we don't want to do that if we can avoid it
			//TODO this means the order of elements in receptor_maps are important
			for (String type: receptorMap) {
				if ( type.substring(1, 2).equals( ""+receptorType)   ) {
					newNeuronReceptorType = type;
					newNeuronActivation = Integer.parseInt(  type.substring(1, 2)  );
				}
			}
			break; //don't load any more since we have found a good one
		}
	}
	
	// if there are no eligible neurons then throw a fatal error
	if (validSourceNeuronFound == false) {
		System.err.println("You should never insert a modulating neuron in a network which cannot activate it by any means!");
		System.err.println("Chance this could occur legally if all neurons fail to produce gas and no gas activated receptors exist!");
		System.exit(1);
	}
	
	//re-randomize list of neurons
	Collections.shuffle( neuronList, config.getRandomGenerator() );
	GasNeatNeuronAllele dest = null;
	boolean validDestNeuronFound = false;
	int synapticGasEmitted = -1;
	
	for (Object impactedNeuron: neuronList ) {
	
		dest = (GasNeatNeuronAllele)neuronsMap.get( ( (GasNeatNeuronAllele)impactedNeuron ).getInnovationId() );
		//make sure not the same neurons!
		if ( !dest.getInnovationId().equals( src.getInnovationId() ))  {
			
			//check if can be impacted
			List<Integer> gasTypes = dest.getModulatingGasTypes();
			if ( gasTypes.size() != 0) {
				Collections.shuffle( gasTypes, config.getRandomGenerator() );
				
				//set to random gas that will impact destination neuron
				synapticGasEmitted = gasTypes.get(0);
				if (synapticGasEmitted > GasNeatConfiguration.getNumberGases() ) {
					System.err.println("You have receptors for gases that are not supposed to ever be introduced!");
					System.exit(1);
				}
				//MEGATODOSPEED #2- calculate the distance between and make sure in range!!!!!
				
				validDestNeuronFound = true;
				break; //we found an appropriate gas to emit!
			}
			
		}
	}
	
	if (validDestNeuronFound == false ) {
		System.err.println("You don't have any neurons that can be modulated by ANY legal gases!!");
		System.exit(1);
	}
	
	
	GasNeatNeuronAllele newGasNeuron = (GasNeatNeuronAllele) config.newNeuronAllele(NeuronType.HIDDEN );
	newGasNeuron.setToRandomValue( config.getRandomGenerator() );
	
	newGasNeuron.setGasEmissionType( 0 );
	
	//from above
	newGasNeuron.setReceptorType(newNeuronReceptorType);
	//from above
	newGasNeuron.setSynapticGasEmissionType( synapticGasEmitted );
	
	//use default - does not emit gas
	newGasNeuron.setGasEmissionRadius( 0 );
	
	int xCoord = (src.getXCoordinate() + dest.getXCoordinate() ) /2;
	int yCoord = (src.getYCoordinate() + dest.getYCoordinate() ) /2;
	//speed used in random calcs, so set it before randomizing
	newGasNeuron.setGasSpeed( src.getGasSpeed() );
	
	newGasNeuron.setPlasticityParameterA(  src.getPlasticityParameterA()  );
	newGasNeuron.setPlasticityParameterB(  src.getPlasticityParameterB()  );
	newGasNeuron.setPlasticityParameterC(  src.getPlasticityParameterC()  );
	newGasNeuron.setPlasticityParameterD(  src.getPlasticityParameterD()  );
	newGasNeuron.setPlasticityParameterLR(  src.getPlasticityParameterLR()  );
	
	//#GASNEATEVOLUTION
	newGasNeuron.setXCoordinate(xCoord);
	newGasNeuron.setYCoordinate(yCoord);
	
	if ( needsConnectionToActivate ) {
		//add a synaptic connection as well if the neuron is going to be activated by synapses
		GasNeatConnectionAllele connection = (GasNeatConnectionAllele)config.newConnectionAllele(src.getInnovationId(), newGasNeuron.getInnovationId() );
		connection.setToRandomValue( config.getRandomGenerator() );
		allelesToAdd.add(  connection );
		
	} else {
		//check if the emitting neuron has an emission 
		//radius smaller than will include new neuron
		int xDiff = newGasNeuron.getXCoordinate() -src.getXCoordinate();
		int yDiff = newGasNeuron.getYCoordinate() -src.getYCoordinate();
		
		double distance = Math.sqrt( xDiff*xDiff +  yDiff*yDiff );
		
		if ( distance > src.getGasEmissionRadius() ) {
			
			int deltaX = 0;
			int deltaY = 0;
			
			if (xDiff == 0) {
				deltaX = 0;
				deltaY = (int)( (Math.abs(yDiff)/yDiff) * src.getGasEmissionRadius()) ;	
			} else {
				double angle = Math.sin( yDiff / (1.0*xDiff) );
				//magnitude in each direction
				deltaX = (int)(Math.cos( angle )*src.getGasEmissionRadius());
				deltaY = (int)(Math.sin( angle )*src.getGasEmissionRadius());
			}
			
			//must check direction to add magnitude for each direction
			if (xDiff <= 0 ) {
				newGasNeuron.setXCoordinate( src.getXCoordinate() -  deltaX  );				
			} else {
				newGasNeuron.setXCoordinate( src.getXCoordinate() +  deltaX  );
			}
			if (yDiff <= 0 ) {
				newGasNeuron.setYCoordinate( src.getYCoordinate() -  deltaY  );				
			} else {
				newGasNeuron.setYCoordinate( src.getYCoordinate() +  deltaY  );
			}
			
			xDiff = newGasNeuron.getXCoordinate() -src.getXCoordinate();
			yDiff = newGasNeuron.getYCoordinate() -src.getYCoordinate();
			distance = Math.sqrt( xDiff*xDiff +  yDiff*yDiff );
			
			if ( distance > src.getGasEmissionRadius() ) {
				System.out.println("YOU DID BAD MATH");
				System.exit(1);
			} else {
				//System.out.println("YOU DID GOOD MATHS");
			}
		}
	}
	//add modulating neuron
	allelesToAdd.add(newGasNeuron);
	
	//add modulating connection from newNeuron
	GasNeatConnectionAllele connectionToTarget = (GasNeatConnectionAllele)config.newConnectionAllele( newGasNeuron.getInnovationId(), dest.getInnovationId() );
	connectionToTarget.setToRandomValue( config.getRandomGenerator() );
	allelesToAdd.add(  connectionToTarget );
	
	
}

}
