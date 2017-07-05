package gasNEAT.view.networkView;

import static gasNEAT.view.Constants.BUTTON_PANEL_HEIGHT;
import static gasNEAT.view.Constants.GAS_LEGEND_BOUNDARY_THICKNESS;
import static gasNEAT.view.Constants.GAS_LEGEND_SWATCH_HEIGHT;
import static gasNEAT.view.Constants.GAS_LEGEND_SWATCH_WIDTH;
import static gasNEAT.view.Constants.SIMULATION_FRAME_HEIGHT;
import static gasNEAT.view.Constants.SIMULATION_FRAME_WIDTH;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import gasNEAT.controller.SimulatorInterface;
import gasNEAT.model.Gas;
import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.model.GasNeatSynapse;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.view.Constants;
import gasNEAT.view.EdgeArtist;
import gasNEAT.view.GasRingArtist;
import gasNEAT.view.NodeArtist;
import gasNEAT.view.ViewConstants;

/**
 * RecurrentNeuralNetworkFrame class is responsible for setting up the frame for
 * simulation of a recursive neural network.
 *
 */
@SuppressWarnings("serial")
public class NetworkViewFrame extends JFrame implements ActionListener, ChangeListener, NetworkViewInterface {
	//Parent panel to add children panels.
	private JPanel outerPanel;
	// Panel to display the simulation.
	private JPanel neuralNetworkPanel;
	// Label to display the status of the simulation.
	private JLabel statusLabel = new JLabel();
	private JPanel controlButtonsPanel;
	
	// Buttons
	private JButton playPauseButton = new JButton(ViewConstants.PAUSE_TEXT);
	private JButton backstepButton = new JButton(ViewConstants.BACKSTEP_TEXT);
	private JButton forwardstepButton = new JButton(ViewConstants.FORWARDSTEP_TEXT);
	private JButton replayButton = new JButton(ViewConstants.REPLAY_TEXT);
	private final JButton[] buttons = {playPauseButton, backstepButton, forwardstepButton, replayButton};

	// Slider things
	private JSlider periodSlider = new JSlider(Constants.MIN_TIMER_DELAY, Constants.MAX_TIMER_DELAY);
	private JLabel sliderLabel = new JLabel("Timer Slider(in ms): ");
	
	// references
	private GasNeatNeuralNetwork neuralNetwork;
	private EdgeArtist edgeArtist;
	private GasRingArtist gasRingArtist;
	private NodeArtist nodeArtist;
	private SimulatorInterface simulator;
	
	private String currentMode;


	/** Setting Logger object of Log4j */
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(NetworkViewFrame.class);

	/**
	 * Constructor: Gets called whenever an object of the class gets
	 * instantiated. Sets the properties of frames and all the private fields of
	 * the class
	 * 
	 * @param neuralNetwork
	 *            the neuralNetwork object which is to be simulated
	 */
	public NetworkViewFrame(GasNeatNeuralNetwork neuralNetwork, SimulatorInterface simulator) {
		this.simulator = simulator;
		setSize(new Dimension(SIMULATION_FRAME_WIDTH, SIMULATION_FRAME_HEIGHT));
		setLocationRelativeTo(null);
		outerPanel = new JPanel();
		outerPanel.setLayout(new BorderLayout());
		this.neuralNetwork = neuralNetwork;

		// setting currentMode to play by default
		this.currentMode = ViewConstants.PLAY_STATUS_TEXT;
		createNeuralNetworkPanel();
		outerPanel.add(neuralNetworkPanel);
		createButtonsAndStatusPanels();
		org.apache.logging.log4j.core.config.Configurator.initialize(ViewConstants.LOG4J_TEXT,ViewConstants.LOG4J_FILE);

		add(outerPanel);
		setResizable(false);
		setVisible(true);
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		edgeArtist = new EdgeArtist(neuralNetwork, neuralNetworkPanel);
		gasRingArtist = new GasRingArtist(neuralNetwork, neuralNetworkPanel);
		nodeArtist = new NodeArtist(neuralNetwork, neuralNetworkPanel);
	}

	/**
	 * Method to update network simulation based on inputs from engine class at
	 * each tick of the timer. Updates simulation based on each timer tick.
	 * 
	 * @param currentMode
	 *            a string which can be one of play, replay, backstep or
	 *            forwardstep based on the user interaction with the buttons.
	 * @param neuralNetwork
	 *            the object of neuralNetwork class which contains all the
	 *            activated neurons, synapses and gas dispersion information.
	 */
	public void updateNeuralNetworkPanel(String currentMode, GasNeatNeuralNetwork neuralNetwork) {
		this.currentMode = currentMode;
		this.neuralNetwork = neuralNetwork;
		setCurrentStatus();
		//System.out.println("UPDATE CALLED");
		repaint();
	}

	/**
	 * Setter method for text for status label panel.
	 * 
	 * <p>
	 * Sets the current status of the simulation to mode:
	 * <ul>
	 * <li>play - Normal simulation based on timer tick.
	 * <li>replay - Replay the simulation after the simulation completes. Also
	 * based on timer tick.
	 * <li>backstep - Stepping back in the simulation on button click. Pressing
	 * play thereafter resumes the simulation from that point.
	 * <li>forwardstep - Stepping forward in the simulation in button click.
	 * Pressing play thereafter resumes the simulation from that point.
	 * </ul>
	 */
	private void setCurrentStatus() {
		String text = ViewConstants.STATUS_TEXT_MAPPING.get(currentMode);
		if(text == null) {
			text = ViewConstants.NULL_STATUS;
		}
		statusLabel.setText(text);
	}

	/**
	 * Method to assign properties to the timer slider.
	 */
	private void createTimerSlider() {
		periodSlider.setMajorTickSpacing(ViewConstants.STANDARD_MAJOR_SPACING);
		periodSlider.setMinorTickSpacing(ViewConstants.STANDARD_MINOR_SPACING);
		periodSlider.setPaintTicks(true);
		periodSlider.setPaintLabels(true);
		periodSlider.setSnapToTicks(true);
		this.periodSlider.addChangeListener(this);
	}

	/**
	 * Method to create control panels and add components inside the panels.
	 * 
	 * <p>
	 * It creates the following panels:
	 * <ul>
	 * <li>controlPanel - Panel which encloses all the following panels.
	 * <li>timerSliderPanel - Panel which contains the timer slider.
	 * <li>statusPanel - Panel which contains the current simulation status
	 * label.
	 * <li>controlButtonsPanel - Panel which contains all the simulation control
	 * buttons.
	 * </ul>
	 * 
	 */
	private void createButtonsAndStatusPanels() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

		JPanel timerSliderPanel = new JPanel();
		timerSliderPanel.setBorder(new LineBorder(Color.BLACK));
		createTimerSlider();
		timerSliderPanel.add(sliderLabel);
		timerSliderPanel.add(periodSlider);
		controlPanel.add(timerSliderPanel);

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new LineBorder(Color.BLACK));
		setCurrentStatus();
		statusPanel.add(statusLabel);
		controlPanel.add(statusPanel);

		this.controlButtonsPanel = new JPanel();
		controlButtonsPanel.setBorder(new LineBorder(Color.BLACK));
		for(JButton button : this.buttons) {
			this.registerButton(button);
		}
		controlPanel.add(controlButtonsPanel);

		outerPanel.add(controlPanel, BorderLayout.SOUTH);
	}
	
	private void registerButton(JButton newButton) {
		newButton.addActionListener(this);
		this.controlButtonsPanel.add(newButton);
	}

	/**
	 * Method to create panel for network simulation. This panel has a paint
	 * component which gets called on each timer tick and paints the simulation
	 * on the frame. It also draws the gas legend in the bottom right corner of
	 * the frame.
	 */
	public void createNeuralNetworkPanel() {

		this.neuralNetworkPanel = new JPanel() {

		@SuppressWarnings("rawtypes")
		@Override
		public void paintComponent(Graphics g) {
			
			boolean isNetworkNull = neuralNetwork == null;
			Graphics2D g2d = (Graphics2D) g;
			

			//#GASNEATVISUAL
			g2d.setColor(Color.BLACK);
			if(!isNetworkNull) {
				
				//Must do gasrings before everything else so that
				//they do not hide the other elements
				Set neuronMapSet = neuralNetwork.getNeuronMap().entrySet();
				Iterator neuronIterator = neuronMapSet.iterator();

				while (neuronIterator.hasNext()) {
					Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
					GasNeatNeuron currentNeuron = (GasNeatNeuron) currentEntry.getValue();
					if (currentNeuron.isGasEmitter() ) {
						gasRingArtist.drawGasRings(currentNeuron, g2d);
					}
				}
				
				
				
				
				
				neuronIterator = neuronMapSet.iterator();
				while (neuronIterator.hasNext()) {
					Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
					GasNeatNeuron currentNeuron = (GasNeatNeuron) currentEntry.getValue();
					
					//System.out.println(  "Drawling " + currentNeuron.getNeuronID() +" "+ currentNeuron.calculateActivation()  );
					
					nodeArtist.drawNode(currentNeuron, g2d);
				}
				
				
				
				Set synapseMapSet = neuralNetwork.getSynapseMap().entrySet();
				Iterator synapseIterator = synapseMapSet.iterator();
				while (synapseIterator.hasNext()) {
					Map.Entry currentEntry = (Map.Entry) synapseIterator.next();
					GasNeatSynapse currentSynapse = (GasNeatSynapse) currentEntry.getValue();
					
					Color edgeColor = Color.GRAY;
					String outputGas = neuralNetwork.getNeuronMap().get( currentSynapse.getSourceNeuron() ).getSynapseProductionType();
					
					//System.out.println(outputGas );
					
					if ( outputGas.equals("G0") ) {
						
					} else if (outputGas.equals("G1") && currentSynapse.getSynapticWeight() >= 0.0 ) {
						edgeColor = ViewConstants.POSITIVE_SYNAPTIC_MODULATION;
					} else if (outputGas.equals("G2") && currentSynapse.getSynapticWeight() < 0.0 ) {
						edgeColor = ViewConstants.POSITIVE_SYNAPTIC_MODULATION;
					} else if (outputGas.equals("G2") && currentSynapse.getSynapticWeight() >= 0.0 ) {
						edgeColor = ViewConstants.NEGATIVE_SYNAPTIC_MODULATION;
					} else if (outputGas.equals("G1") && currentSynapse.getSynapticWeight() < 0.0 ) {
						edgeColor = ViewConstants.NEGATIVE_SYNAPTIC_MODULATION;
					} else if (outputGas.equals("G3") && currentSynapse.getSynapticWeight() >= 0.0 ) {
						edgeColor = ViewConstants.POSITIVE_ACTIVATION_MODULATION;
					} else if (outputGas.equals("G4") && currentSynapse.getSynapticWeight() < 0.0 ) {
						edgeColor = ViewConstants.POSITIVE_ACTIVATION_MODULATION;
					} else if (outputGas.equals("G4") && currentSynapse.getSynapticWeight() >= 0.0 ) {
						edgeColor = ViewConstants.NEGATIVE_ACTIVATION_MODULATION;
					} else if (outputGas.equals("G3") && currentSynapse.getSynapticWeight() < 0.0 ) {
						edgeColor = ViewConstants.NEGATIVE_ACTIVATION_MODULATION;
					} else {
						System.out.println("ERROR!!!!!! this should not happen");
						System.exit(1);
					}
					
					edgeArtist.drawEdge(currentSynapse, edgeColor, g2d);
				}


				
				
				
				if (!isNetworkNull && neuralNetwork.getGasMap().size() > 0) {
					drawGasLegend(g2d);
				}
				
			}
		}
	};
	}

	/**
	 * Method to draw a gas legend on the bottom right corner above the control
	 * panel. Different gases are represented by different colors which are
	 * taken in as input with the gas parameters.
	 * 
	 * @param g2d
	 *            Graphics2D parameter to draw using basic graphics functions
	 *            like setStroke and setColor
	 */
	public void drawGasLegend(Graphics2D g2d) {
		int gasListSize = neuralNetwork.getGasMap().size();
		g2d.setStroke(new BasicStroke(GAS_LEGEND_BOUNDARY_THICKNESS));
		g2d.setColor(Color.BLACK);
		int legendWidth = 190 + GAS_LEGEND_SWATCH_WIDTH;
		int legendHeight = 40 + (GAS_LEGEND_SWATCH_HEIGHT + 10) * gasListSize;
		int legendX = SIMULATION_FRAME_WIDTH - (legendWidth + 20);
		int legendY = SIMULATION_FRAME_HEIGHT - (legendHeight + 30 + BUTTON_PANEL_HEIGHT);

		g2d.drawRoundRect(legendX, legendY, legendWidth, legendHeight, 10, 10);
		int index = 0;
		int x;
		int y;

		for (Gas gas : neuralNetwork.getGasMap().values()) {
			// draw color patch
			x = legendX + 10;
			y = legendY + 40 * index;
			g2d.setColor(gas.getColor());
			g2d.fillRoundRect(x, y, GAS_LEGEND_SWATCH_WIDTH,
					GAS_LEGEND_SWATCH_HEIGHT, 3, 3);
			// draw gas name label
			x += 10 + GAS_LEGEND_SWATCH_WIDTH;
			g2d.setColor(Color.BLACK);
			g2d.drawString(gas.getName(), x + 16, y + 16); // 16 approximates
															// the text height
			index++;
		}

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == this.playPauseButton) {
			if(this.playPauseButton.getText().equals(ViewConstants.PLAY_TEXT)) {
				this.playPauseButton.setText(ViewConstants.PAUSE_TEXT);
			} else {
				this.playPauseButton.setText(ViewConstants.PLAY_TEXT);
				this.replayButton.setEnabled(true);
			}
			this.backstepButton.setEnabled(true);
			this.simulator.recursivePlayPause();
		} else if(event.getSource() == this.backstepButton) {
			this.playPauseButton.setText(ViewConstants.PLAY_TEXT);
			this.playPauseButton.setEnabled(true);
			this.replayButton.setEnabled(false);
			this.forwardstepButton.setEnabled(true);
			this.simulator.backStep();
		} else if(event.getSource() == this.forwardstepButton) {
			this.playPauseButton.setText(ViewConstants.PLAY_TEXT);
			this.playPauseButton.setEnabled(true);
			this.replayButton.setEnabled(false);
			this.backstepButton.setEnabled(true);
			this.simulator.forwardStep();
		} else if(event.getSource() == this.replayButton) {
			this.simulator.replay();
		}
	}

	@Override
	public void stateChanged(ChangeEvent source) {
		if(source.getSource() == this.periodSlider && !this.periodSlider.getValueIsAdjusting()) {
			this.simulator.periodSlideChanger(this.periodSlider.getValue());
		}
	}
	
	public void modButtonStatus(Boolean status, String buttonName) {
		if(buttonName.equals(ViewConstants.PAUSE_TEXT) || buttonName.equals(ViewConstants.PLAY_TEXT)) {
			this.playPauseButton.setEnabled(status);
		} else if(buttonName.equals(ViewConstants.REPLAY_TEXT)) {
			this.replayButton.setEnabled(status);
		} else if(buttonName.equals(ViewConstants.FORWARDSTEP_TEXT)) {
			this.forwardstepButton.setEnabled(status);
		} else if(buttonName.equals(ViewConstants.BACKSTEP_TEXT)) {
			this.backstepButton.setEnabled(status);
		}
	}
}
	
