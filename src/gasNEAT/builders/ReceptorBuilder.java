package gasNEAT.builders;

import java.util.ArrayList;

import gasNEAT.model.GasNeatReceptor;
import gasNEAT.model.PolynomialFunction;

public class ReceptorBuilder {
	private final String receptorID;
	private final String activationType;
	private PolynomialFunction plasticityModFunciton;
	private PolynomialFunction activationModFunciton;
	private ArrayList<String> gasList = new ArrayList<String>();
	
	/**
	 * @param receptorID
	 * @param activationType
	 */
	public ReceptorBuilder(String receptorID, String activationType) {
		this.receptorID = receptorID;
		this.activationType = activationType;
	}
	
	/**
	 * @param gasList
	 */
	public void setGasList(ArrayList<String> gasList) {
		this.gasList = gasList;
	}
	
	/**
	 * @param activationModFunction
	 */
	public void setActivationModFunction(PolynomialFunction activationModFunction) {
		this.activationModFunciton = activationModFunction;
	}
	
	/**
	 * @param plasticityModFunction
	 */
	public void setPlasticityModFunction(PolynomialFunction plasticityModFunction) {
		this.plasticityModFunciton = plasticityModFunction;
	}
	
	/**
	 * @return GasNeatReceptor
	 */
	public GasNeatReceptor build() {
		
		System.out.println("BUILD WITH BUILDER.");
		System.exit(1);
		return new GasNeatReceptor(this);
	}
	
	/**
	 * Builds random receptor
	 * @return Receptor
	 */
	public GasNeatReceptor buildRandomReceptor() {
		
		System.out.println("NOT READY FOR RANDOM RECEPTORS");
		System.exit(1);
		
		GasNeatReceptor receptor = new GasNeatReceptor(this);
		
		//Default:  ( G1 - G2 )
		PolynomialFunctionBuilder activationModFunctionBuilder = new PolynomialFunctionBuilder();
		activationModFunctionBuilder.addVariable("G1", 1, 1);
		activationModFunctionBuilder.addVariable("G2", -1, 1);
		receptor.setActivationModFunction( activationModFunctionBuilder.build() );

		//Default:  ( G3 - G4 )
		PolynomialFunctionBuilder plasticityModFunctionBuilder = new PolynomialFunctionBuilder();
		plasticityModFunctionBuilder.addVariable("G3", 1, 1);
		plasticityModFunctionBuilder.addVariable("G4", -1, 1);
		receptor.setPlasticityModFunction( plasticityModFunctionBuilder.build() );
		
		ArrayList<String> newGasList = new ArrayList<String>();
		newGasList.add( "G1" );
		newGasList.add( "G2" );
		newGasList.add( "G3" );
		newGasList.add( "G4" );

		receptor.setGasList(newGasList );
		
		return receptor;
	}
	
	public String getReceptorID() { return this.receptorID; }
	public String getActivationType() { return this.activationType; }
	
	public PolynomialFunction getPlasticityModFunction() { return this.plasticityModFunciton; }
	public PolynomialFunction getActivationModFunction() { return this.activationModFunciton; }
	public ArrayList<String> getGasList() { return this.gasList; }

}
