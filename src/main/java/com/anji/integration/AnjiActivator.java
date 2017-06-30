/*
 * Copyright (C) 2004 Derek James and Philip Tucker
 * 
 * This file is part of ANJI (Another NEAT Java Implementation).
 * 
 * ANJI is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * Created on Feb 26, 2004 by Philip Tucker
 */
package com.anji.integration;

import com.anji.nn.AnjiNet;
import com.anji.nn.Connection;
import com.anji.nn.Neuron;
import com.anji.nn.NeuronConnection;
import com.anji.nn.Pattern;

/**
 * Simple neural network implementation of Activator interface.
 * 
 * @author Philip Tucker
 */
public class AnjiActivator implements Activator {

private AnjiNet net;

private Pattern inputPattern = null;

private int numCycles = 1;

private int outputDimension;

private double minResponseValue;

private double maxResponseValue;

/**
 * @param aNet ANN
 * @param aNumCycles number of times input pattern is "shown" to network before a result is
 * returned; this allows for recurrent connections to take effect
 * @throws IllegalArgumentException
 */
public AnjiActivator( AnjiNet aNet, int aNumCycles ) throws IllegalArgumentException {
	super();
	setNumCycles( aNumCycles );
	net = aNet;
	outputDimension = aNet.getOutputDimension();

	// input neurons
	inputPattern = new Pattern( aNet.getInputDimension() );
	for ( int i = 0; i < aNet.getInputDimension(); ++i ) {
		Neuron n = aNet.getInputNeuron( i );
		n.addIncomingConnection( inputPattern.getConnection( i ) );
	}

	// verify consistent response ranges
	minResponseValue = net.getOutputNeuron( 0 ).getFunc().getMinValue();
	maxResponseValue = net.getOutputNeuron( 0 ).getFunc().getMaxValue();
	for ( int i = 1; i < net.getOutputDimension(); ++i )
		if ( minResponseValue != net.getOutputNeuron( i ).getFunc().getMinValue()
				|| maxResponseValue != net.getOutputNeuron( i ).getFunc().getMaxValue() )
			throw new IllegalArgumentException( "min and max values for response nodes differ" );
}

/**
 * @return next output pattern
 * @see AnjiActivator#next(double[])
 */
public double[] next() {
	return next( (double[]) null );
}

public void printState() {
	
	System.out.println(  " print state1"   );
	for (Object o:  net.getAllNeurons() ) {
		Neuron n = (Neuron)o;
		if (n.getId() == 7) {
			double value = n.getValueSafe();
			System.out.println( "  AnjiActivator "+ n.getId() +" "+ value +" applyFunc: "+ n.getFunc().apply(value)   );
			for (Object oo: n.getIncomingConns() ) {
				Connection c = (Connection) oo;
				System.out.println( "        N7  " +c.toString() + " value: "+ c.readSafe() + " class: "+ c.getClass()  );
			}
		}
		//System.out.println( "  "+ n.getId() +" "+ n.getValueSafe() +" applyFunc: "+ n.getFunc().apply(n.getValueSafe())   );
	}
	System.out.println(  " print state2"   );
	
	
	for ( Object o:  net.getRecurrentConns() ) {
		NeuronConnection c = (NeuronConnection)o;
		System.out.println( "    " +c.toString() + " value: "+ c.readSafe()  );
	}
	System.out.print( "inputPatterns "   );
	for ( double d:  inputPattern.values ) {
		System.out.print( d + "  "   );
		
	}
	System.out.println( " ]"   );
	
}


/**
 * @param newInputValues input activation pattern to be "shown" to network; if null, uses
 * previous values
 * @return result of activation
 */
public double[] next( double[] newInputValues ) {
	
	for (Double d: newInputValues ) {
	///	System.out.print("   "+ d);
	}
	//System.out.println(" inputs");
	
	
	//System.out.println("BEFORE SET INPUT PATTERN");
	//printState();
	//System.out.println("BEFORE SET INPUT PATTERN2");
	
	if ( newInputValues != null )
		inputPattern.setValues( newInputValues );
	
	//System.out.println("AFTER SET INPUT PATTERN");
	//printState();
	
	// step through network activations
	for ( int cycle = 0; cycle < numCycles - 1; ++cycle ) {
		net.step();
		// sanity check - this may not be necessary if we construct the nets properly such that
		// recurrency is determined by building the network backward from the output layer; in that
		// way, a neuron could not be created having only recurrent outputs
		net.fullyActivate();
	}

	// last step, get results
	net.step();
	
	//if it need to be above it needs to be here...
	//net.fullyActivate();
	
	//System.out.println("after step is called");
	//printState();
	
	double[] result = new double[ outputDimension ];
	for ( int idx = 0; idx < outputDimension; ++idx ) {
		Neuron n = net.getOutputNeuron( idx );
		
		//System.out.println( "    output= "+ n.getValueSafe()  );
		
		result[ idx ] = n.getValue();
		
	}
	net.fullyActivate();
	
	//TEMPPRINT
	//System.out.println("  "+result[0]);
	//if (result[0] !=   0.49907008861216645 )
	//	System.exit(-1);

	return result;
}

/**
 * @param newInputValues input pattern sequence
 * @return next output pattern sequence
 * @see AnjiActivator#next(double[])
 */
public double[][] next( double[][] newInputValues ) {
	double[][] result = new double[ newInputValues.length ][];
	for ( int i = 0; i < newInputValues.length; ++i ) {
		result[ i ] = next( newInputValues[ i ] );
	}
	return result;
}

/**
 * @param array glue between double arrays and neuron connections.
 */
public void setInputPattern( Pattern array ) {
	inputPattern = array;
}

/**
 * clear all memory in network, including neurons and recurrent connections
 */
public void reset() {
	net.reset();
}

/**
 * @see java.lang.Object#toString()
 */
public String toString() {
	return net.toString();
}

/**
 * @see com.anji.integration.Activator#toXml()
 */
public String toXml() {
	return net.toXml();
}

/**
 * @see com.anji.integration.Activator#getName()
 */
public String getName() {
	return net.getName();
}

/**
 * @param aNumCycles number of times input pattern is "shown" to network before a result is
 * returned; this allows for recurrent connections to take effect
 * @throws IllegalArgumentException
 */
public void setNumCycles( int aNumCycles ) throws IllegalArgumentException {
	if ( aNumCycles < 1 )
		throw new IllegalArgumentException( "numCycles must be >= 1" );
	numCycles = aNumCycles;
}

/**
 * @return dimension of input pattern
 */
public int getInputDimension() {
	return inputPattern.getDimension();
}

/**
 * @return dimension of output pattern
 */
public int getOutputDimension() {
	return outputDimension;
}

/**
 * @return true if network contains any recurrent connections, false otherwise
 */
public boolean isRecurrent() {
	return net.isRecurrent();
}

/**
 * @return min response
 */
public double getMinResponse() {
	return net.getOutputNeuron( 0 ).getFunc().getMinValue();
}

/**
 * @return max responses
 */
public double getMaxResponse() {
	return net.getOutputNeuron( 0 ).getFunc().getMaxValue();
}

/**
 * @see com.anji.util.XmlPersistable#getXmlRootTag()
 */
public String getXmlRootTag() {
	return "network";
}

/**
 * @see com.anji.util.XmlPersistable#getXmld()
 */
public String getXmld() {
	return net.getName();
}

}
