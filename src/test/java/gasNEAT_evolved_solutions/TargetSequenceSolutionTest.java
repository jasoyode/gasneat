package gasNEAT_evolved_solutions;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.junit.Test;

import com.anji.integration.XmlPersistableChromosome;
import com.anji.neat.NeatConfiguration;
import com.anji.persistence.Persistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import gasNEAT.GasNeatEvolverTest;
import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.targetSequence.TargetSequenceFitnessFunction;
import junit.framework.TestCase;


public class TargetSequenceSolutionTest extends TestCase { 

private final static Logger logger = Logger.getLogger( TargetSequenceSolutionTest.class );


private ArrayList<String> experimentsToRun;
private ArrayList<String> chromosomesToTest;
private ArrayList<Integer> chromosomesFitnessScore;

public TargetSequenceSolutionTest() {
	this( TargetSequenceSolutionTest.class.toString() );
}


public TargetSequenceSolutionTest( String name ) {
	super( name );
}	

/**
 * main test
 */
@Test
public void test() {

	experimentsToRun = new ArrayList<String>();
	chromosomesToTest = new ArrayList<String>();
	chromosomesFitnessScore = new ArrayList<Integer>();
	
	experimentsToRun.add("src/test/java/sanity_tests/gas_act_mod/task_gas_act_mod.properties");
	chromosomesToTest.add("6572");
	chromosomesFitnessScore.add(991);
	
	
	for (int i=0; i< experimentsToRun.size(); i++) {

		try {
			testSolution( experimentsToRun.get(i), chromosomesToTest.get(i),  chromosomesFitnessScore.get(i) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Exception when running test: " +experimentsToRun.get(i) );
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
}





/**
 * main test
 * @throws Exception
 */
public static void testSolution( String exp, String chromosomeID, int fitness ) throws Exception {

	TargetSequenceFitnessFunction ff = new TargetSequenceFitnessFunction();
	Properties props = new Properties();
	
	props.loadFromResource( exp );
	ff.init( props );
	Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
	
	//JUST CHECK THE CONFIG ANN.TYPE AND THIS APPROACH CAN BE RE-USED!!!!
	Configuration config = new DummyConfiguration();
	
	if (props.get("ann.type").equals("anji") ) {
		config = new NeatConfiguration(props);
	} else if (props.get("ann.type").equals("gasneat") ) {
		config = new GasNeatConfiguration(props);
	} else {
		System.out.println("You must specify a gasneat or anji ann");
		System.exit(1);
		
	}

	Chromosome chrom = db.loadChromosome( chromosomeID, config );
	XmlPersistableChromosome x = new XmlPersistableChromosome(chrom);
	logger.info( x.toXml() );
	
	
	if ( chrom == null )
		throw new IllegalArgumentException( "no chromosome found: " + chromosomeID );
	
	List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	chromosomes.add( chrom);
	//show visual of network
	ff.setViewEnabled(false);
	ff.evaluate( chromosomes );
	
	logger.info( "= Total Fitness = " + chrom.getFitnessValue() );
	
	
	assertEquals(true, chrom.getFitnessValue() ==  fitness );
	
	
}
}