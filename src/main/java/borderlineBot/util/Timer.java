package borderlineBot.util;

public class Timer{
	
	public static final int GENERIC_TIME = 1000 * 6;
	
	public static final int TOTAL_TIME = 15*60*1000;
	public static int timeUsed = 0;
	
	public static int getTimeToCalc(){
		return Math.min(GENERIC_TIME, remainingTime()/25);
	}
	
	public static int remainingTime(){
		return TOTAL_TIME - timeUsed;
	}
	
	public static void usedTime(int timeUsedIter){
		timeUsed += timeUsedIter;
	}
	
}
