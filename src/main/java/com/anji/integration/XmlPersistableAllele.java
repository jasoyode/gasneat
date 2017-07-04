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
 * Created on Jul 5, 2005 by Philip Tucker
 */
package com.anji.integration;

import org.jfree.util.Log;
import org.jgap.Allele;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.anji.neat.ConnectionAllele;
import com.anji.neat.ConnectionGene;
import com.anji.neat.NeuronAllele;
import com.anji.neat.NeuronGene;
import com.anji.neat.NeuronType;
import com.anji.nn.ActivationFunctionType;
import com.anji.util.XmlPersistable;

import gasNEAT.geneticEncoding.GasNeatConnectionAllele;
import gasNEAT.geneticEncoding.GasNeatConnectionGene;
import gasNEAT.geneticEncoding.GasNeatNeuronAllele;
import gasNEAT.geneticEncoding.GasNeatNeuronGene;

/**
 * @author Philip Tucker
 */
public class XmlPersistableAllele implements XmlPersistable {

/**
 * neuron XML tag
 */
public final static String NEURON_XML_TAG = "neuron";
private final static String NEURON_XML_TYPE_TAG = "type";
private final static String NEURON_XML_ACTIVATION_TYPE_TAG = "activation";

/**
 * connection XML tag
 */
public final static String CONN_XML_TAG = "connection";
private final static String XML_ID_TAG = "id";
private final static String CONN_XML_SRCID_TAG = "src-id";
private final static String CONN_XML_DESTID_TAG = "dest-id";
private final static String CONN_XML_WEIGHT_TAG = "weight";
private final static String CONN_XML_RECURRENT_TAG = "recurrent";


//GASNEAT PROPERTIES

public final static String GASNEAT_NEURON_XML_TAG = "gasneat-neuron";
public final static String GASNEAT_CONN_XML_TAG = "gasneat-connection";


private final static String NEURON_THRESHOLD ="threshold";
private final static String GAS_TYPE ="gas-prod-type";
private final static String SYNAPTIC_GAS_TYPE ="syn-gas-type";
private final static String X_POS ="x";
private final static String Y_POS ="y";
private final static String EMISSION_RATE ="emiss-rate";
private final static String RECEPTOR_TYPE ="receptor";
private static final String EMISSION_RADIUS = "emiss-radius";

private final static String PLASTICITY_A ="A";
private final static String PLASTICITY_B ="B";
private final static String PLASTICITY_C ="C";
private final static String PLASTICITY_D ="D";
private final static String PLASTICITY_LR ="LR";


private final static String TIMING_CONSTANT ="timing-c";
private final static String RECEPTOR_STRENGTH ="recp-str";
//#ADDPROPS



private Allele allele;

/**
 * ctor
 * @param aAllele
 */
public XmlPersistableAllele( Allele aAllele ) {
	super();
	allele = aAllele;
}

/**
 * @see com.anji.util.XmlPersistable#toXml()
 */
public String toXml() {
	StringBuffer result = new StringBuffer();

	if ( allele.getClass().equals(NeuronAllele.class) ) {
		NeuronAllele nAllele = (NeuronAllele) allele;
		result.append( "<" ).append( NEURON_XML_TAG ).append( " " );
		result.append( XML_ID_TAG ).append( "=\"" ).append( allele.getInnovationId() ).append(
				"\" " );
		result.append( NEURON_XML_TYPE_TAG ).append( "=\"" ).append( nAllele.getType().toString() )
				.append( "\" " );
		result.append( NEURON_XML_ACTIVATION_TYPE_TAG ).append( "=\"" ).append(
				nAllele.getActivationType().toString() );
		result.append( "\"/>\n" );
	}
	else if ( allele.getClass().equals(ConnectionAllele.class) ) {
		ConnectionAllele cAllele = (ConnectionAllele) allele;
		result.append( "<" ).append( CONN_XML_TAG ).append( " " );
		result.append( XML_ID_TAG ).append( "=\"" ).append( allele.getInnovationId() );
		result.append( "\" " ).append( CONN_XML_SRCID_TAG ).append( "=\"" ).append(
				cAllele.getSrcNeuronId() );
		result.append( "\" " ).append( CONN_XML_DESTID_TAG ).append( "=\"" ).append(
				cAllele.getDestNeuronId() );
		result.append( "\" " ).append( CONN_XML_WEIGHT_TAG ).append( "=\"" ).append(
				cAllele.getWeight() ).append( "\"/>\n" );
	} else if ( allele.getClass().equals(GasNeatNeuronAllele.class) ) {
		
		
		
		GasNeatNeuronAllele nAllele = (GasNeatNeuronAllele) allele;
		result.append( "<" ).append( GASNEAT_NEURON_XML_TAG ).append( " " );
		
		result.append( XML_ID_TAG ).append( "=\"" ).append( allele.getInnovationId() ).append(
				"\" " );
		
		result.append( NEURON_XML_TYPE_TAG ).append( "=\"" ).append( nAllele.getType().toString() )
				.append( "\" " );
		
		result.append( NEURON_XML_ACTIVATION_TYPE_TAG ).append( "=\"" ).append( nAllele.getActivationType().toString() ).append(
				 "\" " );
		
		//////////NEW PROPS
		
		
		result.append( NEURON_THRESHOLD ).append( "=\"" ).append( nAllele.getFiringThreshold() ).append(
				"\" " );
		
		result.append( GAS_TYPE ).append( "=\"" ).append( nAllele.getGasEmissionType() ).append(
				"\" " );
		
		result.append( SYNAPTIC_GAS_TYPE ).append( "=\"" ).append( nAllele.getSynapticGasEmissionType() ).append(
				"\" " );
		
		result.append( RECEPTOR_TYPE ).append( "=\"" ).append( nAllele.getReceptorType() ).append(
				"\" " );
	
		result.append( X_POS ).append( "=\"" ).append( nAllele.getXCoordinate() ).append(
				"\" " );
		
		result.append( Y_POS ).append( "=\"" ).append( nAllele.getYCoordinate() ).append(
				"\" " );
		
		//PLASTICITY
		
		result.append( PLASTICITY_A ).append( "=\"" ).append( nAllele.getPlasticityParameterA() ).append( "\" " );
		result.append( PLASTICITY_B ).append( "=\"" ).append( nAllele.getPlasticityParameterB() ).append( "\" " );
		result.append( PLASTICITY_C ).append( "=\"" ).append( nAllele.getPlasticityParameterC() ).append( "\" " );
		result.append( PLASTICITY_D ).append( "=\"" ).append( nAllele.getPlasticityParameterD() ).append( "\" " );
		result.append( PLASTICITY_LR ).append( "=\"" ).append( nAllele.getPlasticityParameterLR() ).append( "\" " );
		
		
		//END PLASTICITY
		
		
		result.append( TIMING_CONSTANT ).append( "=\"" ).append( nAllele.getTimingConstant() ).append( "\" " );
		result.append( RECEPTOR_STRENGTH ).append( "=\"" ).append( nAllele.getReceptorStrength() ).append( "\" " );
		
		//#ADDPROPS
		
		
		result.append( EMISSION_RATE ).append( "=\"" ).append( nAllele.getGasEmissionStrength() ).append(
				"\" " );
		
		result.append( EMISSION_RADIUS ).append( "=\"" ).append( nAllele.getGasEmissionRadius()  ).append( 
				"\"/>\n" );
		
		
		
		
	} else if ( allele.getClass().equals(GasNeatConnectionAllele.class) ) {
		GasNeatConnectionAllele cAllele = (GasNeatConnectionAllele) allele;
		result.append( "<" ).append( GASNEAT_CONN_XML_TAG ).append( " " );
		result.append( XML_ID_TAG ).append( "=\"" ).append( allele.getInnovationId() );
		result.append( "\" " ).append( CONN_XML_SRCID_TAG ).append( "=\"" ).append(
				cAllele.getSrcNeuronId() );
		result.append( "\" " ).append( CONN_XML_DESTID_TAG ).append( "=\"" ).append(
				cAllele.getDestNeuronId() );
		result.append( "\" " ).append( CONN_XML_WEIGHT_TAG ).append( "=\"" ).append(
				cAllele.getWeight() ).append( "\"/>\n" );
	} else {
		Log.error("You cannot load an Allele that is not GasNeat/Anji neuron/connection!");
		System.exit(1);
		
	}
	
	
	
	
	

	return result.toString();
}

/**
 * @see com.anji.util.XmlPersistable#getXmlRootTag()
 */
public String getXmlRootTag() {
	
	if ( allele.getClass().equals(NeuronAllele.class) ) {
		return NEURON_XML_TAG;
	} else if ( allele.getClass().equals(ConnectionAllele.class) ) {
		return CONN_XML_TAG;
	} else if ( allele.getClass().equals(GasNeatNeuronAllele.class) ) {
		return GASNEAT_NEURON_XML_TAG;
	} else if ( allele.getClass().equals(GasNeatConnectionAllele.class) ) {
		return GASNEAT_CONN_XML_TAG;
	} else {
		Log.error("You cannot load an Allele that is not GasNeat/Anji neuron/connection!");
		System.exit(1);
	}

	return null;
}

/**
 * @see com.anji.util.XmlPersistable#getXmld()
 */
public String getXmld() {
	return allele.getInnovationId().toString();
}

/**
 * Convert from XML to <code>NeuronGene</code> object
 * 
 * @param node
 * @return <code>NeuronAllele</code> constructed from XML <code>node</code>
 * @throws IllegalArgumentException
 */
public static NeuronAllele neuronFromXml( Node node ) throws IllegalArgumentException {
	if ( XmlPersistableAllele.NEURON_XML_TAG.equals( node.getNodeName() ) == false )
		throw new IllegalArgumentException( "tag != " + XmlPersistableAllele.NEURON_XML_TAG );
	if ( node.hasAttributes() == false )
		throw new IllegalArgumentException( "no attributes" );
	NamedNodeMap atts = node.getAttributes();

	String str = atts.getNamedItem( XmlPersistableAllele.NEURON_XML_TYPE_TAG ).getNodeValue();
	NeuronType type = NeuronType.valueOf( str );
	if ( type == null )
		throw new IllegalArgumentException( "invalid neuron type: " + str );

	str = atts.getNamedItem( XmlPersistableAllele.XML_ID_TAG ).getNodeValue();
	Long id = Long.valueOf( str );

	ActivationFunctionType activationType = ActivationFunctionType.SIGMOID;
	Node actNode = atts.getNamedItem( XmlPersistableAllele.NEURON_XML_ACTIVATION_TYPE_TAG );
	if ( actNode != null ) {
		str = actNode.getNodeValue();
		activationType = ActivationFunctionType.valueOf( str );
		if ( activationType == null )
			throw new IllegalArgumentException( "invalid activation function type: " + str );
	}

	return new NeuronAllele( new NeuronGene( type, id, activationType ) );
}

/**
 * Convert from XML to <code>ConnectionGene</code> object
 * 
 * @param node
 * @return <code>ConnectionAllele</code> constructed from XML <code>node</code>
 * @throws IllegalArgumentException
 */
public static ConnectionAllele connectionFromXml( Node node ) throws IllegalArgumentException {
	if ( XmlPersistableAllele.CONN_XML_TAG.equals( node.getNodeName() ) == false )
		throw new IllegalArgumentException( "tag != " + XmlPersistableAllele.CONN_XML_TAG );
	if ( node.hasAttributes() == false )
		throw new IllegalArgumentException( "no attributes" );
	NamedNodeMap atts = node.getAttributes();

	String idStr = atts.getNamedItem( XmlPersistableAllele.XML_ID_TAG ).getNodeValue();
	Long id = Long.valueOf( idStr );
	String srcIdStr = atts.getNamedItem( XmlPersistableAllele.CONN_XML_SRCID_TAG ).getNodeValue();
	Long srcId = Long.valueOf( srcIdStr );
	String destIdStr = atts.getNamedItem( XmlPersistableAllele.CONN_XML_DESTID_TAG )
			.getNodeValue();
	Long destId = Long.valueOf( destIdStr );
	
	ConnectionAllele result = new ConnectionAllele( new ConnectionGene( id, srcId, destId ) );

	String weightStr = atts.getNamedItem( XmlPersistableAllele.CONN_XML_WEIGHT_TAG )
			.getNodeValue();
	result.setWeight( Double.parseDouble( weightStr ) );

	return result;
}

public static GasNeatNeuronAllele gasNeatNeuronFromXml(Node node) {
	if ( XmlPersistableAllele.GASNEAT_NEURON_XML_TAG.equals( node.getNodeName() ) == false )
		throw new IllegalArgumentException( "tag != " + XmlPersistableAllele.GASNEAT_NEURON_XML_TAG );
	if ( node.hasAttributes() == false )
		throw new IllegalArgumentException( "no attributes" );
	NamedNodeMap atts = node.getAttributes();

	//GET NEURON TYPE
	String str = atts.getNamedItem( XmlPersistableAllele.NEURON_XML_TYPE_TAG ).getNodeValue();
	NeuronType type = NeuronType.valueOf( str );
	if ( type == null )
		throw new IllegalArgumentException( "invalid neuron type: " + str );

	//GET ID
	str = atts.getNamedItem( XmlPersistableAllele.XML_ID_TAG ).getNodeValue();
	Long id = Long.valueOf( str );

	//GET ACTIVATION TYPE
	ActivationFunctionType activationType = ActivationFunctionType.SIGMOID;
	Node actNode = atts.getNamedItem( XmlPersistableAllele.NEURON_XML_ACTIVATION_TYPE_TAG );
	if ( actNode != null ) {
		str = actNode.getNodeValue();
		activationType = ActivationFunctionType.valueOf( str );
		if ( activationType == null )
			throw new IllegalArgumentException( "invalid activation function type: " + str );
	}
	
	GasNeatNeuronAllele gasNeatNeuronAllele = new GasNeatNeuronAllele( new GasNeatNeuronGene( type, id, activationType ) );
	
	//GET FIRING THRESHOLD
	actNode = atts.getNamedItem( XmlPersistableAllele.NEURON_THRESHOLD );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setFiringThreshold( new Double(str)  );
	
	//GET GAS_TYPE
	actNode = atts.getNamedItem( XmlPersistableAllele.GAS_TYPE );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setGasEmissionType( new Integer(str) );
	
	//GET SYNAPTIC_GAS_TYPE
	actNode = atts.getNamedItem( XmlPersistableAllele.SYNAPTIC_GAS_TYPE );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setSynapticGasEmissionType( new Integer(str) );
		
	//GET 	X_POS
	actNode = atts.getNamedItem( XmlPersistableAllele.X_POS );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setXCoordinate( new Integer(str) );
	
	//GET 	Y_POS
	actNode = atts.getNamedItem( XmlPersistableAllele.Y_POS );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setYCoordinate( new Integer(str) );
	
	/////////////plasticity
	actNode = atts.getNamedItem( XmlPersistableAllele.PLASTICITY_A );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setPlasticityParameterA( new Double(str) );
	
	actNode = atts.getNamedItem( XmlPersistableAllele.PLASTICITY_B );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setPlasticityParameterB( new Double(str) );
	
	actNode = atts.getNamedItem( XmlPersistableAllele.PLASTICITY_C );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setPlasticityParameterC( new Double(str) );
	
	actNode = atts.getNamedItem( XmlPersistableAllele.PLASTICITY_D );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setPlasticityParameterD( new Double(str) );
	
	actNode = atts.getNamedItem( XmlPersistableAllele.PLASTICITY_LR );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setPlasticityParameterLR( new Double(str) );
	
	///////////////////////////////////////////////////
	
	
	actNode = atts.getNamedItem( XmlPersistableAllele.TIMING_CONSTANT );
	
	if (actNode == null)
		str = "1";
	else
		str = actNode.getNodeValue();
	gasNeatNeuronAllele.setTimingConstant(  new Double(str) );

	
	actNode = atts.getNamedItem( XmlPersistableAllele.RECEPTOR_STRENGTH );
	if (actNode == null)
		str = "1";
	else 
		str = actNode.getNodeValue();
	gasNeatNeuronAllele.setReceptorStrength( new Double(str) );
	
	//#ADDPROPS
	
	
	
	//GET EMISSION_RATE
	actNode = atts.getNamedItem( XmlPersistableAllele.EMISSION_RATE );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setGasEmissionStrength( new Double(str) );
	
	//GET EMISSION_RADIUS
	actNode = atts.getNamedItem( XmlPersistableAllele.EMISSION_RADIUS );
	str = actNode.getNodeValue();
	gasNeatNeuronAllele.setGasEmissionRadius( new Integer(str) );
	
	
	//GET GAS_EMITTER NOT NEEDED
	//actNode = atts.getNamedItem( XmlPersistableAllele.GAS_EMITTER );
	//str = actNode.getNodeValue();
	//gasNeatNeuronAllele.setGasEmissionType( new Boolean(str) );
	
	//GET   RECEPTOR_TYPE
	actNode = atts.getNamedItem( XmlPersistableAllele.RECEPTOR_TYPE );
	str = actNode.getNodeValue();
	//gasNeatNeuronAllele.setReceptorType( new Integer(str) );
	gasNeatNeuronAllele.setReceptorType( str );
	
	if (str.equals(-1)) {
		System.out.println("XML error");
		System.exit(-1);
	}
	

	return gasNeatNeuronAllele;
}

public static GasNeatConnectionAllele gasNeatConnectionFromXml(Node node) {
	if ( XmlPersistableAllele.GASNEAT_CONN_XML_TAG.equals( node.getNodeName() ) == false )
		throw new IllegalArgumentException( "tag != " + XmlPersistableAllele.GASNEAT_CONN_XML_TAG );
	if ( node.hasAttributes() == false )
		throw new IllegalArgumentException( "no attributes" );
	NamedNodeMap atts = node.getAttributes();

	String idStr = atts.getNamedItem( XmlPersistableAllele.XML_ID_TAG ).getNodeValue();
	Long id = Long.valueOf( idStr );
	String srcIdStr = atts.getNamedItem( XmlPersistableAllele.CONN_XML_SRCID_TAG ).getNodeValue();
	Long srcId = Long.valueOf( srcIdStr );
	String destIdStr = atts.getNamedItem( XmlPersistableAllele.CONN_XML_DESTID_TAG )
			.getNodeValue();
	Long destId = Long.valueOf( destIdStr );
	
	GasNeatConnectionAllele result = new GasNeatConnectionAllele( new GasNeatConnectionGene( id, srcId, destId ) );

	String weightStr = atts.getNamedItem( XmlPersistableAllele.CONN_XML_WEIGHT_TAG )
			.getNodeValue();
	result.setWeight( Double.parseDouble( weightStr ) );

	return result;
}

}
