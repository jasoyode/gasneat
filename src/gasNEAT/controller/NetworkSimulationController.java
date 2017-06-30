package gasNEAT.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.view.Constants.NetworkType;

/**
 * Controller for neural network simulator
 *
 */
public class NetworkSimulationController {
	/** Reference variable for Input File name */
	private String fileName="";
	/** Reference variable for NeuralNetwork object */
	private GasNeatNeuralNetwork neuralNetwork;
	
	/** Reference variable for RecurrentSimulator */
	private RecurrentSimulator recurrentSimulator;
	/** LOG4J File name */
	private static final String LOG4J_FILE = "log4j2.xml";
	/** Setting Logger object for LOG4J */
	private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(NetworkSimulationController.class);
	private final String runType;
	
	/***
	 * Contructs a NetworkSimulationController
	 * 
	 * @param neuralNetwork
	 *            NeuralNetwork reference variable
	 */
	public NetworkSimulationController(GasNeatNeuralNetwork neuralNetwork, String runType) {
		this.runType = runType;
		this.neuralNetwork = neuralNetwork;
		org.apache.logging.log4j.core.config.Configurator.initialize("test", LOG4J_FILE);

	}

	
	/**
	 * Evolves neural network whose configuration is stored on filename
	 * @param fileName
	 */
	public void evolve(String fileName) {
			//new NetworkEvolver(fileName, this.runType);
			System.out.println("Evolution not linked up to ANJI interface yet!");
			System.exit(1);
	}

	/**
	 * Method to start the simulation of Neural Network
	 * 
	 * @param neuralNetwork Neural Network object
	 * @param inputTimeSignalMap Input time signal map
	 */
	public void simulateNetwork(GasNeatNeuralNetwork neuralNetwork, Map<Integer, List<Double>> inputTimeSignalMap) {
		// get type of simulation from view and trigger the appropriate
		// simulator
		NetworkType networkType = neuralNetwork.getNetworkType();
		//this.LOG.info(networkType + " types!!!");
		if (networkType.equals(NetworkType.FEEDFORWARD)) {
			System.out.println("Feedforward network no longer supported");
			System.exit(0);
		} else {
			recurrentSimulator = new RecurrentSimulator(neuralNetwork, inputTimeSignalMap, this.runType);
			recurrentSimulator.simulate();
		}
	}
	
	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	/**
	 * Method to display the values of output in the excel sheet of the respective file
	 * 
	 * @param Output List containing all the output values for each case
	 */
	public void updateOutputInExcel(ArrayList<ArrayList<Double>> outputList){
		try {
			String path = this.getClass().getClassLoader().getResource(fileName).getFile();
			InputStream istream =new FileInputStream(path);
			Workbook wb = new XSSFWorkbook(istream);
			int sheetIndex = wb.getSheetIndex("Outputs");
			Sheet sheet = wb.getSheetAt(sheetIndex);
			
			Iterator<Row> rowIterator = sheet.iterator();
			int index=0;
			int columnIndex = 0 ;
			//iterating over each row
			while (rowIterator.hasNext())  {
				Row row = (Row) rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				
				
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();
					
					if (row.getRowNum() == 0) {
						if(cell.getStringCellValue().contains("Actual")) {
							columnIndex = cell.getColumnIndex();
							index=0;
						}
					} else {
						if(cell.getColumnIndex() == columnIndex && outputList.size() > 0) {
							cell.setCellValue(outputList.get(index).get(0));
							if(index < outputList.size() - 1) {
								index++;
							}
						}
					}
				}
				
			}
		    istream.close();
		    FileOutputStream fileOut = new FileOutputStream(path.toString().replace("bin", "src"));
		    wb.write(fileOut);
		    wb.close();
		    fileOut.close();
		    System.out.println("Output values added");
			
		} catch (IOException e) {
			System.out.println("Error during accessing excel sheet..."+e);
		}
			
	}
	
	
	/**
	 * Performs simulation
	 * @param fileName Which file(Neural network) to simulate
	 * @param isLabelSelected
	 */
	public void simulate(String fileName, boolean isLabelSelected) {
		Map<Integer, List<Double>> inputTimeSignalMap = new HashMap<Integer, List<Double>>();

		try {
			this.fileName = fileName;
			neuralNetwork.buildNetwork(fileName, inputTimeSignalMap, isLabelSelected);
			simulateNetwork(neuralNetwork, inputTimeSignalMap);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex);
		}
	}
}
