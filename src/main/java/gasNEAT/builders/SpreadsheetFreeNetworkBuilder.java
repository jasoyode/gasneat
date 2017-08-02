package gasNEAT.builders;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.jgap.Allele;
import org.jgap.ChromosomeMaterial;

import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunctionFactory;
import com.anji.nn.ActivationFunctionType;

import gasNEAT.geneticEncoding.GasNeatConnectionAllele;
import gasNEAT.geneticEncoding.GasNeatConnectionGene;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import gasNEAT.geneticEncoding.GasNeatNeuronGene;
import gasNEAT.model.GasNeatReceptor;
import gasNEAT.model.GasNeatSynapse;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.view.Constants.ActivationThresholdFunction;

/**
 * Builds neural network with random configuration
 *
 */
public class SpreadsheetFreeNetworkBuilder {
	private PrimaryIDManager primaryIDManger;
	/** Logger reference  **/
	private static Logger logger = Logger.getLogger(SpreadsheetFreeNetworkBuilder.class);
	
	/**
	 * @param primaryIDManager
	 */
	public SpreadsheetFreeNetworkBuilder(PrimaryIDManager primaryIDManager) {
		this.primaryIDManger = primaryIDManager;
	}
	
	/**
	 * @param numInputNeurons Number of neurons in neural network
	 * @return Chromosome
	 */
	public ChromosomeMaterial buildBaseNetworkAlleles(int numInputNeurons) {
		System.err.println("NOT READY!");
		System.exit(-1);
		
		ArrayList<Allele> alleles = new ArrayList<Allele>();
		ArrayList<GasNeatNeuron> inputNeurons = new ArrayList<GasNeatNeuron>();
		
		ActivationFunctionFactory factory = ActivationFunctionFactory.getInstance();
		
		//BUILD INPUT NEURONS
		for(int i=0;i<numInputNeurons;i++) {
			logger.info("Adding a neuron!");
			//default for now - eliminate eventually
			ActivationThresholdFunction atf = ActivationThresholdFunction.LOGARITHMIC_SIGMOID;
			
			//hardcoded "G0" since these are always standard
			NeuronBuilder neuronBuilder = new NeuronBuilder( 	factory.get(ActivationFunctionType.SIGMOID.toString() ),
																atf, 
																primaryIDManger.getNewNeuronID(),
																NeuronType.INPUT, 
																"G0" ); 
			GasNeatNeuron neuron = neuronBuilder.buildRandomNeuron();
			inputNeurons.add(neuron);
			
			GasNeatNeuronGene gasNeatNeuronGene = new GasNeatNeuronGene( NeuronType.INPUT, 
							this.primaryIDManger.getLongID(), ActivationFunctionType.SIGMOID);
			alleles.add(new GasNeatNeuronAllele(gasNeatNeuronGene));
			this.buildReceptorForNeuron(neuron);
			logger.info("Receptors: " + neuron.getReceptor().getReceptorID());
		}
			
		//BUILD OUTPUT NEURON
		//default for now - eliminate eventually
		ActivationThresholdFunction atf = ActivationThresholdFunction.LOGARITHMIC_SIGMOID;
		
		//hardcoded "G0" since these are always standard
		NeuronBuilder output = new NeuronBuilder(
				factory.get(ActivationFunctionType.SIGMOID.toString() ), 
				atf, 
				primaryIDManger.getNewNeuronID(), 
				NeuronType.OUTPUT, 
				"G0");
		
		logger.info("Adding a neuron!");
		
		GasNeatNeuron outputNeuron = output.buildRandomNeuron();
		this.buildReceptorForNeuron(outputNeuron);
		GasNeatNeuronGene gasNeatNeuronGene = new GasNeatNeuronGene( 
				NeuronType.OUTPUT, 
				this.primaryIDManger.getLongID(), 
				ActivationFunctionType.SIGMOID);
		alleles.add(new GasNeatNeuronAllele(gasNeatNeuronGene));
		
		//TODO FIXME - will also need to enabled multiple outputs eventually
		//ADD SYNAPSES
		for(GasNeatNeuron inputNeuronId  : inputNeurons) {
			SynapseBuilder synapseBuilder = new SynapseBuilder(inputNeuronId.getNeuronID(),output.getNeuronID());
			GasNeatSynapse synapse = synapseBuilder.buildRandomSynapse();
			inputNeuronId.addOutgoingConnection(synapse);
			
			//TODO FIXME needs to be tested, but in principle could work
			GasNeatConnectionGene synapseGene = new GasNeatConnectionGene( 
					this.primaryIDManger.getLongID(), 
					inputNeuronId.getId(), outputNeuron.getId() );
			
			alleles.add(new GasNeatConnectionAllele(synapseGene));
		}
		
		//BUILD ALLELES FOR ALL COMPONENTS
		return new ChromosomeMaterial(alleles);
	}
	
	 /**
	 * @param neuron GasNeatNeuron
	 */
	private void buildReceptorForNeuron(GasNeatNeuron neuron) {
		ReceptorBuilder receptorBuilder = new ReceptorBuilder(primaryIDManger.getCorrespondingReceptorID(neuron), SpreadsheetConstants.SYNAPSE_ID_PREFIX);
		GasNeatReceptor receptor = receptorBuilder.buildRandomReceptor();
		logger.info("ReceptorID: " + receptor.getReceptorID() + " " + receptorBuilder.getReceptorID());
		neuron.setReceptor(receptor);
	}
}
