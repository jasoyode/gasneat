package experiment_builder.events_commands;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.controller.RegisterEventCommand;
import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;

public class RemoveAllRewardsEvent implements EventCommand {

	private static Logger logger = Logger.getLogger( RemoveAllRewardsEvent.class );
	
	
	private Agent agent;
	private CellGrid gc ;
	private int pos;
	
	public RemoveAllRewardsEvent(CellGrid gc) {
		this.gc = gc;
	}
	
	@Override
	public String className() {
		return "RemoveAllRewards";
	}

	@Override
	public void execute() {
		gc.temporarilyRemoveRewards();
		
	}

	@Override
	public void register() {
		RegisterEventCommand.getInstance().register(this);
		
	}

}
