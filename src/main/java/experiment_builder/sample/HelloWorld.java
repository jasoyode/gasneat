package experiment_builder.sample;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class HelloWorld extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		HelloWorld helloWorld = new HelloWorld();
		JLabel label = new JLabel();
		label.setText("Hello there, world!");
		helloWorld.add(label);
		helloWorld.pack();
		helloWorld.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		helloWorld.setVisible(true);
		
		String testSwitch = "test";
		
		switch (testSwitch) {
			case "yes":
				label.setText("yes");
			break;
			case "no":
				label.setText("yes");
			break;
			default:
				//fall through
		}
		
	}
}
