package gasNEAT.builders;

import java.util.HashMap;

import gasNEAT.model.PolynomialFunction;

/**
 * Generates polynomial for a neural network
 *
 */
public class PolynomialFunctionBuilder {

	private HashMap<String, Double> coefficients = new HashMap<String, Double>();
	private HashMap<String, Double> powers = new HashMap<String, Double>();
	
	private double[] coefficients2 = new double[5];
	private double[] powers2 = new double[5];
	
	private String polyID;
	
	public PolynomialFunctionBuilder() {
		
		
		
	}
	/**
	 * @param coefficients coefficients
	 * @param powers powers to variables
	 * @param polyID Unique PolynomialID
	 * @param functionTarget Type of function represented by polynomial
	 */
	PolynomialFunctionBuilder(HashMap<String, Double> coefficients, HashMap<String, Double> powers, String polyID) {
	//PolynomialFunctionBuilder(double[] coefficients, double[] powers, String polyID) {
		this.coefficients = coefficients;
		this.powers = powers;
		this.polyID = polyID;
	}
	 
	/**
	 * This function is for building random network
	 * @return
	 */
	public PolynomialFunction buildDefault() {
		System.out.println("BUILDING DEFAULT");
		System.exit(1);

		this.polyID = "F0";
		//this.powers.put("G0", 0.0);
		//this.coefficients.put("G0", 0.0);
		return new PolynomialFunction(this);
	}
	
	/**
	 * @return Polynomial
	 */
	public PolynomialFunction build() {
		return new PolynomialFunction(this);
	}
	/**
	 * @return Coefficients
	 */
	public HashMap<String, Double> getCoefficients() {
	//public double[] getCoefficients() {
		return coefficients;
	}
	
	public double[] getCoefficients2() {
		return coefficients2;
	}

	/**
	 * @param coefficients
	 */
	public void setCoefficients(HashMap<String, Double> coefficients) {
	//public void setCoefficients(double[] coefficients) {
		this.coefficients = coefficients;
	}
	
	public void setCoefficients2(double[] coefficients) {
			this.coefficients2 = coefficients;
	}
	
	/**
	 * @param String variableName, Double coefficient, Double power
	 */
	public void addVariable(String var, double co, double power) {
		coefficients.put(var,  co);
		powers.put(var, power);
		
		//System.out.println( var.substring(1) + " "   + co + " "  +power );
		
		coefficients2[ new Integer( var.substring(1) )] = co;
		powers2[ new Integer( var.substring(1) )] = power;
	}

	/**
	 * @return powers to the variables
	 */
	public HashMap<String, Double> getPowers() {
	//public double[] getPowers() {
		return powers;
	}
	

	public double[] getPowers2() {
		return powers2;
	}

	/**
	 * @param powers
	 */
	public void setPowers(HashMap<String, Double> powers) {
	//public void setPowers(double[] powers) {
		this.powers = powers;
	}

	public void setPowers2(double[] powers) {
		this.powers2 = powers;
	}

	
	/**
	 * @return Unique PolynomialID
	 */
	public String getPolyID() {
		return polyID;
	}

	/**
	 * @param polyID
	 */
	public void setPolyID(String polyID) {
		this.polyID = polyID;
	}

}
