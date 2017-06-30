package gasNEAT.geneticEncoding;

import org.jgap.Allele;

import com.anji.neat.ConnectionAllele;

/** Similar to the 'ConnectionAllele' class in com.anji.neat **/
public class GasNeatConnectionAllele extends ConnectionAllele {
	
	//Alleles must have genes to be associated with
	private GasNeatConnectionGene gasNeatConnectionGene;
	
	/**
	 * @param connectionGene
	 */
	public GasNeatConnectionAllele(GasNeatConnectionGene connectionGene) {
		super(connectionGene);
		this.gasNeatConnectionGene = connectionGene;
	}

	/* (non-Javadoc)
	 * @see org.jgap.Allele#cloneAllele()
	 */
	@Override
	public Allele cloneAllele() {
		
		GasNeatConnectionAllele clone = new GasNeatConnectionAllele(this.gasNeatConnectionGene);
		clone.setWeight(  getWeight()    );
		
		return clone;
	}
	
	/**
	 * @return src neuron ID
	 * @see GasNeatConnectionGene#getSrcNeuronId()
	 */
	@Override
	public Long getSrcNeuronId() {
		return gasNeatConnectionGene.getSrcNeuronId();
	}


	/**
	 * @return dest neuron ID
	 * @see GasNeatConnectionGene#getDestNeuronId()
	 */
	@Override
	public Long getDestNeuronId() {
		return gasNeatConnectionGene.getDestNeuronId();
	}
	


}
