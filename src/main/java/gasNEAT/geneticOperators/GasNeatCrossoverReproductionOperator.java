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

import org.jgap.Allele;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;

import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatCrossoverReproductionOperator;

import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.geneticEncoding.GasNeatConnectionAllele;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import gasNEAT.xor.ChromosomeEvaluator;

/**
 * Implements NEAT crossover reproduction according to <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">Evolving Neural Networks
 * through Augmenting Topologies </a>.
 * 
 * @author Philip Tucker
 */
public class GasNeatCrossoverReproductionOperator extends NeatCrossoverReproductionOperator {

/**
 * Crossover according to <a
 * href="http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf">NEAT </a> crossover
 * methodology.
 * 
 * @param config
 * @param dominantChrom dominant parent
 * @param recessiveChrom recessive parent
 * @return ChromosomeMaterial offspring
 */
protected ChromosomeMaterial reproduce( Configuration config, Chromosome dominantChrom,	Chromosome recessiveChrom, int currentGeneration, int maxGenerations ) {
	ChromosomeMaterial child = dominantChrom.cloneMaterial();
	child.setSecondaryParentId( recessiveChrom.getId() );
	
	Iterator iter = child.getAlleles().iterator();
	while ( iter.hasNext() ) {
		Allele allele = (Allele) iter.next();
		if ( allele instanceof GasNeatConnectionAllele ) {
			GasNeatConnectionAllele dominantConnectionAllele = (GasNeatConnectionAllele) allele;
			GasNeatConnectionAllele recessiveConnectionAllele = (GasNeatConnectionAllele) recessiveChrom
					.findMatchingGene( dominantConnectionAllele );
			
			if ( recessiveConnectionAllele != null ) {
				// TODO blending?
				if ( config.getRandomGenerator().nextBoolean() ) {
					dominantConnectionAllele.setWeight( recessiveConnectionAllele.getWeight() );
				}
			}
		}
		
		//Original NEAT neurons actually have zero parameters
		if ( allele instanceof GasNeatNeuronAllele ) {
			GasNeatNeuronAllele dominantNeuronAllele = (GasNeatNeuronAllele) allele;
			GasNeatNeuronAllele recessiveNeuronAllele = (GasNeatNeuronAllele) recessiveChrom
					.findMatchingGene( dominantNeuronAllele );

			if ( recessiveNeuronAllele != null ) {
				if ( config.getRandomGenerator().nextBoolean() ) {
					dominantNeuronAllele.setAllValuesFromAllele( recessiveNeuronAllele );
				}
			}
		}
	}
	
	//*
	List<GasNeatNeuronAllele> neurons = NeatChromosomeUtility.getNeuronList(  child.getAlleles() );
	
	//randomly pick plasticity rule to use for all neurons
	Collections.shuffle(neurons);
	double a = neurons.get(0).getPlasticityParameterA();
	double b = neurons.get(0).getPlasticityParameterB();
	double c = neurons.get(0).getPlasticityParameterC();
	double d = neurons.get(0).getPlasticityParameterD();
	double lr = neurons.get(0).getPlasticityParameterLR();
	
	//#ADDPROPS
	
	
	//#CTRNNTODO
	//need to decide if all neurons should have same timing constant or they can have different ones
	
	for (GasNeatNeuronAllele neuron: neurons) {
		neuron.setPlasticityParameterA(a);
		neuron.setPlasticityParameterB(b);
		neuron.setPlasticityParameterC(c);
		neuron.setPlasticityParameterD(d);
		neuron.setPlasticityParameterLR(lr);
	}
	//*/

	return child;
}

}
