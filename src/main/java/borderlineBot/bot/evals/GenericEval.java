package borderlineBot.bot.evals;

import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;

/** Generic Evaluation Function */
public class GenericEval implements EvaluationFunction{
	
	/** Evaluation */
	public int evaluate(GameBoard board, Player player) {
		int score = 0;
		score += board.countUnits(player)[0][0] * 100;
		score += board.countUnits(player)[0][1] * 110;
		score -= board.countUnits(player)[1][0] * 80;
		score -= board.countUnits(player)[1][1] * 90;
		return score;
	}
	
}
