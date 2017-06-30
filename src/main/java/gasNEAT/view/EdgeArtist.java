package gasNEAT.view;

import static gasNEAT.view.Constants.EDGE_WIDTH_FACTOR;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import gasNEAT.model.GasNeatNeuralNetwork;
import gasNEAT.model.GasNeatSynapse;
import gasNEAT.nn.GasNeatNeuron;

/**
 * For graphics
 *
 */
public class EdgeArtist {

	//Flyweight factory for curved and straight arrows TODO  Will hope to add flyweight functionality later
	//but first we'll just draw the edges and get them looking right.
	private ArrayList<Arc2D> curves = new ArrayList<Arc2D>();
	private ArrayList<Line2D> lines = new ArrayList<Line2D>();
	
	
	//Instead of passing everything around, this seems cleaner, as long as it's handled carefully.
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	//the unit vector pointing from the source to the target.
	private double unitX;
	private double unitY;
	
	private ImageIcon arrowheadIcon;
	private Image arrowheadImage;
	
	private GasNeatSynapse synapse;
	
	//Needs reference to Neuron and Synapse maps and a place to draw
	private GasNeatNeuralNetwork network;
	private JPanel myPanel;
	
	/**
	 * @param network neural network
	 * @param panel Jpanel
	 */
	public EdgeArtist(GasNeatNeuralNetwork network, JPanel panel) {
		this.network = network;
		this.myPanel = panel;
	}
	
	/**
	 * Draw the appropriate representation of the Synapse
	 * @param synapse
	 * @param color
	 * @param g2d
	 */
	public void drawEdge(GasNeatSynapse synapse, Color color, Graphics2D g2d) {
		this.synapse = synapse;
		GasNeatNeuron sourceNeuron = network.getNeuronMap().get(synapse.getSourceNeuron());
		GasNeatNeuron targetNeuron = network.getNeuronMap().get(synapse.getTargetNeuron());
		
		setEdgeEndpoints(sourceNeuron, targetNeuron);

		//draw input and output edges - maybe not needed
		/*
		if(sourceNeuron.getLayerType().equals("I"))
		{
			int x = sourceNeuron.getX()*ViewConstants.SCALING_FACTOR;
			int y = sourceNeuron.getY()*ViewConstants.SCALING_FACTOR;
			
			g2d.setStroke(new java.awt.BasicStroke(1));
			g2d.setColor(Color.BLACK);
			g2d.drawLine(x-50, y, x, y);
			System.out.println("DOES THIS GET HIT?");
			System.exit(1);

		}
		if(targetNeuron.getLayerType().equals("O"))
		{
			int x = targetNeuron.getX()*ViewConstants.SCALING_FACTOR;
			int y = targetNeuron.getY()*ViewConstants.SCALING_FACTOR;
			g2d.setStroke(new java.awt.BasicStroke(1));
			g2d.setColor(Color.BLACK);
			g2d.drawLine(x, y, x+50, y);
			System.out.println("DOES THIS GET HIT?");
			System.exit(1);

		}
		//*/
		
		//draw Synapse
		curvedLine(color, g2d);

		if (network.isLabeled()) {
			drawSynapseLabel(synapse, g2d);
		}
		
		//add arrow head image at target neuron
		drawArrowHead(g2d);
	}
		
	// will return true if the drawn synapse needs to be curved
	/**
	 * @param synapse
	 * @return
	 */
	public boolean needsCurvature(GasNeatSynapse synapse) {
		//TODO
		//check for whether another synapse is connected in the opposite direction
		return false;
	}
	
	
	//either builds and draws a new line, or takes an old line and redraws it in a new location
	/**
	 * @param color
	 * @param g2d
	 */
	public void curvedLine(Color color, Graphics2D g2d) {
		
		Color originalColor = g2d.getColor();
		BasicStroke originalStroke = (BasicStroke) g2d.getStroke();
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke((float) (EDGE_WIDTH_FACTOR * Math.abs(synapse.getSynapticWeight())) ));
		//g2d.drawLine(x1,y1,x2,y2);
		
		int x0 = (x1 + x2)/2;
		int y0 = (y1 + y2)/2;
		int height = Math.abs(y1-y2);
		int width = Math.abs(x1-x2);
		int distance = height+width;
		QuadCurve2D q = new QuadCurve2D.Float();
		
		//#GASNEATVISUALS
		
		if (x2-x1 < 0) {
			q.setCurve(x1, y1, x0, y0+width/ViewConstants.CONTROL_POINT_SCALE, x2, y2);	
		} else {
			q.setCurve(x1, y1, x0, y0-width/ViewConstants.CONTROL_POINT_SCALE, x2, y2);
		}
		
		g2d.draw(q);
		
		/*
		g2d.setColor(Color.CYAN);
		g2d.fillOval(upperLeftX, upperLeftY, 5, 5);
		
		g2d.setColor(Color.MAGENTA);
		g2d.fillOval(x2, y2, 25, 25);
		
		g2d.setStroke(new BasicStroke((float) (EDGE_WIDTH_FACTOR * 0.1*Math.abs(synapse.getSynapticWeight())) ));
		
		g2d.setColor(Color.ORANGE);
		g2d.drawRect(upperLeftX, upperLeftY, width, height);
		*/
		//g2d.setColor(Color.YELLOW);
		//g2d.fillOval(normalX1, normalY1, 25, 25);
		
		//g2d.setColor(Color.ORANGE);
		//g2d.fillOval(normalX2, normalY2, 25, 25);
		//*/
		
		
		
		
		
		g2d.setStroke(originalStroke);
		g2d.setColor(originalColor);
	}
	
	
	//turns a line or curve into an arrow
	/**
	 * @param g2d
	 */
	public void drawArrowHead(Graphics2D g2d) {
		
		
		//get the arrowhead image
		ClassLoader cl = getClass().getClassLoader();
		arrowheadIcon = new ImageIcon(cl.getResource(Constants.ImageFilePaths.RED_TRIANGLE.getPath()));
		//arrowheadImage = arrowheadIcon.getImage();
		
		//#GASNEATVISUALS
		
		
		 arrowheadImage = arrowheadIcon.getImage();
		
		//translate to the intersection of the synapse with the target
		g2d.translate(x2, y2);
		//rotate the image to the slope of the chord that connects the two neurons' centers
		// positive because we are rotating the coordinate system
		g2d.rotate(Math.atan2(y2-y1,x2-x1) - Math.PI / 2 );
        //move to corner of where image will be
        g2d.translate(-1 * arrowheadIcon.getIconWidth() / 2, -1 * arrowheadIcon.getIconHeight());
        //draw image at origin
	    g2d.drawImage(arrowheadImage, 0,  0, myPanel);
	    //translate back to (x2,y2)
	    g2d.translate(arrowheadIcon.getIconWidth() / 2, arrowheadIcon.getIconHeight());
	    //rotate back
	    g2d.rotate(-1 * Math.atan2(y2-y1,x2-x1) + Math.PI / 2 );
	    //translate back
	    g2d.translate(-x2, -y2);
	    
	    
	}
	
	//finds the intersections of the boundaries of two neurons with the line that connects their centers
	//sets the local variables to those two points (x1,y1) for the source neuron, (x2,y2) for the target neuron
	/**
	 * @param source
	 * @param target
	 */
	public void setEdgeEndpoints(GasNeatNeuron source, GasNeatNeuron target) {
		int sourceX = source.getX()*ViewConstants.SCALING_FACTOR;;
		int sourceY = source.getY()*ViewConstants.SCALING_FACTOR;;
		int targetX = target.getX()*ViewConstants.SCALING_FACTOR;;
		int targetY = target.getY()*ViewConstants.SCALING_FACTOR;;
		
		//distance from source to target
		double distance = Math.sqrt( Math.pow((targetX - sourceX), 2) + Math.pow((targetY - sourceY), 2) );
		
		//find the unit vector pointing from source to target
		unitX = (targetX - sourceX) / distance;		
		unitY = (targetY - sourceY) / distance;
		
		//find and set where the line intersects the circle near the source
		x1 = sourceX + (int) (source.getRadius() * unitX);
		y1 = sourceY + (int) (source.getRadius() * unitY);
		//and near the target (pointing toward the source)
		x2 = targetX - (int) (target.getRadius() * unitX);
		y2 = targetY - (int) (target.getRadius() * unitY);				
	}
	
	
	/**
	 * draws the label in appropriate location based on curvature and direction of synapse
	 * @param synapse
	 * @param g2d
	 */
	public void drawSynapseLabel(GasNeatSynapse synapse, Graphics2D g2d) {
		GasNeatNeuron sourceNeuron = network.getNeuronMap().get(
				synapse.getSourceNeuron());
		GasNeatNeuron targetNeuron = network.getNeuronMap().get(
				synapse.getTargetNeuron());
		int x1 = sourceNeuron.getX()*ViewConstants.SCALING_FACTOR;
		int y1 = sourceNeuron.getY()*ViewConstants.SCALING_FACTOR;
		int x2 = targetNeuron.getX()*ViewConstants.SCALING_FACTOR;
		int y2 = targetNeuron.getY()*ViewConstants.SCALING_FACTOR;
		int x = 0;
		int y = 0;
		if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
			x = (int) Math.ceil(((x1 + x2) / 2) - Math.ceil(0.5 * 16)); 
				// magic numbers: 1 is the width of the string
			y = (int) Math.ceil(((y1 + y2) / 2) - 7); 
				// magic numbers: 7 is the height of the string
			y -= (int) Math.floor(5 * Math.abs((y1 - y2) / (x1 - x2))); 
				// adjust for the slope of the line
			y -= ((BasicStroke) g2d.getStroke()).getLineWidth(); 
				// adjust for the thickness of the line
		} else {
			if ((x1 - x2) >= (y1 - y2)) {
				x = (int) Math.ceil(((x1 + x2) / 2) + 7); // 7 is just extra
				y = (int) Math.ceil(((y1 + y2) / 2) - Math.ceil(0.5 * 7));
					// magic numbers: 7 is the height of the string
				y -= ((BasicStroke) g2d.getStroke()).getLineWidth();
					// adjust for the thickness of the line
				x += ((BasicStroke) g2d.getStroke()).getLineWidth();
			} else {
				x = (int) Math.ceil(((x1 + x2) / 2) - 7 - 16); 
					// 7 is extra and 16 is the width of the string
				y = (int) Math.ceil(((y1 + y2) / 2) - Math.ceil(0.5 * 7));
					// magic numbers: 7 is the height of the string
				y -= ((BasicStroke) g2d.getStroke()).getLineWidth(); 
					// adjust for the thickness of the line
				x -= ((BasicStroke) g2d.getStroke()).getLineWidth();
			}
		}
		
		int width = Math.abs(x1-x2);
		int vert = 0;
		if (x2-x1 < 0) {
			vert =  +width/ViewConstants.CONTROL_POINT_SCALE/3;	
		} else {
			vert =  -width/ViewConstants.CONTROL_POINT_SCALE/3;
		}
		
		Font originalFont = g2d.getFont();
		g2d.setFont(new Font("default", Font.BOLD, 16));
		//g2d.drawString(String.format("%.3g%n", synapse.getSynapticWeight()), x , y);//y+vert);
		//g2d.setColor( Color.GRAY  );
		
		g2d.drawString(String.format("%.3g%n", synapse.getSynapticWeight()), (x1+x2)/2 , (y1+y2)/2 + vert );//y+vert);
		g2d.setColor( Color.BLACK  );
		
		g2d.setFont( originalFont);
				
	}
	
}
