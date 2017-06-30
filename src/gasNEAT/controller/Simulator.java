package gasNEAT.controller;

import java.util.ArrayList;

import javax.swing.Timer;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.model.NetworkState;
import gasNEAT.view.ViewConstants;
import gasNEAT.view.networkView.NetworkViewFrame;
import gasNEAT.view.networkView.NetworkViewInterface;
import gasNEAT.view.networkView.NullNetworkViewFrame;

/** This will be used to remove duplicate code between Recurrent Simulator and Feed Forward Simulator 
*/
public abstract class Simulator implements SimulatorInterface {
	protected GasNeatNeuralNetwork neuralNetwork;
	protected boolean isReplay;
	protected int replayIndex;
	protected Timer timer;
	
	protected NetworkViewInterface neuralNetworkFrame;
	//protected NeuralNetwork neuralNetwork;
	
	// Tracks whether the visualization is playing through already-simulated time steps  
	protected boolean playAfterBackStepping = false;
	
	//represents the index of the network state list while back stepping
	protected int backstepIndex = -1;
	
	// list of network state at each time instant
	protected ArrayList<NetworkState> networkStateList;
	protected final String runType;
	/** logger instance */
	private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(Simulator.class);
	
	/**
	 * @param runType
	 */
	protected Simulator(String runType) {
		this.runType = runType;
	}

	/**
	 * initializing view components
	 */
	public void initPanel(String type) {
		if(type.equals(ViewConstants.RUN_TYPE_GUI)) {
			this.neuralNetworkFrame = new NetworkViewFrame(neuralNetwork, this);
			this.neuralNetworkFrame.modButtonStatus(false, ViewConstants.REPLAY_TEXT);
		} else if(type.equals(ViewConstants.RUN_TYPE_HEADLESS)) {
			this.neuralNetworkFrame = new NullNetworkViewFrame();
		}
	}
	
	public void logBeginning(String name) {
		this.LOG.info(name + " beginning!");
	}
	
	/* (non-Javadoc)
	 * @see gasNEAT.controller.SimulatorInterface#replay()
	 */
	@Override
	public void replay() {
		if (timer.isRunning())
			timer.stop();
		else
			timer.start();

		isReplay = true;
		replayIndex = 0;
	}
	
	/* (non-Javadoc)
	 * @see gasNEAT.controller.SimulatorInterface#backStep()
	 */
	@Override
	public void backStep() {
		if (backstepIndex == 0) {
			return;
		}
		if (timer.isRunning())
			timer.stop();
		NetworkState currentState = networkStateList.get(--backstepIndex);
		neuralNetworkFrame.updateNeuralNetworkPanel(ViewConstants.BACKSTEP_TEXT,currentState.getNeuralNetwork());
	}
	
	/* (non-Javadoc)
	 * @see gasNEAT.controller.SimulatorInterface#periodSlideChanger(int)
	 */
	@Override
	public void periodSlideChanger(int delay) {
		if (delay == 0) {
			timer.stop();
		} else {
			if (!timer.isRunning())
				timer.start();
				timer.setDelay(delay);
		}
	}
	
	/* (non-Javadoc)
	 * @see gasNEAT.controller.SimulatorInterface#recursivePlayPause()
	 */
	@Override
	public void recursivePlayPause() {
		if (timer.isRunning()) {
			timer.stop();
			neuralNetworkFrame.setEnabled(true);
		} else {
			if (backstepIndex < (networkStateList.size() - 1)) {
				playAfterBackStepping = true;
			}
			timer.start();
		}
	}
}
