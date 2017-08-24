package gasNEAT.nn;

import static gasNEAT.view.Constants.NEURON_CIRCLE_RADIUS;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunction;
import com.anji.nn.ActivationFunctionFactory;
import com.anji.nn.Neuron;
import com.anji.util.Properties;

import gasNEAT.builders.NeuronBuilder;
import gasNEAT.configurations.GasNeatConfiguration;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import gasNEAT.model.GasDispersionUnit;
import gasNEAT.model.GasNeatReceptor;
import gasNEAT.model.GasNeatSynapse;
import lombok.Getter;

public class GasNeatNeuron extends Neuron {

	private @Getter double plasticityParameterA;
	private @Getter double plasticityParameterB;
	private @Getter double plasticityParameterC;
	private @Getter double plasticityParameterD;
	private @Getter double plasticityParameterLR;
	
	//#ADDPROPS
	private @Getter double timingConstant;
	private @Getter double receptorStrength;
	
	
	/** Unique Id of Neuron*/
	private String neuronID;
	
	private long neuronIDLong;
	
	/** Neuron Layer type*/
	private NeuronType layerType;
	
	/** Gas type emitted by Neuron G0 indicates no gas produced, just synapses */
	private String gasProductionType;
	private int gasProductionTypeInt;
	
	
	/** Gas type emitted by Neuron via synapses G0 = regular transmission*/
	private String synapticGasType;
	
	private int synapticGasTypeInt;
	
	
	/** Neuron X coordinate on a 2D plane*/
	private int x;
	
	/** Neuron Y coordinate on a 2D plane*/
	private int y;
	
	/** Neuron circle radius*/
	private double radius = NEURON_CIRCLE_RADIUS;
	
	/** Bas production of Gas*/
	private double baseProduction;
	
	/** Gas emission radius*/
	private double emissionRadius;
	
	
	/** Outgoing Synapses List*/
	private ArrayList<Long> outgoingSynapsesList;
	
	
	/** Threshold of Neuron*/
	private double threshold;
	
	/** Boolean for storing Gas receiving flag*/
	private boolean isGasReceiver;
	
	/** Neuron gas color*/
	private Color gasColor;
	
	/** Neuron gas dispersion unit*/
	private GasDispersionUnit gasDispersionUnit;

	
	/** Enum to select behavior for calculating output concentration upon firing*/
	private ActivationFunction activationFunction;
	
	/** Receptor class reference variable*/
	private GasNeatReceptor receptor;
	
	private Properties props;
	
	/** logger instance */
	private static Logger LOG = Logger.getLogger( GasNeatNeuron.class );
	
	
	
	public GasNeatNeuron(ActivationFunction activationFunction, Properties props) {
		super(activationFunction);
		this.props = props;

		
		this.x = -1;
		this.y = -1;
		outgoingSynapsesList = new ArrayList<Long>();
		
		//ULTRATODO
		//synapticGasType = "G0"; 
		//gasType = "G0";

	}
	
	
	public void init(Properties props) {
		this.props=props;
	}
	
	//TODO: could change this to be a constructor or builder, this( new NeuronBuilder( neuronAllele) );
	public void setPropertiesFromAllele(GasNeatNeuronAllele neuronAllele) {
		
		plasticityParameterA = neuronAllele.getPlasticityParameterA();
		plasticityParameterB = neuronAllele.getPlasticityParameterB();
		plasticityParameterC = neuronAllele.getPlasticityParameterC();
		plasticityParameterD = neuronAllele.getPlasticityParameterD();
		plasticityParameterLR = neuronAllele.getPlasticityParameterLR();
		
		//#ADDPROPS
		receptorStrength = neuronAllele.getReceptorStrength();
		timingConstant = neuronAllele.getTimingConstant();
		
		
		//if we MUST keep strings, ughhh
		this.neuronID = "N"+neuronAllele.getInnovationId();
		this.neuronIDLong = neuronAllele.getInnovationId();
		
		this.setLayerType(  neuronAllele.getType() );
		this.layerType = neuronAllele.getType();
		
		if (  neuronAllele.getXCoordinate() < 0 ) {
			//System.err.println("NO X coor set!");
			
			
		}
		
		this.x = neuronAllele.getXCoordinate();
		this.y = neuronAllele.getYCoordinate();
		
		if ( neuronAllele.getReceptorType().equals( "") ) {
			System.out.println("Neuron Receptor Type should never be empty!");
			System.out.println( neuronAllele );
			try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}
		
		
		//TODO: this needs to be redone carefully, can hold it for now.
		if ( neuronAllele.getReceptorType().equals("G0_NO_NO_NO_NO")) {
			this.isGasReceiver= false;
		} else {
			this.isGasReceiver=true;
		}
		
		
		
		if (neuronAllele.getGasEmissionType() != 0 ) {
			this.gasProductionType = "G"+neuronAllele.getGasEmissionType();
			gasProductionTypeInt = neuronAllele.getGasEmissionType();
		} else {
			this.gasProductionType = "G0";
			gasProductionTypeInt = 0;
		}
		

		//TODO: NEEDS to be completed in constructor
		//System.out.println("CREATING RECEPTOR WITH TYPE: " + neuronAllele.getReceptorType() );
		this.receptor = new GasNeatReceptor( ""+neuronAllele.getReceptorType(), receptorStrength );
		receptor.init(props);
		
		this.emissionRadius = neuronAllele.getGasEmissionRadius();
		
		if (   0 == neuronAllele.getGasEmissionStrength() && neuronAllele.getGasEmissionType() != 0 ) {
			System.out.println("GAS EMISSION STRENGTH=-1");
			System.out.println( neuronAllele  );
			System.out.println(  neuronAllele.getGasEmissionStrength()  );
			System.out.println(  neuronAllele.getGasEmissionType() 	);
			System.out.println( "Gas emitter must have non-zero emission strength!"   ); 
			System.exit(1);
		}
		
		this.baseProduction = neuronAllele.getGasEmissionStrength();
		
		//TODO: need to get rid of strings everywhere!
		this.gasProductionType = "G"+neuronAllele.getGasEmissionType();
		gasProductionTypeInt = neuronAllele.getGasEmissionType();
		
		if ( neuronAllele.getSynapticGasEmissionType() < 0 ) {
			System.out.println("Synaptic Gas Emission should be set and cannot be zero!");
			System.out.println( "neuronAllele: " + neuronAllele.toString()  );
			System.out.println( "getSynapticGasEmissionType: " +neuronAllele.getSynapticGasEmissionType()  );
			System.exit(-1);
		}
		
		this.synapticGasType = "G"+neuronAllele.getSynapticGasEmissionType();
		
		this.synapticGasTypeInt = neuronAllele.getSynapticGasEmissionType();
		
		
		this.threshold = neuronAllele.getFiringThreshold();
		
		//sigmoid, log, linear, etc
		ActivationFunctionFactory factory = ActivationFunctionFactory.getInstance();
		this.activationFunction = factory.get( neuronAllele.getActivationType().toString() );

		outgoingSynapsesList = new ArrayList<Long>();
		
	}
			
	/**
	 * Updates the concentration of the receptor Neuron by setting the
	 * activation level from the builtUpConcentrations
	 * 
	 */
	public void updateActivationLevelFromAndResetBuiltUpConcentrations() {
		
		receptor.updateActivationLevelFromBuiltUpConcentrations();
		
		//If there modulation to be done on the activation level (multiply)
		//Then it is handled here
		receptor.modulateActivationLevelFromConcentrations();
		
		//May need to be altered
		receptor.clearBuiltUpConcentrations();
	}

	/**
	 * Calculates the Activation value of a Neuron for changing the weight of
	 * Synapse
	 * 
	 * @param concentration
	 *            Concentration of Neuron
	 * @return signalAmplitude Signal Amplitude
	 * 
	 * @return double
	 */
	public double calculateActivation(double concentration) {
		return activationFunction.apply(concentration - threshold);
	}

	/**
	 * Calculates the activation of a Neuron
	 * 
	 * @return signalAmplitude Signal Amplitude
	 */
	public double calculateActivation() {
		
		/*/ #PERFORMANCE BOOST
		if (LOG.isDebugEnabled() ) {
			LOG.debug("receptor.getActivationLevel() "+ receptor.getActivationLevel()   );
			LOG.debug("threshold "  + threshold );
			LOG.debug("activationFunction " + activationFunction.toString() );
		}
		//*/
		return activationFunction.apply( receptor.getActivationLevel() - threshold  );
	}
	

	/**
	 * Creates a clone of the Neuron
	 * 
	 * @return neuron Clone of Neuron Object
	 */
	public GasNeatNeuron clone()  throws CloneNotSupportedException {
		GasNeatNeuron neuron = (GasNeatNeuron) super.clone();
		neuron.setSynapsesList((ArrayList<Long>) outgoingSynapsesList.clone());

		
		//TODO: verify later that it is what it should be
		if (neuron.isGasEmitter() ) {
			neuron.setGasDispersionUnit((GasDispersionUnit) gasDispersionUnit.clone());
		}
		
		if ( !neuron.synapticGasType.equals( synapticGasType) ) {
			//TODO FIXME remove after sanity run
			System.out.println("Clone failed");
			assert(false);
		}
		
		return neuron;
	}

	/**
	 * Returns ArrayList of outgoing Synapses
	 * 
	 * @return synapseList Outgoing Synapses List
	 */
	public ArrayList<Long> getOutgoingSynapses() {
		return outgoingSynapsesList;
	}

	/**
	 * Returns NeuronID
	 * 
	 * @return neuronID Neuron ID
	 */
	public long getNeuronID() {
		return getId();
	}


	/**
	 * Returns layer of the Neuron
	 * 
	 * @return layerType Layer of Neuron
	 */
	public NeuronType getLayerType() {
		return layerType;
	}

	/**
	 * Sets layer of Neuron
	 * 
	 * @param layerType
	 *            Layer of Neuron
	 */
	public void setLayerType(NeuronType layerType) {
		this.layerType = layerType;
	}

	/**
	 * Sets X coordinate of Neuron in the a 2D plane
	 * 
	 * @param x
	 *            Location of Neuron Object on X axis
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets Y coordinate of Neuron in the a 2D plane
	 * 
	 * @param y
	 *            Location of Neuron Object on X axis
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns threshold of a Neuron
	 * 
	 * @return threshold Threshold of Neuron
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * Sets threshold of a Neuron
	 * 
	 * @param threshold
	 *            Threshold of Neuron
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * Returns activationConcentration
	 * 
	 * @return activationConcentration Activation concentration
	 */
	public double getActivationConcentration() {
		return receptor.getActivationConcentration();
	}
	
	
	public double getActivationLevel() {
		return receptor.getActivationLevel();
	}
	
	public double getActivationConcentrationBuffer() {
		return receptor.getActivationConcentrationBuffer();
	}
	
	

	/**
	 * Sets activationConcentration
	 * 
	 * @param concentration
	 *            Activation concentration
	 */
	/*
	public void setActivationConcentration(double concentration) {
		receptor.setGasConcentration( receptor.getActivationType(), concentration);
		System.out.println( "Should add to buffer instead!"  );
		System.exit(-1);
	}
	*/
	
	public void addToActivationConcentrationGasBuffer( double concentrationToAdd) {
		receptor.addBufferedConcentration( receptor.getActivationType()  , concentrationToAdd);
		//receptor.setGasConcentrationBuffer(receptor.getActivationType(), concentrationToAdd );
	}
	
	//public HashMap<String, Double> getBufferedConcentration() {
	public double[] getBufferedConcentration() {
		
		return receptor.getBufferedConcentration();
	}
	
	/**
	 * Sets setGasConcentration
	 * 
	 * @param gas, concentration
	 *            Activation concentration
	 */
	/*
	
	public void setGasConcentration(int gas, double concentration) {
		receptor.setGasConcentration( gas, concentration);
	}
	*/
	

	/**
	 * Returns radius of neurons
	 * 
	 * @return radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets radius of a Neuron
	 * 
	 * @param radius
	 *            Radius of Neuron in a 2D plane
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Returns BaseProduction of a Neuron
	 * 
	 * @return baseProduction Gas base production value
	 */
	public double getBaseProduction() {
		return baseProduction;
	}

	/**
	 * Sets BaseProduction of a Neuron
	 * 
	 * @param baseProduction
	 *            Gas base production value
	 */
	public void setBaseProduction(double baseProduction) {
		this.baseProduction = baseProduction;
	}

	/**
	 * Returns gas emissionRadius
	 * 
	 * @return emissionRadius Gas emission radius of Neuron in a 2D plane
	 */
	public double getEmissionRadius() {
		return emissionRadius;
	}

	/**
	 * Sets gas emissionRadius
	 * 
	 * @param emissionRadius
	 *            Gas emission radius of Neuron in a 2D plane
	 */
	public void setEmissionRadius(double emissionRadius) {
		this.emissionRadius = emissionRadius;
	}

	/**
	 * Returns gasChannel
	 * 
	 * @return gasChannel Gas channel of Neuron
	 */

	public GasDispersionUnit getGasDispersionUnit() {
		return gasDispersionUnit;
	}

	/**
	 * Sets gasChannel of a Neuron
	 * 
	 * @param gasChannel
	 *            Gas channel of Neuron
	 */

	public void setGasDispersionUnit(GasDispersionUnit gasChannel) {
		this.gasDispersionUnit = gasChannel;
	}

	/**
	 * Returns true if a Neuron receives gas
	 * 
	 * @return gasReceiver True of Neuron is a gas Receiver
	 */
	public boolean isGasReceiver() {
		return isGasReceiver;
	}

	/**
	 * Sets gas receiving flag of a neuron
	 * 
	 * @param gasReceiver
	 *            True of Neuron is a gas Receiver
	 */
	public void setGasReceiver(boolean gasReceiver) {
		this.isGasReceiver = gasReceiver;
	}

	/**
	 * Returns Neuron gasType of Neuron
	 * 
	 * @return gasType Gas type a Neuron
	 */
	//public String getGasProductionType() {
	//	return gasProductionType;
	//}
	
	
	/**
	 * Returns Neuron gasType of Neuron
	 * 
	 * @return gasType Gas type a Neuron
	 */
	public int getGasProductionTypeInt() {
		return gasProductionTypeInt;
	}

	/**
	 * Sets gasType of Neuron
	 * 
	 * @param gasType
	 *            Gas type of a Neuron
	 */
	public void setGasType(String gasType) {
		this.gasProductionType = gasType;
	}

	/**
	 * Returns true if a Neuron is a gas emitter
	 * 
	 * @return gasEmitter True if Neuron is gas emitter
	 */
	public boolean isGasEmitter() {
		
		int g = getGasProductionTypeInt();
				//Integer.parseInt(getGasProductionType().substring(1) );
		if (g < 0) {
			System.out.println( "Cannot have negative gas production type: " + getGasProductionTypeInt() );
			System.exit(1);
		}
		
		//gasproduction type must be 0 or higher, if it is zero that means it is not a gas producer
		return   g > 0;
	}

	/**
	 * Returns Activation function of a Neuron
	 * 
	 * @return activationFunction Activation Function of Neuron
	 */
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}
	
	public ActivationFunction getFunction() {
		return activationFunction;
	}

	/**
	 * Sets the activationFunction of a Neuron
	 * 
	 * @param activationFunction
	 *            Activation Function of Neuron
	 */
	public void setActivationFunction(final ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	/**
	 * Returns the location x coordinate of Neuron in 2D plane
	 * 
	 * @return x X Coordinate of Neuron
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets outgoing SynapasesList of a Neuron
	 * 
	 * @param synapsesList
	 *            Outgoing Synapse List
	 */
	public void setSynapsesList(ArrayList<Long> synapsesList) {
		this.outgoingSynapsesList = synapsesList;
	}

	/**
	 * Returns Y coordinate of Neuron on 2D plane
	 * 
	 * @return y Y Coordinate of Neuron
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns gasColor
	 * 
	 * @return gasColor Color of gas emitted by Neuron
	 */
	public Color getGasColor() {
		return gasColor;
	}

	/**
	 * Sets gasColor
	 * 
	 * @param gasColor
	 *            Color of gas emitted by Neuron
	 */
	public void setGasColor(Color gasColor) {
		this.gasColor = gasColor;
	}

	/**
	 * Returns Receptor
	 * 
	 * @return receptor Returns the receptor object of Neuron
	 */
	public GasNeatReceptor getReceptor() {
		return receptor;
	}

	/**
	 * Sets Receptor
	 * 
	 * @param receptor
	 *            receptor Returns the receptor object of Neuron
	 */
	public void setReceptor(GasNeatReceptor receptor) {
		this.receptor = receptor;
	}
	
	
	/**
	 * @param synapse
	 */
	public void addOutgoingConnection(GasNeatSynapse synapse) {
		this.outgoingSynapsesList.add(synapse.getSynapseID());
	}
	
	//Must use this or else the lookup doesn't work properly in
	// NetworkBuilder::buildSynapsesMap   ... source.addOutgoingConnection( synapse );
	@Override
	public long getId() {
		return neuronIDLong;
		//return new Long( neuronID.substring(1) );
		
	}
	
	
	public String getIdString() {
		return neuronID;
		//return new Long( neuronID.substring(1) );
		
	}
	
	@Override
	public String toString() {
		return "GasNeatNeuron [neuronID=" + neuronID + ", layerType=" + layerType + ", gasType=" + gasProductionType + ", x=" + x
				+ ", synapticGasType= "+ synapticGasType
				+ ", y=" + y + ", radius=" + radius + ", baseProduction=" + baseProduction + ", emissionRadius="
				+ emissionRadius + ", synapsesList=" + outgoingSynapsesList + ", threshold=" + threshold + ", isGasReceiver="
				+ isGasReceiver + ", gasColor=" + gasColor + ", gasDispersionUnit=" + gasDispersionUnit
				+ ", activationFunction=" + activationFunction + ", receptor=" + receptor + "]";
	}

	//get the gas produced in synaptic connections
	public String getSynapseProductionType() {
		return synapticGasType;
	}
	
	//get the gas produced in synaptic connections
		public int getSynapseProductionTypeInt() {
			return synapticGasTypeInt;
		}

	//remove all state information
	public void clear() {
		receptor.clear();
	}
	
	

}
