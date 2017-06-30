package experiment_builder.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager; import org.apache.log4j.Logger;

import experiment_builder.constants.Constants;
import experiment_builder.constants.ConstantsMapping;
import lombok.Getter;
import lombok.Setter;


/**/
public class BuildingMenuPanel extends JPanel {
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger( BuildingMenuPanel.class );

	private JButton addAgentButton;
	private JButton addRewardButton;

	private JLabel agentLabel;
	private JButton btnLaunch;
	private JButton btnLaunchWithFile;
	private JButton btnLaunchWithoutUI;
	private @Getter @Setter JButton btnLoadXML;
	private JButton btnSaveCellEvent;
	private JButton btnSaveCellType;
	private @Getter @Setter JButton btnSaveXML;
	private JButton btnTest;

	private @Getter @Setter JList cellEvent;
	private JLabel CellEventLabel;
	private JLabel cellLabel;
	// JLIST
	private @Getter @Setter JList cellType;

	private JLabel CellTypeLabel;
	private JButton clearSelectionButton;
	private @Getter JTextField colInput;
	private JLabel colInputLabel;
	// JButton
	private JButton createMazeButton;
	// File Chooser
	private JFileChooser fc;
	private JButton finalizeGridButton;
	private Font font;
	private GridBagConstraints gridBagConstraints;
	private GridBagLayout gridBagLayout;
	private DefaultListModel<String> listEvent;
	private DefaultListModel<String> listModel;
	private JLabel rewardLabel;

	private @Getter JComboBox rewardType;

	private JLabel rewardTypeLabel;
	private @Getter JTextField rewardValue;
	private JLabel rewardValueLabel;
	// Textbox
	private @Getter JTextField rowInput;
	// Labels
	private JLabel rowInputLabel;
	
	private JLabel sensorInputLabel;
	private @Getter JComboBox sensorList;

	private JLabel actionMapperLabel;
	private @Getter JComboBox actionMapperList;

	
	public BuildingMenuPanel() {
		init();
	}

	public void addBtnCreateListener(ActionListener listener) {
		createMazeButton.addActionListener(listener);
		addRewardButton.addActionListener(listener);
		addAgentButton.addActionListener(listener);
		clearSelectionButton.addActionListener(listener);
		finalizeGridButton.addActionListener(listener);
		btnLaunch.addActionListener(listener);
		btnLaunchWithFile.addActionListener(listener);
		btnSaveXML.addActionListener(listener);
		btnLoadXML.addActionListener(listener);
		btnSaveCellType.addActionListener(listener);
		btnSaveCellEvent.addActionListener(listener);
		btnTest.addActionListener(listener);
		sensorList.addActionListener(listener);
		actionMapperList.addActionListener(listener);

	}

	public void addNotGuiListener(ActionListener listener) {
		btnLaunchWithoutUI.addActionListener(listener);
	}

	public JPanel buildGridMakingPanel() {

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(20, 20, 20, 20);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		JPanel p1 = new JPanel(new GridBagLayout());
		/* Grid */
		rowInputLabel = new JLabel("Num Rows");
		rowInputLabel.setFont(font);
		rowInput = new JTextField();
		colInputLabel = new JLabel("Num Cols");
		colInput = new JTextField();
		colInputLabel.setFont(font);
		
		
		String[] sensorTypes = new String[  ConstantsMapping.SENSOR_TYPES.values().length ];
		
		int i=0;
		for (ConstantsMapping.SENSOR_TYPES type: ConstantsMapping.SENSOR_TYPES.values() ) {
			sensorTypes[i] = type.name();
			i++;
		}

		//Create the combo box, select item at index 0
		sensorList = new JComboBox<String>(sensorTypes);
		sensorInputLabel = new JLabel("Sensor Type");
		sensorInputLabel.setFont(font);
		sensorList.setSelectedIndex(0);
		sensorList.setPreferredSize(new Dimension(200,30) );
		
		
		String[] actionMapperTypes = new String[  ConstantsMapping.ACTION_MAPPER_TYPES.values().length ];
		
		i=0;
		for (ConstantsMapping.ACTION_MAPPER_TYPES type: ConstantsMapping.ACTION_MAPPER_TYPES.values() ) {
			actionMapperTypes[i] = type.name();
			i++;
		}
		
		actionMapperList = new JComboBox<String>(actionMapperTypes);
		actionMapperLabel = new JLabel("ActionMap Type:");
		actionMapperLabel.setFont(font);
		actionMapperList.setSelectedIndex(0);
		actionMapperList.setPreferredSize(new Dimension(200,30) );
		
		

		createMazeButton = new JButton("Create Grid");
		setButtonStyle( createMazeButton );

		clearSelectionButton = new JButton("Clear Selection");
		setButtonStyle( clearSelectionButton );

		
		finalizeGridButton = new JButton("Finalize Maze, Set Sensor, Action Map");
		setButtonStyle( finalizeGridButton);
		

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		p1.add(rowInputLabel, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 0;
		p1.add(rowInput, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		p1.add(colInputLabel, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 1;
		p1.add(colInput, gbc);
		
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 2;
		p1.add(sensorInputLabel, gbc);
		
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 2;
		p1.add(sensorList, gbc);
		
		
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 3;
		p1.add(actionMapperLabel, gbc);
		
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 3;
		p1.add(actionMapperList, gbc);
		
		
		
		
		

		gbc.ipady = 25; // make this component tall
		gbc.weightx = 0.0;
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 4;
		p1.add(createMazeButton, gbc);

		gbc.ipady = 25; // make this component tall
		gbc.weightx = 0.0;
		gbc.gridwidth = 4;
		gbc.gridx = 0;
		gbc.gridy = 5;
		p1.add(finalizeGridButton, gbc);

		gbc.ipady = 25; // make this component tall
		gbc.weightx = 0.0;
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 6;
		p1.add(clearSelectionButton, gbc);

		return p1;
	}

	private void setButtonStyle(JButton button) {
		button.setFont(font);
		button.setOpaque(true);
		button.setBackground(Color.DARK_GRAY);
		button.setForeground(Color.WHITE);
	}

	public JPanel buildLaunchModesPanel() {

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		JPanel p1 = new JPanel(new GridBagLayout());
		btnLaunch = new JButton("Launch with UI : Manual Input");
		setButtonStyle( btnLaunch );
		
		
		btnLaunchWithFile = new JButton("Launch with UI : File Input");
		setButtonStyle( btnLaunchWithFile );
		
		btnLaunchWithoutUI = new JButton("Launch without UI");
		setButtonStyle(btnLaunchWithoutUI  );
		
		btnTest = new JButton("Test");
		setButtonStyle( btnTest );

		gbc.ipady = 25;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		p1.add(btnLaunch, gbc);

		gbc.ipady = 25;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		p1.add(btnLaunchWithFile, gbc);

		gbc.ipady = 25;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 2;
		p1.add(btnLaunchWithoutUI, gbc);

		gbc.ipady = 25;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 3;
		p1.add(btnTest, gbc);
		return p1;
	}

	public JPanel buildPropertiesSettingPanel() {

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 20, 20, 10);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		JPanel p1 = new JPanel(new GridBagLayout());
		/* Reward */
		rewardLabel = new JLabel("REWARDS");
		rewardValueLabel = new JLabel("Reward Value");
		rewardValue = new JTextField();
		rewardValueLabel.setFont(font);
		rewardTypeLabel = new JLabel("Reward Type");
		rewardType = new JComboBox(Constants.REWARD_TYPES.values());
		rewardType.setFont(font);
		rewardTypeLabel.setFont(font);
		addRewardButton = new JButton("Add Reward");
		addRewardButton.setFont(font);
		addRewardButton.setOpaque(true);
		//addRewardButton.setBackground(Color.DARK_GRAY);
		//addRewardButton.setForeground(Color.WHITE);

		// Agent
		agentLabel = new JLabel("AGENT");
		addAgentButton = new JButton("Add Agent");
		setButtonStyle( addAgentButton );

		// Initialize List
		cellLabel = new JLabel("CELLS");
		CellTypeLabel = new JLabel("Cell Type ");
		CellTypeLabel.setFont(font);
		cellType = new JList<>(initializeCellTypeList());
		cellType.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		cellType.setLayoutOrientation(JList.VERTICAL);
		cellType.setFont(font);

		btnSaveCellType = new JButton("Save Cell Type");
		setButtonStyle( btnSaveCellType );

		CellEventLabel = new JLabel("Cell Event ");
		CellEventLabel.setFont(font);
		cellEvent = new JList<>(initializeCellEventsList());
		cellEvent.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		cellEvent.setLayoutOrientation(JList.VERTICAL);
		cellEvent.setFont(font);

		btnSaveCellEvent = new JButton("Save Cell Event");
		setButtonStyle( btnSaveCellEvent );

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		p1.add(rewardValueLabel, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		p1.add(rewardValue, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		p1.add(rewardTypeLabel, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		p1.add(rewardType, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 1;
		p1.add(addRewardButton, gbc);

		gbc.ipady = 25; // make this component tall
		gbc.weightx = 0.0;
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 2;
		p1.add(addAgentButton, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 3;
		p1.add(CellTypeLabel, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 3;
		p1.add(cellType, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 4;
		p1.add(btnSaveCellType, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 5;
		p1.add(CellEventLabel, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 5;
		p1.add(cellEvent, gbc);

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 6;
		p1.add(btnSaveCellEvent, gbc);

		return p1;
	}

	public JPanel buildSaveLoadButtonsPanel() {

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		JPanel p1 = new JPanel(new GridBagLayout());
		btnSaveXML = new JButton("Save XML");
		setButtonStyle( btnSaveXML );
		btnSaveXML.setEnabled(false);

		btnLoadXML = new JButton("Load XML");
		setButtonStyle( btnLoadXML );

		gbc.ipady = 25;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		p1.add(btnSaveXML, gbc);

		gbc.ipady = 25;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		p1.add(btnLoadXML, gbc);

		return p1;
	}

	public String chooseOpenFile() {
		fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		File selectedFile = null;
		String filePath = "";
		FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
		fc.setFileFilter(xmlfilter);
		int status = fc.showOpenDialog(this.getRootPane());

		if (status == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			if (selectedFile != null && selectedFile.exists())
				filePath = selectedFile.getAbsolutePath();
		}
		return filePath;
	}

	public String chooseSaveFile() {
		fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		File selectedFile = null;
		String filePath = "";

		int status = fc.showSaveDialog(this.getRootPane());

		if (status == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			logger.debug("");
			if (!selectedFile.getName().endsWith(".xml")) {
				filePath = selectedFile.getName() + ".xml";
				filePath = selectedFile.getParentFile().getAbsolutePath() + "\\" + filePath;
			} else
				filePath = selectedFile.getAbsolutePath();
		}

		return filePath;
	}

	private void init() {
		font = new Font("TimesRoman", Font.BOLD + Font.ITALIC, 16);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setMaximumSize(this.getPreferredSize());
		CollapsablePanel gridMakingPanel = new CollapsablePanel("CONSTRUCT GRID", buildGridMakingPanel());
		gridMakingPanel.headerPanel_.setBackground(Color.DARK_GRAY);
		gridMakingPanel.headerPanel_.setForeground(Color.WHITE);

		CollapsablePanel propertiesSettingPanel = new CollapsablePanel("SET PROPERTIES", buildPropertiesSettingPanel());
		propertiesSettingPanel.headerPanel_.setBackground(Color.DARK_GRAY);
		propertiesSettingPanel.headerPanel_.setForeground(Color.WHITE);

		CollapsablePanel saveLoadButtonsPanel = new CollapsablePanel("SAVE & LOAD", buildSaveLoadButtonsPanel());
		saveLoadButtonsPanel.headerPanel_.setBackground(Color.DARK_GRAY);
		saveLoadButtonsPanel.headerPanel_.setForeground(Color.WHITE);

		CollapsablePanel buildLaunchModesPanel = new CollapsablePanel("LAUNCH MODES", buildLaunchModesPanel());
		buildLaunchModesPanel.headerPanel_.setBackground(Color.DARK_GRAY);
		buildLaunchModesPanel.headerPanel_.setForeground(Color.WHITE);

		this.add(gridMakingPanel);
		this.add(propertiesSettingPanel);
		this.add(saveLoadButtonsPanel);
		this.add(buildLaunchModesPanel);
		logger.debug(experiment_builder.constants.Constants.MAINFRAMEHEIGHT - (gridMakingPanel.headerPanel_.getHeight()
				+ propertiesSettingPanel.headerPanel_.getHeight() + saveLoadButtonsPanel.headerPanel_.getHeight()
				+ buildLaunchModesPanel.headerPanel_.getHeight()));
		this.add(Box.createVerticalStrut(700));
		this.setVisible(true);

	}

	public DefaultListModel<String> initializeCellEventsList() {

		listEvent = new DefaultListModel<>();
		listEvent.addElement("ShuffleRewards");
		listEvent.addElement("DeathEvent");
		listEvent.addElement("RemoveAllRewardsEvent");
		listEvent.addElement("ReplenishAllRewardsEvent");
		listEvent.addElement("AddRewardToFitnessEvent");
		listEvent.addElement("FlipOrientation");
		
		return listEvent;

	}

	public DefaultListModel<String> initializeCellTypeList() {

		listModel = new DefaultListModel<>();
		listModel.addElement("Home");
		listModel.addElement("Maze-End");
		listModel.addElement("Turn");
		
		//could use this to give inputs about available directions to move
		//listModel.addElement("4directionalspace");
		
		return listModel;

	}

	public void lockGrid() {
		colInput.setEnabled(false);
		rowInput.setEnabled(false);
		sensorList.setEnabled(false);
		actionMapperList.setEnabled(false);

		
	}
	
	public void unlockGrid() {
		colInput.setEnabled(true);
		rowInput.setEnabled(true);
		sensorList.setEnabled(true);
		actionMapperList.setEnabled(true);
		
	}
	
}
