package gasNEAT.builders;

import java.util.Random;

import com.anji.util.Properties;
import com.anji.util.Randomizer;

import gasNEAT.model.GasNeatSynapse;
import lombok.Getter;

/**
 * Generate synapses between neurons
 *
 */
public class SynapseBuilder {
	private final String sourceNeuronName;
	private final String targetNeuronName;
	private double weight;
	private final String synapseID;
	private Properties props;
	private boolean modulatory;
	private @Getter double a, b,c,d,lr;
	
	/**
	 * @param sourceNeuron Source neuron of Synapse
	 * @param targetNeuron Target neuron of Synapse
	 * @param weight Weight of the synapse
	 */
	public SynapseBuilder(String sourceNeuron, String targetNeuron, double weight, boolean modulatory, 
			double a, double b, double c, double d, double lr, Properties props) {
		this.weight = weight;
		this.sourceNeuronName = sourceNeuron;
		this.targetNeuronName = targetNeuron;
		this.synapseID = SpreadsheetConstants.SYNAPSE_ID_PREFIX + this.sourceNeuronName + this.targetNeuronName;
		this.props = props;
		this.modulatory = modulatory;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.lr = lr;
		
			
		
	}
	
	/**
	 * @param sourceNeuron Source neuron of Synapse
	 * @param targetNeuron Target neuron of Synapse
	 */
	public SynapseBuilder(String sourceNeuron, String targetNeuron) {
		this.sourceNeuronName = sourceNeuron;
		this.targetNeuronName = targetNeuron;
		this.synapseID = SpreadsheetConstants.SYNAPSE_ID_PREFIX + this.sourceNeuronName + this.targetNeuronName;
	}
	
	public boolean isModulatory() {
		return modulatory;
	}

	public double getWeight() {
		return this.weight;
	}
	
	public String getSourceNeuronName() {
		return this.sourceNeuronName;
	}
	
	public String getTargetNeuronName() {
		return this.targetNeuronName;
	}
	
	public String getSynapseID() {
		return this.synapseID;
	}
	
	public GasNeatSynapse build() {
		 GasNeatSynapse temp = new GasNeatSynapse(this);
		 temp.init(props);
		 return temp;
	}
	
	/**
	 * Builds synapse with random configuration
	 * @return GasNeatSynapse
	 */
	public GasNeatSynapse buildRandomSynapse() {
		//TODO FIXME - ConnectioNAllele goes from -1 to +1
		Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
		Random rand = randomizer.getRand();
		
		this.weight = rand.nextDouble() * 2.0; // - 1.0;  test with this? TODO FIXME
		GasNeatSynapse temp = new GasNeatSynapse(this);
		temp.init(props);
		return temp;
	}

	public Properties getProperties() {
		return props;
	}
}

