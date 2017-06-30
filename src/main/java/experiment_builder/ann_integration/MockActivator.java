package experiment_builder.ann_integration;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.jgap.Chromosome;
import org.apache.log4j.Logger;

public class MockActivator implements MockActivatorImpl{

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( MockActivator.class );
	
	public static final int INPUT_SIZE = 1;
	private Chromosome chromosome;
	
	public MockActivator(Chromosome c) {
		this.chromosome = c;
	}

	public String getName() {
		return chromosome.toString();
	}
	
	public double[] next( double[] sensorValues) {
		Random r = new Random();
		double[] motorValues = new double[INPUT_SIZE];
		for (int i=0; i< INPUT_SIZE; i++) {
			motorValues[i] = r.nextDouble();
		}
		return motorValues;
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
}
