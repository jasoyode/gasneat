package experiment_builder.ann_integration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import com.anji.util.Randomizer;

import experiment_builder.controller.AgentActions;
import experiment_builder.controller.ParametersCalculator;
import experiment_builder.controller.RegisterEventCommand;
import experiment_builder.controller.XMLController;
import experiment_builder.events_commands.RandomShuffleRewards;
import experiment_builder.events_commands.ShuffleRewards;
import experiment_builder.input_action_map.InputActionMappable;
import experiment_builder.model.CellGrid;
import experiment_builder.view.CellGridPanel;
import experiment_builder.view.ExperimentRunnerFrame;
import experiment_builder.view.HumanButtonControlPanel;
import gasNEAT.activator.GasNeatActivator;
import gasNEAT.genericEvaluater.DisplayableBulkFitnessFunction;
import gasNEAT.view.ViewConstants;
import gasNEAT.view.networkView.NetworkViewFrame;
import lombok.Getter;
import lombok.Setter;


public class ExperimentCreatorFitnessFunction implements DisplayableBulkFitnessFunction, Configurable {

	private static Logger logger = Logger.getLogger( ExperimentCreatorFitnessFunction.class );
	private boolean shuffleFirstTrial;
	private boolean onCurrentFirstTrial;
	
	//delay for watching visualization
	private int delay;
	
	//for watching
	private boolean visibleMode;
	
	//should be read from file
	private InputActionMappable mapper;
	
	//must be set in constructor
	private boolean recordActivations;

	
	private ExperimentRunnerFrame myFrame;
	private BufferedWriter bufferedWriter;
	
	private Set<Integer> shufflePoints;
	
	//This value is kept NULL with when running in non-gui mode
	private CellGridPanel gridView;
	
	//when running in gui mode these variables should NOT be used, but instead pulled out
	//of gridView
	private CellGrid cellGrid; 
	
	//ADDED FOR GASNEAT
	private Randomizer randomizer;
	private ActivatorTranscriber activatorFactory;
	private final static String DISPLAY_DELAY_KEY = "display.delay";
	private final static String ADJUST_FOR_NETWORK_SIZE_FACTOR_KEY = "fitness.function.adjust.for.network.size.factor";
	private float adjustForNetworkSizeFactor = 0.0f;
	private float noise = 0.0f;
	
	private final static String EXPERIMENT_FILE_NAME_KEY = "experiment.builder.design.filename";
	private static final String SHUFFLE_REWARD_LOCATIONS_KEY = "experiment.builder.shuffle.reward.locations.at.start";
	private final static String SENSORY_NOISE_KEY = "experiment.builder.noise.level";


	private static final String NUM_TRIALS_KEY = "experiment.builder.number.of.trials";
	private static final String MAX_TIMESTEPS_KEY = "experiment.builder.number.timesteps.per.trial";
	
	private static final String PERIODIC_SHUFFLE_KEY = "experiment.builder.periodic.shuffle.reward.locations";
	private static final String PERIODIC_SHUFFLE_FILE_KEY = "experiment.builder.periodic.shuffle.reward.locations.file";
		
	private static final String FITNESS_ACTION_COEFFICIENT = "experiment.builder.action.coefficient";
	private static final String FITNESS_TOUCH_REWARD_COEFFICIENT = "experiment.builder.touch.reward.coefficient";
	private static final String FITNESS_CONSUME_REWARD_COEFFICIENT = "experiment.builder.consume.reward.coefficient";
	
	
	private @Getter @Setter boolean enableDisplay=false;
	private @Getter @Setter int maxTimesteps = 50;
	private @Getter @Setter int numTrials = 10;
	
	private NetworkViewFrame frame;
	private ParametersCalculator parametersCalculator;
	
	public ExperimentCreatorFitnessFunction(){
		
	}
	
	//creates the fitnessFunction by instantiating from file
	public ExperimentCreatorFitnessFunction(String filename, int delay, boolean vis, boolean recordOutputs, Set shufflePoints) {
		
		
		
		this.visibleMode = vis;
		this.delay = delay;
		this.recordActivations = recordOutputs;
		this.shufflePoints = shufflePoints;
		init(filename);
	}

	public static String getStringFormat(double[] array) {
		
		DecimalFormat df = new DecimalFormat("0.00#");
		StringBuilder text = new StringBuilder();
		text.append("[");
		for (double d: array) {
			String dd = df.format(d);
			
			if (Double.isNaN(d) ) {
				try {
					throw new Exception("NaN for a value?");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				text.append(" "+  dd.substring(0, 4) +", ");
			}
		}
		text.append("]");
		return text.toString();
	}
	
	public void performAction( double[] motorData) {
		logger.debug(  "   ...Performing action by agent on enviornment" );
		
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		logger.debug(  "motorData: " + getStringFormat( motorData) );
		
		if (mapper == null) {
			
			if (gridView == null) {
				mapper = cellGrid.getActionMap();
			} else {
				mapper = gridView.getCellGrid().getActionMap()  ;
			}
		}
		
		logger.debug("mapper: "+ mapper);
		
		//System.err.println(mapper);
		//System.err.println(mapper.getClass());
		
		
		mapper.actFromDoubleValue( motorData[0]  );
		
		//System.err.println("post-mapper");

	}
	

	

	public void evaluate( List genotypes ) {
		// evaluate each chromosome
		Iterator it = genotypes.iterator();
		while ( it.hasNext() ) {
			Chromosome c = (Chromosome) it.next();
			onCurrentFirstTrial = shuffleFirstTrial;
			//System.err.println( c );
			//System.err.println( c.getAlleles() );
			evaluate( c );
		}
	}

	public void evaluate( Chromosome c ) {
		
		logger.debug("Evaluating "+ c.toString() );
		try {
			//Activator activator = factory.newActivator( c );
			
			Activator activator;
			
			if ( c.equals(new MockChromosome("") ) ) {
				activator = new MockActivator( c );
			} else {
				activator = activatorFactory.newActivator( c );				
			}
			
			frame = null;
			if (visibleMode) {
				activator = (GasNeatActivator)activatorFactory.newActivator( c );
				((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork().setLabeled(true);
				frame = new NetworkViewFrame(((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork(), ((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork().getSimulator() );
				
			}
			
			//String fname = "recorded_activations/Agent 8_trial0.txt";
			
			//FileReadingActivator activator = new FileReadingActivator("recorded_activations/double_output_test.txt");
			//FileReadingActivator activator = new FileReadingActivator(fname);
			
			//must restart trial count for shuffle properly
			if (visibleMode) {
				gridView.getCellGrid().restartTrialCount();
			} else {
				cellGrid.restartTrialCount();
			}
			
			boolean firstTime = true;
			
			int fitness = 0;
			for ( int i = 0; i < numTrials; i++ ) {
				
				if ( recordActivations ) {
					bufferedWriter = new BufferedWriter( new FileWriter("recorded_activations/"+c.getId()+"_trial"+i+".txt") );
				}
				//write to file
				int value = singleTrial( activator ); 
				
				if ( recordActivations ) {
					bufferedWriter.close();
				}
				
				if (visibleMode) {
					logger.info(  c.getId() + " Trial "+ gridView.getCellGrid().getTrialNumber() +" score: "+ value  );
				} else {
					logger.info(  c.getId() + " Trial "+ cellGrid.getTrialNumber() +" score: "+ value  );
				}
				fitness += value;
			}
		
			//System.out.println( c.getId()+ " fitness: " + fitness  );
			
		
			//set to average fitness value, so it is not dependent upon the numTrials
			c.setFitnessValue(   100*   (fitness / numTrials)/25   );
		}
		catch ( Throwable e ) {
			logger.error( "error evaluating chromosome " + c.toString(), e );
			//logger.warn( "error evaluating chromosome " );
			c.setFitnessValue( 0 );
			e.printStackTrace(); 
			System.exit(1);
		}
	}



	private String getFormattedSensorData() {
		logger.debug( "   ...Extracting sensor data from enviornment" );
		String formattedSensorData;
		if (visibleMode) {
			formattedSensorData = gridView.getCellGrid().getFormattedSensorData();
		} else {
			formattedSensorData = cellGrid.getFormattedSensorData();
		}
		return formattedSensorData;
	}
	
	private double[] getSensorData() {
		logger.debug( "   ...Extracting sensor data from enviornment" );
		double[] sensorData;
		if (visibleMode) {
			sensorData = gridView.getCellGrid().getSensorData();
		} else {
			sensorData = cellGrid.getSensorData();
		}
		return sensorData;
	}


	public void init( String filepath ) {
		
		
		if (visibleMode) {
			
			gridView = new CellGridPanel();
			XMLController.loadLayoutXML(filepath, gridView) ;
			HumanButtonControlPanel buttonPanel = new HumanButtonControlPanel();
			gridView.getCellGrid().setActiveInExperiment(true);
			myFrame = new ExperimentRunnerFrame(gridView, buttonPanel, this.visibleMode);
			RegisterEventCommand.getInstance().setEnvironment(gridView.getCellGrid() );
			AgentActions agentActions = AgentActions.getInstance();
			agentActions.setGridView(gridView);
			agentActions.setButtonPanel(buttonPanel);
			
			logger.debug( "visible[0] "+gridView.getCellGrid().getVisibility()[0] );
			gridView.getCellGrid().setFinalizedMazeCellFromVisibility();
			logger.debug( "finalized "+gridView.getCellGrid().getFinalizedMazeCells() );
			parametersCalculator.setCellGrid( gridView.getCellGrid() );
			
			mapper = gridView.getCellGrid().getActionMap();
			
			gridView.getCellGrid().setRandomizer( randomizer );			
		} else {
			//////NONGUI MODE
			
			//pass in references and set values
			cellGrid = new CellGrid();
			XMLController.loadLayoutXML(filepath, cellGrid);
			RegisterEventCommand.getInstance().setEnvironment(cellGrid );
			AgentActions agentActions = AgentActions.getInstance();
			agentActions.setCellGrid(cellGrid);
			
			logger.debug( "visible[0] "+cellGrid.getVisibility()[0] );
			cellGrid.setFinalizedMazeCellFromVisibility();
			logger.debug( "finalized "+cellGrid.getFinalizedMazeCells() );
			parametersCalculator.setCellGrid( cellGrid );
			
			mapper = cellGrid.getActionMap();
			
			cellGrid.setRandomizer( randomizer );
		}
		

	}

	private int singleTrial( Activator activator ) {
		ParametersCalculator.reset();
		
		//System.err.println("SINGLE TRIAL START");
		
		
		//TODO embed this in the environment somehow!
		if (visibleMode) {
			
			
			logger.info( "Trial : " + gridView.getCellGrid().getTrialNumber() );
			gridView.getCellGrid().restartExperiment();
			if ( shufflePoints.contains( gridView.getCellGrid().getTrialNumber() ) ) {
				logger.info("Shuffling Rewards!");
				ShuffleRewards sr = new ShuffleRewards( gridView.getCellGrid() );
				sr.execute();
			}
			
			
			
			
			//randomize first trial
			if (onCurrentFirstTrial) {
				RandomShuffleRewards rsr = new RandomShuffleRewards( gridView.getCellGrid() );
				rsr.execute();
				onCurrentFirstTrial = false;
			}
			
		} else {
			logger.info( "Trial : " + cellGrid.getTrialNumber() );
			cellGrid.restartExperiment();
			
			if ( shufflePoints.contains( cellGrid.getTrialNumber() ) ) {
				logger.info("Shuffling Rewards!");
				ShuffleRewards sr = new ShuffleRewards( cellGrid );
				sr.execute();
			}
			
			//randomize first trial
			if (onCurrentFirstTrial) {
				RandomShuffleRewards rsr = new RandomShuffleRewards( cellGrid );
				rsr.execute();
				onCurrentFirstTrial = false;
			}
			
			
		}
		
		
		int fitness = 0;
		int currentTimestep = 0;
		
		// Network activation SENSOR values
		String  formattedSensorData;
		double[]  sensorData;
		
		for ( currentTimestep = 0; currentTimestep < maxTimesteps; currentTimestep++ ) {
			logger.debug("TIMESTEP START OF LOOP: "+ currentTimestep);
			
			//read the sensor information from experiment
			formattedSensorData = getFormattedSensorData();
			sensorData = getSensorData();
			
			//INTRODUCE OPTIONAL NOISE AT INPUT ONLY
			if (noise > 0) {
				for (int i=0; i < sensorData.length; i++) {
					logger.debug("Prenoise: " + sensorData[i]);
					double rand_noise = noise * (randomizer.getRand().nextDouble() -0.5 );
					sensorData[i] += rand_noise;
					logger.debug("Postnoise: " + sensorData[i]);
				}
			}
			
			
			
			// Activate the network.
			double[] motorData = activator.next( sensorData );
			
			//WRITE MOTOR DATA TO FILE
			if (recordActivations) {
				
				try {
				
					for (double d: motorData ) {
						bufferedWriter.write(  d+";" );
					}
				
					bufferedWriter.newLine();
					
				} catch (IOException e) {
					e.printStackTrace();
				};
			}
			
			if (visibleMode) {
				frame.updateNeuralNetworkPanel(ViewConstants.PLAY_STATUS_TEXT, ((GasNeatActivator)activator).getGasNeatNet().getGasNeatNeuralNetwork() );
			}

			//show before it goes
			if ( visibleMode ) {
				myFrame.getInputDataPanel().setData(  formattedSensorData  );
				myFrame.getOutputDataPanel().setData( motorData   );
				myFrame.repaint();
				myFrame.getOutputDataPanel().repaint();
				myFrame.getInputDataPanel().repaint();
				
				
				
			}
			
			//System.err.println("pre perform action");
			
			//Use the motor data to update the enviornment
			performAction( motorData );
			
			//System.err.println("post perf action");
			
			logger.debug("Motor data: " + motorData[0] );
			ParametersCalculator.displayParameters();

			/*
			if ( visibleMode ) {
				ParametersCalculator.updateParameters(gridView.getCellGrid(), gridView.getCellGrid().getAgent().getPos()  );
			} else {
				ParametersCalculator.updateParameters(cellGrid, cellGrid.getAgent().getPos()  );
			}
			*/
			
			if ( visibleMode ) {
				if ( gridView.getCellGrid().isTrialOver() ) {
					logger.info("Trial has been set to be over! Ending Current Trial Now!");
					break;
				} else {
					logger.info("cellGrid.isTrialOver() = " + gridView.getCellGrid().isTrialOver() );
				}
			} else {
				if ( cellGrid.isTrialOver() ) {
					logger.debug("Trial has been set to be over! Ending Current Trial Now!");
					break;
				} else {
					logger.debug("cellGrid.isTrialOver() = " + cellGrid.isTrialOver() );
				}	
			}
			

		}
		logger.debug("TIMESTEP END OF LOOP: "+ currentTimestep);
		fitness = (int) ParametersCalculator.getFitnessScore();
		logger.debug("FITNESS AT END OF LOOP: "+ fitness );
		return fitness;
	}

	@Override
	public int getMaxFitnessValue() {
		// TODO Auto-generated method stub
		
		
		return 0;
	}

	@Override
	public void init(Properties props) throws Exception {

		
		try {
			randomizer = (Randomizer) props.singletonObjectProperty( Randomizer.class );
			activatorFactory = (ActivatorTranscriber) props
					.singletonObjectProperty( ActivatorTranscriber.class );

			//stimuli = Properties.loadArrayFromFile( props.getResourceProperty( STIMULI_FILE_NAME_KEY ) );
			//targets = Properties.loadArrayFromFile( props.getResourceProperty( TARGETS_FILE_NAME_KEY ) );
			
			
			noise = props.getFloatProperty( SENSORY_NOISE_KEY, 0.0f );
			shuffleFirstTrial = props.getBooleanProperty( SHUFFLE_REWARD_LOCATIONS_KEY, false );
			
			
			if (enableDisplay) {
				this.delay =  props.getIntProperty( DISPLAY_DELAY_KEY, 0 );
			} else {
				this.delay = 0;
			}
			
			
			
			numTrials =  props.getIntProperty( NUM_TRIALS_KEY );
			maxTimesteps =  props.getIntProperty( MAX_TIMESTEPS_KEY );
			
			boolean periodicRewardShuffling =  props.getBooleanProperty(PERIODIC_SHUFFLE_KEY);
			String shuffleFilePath = props.getProperty( PERIODIC_SHUFFLE_FILE_KEY );
			shufflePoints = new HashSet<Integer>();
			
			
			if (periodicRewardShuffling) {
			
				try {
					BufferedReader shuffleList = new BufferedReader( new FileReader( shuffleFilePath ));
					String currentLine = shuffleList.readLine();
					for (int i=0; currentLine  != null; i++) {
						shufflePoints.add(   Integer.parseInt(currentLine)  );
						currentLine = shuffleList.readLine();
					}
					shuffleList.close();
				} catch ( IOException e) {
					System.out.println("Could not read file:" +shuffleFilePath );
					e.printStackTrace();
					System.exit(1);
				}
			}
			
			adjustForNetworkSizeFactor = props.getFloatProperty( ADJUST_FOR_NETWORK_SIZE_FACTOR_KEY,
					0.0f );
			
			String filename = props.getProperty( EXPERIMENT_FILE_NAME_KEY );
			if ( delay == 0 ) {
				visibleMode = false;
			} else {
				visibleMode = true;				
			}
			this.recordActivations = false;
			
			
			
			parametersCalculator = new ParametersCalculator();
			
									
			double fitnessActionCoefficient = props.getFloatProperty( FITNESS_ACTION_COEFFICIENT );
			double fitnessTouchRewardCoefficient = props.getFloatProperty( FITNESS_TOUCH_REWARD_COEFFICIENT );
			double fitnessConsumeRewardCoefficient = props.getFloatProperty( FITNESS_CONSUME_REWARD_COEFFICIENT );
			
			parametersCalculator.setFitnessActionCoefficient(fitnessActionCoefficient);
			parametersCalculator.setFitnessTouchRewardCoefficient(fitnessTouchRewardCoefficient);
			parametersCalculator.setFitnessConsumeRewardCoefficient(fitnessConsumeRewardCoefficient);
			
			
			init(filename);
			
			
		}
		catch ( Exception e ) {
			throw new IllegalArgumentException( "invalid properties: " + e.getClass().toString() + ": "
					+ e.getMessage() );
		}
		
		
	}



}
