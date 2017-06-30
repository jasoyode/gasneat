package gasNEAT.command.functions;

import gasNEAT.view.Constants.ActivationThresholdFunction;

/**
 * Class for storing properties of activation function
 *
 */
public abstract class GasNeatActivationFunction {
	protected double currentActivationLevel = 0.0;
	protected double activationThreshold = 0.0;
	protected final ActivationThresholdFunction activationFunction;
	
	/**
	 * @param activationFunction Activation Function
	 */
	public GasNeatActivationFunction(ActivationThresholdFunction activationFunction) {
		this.activationFunction = activationFunction;
	}
	
	/**
	 * @param activationLevel
	 * @param threshold
	 */
	public void setActivationLevelAndThreshold(double activationLevel, double threshold) {
		this.currentActivationLevel = activationLevel;
		this.activationThreshold = threshold;
	}
	
	/**
	 * @return Maximum value of the function
	 */
	public double getMaxOutputValue() {
		double temp = this.currentActivationLevel;
		this.currentActivationLevel = Integer.MAX_VALUE;
		double output = this.calculateCurrentOutput();
		this.currentActivationLevel = temp;
		
		return output;
	}
	
	/**
	 * @return Minimum value of the function
	 */
	public double getMinOutputValue() {
		double temp = this.currentActivationLevel;
		this.currentActivationLevel = 0;
		double output = this.calculateCurrentOutput();
		this.currentActivationLevel = temp;
		
		return output;
	}
	
	public abstract double calculateCurrentOutput();
	
	/**
	 * @return Activation Function
	 */
	public ActivationThresholdFunction getActivationFunction() {
		return this.activationFunction;
	}
}
