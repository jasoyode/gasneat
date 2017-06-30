package gasNEAT.command.functions;

import gasNEAT.view.Constants;

/**
 * Performs step operation as activation function
 *
 */
public class StepActivationFunction extends GasNeatActivationFunction {
	
	public StepActivationFunction() {
		super(Constants.ActivationThresholdFunction.STEP_FUNCTION);
	}

	@Override
	public double calculateCurrentOutput() {
		if (this.currentActivationLevel - this.activationThreshold >= 0) {
			return 1;
		} else {
			return 0;			
		}
	}

}
