package experiment_builder.ann_integration;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager; 
import org.apache.log4j.Logger;


public class ANNLauncher {
	
	private static Logger logger = Logger.getLogger( ANNLauncher.class );
	
	public static void main(String[] args) {
		
		//Lets run 2 trials with our mock agent
		
		HashSet<Integer> shufflePoints = new HashSet<Integer>();
		
		shufflePoints.add(10);
		shufflePoints.add(20);
		shufflePoints.add(30);
		shufflePoints.add(40);
		
		int trials = 50;
		int numberOfAgents = 1;
		int maxTimesteps = 10;
		int delay = 100;
		boolean visible = true;
		boolean recordActivations = false;
		String filename = "xml_experiments/tmaze.xml";
		
		//String filename = "xml_experiments/strange_grid.xml";
		//String filename = "xml_experiments/food_corners.xml";
		//String filename = "xml_experiments/tmaze.xml";
		//String filename = "xml_experiments/solt_tmaze_no_shuffle.xml";
		//String filename = "xml_experiments/dummy_2x2.xml";
		//String filename = "xml_experiments/test.xml";
		//String filename = "xml_experiments/dtmaze_shuffle.xml";
		
		
		//String sensor = "SOLTOGGIO_SENSOR";
		
		logger.debug("Creating fitness function visiblility: "+visible+", delay: "+delay);
		ExperimentCreatorFitnessFunction fitnessFunction = new ExperimentCreatorFitnessFunction( filename, delay, visible, recordActivations, shufflePoints);
		
		
		fitnessFunction.setMaxTimesteps( maxTimesteps );
		fitnessFunction.setNumTrials(trials);
		
		ArrayList<MockChromosome> chromosomes = new ArrayList<MockChromosome>();
		for (int i=0; i <numberOfAgents; i++)  {
			chromosomes.add(new MockChromosome("Agent "+i) );
		}
		
		logger.debug("Starting to evaluate chromosomes using fitness function");
		fitnessFunction.evaluate(chromosomes);
		
		System.out.println("---------------------------------------"  );
		System.out.println("           Final Results  "  );
		System.out.println("---------------------------------------"  );
		
		double max=-100000;
		MockChromosome best = null;
		
		for (MockChromosome chromosome: chromosomes)  {
			System.out.println( chromosome.getName()+"  fitness= "+ chromosome.getFitnessValue() );
			if ( max < chromosome.getFitnessValue()) {
				best = chromosome;
				max = chromosome.getFitnessValue();
			}
		}
		
		System.out.println("---------------------------------------"  );
		System.out.println("           Overall Winner  "  );
		System.out.println("---------------------------------------"  );
		
		System.out.println("\n"+ best.getName()+"  fitness= "+ best.getFitnessValue() );
		
		
		
	}
	

}
