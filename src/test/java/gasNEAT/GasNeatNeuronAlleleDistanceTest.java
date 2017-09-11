package gasNEAT;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import org.jgap.Chromosome;
import org.jgap.ChromosomeMaterial;
import org.jgap.test.DummyFitnessFunction;
import org.jgap.test.MutationOperatorTest;
import org.junit.Before;

import com.anji.integration.TranscriberException;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;
import com.anji.neat.test.NeatChromosomeUtilityTest;
import com.anji.neat.test.TestChromosomeFactory;
import com.anji.nn.ActivationFunctionType;
import com.anji.nn.RecurrencyPolicy;
import com.anji.util.Properties;
import com.anji.util.Reset;
import gasNEAT.builders.GasNeatTranscriber;
import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import gasNEAT.geneticEncoding.GasNeatNeuronGene;
import gasNEAT.geneticOperators.GasNeatAddConnectionMutationOperator;
import junit.framework.TestCase;


public class GasNeatNeuronAlleleDistanceTest extends TestCase {

private final static String PROP_FILE_NAME = "properties/gasneat_add_connection_test.properties";

private RecurrencyPolicy recurrencyPolicy;
private Properties props = new Properties();
private boolean linearInputs = false;

private GasNeatNeuronAllele normalNeuron;
private GasNeatNeuronAllele actModNeuron;
private GasNeatNeuronAllele plaModNeuron;
private GasNeatNeuronAllele bothModNeuron;


	public GasNeatNeuronAlleleDistanceTest()  {
		initConfig();
	}



protected void initConfig()  {
	
	//props.loadFromResource( PROP_FILE_NAME );
	//props.setProperty( GasNeatConfiguration.MAX_GAS_EMISSION_STRENGTH_KEY, "300" );
	//props.setProperty( GasNeatConfiguration.MIN_GAS_EMISSION_STRENGTH_KEY, "300" );
	
	
	
	GasNeatConfiguration.setMaxEmissionRadius(300);
	GasNeatConfiguration.setMinEmissionRadius(300);
	
	
	GasNeatNeuronGene gasNeatNeuronGene = new GasNeatNeuronGene(NeuronType.HIDDEN, (long) 1, ActivationFunctionType.TANH );
	
	normalNeuron = new GasNeatNeuronAllele(gasNeatNeuronGene);
	normalNeuron.setFiringThreshold(0);
	normalNeuron.setGasEmissionRadius(300);
	normalNeuron.setGasEmissionStrength(0.1);
	normalNeuron.setGasEmissionType(0);
	normalNeuron.setReceptorStrength(1);
	normalNeuron.setPlasticityParameterA(1);
	normalNeuron.setPlasticityParameterB(-0.5);
	normalNeuron.setPlasticityParameterC(-0.5);
	normalNeuron.setPlasticityParameterD(0.25);
	normalNeuron.setPlasticityParameterLR(1);
	normalNeuron.setSynapticGasEmissionType(0);
	normalNeuron.setXCoordinate(0);
	normalNeuron.setYCoordinate(0);
		
	
	actModNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	
	
	
	actModNeuron.setReceptorType("NO_NO_NO_NO_NO");
	actModNeuron.setReceptorType("NO_G1_G2_NO_NO");
	
	//make a lot more tests for distance!
	
	
}

/**
 * test
 * 
 * @throws Exception
 */
public void testDistanceNormalToActMod()  {
	System.out.println( "actModNeuron.distance(normalNeuron) " + actModNeuron.distance(normalNeuron) );
	System.out.println( "normalNeuron.distance(actModNeuron) " + normalNeuron.distance(actModNeuron) );
	assert( actModNeuron.distance(normalNeuron) == 1.0);
	
	
}


}
