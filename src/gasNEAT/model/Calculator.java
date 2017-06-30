package gasNEAT.model;

import java.util.Random;

public class Calculator {
	public double getRandomDouble(double rangeMin, double rangeMax) {
		Random r = new Random();
		return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
	}
	
	public int getMax(int[] input) {
		int max = Integer.MIN_VALUE;
		
		for(int i = 0; i < input.length; i++) {
			if(input[i] > max) {
				max = input[i];
			}
		}
		return max;
	}
}
