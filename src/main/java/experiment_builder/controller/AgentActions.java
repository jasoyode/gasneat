package experiment_builder.controller;

import static experiment_builder.constants.Constants.FOUR;
import static experiment_builder.constants.Constants.ONE;
import static experiment_builder.constants.Constants.THREE;
import static experiment_builder.constants.Constants.TWO;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;
import experiment_builder.view.CellGridPanel;
import experiment_builder.view.InputDataPanel;
import lombok.Getter;
import lombok.Setter;

public class AgentActions {
	
	private static Logger logger = Logger.getLogger( AgentActions.class );
	
	//singleton
	private static AgentActions agentActions;
	
	//USE IN NONGUI MODE
	private static @Setter CellGrid cellGrid;
	
	//USE IN GUI MODE check if null to determine if visible or not
	private static @Setter CellGridPanel gridView;
	
	private static @Setter JPanel buttonPanel;
	private static @Setter int mazeLength;
	private static @Setter @Getter int mazeWidth;

	//TODO:  NEED TO IMPLEMENT THESE OPTIONS
	private static boolean eatAll;
	private static int eatRate = 1;
	
	
	private final static int MAX_TIMESTEPS = 100;
	
	//need this to show the 
	public static InputDataPanel inputDataPanel;
	
	
	public static void setInputDataPanel( InputDataPanel in) {
		inputDataPanel = in;
	}
	
	
	public static void updateInputDataPanel(){
		if (gridView != null) {
			inputDataPanel.setData(  cellGrid.getFormattedSensorData()  );
		}
		
		
	}
	

	public static boolean checkEat() {
		boolean flag = false;
		
		if (gridView == null) {
			if (cellGrid.getRewards().containsKey(cellGrid.getAgent().getPos())) {
				if (cellGrid.getRewards().get(cellGrid.getAgent().getPos()).getValue() >= 0) {
					flag = true;
				}
			}
		} else {
			if (gridView.getCellGrid().getRewards().containsKey(gridView.getCellGrid().getAgent().getPos())) {
				if (gridView.getCellGrid().getRewards().get(gridView.getCellGrid().getAgent().getPos()).getValue() >= 0) {
					flag = true;
				}
			}	
		}
		return flag;
	}

	
	public static void rest() {
		
		if (ParametersCalculator.getTimeSteps() >= MAX_TIMESTEPS) {
			endExperiment();
		}
		ParametersCalculator.updateParameters( cellGrid, cellGrid.getAgent().getPos()  );
		ParametersCalculator.updateAgentHealthAndConsumeResources( cellGrid, false );
	}
	
	
	//fix it so that agents eat 1 unit per eat action
	public static void eat() {
		if (ParametersCalculator.getTimeSteps() >= MAX_TIMESTEPS) {
			endExperiment();
		}
		logger.debug("Eat action performed");
		if (gridView == null) {
			ParametersCalculator.eatAction( cellGrid);
		} else {
			ParametersCalculator.eatAction( gridView.getCellGrid() );
		}
			
	}

	public static AgentActions getInstance() {
		if (agentActions == null) {
			agentActions = new AgentActions();
		}
		return agentActions;
	}

	
	//can make this friendly move name,
	//alternatively return -1 if non-friendly
	//this could then be used to restart experiment...
	public static int move(int pos, int key) {
		int newPos = -1;
		logger.debug("Move action performed: " + key);
		CellGrid temp = cellGrid;
		if (gridView != null) {
			temp = gridView.getCellGrid();
		}
		// UP
		if (key == 1) {
			if (temp.getFinalizedMazeCells().contains(pos - mazeWidth))
				newPos = pos - mazeWidth;
		}
		// DOWN
		if (key == 2) {
			if (temp.getFinalizedMazeCells().contains(pos + mazeWidth))
				newPos = pos + mazeWidth;
		}
		// LEFT
		if (key == 3) {
			if (temp.getFinalizedMazeCells().contains(pos - 1) && (pos % mazeWidth != 0))
				newPos = pos - 1;
		}
		// RIGHT
		if (key == 4) {
			if (temp.getFinalizedMazeCells().contains(pos + 1) && (pos + 1) % mazeWidth != 0)
				newPos = pos + 1;
		}
		logger.debug("newPos="+newPos );
		return newPos > -1 ? newPos : pos;
	}
	
	
	public static void moveDirection(String direction) {
		
		logger.debug("moveDirection performed: " + direction);
		
		if (ParametersCalculator.getTimeSteps() < MAX_TIMESTEPS) {
			Agent singleAgent = new Agent();
			
			if (gridView != null) {
				singleAgent =  gridView.getCellGrid().getAgent();
			} else {
				singleAgent = cellGrid.getAgent();
			}
			
			int originalPosition = singleAgent.getPos();

			switch (direction) {

				case "UP":
					singleAgent.setPos(move(singleAgent.getPos(), ONE));
					break;
				case "DOWN":
					singleAgent.setPos(move(singleAgent.getPos(), TWO));
					break;
				case "LEFT":
					singleAgent.setPos(move(singleAgent.getPos(), THREE));
					break;
				case "RIGHT":
					singleAgent.setPos(move(singleAgent.getPos(), FOUR));
					break;
				default:
					logger.warn("IMPROPER DIRECTION SENT");
					System.exit(1);
					break;
			}
			
			if (gridView == null) {
				if (!cellGrid.isActiveInExperiment()) {
					if (logger.isDebugEnabled() ){
						logger.debug("Event Triggered: Move "+ direction);
					}
					RegisterEventCommand.getInstance().checkEvent(originalPosition, singleAgent.getPos());
					if (logger.isDebugEnabled() ){
						logger.debug("Event check-> pos: "+ singleAgent.getPos() );
					}
					if (gridView != null){
						gridView.repaint();
					}
				}
				cellGrid.setAgent(singleAgent);
				ParametersCalculator.updateParameters(cellGrid, singleAgent.getPos());
				ParametersCalculator.updateAgentHealthAndConsumeResources(cellGrid, true);
				
				
			} else {
				if (!gridView.getCellGrid().isActiveInExperiment()) {
					logger.debug("Event Triggered: Move "+ direction);
					RegisterEventCommand.getInstance().checkEvent(originalPosition, singleAgent.getPos());
					logger.debug("Event check-> pos: "+ singleAgent.getPos() );
					if (gridView != null){
						gridView.repaint();
					}
				}
				gridView.repaint();
				gridView.getCellGrid().setAgent(singleAgent);
				ParametersCalculator.updateParameters(gridView.getCellGrid(), singleAgent.getPos());
				ParametersCalculator.updateAgentHealthAndConsumeResources(cellGrid, true);
				

			}
			
		}

		else {
			endExperiment();
		}
		
	
	}
	
	
	public static void endExperiment() {
		
		logger.info("Experiment ending!");
		
		ParametersCalculator.displayParameters();
		
		if (gridView != null) {
			JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gridView);
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}

	private AgentActions() {
		//empty
	}
}
