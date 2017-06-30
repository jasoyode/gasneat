package experiment_builder.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;
import lombok.Getter;

// I would suggest not to create classes for panels inside left panel 

public class HumanButtonControlPanel extends JPanel{
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( HumanButtonControlPanel.class );
	
	private JButton downBtn;
	private JButton eatBtn;
	private JButton leftBtn;
	private JButton rightBtn;
	private JButton upBtn;
	private JButton restBtn;
	
	private JButton execButton;
	private @Getter JTextField   outputActivationTextField;
	
	public HumanButtonControlPanel() {
		this.setPreferredSize(new Dimension(Constants.MAZEFRAMEWIDTH,  Constants.MAZEFRAMEHEIGHT / 6));
		this.setBackground(new Color(223, 247, 168));
		this.setVisible(true);
		init();
	}

	public void addBtnCreateListener(ActionListener listener) {
		upBtn.addActionListener(listener);
		downBtn.addActionListener(listener);
		leftBtn.addActionListener(listener);
		rightBtn.addActionListener(listener);
		eatBtn.addActionListener(listener);
		restBtn.addActionListener(listener);
		execButton.addActionListener(listener);
	}
	
	private void init() {
		upBtn = new JButton("UP");
		downBtn = new JButton("DOWN");
		leftBtn = new JButton("LEFT");
		rightBtn = new JButton("RIGHT");
		eatBtn = new JButton("EAT");
		restBtn = new JButton("REST");
		
		this.setLayout(new BoxLayout( this, BoxLayout.Y_AXIS ) );
		
		this.add(upBtn);
		this.add(downBtn);
		this.add(leftBtn);
		this.add(rightBtn);
		this.add(eatBtn);
		this.add(restBtn);
		
		execButton = new JButton("EXECUTE");
		outputActivationTextField = new JTextField();
		outputActivationTextField.setPreferredSize(new Dimension(200, 20));
		
		this.add(execButton);
		this.add(outputActivationTextField);
		
		this.setVisible(true);
		
	}
		
}
