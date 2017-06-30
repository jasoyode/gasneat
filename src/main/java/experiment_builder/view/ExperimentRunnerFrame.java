package experiment_builder.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;
import experiment_builder.controller.AgentActions;
import lombok.Getter;

@SuppressWarnings({ "serial", "unused" })
public class ExperimentRunnerFrame extends JFrame {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( ExperimentRunnerFrame.class );

	private HumanButtonControlPanel buttonPanel;
	private GridBagConstraints gbc;
	private GridBagLayout gbl;
	private CellGridPanel gridView;
	private @Getter InputDataPanel inputDataPanel;
	private @Getter OutputDataPanel outputDataPanel;

	public ExperimentRunnerFrame(CellGridPanel gridView, HumanButtonControlPanel buttonPanel, boolean vis) {
		
		logger.warn("ExperimentRunnerFrame:" + vis);
		//System.exit(1);
		
		// panels
		this.gridView = gridView;
		this.buttonPanel = buttonPanel;
		inputDataPanel = new InputDataPanel();
		
		AgentActions.setInputDataPanel( inputDataPanel );
		
		outputDataPanel = new OutputDataPanel( gridView.getCellGrid().getActionMap() ); 

		this.setBounds(0, 0, Constants.MAZEFRAMEWIDTH, Constants.MAZEFRAMEHEIGHT);
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setDefaultCloseOperation(performExitAction());
		this.setTitle("Experiment Runner");

		// layout settings
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;
		gbc.insets = new Insets(5, 5, 5, 5);
		this.getContentPane().setLayout(gbl);

		//int width = 1200; //2 * Constants.MAZEFRAMEWIDTH / 3
		
		
		// setting properties
		gridView.setPreferredSize(new Dimension(2 * Constants.MAZEFRAMEWIDTH / 3, Constants.MAZEFRAMEHEIGHT));
		
		//width = 20; //Constants.MAZEFRAMEWIDTH / 8
		
		
		buttonPanel.setPreferredSize(new Dimension(Constants.MAZEFRAMEWIDTH / 8, Constants.MAZEFRAMEHEIGHT / 2));
		inputDataPanel.setPreferredSize(new Dimension(Constants.MAZEFRAMEWIDTH / 8, Constants.MAZEFRAMEHEIGHT / 4));
		outputDataPanel.setPreferredSize(new Dimension(Constants.MAZEFRAMEWIDTH / 8, Constants.MAZEFRAMEHEIGHT / 4));

		this.getContentPane().add(buttonPanel, gbc);
		gbc.gridheight = 3;
		gbc.gridx++;
		this.getContentPane().add(gridView, gbc);
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy++;
		this.getContentPane().add(inputDataPanel, gbc);
		gbc.gridy++;
		this.getContentPane().add(outputDataPanel, gbc);

		// .setPreferredSize(new Dimension(Constants.mainFrameWidth / 4,
		// Constants.mainFrameHeight / 8));
		// buttonPanel.setBackground(new Color(223, 247, 168));
		this.pack();
		this.setVisible( vis );
		this.repaint();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public int performExitAction() {
		gridView.getCellGrid().setActiveInExperiment(false);
		return WindowConstants.DISPOSE_ON_CLOSE;
	}
}
