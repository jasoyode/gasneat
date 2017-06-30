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
 * created by Philip Tucker on Jul 23, 2004
 */

package gasNEAT.polebalance;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;

import com.anji.persistence.Persistence;
import com.anji.util.DummyConfiguration;
import com.anji.util.Properties;

import gasNEAT.configurations.GasNeatConfiguration;

/**
 * @author Philip Tucker
 */
public class DoublePoleBalanceEvaluator {

private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( DoublePoleBalanceEvaluator.class );

/**
 * @param args
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
	DoublePoleBalanceFitnessFunction ff = new DoublePoleBalanceFitnessFunction();
	
	Properties props = new Properties();
	
	props.loadFromResource( args[ 0 ] );
	
	ff.init( props );
	
	Persistence db = (Persistence) props.newObjectProperty( Persistence.PERSISTENCE_CLASS_KEY );
	
	Configuration config = new DummyConfiguration();
	
	
	//
	config = new GasNeatConfiguration(props);
	
	
	//Load random instead of from file...
	Chromosome chrom = db.loadChromosome( args[ 1 ], config );
	
	
	
	
	//SpreadsheetFreeNetworkBuilder randomBuilder = new SpreadsheetFreeNetworkBuilder( idManager );	

	//MUST INCLUDE 7 AS THAT IS THE NUMBER OF INPIT NEURONS!!!!!
	//ChromosomeMaterial material = randomBuilder.buildBaseNetworkAlleles( 7 );
	
	//Chromosome chrom = new Chromosome(material, material.getPrimaryParentId() );
	
	/*logger.info(chrom);
	logger.info(chrom.getAlleles());
	
	
	//for (Object a: chrom.getAlleles() ) {
		Allele aa = (Allele)a;
		logger.info("allele innovation ID: "  +aa.getInnovationId() );
		logger.info("allele: "  +aa. );
	}
	//*/
	
	
	
	if ( chrom == null )
		throw new IllegalArgumentException( "no chromosome found: " + args[ 1 ] );
	ff.enableDisplay();
	ff.evaluate( chrom );
	logger.info( "Fitness = " + chrom.getFitnessValue() );
}
}
