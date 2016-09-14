package borderlineBot;

import borderlineBot.bot.Bot;
import borderlineBot.bot.bots.RandomBot;
import borderlineBot.game.Game;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.gui.GUI;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import borderlineBot.util.RNG;

/** Launches the application */
public class Launcher {
	
	/** Main */
	public static void main(String[] args){
		Bot bot = new RandomBot();
		GUI gui = new GUI(null);
		Game debug = new Game(gui, gui);
		//debug.flipView();
		gui.setNewGame(debug);
		while(!debug.gameOver()){
			debug.nextTurn();
			try{
				Thread.sleep(250);
			}catch(Exception ex){}
		}
	}
	
}
