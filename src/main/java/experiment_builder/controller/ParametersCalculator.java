package experiment_builder.controller;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import experiment_builder.model.CellGrid;

public class ParametersCalculator {
	
	private static Logger logger = Logger.getLogger( ParametersCalculator.class );
	
	// the object of this class is created in Controller class

	/*
	 * CURRENT CALCULATIONS OF PARAMETERS: parametersCalculator is a singleton
	 * class object fitnessScore = increases by 0.4 after every agent action =
	 * increases by reward value too if reward is present energyLevel =
	 * decreases by 1 after every agent action = increases by reward value too
	 * if reward is present timeSteps = increases by 1 after every agent action
	 * resourceLevel= is an array of all types of resources(food,water) = types
	 * defined in Constants class = keeps track of current level of each of the
	 * types of resources
	 */
	
	private static @Setter double fitnessActionCoefficient = 1.0;
	private static @Setter double fitnessTouchRewardCoefficient = 0.0;
	private static @Setter double fitnessConsumeRewardCoefficient = 4.0;
	
	//set to zero to ignore these
	private static @Setter double foodConsumptionRate = 2.0;
	private static @Setter double waterConsumptionRate = 2.0;
	
	
	private static @Setter double foodMetabolismRate = 1.0;
	private static @Setter double waterMetabolismRate = 1.0;
	private static @Setter double restingFoodMetabolismRate = 0.1;
	private static @Setter double restingWaterMetabolismRate = 0.1;
	
	
	private static @Setter double healthDamageRate = 1.0;
	private static @Setter double healthRecoveryRate = 2.0;
	private static @Setter @Getter double fitnessScore;
	
	private static @Setter CellGrid cellGrid;
	private static ParametersCalculator parametersCalculator;
	private static @Setter @Getter double[] resourcesLevel = new double[Constants.REWARD_TYPES.values().length];
	private static @Setter @Getter double timeSteps;

	
	public static void displayParameters() {

		if (logger.isDebugEnabled()) {
			System.out.println("Agent Position: " + cellGrid.getAgent().getPos()  );
			System.out.println("Agent Health:" +  cellGrid.getAgent().getHealth()  );
			System.out.println("Agent Orientation:" +  cellGrid.getAgent().getOrientation()  );
			System.out.println("Fitness Score:" + fitnessScore);
			System.out.println("Agent Food Level:" + cellGrid.getAgent().getFoodLevel() );
			System.out.println("Agent Water Level:" + cellGrid.getAgent().getWaterLevel() );
			System.out.println("TimeSteps:" + timeSteps + "\n");	
		}
	}
	
	
	public static void updateAgentHealthAndConsumeResources(CellGrid cg, boolean agentExpendedEnergy ) {
		//check if out of health
		if ( cellGrid.getAgent().getHealth() < 0 ) {
			AgentActions.endExperiment();
		}
		cellGrid = cg;
		
		boolean enoughFood = false;
		boolean enoughWater = false;
		
		if (  cellGrid.getAgent().getFoodLevel() > 0 ) {
			//fall through we are good
			enoughFood =true;
		} else {
			//with zero food, we must drop our health
			cellGrid.getAgent().setHealth(   cellGrid.getAgent().getHealth() - healthDamageRate  );
		}
		
		if (  cellGrid.getAgent().getWaterLevel() > 0 ) {
			//fall through we are good
			enoughWater = true;
		} else {
			//with zero water, we must drop our health
			cellGrid.getAgent().setHealth(   cellGrid.getAgent().getHealth() - healthDamageRate  );
		}
		
		//if our health can increase because of available resources, then increase it
		if ( enoughFood && enoughWater ) {
			cellGrid.getAgent().setHealth(  cellGrid.getAgent().getHealth() + healthRecoveryRate );
			//bound at maximum health
			if (  cellGrid.getAgent().getHealth() > cellGrid.getAgent().getMaximumHealth() ) {
				cellGrid.getAgent().setHealth(  cellGrid.getAgent().getMaximumHealth() );
				//System.out.println(  "cellGrid.getAgent().getMaximumHealth(): "+ cellGrid.getAgent().getMaximumHealth()  );
			}
			
			
		}
		
		
		//System.out.println(  "cellGrid.getAgent().getMaximumHealth(): "+ cellGrid.getAgent().getMaximumHealth()  );
		
		//Consume food
		if ( agentExpendedEnergy ) {
			cellGrid.getAgent().setFoodLevel( cellGrid.getAgent().getFoodLevel() - foodMetabolismRate  ) ;
			cellGrid.getAgent().setWaterLevel( cellGrid.getAgent().getWaterLevel() - waterMetabolismRate  );			
		} else {
			cellGrid.getAgent().setFoodLevel( cellGrid.getAgent().getFoodLevel() - restingFoodMetabolismRate  ) ;
			cellGrid.getAgent().setWaterLevel( cellGrid.getAgent().getWaterLevel() - restingWaterMetabolismRate  );
		}
		
		//bound levels at zero
		if ( cellGrid.getAgent().getFoodLevel() < 0) {
			cellGrid.getAgent().setFoodLevel( 0 );
		}
		if ( cellGrid.getAgent().getWaterLevel() < 0) {
			cellGrid.getAgent().setWaterLevel( 0 );
		}
		
		
		//check if out of health
		if ( cellGrid.getAgent().getHealth() < 0 ) {
			AgentActions.endExperiment();
		}
		
		ParametersCalculator.displayParameters();
	}
	
	public static void eatAction(CellGrid cellGrid) {
		
		
		fitnessScore +=  fitnessActionCoefficient;
		timeSteps +=  1.0;
		
		
		//todo fix this everywhere, this should not need done
		ParametersCalculator.cellGrid = cellGrid;
		
		if ( cellGrid.getRewards().containsKey( cellGrid.getAgent().getPos() ) ) {
			
			int agentPos = cellGrid.getAgent().getPos();
			double cellFood = cellGrid.getRewards().get( agentPos  ).getValue();
			double agentHealth = cellGrid.getAgent().getHealth();
			
			double agentFoodLevel = cellGrid.getAgent().getFoodLevel();
			double agentWaterLevel = cellGrid.getAgent().getWaterLevel();
			
			if (cellFood - foodConsumptionRate >= 0) {
				cellGrid.getAgent().setFoodLevel( agentHealth + foodConsumptionRate );
				cellGrid.getRewards().get( agentPos  ).setValue(  cellFood - foodConsumptionRate );
				ParametersCalculator.updateFitnessEatAction(cellGrid, foodConsumptionRate);
				
			} else {
				cellGrid.getAgent().setHealth( agentHealth + cellFood );
				cellGrid.getRewards().get( agentPos  ).setValue(  0 );
				ParametersCalculator.updateFitnessEatAction(cellGrid, cellFood );
			}
			
			ParametersCalculator.displayParameters();
			
		} else {
			//consume some energy for trying to eat, but only when there is no food
			updateAgentHealthAndConsumeResources(cellGrid, true );
		}
			
		
	}
	
	
	
	

	public static void updateFitnessEatAction(CellGrid cg, double foodQty) {
		cellGrid = cg;
		cellGrid.getAgent().setFoodLevel(  cellGrid.getAgent().getFoodLevel() + foodQty);
		
		if ( cellGrid.getAgent().getFoodLevel() > cellGrid.getAgent().getMaximumFood() ) {
			cellGrid.getAgent().setFoodLevel(  cellGrid.getAgent().getMaximumFood()  );
		}
		fitnessScore += fitnessConsumeRewardCoefficient * foodQty;
		
	}
	
	
	public static void updateParameters(CellGrid cg, int pos) {
		cellGrid = cg;
		
		//TODO section - this can be expanded to allow for the food/water balancing needs
		
		fitnessScore +=  fitnessActionCoefficient;
		
		if (cellGrid.getRewards().containsKey(pos)) {
			fitnessScore +=  fitnessTouchRewardCoefficient*cellGrid.getRewards().get(pos).getValue();
		}

		
		timeSteps +=  1.0;
		
		//ParametersCalculator.displayParameters();
		

	}
	
	public static void reset() {
		fitnessScore = 0.0;
		timeSteps = 0.0;
		cellGrid.getAgent().reset();
	}

	public ParametersCalculator() {
		fitnessScore = 0.0;
		timeSteps = 0.0;
	}


}
