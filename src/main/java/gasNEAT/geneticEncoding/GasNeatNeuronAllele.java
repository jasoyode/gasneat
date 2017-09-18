package gasNEAT.geneticEncoding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgap.Allele;

import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronType;

import gasNEAT.configurations.GasNeatConfiguration;
import lombok.Getter;
import lombok.Setter;


/** Similar to the NeuronAllele class in com.anji.neat
 * It just needs to do something similar for that for our neural network instead of the AnjiNet **/
public class GasNeatNeuronAllele extends NeuronAllele {
	
	private @Setter @Getter double plasticityParameterA;
	private @Setter @Getter double plasticityParameterB;
	private @Setter @Getter double plasticityParameterC;
	private @Setter @Getter double plasticityParameterD;
	private @Setter @Getter double plasticityParameterLR;
	
	//#ADDPROPS
	private @Setter @Getter double timingConstant;
	private @Setter @Getter double receptorStrength;
	
	/** Used to known where to crossover*/
	private GasNeatNeuronGene gasNeatNeuronGene;
	
	/** x coordinate of NeuronGene in 2D plane*/
	private int xCoordinate = -999;
	
	/** y coordinate of NeuronGene in 2D plane*/
	private int yCoordinate = -999;
	
	/** Threshold of NeuronGene*/
	private @Setter @Getter double firingThreshold = 0;
	
	/** Receptor type only for now */
	//private int receptorType = 0; // 0 is standard receptor
	private String receptorType = ""; // 0 is standard receptor

	/** What gas is produced*/
	private int gasEmissionType = 0; //0 = no gas
	
	/** What gas is produced*/
	// This allows a neuron to emit a gas or to just send neuromodulators via
	//synaptic connections
	private int synapticGasEmissionType = 0; //0= standard 1=G1 etc
	
	/** How much gas is produced?*/
	private double gasEmissionStrength = -999;
	
	/** How far can the gas reach?*/
	private int gasEmissionRadius = -999;
	
	private double gasSpeed = -999;
	
	/**
	 * @param gasNeatNeuronGene
	 */
	public GasNeatNeuronAllele(GasNeatNeuronGene gasNeatNeuronGene) {
		super(gasNeatNeuronGene);
		this.gasNeatNeuronGene = gasNeatNeuronGene;
	}
	
	private static Logger logger = Logger.getLogger( GasNeatNeuronAllele.class );
	
	/**
	 * @see jgap.Allele#cloneAllele()
	 */
	@Override
	public Allele cloneAllele() {
		GasNeatNeuronAllele allele = new GasNeatNeuronAllele( this.gasNeatNeuronGene );
		allele.setFiringThreshold( getFiringThreshold() );
		allele.setGasEmissionRadius( getGasEmissionRadius() );
		allele.setGasEmissionStrength(getGasEmissionStrength());
		allele.setGasEmissionType(getGasEmissionType());
		allele.setSynapticGasEmissionType( getSynapticGasEmissionType() );
		allele.setXCoordinate(getXCoordinate());
		allele.setYCoordinate(getYCoordinate());
		allele.setReceptorType(getReceptorType());
		allele.setPlasticityParameterA(plasticityParameterA);
		allele.setPlasticityParameterB(plasticityParameterB);
		allele.setPlasticityParameterC(plasticityParameterC);
		allele.setPlasticityParameterD(plasticityParameterD);
		allele.setPlasticityParameterLR(plasticityParameterLR);
		allele.setGasSpeed( getGasSpeed() );
		//#ADDPROPS  not used currently
		allele.setTimingConstant( getTimingConstant() );
		allele.setReceptorStrength( getReceptorStrength() );
		return allele;
	}


	
	/* (non-Javadoc)
	 * @see org.jgap.Allele#distance(org.jgap.Allele)
	 */
	@Override
	public double distance(Allele target) {
		//Distance 1.0 should mean 100% certain different functionality
		//Distance 0.0 should be identical function
		//Distance 0.5 should have a 50% chance of producing same function
		//Distance 0.25 should have 75% chance of same function
		
		GasNeatNeuronAllele other = (GasNeatNeuronAllele)target;
		
		if ( gasSpeed < 0 || other.gasSpeed < 0) {
			logger.error("Gas Speed cannot be zero!");
			System.exit(-1);
		}
		
		
		//initially distance is zero, increase as relevant properties differ
		double distance = 0.0;
		double xDiff =  Math.abs( xCoordinate - other.getXCoordinate() );
		double yDiff =  Math.abs( yCoordinate - other.getYCoordinate() );
		//based on distance formula - should be based upon gasSpeed
		double cartesianDistance = Math.sqrt(  xDiff * xDiff + yDiff *yDiff );
		//regardless of activation type, this increases relative distance
		
		//Could add a constant to represent the weight of this distance calculation
		//to many parameters as it is, if difference is 1.0+ almost guaranteed different
		distance += Math.abs( firingThreshold - other.getFiringThreshold() );
		
		//If one neuron produces gas and the other does not
		//there is a fundamental difference in their connectivity and likely timescale
		//maximize the calculated difference!
		if (  gasEmissionType != other.getGasEmissionType() ) {
			//This mean these neurons produce different gases (or only one produces gas)
			return 1.0;
		
		
		} else if (gasEmissionType == 0 ){ 
			//Inside of this block means these are both standard electric emitting neurons

			if ( synapticGasEmissionType != other.getSynapticGasEmissionType() ) {
				//This means the neuron is producing electric BUT different signal, necess. different function
				return 1.0;
				
			} else { 
				//This means they produce the same synaptic gas
				double receptorDistance = receptorDistance(getReceptorType(), other.getReceptorType(), cartesianDistance/gasSpeed ) ;
				distance += receptorDistance;
			} 			
		} else {
			
			//This means these are both gas emitting neurons of the same gas
			distance += ((cartesianDistance / gasSpeed) / 10.0);
			
			//gas difference in strength is very important
			distance += Math.abs( gasEmissionStrength - other.getGasEmissionStrength()  );

			//emission radius is based upon gas speed, and also only a little bit
			distance += Math.abs( gasEmissionRadius - other.getGasEmissionRadius()  ) / gasSpeed/ 10.0;

			double receptorDistance = receptorDistance(getReceptorType(), other.getReceptorType(), cartesianDistance/gasSpeed ) ;
			distance += receptorDistance;
			
		}
		
		distance += plasticityDistance(this, other);
		
		//maximum return value of 1.0
		if (distance >= 1.0) {
			return 1.0;
		} else {
			return distance;
		}

	}
	
	
	//when two neurons have different plasticity parameters that can count for genetic distance\
	//should only count when plasticity can happen, but plasticity rules should not evolve
	//when there is no plasticity allowed
	//technically we should bypass this somethow
	private static double plasticityDistance(GasNeatNeuronAllele n1, GasNeatNeuronAllele n2) {
		
		//ULTRATODO #3 SPEED UP make config to save these checks
		//if ( !GasNeatConfiguration.getPlasticityEnabled() ) {
		//	return 0.0;
		//}
		
		double distance= 0.0;
		
		//maximum -1 to +1 for distance of 2/10 = 0.2 x 5 = 1.0
		distance += Math.abs( n1.plasticityParameterA - n2.plasticityParameterA )/10.0;
		distance += Math.abs( n1.plasticityParameterB - n2.plasticityParameterB )/10.0;
		distance += Math.abs( n1.plasticityParameterC - n2.plasticityParameterC )/10.0;
		distance += Math.abs( n1.plasticityParameterD - n2.plasticityParameterD )/10.0;
		distance += Math.abs( n1.plasticityParameterLR - n2.plasticityParameterLR ) /1000.0;
		if (distance >= 1.0) {
			return 1.0;
		} else {
			return distance;
		}
	}
	
	

	private static double receptorDistance(String type1, String type2, double cartesianDistanceTime) {
		double distance = 0.0;
		if ( !type1.substring(0, 2).equals( type2.substring(0, 2)) ) {
			//if they have different activation types then max distance
			return 1.0;
		}
		
		//Below here means we have the same activation type
		
		//#RECEPTORHARDCODE
		for (int i=3; i<13; i+=3) {
			if ( !type1.substring(i, i+2).equals( type2.substring(i, i+2)) ) {
				//if they have different modulating receiver types change them
				//maximum 50% distance
				distance += 0.1;
			}			
		}
		
		//if there are no modulations possible cartesianDistanceTime does not matter
		if (type1.equals("G0_NO_NO_NO_NO") &&  type2.equals("G0_NO_NO_NO_NO")) {
			return distance;
		}
		
		//this may mean that our cartesian distance could have an impact
		
		//effectively if there is a potental 1 time unit delay make that a 0.1
		//difference - greater than 10 should be totally different function
		//#RECEPTORHARDCODE
		if (type1.substring(0,2).equals("G0")) {
			//position is less important if both are electrically activated					
			distance += (cartesianDistanceTime / 100.0);
		} else {
			//if they are gas activated then distance is more important!
			distance += (cartesianDistanceTime / 10.0);	
		}
		return distance;
	}

	/* (non-Javadoc)
	 * @see org.jgap.Allele#setToRandomValue(java.util.Random)
	 */
	@Override
	public void setToRandomValue( Random rand) { 

		ArrayList<String> receptorList = GasNeatConfiguration.getReceptorMap();
		String defaultReceptorType = receptorList.get(0);
		
		String randomizedReceptorType = receptorList.get(  rand.nextInt( receptorList.size() ) );
		
		int numberGases = GasNeatConfiguration.getNumberGases();
		int verticalPadding =20;
		int outputLayerX = 200;
		
		//setGasEmissionRadius( GasNeatConfiguration.getInitialEmissionRadius()  ); 
		
		//dnot invoke rand
		if ( GasNeatConfiguration.getMinEmissionRadius() == GasNeatConfiguration.getMaxEmissionRadius() ) {
			setGasEmissionRadius(  GasNeatConfiguration.getMinEmissionRadius() );
		} else {
			setGasEmissionRadius( GasNeatConfiguration.getMinEmissionRadius() + 
					rand.nextInt(  1 + 
							GasNeatConfiguration.getMaxEmissionRadius() - GasNeatConfiguration.getMinEmissionRadius() )   
					);
		}
		
		
		setGasSpeed( GasNeatConfiguration.getGasSpeed()  );
		//unless its emitting gas leave at 0
		setGasEmissionStrength( 0.1 );
		//setGasEmissionStrength( rand.nextDouble() );		
		
		setReceptorType(  receptorList.get( 0 )  );
		
		//TODO: maybe replace with locations on a unit circle
		// using cos and sin moving progressively farther from the center should work
		// id=0 = -1,0
		// id=1 = -1+dx,dy
		// id=2 = -1-dx,-dy
		// id=1 = -1+2dx,2dy
		// id=2 = -1-2dx,-2dy
		
		//Calculate appropriate position via innovation id type
		//inputs, just add verticalPadding*innovation_id= y
		
		//INPUT NEURONS
		if (this.getType() == NeuronType.INPUT ) {
			long y = verticalPadding + this.getInnovationId()*verticalPadding;
			setYCoordinate( (int)y );
			setXCoordinate( verticalPadding );
			
			//only change to gas type if this is > 0
			if ( GasNeatConfiguration.getRandomizeInputGasEmittedRate() > 0 ) {
				if ( rand.nextDouble() < GasNeatConfiguration.getRandomizeInputGasEmittedRate() ) {
					setGasEmissionType( 1 + rand.nextInt( GasNeatConfiguration.getNumberGases() )  );
					setGasEmissionStrength( rand.nextDouble() );
				}
			}
			//only change synaptic gas if this is > 0
			if ( GasNeatConfiguration.getRandomizeInputSynapticGasRate() > 0) {
				if ( rand.nextDouble() < GasNeatConfiguration.getRandomizeInputSynapticGasRate() ) {
					setSynapticGasEmissionType( 1 + rand.nextInt( GasNeatConfiguration.getNumberGases())  );
				}
			}
			//only change rceptor if this is great than 0
			if ( GasNeatConfiguration.getRandomizeInputReceptorsRate() > 0) {
				if ( rand.nextDouble() < GasNeatConfiguration.getRandomizeInputReceptorsRate() ) {
					setReceptorType(  receptorList.get( rand.nextInt(receptorList.size()) )  );
				}
			}
			
		//OUTPUT NEURONS
		} else if (this.getType() == NeuronType.OUTPUT) {
			setXCoordinate( outputLayerX );
			long y = verticalPadding/2 + this.getInnovationId()*verticalPadding/2;
			setYCoordinate( (int)y );
			
			//only change to gas type if this is > 0
			if ( GasNeatConfiguration.getRandomizeGasEmittedRate() > 0 ) {
				if ( rand.nextDouble() < GasNeatConfiguration.getRandomizeGasEmittedRate() ) {
					setGasEmissionType( 1 + rand.nextInt( GasNeatConfiguration.getNumberGases())  );
					setGasEmissionStrength( rand.nextDouble() );
				}
			}
			//only change synaptic gas if this is > 0
			if ( GasNeatConfiguration.getRandomizeSynapticGasRate() > 0) {
				if ( rand.nextDouble() < GasNeatConfiguration.getRandomizeSynapticGasRate() ) {
					setSynapticGasEmissionType( 1 + rand.nextInt( GasNeatConfiguration.getNumberGases())  );
				}
			}
			//only change rceptor if this is great than 0
			if (GasNeatConfiguration.getRandomizeReceptorsRate() > 0 &&
				 rand.nextDouble() < GasNeatConfiguration.getRandomizeReceptorsRate() ) {
					setReceptorType(  receptorList.get( rand.nextInt(receptorList.size()) ) );
				
			}
			
		//HIDDEN NEURONS
		} else {

			//only change to gas type if this is > 0
			if ( GasNeatConfiguration.getRandomizeGasEmittedRate() > 0  &&
				rand.nextDouble() < GasNeatConfiguration.getRandomizeGasEmittedRate() ) {
					setGasEmissionType( 1 + rand.nextInt( GasNeatConfiguration.getNumberGases())  );
					setGasEmissionStrength( rand.nextDouble() );
				
			}
			//only change synaptic gas if this is > 0
			if ( GasNeatConfiguration.getRandomizeSynapticGasRate() > 0 &&
				 rand.nextDouble() < GasNeatConfiguration.getRandomizeSynapticGasRate() ) {
					setSynapticGasEmissionType( 1 + rand.nextInt( GasNeatConfiguration.getNumberGases())  );
				
			}
			//only change rceptor if this is great than 0
			if (GasNeatConfiguration.getRandomizeReceptorsRate() > 0 &&
				 rand.nextDouble() < GasNeatConfiguration.getRandomizeReceptorsRate() ) {
					setReceptorType(  receptorList.get( rand.nextInt(receptorList.size() )) );
				
			}
		}
		setPlasticityParameterA(  GasNeatConfiguration.getDefaultPlasticityA() );
		setPlasticityParameterB(  GasNeatConfiguration.getDefaultPlasticityB() );
		setPlasticityParameterC(  GasNeatConfiguration.getDefaultPlasticityC() );
		setPlasticityParameterD(  GasNeatConfiguration.getDefaultPlasticityD() );
		setPlasticityParameterLR(  GasNeatConfiguration.getDefaultPlasticityLR() );
		
		
		//#ADDPROPS
		//#CTRNNTODO will need to allow these values to change before any effect will happen
		setReceptorStrength(  GasNeatConfiguration.getDefaultReceptorStrength() );
		setTimingConstant( GasNeatConfiguration.getDefaultTimingConstant() );

	}
	
	/**
	 * @return GasNEATNeuronGene Neuron
	 */
	public GasNeatNeuronGene getNeuron() {
		return gasNeatNeuronGene;
	}

	public int getYCoordinate() {
		return yCoordinate;
	}

	public void setYCoordinate(int y) {
		this.yCoordinate = y;
	}

	public int getXCoordinate() {
		return xCoordinate;
	}

	public void setXCoordinate(int x) {
		this.xCoordinate = x;
	}


	public String getReceptorType() {
		return receptorType;
	}

	public void setReceptorType(String receptorType) {
		this.receptorType = receptorType;
	}
	
	public void setReceptorTypeToDefault() {
		ArrayList<String> receptorList = GasNeatConfiguration.getReceptorMap();
		String type = receptorList.get( 0 );
		setReceptorType( type );

	}
	
	public void setReceptorTypeToRandom(Random rand) {
		ArrayList<String> receptorList = GasNeatConfiguration.getReceptorMap();
		String type = receptorList.get(  rand.nextInt( receptorList.size() ) );
		setReceptorType( type );

	}
	

	public void setPreferredActivationTypeForReceptor(int activationType) {
		// TODO Auto-generated method stub
		//IMMEDIATETODO
		//try to set "G"+ activationType+"____"
		
		ArrayList<String> receptorList = GasNeatConfiguration.getReceptorMap();

		for (String receptorType: receptorList) {
			if ( new Integer(receptorType.substring(1, 2)) == activationType ) {
				setReceptorType( receptorType );
				break;
			}
		}
		
		if ( receptorType.equals("")) {
			receptorType = receptorList.get( 0 );
		}
	}

	public int getGasEmissionType() {
		return gasEmissionType;
	}

	public void setGasEmissionType(int gasEmissionType) {
		this.gasEmissionType = gasEmissionType;
	}

	public double getGasEmissionStrength() {
		return gasEmissionStrength;
	}

	public void setGasEmissionStrength(double d) {
		if (d == 0) {
			System.out.println("DO NOT SET GAS EMISSION STRENGTH TO ZERO");
			try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.exit(1);
		}
		this.gasEmissionStrength = d;
	}

	public int getGasEmissionRadius() {
		return gasEmissionRadius;
	}

	public void setGasEmissionRadius(int gasEmissionRadius) {
		this.gasEmissionRadius = gasEmissionRadius;
		
		if (gasEmissionRadius != 300) {
			System.err.println("emissionr adius is: " + gasEmissionRadius);
			System.exit(1);
		}
		
		if ( gasEmissionRadius > GasNeatConfiguration.getMaxEmissionRadius()) {
			try {
				throw new Exception("gas emission radius too large!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ( gasEmissionRadius < GasNeatConfiguration.getMinEmissionRadius()) {
			try {
				throw new Exception("gas emission radius too small!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void setSynapticGasEmissionType(int synapticGasEmissionType) {
		this.synapticGasEmissionType = synapticGasEmissionType;
		
	}

	public int getSynapticGasEmissionType() {
		return synapticGasEmissionType;
	}

	public void setGasSpeed(double speed) {
		gasSpeed = speed;
	}
	
	public double getGasSpeed() {
		return gasSpeed;
	}
	
	@Override
	public String toString() {
		
		if (gasEmissionType == 0 ) {
			return "GasNeatNeuronAllele [ID=" + gasNeatNeuronGene + ", [" + xCoordinate+ ", " + yCoordinate + "]"+ 
					" thresh=" + firingThreshold + ", receptorType="+ receptorType + 
					" SynapticGasType=" + synapticGasEmissionType + " ]\n"; 
		} else {
			return "GasNeatNeuronAllele [ID=" + gasNeatNeuronGene + ", [" + xCoordinate+ ", " + yCoordinate + "]"+
					"thresh=" + firingThreshold + " receptorType=" + receptorType + 
					"\n     GasEmissionType=" + gasEmissionType + ", EmissStrength=" + gasEmissionStrength
					+ ", Radius=" + gasEmissionRadius + ", Speed=" + gasSpeed + "]\n";
		}
		
		
		
	}
	
	//add for sake of reproduction mutation event
	public void setAllValuesFromAllele(GasNeatNeuronAllele recessiveNeuronAllele) {
		
		plasticityParameterA = recessiveNeuronAllele.getPlasticityParameterA();
		plasticityParameterB = recessiveNeuronAllele.getPlasticityParameterB();
		plasticityParameterC = recessiveNeuronAllele.getPlasticityParameterC();
		plasticityParameterD = recessiveNeuronAllele.getPlasticityParameterD();
		plasticityParameterLR = recessiveNeuronAllele.getPlasticityParameterLR();
		gasNeatNeuronGene = (GasNeatNeuronGene)recessiveNeuronAllele.getGene();
		xCoordinate = recessiveNeuronAllele.getXCoordinate();
		yCoordinate  = recessiveNeuronAllele.getYCoordinate();
		firingThreshold  = recessiveNeuronAllele.getFiringThreshold();
		receptorType  = recessiveNeuronAllele.getReceptorType();
		gasEmissionType  = recessiveNeuronAllele.getGasEmissionType();
		synapticGasEmissionType = recessiveNeuronAllele.getSynapticGasEmissionType();
		gasEmissionStrength  = recessiveNeuronAllele.getGasEmissionStrength();
		gasEmissionRadius  = recessiveNeuronAllele.getGasEmissionRadius();
		gasSpeed  = recessiveNeuronAllele.getGasSpeed();
		
		//#ADDPROPS
		receptorStrength = recessiveNeuronAllele.getReceptorStrength();
		timingConstant = recessiveNeuronAllele.getTimingConstant();
				
		
	}


	public List<Integer> getImpactingGasTypes() {
		
		List<Integer> impactingGasTypes = new ArrayList<Integer>();
		
		//#RECEPTORHARDCODE
		for (int i=1; i < 13; i+=3) {
			String gasType = receptorType.substring(i, i+1);
			if ( !gasType.equals("O") ) {
				impactingGasTypes.add( Integer.parseInt( gasType )  );
			}
		}
		
		return impactingGasTypes;
	}
	
	public List<Integer> getModulatingGasTypes() {
		
		List<Integer> impactingGasTypes = new ArrayList<Integer>();
		
		//#RECEPTORHARDCODE
		for (int i=4; i < 13; i+=3) {
			String gasType = receptorType.substring(i, i+1);
			if ( !gasType.equals("O") ) {
				impactingGasTypes.add( Integer.parseInt( gasType )  );
			}
		}
		
		return impactingGasTypes;
	}
	
	
}
