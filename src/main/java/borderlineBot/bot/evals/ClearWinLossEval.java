package borderlineBot.bot.evals;

import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;
import borderlineBot.util.Constants;

/** Evaluation Function that returns whether the given Board returns a clear (i.e. guaranteed) victory or loss for the given Player */
public class ClearWinLossEval implements EvaluationFunction{
	
	/** Evaluates, returns win/loss value if given board win/loss state, -1/+1 for every turn away from clearly reaching that state, 0 for no clear results */
	public int evaluate(GameBoard board, Player player) {
		if(board.getWinner().isLegalPlayer()){
			return board.getWinner().isSame(player)?Constants.WIN_SCORE:Constants.LOSE_SCORE;
		}
		//TODO: Add further cases
		return 0;
	}
	
	
	//--statics--
	/** Static access to the clear win/loss evaluation function */
	public static int clearWinLossEval(GameBoard board, Player player){
		return eval.evaluate(board, player);
	}
	
	/** Static Reference to this Object */
	private static final ClearWinLossEval eval = new ClearWinLossEval();
	
}
