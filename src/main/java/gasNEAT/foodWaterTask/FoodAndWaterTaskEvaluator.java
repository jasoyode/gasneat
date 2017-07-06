
package gasNEAT.foodWaterTask;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.persistence.Persistence;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;


public class FoodAndWaterTaskEvaluator {

private static Logger logger = Logger.getLogger( FoodAndWaterTaskEvaluator.class );

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	FoodAndWaterTaskFitnessFunction ff = new FoodAndWaterTaskFitnessFunction();
	Properties props = new Properties();
	props.loadFromResource( args[ 0 ] );
	ff.init( props );
	Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );

	Configuration config = new GasNeatConfiguration(props);

	Chromosome chrom = db.loadChromosome( args[ 1 ], config );
	
	//Load random instead of from file...	
	//SpreadsheetFreeNetworkBuilder randomBuilder = new SpreadsheetFreeNetworkBuilder( idManager );	
	//MUST INCLUDE 7 AS THAT IS THE NUMBER OF INPIT NEURONS!!!!!
	//ChromosomeMaterial material = randomBuilder.buildBaseNetworkAlleles( 7 );
	//Chromosome chrom = new Chromosome(material, material.getPrimaryParentId() );
	/*logger.info(chrom);
	logger.info(chrom.getAlleles());
	//for (Object a: chrom.getAlleles() ) {
		Allele aa = (Allele)a;
		logger.info("allele innovation ID: "  +aa.getInnovationId() );
		logger.info("allele: "  +aa. );
	}
	//*/
	
	if ( chrom == null )
		throw new IllegalArgumentException( "no chromosome found: " + args[ 1 ] );
	ff.enableDisplay();
	ff.evaluate( chrom );
	logger.info( "Fitness = " + chrom.getFitnessValue() );
}
}
