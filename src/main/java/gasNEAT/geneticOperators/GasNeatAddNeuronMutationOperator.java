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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

import com.anji.integration.AnjiRequiredException;
import com.anji.neat.AddNeuronMutationOperator;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronType;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;

/**
 * Implements NEAT add node mutation inspired by <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf"> Evolving Neural Networks
 * through Augmenting Topologies </a>. In ANJI, mutation rate refers to the likelihood of a new
 * node being created on any existing connection. In traditional NEAT, it is the likelihood of a
 * chromosome experiencing a mutation, and each chromosome can not have more than one
 * topological mutation per generation.
 * 
 * @author Philip Tucker
 */
public class GasNeatAddNeuronMutationOperator extends AddNeuronMutationOperator {



/**
 * properties key, add neuron mutation rate
 */
public static final String GASNEAT_ADD_NEURON_MUTATE_RATE_KEY = "gasneat.add.neuron.mutation.rate";
public static final String GASNEAT_ADD_NEURON_MUTATE_GENERATION_RATE_KEY = "gasneat.add.neuron.mutation.generation";
public static final String GASNEAT_MAX_HIDDEN_NEURONS_KEY =  "gasneat.max.hidden.neurons";

/**
 * default mutation rate
 */
public static final float DEFAULT_MUTATE_RATE = 0.01f;
private int maxHiddenNeurons = -1;

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	setMutationRate( props.getFloatProperty( GASNEAT_ADD_NEURON_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	setMutationGeneration( props.getFloatProperty( GASNEAT_ADD_NEURON_MUTATE_GENERATION_RATE_KEY, 0 ) );
	maxHiddenNeurons = props.getIntProperty(GASNEAT_MAX_HIDDEN_NEURONS_KEY, -1);
}

/**
 * @see AddNeuronMutationOperator#AddNeuronMutationOperator(float)
 */
public GasNeatAddNeuronMutationOperator() {
	this( DEFAULT_MUTATE_RATE );
}

/**
 * @see MutationOperator#MutationOperator(float)
 */
public GasNeatAddNeuronMutationOperator( float newMutationRate ) {
	super( newMutationRate );
}

/**
 * Adds connections according to <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">NEAT </a> add node mutation.
 * @see org.jgap.MutationOperator#mutate(org.jgap.Configuration, org.jgap.ChromosomeMaterial,
 * java.util.Set, java.util.Set)
 */
protected void mutate( Configuration jgapConfig, final ChromosomeMaterial target, Set allelesToAdd, Set allelesToRemove, int currentGeneration, int maxGenerations ) {
	if ( ( jgapConfig instanceof NeatConfiguration ) == false )
		throw new AnjiRequiredException( "com.anji.neat.NeatConfiguration" );
	
	if (! doesMutationOccur(currentGeneration  ) ) {
		return;
	}
	
	//negative 1 means unlimited
	if ( maxHiddenNeurons != -1 ) {
		int hiddenNeurons = NeatChromosomeUtility.getNeuronMap(target.getAlleles(), NeuronType.HIDDEN).size();
		if (hiddenNeurons>= maxHiddenNeurons) {
			return;
		}
		
	}
	
	
	NeatConfiguration config = (NeatConfiguration) jgapConfig;
	Map neurons = NeatChromosomeUtility.getNeuronMap( target.getAlleles() );
	
	// neuron can be mutated on any connection
	List connList = NeatChromosomeUtility.getConnectionList( target.getAlleles() );
	
	Collections.shuffle( connList, config.getRandomGenerator() );

	int numConnections = numMutations( config.getRandomGenerator(), connList.size() );
	Iterator iter = connList.iterator();
	int count = 0;
	while ( iter.hasNext() && ( count++ < numConnections ) ) {
		ConnectionAllele oldConnectAllele = (ConnectionAllele) iter.next();
		addNeuronAtConnection( config, neurons, oldConnectAllele, allelesToAdd, allelesToRemove );
	}
}

/**
 * @param config
 * @param neurons <code>Map</code> contains <code>NeuronAllele</code> objects
 * @param oldConnectAllele connection allele to be replaced by neuron
 * @param allelesToAdd <code>Set</code> contains <code>Allele</code> objects
 * @param allelesToRemove <code>Set</code> contains <code>Allele</code> objects
 * @return true iff neuron added
 */
public boolean addNeuronAtConnection( NeatConfiguration config, Map neurons,
		ConnectionAllele oldConnectAllele, Set allelesToAdd, Set allelesToRemove ) {
	
	GasNeatNeuronAllele newNeuronAllele = (GasNeatNeuronAllele)config.newNeuronAllele( oldConnectAllele.getInnovationId() );
	
	//get the x,y from both and find the midpoint
	GasNeatNeuronAllele src  = (GasNeatNeuronAllele)neurons.get( oldConnectAllele.getSrcNeuronId() );
	GasNeatNeuronAllele dest = (GasNeatNeuronAllele)neurons.get( oldConnectAllele.getDestNeuronId() );
	
	GasNeatConfiguration gasConfig = (GasNeatConfiguration)config;

	
	newNeuronAllele.setToRandomValue( config.getRandomGenerator() );
	
	//*
	newNeuronAllele.setPlasticityParameterA(  src.getPlasticityParameterA()  );
	newNeuronAllele.setPlasticityParameterB(  src.getPlasticityParameterB()  );
	newNeuronAllele.setPlasticityParameterC(  src.getPlasticityParameterC()  );
	newNeuronAllele.setPlasticityParameterD(  src.getPlasticityParameterD()  );
	newNeuronAllele.setPlasticityParameterLR(  src.getPlasticityParameterLR()  );
	//*/
	
	
	//#GASNEATEVOLUTION
	//determine whether to randomize receptor or set to default for all
	if (gasConfig.isUsingDefaultReceptor() ) {
		newNeuronAllele.setReceptorTypeToDefault();
	} else {
		newNeuronAllele.setReceptorTypeToRandom( gasConfig.getRandomGenerator() );
	}
	
	
	int newX = (src.getXCoordinate() + dest.getXCoordinate() )/2;
	int newY = (src.getYCoordinate() + dest.getYCoordinate() )/2;
	newNeuronAllele.setXCoordinate(newX);
	newNeuronAllele.setYCoordinate(newY);
	
	// check for dupes
	if ( neurons.containsKey( newNeuronAllele.getInnovationId() ) == false ) {
		neurons.put( newNeuronAllele.getInnovationId(), newNeuronAllele );

		// and add 2 new connections ...
		ConnectionAllele newConnectAllele1 = config.newConnectionAllele( oldConnectAllele
				.getSrcNeuronId(), newNeuronAllele.getInnovationId() );
		newConnectAllele1.setWeight( 1.0d );

		ConnectionAllele newConnectAllele2 = config.newConnectionAllele( newNeuronAllele
				.getInnovationId(), oldConnectAllele.getDestNeuronId() );
		newConnectAllele2.setWeight( oldConnectAllele.getWeight() );


		
		allelesToRemove.add( oldConnectAllele );
		allelesToAdd.add( newNeuronAllele );
		allelesToAdd.add( newConnectAllele1 );
		allelesToAdd.add( newConnectAllele2 );

		return true;
	}

	return false;
}
}
