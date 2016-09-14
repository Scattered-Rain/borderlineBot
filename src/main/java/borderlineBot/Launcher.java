package borderlineBot;

import borderlineBot.bot.Bot;
import borderlineBot.bot.bots.EvaluateOnePlyBot;
import borderlineBot.bot.bots.RandomBot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.evals.HeuristicEval;
import borderlineBot.game.Game;
import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.gui.GUI;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import borderlineBot.util.RNG;

/** Launches the application */
public class Launcher {
	
	/** Main */
	public static void main(String[] args){
		Bot bot = new EvaluateOnePlyBot(new HeuristicEval());
		GUI gui = new GUI(null);
		Game debug = new Game(bot, bot);
		gui.setNewGame(debug);
		EvaluationFunction debugEval = new HeuristicEval();
		while(!debug.gameOver()){
			try{Thread.sleep(250);}catch(Exception ex){}
			debug.nextTurn();
			System.out.println(debugEval.evaluate(debug.getCurrentStateClone(), Player.RED));
		}
	}
	
}
