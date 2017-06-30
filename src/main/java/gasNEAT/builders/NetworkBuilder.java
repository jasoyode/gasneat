package gasNEAT.builders;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.anji.nn.ActivationFunctionFactory;

import gasNEAT.model.Gas;
import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.model.GasNeatReceptor;
import gasNEAT.model.GasNeatSynapse;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.view.Constants;
import gasNEAT.view.Constants.NetworkType;
import gasNEAT.view.Constants.VisualizationModes;

/**
 * This class is used to read input files and build neural network based on
 * input files.
 */
public class NetworkBuilder {

	/** neural network instance which needs to be built */
	private GasNeatNeuralNetwork neuralNetwork;

	/**
	 * map of input signals based on time instance (Map structure : Time
	 * Instant, List of Input Signals)
	 */
	private Map<Integer, List<Double>> inputTimeSignalMap;

	/**
	 * map of expected output signals based on time instance (Map structure :
	 * Time Instant, List of Output Signals)
	 */
	private Map<Integer, List<Double>> outputTimeSignalMap;

	/** red color value used to paint appropriate view contents */
	private float r = 0;

	/** green color value used to paint appropriate view contents */
	private float g = 0;

	/** blue color value used to paint appropriate view contents */
	private float b = 0;

	/**
	 * builds a neural network by reading files
	 * 
	 * @param fileName
	 *            name of the excel workbook that has all information to build a
	 *            network
	 * @param neuralNetwork
	 *            neuralNetwork instance that gets built
	 */
	@SuppressWarnings("resource")
	public void buildNetwork(String fileName, GasNeatNeuralNetwork neuralNetwork) {
		String path = this.getClass().getClassLoader().getResource(fileName).getFile();
		this.neuralNetwork = neuralNetwork;
		this.outputTimeSignalMap = new HashMap<Integer, List<Double>>();

		InputStream fis = null;
		try {
			fis = new FileInputStream(path);

			// Using XSSF for xlsx format, for xls use HSSF
			Workbook workbook = new XSSFWorkbook(fis);

			int numberOfSheets = workbook.getNumberOfSheets();

			// looping over each workbook sheet
			for (int i = 0; i < numberOfSheets; i++) {
				if (i == 0) {
					setNetworkParameters(workbook.getSheetAt(i));
				} else if (i == 1) {
					buildGasMap(workbook.getSheetAt(i));
				} else if (i == 2) {
					buildFunctions(workbook.getSheetAt(i));
				} else if (i == 3) {
					buildReceptors(workbook.getSheetAt(i));
				} else if (i == 4) {
					buildNeuronMap(workbook.getSheetAt(i));
				} else if (i == 5) {
					buildSynapsesMap(workbook.getSheetAt(i));
				} else if (i == SpreadsheetConstants.INPUT_SHEET_INDEX && inputTimeSignalMap != null) {
					buildInputTimeSignalMap(workbook.getSheetAt(i));
				} else if (i == SpreadsheetConstants.OUTPUT_SHEET_INDEX && inputTimeSignalMap != null) {
					buildOutputTimeSignalMap(workbook.getSheetAt(i));
				}
			}

			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter for inputTimeSignalMap
	 * 
	 * @return inputTimeSignalMap map of input signals based on time instance
	 *         (Map structure : Time Instant, List of Input Signals)
	 */
	public Map<Integer, List<Double>> getInputTimeSignalMap() {
		return inputTimeSignalMap;
	}

	/**
	 * Setter for inputTimeSignalMap
	 * 
	 * @param inputTimeSignalMap2
	 *            sets map of input signals based on time instance (Map
	 *            structure : Time Instant, List of Input Signals)
	 */
	public void setInputTimeSignalMap(Map<Integer, List<Double>> inputTimeSignalMap2) {
		this.inputTimeSignalMap = inputTimeSignalMap2;
	}

	/**
	 * Getter for outputTimeSignalMap
	 * 
	 * @return outputTimeSignalMap map of output signals based on time instance
	 *         (Map structure : Time Instant, List of output Signals)
	 */
	public Map<Integer, List<Double>> getOutputTimeSignalMap() {
		return outputTimeSignalMap;
	}

	/**
	 * Setter for outputTimeSignalMap
	 * 
	 * @return outputTimeSignalMap map of output signals based on time instance
	 *         (Map structure : Time Instant, List of output Signals)
	 */
	public void setOutputTimeSignalMap(Map<Integer, List<Double>> outputTimeSignalMap) {
		this.outputTimeSignalMap = outputTimeSignalMap;
	}

	/**
	 * Sets network wide parameters using the information on excel sheet
	 * 
	 * @param sheet
	 *            Excel sheet containing information of network wide parameters
	 */
	private void setNetworkParameters(Sheet sheet) {
		ArrayList<Cell> column = new ArrayList<Cell>();

		// build the column by iterating over each row
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();
			Cell cell = row.getCell(1);
			column.add(cell);
		}

		// populate neuralNetwork with parameters from the column
		Iterator<Cell> columnIterator = column.iterator();
		while (columnIterator.hasNext()) {
			Cell cell = columnIterator.next();
			if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
				if (cell.getRowIndex() == 1) {
					if (cell.getStringCellValue().equalsIgnoreCase("FeedForward")) {
						neuralNetwork.setNetworkType(NetworkType.FEEDFORWARD);
					} else if (cell.getStringCellValue().equalsIgnoreCase("Recurrent")) {
						neuralNetwork.setNetworkType(NetworkType.RECURRENT);
					}
				} else if (cell.getRowIndex() == 2) {
					if (cell.getStringCellValue().equalsIgnoreCase("Step")) {

						ActivationFunctionFactory factory = ActivationFunctionFactory.getInstance();
						neuralNetwork.setActivationFunction(factory.getStep());
					} else if (cell.getStringCellValue().equalsIgnoreCase("LogSig")) {
						ActivationFunctionFactory factory = ActivationFunctionFactory.getInstance();
						neuralNetwork.setActivationFunction(factory.getSigmoid());

					}
				} else if (cell.getRowIndex() == 3) {
					if (cell.getStringCellValue().equalsIgnoreCase("Hidden")) {
						neuralNetwork.setMode(VisualizationModes.GAS_HIDDEN);
					} else if (cell.getStringCellValue().equalsIgnoreCase("Translucent")) {
						neuralNetwork.setMode(VisualizationModes.TRANSLUCENT_GAS);
					} else if (cell.getStringCellValue().equalsIgnoreCase("Rings")) {
						neuralNetwork.setMode(VisualizationModes.GAS_RINGS);
					}
				}
			}
		}
	}

	/**
	 * builds a map of gas id against gas object by reading excel sheet
	 * 
	 * @param sheet
	 *            Excel sheet that contains information of gases
	 */
	private void buildGasMap(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		// iterating over each row
		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if (row.getRowNum() == 0) {
				continue;
			}
			// create one gas per row
			Gas gas = new Gas();

			Iterator<Cell> cellIterator = row.cellIterator();

			// Iterating over each cell (column wise) in a particular row.
			while (cellIterator.hasNext()) {
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
					if (cell.getColumnIndex() == 0) {
						gas.setGasID(cell.getStringCellValue());
					} else if (cell.getColumnIndex() == 1) {
						gas.setName(cell.getStringCellValue());
					} else if (cell.getColumnIndex() == 4)
						gas.setGasDispersionType(cell.getStringCellValue());
				} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())
					if (cell.getColumnIndex() == 2) {
						gas.setPropagationSpeed((int) cell.getNumericCellValue());
					} else if (cell.getColumnIndex() == 3) {
						gas.setDecayFactor(cell.getNumericCellValue());
					} else if (cell.getColumnIndex() == 5) {
						r = (float) cell.getNumericCellValue();
					} else if (cell.getColumnIndex() == 6) {
						g = (float) cell.getNumericCellValue();
					} else if (cell.getColumnIndex() == 7) {
						b = (float) cell.getNumericCellValue();
						gas.setColor(r, g, b);
					}
			}
			//re-do this the proper way
			neuralNetwork.getGasMap().put(gas.getGasID(), gas);
		}

	}

	/**
	 * builds a map of polynomial id against polynomial function by reading
	 * excel sheet
	 * 
	 * @param sheet
	 *            excel sheet containing information of polynomial functions to
	 *            be built
	 */
	private void buildFunctions(Sheet sheet) {
		// iterating over each row
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if (row.getRowNum() == 0) {
				continue;
			}

			// create one polynomial per row
			// Polynomial polynomial = new Polynomial();
			PolynomialFunctionBuilder polynomialBuilder = new PolynomialFunctionBuilder();
			// PolynomialBuilder polynomialB

			Iterator<Cell> cellIterator = row.cellIterator();
			
			int cellCount=0;
			// Iterating over each cell (column wise) in a particular row.
			while (cellIterator.hasNext()) 
			{
				Cell cell = (Cell) cellIterator.next();
				cellCount++;
				if (Cell.CELL_TYPE_STRING == cell.getCellType()) 
				{
					if (cell.getColumnIndex() == 0) 
					{
						// polynomial.setPolyID(cell.getStringCellValue());
						polynomialBuilder.setPolyID(cell.getStringCellValue());
						//System.out.println("F set [" + cell.getStringCellValue() + "]");
					} else if (cell.getColumnIndex() == 1) 
					{
						//TODO FIXME -  need to update the spreadsheet to use receptors instead of this
						if (cell.getStringCellValue().equalsIgnoreCase("A")) 
						{
						//	polynomialBuilder.setFunctionTarget(ModFunctionTarget.ACTIVATION);
						} else if (cell.getStringCellValue().equalsIgnoreCase("P")) {
						//	polynomialBuilder.setFunctionTarget(ModFunctionTarget.PLASTICITY);
						} 
					} else if (cell.getColumnIndex() % 2 == 0) 	{
							//polynomialBuilder.getCoefficients().put(cell.getStringCellValue(), 1.0);
							//polynomialBuilder.getPowers().put(cell.getStringCellValue(), 1.0);
						}
					} else if (cell.getColumnIndex() >= 3 && Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if (cell.getColumnIndex() % 3 == 0) {
							//polynomialBuilder.getCoefficients().put(
									//row.getCell(cell.getColumnIndex() - 1).getStringCellValue(),
									//cell.getNumericCellValue());
						} else if (cell.getColumnIndex() % 3 == 1) {
							//polynomialBuilder.getPowers().put(
								//	row.getCell(cell.getColumnIndex() - 2).getStringCellValue(),
								//	cell.getNumericCellValue());
						}
					}
				}
			neuralNetwork.getFunctionMap().put(polynomialBuilder.getPolyID(), polynomialBuilder.build());
		}
	}

	/**
	 * builds a map of receptor id against receptor instance by reading excel
	 * sheet
	 * 
	 * @param sheet
	 *            excel sheet containing receptor information
	 */
	private void buildReceptors(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		// iterating over each row
		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if (row.getRowNum() == 0) {
				continue;
			}
			// create one receptor per row
			// Receptor receptor = new Receptor();
			ReceptorBuilder receptorBuilder = new ReceptorBuilder(
					row.getCell(SpreadsheetConstants.RECEPTOR_ID_COLUMN).getStringCellValue(),
					row.getCell(SpreadsheetConstants.RECEPTOR_ACTIVATION_TYPE_COLUMN).getStringCellValue());

			Iterator<Cell> cellIterator = row.cellIterator();
			// Iterating over each cell (column wise) in a particular row.
			while (cellIterator.hasNext()) {
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
					if (cell.getColumnIndex() == 2) {
						String gasString = cell.getStringCellValue();
						// get gases from the (String) list and save an ArrayList in the Receptor
						ArrayList<String> gasList = new ArrayList<String>(Arrays.asList(gasString.split("\\s*,\\s*")));
						receptorBuilder.setGasList(gasList);
					} else if (cell.getColumnIndex() == 3) {
						receptorBuilder.setActivationModFunction(
								neuralNetwork.getFunctionMap().get(cell.getStringCellValue()));
					} else if (cell.getColumnIndex() == 4) {
						receptorBuilder.setPlasticityModFunction(
								neuralNetwork.getFunctionMap().get(cell.getStringCellValue()));
					}
				}
			}

			// TODO: Move code like this to the neural network so that we can
			// say neuralNetwork.addX and it will do it. This should prevent
			// duplicate code
			GasNeatReceptor receptor = receptorBuilder.build();
			this.neuralNetwork.addReceptor(receptor);
		}
	}

	/**
	 * Fetches the training data from excel file mentioned in parameter
	 * 
	 * @param fileName
	 *            Filename containing the training data
	 * @return A hashmap containing a list of neural network output of traning
	 *         data
	 */

	public Map<Integer, List<Double>> extractTrainingSetData(String fileName) {
		String path = this.getClass().getClassLoader().getResource(fileName).getFile();
		Map<Integer, List<Double>> output = new HashMap<Integer, List<Double>>();

		int timeStep = 0;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);

			// Using XSSF for xlsx format, for xls use HSSF
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet inputSheet = workbook.getSheetAt(SpreadsheetConstants.INPUT_SHEET_INDEX);
			Sheet outputSheet = workbook.getSheetAt(SpreadsheetConstants.OUTPUT_SHEET_INDEX);

			for (Row row : inputSheet) {
				if (row.getRowNum() == 0) {
					continue;
				}
				ArrayList<Double> values = new ArrayList<Double>();
				for (Cell cell : row) {
					if (cell.getColumnIndex() == 0 || !(Cell.CELL_TYPE_NUMERIC == cell.getCellType())) {
						continue;
					}
					values.add(cell.getNumericCellValue());
				}
				output.put(timeStep, values);
				timeStep++;
			}

			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

	public double getClassificationForRow(int rowIndex, Sheet sheet) {
		Row row = sheet.getRow(rowIndex);
		Cell cell = row.getCell(SpreadsheetConstants.EXPECTED_OUTPUT_COLUMN_NUMBER);
		return cell.getNumericCellValue();
	}

	/**
	 * builds a map of neuron id against neuron object by reading excel sheet
	 * 
	 * @param sheet
	 *            excel sheet that contains neuron information
	 */
	private void buildNeuronMap(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		// iterating over each row
		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if (row.getRowNum() == 0) {
				continue;
			}

			// create one neuron per row
			NeuronBuilder neuronBuilder = new NeuronBuilder(neuralNetwork.getActivationFunction(),
					neuralNetwork.getActivationThresholdFunction(),
					row.getCell(SpreadsheetConstants.NEURON_ID_COLUMN).getStringCellValue(),
					SpreadsheetConstants.TYPE_MAPPING
							.get(row.getCell(SpreadsheetConstants.NEURON_TYPE_COLUMN).getStringCellValue()), "G0"); //TODO add synapticgas type to spreadsheets

			Iterator<Cell> cellIterator = row.cellIterator();

			// Iterating over each cell (column wise) in a particular row.
			while (cellIterator.hasNext()) {
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
					if (cell.getColumnIndex() == 5) {
						// TODO: obviously needs to be re-evaluated
						neuronBuilder.setIsGasEmitter(cell.getStringCellValue().equalsIgnoreCase(Constants.TRUE_VALUE));
					} else if (cell.getColumnIndex() == 6 && neuronBuilder.getIsGasEmitter()) {
						neuronBuilder.setGasType(cell.getStringCellValue());
						Color color = neuralNetwork.getGasMap().get(cell.getStringCellValue()).getColor();
						neuronBuilder.setGasColor(color);
					} else if (cell.getColumnIndex() == 9) {
						neuronBuilder.setIsGasReceiver(
								cell.getStringCellValue().equalsIgnoreCase(Constants.TRUE_VALUE) ? true : false);
					} else if (cell.getColumnIndex() == 10) {
						GasNeatReceptor receptor = neuralNetwork.getReceptorMap().get(cell.getStringCellValue());
						neuronBuilder.setReceptor(receptor);
					}
				} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
					if (cell.getColumnIndex() == SpreadsheetConstants.NEURON_X_COLUMN) {
						neuronBuilder.setXAndYCoordinate((int) cell.getNumericCellValue(),
								(int) row.getCell(SpreadsheetConstants.NEURON_Y_COLUMN).getNumericCellValue());
					} else if (cell.getColumnIndex() == 4) {
						neuronBuilder.setThreshold(cell.getNumericCellValue());
					} else if (cell.getColumnIndex() == 8 && neuronBuilder.getIsGasEmitter()) {
						neuronBuilder.setGasEmissionRadius(cell.getNumericCellValue());
					} else if (cell.getColumnIndex() == 7 && neuronBuilder.getIsGasEmitter()) {
						neuronBuilder.setBaseProduction(cell.getNumericCellValue());
					}
				}
			}
			GasNeatNeuron neuron = neuronBuilder.build();
			this.neuralNetwork.addNeuron(neuron);
		}

	}

	/**
	 * builds a map of synapse id against synapse object by reading excel sheet
	 * 
	 * @param sheet
	 *            excel sheet containing synapse information
	 */
	private void buildSynapsesMap(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		List<String> targetNeurons = new ArrayList<String>();

		// iterating over each row
		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();

			String sourceNeuron = null;

			Iterator<Cell> cellIterator = row.cellIterator();

			// Iterating over each cell (column wise) in a particular row.
			while (cellIterator.hasNext()) {
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
					if (row.getRowNum() == 0 && cell.getColumnIndex() >= 1) {
						// fill the targetNodes list with elements of row 0,
						// starting with cell 1
						targetNeurons.add(cell.getStringCellValue());
					} else if (cell.getColumnIndex() == 0) {
						sourceNeuron = cell.getStringCellValue();
					}
				} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
					// fill synapseMap
					// valid column indexes are from 1 to targetNodes.size()
					if (cell.getColumnIndex() >= 1 && cell.getColumnIndex() <= targetNeurons.size()
							&& cell.getNumericCellValue() != 0) {
						// create one synapse per weight cell
						String targetNeuron = targetNeurons.get(cell.getColumnIndex() - 1);
						SynapseBuilder synapseBuilder = new SynapseBuilder(sourceNeuron, targetNeuron,
								cell.getNumericCellValue(), false,0,0,0,0,0, null);
						GasNeatSynapse synapse = synapseBuilder.build();
						System.out.println("DONT USE THIS");
						System.exit(1); 
						// filling the synapse list in source neuron
						GasNeatNeuron source = neuralNetwork.getNeuronMap().get(sourceNeuron);

						source.addOutgoingConnection(synapse);

						this.neuralNetwork.addSynapse(synapse);
					}
				}
			}
		}

	}

	/**
	 * builds a map of time instant against list of input signals by reading
	 * excel sheet
	 * 
	 * @param sheet
	 *            excel sheet containing input signal information
	 */
	private void buildInputTimeSignalMap(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		// iterating over each row
		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if (row.getRowNum() == 0) {
				continue;
			}

			// random initialization
			int timeInstant = -1;

			Iterator<Cell> cellIterator = row.cellIterator();

			// Iterating over each cell (column wise) in a particular row.
			while (cellIterator.hasNext()) {
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
					if (cell.getColumnIndex() == 0) {
						timeInstant = (int) cell.getNumericCellValue();
					} else {
						List<Double> signalList;

						if (inputTimeSignalMap.get(timeInstant) == null) {
							signalList = new ArrayList<Double>();
						} else {
							signalList = inputTimeSignalMap.get(timeInstant);
						}
						signalList.add((double) cell.getNumericCellValue());
						inputTimeSignalMap.put(timeInstant, signalList);
					}
				}
			}
		}
	}

	/**
	 * builds a map of time instant against list of output signals by reading
	 * excel sheet
	 * 
	 * @param sheet
	 *            excel sheet that contains information of expected output
	 *            signals
	 */
	private void buildOutputTimeSignalMap(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		// iterating over each row
		while (rowIterator.hasNext()) {
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if (row.getRowNum() == 0)
				continue;

			// random initialization
			int timeInstant = -1;

			Iterator<Cell> cellIterator = row.cellIterator();

			// Iterating over each cell (column wise) in a particular row.
			while (cellIterator.hasNext()) {
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {

					if (cell.getColumnIndex() == 0) {
						timeInstant = (int) cell.getNumericCellValue();

					} else {
						List<Double> signalList;

						if (outputTimeSignalMap.get(timeInstant) == null) {
							signalList = new ArrayList<Double>();
						} else {
							signalList = outputTimeSignalMap.get(timeInstant);
						}
						signalList.add((double) cell.getNumericCellValue());
						outputTimeSignalMap.put(timeInstant, signalList);
					}
				}
			}
		}

	}
}
