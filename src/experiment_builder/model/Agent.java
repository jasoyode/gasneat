package experiment_builder.model;

import org.apache.logging.log4j.LogManager;

import experiment_builder.controller.ParametersCalculator;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

public class Agent {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( Agent.class );
	
	private @Getter @Setter int pos = -1;
	private @Getter @Setter double health = 10;
	private @Getter @Setter double maximumHealth = 10;
	
	private @Setter @Getter double maximumFood = 10.0;
	private @Setter @Getter double maximumWater = 10.0;
	
	private @Setter @Getter double foodLevel = 10.0;
	private @Setter @Getter double waterLevel = 10.0;
	
	//only needed for agents with relative movements
	

	//one of these should be zero and the other -1 or +1
	// y up is negative, down is position
	// x right is positive, left is negative
	private @Getter @Setter int xOrientation= 0;
	private @Getter @Setter int yOrientation = -1;
	
	
	public boolean validOrientation() {
		boolean atLeastOneZero = ( xOrientation == 0 || yOrientation == 0);
		boolean absoluteSumOfOne = Math.abs( xOrientation + yOrientation) == 1;
		return atLeastOneZero && absoluteSumOfOne;
	}

	public Agent() {
		health = maximumHealth;
		foodLevel = maximumFood;
		waterLevel = maximumWater;
	}
	
	
	public void reset() {
		health = maximumHealth;
		foodLevel = maximumFood;
		waterLevel = maximumWater;
	}
	

	public Agent(int pos) {
		this.pos = pos;
		health = maximumHealth;
		foodLevel = maximumFood;
		waterLevel = maximumWater;
	}

	public void flipOrientation() {
		xOrientation  = xOrientation  * -1;
		yOrientation  = yOrientation * -1;
		
	}
	
	public String getOrientation() {
		
		if ( validOrientation() ) 
		{
			if ( getXOrientation() == 0) {
				if ( getYOrientation() == 1) {
					return "DOWN";
				} else {
					return "UP";
				}
			} else {
				if ( getXOrientation() == 1) {
					return "RIGHT";
				} else {
					return "LEFT";
				}
			}
		}
		return "INVALID ORIENTATION!"; 
		
	}

	
}
