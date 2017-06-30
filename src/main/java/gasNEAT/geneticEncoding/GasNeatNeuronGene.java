package gasNEAT.geneticEncoding;

import com.anji.neat.NeuronGene;
import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunctionType;

/**
 * Extension of NeuronGene class. 
 * Might be expanded later, but for now its adding nothing
 */

public class GasNeatNeuronGene extends NeuronGene {
	
	/**
	 * Constructs a NeuronGene from a neuron object
	 * 
	 * @param neuron Neuron Object
	 */
	public GasNeatNeuronGene(NeuronType newType, Long newInnovationId, ActivationFunctionType anActivationType) {
		super(  newType, newInnovationId, anActivationType ); 
	}

	/**
	 * Copy constructor that constructs a deep copy of neuronGene object
	 * 
	 * @param neuronGene Neuron Gene Object
	 */
	public GasNeatNeuronGene(GasNeatNeuronGene neuronGene) {
		super(  neuronGene.getType(), neuronGene.getInnovationId(), neuronGene.getActivationType() ); 

	}

}
