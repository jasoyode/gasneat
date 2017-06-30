package gasNEAT.model;

import java.util.HashMap;

/**  
* GasDispersionSlot class holds information about the gas strength, start location 
* and slot radius for gas at particular time interval along with information
* about receiver neurons in that gas range
*/
public class GasDispersionSlot implements Cloneable {

	/** gas concentration in the slot **/
	private double gasConcentration;
	
	/** receiver neurons map with distance from the center of source neuron **/
	private HashMap<String , Double> receiverNeurons = new HashMap<String, Double>();
	
	/** start location for gas dispersion slot **/
	private double startLocation;
	
	/** slot radius **/
	private double slotRadius;

	/**
	 * Constructor to create gas dispersion slot 
	 * 
	 * @param slotRadius		slot radius value
	 */	
	public GasDispersionSlot(double slotRadius, double startingRadius) {
		this.slotRadius = slotRadius;
		startLocation  = startingRadius;
		
	}
	
	/**
	 * Getter method for slot radius
	 * 
	 * @return slotRadius	slot radius
	 */	
	public double getSlotRadius() {
		return slotRadius;
	}
	

	/**
	 * Setter method for slot radius
	 * 
	 * @param slotRadius	slot radius
	 */	
	public void setSlotRadius(double slotRadius) {
		this.slotRadius = slotRadius;
	}
	
	/**
	 * Getter method for Start Location
	 * 
	 * @return startLocation	Start Location for gas dispersion slot
	 */	
	public double getStartLocation() {
		return startLocation;
	}
	
	/**
	 * Setter method for Start Location
	 * 
	 * @param startLocation	Start Location for gas dispersion slot
	 */
	public void setStartLocation(int startLocation) {
		this.startLocation = startLocation;
	}
	
	/**
	 * Getter method for gas Concentration
	 * 
	 * @return gasConcentration	gas Concentration at particular slot
	 */	
	public double getGasConcentration() {
		//if (gasConcentration > 1.0 )
		//	System.out.println(  this.toString() );
		return gasConcentration;
	}
	
	/**
	 * Setter method for gas Concentration
	 * 
	 * @param gasConcentration	gas Concentration at particular slot
	 */	
	public void setGasConcentration(double gasConcentration) {
		this.gasConcentration = gasConcentration;
	}
	
	/**
	 * Getter method for receiverNeurons map
	 * 
	 * @return nextGenerationDnaList	map to hold receiver Neurons
	 */	
	public HashMap<String, Double> getReceiverNeurons() {
		return receiverNeurons;
	}
	
	/**
	 * Setter method receiverNeurons map
	 * 
	 * @param receiverNeurons map to hold receiver Neurons
	 */	
	public void setReceiverNeurons(HashMap<String, Double> receiverNeurons) {
		this.receiverNeurons = receiverNeurons;
	}
	
	@Override
	public String toString() {
		return "GasDispersionSlot [gasConcentration=" + gasConcentration + ", receiverNeurons=" + receiverNeurons
				+ ", startLocation=" + startLocation + ", slotRadius=" + slotRadius + "]";
	}

	/**
	 * Clone method to clone gas dispersion slot
	 * 
	 * @throws CloneNotSupportedException	Clone not supported exception
	 */	
	public GasDispersionSlot clone() throws CloneNotSupportedException {
		GasDispersionSlot gasDispersionSlot = (GasDispersionSlot) super.clone();
		gasDispersionSlot.setReceiverNeurons((HashMap<String, Double>) receiverNeurons.clone());
		return gasDispersionSlot;
	}
}
