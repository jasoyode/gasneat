package gasNEAT.aplysiaTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;
import org.jgap.InvalidConfigurationException;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TargetFitnessFunction;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;
import gasNEAT.activator.GasNeatActivator;
import gasNEAT.genericEvaluater.DisplayableBulkFitnessFunction;
import gasNEAT.view.ViewConstants;
import gasNEAT.view.networkView.NetworkViewFrame;
import lombok.Getter;
import lombok.Setter;

public class AplysiaFitnessFunction implements DisplayableBulkFitnessFunction, Configurable {
	
	
	/*
	 *  APLYSIA SURVIVAL TASK
	 * 
	 *  AGENT MUST AVOID PREDATORS AND MAINTAIN ENERGY
	 *
	 *  INPUT: TOUCH SENSORS, OPTIONAL PAIN SENSOR
	 *  OUTPUT: ESCAPE (WITHDRAWAL) MECHANISM 
	 * 
	 *  FITNESS: MAXIMIZE LIFE
	 *  
	 *  BODY: ENERGY 100
	 *        HEALTH 100
	 *  
	 *  Notes: 
	 *        ALL PARAMETERS TO BE SET VIA PROPERTIES FILE 
	 *         
	 *  Bonus:
	 *         May want a visualizer!
	 *         
	 */
	
	private static Logger logger = Logger.getLogger( AplysiaFitnessFunction.class );
	
	private final static String ADJUST_FOR_NETWORK_SIZE_FACTOR_KEY = "fitness.function.adjust.for.network.size.factor";
	private float adjustForNetworkSizeFactor = 0.0f;
	
	private final static String DISPLAY_DELAY_KEY = "display.delay";
	private final static String STIMULI_NOISE_LEVEL = "gasneat.target.sequence.stimuli.noise.level";
	private final static String TARGET_NOISE_LEVEL = "gasneat.target.sequence.target.noise.level";
	private double sensorNoise = 0.0d;
	private int maxFitnessValue;
	private ActivatorTranscriber activatorFactory;
	private Randomizer randomizer;
	private @Getter @Setter boolean enableDisplay;
	
	//Aplysia experiment set properties
	//*
	private final static String UPPER_REAL_ATTACK_SIGNAL_BOUND_KEY = "gasneat.aplysia.upperRealAttackSignalBound";
	private final static String LOWER_REAL_ATTACK_SIGNAL_BOUND_KEY = "gasneat.aplysia.lowerRealAttackSignalBound";
	private final static String UPPER_FAKE_ATTACK_SIGNAL_BOUND_KEY = "gasneat.aplysia.upperFakeAttackSignalBound";
	private final static String LOWER_FAKE_ATTACK_SIGNAL_BOUND_KEY = "gasneat.aplysia.lowerFakeAttackSignalBound";
	private final static String GAUSSIAN_DISTRIBUTION_OF_NOISE_KEY = "gasneat.aplysia.gaussianDistributionOfNoise";
	private final static String RANDOM_EVENTS_KEY = "gasneat.aplysia.randomEvents";
	
	//
	private final static String REAL_ATTACK_DYNAMIC_SENSOR_KEY = "gasneat.aplysia.real.attack.dynamic.sensor";
	private final static String REAL_ATTACK_MULTI_SENSOR_DELAY_KEY= "gasneat.aplysia.real.attack.multi.sensor.delay";
	private final static String REAL_ATTACK_DAMAGE_DELAY_KEY= "gasneat.aplysia.real.attack.damage.delay";
	
	
	private final static String REAL_ATTACK_RATE_KEY = "gasneat.aplysia.realAttackRate";
	private final static String FAKE_ATTACK_RATE_KEY = "gasneat.aplysia.fakeAttackRate";
	
	private final static String ESCAPE_DISTANCE_KEY = "gasneat.aplysia.escapeDistance";
	private final static String PREDATOR_SPEED_KEY = "gasneat.aplysia.predatorSpeed";
	private final static String EVASION_SPEED_KEY = "gasneat.aplysia.evasionSpeed";
	
	private final static String PREDATOR_DAMAGE_KEY = "gasneat.aplysia.predatorDamage";
	private final static String PAIN_DELAY_KEY = "gasneat.aplysia.painDelay";
	private final static String PAIN_ACTIVATION_KEY = "gasneat.aplysia.painActivation";
	private final static String APLYSIA_HEALTH_KEY = "gasneat.aplysia.aplysiaHealth";
	private final static String APLYSIA_ENERGY_KEY = "gasneat.aplysia.aplysiaEnergy";
	
	private final static String NUMBER_SENSORS_KEY = "gasneat.aplysia.numberSensors";
	private final static String BASELINE_SIGNAL_KEY = "gasneat.aplysia.baselineSignal";
	private final static String BASELINE_SIGNAL_NOISE_KEY = "gasneat.aplysia.baselineSignalNoise";
	private final static String ENERGY_COST_OF_ESCAPE_ACTIVATION_KEY = "gasneat.aplysia.energyCostOfEscapeActivation";
	//*/
	private final static String FITNESS_TARGET_KEY = "fitness.target";
	
	
	//////NEW
	private final static String ENERGY_REGEN_RATE_KEY = "gasneat.aplysia.energy.regeneration.rate";
	private final static String REST_THRESHHOLD_KEY = "gasneat.aplysia.rest.threshhold";
	private final static String PREDATOR_SPEED_UPPER_KEY = "gasneat.aplysia.predator.speed.upper";
	private final static String PREDATOR_SPEED_LOWER_KEY = "gasneat.aplysia.predator.speed.lower";
	private final static String MULTIMODAL_DISTANCE_KEY = "gasneat.aplysia.multimodal.distance";
	private final static String DAMAGE_DISTANCE_KEY = "gasneat.aplysia.damage.distance";
	private final static String FAKE_DURATION_UPPER_KEY = "gasneat.aplysia.fake.attack.duration.upper";
	private final static String FAKE_DURATION_LOWER_KEY = "gasneat.aplysia.fake.attack.duration.lower";

	private final static String MAXIMUM_LIFETIME_KEY = "gasneat.aplysia.maximum.lifetime";
	
	private final static String PREDATOR_ACC_UPPER_KEY = "gasneat.aplysia.predator.acc.upper";
	private final static String PREDATOR_ACC_LOWER_KEY = "gasneat.aplysia.predator.acc.lower";
	private final static String EVASION_ACC_UPPER_KEY = "gasneat.aplysia.evasionAccUpper";
	
	
	private final static String HABITUATION_ANALYSIS_MODE_KEY = "gasneat.aplysia.habituation.mode";
	
	
	//SENSORY INPUT CONTROLS
	private double upperRealAttackSignalBound;// = 1.0;
	private double lowerRealAttackSignalBound;// = 0.5;
	private double upperFakeAttackSignalBound;// = 0.25;
	private double lowerFakeAttackSignalBound;// = 0.75;
	private boolean gaussianDistributionOfNoise;// = true;
	//true means random, false means deterministic
	private boolean randomEvents;// = false;
	//must be between 0.0 and 1.0
	private double realAttackRate;// = 0.01;
	private double fakeAttackRate;//  = 0.1;
	private int fakeAttackDuration;// = 5;
	private double escapeDistance;//  = 10.0;
	
	//starting out predator is at edge of detection
	private double predatorProximity = escapeDistance;
	//the reflex needs to be strong and sudden, if it is not, the predator can attack
	private double predatorSpeed;
	private double predatorAcc;
	
	//one full activation is enough to escape
	//with 0.5 activation, will take a long time to escape
	private double startingEvasionSpeed;
	private double evasionSpeed;
	
	//four consecutive attacks without escape should kill organism
	//maybe this should be higher!?
	private double predatorDamage;// = 25.0;
	
	//APLSYIA ANN SETTINGS
	//boolean painSensor = false;
	//use this so that network cannot ONLY respond to pain
	//pain occurs after attack has started...
	//arguably the tissue damage releases signals that take time to reach system
	//NEGATIVE MEANS THERE IS NOT PAIN SENSOR
	private int painDelay;// = -1;
	private double painActivation;// = 0.75;
	private double aplysiaHealth;// = 100;
	private double aplysiaEnergy;// = 100;
	
	private double aplysiaMaxHealth;// = 100;
	private double aplysiaMaxEnergy;// = 100;
	
	//touch sensors everywhere across body
	private int numberSensors;// = 2;
	private double baselineSignal;// = 0.1;
	private double baselineSignalNoise;// = 0.1;

	//weak touches should result in more habituation
	//strong attacks indicate
	private double energyCostOfEscapeActivation;// = 1.0;
	private int timeFitness = 0;
	
	//only one sensor is bumped in a false alarm
	private int singleFakeAttackSensor = -10;
	
	private int fakeAttackTimeRemaining = 0;
	//how long has real attack been occurring?
	private int realAttackDuration = 0; 
	//no timing, its based on escaping
	private int firstRealAttackSensor = -10;
	private int secondRealAttackSensor = -10;
	
	
	private boolean realAttackDynamicSensor;
	private int realAttackDamageDelay;
	private int realAttackMultiSensorDelay;
		
	private int maxFitnessTime;
	
	/////////new
	
	private double regenerationRate;
	private double restThreshhold;
	private double predatorSpeedUpper;
	private double predatorSpeedLower;
	private double multimodalDistance;
	private double damageDistance;
	private int fakeAttackDurationUpper;
	private int fakeAttackDurationLower;
	
	private double predatorAccUpper;
	private double predatorAccLower;
	
	private double evasionAccUpper;
	
	private boolean habituationAnalysisMode;

	//////
	

	private int displayDelay = 1000; 
	
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

			
			displayDelay =  props.getIntProperty( DISPLAY_DELAY_KEY, 1000 );
			
			adjustForNetworkSizeFactor = props.getFloatProperty( ADJUST_FOR_NETWORK_SIZE_FACTOR_KEY, 0.0f );
			
			//APLYSIA
			lowerRealAttackSignalBound = props.getFloatProperty( LOWER_REAL_ATTACK_SIGNAL_BOUND_KEY );
			upperRealAttackSignalBound = props.getFloatProperty( UPPER_REAL_ATTACK_SIGNAL_BOUND_KEY );
			
			upperFakeAttackSignalBound = props.getFloatProperty( UPPER_FAKE_ATTACK_SIGNAL_BOUND_KEY );
			lowerFakeAttackSignalBound = props.getFloatProperty( LOWER_FAKE_ATTACK_SIGNAL_BOUND_KEY );
			
			gaussianDistributionOfNoise = props.getBooleanProperty( GAUSSIAN_DISTRIBUTION_OF_NOISE_KEY );
			randomEvents = props.getBooleanProperty( RANDOM_EVENTS_KEY );
			
			//characteristics of the attack
			realAttackDynamicSensor = props.getBooleanProperty( REAL_ATTACK_DYNAMIC_SENSOR_KEY );
			realAttackMultiSensorDelay =  props.getIntProperty( REAL_ATTACK_MULTI_SENSOR_DELAY_KEY );
			
			//damageDelay
			realAttackDamageDelay = props.getIntProperty( REAL_ATTACK_DAMAGE_DELAY_KEY );
			
			
			realAttackRate = props.getFloatProperty( REAL_ATTACK_RATE_KEY );
			fakeAttackRate = props.getFloatProperty( FAKE_ATTACK_RATE_KEY );
			escapeDistance = props.getFloatProperty( ESCAPE_DISTANCE_KEY );
			//predatorSpeed = props.getFloatProperty( PREDATOR_SPEED_KEY );
			startingEvasionSpeed = props.getFloatProperty( EVASION_SPEED_KEY );
			predatorDamage = props.getFloatProperty( PREDATOR_DAMAGE_KEY );
			
			painDelay= props.getIntProperty( PAIN_DELAY_KEY );
			painActivation= props.getFloatProperty( PAIN_ACTIVATION_KEY );
			aplysiaMaxHealth= props.getFloatProperty( APLYSIA_HEALTH_KEY );
			aplysiaMaxEnergy= props.getFloatProperty( APLYSIA_ENERGY_KEY );
			numberSensors= props.getIntProperty( NUMBER_SENSORS_KEY );
			baselineSignal= props.getFloatProperty( BASELINE_SIGNAL_KEY );
			baselineSignalNoise= props.getFloatProperty( BASELINE_SIGNAL_NOISE_KEY );
			energyCostOfEscapeActivation= props.getFloatProperty( ENERGY_COST_OF_ESCAPE_ACTIVATION_KEY );
			maxFitnessTime = props.getIntProperty( MAXIMUM_LIFETIME_KEY );
			
			//NEW
			regenerationRate= props.getFloatProperty(ENERGY_REGEN_RATE_KEY);
			restThreshhold= props.getFloatProperty(REST_THRESHHOLD_KEY);
			predatorSpeedUpper= props.getFloatProperty(PREDATOR_SPEED_UPPER_KEY);
			predatorSpeedLower= props.getFloatProperty(PREDATOR_SPEED_LOWER_KEY);
			multimodalDistance= props.getFloatProperty(MULTIMODAL_DISTANCE_KEY);
			damageDistance= props.getFloatProperty(DAMAGE_DISTANCE_KEY);
			fakeAttackDurationUpper= props.getIntProperty(FAKE_DURATION_UPPER_KEY);
			fakeAttackDurationLower= props.getIntProperty(FAKE_DURATION_LOWER_KEY);
			
			predatorAccLower =  props.getFloatProperty(PREDATOR_ACC_UPPER_KEY);
			predatorAccUpper = props.getFloatProperty(PREDATOR_ACC_LOWER_KEY);
			
			evasionAccUpper = props.getFloatProperty(EVASION_ACC_UPPER_KEY);
			
			habituationAnalysisMode = props.getBooleanProperty(HABITUATION_ANALYSIS_MODE_KEY, false);
			
			
			if (realAttackMultiSensorDelay >= 0 && numberSensors <= 1) {
				try {
					throw new InvalidConfigurationException("Cannot have multisensor attack with a single sensor!");
				} catch (Exception e) { 
					e.printStackTrace();
					System.exit(1);
				}
				
			}
			
			if ( painDelay >= 0) {
				
				if (numberSensors + 1 !=  props.getIntProperty( "stimulus.size" ) ) {
					try {
						throw new InvalidConfigurationException("Stimulus size must equal sensors + pain (1)");
					} catch (Exception e) { 
						e.printStackTrace();
						System.exit(1);
					}
				}
				
			} else {
				
				if (numberSensors  != props.getIntProperty( "stimulus.size" ) ) {
					try {
						throw new InvalidConfigurationException("Stimulus size must equal sensors");
					} catch (Exception e) { 
						e.printStackTrace();
						System.exit(1);
					}
				}
				
			}
			
			
			
			
			

		}
		catch ( Exception e ) {
			throw new IllegalArgumentException( "invalid properties: " + e.getClass().toString() + ": "
					+ e.getMessage() );
		}
	}
	
	
	
	
	public void habituationAnalysis(Activator activator ) {
		
		//touch sensors for agent
		double[] sensors;
		
		if (painDelay >= 0) {
			//extra sensor for pain
			sensors = new double[numberSensors + 1];
		}else {
			sensors = new double[numberSensors];
		}

		
		int rounds = 10;
		int timePerRound = 10;
		int duration = 5;
		int timeToMax = 2;
		
		int iterations = (1 + sensors.length );
		
		double[] motors = new double[1];
		double[][] motorData = new double[rounds*timePerRound * iterations][1];
		
		
		
		double baseline = 0.1;
				
		double[] testingLevels = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };
		
		
		for (int k=0; k < sensors.length; k++) {
			sensors[k] = baseline;
		}
		
		for (int iter=0; iter < iterations-1; iter++) {
			
			for (int j=0; j< testingLevels.length; j++ ) {
				for (int t=0; t< timePerRound; t++) {
					if ( t < duration) {
						if (t< timeToMax) {
							sensors[iter] = testingLevels[j]* (1+t) / timeToMax;
						} else {
							sensors[iter] = testingLevels[j];							
						}
					} else {
						sensors[iter] = baseline;
					}
					
					
					motors = activator.next( sensors );
					//copy value
					motorData[iter*j*rounds + t][0] = motors[0];
				}
			}
		}
		
		
		
		
		for (int j=0; j< testingLevels.length; j++ ) {
			
			for (int t=0; t< rounds; t++) {
				if ( t < duration) {
					for (int k=0; k < sensors.length; k++) {
						if (k< timeToMax) {
							sensors[k] = testingLevels[j]* (1+k) / timeToMax;
						} else {
							sensors[k] = testingLevels[j];							
						}
					}
				}else {
					for (int k=0; k < sensors.length; k++) {
						sensors[k] = baseline;
					}
				}
				motors = activator.next( sensors );
				//copy value
				motorData[(iterations-1) * rounds*timePerRound+   j*rounds + t][0] = motors[0];
			}
		}
		
		

		
		
		
		
		
		for (int s=0; s< iterations; s++) {
			System.out.println("---------------------------------------------------");
			if (s+1 == iterations) {
				System.out.println("Test w/ALL :");
			} else {
				System.out.println("Test w/sensor"+s+":");				
			}
			
			for (int r=0; r< rounds; r++) {
				System.out.print("Test level "+ testingLevels[r]+": " );
				for (int t=0; t< timePerRound; t++) {
					System.out.print( round( motorData[ s*rounds*timePerRound + r*timePerRound + t  ][0], 3 ) +" \t" );
				}
				System.out.println("");				
			}
			
			
		}
		
		
		
		
		
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
			
			
			
			
			
			//make deterministic
			//NEED TO MAKE SURE VALUES ARE SET DETERMINSTICALLY HERE!
			singleFakeAttackSensor=-10;
			firstRealAttackSensor=-10;
			fakeAttackTimeRemaining=0;
			realAttackDuration=0;
			evasionSpeed = startingEvasionSpeed;
			
			
			try {
				Activator activator = activatorFactory.newActivator( genotype );
				
				if ( habituationAnalysisMode ) {
					System.out.println("HABITUATION MODE ENABLED!");
					habituationAnalysis( activator);
					System.out.println("HABITUATION ANALYSIS COMPLETE... EXITING NOW");
					System.exit(1);
				}
				
				
				NetworkViewFrame frame = null;
				if (enableDisplay) {
					activator = (GasNeatActivator)activatorFactory.newActivator( genotype );
					((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork().setLabeled(true);
					frame = new NetworkViewFrame(((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork(), ((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork().getSimulator() );
				}

				
				predatorProximity = escapeDistance;
				
				//NON PROPS- JUST USED FOR SIMULATION
				ArrayList<Integer> realAttackTimes = new ArrayList<Integer> ();
				ArrayList<Integer> fakeAttackTimes = new ArrayList<Integer> ();

				//turn rates into intervals
				int realAttackInterval = (int) (1/realAttackRate );
				int fakeAttackInterval = (int) (1/fakeAttackRate );
				
				//add attack close to beginning
				realAttackTimes.add( 5 );
				
				for (int i=1; i < 10; i++) {
					if (!randomEvents) {
						//off set increasingly far apart so its not too predictable in non-random version
						realAttackTimes.add( realAttackInterval + i );
						fakeAttackTimes.add( fakeAttackInterval + i );	
					} else {
						//if random events we want this to be even noisier
						realAttackTimes.add( realAttackInterval + randomizer.getRand().nextInt( realAttackInterval ) );
						fakeAttackTimes.add( fakeAttackInterval + randomizer.getRand().nextInt( fakeAttackInterval ) );
					}
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug( realAttackTimes  );
					logger.debug( fakeAttackTimes  );
				}
				
				//pull from head, push to end for re-use
				int realAttackCountdown = realAttackTimes.remove(0);
				realAttackTimes.add( realAttackCountdown );
				int fakeAttackCountdown = fakeAttackTimes.remove(0);
				fakeAttackTimes.add( fakeAttackCountdown );
				
				//touch sensors for agent
				double[] sensors;
				
				if (painDelay >= 0) {
					//extra sensor for pain
					sensors = new double[numberSensors + 1];
				}else {
					sensors = new double[numberSensors];
				}
				
				//could later work on setting up CPG for locomotive escape
				//alternating flapping tail one way and then the other
				double[] motors = new double[1];
				
				//start of experiment set everything
				timeFitness = 0;
				aplysiaHealth = aplysiaMaxHealth;
				aplysiaEnergy = aplysiaMaxEnergy;
				int durationSinceDamageFirstOccurred = -1;
				
				double[] fakeAttackSensorValues = new double[0];
				
				
				//Aplysia dies when health or energy drop to zero
				while (aplysiaHealth > 0 && aplysiaEnergy > 0 && timeFitness <= maxFitnessTime ) {
					
					
					
					
					//ADD REGULAR NOISY DATA TO SENSORS
					timeFitness++;
					
					//fill in random noise into sensors
					//set each touch sensor to random value according to params
					//only up to numSensors, pain should not be detectable!
					for (int i=0; i< numberSensors; i++) {
						//baseline + noise * random ( 0.0-1.0 )
						sensors[i] = baselineSignal + (baselineSignalNoise * randomizer.getRand().nextDouble() );
					}
					
				
					
					// SETUP /  START FAKE ATTACK
					//
					// could be setup as event driven instead, but this is faster for now
					//
					//When countdown reaches zero we need to start fake attack
					if ( fakeAttackCountdown <= 0 ) {
						
						
						//TRIGGER FAKE ATTACK and start countdown until it is over!
						//must add 1 so that upper is reachable, otherwise 0,1 always returns 0
						fakeAttackTimeRemaining = fakeAttackDurationLower 
								+ randomizer.getRand().nextInt( 1 + fakeAttackDurationUpper - fakeAttackDurationLower );
						
						//array of sensors values equal to the length of the fakeattacktime
						fakeAttackSensorValues = new double[fakeAttackTimeRemaining];
						
						// lower + 0-1*(range)
						//double predatorSpeed = predatorSpeedLower + randomizer.getRand().nextDouble() * (predatorSpeedUpper -predatorSpeedLower);
						
						//random slope based on predator speed
						double fakeAttackSpeed = predatorSpeedLower + randomizer.getRand().nextDouble() * (predatorSpeedUpper -predatorSpeedLower);
						
						//if escape distance is 10, then if speed is 4
						// slope = 4/10  0.4
						double slope = fakeAttackSpeed / escapeDistance;
						double maxFakeSensorValue = getFakeAttackSensorSignal();
						
						//System.out.println("fakeAttackSpeed " +fakeAttackSpeed );
						//System.out.println("slope " + slope);
						//System.out.println("maxFakeSensorValue " + maxFakeSensorValue);
						//System.out.println("fakeAttackTimeRemaining " +fakeAttackTimeRemaining );
						
						for (int i=0; i < fakeAttackTimeRemaining; i++) {
							int j=i+1;
							//before maximum (maxFakeSensorValue) is reached
							if ( slope *j <= maxFakeSensorValue ) {
								fakeAttackSensorValues[i] = slope * j;
							//at the end have the value trail off
							} else if ( slope * (fakeAttackTimeRemaining-j) <= maxFakeSensorValue  ) {
								fakeAttackSensorValues[i] = slope * (fakeAttackTimeRemaining-j);
							//in between the value should be maxed out
							} else {
								fakeAttackSensorValues[i] = maxFakeSensorValue;
							}
							
						}
						
						/*
						//print to verify
						System.out.println("FAKE ATTACK STARTED WITH SIGNALS: ");
						for (int i=0; i < fakeAttackTimeRemaining; i++) {
							System.out.println("["+i+"] "+ fakeAttackSensorValues[i] );
						}
						*/
						
						//pick random sensor for first time, then rotate so that all are hit
						if( singleFakeAttackSensor == -10 ) {
							singleFakeAttackSensor = timeFitness  % numberSensors;
						} else {
							singleFakeAttackSensor = (singleFakeAttackSensor + 1) % numberSensors;
						}
						
						
						fakeAttackCountdown = fakeAttackTimes.remove(0);
						fakeAttackTimes.add( fakeAttackCountdown );
						if (logger.isDebugEnabled()) {
							logger.debug( "FAKE ATTACK AT:" + timeFitness   );
						}
						
						
						
						//System.err.println("singleFakeAttackSensor " + singleFakeAttackSensor);
						///System.err.println("fakeAttackSensorValues.length " + fakeAttackSensorValues.length);
						//System.err.println("fakeAttackTimeRemaining " + fakeAttackTimeRemaining);
						//System.err.println(" " + );
						//System.err.println(" " + );
						
						
						
						
						//UPDATE SENSOR DATA TO INCLUDE FAKE ATTACK SENSING
						sensors[ singleFakeAttackSensor ] = 
								// 8 -8 = 0
								fakeAttackSensorValues[ fakeAttackSensorValues.length - fakeAttackTimeRemaining ];
						
						////////////////////
					} else {
						
						
						
						if ( fakeAttackTimeRemaining > 0 ) {
							
							if (fakeAttackSensorValues.length == 0 ) {
								System.err.println("NO cannot have initialized fakesensordata!");
								System.exit(1);
							}
							
							//when fakeattacktimeremainig = 1, then will hit length-1
							sensors[ singleFakeAttackSensor ] = 
									fakeAttackSensorValues[ fakeAttackSensorValues.length - fakeAttackTimeRemaining ];
							
							//update this AFTER so we dont go out of bounds
							fakeAttackTimeRemaining--;
							
						} else {
							fakeAttackCountdown--;
							
						}
						
					}
					
					//could happen same time as fake attack
					if ( realAttackCountdown <= 0 ) {
						
						//TRIGGER ATTACK!
						realAttackCountdown = realAttackTimes.remove(0);
						realAttackTimes.add( realAttackCountdown );
						if (logger.isDebugEnabled()) {
							logger.debug( "ATTACK AT:" + timeFitness   );
						}
						//UPDATE SENSOR DATA TO INCLUDE REAL ATTACK SENSING
						
						if (enableDisplay ) {
							System.out.println("PREDATOR ATTACK STARTS");
						}
						
						//CONTINUE HERE
						/////NEED TO HAVE PREDATOR ACCELERATE
						//THIS WILL HUGELY REWARD EARLY DETECTION
						//NEED TO KEEP TRACK OF DAMAGE AND DELAY PAIN SIGNAL BASED ON IT
						//DAMAGE SHOULD HAPPEN AT DISTANCE
						//MULTIMODAL SHOULD HAPPEN AT DISTANCE
						evasionSpeed = startingEvasionSpeed;
						
						
						predatorSpeed = predatorSpeedLower 
								+ randomizer.getRand().nextDouble() 
								* (predatorSpeedUpper -predatorSpeedLower);
						
						//NEED TO ADD THIS
						predatorAcc = predatorAccLower 
								+ randomizer.getRand().nextDouble() 
								* (predatorAccUpper -predatorAccLower);
						
						
						//starting at boundary, predator moves in to attack
						predatorProximity = escapeDistance - predatorSpeed;
						
						//starting new attack so duration resets
						realAttackDuration = 0;
						
						//pick random sensor for attack signal to come from
						//after the first time though, incrementally move through sensors
						//making sure attack happen on each sensor
						if (firstRealAttackSensor == -10) {
							
							//if stochastic events then randomize starting real sensor
							if (randomEvents) {
								firstRealAttackSensor = randomizer.getRand().nextInt( numberSensors );
							} else {
								firstRealAttackSensor = timeFitness % numberSensors;	
							}
							
						} else {
							firstRealAttackSensor = (firstRealAttackSensor + 1) % numberSensors;
						}
						
						
						
						//   predatorProximity = 8
						//   escapeDistance = 10
						//   1.0  - ( 8 / 10)
						//   0.2
						//   don't go above 1 even if negative proximity
						double attackSignal = Math.min( 
								1.0 - (predatorProximity/escapeDistance), 
								1.0);
						
						sensors[firstRealAttackSensor] = attackSignal;
						
						
						
						/////////////////
					} else {
						
						//System.err.println( "predatorProximity" + predatorProximity  );
						//System.err.println( "escapeDistance" + escapeDistance  );
						// sensitization
						//  - multiple different sensors firing 
						//  - drastically different strong signals are strong indication of attack 
						//  - innocuous bumping is likely to stay in the same sensor
						// the attack takes a while before damage is actually incurred
						
						//attack ongoing condition
						if ( predatorProximity < escapeDistance) {
							//attack has been going on longer period of time
							durationSinceDamageFirstOccurred = -1;
							realAttackDuration++;
							
							//randomize the attack sensors and increase number of sensors...
							//singleRealAttackSensor
							
							if (randomEvents) {
								
								if (realAttackDynamicSensor) {
									firstRealAttackSensor = randomizer.getRand().nextInt( numberSensors );
									secondRealAttackSensor = randomizer.getRand().nextInt( numberSensors );
									//pick random once, but otherwise increment
									if (secondRealAttackSensor == firstRealAttackSensor) {
										secondRealAttackSensor = (firstRealAttackSensor + 1) % numberSensors;
									}
								} 
							} else {
								
								if (realAttackDynamicSensor) {
									//just rotate sensors forward
									firstRealAttackSensor = (firstRealAttackSensor + timeFitness) % numberSensors;
									secondRealAttackSensor = (firstRealAttackSensor + 1) % numberSensors;
								}
							}
							
							
							//if damage has occurred long enough ago, then pain sensor activates the remainder of attack
							if (painDelay >= 0 && durationSinceDamageFirstOccurred >= painDelay ) {
								sensors[ numberSensors +1 ] = painActivation;
							}
							
							//predator accelerates each time step
							predatorSpeed += predatorAcc;
							
							//predator moves closer and potentially damages Aplysia
							predatorProximity += evasionSpeed/2 - predatorSpeed/2;
							
							//sensors[firstRealAttackSensor] = getRealAttackSensorSignal();
							
							double attackSignal = Math.min( 
									1.0 - (predatorProximity/escapeDistance), 
									1.0);
							
							sensors[firstRealAttackSensor] = attackSignal;
							if ( predatorProximity <= multimodalDistance ) {
								//ADD SENSITIZATION WITH SECOND SIGNALS CO-OCCURING
								sensors[secondRealAttackSensor] = attackSignal;
							}
							
							
							//if in range....
							if ( predatorProximity <= damageDistance 
									
									//OR IF THE ATTACK GOES ON FOR TOO LONG!
									//THIS PREVENTS EQUILIBRIUM PARALLEL ACCELERATION OF
									//PREDATOR AND PREY
									//( ALTHOUGH THIS WOULD CAUSE ENERGY TO RUN OUT)
									|| realAttackDuration > realAttackDamageDelay
									
									) {
								aplysiaHealth -= predatorDamage;
								if (durationSinceDamageFirstOccurred < 0) {
									durationSinceDamageFirstOccurred = 0;
								}
							}
							
							if (durationSinceDamageFirstOccurred >= 0) {
								durationSinceDamageFirstOccurred++;
							}
							
							
							
							
						} else {
							
							//if attack not occurring, then countdown to the next real attack
							realAttackCountdown--;
						}
					}
					
					//SHOW ACTUAL OUTPUTS
					if (logger.isDebugEnabled()) {
						logger.debug("len: " + motors.length );
						logger.debug("M: ");
						for (int i=0; i< motors.length; i++) {
							logger.debug( motors[i]+ " " );
						}
						logger.debug("len: " + sensors.length );
						logger.debug("S: ");
						for (int i=0; i< sensors.length; i++) {
							logger.debug( sensors[i]+ " " );
						}
						logger.debug("END");
						
					}
					//Entire ANN abstraction
					motors = activator.next( sensors );
					
					double escapeActivation = motors[0];
					
					
					boolean needsToEscape = predatorProximity < escapeDistance; 
					
					//if we have a linear or tanh activation
					//then we need to zero it to prevent moving towards predator and gaining energy
					if (escapeActivation < 0) {
						
						logger.debug("CANT HAVE NEGATIVE ESCAPE! SET TO ZERO");
						escapeActivation = 0;
					}
					
					//escape acceleration to escapeSpeed
					//MUST be setup to make high values early more valuable...
					//currently is
					evasionSpeed += escapeActivation * evasionAccUpper;
					
					
					if (needsToEscape) {
						predatorProximity += evasionSpeed/2 - predatorSpeed/2;
					}
					
					//it takes energy to escape which lowers energy and lifespan
					if (escapeActivation > restThreshhold) {
						aplysiaEnergy -= energyCostOfEscapeActivation * escapeActivation;
					} else {
						//if not escaping, then recovering health instead
						aplysiaEnergy += regenerationRate;
						if (aplysiaEnergy > aplysiaMaxEnergy) {
							aplysiaEnergy = aplysiaMaxEnergy;
						}
					}

					
					//end of timestep, agent has been damaged and moved
					
					if ( enableDisplay  ) {
						
						frame.updateNeuralNetworkPanel(ViewConstants.PLAY_STATUS_TEXT, ((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork() );
						
						for (int j=0; j< sensors.length; j++) {
							
							System.out.print(" [ "+round( sensors[j]) +  "] "  );
						}
						System.out.println("");
						System.out.println("");
						
						
						//show agent, predator via text output
						System.out.println( "Health: " + round(aplysiaHealth) + 
								"\tEnergy: " + round(aplysiaEnergy) + 
								"\tPredProx: " + round(predatorProximity) + 
								"\tFalsAlrmRm: " + fakeAttackTimeRemaining + 
								"\tEscp: " + round(escapeActivation) +
								"\tEscSpeed: " + round(evasionSpeed) +
								"\tPredSpeed: " + round(predatorSpeed) 
								//"\tFakeS: " + round(sensors[ singleFakeAttackSensor ]) +
								//"\tRealS: " + round(sensors[ firstRealAttackSensor ])
								
								);
						
						System.out.print("A*");
						for (int k=0; k< 10*(predatorProximity/escapeDistance); k++ ) {
							System.out.print("-----"+k+"-----");
						}
						System.out.println("P");
						
						
						if (needsToEscape && predatorProximity >= escapeDistance) {
							System.out.println("ESCAPE OCCURRED!");
							evasionSpeed = startingEvasionSpeed;
							
							
						}
						System.out.println("-----------------TIME:"+timeFitness+"-----------------------------------------");
						
						try {
							
							
							Thread.sleep( displayDelay );
							
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}
					
				//ASSIGN FITNESS BASED UPON LIFE OF AGENT
				//REDUCE SCORE BASED UPON SIZE OF GENOME
				double sizePenalty = genotype.size() * adjustForNetworkSizeFactor;
				genotype.setFitnessValue( (int)(timeFitness - sizePenalty) );
				
			}
			catch ( TranscriberException e ) {
				logger.error( "transcriber error: " + e.getMessage() );
				genotype.setFitnessValue( 1 );
				System.exit(1);
			}
		}
		
	}
	
	private double round( double num, int places) {
		return Math.round( num*(Math.pow(10, places)))/Math.pow(10, places) ;
	}
	
	private double round( double num) {
		return Math.round(num*100)/100.0;
	}

	private double getFakeAttackSensorSignal() {
		double randomFakeAttackSensorValue = (upperFakeAttackSignalBound + lowerFakeAttackSignalBound)/2;
		
		if (gaussianDistributionOfNoise) {
			//probabilistic limits (could give bad values)
			randomFakeAttackSensorValue += randomizer.getRand().nextGaussian() * (upperFakeAttackSignalBound - lowerFakeAttackSignalBound)/2;
			//SET BOUNDS
			if (randomFakeAttackSensorValue < 0) {
				randomFakeAttackSensorValue = 0;
			} else if (randomFakeAttackSensorValue > 1) {
				randomFakeAttackSensorValue = 1.0;
			}
		} else {
			//hard limits (lower + random (0-1)* (upper-lower) )
			randomFakeAttackSensorValue = lowerFakeAttackSignalBound + randomizer.getRand().nextDouble() * (upperFakeAttackSignalBound - lowerFakeAttackSignalBound);
		}
		
		return randomFakeAttackSensorValue;
	}
	
	
	
	private double getRealAttackSensorSignal() {
		double randomRealAttackSensorValue = (upperRealAttackSignalBound + lowerRealAttackSignalBound)/2;
		
		if (gaussianDistributionOfNoise) {
			//probabilistic limits (could give bad values)
			randomRealAttackSensorValue += randomizer.getRand().nextGaussian() * (upperRealAttackSignalBound - lowerRealAttackSignalBound)/2;
			//SET BOUNDS
			if (randomRealAttackSensorValue < 0) {
				randomRealAttackSensorValue = 0;
			} else if (randomRealAttackSensorValue > 1) {
				randomRealAttackSensorValue = 1.0;
			}
		} else {
			//hard limits (lower + random (0-1)* (upper-lower) )
			randomRealAttackSensorValue = lowerRealAttackSignalBound + randomizer.getRand().nextDouble() * (upperRealAttackSignalBound - lowerRealAttackSignalBound);
		}
		
		return randomRealAttackSensorValue;
	}

	
	
	@Override
	public int getMaxFitnessValue() {
		return maxFitnessValue;
	}




	
	
		

}
