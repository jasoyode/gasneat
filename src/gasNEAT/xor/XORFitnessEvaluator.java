package gasNEAT.xor;

import java.util.ArrayList;
import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.integration.XmlPersistableChromosome;
import com.anji.neat.NeatConfiguration;
import com.anji.neat.NeatTargetFitnessFunction;
import com.anji.persistence.Persistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;


public class XORFitnessEvaluator {

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	NeatTargetFitnessFunction ff = new NeatTargetFitnessFunction();
	
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

	Chromosome chrom = db.loadChromosome( args[ 1 ], config );
	XmlPersistableChromosome x = new XmlPersistableChromosome(chrom);
	System.out.println( x.toXml() );
	
	
	if ( chrom == null )
		throw new IllegalArgumentException( "no chromosome found: " + args[ 1 ] );
	//ff.enableDisplay();
	
	List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	chromosomes.add( chrom);
	
	ff.evaluate( chromosomes );
	
	System.out.println( "= Total Fitness = " + chrom.getFitnessValue() );
}
}