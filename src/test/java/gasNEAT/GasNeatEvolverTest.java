/*
 * Copyright (C) 2004 Derek James and Philip Tucker This file is part of ANJI (Another NEAT Java
 * Implementation). ANJI is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA Created on Feb 5, 2004 by Philip Tucker
 */
package gasNEAT;

import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;

import com.anji.neat.Evolver;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;
import junit.framework.TestCase;

/**
 * @author Philip Tucker
 */
public class GasNeatEvolverTest extends TestCase {

private final static Logger logger = Logger.getLogger( GasNeatEvolverTest.class );

private ArrayList<String> experimentsToRun;


/**
 * ctor
 */
public GasNeatEvolverTest() {
	this( GasNeatEvolverTest.class.toString() );
}

/**
 * ctor
 * @param name
 */
public GasNeatEvolverTest( String name ) {
	super( name );
	
	experimentsToRun = new ArrayList<String>();
	
	
	experimentsToRun.add("experiments/sanity_tests/gas_act_mod/task_gas_act_mod.properties");
	experimentsToRun.add("experiments/sanity_tests/gas_activated_neurons/task_gas_activated_neurons.properties");
	
	
	
	//*
	experimentsToRun.add("experiments/sanity_tests/multi_output/multi_output.properties");
	
	
	experimentsToRun.add("experiments/sanity_tests/multi_output_delayed/multi_output_delayed.properties");
	experimentsToRun.add("experiments/sanity_tests/one_dim_cpg/task_temp_mutable.properties");
	experimentsToRun.add("experiments/sanity_tests/topological_act_mod/task_topological_act_mod.properties");
	experimentsToRun.add("experiments/sanity_tests/gas_synaptic_plasticity/task_gas_synaptic_plasticity.properties");
	experimentsToRun.add("experiments/sanity_tests/topological_synaptic_plasticity/task_topological_synaptic_plasticity.properties");
	experimentsToRun.add("experiments/sanity_tests/zachs_test/zachs_exp.properties");
	
	//*/
	
	/*
	experimentsToRun.add("experiments/sanity_tests//");
	*/
	
}

/**
 * main test
 * @throws Exception
 */
public void testEvolver() throws Exception {
	int failCount = 0;
	StringBuffer logText = new StringBuffer();
	
	int NUMBER_TESTS = 1;
	
	for (String propertyPath: experimentsToRun) 
	{
		logText.append("Properties File: " ).append( propertyPath ).append( "\n" );
		GasNeatConfiguration.resetReceptorMap();
		//TODOTEST
		for ( int i = 0; i < NUMBER_TESTS; ++i ) {
			Evolver uut = new Evolver();
			uut.init( new Properties( propertyPath ) );
			logger.info( "EvolverTest: RUN " + i );
			Chromosome champ = uut.getChamp();
			assertNull( "not null initial champ", champ );
			uut.run();

			champ = uut.getChamp();
			if ( champ == null ) {
				logText.append(" "+ i ).append( ": no champ\n" );
				++failCount;
			}
			else if ( champ.getFitnessValue() < uut.getThresholdFitness() ) {
				logText.append( i ).append( ": fitness < threshold: " ).append(
						champ.getFitnessValue() + " < " + uut.getThresholdFitness() ).append( "\n" );
				++failCount;
			}
			else if ( champ.getFitnessValue() < uut.getTargetFitness() ) {
				
				logText.append( i ).append( ": fitness < target: " ).append(
						champ.getFitnessValue()+ " < " + uut.getTargetFitness() ).append( "\n" );

			} else {
				logText.append( i ).append( ": fitness >= target: " ).append(
						champ.getFitnessValue()+ " >= " + uut.getTargetFitness() ).append( "\n" );
				
			}
			
			
		}
	
		logger.info( logText.toString() );
		assertEquals( failCount + " failures", 0, failCount );
	
	}
}

}
