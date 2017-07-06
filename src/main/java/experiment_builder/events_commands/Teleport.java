package experiment_builder.events_commands;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.controller.RegisterEventCommand;
import experiment_builder.model.Agent;

public class Teleport implements EventCommand{
	
	private static Logger logger = Logger.getLogger( Teleport.class );
	
	Agent agent;
	int pos;
	
	Teleport(Agent agent, int i ) {
		this.agent = agent;
		this.pos = i;
	}
	
	@Override
	public String className() {
		return "Teleport";
	}

	@Override
	public void execute() {
		logger.info("Agent teleported to " + pos);
		agent.setPos(pos);
	}

	@Override
	public void register() {
		RegisterEventCommand.getInstance().register(this);
	}

}
