package borderlineBot;

import java.util.Scanner;

import borderlineBot.bot.Bot;
import borderlineBot.bot.bots.BasicTreeSearchBot;
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
import borderlineBot.util.hashing.HashManager;
import borderlineBot.util.hashing.Hasher;
import borderlineBot.util.hashing.Hasher.Hash;

/** Launches the application */
public class Launcher {
	
	/** Main */
	public static void main(String[] args){
		GUI gui = new GUI(null);
		Bot[] bots = new Bot[]{
				gui,//0
				new RandomBot(),//1
				new EvaluateOnePlyBot(new HeuristicEval()),//2
				new BasicTreeSearchBot(new HeuristicEval(), 2),//3
				new BasicTreeSearchBot(new HeuristicEval(), 3),//4
		};
		Game debug = new Game(bots[2], bots[3]);
		gui.setNewGame(debug);
		EvaluationFunction debugEval = new HeuristicEval();
		while(!debug.gameOver()){
			try{Thread.sleep(250);}catch(Exception ex){}
			debug.nextTurn();
		}
	}
	
}
