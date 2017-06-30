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

public class RandomShuffleRewards implements EventCommand {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( RandomShuffleRewards.class );
	
	private CellGrid gc;

	public RandomShuffleRewards(CellGrid gc) {
		this.gc = gc;

	}

	@Override
	public String className() {
		return "RandomShuffleRewards";
	}

	
	//may not actually change positions, depends on shuffle
	public void execute() {
		
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
