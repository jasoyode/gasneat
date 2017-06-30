package experiment_builder.events_commands;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.controller.RegisterEventCommand;
import experiment_builder.model.Agent;

public class FlipOrientation implements EventCommand{
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( FlipOrientation.class );
	
	private Agent agent;
	
	public FlipOrientation(Agent agent ) {
		this.agent = agent;
	}
	
	@Override
	public String className() {
		return "FlipOrientation";
	}

	@Override
	public void execute() {
		logger.info("Agent orientation flipped!" );
		agent.flipOrientation();
	}

	@Override
	public void register() {
		RegisterEventCommand.getInstance().register(this);
	}

}
