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
	private @Getter Long sourceNeuronId;
	private @Getter Long targetNeuronId;
	private double weight;
	private final long  synapseID;
	private Properties props;
	private boolean modulatory;
	private @Getter double a, b,c,d,lr;
	
	
	public static void main(String[] ars) {
		
		
		for (int i=1; i< 10; i++) {
			for (int j=1; j< 10; j++) {
				System.out.println( "i: " + i +",  j: "+ j  );
				long z = elegantPairing(i,j);
				System.out.println( "  z: "+  z );
				System.out.println( "  unpaired: i: "+ elegantUnpairing(z)[0]+ ", j: "+ elegantUnpairing(z)[1]  );
				
				if (i != elegantUnpairing(z)[0]) {
					System.err.println( "i did not match!"  );
					System.exit(-1);
				}
				
				if (j != elegantUnpairing(z)[1]) {
					System.err.println( "j did not match!"  );
					System.exit(-1);
				}
				
			}
			
			
			for (int j=10; j> 1; j--) {
				System.out.println( "i: " + i +",  j: "+ j  );
				long z = elegantPairing(i,j);
				System.out.println( "  z: "+  z );
				System.out.println( "  unpaired: i: "+ elegantUnpairing(z)[0]+ ", j: "+ elegantUnpairing(z)[1]  );
				
				if (i != elegantUnpairing(z)[0]) {
					System.err.println( "i did not match!"  );
					System.exit(-1);
				}
				
				if (j != elegantUnpairing(z)[1]) {
					System.err.println( "j did not match!"  );
					System.exit(-1);
				}
				
			}
			
			
			
			
			
			
		}
		
		
		
	}
	
	//https://www.semanticscholar.org/paper/An-Elegant-Pairing-Function-Szudzik/68e87ad59107481bc3cfdf1669706fd0368cce60
	//http://szudzik.com/ElegantPairing.pdf
	public static long elegantPairing(long x, long y) {
		
		if (x > 1000000000 || y > 1000000000) {
			try {
				throw new Exception("network must be too large, ids are beyond the maximum allowable size!");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit( -1 );
			}
		}
		
		
		if (x < y) {
			return y*y + x;
		} else {
			return x*x + x + y;			
		}
		
		 
	}

	//https://www.semanticscholar.org/paper/An-Elegant-Pairing-Function-Szudzik/68e87ad59107481bc3cfdf1669706fd0368cce60
	//http://szudzik.com/ElegantPairing.pdf
	public static long[] elegantUnpairing(long z) {
		
		long[] pair = new long[2];
		
		long trunSqrtZ = (long)( Math.sqrt(z ) );
		
		if (z - trunSqrtZ*trunSqrtZ < trunSqrtZ ) {
			pair[0] = z - trunSqrtZ * trunSqrtZ;
			pair[1] = trunSqrtZ;
		} else {
			pair[0] = trunSqrtZ;
			pair[1] = z - trunSqrtZ*trunSqrtZ - trunSqrtZ;			
		}
		
		return pair;

	}
	
	/**
	 * @param sourceNeuron Source neuron of Synapse
	 * @param targetNeuron Target neuron of Synapse
	 * @param weight Weight of the synapse
	 */
	public SynapseBuilder(long sourceNeuron, long targetNeuron, double weight, boolean modulatory, 
			double a, double b, double c, double d, double lr, Properties props) {
		this.weight = weight;
		this.sourceNeuronId = sourceNeuron;
		this.targetNeuronId = targetNeuron;
		this.synapseID = elegantPairing( sourceNeuronId, targetNeuronId);
				
				//SpreadsheetConstants.SYNAPSE_ID_PREFIX + this.sourceNeuronName + this.targetNeuronName;
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
	public SynapseBuilder(long sourceNeuron, long targetNeuron) {
		this.sourceNeuronId = sourceNeuron;
		this.targetNeuronId = targetNeuron;
		this.synapseID = elegantPairing(sourceNeuron, targetNeuron); 
				//
				//SpreadsheetConstants.SYNAPSE_ID_PREFIX + this.sourceNeuronName + this.targetNeuronName;
	}
	
	public boolean isModulatory() {
		return modulatory;
	}

	public double getWeight() {
		return this.weight;
	}
	
	public long getSynapseID() {
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
		
		System.err.println("NOT READY!");
		System.exit(-1);
		
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

