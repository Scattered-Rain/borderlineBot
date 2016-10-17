package borderlineBot.bot.moveOrderers;

import java.util.List;

import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.util.Constants;
import borderlineBot.util.Tuple;

/** Move Orderer which has learned an ordering scheme */
public class TrainedOrderer implements MoveOrderer{
	
	/** Ordering */
	public List<Move> orderMoves(GameBoard board) {
		//Maintenance
		List<Move> moves = board.generateAllLegalMoves();
		Tuple<Move, Integer>[] valuedMoves = new Tuple[moves.size()];
		for(int c=0; c<valuedMoves.length; c++){
			valuedMoves[c] = new Tuple<Move, Integer>(moves.get(c), 0);
		}
		//The juicy bits
		for(int c=0; c<valuedMoves.length; c++){
			Move move = valuedMoves[c].getA();
			int score = 0;
			//TODO: Implement all the juicy bits
			valuedMoves[c].setB(score);
		}
		//Sorting & Returning
		for(int c=0; c<valuedMoves.length; c++){
			for(int c2=c+1; c2<valuedMoves.length; c2++){
				if(valuedMoves[c].getB()<valuedMoves[c2].getB()){
					Tuple<Move, Integer> help = valuedMoves[c];
					valuedMoves[c] = valuedMoves[c2];
					valuedMoves[c2] = help;
				}
			}
		}
		moves.clear();
		for(int c=0; c<valuedMoves.length; c++){
			moves.add(valuedMoves[c].getA());
		}
		return moves;
	}
	
	//--classes--
	/** Class containing the properties of a Move in numerical form */
	private static class MoveProperties{
		
		/** Array containing all properties of a given move */
		private int[] props;
		
		/** Constructs new Move Properties */
		public MoveProperties(Move move, GameBoard board){
			this.props = new int[100];
			
		}
		
	}
	
}
