package experiment_builder.view;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;

@SuppressWarnings("serial")
public class ExperimentBuilderFrame extends JFrame {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( ExperimentBuilderFrame.class );

	private CellGridPanel gridView;
	private ExperimentBuilderPanel mazeCreator;

	public ExperimentBuilderFrame(ExperimentBuilderPanel mazeCreator, CellGridPanel gridView) {

		this.mazeCreator = mazeCreator;
		this.gridView = gridView;
		init();
	}

	public void init() {
		// Setting parameters related to frame
		this.setBounds(100, 20, Constants.MAINFRAMEWIDTH, Constants.MAINFRAMEHEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Experiment Builder");
		this.getContentPane().setLayout(null);
		this.add(mazeCreator);
		this.add(gridView);
		this.setVisible(true);
		this.repaint();
	}

}
