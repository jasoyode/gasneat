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
import java.util.Set;

import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

import com.anji.integration.AnjiRequiredException;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.WeightMutationOperator;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import gasNEAT.geneticEncoding.GasNeatNeuronAllele;

/**
 * Implements a Mutation that changes the threshold of firing of a neuron
 * 
 * @author Jason Yoder
 */
public class GasNeatNeuronPositionMutationOperator extends MutationOperator implements Configurable {

/**
 * properties key, perturb weight mutation rate
 */
public static final String NEURON_POSITION_MUTATE_RATE_KEY = "gasneat.neuron.position.mutation.rate";
public static final String NEURON_POSITION_MUTATE_STD_KEY = "gasneat.neuron.position.std";

/**
 * default mutation rate
 */
public static final float DEFAULT_MUTATE_RATE = 0.0f;
public static final float DEFAULT_NEURON_POSITION_MUTATE_STD = 50.0f;
public static final double X_MAX_POSITION  = 250;
public static final double X_MIN_POSITION  = 0;

public static final double Y_MAX_POSITION  = 200;
public static final double Y_MIN_POSITION  = 10;

private double pertubationStandardDeviation = DEFAULT_NEURON_POSITION_MUTATE_STD;


/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	setMutationRate( props.getFloatProperty( NEURON_POSITION_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	pertubationStandardDeviation = props.getFloatProperty( NEURON_POSITION_MUTATE_STD_KEY, DEFAULT_NEURON_POSITION_MUTATE_STD );
}

/**
 * @see MutationOperator#MutationOperator(float)
 */
public GasNeatNeuronPositionMutationOperator() {
	super( DEFAULT_MUTATE_RATE );
}

/**
 * @param newMutationRate
 * @see WeightMutationOperator#WeightMutationOperator(float, float)
 */
public GasNeatNeuronPositionMutationOperator( float newMutationRate ) {
	super( newMutationRate );
}

/**
 * Removes from <code>genesToAdd</code> and adds to <code>genesToRemove</code> all
 * connection genes that are modified.
 * 
 * @param jgapConfig The current active genetic configuration.
 * @param target chromosome material to mutate
 * @param genesToAdd <code>Set</code> contains <code>Gene</code> objects
 * @param genesToRemove <code>Set</code> contains <code>Gene</code> objects
 */
protected void mutate( Configuration jgapConfig, final ChromosomeMaterial target, Set genesToAdd, Set genesToRemove,  int currentGeneration, int maxGenerations ) {
	if ( ( jgapConfig instanceof NeatConfiguration ) == false )
		throw new AnjiRequiredException( NeatConfiguration.class.toString() );
	NeatConfiguration config = (NeatConfiguration) jgapConfig;

	List neurons = NeatChromosomeUtility.getNeuronList(  target.getAlleles()   );
	Collections.shuffle( neurons, config.getRandomGenerator() );
	int numMutations = numMutations( config.getRandomGenerator(), neurons.size() );
	
	
	Iterator iter = neurons.iterator();
	int i = 0;
	while ( ( i++ < numMutations ) && iter.hasNext() ) {
		
		GasNeatNeuronAllele origAllele = (GasNeatNeuronAllele) iter.next();
		GasNeatNeuronAllele newAllele = (GasNeatNeuronAllele) origAllele.cloneAllele();

		double adjX = config.getRandomGenerator().nextGaussian() * pertubationStandardDeviation;
		double adjY = config.getRandomGenerator().nextGaussian() * pertubationStandardDeviation;
		double newX = origAllele.getXCoordinate() + adjX;
		double newY = origAllele.getYCoordinate() + adjY;
		
		newX  = Math.max( Math.min( newX, X_MAX_POSITION), X_MIN_POSITION ) ;
		newY  = Math.max( Math.min( newY, Y_MAX_POSITION), Y_MIN_POSITION ) ;
		
		newAllele.setXCoordinate( (int)newX ); 
		newAllele.setYCoordinate( (int)newY );
		
		genesToRemove.add( origAllele );
		genesToAdd.add( newAllele );
	}
	
	/*
	System.out.println("POSITION MUTATION OCCURRED");
	for (Object old: genesToRemove) {
		System.out.println("posX was: " + ((GasNeatNeuronAllele)old).getXCoordinate());
		System.out.println("posY was: " + ((GasNeatNeuronAllele)old).getYCoordinate());
	}
	for (Object n: genesToAdd) {
		System.out.println("posX is now: " + ((GasNeatNeuronAllele)n).getXCoordinate());
		System.out.println("posY is now: " + ((GasNeatNeuronAllele)n).getYCoordinate());
	}
	if (genesToAdd.size() > 0)
		System.exit(1);
	// */
}



}
