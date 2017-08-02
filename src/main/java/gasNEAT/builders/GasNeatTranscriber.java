package gasNEAT.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.jgap.Allele;
import org.jgap.Chromosome;

import com.anji.integration.Transcriber;
import com.anji.integration.TranscriberException;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunctionFactory;
import com.anji.nn.ActivationFunctionType;
import com.anji.nn.Neuron;
import com.anji.nn.RecurrencyPolicy;
import com.anji.util.Configurable;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.model.GasNeatSynapse;
import gasNEAT.nn.GasNeatNet;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.util.GasNeatChromosomeUtility;
import gasNEAT.view.Constants;

public class GasNeatTranscriber implements Transcriber, Configurable {

	private static Logger logger = Logger.getLogger( GasNeatTranscriber.class );
	private RecurrencyPolicy recurrencyPolicy = RecurrencyPolicy.BEST_GUESS;
	private Properties props;
	
	public GasNeatTranscriber(RecurrencyPolicy policy) {
		recurrencyPolicy  = policy;
	}
		
	public GasNeatTranscriber() {
		this( RecurrencyPolicy.BEST_GUESS );
	}
	
	@Override
	public Class getPhenotypeClass() {
		return GasNeatNeuralNetwork.class;
	}
	
	private void setDefaultNeuralNetworkParameters(GasNeatNeuralNetwork neuralNetwork) {
		neuralNetwork.setNetworkType(Constants.NetworkType.RECURRENT);
		ActivationFunctionFactory factory = ActivationFunctionFactory.getInstance();
		neuralNetwork.setActivationFunction( factory.get( ActivationFunctionType.SIGMOID.toString()  )) ;
		neuralNetwork.setMode(Constants.VisualizationModes.TRANSLUCENT_GAS);
	}

	/**
	 * @see Configurable#init(Properties)
	 */
	@Override
	public void init( Properties props ) {
		this.props = props;
		recurrencyPolicy = RecurrencyPolicy.load( props );
	}
	
	/**
	 * @see Transcriber#transcribe(Chromosome)
	 */
	@Override
	public Object transcribe( Chromosome c ) throws TranscriberException {
		
		
		try {
			throw new Exception();
		} catch (Exception e) {
			System.err.println( "why is this called?"  );
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		
		return newGasNeatNet( c, null  );
	}

	
	public GasNeatNeuralNetwork newGasNeatNeuralNetworkOld(Chromosome chromosome) {
		GasNeatNeuralNetwork neuralNetwork = new GasNeatNeuralNetwork();
		this.setDefaultNeuralNetworkParameters(neuralNetwork);
		
		@SuppressWarnings("unchecked")
		SortedSet<Allele> alleles = chromosome.getAlleles();
		
		return neuralNetwork;
	}
	


/**
 * create new <code>GasNeatNeuralNetwork</code> from <code>chromosome</code>
 * 
 * @param chromosome chromosome to transcribe
 * @return phenotype
 * @throws TranscriberException
 */
public GasNeatNet newGasNeatNet( Chromosome chromosome, Properties props ) throws TranscriberException {
	

	//temporary sanity check for same learning rules:
	if ( logger.isDebugEnabled() ) {
		List<GasNeatNeuronAllele> neurons = NeatChromosomeUtility.getNeuronList(chromosome.getAlleles());
		double A = neurons.get(0).getPlasticityParameterA();
		double B = neurons.get(0).getPlasticityParameterB();
		double C = neurons.get(0).getPlasticityParameterC();
		double D = neurons.get(0).getPlasticityParameterD();
		double LR = neurons.get(0).getPlasticityParameterLR();
		
		for (GasNeatNeuronAllele neuron: neurons) {
			if (neuron.getPlasticityParameterA() != A) {
				System.out.println("A didn't match! exiting");
				System.exit(1);
			} else if (neuron.getPlasticityParameterB() != B) {
				System.out.println("B didn't match! exiting");
				System.exit(1);
			}  else if (neuron.getPlasticityParameterC() != C) {
				System.out.println("C didn't match! exiting");
				System.exit(1);
			}  else if (neuron.getPlasticityParameterD() != D) {
				System.out.println("D didn't match! exiting");
				System.exit(1);
			}  else if (neuron.getPlasticityParameterLR() != LR) {
				
				System.err.println(neuron.getPlasticityParameterLR() +"!="+ LR   );
				
				System.out.println("LR didn't match! exiting");
				System.exit(1);
			}  
		}
	}
	
	
	Collection<Allele> effectiveAlleles = new ArrayList<Allele>();
	for (Object a: chromosome.getAlleles() ) {
		effectiveAlleles.add(  (Allele)a );

	}

	//REMOVE IN-EFFECTIVE ALLELES
	Collection<Allele> toBeRemoved = GasNeatChromosomeUtility.getAllUnactivatableAllelesFromSrcNeurons(  effectiveAlleles  );
	
	int recurrentSteps = GasNeatChromosomeUtility.getMinimumStepsFromSrcToDestNeurons( effectiveAlleles,  props.getIntProperty( GasNeatConfiguration.GAS_SPEED_KEY) );
	logger.debug("RECURRENT STEPS:        "+ recurrentSteps );
	
	
	
	effectiveAlleles.removeAll(toBeRemoved );
	
	Map<Long, GasNeatNeuron> allNeurons = new HashMap<Long, GasNeatNeuron>();

	// input neurons - should work now that all neurons are randomized in the Utility
	SortedMap<Long,GasNeatNeuronAllele> inNeuronAlleles = NeatChromosomeUtility.getNeuronMap( effectiveAlleles, NeuronType.INPUT );
	
	//ADD AND SETUP INPUT NEURONS
	List<GasNeatNeuron> inNeurons = new ArrayList<GasNeatNeuron>();
	Iterator it = inNeuronAlleles.values().iterator();
	while ( it.hasNext() ) {
		Object o =  it.next();
		GasNeatNeuronAllele neuronAllele = (GasNeatNeuronAllele) o;
		
		GasNeatNeuron n = new GasNeatNeuron( ActivationFunctionFactory.getInstance().get(neuronAllele.getActivationType().toString() ), props );
		n.setId( neuronAllele.getInnovationId().longValue() );
		
		
		
		//MUST BE DONE FOR GASNEAT NEURONS
		n.setPropertiesFromAllele( neuronAllele );
		inNeurons.add( n );
		allNeurons.put( neuronAllele.getInnovationId(), n );
	}
	
	// ADD AND SETUP OUTPUT NEURONS
	SortedMap outNeuronAlleles = NeatChromosomeUtility.getNeuronMap( effectiveAlleles,	NeuronType.OUTPUT );
	
	if (outNeuronAlleles.size() == 0) {
		System.err.println("NO OUTPUT NEURONS IN THIS CHROMOSOME!!!!");
		System.exit(1);
	}
	
	List<GasNeatNeuron> outNeurons = new ArrayList<GasNeatNeuron>();
	it = outNeuronAlleles.values().iterator();
	while ( it.hasNext() ) {
		GasNeatNeuronAllele neuronAllele = (GasNeatNeuronAllele) it.next();
		GasNeatNeuron n = new GasNeatNeuron( ActivationFunctionFactory.getInstance().get(
				neuronAllele.getActivationType().toString() ), props );
		n.setId( neuronAllele.getInnovationId().longValue() );
		//MUST BE DONE FOR GASNEAT NEURONS
		n.setPropertiesFromAllele( neuronAllele );
		outNeurons.add( n );
		allNeurons.put( neuronAllele.getInnovationId(), n );
	}
	
	// ADD AND SETUP HIDDEN NEURONS 
	SortedMap hiddenNeuronAlleles = NeatChromosomeUtility.getNeuronMap( effectiveAlleles,
			NeuronType.HIDDEN );
	it = hiddenNeuronAlleles.values().iterator();
	while ( it.hasNext() ) {
		GasNeatNeuronAllele neuronAllele = (GasNeatNeuronAllele) it.next();
		GasNeatNeuron n = new GasNeatNeuron( ActivationFunctionFactory.getInstance().get(
				neuronAllele.getActivationType().toString() ), props );
		n.setId( neuronAllele.getInnovationId().longValue() );
					
		//MUST BE DONE FOR GASNEAT NEURONS
		n.setPropertiesFromAllele( neuronAllele );
		
		allNeurons.put( neuronAllele.getInnovationId(), n );
	}
	
	Collection recurrentConns = new ArrayList();
	List remainingConnAlleles = NeatChromosomeUtility.getConnectionList( effectiveAlleles );
	
	
	//this is what is used to create the underlying recurrentsimulator model 
	HashMap<Long, GasNeatSynapse> synapseMap = new HashMap<Long, GasNeatSynapse>();
	

	
	//transcribe each connection
	for (Object o: remainingConnAlleles) {

		ConnectionAllele connAllele = (ConnectionAllele) o;
		Neuron src = (Neuron) allNeurons.get( connAllele.getSrcNeuronId() );
		Neuron dest = (Neuron) allNeurons.get( connAllele.getDestNeuronId() );
		
		if ( src == null || dest == null ) {
			System.out.println("THIS SHOULD NOT HAPPEN EVER ONCE TRANSCRIBER IS PRUNING PROPERLTY!!!");
			//throw new TranscriberException( "connection with missing src or dest neuron: "
			//			+ connAllele.toString() );
			System.exit(1);
		}
		
		boolean modulatory =false;
		GasNeatNeuron srcNeuron = (GasNeatNeuron)src; 
		GasNeatNeuron targetNeuron = (GasNeatNeuron)dest; 
		//System.out.print( "Connection ID: "+ connAllele.getInnovationId() );   
		//System.out.print( "\tsrcNeuronID: "+srcNeuron.getId()  );
		//System.out.print( "\tsrcNeuron.getSynapseProductionType()" + srcNeuron.getSynapseProductionType() );
		//System.out.println( "\tsrcNeuron.getGasProductionType()" + srcNeuron.getGasProductionType()  );
		
		//if neuron is non-gas producing and has non standard connections, it is modulatory
		//and should not have synaptic plasticity enabled
		//according to settings....
		if ( srcNeuron.getSynapseProductionTypeInt() != 0
				&& srcNeuron.getGasProductionTypeInt() == 0   ) {
			modulatory = true;
		}
		
		double a = targetNeuron.getPlasticityParameterA();
		double b = targetNeuron.getPlasticityParameterB();
		double c = targetNeuron.getPlasticityParameterC();
		double d = targetNeuron.getPlasticityParameterD();
		double lr = targetNeuron.getPlasticityParameterLR();
		
		
		SynapseBuilder builder = new SynapseBuilder(src.getId(), dest.getId(), connAllele.getWeight(), modulatory, a,b,c,d,lr, props );
		
		synapseMap.put( SynapseBuilder.elegantPairing( src.getId(), dest.getId()), new GasNeatSynapse(builder) );
		
	}

	// build network
	return new GasNeatNet( allNeurons.values(), inNeurons, outNeurons, recurrentConns, chromosome
			.getId().toString(), synapseMap, props, recurrentSteps );
}
	


}
