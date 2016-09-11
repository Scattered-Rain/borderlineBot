package borderlineBot;

import borderlineBot.game.GameBoard;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import borderlineBot.util.RNG;

/** Launches the application */
public class Launcher {
	
	/** Main */
	public static void main(String[] args){
		GameBoard debug = new GameBoard();
		System.out.println(debug.clone().clone());
	}
	
}
