package helper;

import java.util.ArrayList;

import world.Tile;

/**
 * Miscellaneous convenience methods
 * @author Matthew Zane
 *
 */
public class Utility {
	private final static int PREFIX_OFFSET = 5;
	private final static String[] PREFIX_ARRAY = {"f", "p", "n", "µ", "m", "", "k", "M", "G", "T"};

	public static String getEngineeringNotation(double val, int dp) {
	   // If the value is zero, then simply return 0 with the correct number of dp
	   if (val == 0) {
		   return String.format("%." + dp + "f", 0.0);
	   }

	   double log10 = Math.log10(Math.abs(val));

	   // Determine how many orders of 3 magnitudes the value is
	   int count = (int) Math.floor(log10/3);

	   // Calculate the index of the prefix symbol
	   int index = count + PREFIX_OFFSET;

	   // Scale the value into the range 1<=val<1000
	   double newVal = val / Math.pow(10, count * 3);

	   if (index >= 0 && index < PREFIX_ARRAY.length) {
	      // If a prefix exists use it to create the correct string
	      return String.format("%." + dp + "f %s", newVal, PREFIX_ARRAY[index]);
	   }
	   else {
	      // If no prefix exists just make a string of the form 000e000
	      return String.format("%." + dp + "fe%d", newVal, count * 3);
	   }
	}
	
	public static ArrayList<Tile> deepCopyTileList(ArrayList<Tile> tileList) throws CloneNotSupportedException {
		ArrayList<Tile> copy = new ArrayList<Tile>();
		
		for (Tile temp : tileList) {
			copy.add((Tile) temp.clone());
		}
		
		return copy;
	}
}
