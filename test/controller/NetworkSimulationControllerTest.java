package controller;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gasNEAT.controller.NetworkSimulationController;
import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.view.Constants.NetworkType;
import gasNEAT.view.FileInputFrame;
import gasNEAT.view.ViewConstants;


public class NetworkSimulationControllerTest {
	
	
	NetworkSimulationController networkSimulationController;
	GasNeatNeuralNetwork neuralNetwork;
	FileInputFrame fileInputScreen;
	Map<Integer, List<Integer>> inputTimeSignalMap;

	@Before
	public void setUp() {
		fileInputScreen = mock(FileInputFrame.class); 
		neuralNetwork = mock(GasNeatNeuralNetwork.class);
		// create the test inputTimeSignalMap, the 
		inputTimeSignalMap = new HashMap<Integer, List<Integer>>();
		List<Integer> value = new ArrayList();
		value.add(0, 0);
		inputTimeSignalMap.put(0, value);
		List<Integer> value2 = new ArrayList();
		value2.add(0, 1);
		inputTimeSignalMap.put(0, value2);
		List<Integer> value3 = new ArrayList();
		value.add(1, 0);
		inputTimeSignalMap.put(0, value3);
		List<Integer> value4 = new ArrayList();
		value.add(1, 1);
		inputTimeSignalMap.put(0, value4);
		networkSimulationController = new NetworkSimulationController(neuralNetwork, ViewConstants.RUN_TYPE_GUI);
	}
	@Test
	public void simulateNetworkTest() {
		when(neuralNetwork.getNetworkType()).thenReturn(NetworkType.FEEDFORWARD);
		networkSimulationController.setFileName("data/XORNetwork.xlsx");
//		networkSimulationController.simulateNetwork(neuralNetwork, inputTimeSignalMap);
//		verify(neuralNetwork).getNetworkType();
		//verify(fileInputScreen).addSimulateActionListener(Action);
	}

	
}
