package helper;

import java.awt.Color;
import java.util.Random;

public class RandomGenerator {
	
	private static Random rand = new Random();
	
	//TODO
	public static String randName() {
		return null;
	}
	
	public static Color randColor() {
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		
		return new Color(r, g, b);
	}
}
