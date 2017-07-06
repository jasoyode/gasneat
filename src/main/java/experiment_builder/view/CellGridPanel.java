package experiment_builder.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import experiment_builder.model.Agent;
import experiment_builder.model.CellGrid;

public class CellGridPanel extends JPanel implements MouseListener, MouseMotionListener {
	
	private static Logger logger = Logger.getLogger( CellGridPanel.class );
	
	//SHOULD CONTAIN FULL MODEL
	private @Getter @Setter CellGrid cellGrid;
	
	private @Setter Rectangle[] boundingRectangle;
	
	private @Getter int pos;
	private @Getter @Setter int selectedRectangle;
	
	//TODO: consider changing to private once understood
	public boolean mazeFinalized = false;
	public boolean mazeSetFlag = false;
	public boolean rewardSetFlag = false;


	public CellGridPanel() {
		this.setPreferredSize(new Dimension(Constants.RIGHTPANELWIDTH, Constants.RIGHTPANELHEIGHT));
		this.setBounds(Constants.MAINFRAMEWIDTH / 3 + 1, 0, Constants.RIGHTPANELWIDTH, Constants.RIGHTPANELHEIGHT);
		this.setVisible(true);
		init();
		addMouseListener(this);
		addMouseMotionListener(this);
		requestFocus();
	}

	public void addAgent(Agent a) {
		// agents.add(a);
		cellGrid.setAgent( a );
	}

	public void setCellGrid(CellGrid cellGrid) {
		this.cellGrid = cellGrid;
	}

	public void clearSelection() {
		Arrays.fill(cellGrid.getVisibility(), false);
		cellGrid.setAgent( new Agent() );
	}

	public void init() {
		cellGrid = new CellGrid();
		cellGrid.setAgent( new Agent() );
		pos = -1; // Default out of Array index
		cellGrid.setFinalizedMazeCells( new ArrayList<Integer>() );
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//do nothing

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		if (mazeFinalized == false && cellGrid != null && boundingRectangle != null) {
			for (int i = 0; i < boundingRectangle.length; i++) {
				if (boundingRectangle[i].contains(mouseX, mouseY)) {
					if (i != pos)
						cellGrid.getVisibility()[i] = !cellGrid.getVisibility()[i];
					pos = i;
					repaint();

				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		if (mazeFinalized == true) {
			for (int i = 0; i < boundingRectangle.length; i++) {
				if (boundingRectangle[i].contains(mouseX, mouseY)) {
					pos = i;
					selectedRectangle = i;
					repaint();
				}
			}
		} else if (cellGrid != null && boundingRectangle != null) {
			for (int i = 0; i < boundingRectangle.length; i++) {
				if (boundingRectangle[i].contains(mouseX, mouseY)) {
					if (i != pos)
						cellGrid.getVisibility()[i] = !cellGrid.getVisibility()[i];
					pos = i;
					selectedRectangle = i;
					repaint();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//do nothing
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (mazeSetFlag == true) {
			for (int i = 0; i < boundingRectangle.length; i++) {
				if (cellGrid.getVisibility()[i]) {

					if (mazeFinalized && i == this.getSelectedRectangle()) {
						g.setColor(Color.GREEN);
						g.fillRect(boundingRectangle[i].x - 1, boundingRectangle[i].y - 1, boundingRectangle[i].width + 2, boundingRectangle[i].height + 2);
						g.setColor(Color.DARK_GRAY);
						g.fillRect(boundingRectangle[i].x + 4, boundingRectangle[i].y + 4, boundingRectangle[i].width - 9, boundingRectangle[i].height - 9);
					} else {
						g.setColor(Color.DARK_GRAY);
						g.fillRect(boundingRectangle[i].x, boundingRectangle[i].y, boundingRectangle[i].width - 1, boundingRectangle[i].height - 1);
					}
					
					if (!cellGrid.getFinalizedMazeCells().contains(i)) {
						cellGrid.getFinalizedMazeCells().add(i);
					}
				} else if (!cellGrid.isActiveInExperiment()) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(boundingRectangle[i].x, boundingRectangle[i].y, boundingRectangle[i].width - 1, boundingRectangle[i].height - 1);

				}
			}

			if (mazeFinalized) {
				for (int i = 0; i < cellGrid.getBoundingRectangle().length; i++) {
					
					int position = cellGrid.getAgent().getPos();
					if (position == i) {
						g.setColor(Color.RED);
						g.fillOval(boundingRectangle[position].x + (boundingRectangle[position].width / 2),
								boundingRectangle[position].y + (boundingRectangle[position].height / 2),
								(int) (0.25 * boundingRectangle[position].width - 1),
								(int) (0.25 * boundingRectangle[position].width - 1));
						
						g.drawString( cellGrid.getAgent().getOrientation(), boundingRectangle[i].x+20, boundingRectangle[i].y+20 );
						
						
					}
					g.setColor(Color.WHITE);
					g.setFont(new Font("Verdana", Font.BOLD, 14));
					
					if (cellGrid.getRewards().containsKey(i) && cellGrid.getVisibility()[i]) {
						
						String firstCharOfReward = cellGrid.getRewards().get(i).getType().substring(0,1);
						g.drawString(firstCharOfReward + ":"+cellGrid.getRewards().get(i).getValue() + "", boundingRectangle[i].x + 40,
								boundingRectangle[i].y + 40);
					}
					
					if (cellGrid.getCellProperties().containsKey(i) && cellGrid.getVisibility()[i]) {
						
						g.drawString(cellGrid.getCellProperties().get(i).toString() , boundingRectangle[i].x + 2,
								boundingRectangle[i].y + 60);
					}
					
					if (cellGrid.getCellEvents().containsKey(i) && cellGrid.getVisibility()[i]) {
						
						int yOffset = 80;
						for (String event: cellGrid.getCellEvents().get(i) ) {
							
							g.drawString(event , boundingRectangle[i].x + 2,
									boundingRectangle[i].y + yOffset);
							yOffset += 20;							
						}
					}
				}
			}
		}
	}
}