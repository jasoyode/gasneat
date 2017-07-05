package gasNEAT.nn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import com.anji.nn.Neuron;
import com.anji.util.Properties;

import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.model.GasNeatSynapse;
import gasNEAT.view.Constants;

public class GasNeatNet  {
	
	/**
	 * base XML tag
	 */
	public final static String XML_TAG = "network";

	private List<GasNeatNeuron> allNeurons;
	private List<GasNeatNeuron> inNeurons;
	private List<GasNeatNeuron> outNeurons;
	private Collection recurrentConns;
	private String name;
	private GasNeatNeuralNetwork net;
	
	private Properties props;
	
	public GasNeatNeuralNetwork getGasNeatNeuralNetwork() {
		return net;
	}
	
	private static Logger logger = Logger.getLogger( GasNeatNet.class );
	

	/**
	 * @param someNeurons
	 * @param someInNeurons
	 * @param someOutNeurons
	 * @param someRecurrentConns
	 * @param aName
	 */
	public GasNeatNet( Collection<GasNeatNeuron> someNeurons, List<GasNeatNeuron> someInNeurons, List<GasNeatNeuron> someOutNeurons,
			Collection someRecurrentConns, String aName, HashMap<Long, GasNeatSynapse> synapseMap, Properties props, int recurrentSteps ) {
		init( someNeurons, someInNeurons, someOutNeurons, someRecurrentConns, aName );

		net = new GasNeatNeuralNetwork(someNeurons, someInNeurons, someOutNeurons,
				someRecurrentConns, aName, synapseMap, props, recurrentSteps);
		
		net.setMode(Constants.VisualizationModes.TRANSLUCENT_GAS);
		this.props = props;
	}


	/**
	 * @return number corresponding to cost of network activation in resources
	 */
	public long NOTNEEDEDcost() {
		long result = 0;

		Iterator<GasNeatNeuron> it = allNeurons.iterator();
		while ( it.hasNext() ) {
			Neuron n = (Neuron) it.next();
			result += n.cost();
		}

		return result;
	}

	/**
	 * @param someNeurons all neurons
	 * @param someInNeurons input neurons (also included in someNeurons)
	 * @param someOutNeurons output neurons (also included in someNeurons)
	 * @param someRecurrentConns recurrent connections
	 * @param aName
	 */
	protected void init( Collection someNeurons, List<GasNeatNeuron> someInNeurons, List<GasNeatNeuron> someOutNeurons,
			Collection someRecurrentConns, String aName ) {
		allNeurons = new ArrayList<GasNeatNeuron>( someNeurons );

		inNeurons = someInNeurons;
		outNeurons = someOutNeurons;
		recurrentConns = someRecurrentConns;
		name = aName;
	}

	/**
	 * @param idx
	 * @return input neuron at position <code>idx</code>
	 */
	public GasNeatNeuron getInputNeuron( int idx ) {
		return (GasNeatNeuron) inNeurons.get( idx );
	}

	/**
	 * @return number input neurons
	 */
	public int getInputDimension() {
		return inNeurons.size();
	}

	/**
	 * @return <code>Collection</code> contains all <code>Neuron</code> objects
	 */
	public Collection<GasNeatNeuron> getAllNeurons() {
		return allNeurons;
	}

	/**
	 * @param idx
	 * @return output neuron at position <code>idx</code>
	 */
	public GasNeatNeuron getOutputNeuron( int idx ) {
		return (GasNeatNeuron) outNeurons.get( idx );
	}

	/**
	 * @param fromIdx
	 * @param toIdx
	 * @return output neurons from position <code>toIdx</code> (inclusive) to <code>fromIdx</code>
	 * (exclusive)
	 */
	public List<GasNeatNeuron> getOutputNeurons( int fromIdx, int toIdx ) {
		return outNeurons.subList( fromIdx, toIdx );
	}

	/**
	 * @param fromIdx
	 * @param toIdx
	 * @return input neurons from position <code>toIdx</code> (inclusive) to <code>fromIdx</code>
	 * (exclusive)
	 */
	public List<GasNeatNeuron> getInputNeurons( int fromIdx, int toIdx ) {
		return inNeurons.subList( fromIdx, toIdx );
	}

	/**
	 * @return number output neurons
	 */
	public int getOutputDimension() {
		return outNeurons.size();
	}

	/**
	 * @return <code>Collection</code> contains recurrent <code>Connection</code> objects
	 */
	public Collection getRecurrentConns() {
		return recurrentConns;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
	
	public double[] next(double[] inputs) throws CloneNotSupportedException {
		
		return net.next(inputs);
	}

	/**
	 * @return true if network contains any recurrent connections, false otherwise
	 */
	public boolean isRecurrent() {
		return !recurrentConns.isEmpty();
	}
	
}
