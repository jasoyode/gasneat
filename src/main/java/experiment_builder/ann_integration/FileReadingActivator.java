package experiment_builder.ann_integration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;

import com.anji.integration.Activator;

import org.apache.log4j.Logger;

public class FileReadingActivator implements Activator {

	private static Logger logger = Logger.getLogger( FileReadingActivator.class );
	public static final int INPUT_SIZE = 1;
	
	//each element in the array list is the set of output signals received from output neurons
	ArrayList<double[]> activationSignals;
	
	int currentTimeStepIndex;
	
	
	public FileReadingActivator(String filename) {
		
		activationSignals = new ArrayList<double[]>(); 
		currentTimeStepIndex = 0;
	
		logger.info("Reading from Console");
		Scanner scan = new Scanner("");
		try {
			scan = new Scanner(  new FileInputStream( filename ) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while (scan.hasNext()) {
			String line = scan.nextLine();
			logger.info("Network Input received from file : " + line);
			String[] str = line.split(";");
			double[] input = new double[str.length];
			
			for (int i = 0; i < str.length; i++) {
				System.out.println( str[i]  );
				input[i] = Double.parseDouble(str[i]);
			}
			activationSignals.add(input);
		}
		
		int time = 0;
		for (double[] data: activationSignals ) {
			logger.debug( "activation signals @ time: "+ time +"\t"  );
			for (double datum: data) {
				logger.debug("\t"+datum);				
			}
			logger.debug("");
			time++;
		}
		scan.close();
	}
	
	
	
	public double[] next( double[] sensorValues) {
		//IGNORE SENSOR VALUES, WE ARE REPLAYING WHAT HAPPENED ALREADY!
		double[] motorData = activationSignals.get(currentTimeStepIndex);
		
		currentTimeStepIndex += 1;
		logger.debug("Motor data loaded from file: " );
		for (double datum: motorData) {
			logger.debug("\t"+datum);				
		}
		
		
		if (currentTimeStepIndex >=  activationSignals.size() ) {
			currentTimeStepIndex = currentTimeStepIndex %  activationSignals.size();
			logger.warn("USED ALL INPUTS FROM FILE, REPEATING NOW...");
		}
		
		return motorData;
	}



	@Override
	public String getXmlRootTag() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String getXmld() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public double[] next() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public double[][] next(double[][] stimuli) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public double getMinResponse() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public double getMaxResponse() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getInputDimension() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int getOutputDimension() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
