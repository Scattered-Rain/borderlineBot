package borderlineBot.bot.moveOrderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.util.Direction;

/** A very simple heuristic Move Orderer */
public class BasicOrderer implements MoveOrderer{
	
	/** Returns List of Moves as GameBoard would */
	public List<Move> orderMoves(GameBoard board) {
		List<Move> moves = board.generateAllLegalMoves();
		Collections.shuffle(moves);
		List<Move> other = new ArrayList<Move>();
		List<Move> out = new ArrayList<Move>();
		//Kill Units
		for(Move move : moves){
			for(Tile tile : move.getTargetTileList(board)){
				if(!tile.isEmpty() && tile.getPlayer().isOpponent(board.getActivePlayer())){
					out.add(move);
				}
				else{
					other.add(move);
				}
			}
		}
		moves = other;
		other = new ArrayList<Move>();
		//Moving Up
		for(Move move : moves){
			if(move.getMoveDir(board).isDirection(board.getMovingDirection(board.getActivePlayer()))){
				out.add(move);
			}
			else{
				other.add(move);
			}
		}
		moves = other;
		other = new ArrayList<Move>();
		//Does nothing special
		for(Move move : moves){
			out.add(move);
		}
		return out;
	}
	
}
