
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
 * Implements a Mutation that changes the strength of gas emission of a neuron Neuron
 * 
 */
public class GasNeatNeuronEmissionMutationOperator extends MutationOperator implements Configurable {

/**
 * properties key, perturb weight mutation rate
 */
public static final String NEURON_EMISSION_RATE_MUTATE_RATE_KEY = "gasneat.neuron.emission.rate.mutation.rate";

/**
 * default mutation rate
 */
public static final float DEFAULT_MUTATE_RATE = 0.10f;

/**
 * properties key, standard deviation of perturb weight mutation
 */
public static final String EMISSION_MUTATE_STD_DEV_KEY = "gasneat.emission.mutation.std.dev";

private static final float DEFAULT_STD_DEV  = 0.1f;

private float stdDev = DEFAULT_STD_DEV;


/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	setMutationRate( props.getFloatProperty( NEURON_EMISSION_RATE_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	stdDev = props.getFloatProperty( EMISSION_MUTATE_STD_DEV_KEY, DEFAULT_STD_DEV );
}

/**
 * @see MutationOperator#MutationOperator(float)
 */
public GasNeatNeuronEmissionMutationOperator() {
	super( DEFAULT_MUTATE_RATE );
}

/**
 * @param newMutationRate
 * @see WeightMutationOperator#WeightMutationOperator(float, float)
 */
public GasNeatNeuronEmissionMutationOperator( float newMutationRate ) {
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
		
		double oldweight = origAllele.getGasEmissionStrength();
		
		double nextWeight = origAllele.getGasEmissionStrength()
				+ ( config.getRandomGenerator().nextGaussian() * getStdDev() );
		
		if (nextWeight < 0.01) {
			nextWeight = 0.01;
		}
		
		if (nextWeight > 1.0) {
			nextWeight = 1;
		}
		
		
		GasNeatNeuronAllele newAllele = (GasNeatNeuronAllele) origAllele.cloneAllele();
		newAllele.setGasEmissionStrength(  nextWeight  );
		genesToRemove.add( origAllele );
		genesToAdd.add( newAllele );
		
	}

}

/**
 * @return standard deviation for weight delta
 */
public float getStdDev() {
	return stdDev;
}


}
