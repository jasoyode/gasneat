package gasNEAT.view.networkView;

import java.awt.Graphics2D;

import gasNEAT.model.GasNeatNeuralNetwork;

public interface NetworkViewInterface {
	public void updateNeuralNetworkPanel(String currentMode, GasNeatNeuralNetwork neuralNetwork);
	public void createNeuralNetworkPanel();
	public void drawGasLegend(Graphics2D g2d);
	public void modButtonStatus(Boolean status, String buttonName);
	public void setEnabled(boolean isEnabled);
}
