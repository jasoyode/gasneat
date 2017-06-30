package gasNEAT.targetSequence;

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


public class TargetSequenceFitnessWithNoiseEvaluator {

private final static Logger logger = Logger.getLogger( TargetSequenceFitnessWithNoiseEvaluator.class );

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	TargetSequenceFitnessFunction ff = new TargetSequenceFitnessFunction();
	
	Properties props = new Properties();
	props.loadFromResource( args[ 0 ] );
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
	
	
	double[] noiseValues = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

	Chromosome chrom = db.loadChromosome( args[ 1 ], config );
	XmlPersistableChromosome x = new XmlPersistableChromosome(chrom);
	logger.info( x.toXml() );
	
	
	if ( chrom == null )
		throw new IllegalArgumentException( "no chromosome found: " + args[ 1 ] );
	
	List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	chromosomes.add( chrom);
	//show visual of network
	ff.setViewEnabled(false);
	
	
	
	for (double noise: noiseValues) {
		
		System.out.print("Noise level:"+ noise );
		double noiseLevelTotal=0;
		for (int i=0; i < 10; i++) {
			ff.evaluate( chromosomes, noise );
			System.out.print(i+":" + chrom.getFitnessValue()+" "  );
			noiseLevelTotal+= chrom.getFitnessValue();
		}
		System.out.println("  |  avg=" + ( noiseLevelTotal/10  ) );
		
		
	}
	
	
	logger.info( "= Total Fitness = " + chrom.getFitnessValue() );
}
}