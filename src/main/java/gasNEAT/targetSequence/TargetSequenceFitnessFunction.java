package gasNEAT.targetSequence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.ErrorFunction;
import com.anji.integration.TargetFitnessFunction;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

import gasNEAT.activator.GasNeatActivator;
import gasNEAT.view.ViewConstants;
import gasNEAT.view.networkView.NetworkViewFrame;
import genericEvaluater.DisplayableBulkFitnessFunction;

public class TargetSequenceFitnessFunction implements DisplayableBulkFitnessFunction, Configurable {
	
	private static Logger logger = Logger.getLogger( TargetSequenceFitnessFunction.class );
	private final static String ADJUST_FOR_NETWORK_SIZE_FACTOR_KEY = "fitness.function.adjust.for.network.size.factor";
	private float adjustForNetworkSizeFactor = 0.0f;
	public final static String STIMULI_FILE_NAME_KEY = "stimuli.file";
	public final static String TARGETS_FILE_NAME_KEY = "targets.file";
	private final static String TARGETS_RANGE_KEY = "targets.range";
	private final static String DISPLAY_DELAY_KEY = "display.delay";
	private final static String PRECISION_PENALTY_KEY = "precision.penalty";
	private final static String STIMULI_NOISE_LEVEL = "gasneat.target.sequence.stimuli.noise.level";
	private final static String TARGET_NOISE_LEVEL = "gasneat.target.sequence.target.noise.level";
	
	private final static String FITNESS_POSITIVE_SCORING_MODE_KEY = "fitness.positive.scoring.mode";
	
	private double precisionPenalty = 0.0d;
	private boolean positiveFitnessMode = false;
	
	
	private double stimuliNoise = 0.0d;
	private double targetNoise = 0.0d;
	
	
	private double[][] stimuli;
	private double[][] targets;
	private double targetRange = 0.0d;
	private int maxFitnessValue;
	private ActivatorTranscriber activatorFactory;
	private Randomizer randomizer;
	
	
	private boolean viewEnabled = false;
	
	public void setViewEnabled(boolean v) {
		
		viewEnabled = v;

	}
	
	private int displayDelay = 1000; 
	private final static boolean SUM_OF_SQUARES = false;
	
	//private final static int MAX_FITNESS = 100000;
	private final static int MAX_FITNESS = 1000;

	
	
	/**
	 * See <a href=" {@docRoot}/params.htm" target="anji_params">Parameter Details </a> for
	 * specific property settings.
	 * 
	 * @param newProps configuration parameters
	 */
	public void init( Properties props ) {
		try {
			randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
			activatorFactory = (ActivatorTranscriber) props
					.singletonObjectProperty( ActivatorTranscriber.class );

			stimuli = Properties.loadArrayFromFile( props.getProperty(STIMULI_FILE_NAME_KEY ) );
			targets = Properties.loadArrayFromFile( props.getProperty( TARGETS_FILE_NAME_KEY ) );
			
			positiveFitnessMode = props.getBooleanProperty(FITNESS_POSITIVE_SCORING_MODE_KEY, false );
			
			displayDelay =  props.getIntProperty( DISPLAY_DELAY_KEY, 1000 );
			
			precisionPenalty =  props.getDoubleProperty( PRECISION_PENALTY_KEY, 0.4 );
			
			stimuliNoise = props.getDoubleProperty( STIMULI_NOISE_LEVEL, 0.0 );
			targetNoise = props.getDoubleProperty( TARGET_NOISE_LEVEL, 0.0 );
			
			targetRange = props.getDoubleProperty( TARGETS_RANGE_KEY, 0.0d );
			adjustForNetworkSizeFactor = props.getFloatProperty( ADJUST_FOR_NETWORK_SIZE_FACTOR_KEY,
					0.0f );
			

			if ( stimuli.length == 0 || targets.length == 0 )
				throw new IllegalArgumentException( "require at least 1 training set for stimuli ["
						+ stimuli.length + "] and targets [" + targets.length + "]" );
			if ( stimuli.length != targets.length )
				throw new IllegalArgumentException( "# training sets does not match for stimuli ["
						+ stimuli.length + "] and targets [" + targets.length + "]" );
		}
		catch ( Exception e ) {
			throw new IllegalArgumentException( "invalid properties: " + e.getClass().toString() + ": "
					+ e.getMessage() );
		}
	}

	/**
	 * Subtract <code>responses</code> from targets, sum all differences, subtract from max
	 * fitness, and square result.
	 * 
	 * @param responses output top be compared to targets
	 * @param minResponse
	 * @param maxResponse
	 * @return result of calculation
	 */
	protected int calculateErrorFitness( double[][] responses, double minResponse, double maxResponse ) {
		
		
		double MAX = 500.0;
		double MIN = -500.0;
		
		if ( maxResponse > MAX) {
			maxResponse =  MAX;
		}
		
		if (minResponse < MIN) {
			minResponse = MIN;
		}
		 
		//System.out.println( "maxResponse" + maxResponse  );
		
		
		
		
		double maxSumDiff = ErrorFunction.getInstance().getMaxError(
				getTargets().length * getTargets()[ 0 ].length,
				( maxResponse - minResponse ), SUM_OF_SQUARES );
		
		double maxRawFitnessValue = Math.pow( maxSumDiff, 2 );
		double sumDiff = ErrorFunction.getInstance().calculateError( getTargets(), responses, false );

		
		double rawFitnessValue = Math.pow( maxSumDiff - sumDiff, 2 );
		double skewedFitness = ( rawFitnessValue / maxRawFitnessValue ) * MAX_FITNESS;
		int result = (int) skewedFitness;
		
		/*
		System.out.println( "maxSumDiff " + maxSumDiff );
		System.out.println("maxRawFitnessValue "  +maxRawFitnessValue );
		System.out.println("sumDiff "  + sumDiff);
		System.out.println("rawFitnessValue "  + rawFitnessValue);
		System.out.println("skewedFitness "  + skewedFitness);
		System.out.println("result "  +result );
		*/
		
		
		return result;
	}
	
	
	final public void evaluate( List genotypes, double noise ) {
		//stimuliNoise = noise;
		targetNoise = noise;
		evaluate( genotypes );
		
	}
	
	
	/**
	 * Iterates through chromosomes. For each, transcribe it to an <code>Activator</code> and
	 * present the stimuli to the activator. The stimuli are presented in random order to ensure the
	 * underlying network is not memorizing the sequence of inputs. Calculation of the fitness based
	 * on error is delegated to the subclass. This method adjusts fitness for network size, based on
	 * configuration.
	 * 
	 * @param genotypes <code>List</code> contains <code>Chromosome</code> objects.
	 * @see TargetFitnessFunction#calculateErrorFitness(double[][], double, double)
	 */
	final public void evaluate( List genotypes ) {
		
		Iterator it = genotypes.iterator();
		while ( it.hasNext() ) {
			Chromosome genotype = (Chromosome) it.next();

			try {
				Activator activator = activatorFactory.newActivator( genotype );
				
				NetworkViewFrame frame = null;
				if (viewEnabled) {
					activator = (GasNeatActivator)activatorFactory.newActivator( genotype );
					((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork().setLabeled(true);
					frame = new NetworkViewFrame(((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork(), ((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork().getSimulator() );
					
				}
				HashMap<Long, Double> randomMap = new HashMap<Long, Double>(); 
				
				
				
				//MAKE FLAG FOR NEGATIVE VALUES
				//targetRange 
				for (int i=0; i < stimuli.length; i++) {
					
					for (int j=0; j < stimuli[0].length; j++) {
					
						//for each input (NEGATIVE ARE RANDOM VALUES)
						if (stimuli[i][j] < 0.0   ) {
							long longValue = Double.doubleToLongBits(stimuli[i][j]);
							if (randomMap.containsKey( longValue  )  ) {
								stimuli[i][j] = randomMap.get( longValue );
							} else {
								//put random value between 0 and 1 
								randomMap.put(longValue, randomizer.getRand().nextDouble()  );
								stimuli[i][j] = randomMap.get( longValue  );
							}
						}
					}

					for (int j=0; j < targets[0].length; j++) {
					
						if (targets[i][j] < 0.0   ) {
							long longValue = Double.doubleToLongBits(targets[i][j]);
							if (randomMap.containsKey( longValue  )  ) {
								targets[i][j] = randomMap.get( longValue );
							} else {
								//put random value between 0 and 1 
								randomMap.put(longValue, randomizer.getRand().nextDouble()  );
								targets[i][j] = randomMap.get( longValue  );
							}
						}
					}
				}
				
				
				//for (int i=0; i < stimuli.length; i++) {
				//	logger.debug(stimuli[i][0] +" " +stimuli[i][1] +" " +stimuli[i][2] +" =" +targets[i][0]  );
				//}
				
				double[][] responses = new double[targets.length][targets[0].length];
				
				StringBuilder sb = new StringBuilder();
				for (int i=0; i < stimuli.length; i++) {
					
					//add noise to input
					if ( stimuliNoise > 0 ) {
						for (int j=0; j< stimuli[i].length; j++) {
							stimuli[i][j] += 2 * ( 0.5 - randomizer.getRand().nextDouble() ) * stimuliNoise;
							//DONT CAP 
							//if ( stimuli[i][j] < 0) {
							//	stimuli[i][j] = 0;
							//} else if (stimuli[i][j] > 1) {
							//	stimuli[i][j] = 1;
							//}
						}
					}
					
					responses[i] = activator.next( stimuli[i] );
					//add noise to input
					if ( targetNoise > 0 ) {
						for (int j=0; j< responses[i].length; j++) {
							responses[i][j] += 2 * ( 0.5 - randomizer.getRand().nextDouble() ) * targetNoise;
							//DONT CAP 
							//if ( stimuli[i][j] < 0) {
							//	stimuli[i][j] = 0;
							//} else if (stimuli[i][j] > 1) {
							//	stimuli[i][j] = 1;
							//}
						}
					}
					
					
					
					if ( responses[i][0] > 10) {
						//System.out.println("responses[i][0] " + responses[i][0]  );
						logger.debug( "You must be using non-sigmoid funcs");
						responses[i][0] =  1;
						//System.exit(0);
					}else if (responses[i][0] < 0) {
						//System.out.println("responses[i][0] " + responses[i][0]  );
						logger.debug( "You must be using non-sigmoid funcs");
						responses[i][0] =  0;
						//System.exit(0);
					}
					
					
					if (viewEnabled) {
						try {
							frame.updateNeuralNetworkPanel(ViewConstants.PLAY_STATUS_TEXT, ((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork() );
							Thread.sleep( displayDelay );
							logger.info( "------- timestep: "+i+" ---------------------------" );
							for (int n=0; n < targets[0].length; n++) {
								logger.info(  String.format("Output= %.2f    ~   %.2f =Target   | Difference: %.2f", responses[i][n], targets[i][n], Math.abs( responses[i][n]- targets[i][n]   )       )  );
							}
							
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						sb.append( String.format("Output= %.2f    ~   %.2f =Target   | Difference: %.2f", responses[i][0], targets[i][0], Math.abs( responses[i][0]- targets[i][0]   )       )  );						
					}
					
				}
				if (!viewEnabled) {
					logger.debug( sb );
				}
				
				if ( positiveFitnessMode ) {
					genotype.setFitnessValue( calculatePositiveSumSquares( responses, targets, genotype.size() ) );
				} else {
					genotype.setFitnessValue( calculateSumSquares( responses, targets, genotype.size() ) );
				}
				
				
				
				//OLD CALCULATIONS LESS INTERPRETTABLE
				/*
				genotype.setFitnessValue( calculateErrorFitness( responses, activator.getMinResponse(),
						activator.getMaxResponse() ) //;//
						- (int) ( adjustForNetworkSizeFactor * genotype.size() ) );
				//*/
				
			}
			catch ( TranscriberException e ) {
				logger.warn( "transcriber error: " + e.getMessage() );
				genotype.setFitnessValue( 1 );
			}
		}
	}
	
	
	//calculates a positive value as an integer exactly when
	// the target matches the output within target.range
	private int calculatePositiveSumSquares(double[][] responses, double[][] targets2, int genotypeLength) {
		
		int totalTimeSteps = responses.length;
		int totalOutputNeurons = responses[0].length;
		
		int totalTargetsMatching = 0;
		int totalTargetsMissing = 0;
		
		
		for (int t=0; t < totalTimeSteps; t++) {
			for (int n=0; n < totalOutputNeurons; n++) {
				
				double error = Math.abs( (responses[t][n] - targets2[t][n]) );
								
				if (error < targetRange) {
					error = 0;
					totalTargetsMatching++;
				} else {
					totalTargetsMissing++;
				}
			}
		}
		
		logger.debug("total matches: " + totalTargetsMatching );
		logger.debug("total misses: " + totalTargetsMissing );
		
		double matchRatio = 1.0 * totalTargetsMatching / ( totalTargetsMatching + totalTargetsMissing );
		int fitness = (int)( MAX_FITNESS * matchRatio);
		
		logger.debug("FITNESS:"+ fitness);
		
		fitness -= (int) ( adjustForNetworkSizeFactor * genotypeLength );
		logger.debug("Penalty for size: " + (adjustForNetworkSizeFactor * genotypeLength) );
		
		return fitness;
	}
	

	private int calculateSumSquares(double[][] responses, double[][] targets2, int genotypeLength) {

		double totalError = 0;
		int totalTimeSteps = responses.length;
		int totalOutputNeurons = responses[0].length;
		
		int adjuster = (int)(MAX_FITNESS * precisionPenalty);
		
		for (int t=0; t < totalTimeSteps; t++) {
			for (int n=0; n < totalOutputNeurons; n++) {
				
				double error = Math.abs( (responses[t][n] - targets2[t][n]) );
				
				//System.out.println("response:"+  responses[t][n] );
				//System.out.println("target: "+ targets2[t][n]  );
				//System.out.println( "erorr:" + error);
				
				if (error < targetRange/2) {
					error = 0;
				} else {
					error -= targetRange/2;
				}
				
				//adjuster = 1000;
				if (error > 0) {
					
					//System.out.println( "error: "+adjuster *( error *  error)    );
				}
				
				totalError += adjuster *( error *  error);
				
			}
		}
		
		logger.debug("total error: " + totalError );
		double averageError = totalError / (totalOutputNeurons*totalTimeSteps);
		int fitness = (int)( MAX_FITNESS - averageError);
		
		//System.out.println( averageError );
		
		//this handle int overflow
		if (averageError < 0 || averageError > 1000000) {
			fitness = 0;
		}
		
		//System.out.println( fitness );
		
		fitness -= (int) ( adjustForNetworkSizeFactor * genotypeLength );
		logger.debug("Penalty for size: " + (adjustForNetworkSizeFactor * genotypeLength) );
		
		return fitness;
	}

	@Override
	public int getMaxFitnessValue() {
		return maxFitnessValue;
	}
	
	/**
	 * @return if response is within this range of the target, error is 0
	 */
	protected double getTargetRange() {
		return targetRange;
	}

	/**
	 * @return sequence of stimuli activation patterns
	 */
	protected double[][] getStimuli() {
		return stimuli;
	}

	/**
	 * @return sequence of target values
	 */
	protected double[][] getTargets() {
		return targets;
	}

	@Override
	public void setEnableDisplay(boolean b) {
		// TODO Auto-generated method stub
		
	}
	

}
