package experiment_builder.main;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.controller.ExperimentBuilder;

public class Launcher {
	
	private static Logger logger = Logger.getLogger( Launcher.class );
	

	public static void main(String[] args) {
		
		ExperimentBuilder experimentBuilder = new ExperimentBuilder();
	}

}
