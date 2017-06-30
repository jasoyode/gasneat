package gasNEAT.view.networkView;

import java.awt.Graphics2D;

import gasNEAT.model.GasNeatNeuralNetwork;

public class NullNetworkViewFrame implements NetworkViewInterface {
	
	public NullNetworkViewFrame() { }

	@Override
	public void updateNeuralNetworkPanel(String currentMode, GasNeatNeuralNetwork neuralNetwork) { }

	@Override
	public void createNeuralNetworkPanel() { }

	@Override
	public void drawGasLegend(Graphics2D g2d) { }

	@Override
	public void modButtonStatus(Boolean status, String buttonName) { }

	@Override
	public void setEnabled(boolean isEnabled) { }
	
}
