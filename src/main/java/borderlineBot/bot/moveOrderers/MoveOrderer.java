package borderlineBot.bot.moveOrderers;

import java.util.Collections;
import java.util.List;

import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;

/** Object designed to make an exhaustive ordered List of all Moves according to presumed viability */
public interface MoveOrderer{
	
	
	/** Returns exhaustive list of all possible moves by the given Player ordered according to a certain heuristic (best to worst)*/
	public List<Move> orderMoves(GameBoard board);
	
	
	//--classes--
	/** The Default Move order given by Game Board */
	public static class DefaultMoveOrder implements MoveOrderer{
		
		/** Returns List of Moves as GameBoard would */
		public List<Move> orderMoves(GameBoard board) {
			List<Move> moves = board.generateAllLegalMoves();
			Collections.shuffle(moves);
			return moves;
		}
		
	}
	
}
