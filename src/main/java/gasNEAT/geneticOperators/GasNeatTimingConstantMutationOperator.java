
package gasNEAT.geneticOperators;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

import com.anji.integration.AnjiRequiredException;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.WeightMutationOperator;
import com.anji.util.Properties;

import gasNEAT.geneticEncoding.GasNeatNeuronAllele;

/**
 * Mutates A,B,C,D or LR for ALL neurons
 * 
 * @author Jason Yoder
 */
public class GasNeatTimingConstantMutationOperator extends MutationOperator {

/**
 * properties key, perturb weight mutation rate
 */
public static final String TIMING_CONSTANT_MUTATE_RATE_KEY = "gasneat.timing.constant.mutation.rate";

/**
 * properties key, standard deviation of perturb weight mutation
 */
private static final String TIMING_CONSTANT_MUTATE_STD_DEV_KEY = "gasneat.timing.constant.mutation.std.dev";

/**
 * default mutation rate
 */
public static final float DEFAULT_MUTATE_RATE = 0.10f;

/**
 * default standard deviation for weight delta
 */
public final static float DEFAULT_STD_DEV = 1.0f;

private float stdDev = DEFAULT_STD_DEV;

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	setMutationRate( props.getFloatProperty( TIMING_CONSTANT_MUTATE_RATE_KEY, DEFAULT_MUTATE_RATE ) );
	stdDev = props.getFloatProperty( TIMING_CONSTANT_MUTATE_STD_DEV_KEY, DEFAULT_STD_DEV );
}

/**
 * @see MutationOperator#MutationOperator(float)
 */
public GasNeatTimingConstantMutationOperator() {
	super( DEFAULT_MUTATE_RATE );
}

/**
 * @param newMutationRate
 * @see WeightMutationOperator#WeightMutationOperator(float, float)
 */
public GasNeatTimingConstantMutationOperator( float newMutationRate ) {
	this( newMutationRate, DEFAULT_STD_DEV );
}

/**
 * @param newMutationRate
 * @param newStdDev
 * @see MutationOperator#MutationOperator(float)
 */
public GasNeatTimingConstantMutationOperator( float newMutationRate, float newStdDev ) {
	super( newMutationRate );
	stdDev = newStdDev;
}

/**
 *  Changes either A,B,C,D or LR
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
	
	
	
	//don't mutate every generation, but when you do, change the values for all neurons
	if ( config.getRandomGenerator().nextFloat() <  this.getMutationRate() ) {
		//System.out.println("plasticity mutation occuring!");
		
		List neurons = NeatChromosomeUtility.getNeuronList(  target.getAlleles()   );
		Iterator iter = neurons.iterator();
		

		
		double mutationVector = ( config.getRandomGenerator().nextGaussian() * getStdDev() );
		double oldValue = 0;
		double newValue = 0;
		
		while ( iter.hasNext() ) {
			GasNeatNeuronAllele origAllele = (GasNeatNeuronAllele) iter.next();
			GasNeatNeuronAllele newAllele = (GasNeatNeuronAllele) origAllele.cloneAllele();
			
			
			//ULTRAJULYTODO
			//need to implment this method in allele
			//oldValue = origAllele.getTimingConstant();
			
			newValue = oldValue + mutationVector;
			
			if (newValue < -1.0)
				newValue = -1.0;
			if (newValue > 1.0) 
				newValue = 1;
			
			//ULTRAJULYTODO
			//need to implment this method in allele
			//newAllele.setTimingConstant( newValue  );
			
			genesToRemove.add( origAllele );
			genesToAdd.add( newAllele );
		
		}
		
		
		
	} else {
		//System.out.println("no plasticity mutation this time!");
	}
	
	
	
	
	
}

/**
 * @return standard deviation for weight delta
 */
public float getStdDev() {
	return stdDev;
}

}
