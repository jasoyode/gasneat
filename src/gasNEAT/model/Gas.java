package gasNEAT.model;

import java.awt.Color;

/**
 * Configures Gas in neural network
 *
 */
public class Gas implements Cloneable {

	/** Unique Id of Gas */
	private String gasID;
	
	/** Name of Gas */
	private String name;
	
	/** Propagation speed of Gas */
	private double propagationSpeed;
	
	/** Decay factor of Gas */
	private double decayFactor;
	
	/** Gas Dispersion Type */
	private String gasDispersionType;
	
	/** Gas color */
	private Color color;

	/**
	 * Returns Gas dispersion type of Gas
	 * 
	 * @return gasDispersionType Gas Dispersion type
	 */
	public String getGasDispersionType() {
		return gasDispersionType;
	}

	/**
	 * Sets Gas dispersion type
	 * 
	 * @param gasDispersionType
	 *            Gas Dispersion type
	 */
	public void setGasDispersionType(String gasDispersionType) {
		this.gasDispersionType = gasDispersionType;
	}

	/**
	 * Returns name of Gas
	 * 
	 * @return name Gas name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name of Gas
	 * 
	 * @param name
	 *            Gas name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns Id of Gas
	 * 
	 * @return gasID Gas Id
	 */
	public String getGasID() {
		return gasID;
	}

	/**
	 * 
	 * Sets Id of Gas
	 * 
	 * @param gasID
	 *            Gas id
	 */
	public void setGasID(String gasID) {
		this.gasID = gasID;
	}

	/**
	 * Returns PropagationSpeed of Gas
	 * 
	 * @return propagationSpeed Propagation speed of Gas
	 */
	public double getPropagationSpeed() {
		return propagationSpeed;
	}

	/**
	 * Sets PropagationSpeed of Gas
	 * 
	 * @param propagation
	 *            Propagation Speed of Gas
	 */
	public void setPropagationSpeed(double propagationSpeed) {
		this.propagationSpeed = propagationSpeed;
	}

	/**
	 * Sets decay Factor of Gas
	 * 
	 * @param decayFactor
	 */
	public void setDecayFactor(double decayFactor) {
		this.decayFactor = decayFactor;
	}

	/**
	 * Creates a Clone of Gas object
	 * 
	 * throws CloneNotSupportedException returns Gas Creates a clone of Gas
	 * object
	 */
	public Gas clone() throws CloneNotSupportedException {
		return (Gas) super.clone();
	}

	/**
	 * Returns Color of Gas
	 * @return color Gas color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets color of Gas
	 * @param color Gas color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Sets color of Gas
	 * @param r	Red color value
	 * @param g Green color value
	 * @param b Blue color value
	 */
	public void setColor(float r, float g, float b) {
		this.color = new Color(r, g, b);
	}

}
