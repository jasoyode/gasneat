package experiment_builder.events_commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;

import com.anji.util.Randomizer;

import org.apache.log4j.Logger;

import experiment_builder.controller.RegisterEventCommand;
import experiment_builder.model.CellGrid;
import experiment_builder.model.Reward;

public class ShuffleRewards implements EventCommand {
	
	private static Logger logger = Logger.getLogger( ShuffleRewards.class );
	
	private CellGrid gc;

	public ShuffleRewards(CellGrid gc) {
		this.gc = gc;

	}

	@Override
	public String className() {
		return "ShuffleRewards";
	}

	//deterministic always shifts the rewards in the same way
	@Override
	public void execute() {
		
		List<Reward> valueList = new ArrayList<Reward>(gc.getRewards().values() );
		//List<Integer> keyList = new ArrayList<Integer>(gc.getRewards().keySet() ); 
		Randomizer r = gc.getRandomizer();
		
		//Collections.shuffle(valueList, r.getRand() );
		//move each reward to the next location
		
		logger.debug("PRE "+gc.getRewards().entrySet());
		
		Integer valueIt = 0;
		for (Map.Entry<Integer, Reward> e : gc.getRewards().entrySet()) {
			valueIt = (valueIt + 1 ) % gc.getRewards().entrySet().size() ;
			
			//current entry change values to next 
			e.setValue( valueList.get( valueIt ) );
			
		}
		logger.info("Shuffled current rewards");
		
		logger.debug("POST "+gc.getRewards().entrySet());
		
		//System.exit(1);
		
		//ONLY SET STARTING REWARDS IF CURRENT REWARDS IS NOT EMPTY!!!!
		if (gc.getRewards().size() != 0) {
			valueIt = 0;
			for (Map.Entry<Integer, Reward> e : gc.getStartingRewards().entrySet()) {
				valueIt = (valueIt + 1 ) % gc.getRewards().entrySet().size() ;
				e.setValue(valueList.get(valueIt));
			}
			logger.debug("set starting rewards to shuffled");
		} else {
			
			System.err.println("NOOOOO REWARDS GONE");
			System.exit(1);
			
		}
		
		logger.debug(  gc.getRewards().entrySet()  );
		logger.debug( gc.getStartingRewards().entrySet() );
		
		
		logger.debug("Reward shuffle command executed");
		
		
	}

	
	
	
	public void oldExecute() {
		List<Reward> valueList = new ArrayList<Reward>(gc.getRewards().values());
		
		Randomizer r = gc.getRandomizer();
		
		Collections.shuffle(valueList, r.getRand() );
		
		Integer valueIt = 0;
		for (Map.Entry<Integer, Reward> e : gc.getRewards().entrySet()) {
			e.setValue(valueList.get(valueIt++));
		}
		logger.debug("Shuffled current rewards");
		
		//ONLY SET STARTING REWARDS IF CURRENT REWARDS IS NOT EMPTY!!!!
		if (gc.getRewards().size() != 0) {
			valueIt = 0;
			for (Map.Entry<Integer, Reward> e : gc.getStartingRewards().entrySet()) {
				e.setValue(valueList.get(valueIt++));
			}
			logger.debug("set starting rewards to shuffled");
		}
		
		logger.debug(  gc.getRewards().entrySet()  );
		logger.debug( gc.getStartingRewards().entrySet() );
		
		
		logger.debug("Reward shuffle command executed");
		
		
	}

	
	
	@Override
	public void register() {
		RegisterEventCommand.getInstance().register(this);
	}

}
