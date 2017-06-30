package experiment_builder.controller;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.view.CellGridPanel;
import experiment_builder.view.ExperimentBuilderFrame;
import experiment_builder.view.ExperimentBuilderPanel;

public class ExperimentBuilder {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( ExperimentBuilder.class );
	
	private AgentActions agentActions;
	private BtnCreateController btnCreateController;
	private CellGridPanel gridView;
	private ExperimentBuilderFrame mainFrame;
	private ExperimentBuilderPanel experimentBuilderPanel;
	private NonGuiListener nonGuiListener;
	private ParametersCalculator parametersCalculator;

	// private MouseListenerController mouseListenerController;
	public ExperimentBuilder() {
		experimentBuilderPanel = new ExperimentBuilderPanel();
		gridView = new CellGridPanel();
		btnCreateController = new BtnCreateController(experimentBuilderPanel.getInputPanel(), gridView,
				experimentBuilderPanel.getButtonPanel());
		mainFrame = new ExperimentBuilderFrame(experimentBuilderPanel, gridView);
		agentActions = AgentActions.getInstance();
		agentActions.setGridView(gridView);
		nonGuiListener = new NonGuiListener(experimentBuilderPanel.getInputPanel(), gridView);
		parametersCalculator = new ParametersCalculator();
		// mouseListenerController = new MouseListenerController(gridView);
	}

}
