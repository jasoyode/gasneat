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
 * Created on Apr 6, 2004 by Philip Tucker
 */
package gasNEAT.geneticOperators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgap.Allele;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

import com.anji.neat.NeuronType;
import com.anji.neat.PruneMutationOperator;
import com.anji.util.Properties;

import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import gasNEAT.util.GasNeatChromosomeUtility;

/**
 * Removes neurons and connections that do not affect the activation of the network. This
 * includes hidden neurons without inputs or outputs, connections missing source or destination
 * neurons, or sub-structures of neurons and connections that are stranded. Allows additive and
 * subtractive mutation operators to be less careful about what they do and require less
 * coordination among them, since this operator can follow them and "clean up the mess". For
 * this reason, this operator generally should be the last executed in the sequence of mutation
 * operators. This operator was necessary with the addition of simplification dynamics for James
 * and Tucker's "A Comparative Analysis of Simplification and Complexification in the Evolution
 * of Neural Network Topologies" paper for <a
 * href="http://gal4.ge.uiuc.edu:8080/GECCO-2004/">GECCO 2004 </a>.
 * 
 * TODO - mutation rate less than 1.0 might yield unexpected results - maybe should handle nodes
 * and connections differently in that case
 * 
 * @author Philip Tucker
 */
public class GasNeatPruneMutationOperator extends PruneMutationOperator  {

	/**
	 * properties key, prune network mutation rate
	 */
	private static final String PRUNE_MUTATE_RATE_KEY = "gasneat.prune.mutation.rate";
	
	/**
	 * default mutation rate
	 */
	public final static float DEFAULT_MUTATE_RATE = 1.00f;
	
	/**
	 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
	 */
	public void init( Properties props ) throws Exception {
		setMutationRate( props.getFloatProperty( PRUNE_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	}
	
	/**
	 * @see PruneMutationOperator#PruneMutationOperator(float)
	 */
	public GasNeatPruneMutationOperator() {
		this( DEFAULT_MUTATE_RATE );
	}
	
	/**
	 * @see MutationOperator#MutationOperator(float)
	 */
	public GasNeatPruneMutationOperator( float newMutationRate ) {
		super( newMutationRate );
		
		if (   ! (newMutationRate == 0 || newMutationRate == 1.0) ){
			System.err.println("Do not allow prune mutation rate to be anything other than 1 or 0!");
			System.exit(1);
		}
	
	}
	
	/**
	 * Traverse network flowing forward and backward to identify unvisited connections and neurons.
	 * Then, remove a number of those depending on mutation rate.
	 * 
	 * @param config
	 * @param target chromosome material to mutate
	 * @param genesToAdd <code>Set</code> contains <code>Gene</code> objects
	 * @param genesToRemove <code>Set</code> contains <code>Gene</code> objects
	 * @see org.jgap.MutationOperator#mutate(org.jgap.Configuration, org.jgap.ChromosomeMaterial,
	 * java.util.Set, java.util.Set)
	 */
	protected void mutate( Configuration config, ChromosomeMaterial target, Set genesToAdd,
			Set genesToRemove, int currentGeneration, int maxGenerations ) {
		List candidatesToRemove = new ArrayList();
		
		//ULTRATODO -  decide how to change pruning or mutations
		//comment this out to stop pruning
		findUnvisitedAlleles( target, candidatesToRemove);
		
		Collections.shuffle( candidatesToRemove, config.getRandomGenerator() );
		for ( int i = 0; i < numMutations( config.getRandomGenerator(), candidatesToRemove.size() ); ++i ) {
			genesToRemove.add( candidatesToRemove.get( i ) );
		}
		
	}
	
	/**
	 * @param material target from which to remove stranded nodes and connections
	 * @param unvisitedAlleles <code>List</code> contains <code>Gene</code> objects, unvisited
	 * nodes and connections
	 * @param isForward traverse the network from input to output if true, output to input if false
	 */
	private void findUnvisitedAlleles( ChromosomeMaterial material, List unvisitedAlleles ) {
	
		Collection<Allele> toBeRemoved=  GasNeatChromosomeUtility.getAllUnactivatableAllelesFromSrcNeurons( material.getAlleles() );
		
		for (Allele a: toBeRemoved) {
			if (a instanceof GasNeatNeuronAllele ) {
				if  ( ((GasNeatNeuronAllele)a).getType().equals(NeuronType.INPUT) ) {
					System.err.println("INPUT neurons should never be removed!");
					System.exit(1);
				} else if  ( ((GasNeatNeuronAllele)a).getType().equals(NeuronType.OUTPUT) ) {
					System.err.println("OUTPUT neurons should never be removed, even if they are not reachable!");
					System.exit(1);
				}
			}
		}
	
		unvisitedAlleles.addAll( toBeRemoved );
		
	}

	
}
