package experiment_builder.controller;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;
import experiment_builder.input_action_map.InputActionMappable;
import lombok.extern.log4j.Log4j;
import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;
import experiment_builder.model.Reward;
import experiment_builder.view.BuildingMenuPanel;
import experiment_builder.view.CellGridPanel;
import experiment_builder.view.ExperimentRunnerFrame;
import experiment_builder.view.HumanButtonControlPanel;

@Log4j
public class BtnCreateController implements ActionListener {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( BtnCreateController.class );
	
	//private Agent agent;
	private HumanButtonControlPanel buttonPanel;
	private CellGrid cellGrid;
	private CellGridPanel gridView;
	private BuildingMenuPanel inputPanel;
	int mazeLength = 0;
	int mazeWidth = 0;
	
	InputActionMappable mapper; 
	

	public BtnCreateController(BuildingMenuPanel inputPanel, CellGridPanel gridView, HumanButtonControlPanel buttonPanel) {
		this.inputPanel = inputPanel;
		this.gridView = gridView;
		this.buttonPanel = buttonPanel;
		inputPanel.addBtnCreateListener(this);
		buttonPanel.addBtnCreateListener(this);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Create Grid") ) {
			if (!inputPanel.getRowInput().getText().equals("") || !inputPanel.getColInput().getText().equals("")) {
				int res[] = new int[2];
				res[0] = Integer.parseInt(inputPanel.getRowInput().getText());
				res[1] = Integer.parseInt(inputPanel.getColInput().getText());
				
				logger.debug("selected item "+ inputPanel.getSensorList().getSelectedItem().toString()   );
				
				cellGrid = new CellGrid(res[0], 
						res[1], 
						inputPanel.getSensorList().getSelectedItem().toString(),
						inputPanel.getActionMapperList().getSelectedItem().toString() );
				
				Rectangle rectangle[] = new Rectangle[res[0] * res[1]];
				int x = 0;
				int y = 0;
				int count = 0;
				int rowInc = (Constants.RIGHTPANELHEIGHT - 2 * Constants.ROWOFFSET) / res[0];
				int colInc = (Constants.RIGHTPANELWIDTH - 2 * Constants.COLOFFSET) / res[1];
				mazeLength = res[0];
				mazeWidth = res[1];
				AgentActions.setMazeLength(mazeLength);
				AgentActions.setMazeWidth(mazeWidth);
				AgentActions.setCellGrid(cellGrid);
				
				for (int i = 0; i < rectangle.length; i++) {
					rectangle[i] = new Rectangle(x, y, colInc, rowInc);
					x = x + colInc;
					count++;
					if (count % res[1] == 0) {
						count = 0;
						x = 0;
						y = y + rowInc;
					}

				}
				cellGrid.setBoundingRectangle(rectangle);
				gridView.setBoundingRectangle(rectangle);
				gridView.setCellGrid(cellGrid);
				gridView.mazeSetFlag = true;
				//gridView.setVisibility(cellGrid.getVisibility());
				gridView.repaint();

			}
		}
		if (e.getActionCommand().equals("Finalize Maze, Set Sensor, Action Map" ) ) {
			logger.info("Sensor saved!");
			gridView.getCellGrid().setSensorFromString( inputPanel.getSensorList().getSelectedItem().toString() );
			gridView.getCellGrid().setInputActionMapFromString( inputPanel.getActionMapperList().getSelectedItem().toString() );
			
			gridView.mazeFinalized = true;
			
			inputPanel.lockGrid();
			
			
		}

		if (e.getActionCommand().equals("Save XML") ) {
			String filePath = inputPanel.chooseSaveFile();
			Agent singleAgent = cellGrid.getAgent();
			XMLController.saveLayoutXML(cellGrid, filePath, singleAgent);
		}

		if (e.getActionCommand().equals("Load XML") ) {
			String filePath = inputPanel.chooseOpenFile();
			if (!filePath.equals("")) {
				int status = XMLController.loadLayoutXML(filePath, gridView);
				if (status == 0) {
					cellGrid = gridView.getCellGrid();
					//agent = cellGrid.getAgent();
					gridView.repaint();

				} else
					JOptionPane.showMessageDialog(inputPanel.getRootPane(), "XMl is not a GasNeat Project");
			}
		}

		if (e.getActionCommand().equals("Launch with UI : Manual Input")) {
			gridView.getCellGrid().setActiveInExperiment(true);
			ExperimentRunnerFrame mazeFrame = new ExperimentRunnerFrame(gridView, buttonPanel, true);
			log.info("Agent set :" + gridView.getCellGrid().getAgent() );
			RegisterEventCommand.getInstance().setEnvironment( cellGrid );
		}
		if (e.getActionCommand().equals("Launch with UI : File Input")) {
			gridView.getCellGrid().setActiveInExperiment(true);
			ExperimentRunnerFrame mazeFrame = new ExperimentRunnerFrame(gridView, buttonPanel, true);
			// FileController fileController = new FileController(mazeFrame,
			// gridView);
			RegisterEventCommand.getInstance().setEnvironment( cellGrid );
		}
		if (e.getActionCommand().equals("Add Reward")) {
			if (!inputPanel.getRewardValue().getText().equals("")) {
				String rewardType = inputPanel.getRewardType().getSelectedItem().toString();
				logger.debug(rewardType);
				double reward = Double.parseDouble(inputPanel.getRewardValue().getText());
				logger.debug(reward);
				int pos = gridView.getPos();
				Reward localReward = new Reward(rewardType, reward);
				// logger.debug(gr);
				cellGrid.getRewards().put(pos, localReward);
				gridView.setCellGrid(cellGrid);
				gridView.repaint();
			}
		}

		if (e.getActionCommand().equals("Clear Selection")) {
			gridView.clearSelection();
			gridView.mazeFinalized = false;
			inputPanel.unlockGrid();
			
			gridView.repaint();
		}

		if (e.getActionCommand().equals("Add Agent")) {
			int pos = gridView.getPos();
			if (gridView.getCellGrid().getVisibility()[pos]) {
				Agent agent = new Agent(pos);
				gridView.addAgent(agent);
				gridView.repaint();
			}
			inputPanel.getBtnSaveXML().setEnabled(true);
		}
		
		HashSet<String> directions = new HashSet<String>();
		directions.add("UP");
		directions.add("DOWN");
		directions.add("LEFT");
		directions.add("RIGHT");
		

		if (directions.contains( e.getActionCommand() ) ) {
			AgentActions.moveDirection(e.getActionCommand());

			AgentActions.updateInputDataPanel();
			
			//update sensor panel
			gridView.repaint();
		}
		

		if (e.getActionCommand().equals("EAT")) {
			AgentActions.eat();
			
			AgentActions.updateInputDataPanel();
			
			//update sensor panel
			gridView.repaint();
		}
		
		if (e.getActionCommand().equals("REST")) {
			AgentActions.rest();
			
			AgentActions.updateInputDataPanel();
			
			//update sensor panel
			gridView.repaint();
		}

		if (e.getActionCommand().equals("Save Cell Type")) {
			int cellNo = gridView.getSelectedRectangle();
			logger.debug("Attempting to [Save Cell TYPE] on Cell No :"+cellNo);
			HashSet<String> cellProperties = new HashSet<String>();
			if (!inputPanel.getCellType().isSelectionEmpty()) {
				cellProperties.addAll((ArrayList<String>) inputPanel.getCellType().getSelectedValuesList());
				logger.debug("Cell No :"+cellNo + " cell type set to be " + inputPanel.getCellType().getSelectedValuesList());
				if (!cellGrid.getCellProperties().containsKey(cellNo))
					cellGrid.getCellProperties().put(cellNo, cellProperties);
			}

		}
		if (e.getActionCommand().equals("Save Cell Event")) {
			int cellNo = gridView.getSelectedRectangle();
			logger.debug("Attempting to [Save Cell EVENT] on Cell No :"+cellNo);
			HashSet<String> cellProperties = new HashSet<String>();
			if (!inputPanel.getCellEvent().isSelectionEmpty()) {
				cellProperties.addAll((ArrayList<String>) inputPanel.getCellEvent().getSelectedValuesList());
				if (cellGrid.getCellProperties().containsKey(cellNo)
						&& cellGrid.getCellProperties().get(cellNo).contains("Home")) {
					JOptionPane.showMessageDialog(inputPanel.getRootPane(), "Cannot Assign Event to Home Cell");
				} else {
					logger.debug("Cell No :"+cellNo + " cell event set to be " + inputPanel.getCellEvent().getSelectedValuesList() );
				}
				cellGrid.getCellEvents().put(cellNo, cellProperties);
			}

		}

		if (e.getActionCommand().equals("Test")) {
			for (Integer i : cellGrid.getCellProperties().keySet()) {
				logger.debug("Row :" + i + " , val : " + cellGrid.getCellProperties().get(i).toString());
			}
			for (Integer i : cellGrid.getCellEvents().keySet()) {
				logger.debug("Row :" + i + " , val : " + cellGrid.getCellEvents().get(i).toString());
			}
		}
		
		
		
		
		if (e.getActionCommand().equals("EXECUTE")) {
			double outputLevel = Double.parseDouble(buttonPanel.getOutputActivationTextField().getText() ); 
			
			if (mapper == null) {
				mapper = cellGrid.getActionMap();
			}
			
			logger.debug("Output level "+ outputLevel+ "mapping to action according to " + mapper.getClass().toString()  );
			
			logger.trace("pre EXECUTE agent's orientation is "+  cellGrid.getAgent().getOrientation()  );
			
			mapper.actFromDoubleValue(  outputLevel );
			AgentActions.updateInputDataPanel();
			
			logger.trace("post EXECUTE agent's orientation is "+  cellGrid.getAgent().getOrientation()  );
			
			//update sensor panel
			gridView.repaint();
		}
		
		
		
		
		
		
		
	}

}
