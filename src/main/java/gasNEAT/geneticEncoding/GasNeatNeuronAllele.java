package gasNEAT.geneticEncoding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
	
	
	//maybe should be done with receptor map singleton
	
	//private ArrayList<String> receptorList;
	
	
	/**
	 * @param gasNeatNeuronGene
	 */
	public GasNeatNeuronAllele(GasNeatNeuronGene gasNeatNeuronGene) {
		super(gasNeatNeuronGene);
		this.gasNeatNeuronGene = gasNeatNeuronGene;
		
	
		
	}
	
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
		
		return allele;
	}


	
	/* (non-Javadoc)
	 * @see org.jgap.Allele#distance(org.jgap.Allele)
	 */
	@Override
	public double distance(Allele target) {
		
		//ULTRATODO include calculation for difference in ABCDLR
		
		
		//TODO - implement an algorithm, maybe review the others...
		GasNeatNeuronAllele other = (GasNeatNeuronAllele)target;
		
		double distance = 0.0;
		
		double xDiff =  Math.abs( xCoordinate - other.getXCoordinate() );
		double yDiff =  Math.abs( yCoordinate - other.getYCoordinate() );
		
		//based on distance formula - should be based upon gasSpeed
		//TODO: constant to represent gasSpeed in configuration file
		double cartesianDistance = Math.sqrt(  xDiff * xDiff + yDiff *yDiff ) / gasSpeed;
		
		//regardless of activation type, this increases relative distance
		//TODO maybe not even bother with this
		distance += Math.abs( firingThreshold - other.getFiringThreshold()  );
		
		if (synapticGasEmissionType != other.getSynapticGasEmissionType() ) {
			distance += 1.0;
			
			//System.out.println("THIS SHOULD NOT HAPPEN- all neurons produce 0 gas");
			//System.exit(-1);
			
		} else if (  gasEmissionType != other.getGasEmissionType() ) {
			
			//This mean these neurons produce different gases (or only one produced gas)
			distance += 1.0;
			
			//distance is important if they are not both synaptic
			//if they are synaptic, then gasEmission type is 0
			distance += cartesianDistance;
			
			//System.out.println("THIS SHOULD NOT HAPPEN diff gases cant be produced");
			//System.exit(-1);
			
		} else if (gasEmissionType == 0 ) {
			
			//This means these are both standard electric emitting neurons
			if ( ! receptorType.equals( other.getReceptorType() ) ) {
				//ULTRATODO calculate the important differences in receptor types
				distance += 1.0;
				//because of being activated by gas, distance is important
				//#GASNEATMODEL  ULTRATODO
				//this needs to be distinguished between how different receptors are!
				//distance += cartesianDistance;
				
				//System.out.println("THIS SHOULD NOT HAPPEN diff receptors dont exist yet");
				//System.exit(-1);
				
			} else {
				//position is less important if both are electrically activated
				//TODO THIS DISTANCE IS MEANINGLESS except for modulation 
				distance += cartesianDistance/100.0;
			}
			
		} else {
			
			//This means these are both gas emitting neurons of the same gas
			distance += cartesianDistance/10.0;
			distance += Math.abs( gasEmissionStrength - other.getGasEmissionStrength()  );
			//emission radius is important based upon gas speed, and also only a little bit
			distance += Math.abs( gasEmissionRadius - other.getGasEmissionRadius()  ) / gasSpeed/ 10.0;

			if (receptorType != other.getReceptorType() ) {
				//This means that the neurons are activated differently
				distance += 1.0;
			}
			
			//System.out.println("THIS SHOULD NOT HAPPEN until we have gas emitters");
			//System.exit(-1);
			
		}
		
		//if the neurons are activated in different ways
		/*  I don't think we want to do this, but it needs to be though of further
		if ( receptorType != other.getReceptorType() ){
			distance += 0.9;
			
			System.out.println("THIS SHOULD NOT HAPPEN until we are ready for different receptors");
			System.exit(-1);
		}
		*/
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
		
		//ULTRATODO this is can be set from file, for now leave as is
		
		setGasEmissionRadius( GasNeatConfiguration.getInitialEmissionRadius()  ); 
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
			if (GasNeatConfiguration.getRandomizeReceptorsRate() > 0) {
				if ( rand.nextDouble() < GasNeatConfiguration.getRandomizeReceptorsRate() ) {
					setReceptorType(  receptorList.get( rand.nextInt(receptorList.size()) ) );
				}
			}
			
		//HIDDEN NEURONS
		} else {

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
			if (GasNeatConfiguration.getRandomizeReceptorsRate() > 0) {
				if ( rand.nextDouble() < GasNeatConfiguration.getRandomizeReceptorsRate() ) {
					setReceptorType(  receptorList.get( rand.nextInt(receptorList.size() )) );
				}
			}
		}
		setPlasticityParameterA(  GasNeatConfiguration.getDefaultPlasticityA() );
		setPlasticityParameterB(  GasNeatConfiguration.getDefaultPlasticityB() );
		setPlasticityParameterC(  GasNeatConfiguration.getDefaultPlasticityC() );
		setPlasticityParameterD(  GasNeatConfiguration.getDefaultPlasticityD() );
		setPlasticityParameterLR(  GasNeatConfiguration.getDefaultPlasticityLR() );

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

	public double getFiringThreshold() {
		return firingThreshold;
	}

	public void setFiringThreshold(double threshold) {
		this.firingThreshold = threshold;
	}

	public String getReceptorType() {
		return receptorType;
	}

	public void setReceptorType(String receptorType) {
		this.receptorType = receptorType;
	}
	
	public void setReceptorTypeToDefault() {
		ArrayList<String> receptorList = GasNeatConfiguration.getReceptorMap();
		String receptorType = receptorList.get( 0 );
		setReceptorType( receptorType );

	}
	
	public void setReceptorTypeToRandom(Random rand) {
		ArrayList<String> receptorList = GasNeatConfiguration.getReceptorMap();
		String receptorType = receptorList.get(  rand.nextInt( receptorList.size() ) );
		setReceptorType( receptorType );

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
