package borderlineBot.bot.bots;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.evals.GenericEval;
import borderlineBot.bot.moveOrderers.BasicOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;

public class LoveMachine implements Bot{
	
	/** The main Bot used by Love Machine */
	private Bot mainBot;
	
	/** Bot that can return a move in case anything breaks */
	private Bot backup;
	
	/** Constructs new Love Machine */
	public LoveMachine(){
		EvaluationFunction eval = new GenericEval();
		this.mainBot = new AlphaBetaTranspositionTableNegaMaxBot(new BasicOrderer(), eval, 6);
		this.backup = new EvaluateOnePlyBot(eval);
	}
	
	
	/** Processes Move */
	public Move move(GameBoard board, Player player) {
		Move move = backup.move(board, player);
		
		return null;
	}
	
}
