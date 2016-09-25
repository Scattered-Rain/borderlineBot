package borderlineBot.bot.bots;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;

/** Bot based on an Alpha/Beta pruned Nega Max Search */
public class BasicAlphaBetaNegaMaxBot implements Bot{
	
	/** The Move Orderer used for this Bot */
	private MoveOrderer orderer;
	/** The Evaluation Function used for this Bot */
	private EvaluationFunction eval;
	
	
	/** Constructs new Bot */
	public BasicAlphaBetaNegaMaxBot(MoveOrderer orderer, EvaluationFunction eval){
		this.orderer = orderer;
		this.eval = eval;
	}
	
	
	/** Bot Processing */
	public Move move(GameBoard board, Player player) {
		
		return null;
	}
	
}
