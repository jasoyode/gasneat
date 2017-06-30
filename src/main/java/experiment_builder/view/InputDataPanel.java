package experiment_builder.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;
import lombok.Getter;
import lombok.Setter;

public class InputDataPanel extends JPanel{
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( InputDataPanel.class );
	
	private @Getter TitledBorder inputDataPanelTitle;
	private @Setter @Getter JLabel inputLabel;
	
	public InputDataPanel(){
		
		inputDataPanelTitle = BorderFactory.createTitledBorder("Input Data");
		inputLabel = new JLabel("DATA");
		this.setPreferredSize(new Dimension(Constants.MAZEFRAMEWIDTH/2,  Constants.MAZEFRAMEHEIGHT / 6));
		this.setBackground(new Color(223, 247, 168));
		this.setBorder(inputDataPanelTitle);
		this.add(inputLabel);
		this.setVisible(true);
		
		
	}
	
	public void setData(String formattedData) {
		inputLabel.setText( formattedData );
	}
	
	



}
