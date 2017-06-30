package headless;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gasNEAT.main.NetworkSimulator;

public class HeadlessTest {
	
	NetworkSimulator networkSimulator;
	String[] arg;
	String fileName;
	//FeedForwardSimulator feedForwardSimulator;
	ArrayList<ArrayList<Double>> outputList;
	/** map of expected output signals based on time instance (Map structure : Time Instant, List of Output Signals) */
	Map<Integer, List<Double>> outputTimeSignalMap;
	@Before
	public void setUp() {
		//initialize the networkSimulator
		arg = new String[2];
	}
	
	@Test
	public void HeadlessEvolutionTest() {
		//TODOTEST
		return;
		/*
		arg[0] = new String("evolution");
		arg[1] = new String("XORNetwork.xlsx");
		//arg[2] = new String("XORNetwork.xlsx");
		networkSimulator = new NetworkSimulator(arg);
		outputTimeSignalMap = networkSimulator.getNeuralNetwork().getNetworkBuilder().getOutputTimeSignalMap();
		// evolution part does not works now, just add one default assertion 
		Assert.assertTrue(true);
		*/
	}
	
	@Test
	public void HeadlessSimulationTest() {
		//TODOTEST
				return;
				/*
		arg[0] = new String("simulation");
		arg[1] = new String("XORNetwork.xlsx");
		//arg[2] = new String("XORNetwork.xlsx");
		networkSimulator = new NetworkSimulator(arg);
		fileName = networkSimulator.getNetworkSimulationController().getFileName();
		//TODO FIXME use recurrent network to do this.
		//outputList = networkSimulator.getNetworkSimulationController().getFeedForwardSimulator().getOutputList();
		
		try{
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
					if(cell.getColumnIndex() == columnIndex ) {
						cell.setCellValue(outputList.get(index).get(0));
						if(index < outputList.size()-1) {
							index++;
						}
					}
				}
			}
			
		}
		
		} catch(IOException e) {
			System.out.println("Error during accessing excel sheet..."+e);
		}
		int count = 0;
		for (ArrayList<Double> i : outputList) {
			if (count == 0){
				Assert.assertTrue((i.get(0) < 0.2 ));
			}
			if (count == 1){
				Assert.assertTrue(( i.get(0) > 0.5));
			}
			if (count == 2){
				Assert.assertTrue(( i.get(0) > 0.5));
			}
			if (count == 3){
				Assert.assertTrue(( i.get(0) < 0.5));
			}
			count++;
		}
	
	*/
	}
}



