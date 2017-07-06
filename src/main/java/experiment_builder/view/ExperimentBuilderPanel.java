package experiment_builder.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;
import lombok.Getter;

/*This class is the left panel in the view where you set all the parameters, view the output, buttons. 
 * This panel contain 3 sub panels: Input, output, buttons*/

public class ExperimentBuilderPanel extends JPanel {
	
	private static Logger logger = Logger.getLogger( ExperimentBuilderPanel.class );

	private @Getter HumanButtonControlPanel buttonPanel = new HumanButtonControlPanel();
	private @Getter BuildingMenuPanel inputPanel = new BuildingMenuPanel();
	private @Getter TitledBorder inputPanelTitle;

	public ExperimentBuilderPanel() {

		init();
	}

	public void init() {

		this.setBounds(0, 0, Constants.MAINFRAMEWIDTH / 3, Constants.MAINFRAMEHEIGHT);
		// these titles can be changed later
		inputPanelTitle = BorderFactory.createTitledBorder("Inputs");
		inputPanel.setPreferredSize(new Dimension(Constants.MAINFRAMEWIDTH / 3, Constants.MAINFRAMEHEIGHT));
		inputPanel.setBackground(new Color(200, 200, 220));
		inputPanel.setBorder(inputPanelTitle);
		this.add(inputPanel);

		this.setVisible(true);
		this.repaint();

	}
}
