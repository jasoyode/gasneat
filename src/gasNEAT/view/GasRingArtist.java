package gasNEAT.view;

import static gasNEAT.view.Constants.GAS_BOUNDARY_THICKNESS;
import static gasNEAT.view.Constants.GAS_RING_WIDTH_PARAMETER;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import gasNEAT.model.GasDispersionSlot;
import gasNEAT.model.GasDispersionUnit;
import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.nn.GasNeatNeuron;
import gasNEAT.view.Constants.VisualizationModes;

/**
 * For drawing gases in simulation
 *
 */
public class GasRingArtist {

	// Neuron Coordinates
	private GasNeatNeuron neuron;
	private int x;
	private int y;
	private double radius;
	private GasDispersionUnit gasChannel;
	private ArrayList<GasDispersionSlot> channel;
	private double slotSize;
	private Color color;



	// Slot Coordinates
	private GasDispersionSlot slot;

	private ImageIcon neuronIcon;
	private Image neuronImage;

	// Needs reference to Neuron and Synapse maps and a place to draw
	private GasNeatNeuralNetwork network;
	private JPanel myPanel;

	public GasRingArtist(GasNeatNeuralNetwork network, JPanel panel) {
		this.network = network;
		this.myPanel = panel;
	}


	// uses other functions to draw the appropriate representation of the gas
	// rings
	/**
	 * Draws circles representing the rings
	 * @param neuron
	 * @param g2d
	 */
	public void drawGasRings(GasNeatNeuron neuron, Graphics2D g2d) {

		color= network.getGasMap().get( neuron.getGasProductionType() ).getColor();
		
		setCoordinates(neuron);
		slotSize = neuron.getGasDispersionUnit().getSlotSize()*ViewConstants.SCALING_FACTOR;

		//for (int i = 0; i < channel.size(); i++) {
		//must reverse order of rings to how it show up properly
		for (int i = channel.size()-1; i >=0; i--) {
			
			//System.out.println("network.getMode()" + network.getMode());
			//System.exit(1);
			
			slot = channel.get(i);
			
			
			if (network.getMode() == VisualizationModes.TRANSLUCENT_GAS) {
					
				drawTranslucentGas(g2d);
			
			} else if (network.getMode() == VisualizationModes.GAS_RINGS) { 
				drawGasRing(g2d);
				System.out.println("DONT DRAW GAS-RINGS");
				System.exit(1);
			} else {
				System.out.println("MUST SET VISLAUZIATION MODE!");
				System.exit(1);
			}
			
			//TODO
			//draw small (black? light gray?) ring around neuron to indicate that it receives the gas.
			
			if (network.isLabeled()) {
				drawGasRingLabel(neuron, g2d);
			}

			// add icon to a gas ring
			drawGasRingIcon(g2d);
		}
	}
	
	//
	//http://stackoverflow.com/questions/35524394/draw-ring-with-given-thickness-position-and-radius-java2d
	//
	private static Shape createRingShape(
	        double centerX, double centerY, double outerRadius, double thickness)
	    {
	        Ellipse2D outer = new Ellipse2D.Double(
	            centerX - outerRadius, 
	            centerY - outerRadius,
	            outerRadius + outerRadius, 
	            outerRadius + outerRadius);
	        Ellipse2D inner = new Ellipse2D.Double(
	            centerX - outerRadius + thickness, 
	            centerY - outerRadius + thickness,
	            outerRadius + outerRadius - thickness - thickness, 
	            outerRadius + outerRadius - thickness - thickness);
	        Area area = new Area(outer);
	        area.subtract(new Area(inner));
	        return area;
	    }
	

	/**
	 * @param g2d
	 */
	public void drawGasRing(Graphics2D g2d) {

		if (slot.getGasConcentration() != 0) {
			g2d.setStroke(new BasicStroke(GAS_BOUNDARY_THICKNESS));
			//double radius = slot.getSlotRadius();
			double radius = slot.getSlotRadius()*ViewConstants.SCALING_FACTOR;
			double slotUpperLimit = radius + slotSize;
			g2d.setColor(Color.BLACK);
			//g2d.drawOval((int) (neuron.getX() - slotUpperLimit), (int) (neuron.getY() - slotUpperLimit), (int) (2 * slotUpperLimit), (int) (2 * slotUpperLimit));
			//g2d.drawOval((int) (neuron.getX() - radius), (int) (neuron.getY() - radius), (int) (2 * radius), (int) (2 * radius));
			
			g2d.drawOval((int) (x - slotUpperLimit), (int) (y - slotUpperLimit), (int) (2 * slotUpperLimit), (int) (2 * slotUpperLimit));
			g2d.drawOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
			drawInnerRing(slotUpperLimit, g2d);
		} else {
			System.out.println("slot.getGasConcentration() = 0");
		}
		
		
		
	}
	
	/**
	 * @param slotUpperLimit
	 * @param g2d
	 */
	public void drawInnerRing(double slotUpperLimit, Graphics2D g2d)
	{	
		g2d.setColor(color);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke((float) (slot.getGasConcentration()) *  GAS_RING_WIDTH_PARAMETER));
		slotUpperLimit -= slotSize / 2;
		//g2d.drawOval((int) (neuron.getX() - slotUpperLimit), (int) (neuron.getY() - slotUpperLimit), (int) (2 * slotUpperLimit), (int) (2 * slotUpperLimit));
		g2d.drawOval((int) (x - slotUpperLimit), (int) (y - slotUpperLimit), (int) (2 * slotUpperLimit), (int) (2 * slotUpperLimit));
		
		System.out.println( "x= "+x  );
		System.out.println( "y= "+y  );
		System.out.println("slotUpperLimit= " + slotUpperLimit);
		
		System.exit(-1);
		g2d.setColor(color);
	}
	
	/**
	 * @param g2d
	 */
	public void drawTranslucentGas(Graphics2D g2d) {
		
		
		if (color == null) {
			color = Color.MAGENTA;
			System.out.println("COLOR IS NULL inside GasRingArtist");
			System.exit(-1);
		}
		
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		//System.out.println("Gas color based on: " + slot.getGasConcentration());
		
		int alpha = (int) Math.floor(slot.getGasConcentration() * 255*0.25 );
		Color translucentColor = new Color(r,g,b,alpha);
		g2d.setColor(translucentColor);
		g2d.setStroke(new BasicStroke((float) slotSize));
		//int ringRadius = (int) Math.floor(slot.getSlotRadius() + slotSize);
		int ringRadius = (int) Math.floor(slot.getSlotRadius()*ViewConstants.SCALING_FACTOR + slotSize);
		int innerRingRadius = (int) Math.floor(slot.getSlotRadius()*ViewConstants.SCALING_FACTOR);
		
		//g2d.drawOval(neuron.getX() - ringRadius, neuron.getY() - ringRadius, 2 * ringRadius, 2 * ringRadius);
		
		/*
		g2d.drawOval(x - ringRadius, y - ringRadius, 2 * ringRadius, 2 * ringRadius);
		
		//System.out.println("x " + x);
		//System.out.println("y " + y);
		///System.out.println("ringRadius " + ringRadius);
		//System.out.println("innerRingRadius " + innerRingRadius);
		
		//
		g2d.setColor( g2d.getBackground() );
		g2d.drawOval(x - innerRingRadius, y - innerRingRadius, 2 * innerRingRadius, 2 * innerRingRadius);
		//*/
		
		
		
		
		Shape ring = createRingShape(x, y, ringRadius, innerRingRadius); 
		
		g2d.fill(ring);
		
        //g.setColor(Color.CYAN);
        //g.fill(ring);
        //g.setColor(Color.BLACK);
        //g.draw(ring);
		
		
		g2d.setColor(Color.BLACK);
		
	}
	
	/**
	 * @param neuron
	 */
	public void setCoordinates(GasNeatNeuron neuron) {
		this.neuron = neuron;
		//STARTHERE
		x = neuron.getX()*ViewConstants.SCALING_FACTOR;
		y = neuron.getY()*ViewConstants.SCALING_FACTOR;
	
		radius = neuron.getRadius()*ViewConstants.SCALING_FACTOR;
		
		gasChannel = neuron.getGasDispersionUnit();
		channel = neuron.getGasDispersionUnit().getGasDispersionSlotList();
		//this does not work!
		//color = neuron.getGasColor();
		
		color= network.getGasMap().get( neuron.getGasProductionType() ).getColor();
		
	}

	// turns a line or curve into an arrow
	public void drawGasRingIcon(Graphics2D g2d) {

	}

	// draws the label in appropriate location based on curvature and direction
	// of synapse
	public void drawGasRingLabel(GasNeatNeuron neuron, Graphics2D g2d) {

	}

}
