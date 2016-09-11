package borderlineBot.util;

import java.util.Random;

/** Static class that provides easy access to RNG */
public class RNG{
	
	/** The Random object used by this class */
	private static Random random = new Random();
	
	
	/** Returns the Random object */
	public static Random getRandObject(){
		return random;
	}
	
	/** Returns random int between 0 (inclusive) and bound (exclusive) */
	public static int nextInt(int bound){
		return random.nextInt(bound);
	}
	
	/** Returns random double between 0.0 and 1.0 */
	public static double nextDouble(){
		return random.nextDouble();
	}
	
	/** Returns random float between 0.0f and 1.0f */
	public static float nextFloat(){
		return random.nextFloat();
	}
	
	/** Returns random boolean */
	public static boolean nextBoolean(){
		return random.nextBoolean();
	}
	
}
