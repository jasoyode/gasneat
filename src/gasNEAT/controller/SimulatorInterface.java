package gasNEAT.controller;

/**
 * Interface to various neural networks' simulators
 *
 */
public interface SimulatorInterface {
	/**Performs a step forward in evolution*/
	public void backStep();	
	/**Replays the evolution in simulator*/
	public void replay();
	public void recursivePlayPause();
	/**Performs a step forward in evolution*/
	public void forwardStep();
	public void periodSlideChanger(int sliderValue);
	/**Performs the simulation of evolution*/
	public void simulate();
}
