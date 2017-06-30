package gasNEAT.util;

import java.text.DecimalFormat;

public class LoggingUtilities {

	
	public static String getStringFormat(Object[] array) {
		
		StringBuilder text = new StringBuilder();
		text.append("[");
		for (Object o: array) {
			text.append(" "+o.toString() +", ");
		}
		text.append("]");
		return text.toString();
		
	}
	
	
	
public static String getStringFormat(double[] array) {
		
		DecimalFormat df = new DecimalFormat("0.00#");
		StringBuilder text = new StringBuilder();
		text.append("[");
		for (double d: array) {
			String dd = df.format(d);
			text.append(" "+  dd.substring(0, 4) +", ");
		}
		text.append("]");
		return text.toString();
		
	}
}
