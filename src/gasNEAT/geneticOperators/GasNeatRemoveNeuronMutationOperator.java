/*
 * 
 */
package gasNEAT.geneticOperators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jgap.ChromosomeMaterial;
import org.jgap.Configuration;
import org.jgap.MutationOperator;

import com.anji.integration.AnjiRequiredException;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.Evolver;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronType;
import com.anji.neat.RemoveConnectionMutationOperator;
import com.anji.neat.WeightMagnitudeComparator;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import lombok.Getter;

/**
 * 
 * @author Jason Yoder
 */
public class GasNeatRemoveNeuronMutationOperator extends MutationOperator implements Configurable {

	
private static Logger logger = Logger.getLogger( Evolver.class );

/**
 * properties key, remove connection mutation rate
 */
public static final String REMOVE_NEURON_MUTATE_RATE_KEY = "gasneat.remove.neuron.mutation.rate";

private static final String REMOVE_NEURON_MAX_DEGREE_KEY = "gasneat.remove.neuron.max.degree";

/**
 * default mutation rate
 */
public final static float DEFAULT_MUTATE_RATE = 0.01f;
public final static int DEFAULT_REMOVE_NEURON_MAX_DEGREE = 2;

private @Getter int  removeNeuronMaxDegree; 


/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	setMutationRate( props.getFloatProperty( REMOVE_NEURON_MUTATE_RATE_KEY,
			GasNeatRemoveNeuronMutationOperator.DEFAULT_MUTATE_RATE ) );
	
	removeNeuronMaxDegree = props.getIntProperty( REMOVE_NEURON_MAX_DEGREE_KEY, 
			GasNeatRemoveNeuronMutationOperator.DEFAULT_REMOVE_NEURON_MAX_DEGREE);

}

/**
 * @see RemoveConnectionMutationOperator#RemoveConnectionMutationOperator(float)
 */
public GasNeatRemoveNeuronMutationOperator() {
	this( DEFAULT_MUTATE_RATE );
	
}

/**
 * @see MutationOperator#MutationOperator(float)
 */
public GasNeatRemoveNeuronMutationOperator( float aMutationRate ) {
	super( aMutationRate );
}


/**
 * Removes, in ascending order of edge count, neurons
 * Maximum number of neurons that can be removed
 * is determined by mutation rate.
 * 
 * @param jgapConfig must be <code>NeatConfiguration</code>
 * @param target chromosome material to mutate
 * @param allelesToAdd <code>Set</code> contains <code>Allele</code> objects
 * @param allelesToRemove <code>Set</code> contains <code>Allele</code> objects
 * @see org.jgap.MutationOperator#mutate(org.jgap.Configuration, org.jgap.ChromosomeMaterial,
 * java.util.Set, java.util.Set)
 */
protected void mutate( Configuration jgapConfig, final ChromosomeMaterial target,
		Set allelesToAdd, Set allelesToRemove, int currentGeneration, int maxGenerations ) {
	if ( ( jgapConfig instanceof NeatConfiguration ) == false )
		throw new AnjiRequiredException( "com.anji.neat.NeatConfiguration" );
	
	NeatConfiguration config = (NeatConfiguration) jgapConfig;
	
	List allHiddenNeurons = NeatChromosomeUtility.getNeuronList(target.getAlleles(), NeuronType.HIDDEN );
	//will need these for 
	List allConnections = NeatChromosomeUtility.getConnectionList(target.getAlleles() );
	
	mutate( config, allHiddenNeurons, allConnections, allelesToRemove );
}



private void mutate( NeatConfiguration config, List hiddenNeurons, List allConns, Set allelesToRemove ) {
	
	
	Iterator it = hiddenNeurons.iterator();
	
	logger.debug( hiddenNeurons.size() + " hidden neurons"    );
	logger.debug( allConns.size() + " connections total"    );
	
	while ( it.hasNext() ) {
		int edgeCount = 0;
		GasNeatNeuronAllele neuronAllele = (GasNeatNeuronAllele) it.next();
		Long id = neuronAllele.getInnovationId();
		
		logger.debug( "checking out neuron[" + id +"]");
		
		Iterator connectionIt = allConns.iterator();
		
		List<ConnectionAllele> potentialConnectionsToPrune = new ArrayList<ConnectionAllele>();
		
		while ( connectionIt.hasNext() ) {
			ConnectionAllele connAllele = (ConnectionAllele) connectionIt.next();
			
			logger.debug( "checking out connection [" + connAllele.getSrcNeuronId() +"] -> ["+connAllele.getDestNeuronId() + "]"  );
			
			//self loop count once
			if (connAllele.getDestNeuronId().equals(id) && connAllele.getSrcNeuronId().equals(id)) {
				edgeCount++;
				potentialConnectionsToPrune.add(connAllele);
			} else if (connAllele.getSrcNeuronId().equals(id) ) {
				edgeCount++;
				potentialConnectionsToPrune.add(connAllele);
			} else if (connAllele.getDestNeuronId().equals(id) ) {
				edgeCount++;
				potentialConnectionsToPrune.add(connAllele);
			}
		}
		
		logger.debug( "Edge count = " + edgeCount );
		
		logger.debug("mutation rate: "+  getMutationRate()  );
		
		if (removeNeuronMaxDegree > edgeCount && doesMutationOccur( config.getRandomGenerator() )) {
			
			allelesToRemove.addAll( potentialConnectionsToPrune );
			allelesToRemove.add( neuronAllele );
		}
		
	}
	
	
}



}
