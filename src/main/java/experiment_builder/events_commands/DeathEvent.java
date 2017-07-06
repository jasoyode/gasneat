package experiment_builder.events_commands;

import org.apache.logging.log4j.LogManager; 
import org.apache.log4j.Logger;

import experiment_builder.controller.ParametersCalculator;
import experiment_builder.controller.RegisterEventCommand;
import experiment_builder.model.CellGrid;

public class DeathEvent implements EventCommand{

	private static Logger logger = Logger.getLogger( DeathEvent.class );
	
	//private Agent agent;
	private CellGrid cellGrid ;
	private int pos ;
	
	private static final double DEATH_COEFFICIENT  = -8;
	
	public DeathEvent(CellGrid gc,int pos) {
		this.cellGrid = gc;
		this.pos = pos;
	}
	
	@Override
	public String className() {
		return "DeathEvent";
	}

	@Override
	public void execute() {
		Teleport tele = new Teleport(cellGrid.getAgent(), pos);
		
		ParametersCalculator.setFitnessScore(  ParametersCalculator.getFitnessScore() + DEATH_COEFFICIENT  );
		logger.info("Triggered Death Event Reducing score by "+ DEATH_COEFFICIENT);
		tele.execute();
		
		logger.debug(" PRE DEATH agent orientation "+ cellGrid.getAgent().getOrientation()  );
		//ALWAYS SET UPRIGHT
		cellGrid.getAgent().setXOrientation(0);
		cellGrid.getAgent().setYOrientation(-1);
		
		logger.debug(" POST DEATH agent orientation "+ cellGrid.getAgent().getOrientation()  );
		logger.debug(" DEATH EVENT TRIGGERED CALLING endExperiment"  );
		cellGrid.endExperiment();
		
	}

	@Override
	public void register() {
		RegisterEventCommand.getInstance().register(this);
	}
	
}
