package gasNEAT.model;

import gasNEAT.builders.PolynomialFunctionBuilder;

/**
 * Class for storing polynomial
 *
 */
public class PolynomialFunction {
	
	private String polyID;
	
	//private HashMap<String, Double> coefficients2 = new HashMap<String, Double>();
	//private HashMap<String, Double> powers2 = new HashMap<String, Double>();
	
	//0 1 2 3 4 5 6
	//
	private double[] coefficients;
	private double[] powers;
	
	
	// default constructor
	public PolynomialFunction() {}
	
	// this constructor is for polynomialBuilder
	/**
	 * @param polynomialBuilder Stores polynomial built through PolynomimalBuilder class
	 */
	public PolynomialFunction(PolynomialFunctionBuilder polynomialBuilder) {
		//this.coefficients2 = polynomialBuilder.getCoefficients();
		//this.powers2 = polynomialBuilder.getPowers();
		this.polyID = polynomialBuilder.getPolyID();
		
		this.coefficients = polynomialBuilder.getCoefficients2();
		this.powers = polynomialBuilder.getPowers2();
		
		
	}
	
	/**
	 * @param inputs
	 * @return Returns the exact value of the polynomial by replacing variables from it with inputs.
	 */
	public double evaluate( double[] inputs) {
		//public double evaluate(HashMap<String, Double> inputs) {
		
		double x = 0;
		
		/*
		Set inputsEntrySet = inputs.entrySet();
		Iterator inputIterator = inputsEntrySet.iterator();
		
		while (inputIterator.hasNext()) {
			Map.Entry currentEntry = (Map.Entry) inputIterator.next();
			String currentID = (String) currentEntry.getKey();
			
			
			
			if( powers.keySet().contains(currentID) && coefficients.keySet().contains(currentID)) {
				x += coefficients.get(currentID) * Math.pow(inputs.get(currentID), powers.get(currentID));
			}
			
		}
		*/
		
		for (int i=0; i < inputs.length; i++) {
			x += coefficients[i]* Math.pow(inputs[i], powers[i] );
		}

		return x;
	}
	
	public double randomEvaluation() {
		double x = 0;
		return x;
	}

	/**
	 * @return Unique polynomialID
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
