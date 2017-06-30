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
public class GasNeatAddGasProducingNeuronMutationOperation extends AddNeuronMutationOperator {

/**
 * properties key, add neuron mutation rate
 */
public static final String GASNEAT_ADD_GAS_NEURON_MUTATE_RATE_KEY = "gasneat.add.gas.neuron.mutation.rate";
public static final String GASNEAT_ADD_GAS_NEURON_MUTATE_GENERATION_RATE_KEY = "gasneat.add.gas.neuron.mutation.generation";
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
	setMutationRate( props.getFloatProperty( GASNEAT_ADD_GAS_NEURON_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	setMutationGeneration( props.getFloatProperty( GASNEAT_ADD_GAS_NEURON_MUTATE_GENERATION_RATE_KEY, 0 ) );
	
	maxHiddenNeurons = props.getIntProperty(GASNEAT_MAX_HIDDEN_NEURONS_KEY, -1);
	
	gasCount = props.getIntProperty(GAS_COUNT_KEY);
	receptorMapFilePath = props.getProperty(GasNeatConfiguration.MAP_RECEPTOR_FILE);
	setupReceptorMap();
	
}


public void setupReceptorMap() {
	receptorMap = new ArrayList<String>(); 

	try {
		BufferedReader receptorList = new BufferedReader( new FileReader( receptorMapFilePath ));
		
		String currentLine = receptorList.readLine();
		for (int i=0; currentLine  != null; i++) {
			receptorMap.add( currentLine );
			currentLine = receptorList.readLine();
		}
		receptorList.close();
	
	} catch ( IOException e) {
		// TODO Auto-generated catch block
		System.out.println("Could not read file:" + receptorMapFilePath );
		e.printStackTrace();
		System.exit(1);
	}
}


/**
 * @see AddNeuronMutationOperator#AddNeuronMutationOperator(float)
 */
public GasNeatAddGasProducingNeuronMutationOperation() {
	this( DEFAULT_MUTATE_RATE );
}

/**
 * @see MutationOperator#MutationOperator(float)
 */
public GasNeatAddGasProducingNeuronMutationOperation( float newMutationRate ) {
	super( newMutationRate );
}

/**
 * TODO: CAN MAKE THIS MORE EFFICIENT
 * Mutates receptors in the vicinity to match the gas type (in some variety) if possible
 */
protected void mutate( Configuration jgapConfig, final ChromosomeMaterial target, Set allelesToAdd, Set allelesToRemove, int currentGeneration, int maxGenerations ) {
	
	if (! doesMutationOccur(currentGeneration  ) ) {
		return;
	} 
	
	if ( ( jgapConfig instanceof NeatConfiguration ) == false )
		throw new AnjiRequiredException( "com.anji.neat.NeatConfiguration" );
	
	
	NeatConfiguration config = (NeatConfiguration) jgapConfig;
	Map neuronsMap = NeatChromosomeUtility.getNeuronMap( target.getAlleles() );
	
	//negative 1 means unlimited
	if ( maxHiddenNeurons != -1 ) {
		int hiddenNeurons = NeatChromosomeUtility.getNeuronMap(target.getAlleles(), NeuronType.HIDDEN).size();
		if (hiddenNeurons>= maxHiddenNeurons) {
			return;
		}
		
	}
	
	int numNeurons = numMutations( config.getRandomGenerator(), neuronsMap.size() );


	// TODO: decide if we want special placement of gas producing neurons
	List neuronList = NeatChromosomeUtility.getNeuronList( target.getAlleles() );
	
	
	//List<String> neuronIDs =  (List<String>) (neuronsMap.keySet() );
	Collections.shuffle( neuronList, config.getRandomGenerator() );
	
	//grab first two random neurons
	GasNeatNeuronAllele src = (GasNeatNeuronAllele)neuronsMap.get( ( (GasNeatNeuronAllele)neuronList.get(0) ).getInnovationId() );
	GasNeatNeuronAllele dest = (GasNeatNeuronAllele)neuronsMap.get( ( (GasNeatNeuronAllele)neuronList.get(1) ).getInnovationId() );
	
	//Take the receptor type so that src produced new neurons activation signal 
	int receptorType = src.getGasEmissionType();
	
	//if 0 is emitted, it means that it is not a gas emitting neuron
	//it might however emit synaptic neuromodulators
	if (receptorType == 0) {
		receptorType  =  src.getSynapticGasEmissionType();
	}
	
	//Produce the signal which will activate the dest neuron
	//unless it is a synapse-based activation, then pick a random gas
	// "G*_..."  * is the index of gas that activates the neuron
	int emissionType = new Integer( dest.getReceptorType().substring(1, 2) );
	
	if (emissionType == 0) {
		emissionType = (int)(config.getRandomGenerator().nextInt(gasCount)+1 );
	}
	
	int xCoord = (src.getXCoordinate() + dest.getXCoordinate() ) /2;
	int yCoord = (src.getYCoordinate() + dest.getYCoordinate() ) /2;
	
	
	GasNeatNeuronAllele newGasNeuron = (GasNeatNeuronAllele) config.newNeuronAllele(NeuronType.HIDDEN );
	
	//speed used in random calcs, so set it before randomizing
	newGasNeuron.setGasSpeed( src.getGasSpeed() );
	
	
	
	//randomize all and then just set what needs to be set
	newGasNeuron.setToRandomValue( config.getRandomGenerator() );
	
	
	newGasNeuron.setPlasticityParameterA(  src.getPlasticityParameterA()  );
	newGasNeuron.setPlasticityParameterB(  src.getPlasticityParameterB()  );
	newGasNeuron.setPlasticityParameterC(  src.getPlasticityParameterC()  );
	newGasNeuron.setPlasticityParameterD(  src.getPlasticityParameterD()  );
	newGasNeuron.setPlasticityParameterLR(  src.getPlasticityParameterLR()  );
	
	
	///set properties based on what we know to be desired
	newGasNeuron.setGasEmissionType( emissionType );
	
	//#GASNEATEVOLUTION
	newGasNeuron.setPreferredActivationTypeForReceptor(receptorType);
	
	newGasNeuron.setXCoordinate(xCoord);
	newGasNeuron.setYCoordinate(yCoord);
	
	if (receptorType == 0) {
		//add a synaptic connection as well if the neuron is going to be activated by synapses
		allelesToAdd.add(  (GasNeatConnectionAllele)config.newConnectionAllele(src.getInnovationId(), newGasNeuron.getInnovationId() ) );
		
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
	
	allelesToAdd.add(newGasNeuron);
	
}

}
