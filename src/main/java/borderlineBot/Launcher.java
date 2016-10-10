package borderlineBot;

import java.util.Scanner;

import borderlineBot.bot.Bot;
import borderlineBot.bot.bots.BasicAlphaBetaNegaMaxBot;
import borderlineBot.bot.bots.BasicTreeSearchBot;
import borderlineBot.bot.bots.EvaluateOnePlyBot;
import borderlineBot.bot.bots.RandomBot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.evals.TrainedEvaluation;
import borderlineBot.bot.evals.TrainedEvaluationTrainer;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.Game;
import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.gui.GUI;
import borderlineBot.util.Constants;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import borderlineBot.util.RNG;
import borderlineBot.util.hashing.HashManager;
import borderlineBot.util.hashing.Hasher;
import borderlineBot.util.hashing.Hasher.Hash;

/** Launches the application */
public class Launcher {
	
	/** Initialize all important Systems an call Launch */
	public static void main(String[] args){
		HashManager.initHashManager(0);
		launch();
		//TrainedEvaluationTrainer.trainEvaluation();
	}
	
	/** Launch for actual Program */
	private static  void launch(){
		GUI gui = new GUI(null);
		EvaluationFunction eval = new TrainedEvaluation(TrainedEvaluation.TRAINING_RESULTS[0]);
		Bot[] bots = new Bot[]{
				gui,//0
				new RandomBot(),//1
				new EvaluateOnePlyBot(eval),//2
				new BasicTreeSearchBot(eval, 2),//3
				new BasicTreeSearchBot(eval, 2),//4
				new BasicAlphaBetaNegaMaxBot(new MoveOrderer.DefaultMoveOrder(), eval, 8),//5
		};
		Game game = new Game(bots[5], bots[5]);
		gui.setNewGame(game);
		while(!game.gameOver()){
			try{Thread.sleep(250);}catch(Exception ex){}
			game.nextTurn();
		}
		HashManager.writeTableManager(HashManager.TABLE_HASH_FILES[0]);
	}
	
	
	//--settings--
	/** Variable deciding whether some non-necessary/util/debug operations should be omitted during execution */
	public static final boolean COMPETITIVE = false;
	
}
