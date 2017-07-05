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
 * Implements a Mutation that changes the Neuron from producing one gas to another...
 * 
 * @author Jason Yoder
 */
public class GasNeatNeuronReceptorStrengthMutationOperator extends MutationOperator implements Configurable {
	/**
	 * properties key, perturb weight mutation rate
	 */
	public static final String NEURON_RECEPTOR_STRENGTH_MUTATE_RATE_KEY = "gasneat.neuron.receptor.strength.mutation.rate";
	
	/**
	 * default mutation rate
	 */
	public static final float DEFAULT_MUTATE_RATE = 0.0f;
	
	/**
	 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
	 */
	public void init( Properties props ) throws Exception {
		setMutationRate( props.getFloatProperty( NEURON_RECEPTOR_STRENGTH_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	}
	
	/**
	 * @see MutationOperator#MutationOperator(float)
	 */
	public GasNeatNeuronReceptorStrengthMutationOperator() {
		super( DEFAULT_MUTATE_RATE );
	}
	
	/**
	 * @param newMutationRate
	 * @see WeightMutationOperator#WeightMutationOperator(float, float)
	 */
	public GasNeatNeuronReceptorStrengthMutationOperator( float newMutationRate ) {
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
			newAllele.setReceptorTypeToRandom( config.getRandomGenerator()  );
			
			
			//ULTRAJULYTODO
			// create mutation and add the std as well to mutate this
			//decide if discrete levels of receptor strength or not
			//currently can be fully on or fully off at least
			//newAllele.setReceptorStrengthToRandom( config.getRandomGenerator()  );
			
			genesToRemove.add( origAllele );
			genesToAdd.add( newAllele );
		}
	
	}


}
