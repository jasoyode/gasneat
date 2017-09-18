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

private GasNeatNeuronAllele normalNeuronTranslated;
private GasNeatNeuronAllele normalNeuronDiffPlasticity;


private GasNeatNeuronAllele actModReceiverNeuron;
private GasNeatNeuronAllele plaModReceiverNeuron;
private GasNeatNeuronAllele bothModReceiverNeuron;


private GasNeatNeuronAllele g1GasProducerNeuron;
private GasNeatNeuronAllele g1SynGasProducerNeuron;

private GasNeatNeuronAllele g2GasProducerNeuron;
private GasNeatNeuronAllele g2SynGasProducerNeuron;

private GasNeatNeuronAllele g2GasProducerNeuronTranslated;
private GasNeatNeuronAllele g2SynGasProducerNeuronTranslated;

private GasNeatNeuronAllele g2GasProducerNeuronActModReceiver;
private GasNeatNeuronAllele g2SynGasProducerNeuronPlaModReceiver;
private GasNeatNeuronAllele g2SynGasProducerNeuronBothModReceiver;

private GasNeatNeuronAllele g4GasProducerNeuron;
private GasNeatNeuronAllele g4SynGasProducerNeuron;

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
	normalNeuron.setReceptorType("G0_NO_NO_NO_NO");
	normalNeuron.setGasSpeed( 100 );
	
	
	actModReceiverNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	actModReceiverNeuron.setReceptorType(  "G0_G1_G2_NO_NO");
	
	plaModReceiverNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	plaModReceiverNeuron.setReceptorType(  "G0_NO_NO_G1_G2");
	
	bothModReceiverNeuron= (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	bothModReceiverNeuron.setReceptorType( "G0_G1_G2_G3_G4");
	
	
	
	normalNeuronTranslated = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	normalNeuronTranslated.setXCoordinate(100);
	normalNeuronTranslated.setYCoordinate(-100);
	
	
	
	normalNeuronDiffPlasticity = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	normalNeuronDiffPlasticity.setPlasticityParameterA(0);
	normalNeuronDiffPlasticity.setPlasticityParameterB(0);
	normalNeuronDiffPlasticity.setPlasticityParameterC(0);
	normalNeuronDiffPlasticity.setPlasticityParameterD(0);
	normalNeuronDiffPlasticity.setPlasticityParameterLR(0);
	
	
	
	g1GasProducerNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	g1GasProducerNeuron.setGasEmissionType(1);
	
	g2GasProducerNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	g2GasProducerNeuron.setGasEmissionType(2);
	

	g1SynGasProducerNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	g1SynGasProducerNeuron.setSynapticGasEmissionType(1);
	
	g2SynGasProducerNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	g2SynGasProducerNeuron.setSynapticGasEmissionType(2);
	
	g4SynGasProducerNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	g4SynGasProducerNeuron.setSynapticGasEmissionType(4);
	
	g4GasProducerNeuron = (GasNeatNeuronAllele) normalNeuron.cloneAllele();
	g4GasProducerNeuron.setSynapticGasEmissionType(4);


	
	g2GasProducerNeuronTranslated = (GasNeatNeuronAllele) g2GasProducerNeuron.cloneAllele();
	g2GasProducerNeuronTranslated.setXCoordinate(-100);
	g2GasProducerNeuronTranslated.setYCoordinate(100);
	
	g2SynGasProducerNeuronTranslated = (GasNeatNeuronAllele) g2SynGasProducerNeuron.cloneAllele();
	g2SynGasProducerNeuronTranslated.setXCoordinate(500);
	g2SynGasProducerNeuronTranslated.setYCoordinate(-150);

	g2GasProducerNeuronActModReceiver = (GasNeatNeuronAllele) g2GasProducerNeuron.cloneAllele();
	g2GasProducerNeuronActModReceiver.setReceptorType("G0_G1_G2_NO_NO");
	
	
	g2SynGasProducerNeuronPlaModReceiver = (GasNeatNeuronAllele)g2SynGasProducerNeuron.cloneAllele();
	g2SynGasProducerNeuronPlaModReceiver.setReceptorType("G0_NO_NO_G1_G2");
	
	
	g2SynGasProducerNeuronBothModReceiver = (GasNeatNeuronAllele) g2SynGasProducerNeuron.cloneAllele();
	g2SynGasProducerNeuronBothModReceiver.setReceptorType("G0_G1_G2_G3_G4");
	
	
	
	
	
}

/**
 * test distance of normal neuron than varies only in its receptor type
 * 
 */
public void testDistances()  {
	assertEquals("Non mod receiver to act mod receiver should match", 0.2, actModReceiverNeuron.distance(normalNeuron) );
	assertEquals("Distance must be symmetric!",  
			actModReceiverNeuron.distance(normalNeuron),
			normalNeuron.distance(actModReceiverNeuron));
	
	assertEquals("Non mod receiver to pla mod receiver should match", 0.2, plaModReceiverNeuron.distance(normalNeuron) );
	assertEquals("Distance must be symmetric!",  
			plaModReceiverNeuron.distance(normalNeuron),
			normalNeuron.distance(plaModReceiverNeuron));

	assertEquals("Non mod receiver to both mod receiver should match", 0.4, bothModReceiverNeuron.distance(normalNeuron) );
	assertEquals("Distance must be symmetric!",  
			bothModReceiverNeuron.distance(normalNeuron),
			normalNeuron.distance(bothModReceiverNeuron));
	
	
	
	assertEquals("normalNeuronTranslated should match", 0.0, normalNeuronTranslated.distance(normalNeuron) );
	assertEquals("Distance must be symmetric!",  
			normalNeuronTranslated.distance(normalNeuron),
			normalNeuron.distance(normalNeuronTranslated));
	
	assertEquals("normalNeuronDiffPlasticity should match", 0.226, normalNeuronDiffPlasticity.distance(normalNeuron) );
	assertEquals("Distance must be symmetric!",  
			normalNeuronDiffPlasticity.distance(normalNeuron),
			normalNeuron.distance(normalNeuronDiffPlasticity));
	
	assertEquals("g1GasProducerNeuron should match", 1.0, g1GasProducerNeuron.distance(normalNeuron) );
	assertEquals("Distance must be symmetric!",  
			g1GasProducerNeuron.distance(normalNeuron),
				normalNeuron.distance(g1GasProducerNeuron));
	
	
	assertEquals("g1SynGasProducerNeuron should match", 1.0, g1SynGasProducerNeuron.distance(normalNeuron) );
		assertEquals("Distance must be symmetric!",  
				g1SynGasProducerNeuron.distance(normalNeuron),
				normalNeuron.distance(g1SynGasProducerNeuron));
	
	assertEquals("g1SynGasProducerNeuron, g1GasProducerNeuron should match", 1.0, g1SynGasProducerNeuron.distance(g1GasProducerNeuron) );
		assertEquals("Distance must be symmetric!",  
				g1SynGasProducerNeuron.distance(g1GasProducerNeuron),
				g1GasProducerNeuron.distance(g1SynGasProducerNeuron));
	
	assertEquals("g1GasProducerNeuron, g2GasProducerNeuron should match", 1.0, g2GasProducerNeuron.distance(g1GasProducerNeuron) );
	assertEquals("Distance must be symmetric!",  
			g2GasProducerNeuron.distance(g1GasProducerNeuron),
			g1GasProducerNeuron.distance(g2GasProducerNeuron));
	
	assertEquals("g1SynGasProducerNeuron, g2SynGasProducerNeuron should match", 1.0, g1SynGasProducerNeuron.distance(g2SynGasProducerNeuron) );
	assertEquals("Distance must be symmetric!",  
			g1SynGasProducerNeuron.distance(g2SynGasProducerNeuron),
			g2SynGasProducerNeuron.distance(g1SynGasProducerNeuron));
	
	
	assertEquals("g2GasProducerNeuronTranslated should match", 0.14142, g2GasProducerNeuronTranslated.distance(g2GasProducerNeuron), 0.001 );
	assertEquals("Distance must be symmetric!",  
			g2GasProducerNeuronTranslated.distance(g2GasProducerNeuron),
			g2GasProducerNeuron.distance(g2GasProducerNeuronTranslated));

	
	
	assertEquals("g2SynGasProducerNeuron, g2SynGasProducerNeuronTranslated should match", 0.0, g2SynGasProducerNeuron.distance(g2SynGasProducerNeuronTranslated) );
	assertEquals("Distance must be symmetric!",  
			g2SynGasProducerNeuron.distance(g2SynGasProducerNeuronTranslated),
			g2SynGasProducerNeuronTranslated.distance(g2SynGasProducerNeuron));
	
	
	assertEquals("g2GasProducerNeuron, g2GasProducerNeuronActModReceiver should match", 0.2, g2GasProducerNeuronActModReceiver.distance(g2GasProducerNeuron) );
	assertEquals("Distance must be symmetric!",  
			g2GasProducerNeuronActModReceiver.distance(g2GasProducerNeuron),
			g2GasProducerNeuron.distance(g2GasProducerNeuronActModReceiver));
	
	assertEquals("g2SynGasProducerNeuron, g2SynGasProducerNeuronBothModReceiver should match", 0.4, g2SynGasProducerNeuronBothModReceiver.distance(g2SynGasProducerNeuron) );
	assertEquals("Distance must be symmetric!",  
			g2SynGasProducerNeuronBothModReceiver.distance(g2SynGasProducerNeuron),
			g2SynGasProducerNeuron.distance(g2SynGasProducerNeuronBothModReceiver));
	
	assertEquals("g2SynGasProducerNeuron, g2SynGasProducerNeuronPlaModReceiver should match", 0.2, g2SynGasProducerNeuronPlaModReceiver.distance(g2SynGasProducerNeuron) );
	assertEquals("Distance must be symmetric!",  
			g2SynGasProducerNeuronPlaModReceiver.distance(g2SynGasProducerNeuron),
			g2SynGasProducerNeuron.distance(g2SynGasProducerNeuronPlaModReceiver));
	
		
	
	assertEquals("g4GasProducerNeuron, g1GasProducerNeuron should match", 1.0, g4GasProducerNeuron.distance(g1GasProducerNeuron) );
	assertEquals("Distance must be symmetric!",  
				g4GasProducerNeuron.distance(g1GasProducerNeuron),
				g1GasProducerNeuron.distance(g4GasProducerNeuron));	
	
	assertEquals("g4SynGasProducerNeuron, normalNeuron should match", 1.0, g4SynGasProducerNeuron.distance(normalNeuron) );
	assertEquals("Distance must be symmetric!",  
			g4SynGasProducerNeuron.distance(normalNeuron),
			normalNeuron.distance(g4SynGasProducerNeuron));
	
	
	
}




}

