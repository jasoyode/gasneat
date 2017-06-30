
package gasNEAT.foodWaterTask;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.imaging.IdentifyImageFitnessFunction;
import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.util.Arrays;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

import gasNEAT.model.GasNeatNeuralNetwork;

public class FoodAndWaterTaskFitnessFunction implements BulkFitnessFunction, Configurable {

//Example
private final static String TRACK_LENGTH_KEY = "polebalance.track.length";
private final static String TIMESTEPS_KEY = "timesteps";
private final static String NUM_TRIALS_KEY = "number.of.trials";
private final static String MAX_FOOD_LEVEL_KEY = "max.food.level";
private final static String MAX_WATER_LEVEL_KEY = "max.water.level";
private final static String REST_THRESHOLD_LEVEL_KEY = "rest.threshold.rate";
private final static String EAT_RATE_KEY = "eat.rate";
private final static String DRINK_RATE_KEY = "drink.rate";
private final static String REST_RATE_KEY = "rest.rate";
//private final static String REST_THRESHOLD_LEVEL_KEY = "max.water.level";
//private final static String REST_THRESHOLD_LEVEL_KEY = "max.water.level";
//private final static String REST_THRESHOLD_LEVEL_KEY = "max.water.level";


private FoodAndWaterTaskDisplay display = null;
private final static int DEFAULT_TIMESTEPS = 1000;
private int maxTimesteps = DEFAULT_TIMESTEPS;
private final static int DEFAULT_NUM_TRIALS = 10;
private int numTrials = DEFAULT_NUM_TRIALS;
private final static Logger logger = Logger.getLogger( FoodAndWaterTaskFitnessFunction.class );
private ActivatorTranscriber factory;
private Random rand;

private int maxWaterLevel;
private int maxFoodLevel;
private double restThresholdLevel;
private double eatRate;
private double drinkRate;
private double restRate;
//private double restThresholdLevel;
//private double restThresholdLevel;
//private double restThresholdLevel;
//private double restThresholdLevel;
//private double restThresholdLevel;

private static final int restCoefficient = 2;


/** logger instance */
private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GasNeatNeuralNetwork.class);

/**
 * @see com.anji.util.Configurable#init(com.anji.util.Properties)
 */
public void init( Properties props ) throws Exception {
	try {
		
		factory = (ActivatorTranscriber) props.singletonObjectProperty( ActivatorTranscriber.class );
		maxTimesteps = props.getIntProperty( TIMESTEPS_KEY, DEFAULT_TIMESTEPS );
		//System.out.println(maxTimesteps);
		//System.exit(0);
		numTrials = props.getIntProperty( NUM_TRIALS_KEY, DEFAULT_NUM_TRIALS );
		
		maxWaterLevel = props.getIntProperty( MAX_WATER_LEVEL_KEY );
		maxFoodLevel = props.getIntProperty( MAX_FOOD_LEVEL_KEY );
		restThresholdLevel = props.getDoubleProperty( REST_THRESHOLD_LEVEL_KEY );
		eatRate = props.getDoubleProperty( REST_THRESHOLD_LEVEL_KEY );
		drinkRate = props.getDoubleProperty( REST_THRESHOLD_LEVEL_KEY );
		restRate = props.getDoubleProperty( REST_RATE_KEY );
		//restThresholdLevel = props.getDoubleProperty( REST_THRESHOLD_LEVEL_KEY );
		//restThresholdLevel = props.getDoubleProperty( REST_THRESHOLD_LEVEL_KEY );
		//restThresholdLevel = props.getDoubleProperty( REST_THRESHOLD_LEVEL_KEY );
		
		
		Randomizer randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
		rand = randomizer.getRand();
	}
	catch ( Exception e ) {
		throw new IllegalArgumentException( "invalid properties: " + e.getClass().toString() + ": "
				+ e.getMessage() );
	}
}

/**
 * @see org.jgap.BulkFitnessFunction#evaluate(java.util.List)
 * @see IdentifyImageFitnessFunction#evaluate(Chromosome)
 */
public void evaluate( List genotypes ) {
	// evaluate each chromosome
	Iterator it = genotypes.iterator();
	while ( it.hasNext() ) {
		Chromosome c = (Chromosome) it.next();
		evaluate( c );
	}
}

/**
 * Evaluate chromosome and set fitness.
 * @param c
 */
public void evaluate( Chromosome c ) {
	try {
		Activator activator = factory.newActivator( c );
		// calculate fitness, sum of multiple trials
		int fitness = 0;
		for ( int i = 0; i < numTrials; i++ )
			fitness += singleTrial( activator );
		c.setFitnessValue( fitness );
	}
	catch ( Throwable e ) {
		logger.warn( "Error evaluating chromosome " + c.toString(), e );
		c.setFitnessValue( 0 );
	}
}

/**
 * @return 4-dimensional array with the following data
 * 
 * [0] - Amount of food left in reserves
 * [1] - Amount of water left in reserves
 * [2] - Amount of food in agent
 * [3] - Amount of water in agent
 */
private double[] newState() {
	double[] state = new double[ 4 ];
	//default static
	if ( true ) {
		state[ 0 ] = 50;
		state[ 1 ] = 50;
		state[ 2 ] = 10;
		state[ 3 ] = 10;
	} else {
	//set to random values or from a list
		
	}
	
	return state;
}

private int singleTrial( Activator activator ) {
	double[] state = newState();
	int fitness = 0;
	logger.debug( "state = " + Arrays.toString( state ) );

	// Run the simulation.
	int currentTimestep = 0;
	
	for ( currentTimestep = 0; currentTimestep < maxTimesteps; currentTimestep++ ) {
		// Network activation values
		double[] networkInput;
		networkInput = new double[ 2 ];
		networkInput[0] =  state[0] / (1.0 * maxFoodLevel);
		networkInput[1] =  state[1] / (1.0 * maxWaterLevel);
		
		// Activate the network.
		double foodOutput = activator.next( networkInput )[ 0 ];
		double waterOutput = activator.next( networkInput )[ 1 ];
		
		//neither outputs fired high enough to allow select an eat or drink action
		if ( foodOutput < restThresholdLevel && waterOutput < restThresholdLevel ) {
			//rest action -1, -1
			performRestAction(state);
		} else if ( foodOutput > waterOutput) {
			//eat action +5, -2
			performEatAction(state);
		} else {
			//drink action + -2, +5
			performDrinkAction(state);
		}

		if ( display != null ) {
			// display.setStatus( Arrays.toString( state ) );
			//display.step( currentTimestep, state[ 0 ], new double[] { state[ 2 ], state[ 4 ] } );
		}

		
		//CHECK TO SEE IF THE SIMULATION SHOULD HALT
		if (state[0] < 0 || state[1] < 0) {
			logger.info( "agent levels" + state[0] +", "+state[1] + "at time step: "+currentTimestep);
			break;
		}
		
	}
	//Conditional for penalizing energy used.
	fitness = currentTimestep;
	
	logger.debug( "trial took " + currentTimestep + " steps" );
	return fitness;
}

private void performEatAction( double[] state ) {
	if (state[2] >= eatRate ) {
		state[0] += eatRate;
		state[1] += -restCoefficient* restRate;
		state[2] += -eatRate;
		//state[3] eating doesn't affect water reserves
	} else { //failure
		state[0] += -restCoefficient* restRate;;
		state[1] += -restCoefficient* restRate;
		//state[2] 
		//state[3] 
	}
}

private void performDrinkAction( double[] state ) {
	
	if (state[3] >= drinkRate) {
		state[0] += -restCoefficient* restRate;
		state[1] += drinkRate;
		//state[2] drinking doesn't affect food reserves
		state[3] += -drinkRate;
	}else {
		state[0] += -restCoefficient* restRate;;
		state[1] += -restCoefficient* restRate;
		//state[2]
		//state[3]
	}
}

private void performRestAction( double[] state ) {
	state[0] += -restRate;
	state[1] += -restRate;
	//state[2] drinking doesn't affect food reserves
	//state[3] eating doesn't affect water reserves
}



/**
 * @see org.jgap.BulkFitnessFunction#getMaxFitnessValue()
 */
public int getMaxFitnessValue() {
	return ( numTrials * maxTimesteps );
}

/**
 * enable GUI display of pole balancing
 */
public void enableDisplay() {
	display = new FoodAndWaterTaskDisplay( 100, new double[] { 0, 0 },
			maxTimesteps );
	display.setVisible( true );
}
}
