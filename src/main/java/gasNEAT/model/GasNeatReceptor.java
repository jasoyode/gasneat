package gasNEAT.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import com.anji.util.Properties;

import gasNEAT.builders.PolynomialFunctionBuilder;
import gasNEAT.builders.ReceptorBuilder;
import gasNEAT.configurations.GasNeatConfiguration;

/**
 * Configures the receptor of the neural network
 *
 */
public class GasNeatReceptor {
	
	//MEGATODO #3need to load this from the config not hardcode
	private final static int NUMBER_GASES = 5;
	
	private String receptorMapFilePath;
	
	//ID referencing the receptor
	private String receptorType;
	
	//#ADDPROPS
	private double receptorStrength;
	
	//#ARRAYMAP
	//Stores the buffered concentration of gases before actually updating the values
	//private HashMap<String, Double> bufferedConcentrations2 = new HashMap<String, Double>();
	

	private double[] bufferedConcentrations = new double[NUMBER_GASES];
	private static ArrayList<String> receptorMap;

	private double activationLevelPreSquash = 0;
	
	private double maximumPlasticity = -1.0;
	private double minimumPlasticity = -1.0;
	private double plasticity = -1.0;
	
	//determines if the neuron is activated by the presence of synaptic activations of gases
	private String activationType;
	
	private int activationTypeInt = -1;
	
	//determines the modulation on activation
	private PolynomialFunction activationModFunction;
	
	//refactor into 4 variables
	
	
	
	
	//determines the modulation on plasticity
	private PolynomialFunction plasticityModFunction;
	
	private boolean exclusiveNeuromodulatedPlasticity;
	private boolean tanhSquashModulationSignal;
	
	//List of gases that influence this receptor
	private ArrayList<String> gasList;
	
	private static Logger LOG = Logger.getLogger( GasNeatReceptor.class );
	
	//*
	//Considering 4 possible gases - then positive and negative modulation
	//Can be paired potentially with different gases
	//This leaves open the room for receptors to mutate into existence
	//with a range of different impacts to the neuron
	//Activation types: G0, G1, G2, G3, G4
	//Positive Modulation of Activation Types:  NO, G1, G2, G3, G4
	//Negative Modulation of Activation Types:  NO, G1, G2, G3, G4
	//Positive Modulation of Plasticity Types:  NO, G1, G2, G3, G4
	//Negative Modulation of Plasticity Types:  NO, G1, G2, G3, G4
	
	//types of receptors 30 in total
	//Format:
	//
	//  {ActivationType}_{Pos Mod of Act}_{Neg Mod of Act}_{Pos Mod of Pla}_{Neg Mod of Pla}
	
	public static ArrayList<String> RECEPTOR_TYPES;
	public static String[] RECEPTOR_TYPES_ACTIVATION = new String[] {"G0","G1","G2","G3","G4"};
	public static String[] RECEPTOR_TYPES_POS_MOD_ACT= new String[] {"NO","G1","G2","G3","G4"};;
	public static String[] RECEPTOR_TYPES_NEG_MOD_ACT= new String[] {"NO","G1","G2","G3","G4"};;
	public static String[] RECEPTOR_TYPES_POS_MOD_PLA= new String[] {"NO","G1","G2","G3","G4"};;
	public static String[] RECEPTOR_TYPES_NEG_MOD_PLA= new String[] {"NO","G1","G2","G3","G4"};;
	
	public static void createReceptorTypeList() {
		for (String a: RECEPTOR_TYPES_ACTIVATION) {
			for (String b: RECEPTOR_TYPES_POS_MOD_ACT) {
				for (String c: RECEPTOR_TYPES_NEG_MOD_ACT) {
					for (String d: RECEPTOR_TYPES_POS_MOD_PLA) {
						for (String e: RECEPTOR_TYPES_NEG_MOD_PLA) {
							RECEPTOR_TYPES.add(a+"_"+b+"_"+c+"_"+d+"_"+e);
						}
					}
				}
			}
		}
	}
	//RECEPTOR_TYPES[0] = "NO_NO_NO_NO_NO_NO";
	//RECEPTOR_TYPES[1] = "NO_NO_NO_NO_NO_G1";
	//...
	//RECEPTOR_TYPES[124] = "G4_G4_G4_G4_G4_G4";
	
	
	//*/
	
	public GasNeatReceptor() {
		System.out.println("must provide constru"  );
		System.exit(-1);
		
		
	}

	/**
	 * Configures receptor from ReceptorBuilder
	 * @param receptorBuilder
	 */
	public GasNeatReceptor(ReceptorBuilder receptorBuilder) {
		System.out.println( "built with builder"  );
		System.exit(1);
		
		this.receptorType = receptorBuilder.getReceptorID();
		this.setActivationType(receptorBuilder.getActivationType());
		this.plasticityModFunction = receptorBuilder.getPlasticityModFunction();
		this.activationModFunction = receptorBuilder.getActivationModFunction();
		this.setGasList(receptorBuilder.getGasList());
		setupReceptorMap();
		setupFunctions();
		
	}
	
	
	public void init(Properties props) {
		plasticity = props.getDoubleProperty(GasNeatConfiguration.INITIAL_PLASTICITY_KEY, 0.1);
		maximumPlasticity = props.getDoubleProperty(GasNeatConfiguration.MAXIMUM_PLASTICITY_KEY, 1.0);
		minimumPlasticity = props.getDoubleProperty(GasNeatConfiguration.MINIMUM_PLASTICITY_KEY, 0.0);
		exclusiveNeuromodulatedPlasticity = props.getBooleanProperty(GasNeatConfiguration.EXCLUSIVE_NEUROMODULATED_PLASTICITY_KEY, false);
		tanhSquashModulationSignal = props.getBooleanProperty(GasNeatConfiguration.TANH_SQUASH_MODULATION_SIGNAL_KEY, false);
		receptorMapFilePath = props.getProperty(GasNeatConfiguration.MAP_RECEPTOR_FILE);

		//receptor maps have to be setup ahead of time
		setupReceptorMap();
		setupFunctions();
		
	}
	
	public void setupReceptorMap() {
		
		if (receptorMap == null ) {
			receptorMap = new ArrayList<String>(); 
			try {
				BufferedReader receptorList = new BufferedReader( new FileReader( receptorMapFilePath ));
				String currentLine = receptorList.readLine();
				for (int i=0; currentLine  != null; i++) {
					receptorMap.add( currentLine );
					currentLine = receptorList.readLine();
				}
				receptorList.close();
			
			} catch ( IOException e) {
				System.out.println("Could not read file:" +receptorMapFilePath );
				e.printStackTrace();
			}
		}
	}
	
	
	public void setupFunctions() {
		
		//String encoding  = receptorMap.get(receptorType);
		String encoding  = receptorType;
		
		if (encoding == null) {
			System.out.println("encoding cannot be null!");
			System.out.println("receptorType: "+ receptorType );
			System.exit(1);
		}
		
		String activationTypeTemp = encoding.substring(0, 2);
		
		if ( activationTypeTemp.equals("G0") ) {
			//if .equals("NO")
			//activationTypeTemp = "G0";
			setActivationType(  activationTypeTemp );
		} else {
			//ULTRATODO this is one place that the gas list can be added to!
			gasList.add( activationType );
			setActivationType(  activationTypeTemp );
		}
        
        //CREATE ACTIVATION MODULATION FUNC
        PolynomialFunctionBuilder builder = new PolynomialFunctionBuilder();
        if (!encoding.substring(3, 5).equals("NO") ) {
			String posActMod = encoding.substring(3, 5);
			builder.addVariable(posActMod , 1, 1);
			gasList.add( posActMod );
			
		}
		if (!encoding.substring(6, 8).equals("NO") ) {
			String negActMod = encoding.substring(6, 8);
			builder.addVariable(negActMod , -1, 1);
			gasList.add( negActMod );
			
		}
		activationModFunction = builder.build();
		
		//CREATE SYNAPTIC PLASTICITY MODULATION FUNC
		builder = new PolynomialFunctionBuilder();
		if (!encoding.substring(9, 11).equals("NO") ) {
			String posPlaMod = encoding.substring(9, 11);
			builder.addVariable(posPlaMod , 1, 1);
			gasList.add( posPlaMod);
		}
		
		if (!encoding.substring(12, 14).equals("NO") ) {
			String negPlaMod = encoding.substring(12, 14);
			builder.addVariable(negPlaMod, -1, 1);
			gasList.add( negPlaMod );
		}
        plasticityModFunction = builder.build();
        
        
	}
	
	
	//TODO: need to make exhaustive list here if you want to define these explicit types...
	public GasNeatReceptor(String receptorType, double receptorStrength) {
		
		this.receptorType = receptorType;
		//#ADDPROPS
		this.receptorStrength = receptorStrength;
		//NEED TO SETUP TO READ FROM PROP
		// #GASNEATMODEL
		gasList = new ArrayList<String>();
			
	}

	/**
	 * Increases the activation concentration
	 */
	public void updateActivationLevelFromBuiltUpConcentrations() {
		// #GASNEATMODEL
		//activation level depends upon the concentration of the given activationType gas
		activationLevelPreSquash = bufferedConcentrations[activationTypeInt];
		checkReasonableValue(activationLevelPreSquash);
		
	}
	
	/**
	 * Set the concentration to zero
	 */
	public void clearBufferedConcentrations() {
	
		Arrays.fill(bufferedConcentrations, 0.0);
	}
	
	/**
	 * modulateActivationConcentration
	 */
	public void modulateActivationLevelFromConcentrations() {
		//#GASNEATMODEL
		//May need to be sure updateActivationLevelFromBuiltUpConcentrations
		//is called before running this method
		
		//#BOTTLENECK
		
		//#SPEEDTEST
		activationLevelPreSquash = activationLevelPreSquash * (1 + activationModFunction.evaluate( bufferedConcentrations) ); 
		
		
		//add a parameter?
		// activationConcentration = aC * \beta * p(Gas)
		//checkAndKill("modulateActivationLevelFromConcentrations");
		
		
		checkReasonableValue(activationLevelPreSquash);

		
	}
	
	public static void checkReasonableValue( double act) {
		
		try {
			// 100 might be reasonable??? high weights and lots of them?
			// 10*10 is reasonable, 100*10 seems a bit too high to be reasonable
			// 
			if ( Double.isNaN(act)) {
				System.err.println("modulateActivationLevelFromConcentrations");
				System.err.println("activationLevelPreSquash is NaN in Receptor");
				System.err.println( act );
				throw new Exception("concentration value is NaN");
			} else if (act > 10000 || act < -10000) {
				System.err.println("modulateActivationLevelFromConcentrations");
				System.err.println("activationLevelPreSquash is NaN in Receptor");
				System.err.println( act );
				throw new Exception("concentration value is >10000 or < -10000");
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
			
		}
		
		
	}
	
	
	
	/**
	 * modulatePlasticity
	 */
	public void modulatePlasticityFromConcentrations() {
		//checkAndKill("modulatePlasticityFromConcentrations");
		if (exclusiveNeuromodulatedPlasticity && minimumPlasticity > 0) {
			System.err.println("You must allow plasticity to reach zero if you have enabled neuromodulated plasticity");
			System.err.println("because if there is no modulation present, the plasticity must be zero!");
			System.exit(1);
			
		}
		
		
		
		// #GASNEATMODEL
		double prePlasticity = plasticity;
		
		if (exclusiveNeuromodulatedPlasticity ) {
			//TODO really should be set via functions on the plasticity mod, but this is
			//way way faster for the time being
			//adding simple option to squash the modulation signal 
			if (tanhSquashModulationSignal) {
				plasticity = Math.tanh( receptorStrength * plasticityModFunction.evaluate( bufferedConcentrations  ) / 2 );
				
				//System.out.println("squashed: " + plasticity);
				//System.out.println("non-squahsed: "  + plasticityModFunction.evaluate( builtUpConcentrations  ) );
				
			} else {
				plasticity = receptorStrength * plasticityModFunction.evaluate( bufferedConcentrations  );
				
				//System.out.println("non-squashed: " + plasticity);
				//System.out.println("squashed: "  + Math.tanh(  plasticityModFunction.evaluate( builtUpConcentrations  ) / 2 )	);
				
				
			}
			
		} else {
			
			if (tanhSquashModulationSignal) {
				plasticity = ( 1.0 + receptorStrength * Math.tanh(  plasticityModFunction.evaluate( bufferedConcentrations) / 2 ) );
				//plasticity = plasticity * ( 1.0 + receptorStrength * Math.tanh(  plasticityModFunction.evaluate( bufferedConcentrations) / 2 ) );
				
			} else {
				//#SPEEDTEST
				plasticity =  ( 1.0 + receptorStrength * plasticityModFunction.evaluate( bufferedConcentrations) );
				//plasticity = plasticity * ( 1.0 + receptorStrength * plasticityModFunction.evaluate( bufferedConcentrations) );
				
			}
		}

		//plasticity = plasticity * (1 + plasticityModFunction.evaluate(builtUpConcentrations));
		// plasticity = plasticity * \beta * p(Gas)
		
		
		if (plasticity >= maximumPlasticity) {
			plasticity = maximumPlasticity;
		}
		if (plasticity <= minimumPlasticity) {
			plasticity = minimumPlasticity;
		}
	
		
		
		//if (prePlasticity <  plasticity) {
			//System.out.println("Pre:" + prePlasticity + "  Post:"+ plasticity );
			//System.out.println(  "modeval= " + plasticityModFunction.evaluate( builtUpConcentrations ) );
			//for (double d: builtUpConcentrations) {
			//	System.out.println( d + " "  );
			//}
			//System.exit(1);
			
		//}
		
		
		
	}

	/**
	 * @return receptorID
	 */
	public String getReceptorID() {
		return receptorType;
	}

	/**
	 * @param receptorID
	 */
	public void setReceptorID(String receptorID) {
		this.receptorType = receptorID;
	}

	/**
	 * @return List of the gases
	 */
	public ArrayList<Integer> getGasList() {
		ArrayList<Integer> gases = new ArrayList<Integer>();
		//MEGATODOBUG? #3
		//test out if everything is handled properly in setup by just
		//returning gas list without adding inside the function here
		gases.add(1);
		gases.add(2);
		gases.add(3);
		gases.add(4);
		
		//return gasList;
		return gases;
		
	}

	/**
	 * @param gasList
	 */
	public void setGasList(ArrayList<String> gasList) {
		this.gasList = gasList;
		/*
		for (String gas: gasList) {
			if (builtUpConcentrations2.get(gas) != null) {
				System.out.println("WTH?");
				System.exit(-1);
			}
			this.builtUpConcentrations2.put(gas, 0.0);
		}
		//*/
		bufferedConcentrations = new double[NUMBER_GASES];

	}

	/**
	 * @return activationModFunction
	 */
	public PolynomialFunction getActivationModFunction() {
		return activationModFunction;
	}

	/**
	 * @param activationModFunction
	 */
	public void setActivationModFunction(PolynomialFunction activationModFunction) {
		this.activationModFunction = activationModFunction;
	}

	/**
	 * @return plasticityModFunction
	 */
	public PolynomialFunction getPlasticityModFunction() {
		return plasticityModFunction;
	}

	/**
	 * @param plasticityModFunction
	 */
	public void setPlasticityModFunction(PolynomialFunction plasticityModFunction) {
		this.plasticityModFunction = plasticityModFunction;
	}

	/**
	 * @return activationLevelPreSquash
	 */
	public double getActivationLevel() {
		return activationLevelPreSquash;
	}
		
	/**
	 * @return activationConcentration
	 */
	public double getActivationConcentration() {
		return bufferedConcentrations[activationTypeInt];
	}
	
	
	public void checkAndKillX(String message) {
		
		//System.out.println( message);
		/*
		if (  bufferedConcentrations.get(activationType) != bufferedConcentrations2[activationTypeInt]) {
			System.out.println("buffer doesnt match???");
			System.out.println(bufferedConcentrations.get(activationType) +"   "+ bufferedConcentrations2[activationTypeInt] );
			System.out.println( activationType +"   "+ activationTypeInt );
			System.out.println(builtUpConcentrations.get(activationType) + "  "+  builtUpConcentrations2[activationTypeInt] );
			System.exit(1);
		}
		
		if (  builtUpConcentrations.get(activationType) !=  builtUpConcentrations2[activationTypeInt]) {
			System.out.println("buiiltup doesnt match???");
			System.out.println(bufferedConcentrations.get(activationType) +"   "+ bufferedConcentrations2[activationTypeInt] );
			System.out.println( activationType +"   "+ activationTypeInt );
			System.out.println(builtUpConcentrations.get(activationType) + "  "+  builtUpConcentrations2[activationTypeInt] );
			System.exit(1);
		}
		//*/
	}
	
	
	/**
	 * @return activationConcentration
	 */
	public double getActivationConcentrationBuffer() {
		return bufferedConcentrations[activationTypeInt];

	}

	
	/**
	 * @param activationConcentration
	 */
	public void setActivationLevel(double activationConcentration) {
		this.activationLevelPreSquash = activationConcentration;
		System.out.println("This should not be called!");
		System.exit(-1);
	}

	/**
	 * @return activationType
	 */
	public int getActivationType() {
		return activationTypeInt;
	}

	/**
	 * @param activationType
	 */
	public void setActivationType(String activationType) {
		//must truncate G from "G1" "G0" etc
		this.activationTypeInt = new Integer( activationType.substring(1)  );
		this.activationType = activationType;
		this.bufferedConcentrations[activationTypeInt]= 0.0;

		
	}

	/**
	 * @return builtUpConcentrations
	 */
	public double[] getBufferedConcentrations() {
		return bufferedConcentrations;

	}



	/**
	 * This method will add concentrations to be buffered until the next timestep
	 * @param String gasType to be added to receptor buffer
	 * @param Double concentration amount of gas to be added
	 */
	public void addBufferedConcentration(int gasType, Double concentration) {

		double newValue = bufferedConcentrations[ gasType ];
		newValue+= concentration;
		bufferedConcentrations[ gasType] = newValue;

		
	}
	
	public double[] getBufferedConcentration() {
		return bufferedConcentrations;
	}
	
	
	public String getBufferedConcentrationText() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i< bufferedConcentrations.length; i++) {
			sb.append( "["+i+"]: "+ bufferedConcentrations[i] +"   "    );
		}
		return sb.toString();
	}
	


	/**
	 * @return plasticity
	 */
	public double getPlasticity() {
		return plasticity;
	}

	/**
	 * @param plasticity
	 */
	public void setPlasticity(double plasticity) {
		this.plasticity = plasticity;
	}
	
	/**
	 * @param gasList2
	 */
	public void initGasList(ArrayList<String> gasList2) {

		Arrays.fill(bufferedConcentrations,  0.0);
	}

	public void clear() {

		Arrays.fill(bufferedConcentrations,  0.0);
		
	}
	
	public String toString() {
		return "Receptor: "+receptorType;
	}

	
}