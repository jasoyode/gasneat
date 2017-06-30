package gasNEAT.command.functions;

import gasNEAT.view.Constants;

/**
 * Class for applying no activation function to a neuron
 *
 */
public class NullActivationFunction extends GasNeatActivationFunction {

	public NullActivationFunction() {
		super( Constants.ActivationThresholdFunction.NULL_FUNCTION);
	}

	@Override
	public double calculateCurrentOutput() {
		return 0;
	}

}
