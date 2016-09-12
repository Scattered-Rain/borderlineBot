package borderlineBot;

import borderlineBot.bot.Bot;
import borderlineBot.bot.bots.RandomBot;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import borderlineBot.util.RNG;

/** Launches the application */
public class Launcher {
	
	/** Main */
	public static void main(String[] args){
		GameBoard debug = new GameBoard();
		Bot bot = new RandomBot();
		System.out.println(debug);
		while(!debug.getWinner().isLegalPlayer()){
			Move move = bot.move(debug, debug.getActivePlayer());
			debug = debug.move(move);
			System.out.println("---");
			System.out.println(debug);
			try{
				Thread.sleep(250);
			}catch(Exception ex){};
		}
		System.out.println("Game Over, "+debug.getWinner()+" won.");
	}
	
}
