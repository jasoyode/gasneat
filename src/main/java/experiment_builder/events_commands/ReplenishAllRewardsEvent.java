package experiment_builder.events_commands;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.controller.RegisterEventCommand;
import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;

public class ReplenishAllRewardsEvent implements EventCommand {

	private static Logger logger = Logger.getLogger( ReplenishAllRewardsEvent.class );
	
	
	private Agent agent;
	private CellGrid gc ;
	private int pos;
	
	public ReplenishAllRewardsEvent(CellGrid gc) {
		this.gc = gc;
	}
	
	@Override
	public String className() {
		return "ReplenishAllRewards";
	}

	@Override
	public void execute() {
		gc.replenishRemovedRewards();
		
	}

	@Override
	public void register() {
		RegisterEventCommand.getInstance().register(this);
		
	}

}
