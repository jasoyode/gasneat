package experiment_builder.controller;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.events_commands.AddRewardToFitnessEvent;
import experiment_builder.events_commands.DeathEvent;
import experiment_builder.events_commands.EventCommand;
import experiment_builder.events_commands.FlipOrientation;
import experiment_builder.events_commands.RemoveAllRewardsEvent;
import experiment_builder.events_commands.ReplenishAllRewardsEvent;
import experiment_builder.events_commands.ShuffleRewards;
import experiment_builder.model.CellGrid;

public class RegisterEventCommand {

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( RegisterEventCommand.class );
	
	private volatile static RegisterEventCommand rec;

	public static RegisterEventCommand getInstance() {
		if (rec == null) {
			synchronized (RegisterEventCommand.class) {
				rec = new RegisterEventCommand();
			}
		}

		return rec;
	}

	//private Agent agent;
	private HashSet<EventCommand> commandList;
	private CellGrid gc;

	private RegisterEventCommand() {
		commandList = new HashSet<>();
	}

	
	
	public void checkEvent(int start, int stop) {
		EventCommand ec = null;
		
		logger.info( "Cell event values" +  gc.getCellEvents().values() );
		
		//ACTIONS THAT TAKE PLACE IMMEDIATELY UPON ENTERING SPACE
		if (gc.getCellEvents().containsKey(stop)) {
			for (String s : gc.getCellEvents().get(stop)) {
				
				logger.info("Considering Event:" + s);
				
				
				//THIS MUST HAPPEN BEFORE THE BELOW!
				if (s.equals("AddRewardToFitnessEvent")) {
					ec = new AddRewardToFitnessEvent(gc, stop);
					ec.execute();
				}
				
				if (s.equals("FlipOrientation")) {
					ec = new FlipOrientation(gc.getAgent() );
					
					logger.trace("PRE FLIP "+  gc.getAgent().getOrientation() );
					
					ec.execute();
					
					logger.trace("POST FLIP "+  gc.getAgent().getOrientation() );
					
				}
				
				
				if (s.equals("DeathEvent")) {
					int pos = -1;
					for (Integer i : gc.getCellProperties().keySet()) {
						if (gc.getCellProperties().get(i).contains("Home")) {
							pos = i;
						}
					}

					logger.trace("PRE DEATHEVENT "+  gc.getAgent().getOrientation() );
					
					ec = new DeathEvent(gc, pos);
					ec.execute();
					
					logger.trace("POST DEATHEVENT "+  gc.getAgent().getOrientation() );

				}
				if (s.equals("ShuffleRewards")) {
					ec = new ShuffleRewards(gc);
					ec.execute();
				}
			}
		}
		
		
		
		//ACTIONS THAT TAKE PLACE IMMEDIATELY AFTER BEING IN A SPACE
		if (gc.getCellEvents().containsKey(start)) {
			for (String s : gc.getCellEvents().get(start)) {
				
				logger.info("Considering Event:" + s);
				

				
				if (s.equals("RemoveAllRewardsEvent")) {
					ec = new RemoveAllRewardsEvent(gc);
					ec.execute();
				}
				
				if (s.equals("ReplenishAllRewardsEvent")) {
					ec = new ReplenishAllRewardsEvent(gc);
					ec.execute();
				}
			}
		}
		
				
		
	}

	public void deRegister(EventCommand ec) {
		commandList.remove(ec);
	}

	public void register(EventCommand ec) {
		commandList.add(ec);
	}

	public void setEnvironment(CellGrid gc){ //, Agent agent) {
		//this.agent = agent;
		this.gc = gc;
	}
}
