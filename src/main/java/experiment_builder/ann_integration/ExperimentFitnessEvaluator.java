package experiment_builder.ann_integration;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.integration.XmlPersistableChromosome;
import com.anji.neat.NeatConfiguration;
import com.anji.persistence.Persistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;


public class ExperimentFitnessEvaluator {

private final static Logger logger = Logger.getLogger( ExperimentFitnessEvaluator.class );

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	ExperimentCreatorFitnessFunction ff = new ExperimentCreatorFitnessFunction();
	
	Properties props = new Properties();
	props.loadFromResource( args[ 0 ] );
	
	//mustd happen before inti
	ff.setEnableDisplay( true );
	
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
	
	//System.out.println("HERE");
	//System.exit(1);
	

	Chromosome chrom = db.loadChromosome( args[ 1 ], config );
	XmlPersistableChromosome x = new XmlPersistableChromosome(chrom);
	logger.info( x.toXml() );
	
	
	if ( chrom == null )
		throw new IllegalArgumentException( "no chromosome found: " + args[ 1 ] );
	
	List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	chromosomes.add( chrom);
	//show visual of network
	
	ff.evaluate( chromosomes );
	
	logger.info( "= Total Fitness = " + chrom.getFitnessValue() );
}
}