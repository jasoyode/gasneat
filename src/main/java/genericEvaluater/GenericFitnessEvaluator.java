package genericEvaluater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.integration.XmlPersistableChromosome;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeatTargetFitnessFunction;
import com.anji.persistence.Persistence;
import com.anji.polebalance.DoublePoleBalanceFitnessFunction;
import com.anji.util.Configurable;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import experiment_builder.ann_integration.ExperimentCreatorFitnessFunction;
import gasNEAT.aplysiaTask.AplysiaFitnessFunction;
import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.targetSequence.TargetSequenceFitnessFunction;


public class GenericFitnessEvaluator {

private final static Logger logger = Logger.getLogger( GenericFitnessEvaluator.class );



/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	
	//EXTRACT FITNESSFUNCTION FROM PROPERTIES
	HashMap<String, DisplayableBulkFitnessFunction> nameToFitnessFunction= new HashMap<String, DisplayableBulkFitnessFunction>();
	
	//APLYSIA
	nameToFitnessFunction.put(
			"gasNEAT.aplysiaTask.AplysiaFitnessFunction",
			(DisplayableBulkFitnessFunction)new AplysiaFitnessFunction() );
	
	//DOUBLE POLE BALANCE
	nameToFitnessFunction.put(
			"com.anji.polebalance.DoublePoleBalanceFitnessFunction",
			(DisplayableBulkFitnessFunction)new DoublePoleBalanceFitnessFunction() );
	
	//TARGET SEQUENCE
	nameToFitnessFunction.put(
			"gasNEAT.targetSequence.TargetSequenceFitnessFunction",
			(DisplayableBulkFitnessFunction)new TargetSequenceFitnessFunction() );
	
	//EXPERIMENT FUNCT
	nameToFitnessFunction.put(
			"experiment_builder.ann_integration.ExperimentCreatorFitnessFunction",
			(DisplayableBulkFitnessFunction)new ExperimentCreatorFitnessFunction() );
	
	
	//XOR and other 1 to 1 mapping tests
		nameToFitnessFunction.put(
				"com.anji.neat.NeatTargetFitnessFunction",
				(DisplayableBulkFitnessFunction)new NeatTargetFitnessFunction() );
	
		
	
	Properties props = new Properties();
	props.loadFromResource( args[ 0 ] );
	Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
	
	String functionName = props.getProperty("fitness_function.class");
	
	
	DisplayableBulkFitnessFunction ff = nameToFitnessFunction.get(functionName) ;
		
	((Configurable)ff).init( props );

	
	
	
	
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
	
	
	
	

	Chromosome chrom = db.loadChromosome( args[ 1 ], config );
	XmlPersistableChromosome x = new XmlPersistableChromosome(chrom);
	logger.info( x.toXml() );
	
	
	if ( chrom == null )
		throw new IllegalArgumentException( "no chromosome found: " + args[ 1 ] );
	
	List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	chromosomes.add( chrom);
	//show visual of network
	ff.setEnableDisplay( true );
	ff.evaluate( chromosomes );
	
	logger.info( "= Total Fitness = " + chrom.getFitnessValue() );
}
}