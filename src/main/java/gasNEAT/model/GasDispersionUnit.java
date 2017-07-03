package gasNEAT.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import gasNEAT.nn.GasNeatNeuron;

/**
 * GasDispersionUnit class implements functionality of increasing/decreasing
 * strength of gas based on the activation of source neuron, gas concentration
 * is then passed on to receiver neuron based on neuron position and gas
 * strength
 */
public class GasDispersionUnit implements Cloneable {

	private static Logger logger = Logger.getLogger( GasDispersionUnit.class );
	
	/** gas dispersion slot list to hold information of gas at each slot **/
	private ArrayList<GasDispersionSlot> gasDispersionSlotList;
	
	/** emitted gas type **/
	private String gasType;
	
	/** radius for emitted gas **/
	double emissionRadius;
	
	/** base strength for emitted gas **/
	private double baseStrength;
	
	/** current strength of emitted gas based on neuron activation **/
	private double currentStrength;
	
	/** propagation speed for gas per time unit **/
	private double propagationSpeed;
	
	/** increase in gas strength based on continuous neuron activation **/
	/////////private double strengthIncrease = -999;
	
	/** maximum allowed gas strength **/
	private double maximumAllowedStrength = 1.0;
	
	/** boolean variable to check if neuron is activated in previous step **/
	//private boolean isEmitted = false;
	
	/** gas dispersion type **/
	private String gasDispersionType;
	/**
	 * gas dispersion unit slot size based on speed of propagation and emission
	 * radius
	 **/
	private double slotSize;

	/**
	 * Constructor to create gas dispersion unit per each neuron and each
	 * emitted gas type
	 * 
	 * @param emissionRadius
	 *            radius for emitted gas
	 * @param currentStrength
	 *            current strength of emitted gas
	 * @param propagationSpeed
	 *            propagation speed for gas per time unit
	 * @param gasDispersionType
	 *            gas dispersion type
	 */
	public GasDispersionUnit(double emissionRadius, double currentStrength, double propagationSpeed, String gasDispersionType){
		//ULTRATODO - do I need to make the gas emit based upon strength?
		//Or use a purely stepwise function 0 or 1
		//, double decayRate) {
		this.gasDispersionSlotList = new ArrayList<GasDispersionSlot>();
		this.emissionRadius = emissionRadius;
		this.currentStrength = currentStrength;
		this.baseStrength = currentStrength;
		this.propagationSpeed = propagationSpeed;
		this.gasDispersionType = gasDispersionType;
	}

	/**
	 * Constructor to create gas dispersion unit per each neuron and each
	 * emitted gas type
	 * 
	 * @param emissionRadius
	 *            radius for emitted gas
	 * @param currentStrength
	 *            current strength of emitted gas
	 * @param gas
	 *            gas object with properties of emitted gas
	 */
	public GasDispersionUnit(double emissionRadius, double currentStrength, Gas gas) {
		this.gasDispersionSlotList = new ArrayList<GasDispersionSlot>();
		this.emissionRadius = emissionRadius;
		this.currentStrength = currentStrength;
		this.baseStrength = currentStrength;
		this.propagationSpeed = gas.getPropagationSpeed();
		this.gasDispersionType = gas.getGasDispersionType();
		this.gasType = gas.getGasID();
	}

	/**
	 * This method implements functionality of gas emission
	 * current gas emitting strength gets added to first slot 
	 * in gas dispersion unit so it advances further for each time unit
	 */
	public void emitGas() {
		
		if ( gasDispersionSlotList.size() > 0 ) {
			
			//System.out.println("currentStrength "+ currentStrength   );
			
			gasDispersionSlotList.get(0).setGasConcentration(currentStrength);
		} else {
			System.out.println("gasDispersionSlotList size= zero");
			System.exit(1);
		}
		
	}

	/**
	 * This method implements increase in the current gas strength if 
	 * source neuron gets activated continuously for consecutive time units
	 */
	public void increaseStrength() {
		
		logger.debug("increaseStrength   currentStrength "  +currentStrength);
		currentStrength += baseStrength;
		logger.debug("increaseStrength   currentStrength "  +currentStrength);
		
		
		if (currentStrength > maximumAllowedStrength ) {
			currentStrength = maximumAllowedStrength;
		}
		
	}

	/**
	 * This method implements decrease in the current gas strength if 
	 * source neuron does not get activated continuously for consecutive time units
	 * 
	 */
	public void decreaseStrength() {
		
		logger.debug("decreaseStrength   currentStrength "  +currentStrength);
		
		currentStrength -= baseStrength;
		logger.debug("decreaseStrength   currentStrength "  +currentStrength);
		
		
		if (currentStrength < 0.0 ) {
			currentStrength = 0.0;
		}
		
	}

	/**
	 * This method checks if gas is present at particular location at a given 
	 * time unit and updates receiver neurons within the gas dispersion slot
	 * 
	 * @param neuronMap	Neuron map having information about all neurons
	 */
	public void updateTargetNeurons(HashMap<String, GasNeatNeuron> neuronMap) {
		
		for (GasDispersionSlot channelSlot : gasDispersionSlotList) {
			
			// update neurons gas concentration if gas is present
			if (channelSlot.getGasConcentration() > 0) {
				
				// get each entry from the list of receiver neuron
				for (Map.Entry<String, Double> receiverNeuron : channelSlot.getReceiverNeurons().entrySet()) {
					
					//System.out.println("updateTargetNeurons");
					//System.out.println(channelSlot);
					//System.exit(1);
					
					// get individual neuron ID
					String updateNeuronID = receiverNeuron.getKey();

					// get neuron object for updating gas concentration
					GasNeatNeuron updateNeuron = neuronMap.get(updateNeuronID);

					// update build up gas concentration
					double buildUpGasConcentration = 0;

					// if gas dispersion type is FLAT then add the gas
					// concentration to target neuron
					if (gasDispersionType.equals("FLAT")) {
						buildUpGasConcentration = channelSlot.getGasConcentration();

						// if gas dispersion type is DECAY then
						// calculate build up concentration based on gas
						// concentration and neuron distance
					} else if (gasDispersionType.equals("GRADIENT")) {
						double neuronDistance = channelSlot.getReceiverNeurons().get(updateNeuronID);
						
						double concentration = Math.pow(Math.E, 
								-1* ( Math.pow(  
											neuronDistance/ emissionRadius, 2)
									)
								);
						//System.err.println( "Distance: "+ neuronDistance );
						//System.err.println( "emissionRadius: " + emissionRadius );
						//System.err.println( "concentration: "+ concentration );
						
						//TODO: this is going to be waaaaay to small...
						buildUpGasConcentration = channelSlot.getGasConcentration() * concentration;
						/// (neuronDistance * neuronDistance);
						//System.out.println("NEED TO FIX DECAY RATE MODEL HERE");
						//System.exit(1);
					} else {
						
						System.out.println("NEED TO SELECT gasDispersionType HERE");
						System.exit(1);
						
					}
					
					//buildUpGasConcentration += updateNeuron.getReceptor().getBuiltUpConcentrations()[ new Integer(gasType.substring(1))];
					//System.out.println("buildUpGasConcentration "+ buildUpGasConcentration +" "+gasType  );
					
					//System.out.println("THIS NEEDS TO BE CAREFULLY LOOKED AT WHEN YOU HIT THIS");
					//updateNeuron.getReceptor().addBufferedConcentration(gasType, buildUpGasConcentration);
					
					//for (double d: updateNeuron.getReceptor().getBufferedConcentration()) {
					//	System.out.print( d +"  "  );	
					//}
					
					updateNeuron.getReceptor().addBufferedConcentration(gasType, buildUpGasConcentration);
					
					//for (double d: updateNeuron.getReceptor().getBufferedConcentration()) {
					//	System.out.print( d +"  "  );
					//}
					//System.exit(1);
					
					//STARTHERE
					//#HOOK
					
					//OK we have the concentration getting loaded,
					//however it is not clear why the modulation is not taking place
					//but we must be very very close here.
					
					//updateNeuron.getReceptor().getBuiltUpConcentrations().put(gasType, buildUpGasConcentration);
					
					
				}
			}
		}
	}

	/**
	 * This method creates gas dispersion unit slots based on gas propagation speed
	 * and radius of emission. It also calculates distance of all receiver neurons
	 * and add them to particular gas dispersion unit slot
	 * 
	 * @param targetNeuronList	receivers neurons for emitted gas 
	 * @param sourceNeuron		Source neuron emitting gas
	 * 
	 */
	// Add receiver neurons on gas channel slot
	public void addNeuron(List<GasNeatNeuron> targetNeuronList, GasNeatNeuron sourceNeuron) {

		
		
		
		if (targetNeuronList.size() > 0) {
			
			//System.out.println("addNeuron"   );
			//System.out.println(  targetNeuronList );
			//System.exit(1);
					
			for (GasNeatNeuron targetNeuron : targetNeuronList) {
	
				
				
				// Calculate euclidean distance of the target neuron from the source
				// neuron
				if (!targetNeuron.getNeuronID().equals(sourceNeuron.getNeuronID())) {
	
					// get coordinates for target neuron
					double targetNeuronX = targetNeuron.getX();
					double targetNeuronY = targetNeuron.getY();
	
					//get coordinates for source neuron
					double sourceNeuronX = sourceNeuron.getX();
					double sourceNeuronY = sourceNeuron.getY();
	
					//calculate distance between source and target neuron
					double xCoord = Math.abs(targetNeuronX - sourceNeuronX);
					double yCoord = Math.abs(targetNeuronY - sourceNeuronY);
					double distance = Math.sqrt((Math.pow(yCoord, 2)) + (Math.pow(xCoord, 2)));
	
					for (int i = 0; i < gasDispersionSlotList.size(); i++) {
						//create gas dispersion slot
						GasDispersionSlot gasChannel = gasDispersionSlotList.get(i);
						double radius = gasChannel.getSlotRadius();
						double nextSlotRadius = radius + propagationSpeed;
	
						// Checking if the targets neurons location is within a
						// channel
						if (distance > radius && distance <= nextSlotRadius) {
							gasChannel.getReceiverNeurons().put(targetNeuron.getNeuronID(), distance);
						}
					}
				}
			} 
		} else {
				
			System.out.println("NULL GAS LIST PROVIDED!!!");
			targetNeuronList.get(0);
				
		}
		
	}

	/**
	 * Create gas dispersion unit slots
	 */
	@SuppressWarnings("unused")
	// This method will create a gas channel with empty GasChannelSlot objects
	public void createGasChannel() {
		
		//System.out.println( "emissionRadius "+emissionRadius  );
		//System.out.println( "propagationSpeed "+ propagationSpeed );
		
		
		// calculate total number of slots of the gas channel
		double totalSlots = emissionRadius / propagationSpeed;

		// slot size will be equal to propagation speed
		slotSize = emissionRadius / totalSlots;
		
		//System.out.println( "totalSlots "+ totalSlots );

		//create all slots in given gas channel
		for (int i = 0; i < totalSlots; i++) {
			gasDispersionSlotList.add(new GasDispersionSlot(slotSize * i, slotSize * (i-1) ));

		}
	}

	/**
	 * This method implements advancing gas from one slot to next slot.
	 * This method will be called on every tick to propagate gas to next slot.
	 */
	public void advance() {

		boolean notFirstSlot = false;
		double previousConcentration = 0;
		// loop through each slot in gas channel and advance gas from one slot
		// to another
		for (GasDispersionSlot channelSlot : gasDispersionSlotList) {
			if (notFirstSlot) {
				double tempConcentration = channelSlot.getGasConcentration();
				channelSlot.setGasConcentration(previousConcentration);
				previousConcentration = tempConcentration;
			} else {
				notFirstSlot = true;
				previousConcentration = channelSlot.getGasConcentration();
				channelSlot.setGasConcentration(0);
			}
		}
	}

	/**
	 * This method creates clone for gas dispersion unit 
	 * to new gas dispersion unit
	 *  
	 * @throws CloneNotSupportedException	Exception for clone not supported
	 */
	public GasDispersionUnit clone() throws CloneNotSupportedException {
		GasDispersionUnit gasDispersionUnit = (GasDispersionUnit) super.clone();
		gasDispersionUnit.setGasDispersionSlotList(deepClone(gasDispersionSlotList));
		return gasDispersionUnit;
	}

	/**
	 * This method creates deep clone for gas dispersion unit 
	 * to new gas dispersion unit
	 *  
	 * @param gasDispersionUnitSlotList	input gas Dispersion Unit Slot List
	 * @return list	copied gas Dispersion Unit Slot List
	 * @throws CloneNotSupportedException	Exception for clone not supported
	 */
	private ArrayList<GasDispersionSlot> deepClone(ArrayList<GasDispersionSlot> gasDispersionUnitSlotList) throws CloneNotSupportedException {
		ArrayList<GasDispersionSlot> list = new ArrayList<GasDispersionSlot>();
		for (GasDispersionSlot slot : gasDispersionUnitSlotList)
			list.add((GasDispersionSlot) slot.clone());
		return list;
	}

	/**
	 * Getter method for gasDispersionSlotList
	 * 
	 * @return gasDispersionSlotList Array list to hold gas dispersion unit slots
	 */
	public ArrayList<GasDispersionSlot> getGasDispersionSlotList() {
		return gasDispersionSlotList;
	}

	/**
	 * Setter method for gasDispersionSlotList
	 * 
	 * @param gasDispersionSlotList
	 *            Array list to hold gas dispersion unit slots
	 */
	public void setGasDispersionSlotList(ArrayList<GasDispersionSlot> gasDispersionSlotList) {
		this.gasDispersionSlotList = gasDispersionSlotList;
	}

	/**
	 * Getter method for emissionRadius
	 * 
	 * @return emissionRadius radius for emitted gas
	 */
	public double getEmissionRadius() {
		return emissionRadius;
	}

	/**
	 * Setter method emissionRadius
	 * 
	 * @param emissionRadius
	 *            radius for emitted gas
	 */
	public void setEmissionRadius(double emissionRadius) {
		this.emissionRadius = emissionRadius;
	}

	/**
	 * Getter method for currentStrength
	 * 
	 * @return currentStrength current strength of emitted gas based on neuron
	 *         activation
	 */
	public double getCurrentStrength() {
		return currentStrength;
	}

	/**
	 * Setter method for currentStrength
	 * 
	 * @param currentStrength
	 *            current strength of emitted gas based on neuron activation
	 */
	public void setCurrentStrength(double currentStrength) {
		this.currentStrength = currentStrength;
	}

	/**
	 * Getter method for propagationSpeed
	 * 
	 * @return propagationSpeed propagation speed for gas per time unit
	 */
	public double getPropagationSpeed() {
		return propagationSpeed;
	}

	/**
	 * Setter method for propagationSpeed
	 * 
	 * @param propagationSpeed
	 *            propagation speed for gas per time unit
	 */
	public void setPropagationSpeed(double propagationSpeed) {
		this.propagationSpeed = propagationSpeed;
	}
	
	/**
	 * Getter method for slotSize
	 * 
	 * @return slotSize gas dispersion unit slot size 
	 * 					based on speed of propagation and  emission radius
	 */
	public double getSlotSize() {
		return slotSize;
	}

	/**
	 * Setter method for slotSize
	 * 
	 * @param slotSize
	 *            gas dispersion unit slot size 
	 *            based on speed of propagation and  emission radius
	 */
	public void setSlotSize(double slotSize) {
		this.slotSize = slotSize;
	}

	@Override
	public String toString() {
		return "GasDispersionUnit [gasDispersionSlotList=" + gasDispersionSlotList + ", gasType=" + gasType
				+ ", emissionRadius=" + emissionRadius + ", baseStrength=" + baseStrength + ", currentStrength="
				+ currentStrength + ", propagationSpeed=" + propagationSpeed // ", strengthIncrease=" + strengthIncrease
				+ ", maximumAllowedStrength=" + maximumAllowedStrength //+ ", isEmitted=" + isEmitted
				+ ", gasDispersionType=" + gasDispersionType + ", slotSize=" + slotSize + "]";
	}

}
