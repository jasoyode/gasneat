package gasNEAT.command.functions;

import gasNEAT.view.Constants;

/**
 * Performs leaner operation as activation function
 *
 */
public class LinearActivationFunction extends GasNeatActivationFunction {
	
	public LinearActivationFunction() {
		super(Constants.ActivationThresholdFunction.LINEAR);
	}

	@Override
	public double calculateCurrentOutput() {
		return (this.currentActivationLevel - this.activationThreshold) ;
	}
}
