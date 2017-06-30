package gasNEAT.geneticEncoding;

import com.anji.neat.ConnectionGene;

/**
 * Represent a connection as the section of code where connection info would be specified
 * Could include more here if more needs to be specified, but I don't think there is any need.  
 *
 */
public class GasNeatConnectionGene extends ConnectionGene {

	/**
	 * @param synapse Synapse
	 * @param id
	 */
	public GasNeatConnectionGene( Long anInnovationId, Long aSrcNeuronId, Long aDestNeuronId) {
		super(anInnovationId, aSrcNeuronId, aDestNeuronId);
	}
	

	
}
