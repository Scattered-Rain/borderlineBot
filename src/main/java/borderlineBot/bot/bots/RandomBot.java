package borderlineBot.bot.bots;

import java.util.List;

import borderlineBot.bot.Bot;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
import borderlineBot.util.RNG;

/** Randomly Moving Bot */
public class RandomBot implements Bot{
	
	/** AI processing */
	public Move move(GameBoard board, Player player){
		List<Move> moves = board.generateAllLegalMoves();
		return moves.get(RNG.nextInt(moves.size()));
	}

}
