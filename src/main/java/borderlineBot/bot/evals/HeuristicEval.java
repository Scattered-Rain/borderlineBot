package borderlineBot.bot.evals;

import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;
import borderlineBot.game.Unit;
import borderlineBot.util.Point;

/** Basic Evaluation function driven by arbitrary heuristics */
public class HeuristicEval implements EvaluationFunction{
	
	/** The bias the Evaluation Function gives to clear win/lose conditions */
	private static final float CLEAR_BIAS = 100000f;
	/** Evaluation Function used for clear win/loss predictions */
	private static final ClearWinLossEval clear = new ClearWinLossEval();
	
	
	/** Evaluates */
	public float evaluate(GameBoard board, Player player){
		float score = 0;
		board.setView(player);
		//Clear Check
		score += clear.evaluate(board, player)*CLEAR_BIAS;
		//Eval start:
		//Counts Units of both players on the board
		int numberPieces[][] = board.countUnits(player);
		final float[][] valueBias = new float[][]{{1.0f, 1.5f}, {-1.0f, -1.5f}};//[Player][Unit]
		for(int cy=0; cy<numberPieces.length; cy++){
			for(int cx=0; cx<numberPieces[0].length; cx++){
				score += numberPieces[cy][cx]*valueBias[cy][cx];
			}
		}
		return score;
	}
	
}
