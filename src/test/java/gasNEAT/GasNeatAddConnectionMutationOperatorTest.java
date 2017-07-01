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
import com.anji.integration.TranscriberException;
import com.anji.neat.ConnectionAllele;
import com.anji.neat.NeatChromosomeUtility;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeuronAllele;
import com.anji.neat.test.NeatChromosomeUtilityTest;
import com.anji.neat.test.TestChromosomeFactory;
import com.anji.nn.ActivationFunctionType;
import com.anji.nn.RecurrencyPolicy;
import com.anji.util.Properties;
import com.anji.util.Reset;

import gasNEAT.builders.GasNeatTranscriber;
import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.geneticOperators.GasNeatAddConnectionMutationOperator;


public class GasNeatAddConnectionMutationOperatorTest extends MutationOperatorTest {

private final static String PROP_FILE_NAME = "properties/gasneat_test.properties";

private final static int POPULATION_SIZE = 100;
private final static int PRE_MUTANTS_SIZE = 60;
private final static short DIM_INPUTS = 4;
private final static short DIM_OUTPUTS = 2;
private final static float MUTATION_RATE = 0.10f;

private RecurrencyPolicy recurrencyPolicy;
private Properties props = new Properties();
private boolean linearInputs = false;

/**
 * ctor
 */
public GasNeatAddConnectionMutationOperatorTest() {
	this( GasNeatAddConnectionMutationOperatorTest.class.toString() );
}

/**
 * ctor
 * 
 * @param name
 */
public GasNeatAddConnectionMutationOperatorTest( String name ) {
	super( name );
}

/**
 * @see org.jgap.test.MutationOperatorTest#initUut()
 */
protected void initUut() throws Exception {
	uut = new GasNeatAddConnectionMutationOperator( MUTATION_RATE, recurrencyPolicy );
	assertEquals( "wrong mutation rate", MUTATION_RATE, uut.getMutationRate(), 0.0f );
}

/**
 * @see org.jgap.test.MutationOperatorTest#initConfig()
 */
protected void initConfig() throws Exception {
	// clear previous stored configs and populations
	Reset reset = new Reset( props );
	reset.setUserInteraction( false );
	reset.reset();

	
	props.loadFromResource( PROP_FILE_NAME );
	
	props.setProperty( NeatConfiguration.STIMULUS_SIZE_KEY, "" + DIM_INPUTS );
	props.setProperty( NeatConfiguration.RESPONSE_SIZE_KEY, "" + DIM_OUTPUTS );
	props.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_FULLY_CONNECTED_KEY, "false" );
	recurrencyPolicy = RecurrencyPolicy.load( props );
	
	if ( linearInputs )
		props.setProperty( NeatConfiguration.INITIAL_TOPOLOGY_ACTIVATION_INPUT_KEY,
				ActivationFunctionType.LINEAR.toString() );

	

	// config
	config = new GasNeatConfiguration( props );
	config.getRandomGenerator().setSeed( 0 );
	config.setFitnessFunction( new DummyFitnessFunction( config.getRandomGenerator() ) );
	config.setPopulationSize( POPULATION_SIZE );
	( (NeatConfiguration) config ).load();
}

/**
 * @see org.jgap.test.MutationOperatorTest#initPreMutants()
 */
protected void initPreMutants() throws Exception {
	preMutants.clear();
	NeatConfiguration neatConfig = (NeatConfiguration) config;
	for ( int i = 0; i < PRE_MUTANTS_SIZE; ++i ) {
		ChromosomeMaterial m = TestChromosomeFactory.newMatureChromosomeMaterial( neatConfig );
		preMutants.add( m );
	}
}

/**
 * @see org.jgap.test.MutationOperatorTest#doTestAfterMutate(java.util.List)
 */
protected void doTestAfterMutate( List mutants ) throws Exception {
	int totalNewConns = 0;
	for ( int i = 0; i < preMutants.size(); ++i ) {
		ChromosomeMaterial preMutant = (ChromosomeMaterial) preMutants.get( i );
		ChromosomeMaterial mutant = (ChromosomeMaterial) mutants.get( i );
		NeatChromosomeUtilityTest.validate( mutant.getAlleles() );

		// neurons unchanged
		SortedMap preMutantNeurons = NeatChromosomeUtility.getNeuronMap( preMutant.getAlleles() );
		SortedMap mutantNeurons = NeatChromosomeUtility.getNeuronMap( mutant.getAlleles() );
		assertEquals( "modified neurons", preMutantNeurons, mutantNeurons );

		// connections only grow
		SortedMap preMutantConns = NeatChromosomeUtility.getConnectionMap( preMutant.getAlleles() );
		SortedMap mutantConns = NeatChromosomeUtility.getConnectionMap( mutant.getAlleles() );
		assertTrue( "lost connections", mutantConns.values().containsAll( preMutantConns.values() ) );

		// new connections
		Set newConnIds = new HashSet();
		newConnIds.addAll( mutantConns.keySet() );
		newConnIds.removeAll( preMutantConns.keySet() );
		totalNewConns += newConnIds.size();

		// recurrency
		if ( RecurrencyPolicy.DISALLOWED.equals( recurrencyPolicy ) ) {
			Chromosome c = new Chromosome( mutant, config.nextChromosomeId() );
			addChromosome( c );
			GasNeatTranscriber tr = new GasNeatTranscriber( recurrencyPolicy );
			try {
				tr.newGasNeatNet( c, props );
			}
			catch ( TranscriberException e ) {
				fail( "mutation should not have created recurrent connection" );
			}
			try {
				tr.newGasNeatNet( c, props );
			}
			catch ( TranscriberException e ) {
				fail( "mutation should not have created recurrent connection" );
			}
		}

		// TODO - expected mutations
	}

	assertTrue( "no mutations", totalNewConns > 0 );
}

/**
 * test
 * 
 * @throws Exception
 */
public void testNoRecurrency() throws Exception {
	recurrencyPolicy = RecurrencyPolicy.DISALLOWED;
	initUut();
	super.testMutationOperator();
}

/**
 * test
 * 
 * @throws Exception
 */
public void testLinearInput() throws Exception {
	linearInputs = true;
	recurrencyPolicy = RecurrencyPolicy.BEST_GUESS;
	initUut();
	initPreMutants();
	super.testMutationOperator();
}

/**
 * test
 */
public void testDefaults() {
	GasNeatAddConnectionMutationOperator oper = new GasNeatAddConnectionMutationOperator();
	assertEquals( "wrong default mutation rate",
			GasNeatAddConnectionMutationOperator.DEFAULT_MUTATE_RATE, oper.getMutationRate(), 0.0f );
}

}
