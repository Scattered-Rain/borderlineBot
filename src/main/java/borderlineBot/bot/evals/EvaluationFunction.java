package borderlineBot.bot.evals;

import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;

/** The interface used by evaluation functions */
public interface EvaluationFunction{
	
	/** Returns evaluation score of the given Player on the given State of the game */
	public int evaluate(GameBoard board, Player player);
	
}
