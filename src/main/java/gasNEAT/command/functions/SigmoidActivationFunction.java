package gasNEAT.command.functions;

import gasNEAT.view.Constants;

/**
 * Performs sigmoid operation as activation function
 *
 */
public class SigmoidActivationFunction extends GasNeatActivationFunction {
	
	private double LOGARITHMIC_SIGMOID_SLOPE = 5.0;
	
	
	public SigmoidActivationFunction() {
		super(Constants.ActivationThresholdFunction.LOGARITHMIC_SIGMOID);
	}
	


	@Override
	public double calculateCurrentOutput() {
		return 1.0 / (1.0 + Math.exp(-1.0 * LOGARITHMIC_SIGMOID_SLOPE * (this.currentActivationLevel - this.activationThreshold)));
	}
}
