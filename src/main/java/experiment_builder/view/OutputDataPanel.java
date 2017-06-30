package experiment_builder.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.ann_integration.ExperimentCreatorFitnessFunction;
import experiment_builder.constants.Constants;
import experiment_builder.input_action_map.InputActionMappable;
import lombok.Getter;
import lombok.Setter;

public class OutputDataPanel extends JPanel{
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( OutputDataPanel.class );
	
	private @Getter TitledBorder outputDataPanelTitle;
	private @Setter @Getter JLabel outputLabel;
	
	
	InputActionMappable mapper;  
	
	
	public OutputDataPanel(InputActionMappable mapper ){
		
		outputDataPanelTitle = BorderFactory.createTitledBorder("Output Data");
		this.setPreferredSize(new Dimension(Constants.MAZEFRAMEWIDTH,  Constants.MAZEFRAMEHEIGHT / 6));
		this.setBackground(new Color(223, 247, 168));
		this.setBorder(outputDataPanelTitle);
		
		outputLabel = new JLabel("OUTPUT DATA");
		this.add(outputLabel);
		
		this.mapper = mapper;
		this.setVisible(true);
		
		
		
	}
	
	
	public void setData(double[] data) {
		
		
		//MovementOnlyInputActionMap mapper = new MovementOnlyInputActionMap();
		String action = mapper.stringActionFromDoubleValue( data[0]  );
		
		outputLabel.setText( action +" "+ ExperimentCreatorFitnessFunction.getStringFormat(data )  );
		
	}
	
	
	

}
