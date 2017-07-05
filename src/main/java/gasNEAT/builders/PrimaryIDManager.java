package gasNEAT.builders;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;


import gasNEAT.nn.GasNeatNeuron;

public class PrimaryIDManager {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PrimaryIDManager.class);
	private HashSet<String> claimedReceptorIDs = new HashSet<String>();
	private long longID = -1;
	private int currentNeuronID = -1;
	private int currentReceptorID = -1;
	private int currentFunctionID = -1;
	
	/**
	 * @return NeuronID
	 */
	public int getNewNeuronID() {
		this.currentNeuronID++;
		return this.currentNeuronID;
		//return SpreadsheetConstants.NEURON_ID_PREFIX + this.currentNeuronID;
	}
	
	/**
	 * @return
	 */
	public long getLongID() {
		return this.longID++;
	}
	
	/**
	 * @return ReceptorID
	 */
	public String getNewReceptorID() {
		this.currentReceptorID++;
		
		while(this.claimedReceptorIDs.contains(SpreadsheetConstants.RECEPTOR_ID_PREFIX + currentReceptorID)) {
			this.currentReceptorID++;
		}
		this.claimedReceptorIDs.add(SpreadsheetConstants.RECEPTOR_ID_PREFIX + this.currentReceptorID);
		return SpreadsheetConstants.RECEPTOR_ID_PREFIX + this.currentReceptorID; 
	}
	
	/**
	 * @return FunctionID
	 */
	public String getNewFunctionID() {
		this.currentFunctionID++;
		return SpreadsheetConstants.FUNCTION_ID_PREFIX + this.currentFunctionID; 
	}
	
	/**
	 * @param neuron GasNeatNeuron
	 * @return
	 */
	public String getCorrespondingReceptorID(GasNeatNeuron neuron) {
		System.err.println("Not using spreadsheets");
		System.exit(-1);
		return null;
		//int neuronID = neuron.getNeuronID();
		//int receptorID = neuronID.replace(SpreadsheetConstants.NEURON_ID_PREFIX, SpreadsheetConstants.RECEPTOR_ID_PREFIX);
		//return this.claimReceptorID(receptorID);
	}
	
	/**
	 * @param receptorID
	 * @return
	 */
	private String claimReceptorID(String receptorID) {
		if(this.claimedReceptorIDs.contains(receptorID)) {
			return null;
		}
		this.claimedReceptorIDs.add(receptorID);
		return receptorID;
	}
}
