package gasNEAT.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import gasNEAT.builders.SpreadsheetConstants;
import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.util.LoggingUtilities;

public class NodeArtist {
	
	private final static Logger logger = Logger.getLogger( NodeArtist.class );
	
	//Instead of passing everything around, this seems cleaner, as long as it's handled carefully.
	private int x;
	private int y;
	private double radius;
	
	private GasNeatNeuron neuron;
	
	private ImageIcon neuronIcon;
	private Image neuronImage;
	
	//Needs reference to Neuron and Synapse maps and a place to draw
	private GasNeatNeuralNetwork network;
	private JPanel myPanel;
	
	private static final int xoffset = -30;
	private static final int yoffset = -25;
	
	public NodeArtist(GasNeatNeuralNetwork network, JPanel panel) {
		this.network = network;
		this.myPanel = panel;
	}
	
	
	//uses other functions to draw the approriate representation of the Neuron
	public void drawNode(GasNeatNeuron neuron, Color color, Graphics2D g2d) {		
		setCoordinates(neuron);
		
		drawNeuron(color, g2d);
		if (network.isLabeled()) {
			drawNeuronLabel(g2d);
		}
		
		//add arrow head image at target neuron
		drawNeuronIcon(g2d);
	}
	
	
	
	//uses other functions to draw the approriate representation of the Neuron
	public void drawNode(GasNeatNeuron neuron, Graphics2D g2d) {
		this.neuron = neuron;
		
		setCoordinates(neuron);
		
		drawNeuron(g2d);
		
		if (network.isLabeled()) {
			drawNeuronLabel(g2d);
		}
		
		//add arrow head image at target neuron
		drawNeuronIcon(g2d);
	}
	
	public void setCoordinates(GasNeatNeuron neuron) {
		this.neuron = neuron;
		x = neuron.getX()*ViewConstants.SCALING_FACTOR;
		y = neuron.getY()*ViewConstants.SCALING_FACTOR;
		radius = neuron.getRadius();
	}
	
	
	
		
	//draws a neuron of the specified color using the current coordinate values in NodeFactory
	public void drawNeuron(Color color, Graphics2D g2d) {
		//#GASNEATVISUALS
		double activationLevel = neuron.calculateActivation();
		int actIntLevel =   Math.max(0, Math.min(255, (int)(activationLevel * 255))) ;

		Color activationColor = new Color(actIntLevel,actIntLevel,actIntLevel  );
		g2d.setColor(  activationColor  );
		
		double outerRingFactor = 1.3;
		g2d.fillOval((int) (x-outerRingFactor*radius), (int) (y-outerRingFactor*radius), (int) (2*outerRingFactor*radius), (int) (2*outerRingFactor*radius));

		
		g2d.setColor(color);
		g2d.fillOval((int) (x-radius), (int) (y-radius), (int) (2*radius), (int) (2*radius));
	}

	

	//draws a neuron of the specified color using the current coordinate values in NodeFactory
	//and the state of activation of the given neuron
	public void drawNeuron(Graphics2D g2d) {
		// if activated
		//if (neuron.getActivationConcentration() >= neuron.getThreshold()) {
		//#GASNEATVISUALS
		
		if (neuron.calculateActivation() >= 0.5 ) {			
			// draw neuron with different color
			if(neuron.getLayerType().equals(SpreadsheetConstants.LAYER_TYPES.OUTPUT) ) {
				drawNeuron(Color.GREEN, g2d);
			} else  {
				if (neuron.isGasEmitter() ){
					
					
					
					
					//drawNeuron(neuron.getGasColor(), g2d);
					drawNeuron( network.getGasMap().get( neuron.getGasProductionTypeInt()  ).getColor() , g2d);
					
					
				}
				else {
					drawNeuron(Color.YELLOW, g2d);
				}	
			}		
		} 
		else {
			if (neuron.isGasEmitter() ){
				
				
				
				
				//drawNeuron(neuron.getGasColor(), g2d);
				drawNeuron( network.getGasMap().get( neuron.getGasProductionTypeInt()  ).getColor() , g2d);
				
				
			} else {
				drawNeuron(Color.RED, g2d);
			}
		}
	}
	
	//turns a line or curve into an arrow
	public void drawNeuronIcon(Graphics2D g2d) {
			
	}
	
	//draws the label in appropriate location based on curvature and direction of synapse
	public void drawNeuronLabel(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		
		StringBuilder label = new StringBuilder(  );
		label.append( neuron.getNeuronID()  );
		
		//only show synaptic gas type if it produces it
		if (  !neuron.getSynapseProductionType().equals("G0") ) {
			label.append( "(s->" + neuron.getSynapseProductionType()  +") " );
		}
		
		label.append(" R="+ neuron.getReceptor().getReceptorID().replaceAll("G", "").replaceAll("NO", "") );
		
		if (  neuron.getGasProductionTypeInt() != 0 ) {
		
			label.append(" (P->G"+neuron.getGasProductionTypeInt()+")" );
		}
		
		g2d.drawString(label.toString() , x+xoffset, y+yoffset );

		//for debugging using visualization
		if (logger.isDebugEnabled() ) {
			String str = ""+ neuron.getActivationConcentration();
			g2d.drawString("activation con: "+str, x-8, y+10);			str = LoggingUtilities.getStringFormat( neuron.getReceptor().getBufferedConcentration() );
			g2d.drawString("buffered   con:"+ str, x-8, y+25);
			str = LoggingUtilities.getStringFormat( neuron.getReceptor().getBuiltUpConcentrations() );
			g2d.drawString("builtup    con:"+str, x-8, y+40);
		}
	}
	
}
