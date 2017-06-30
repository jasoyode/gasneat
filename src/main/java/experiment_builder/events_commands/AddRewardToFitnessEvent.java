package experiment_builder.events_commands;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.controller.ParametersCalculator;
import experiment_builder.controller.RegisterEventCommand;
import experiment_builder.model.CellGrid;

public class AddRewardToFitnessEvent implements EventCommand {

private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( AddRewardToFitnessEvent.class );
	
	
	private CellGrid gc ;
	private int pos;
	
	public AddRewardToFitnessEvent(CellGrid gc, int pos) {
		this.gc = gc;
		this.pos = pos;
	}
	
	@Override
	public String className() {
		return "AddRewardToFitnessEvent";
	}

	@Override
	public void execute() {

		if ( gc.getRewards().containsKey(pos) ) {
			double reward = gc.getRewards().get(pos).getValue();
			ParametersCalculator.setFitnessScore(  ParametersCalculator.getFitnessScore() + reward );
			logger.info("Fitness Increased by " + reward);
		} else {
			logger.info("There is no reward in this cell to increase score by!");
		}

		
	}

	@Override
	public void register() {
		RegisterEventCommand.getInstance().register(this);
		
	}
	
	
}