package borderlineBot.util.hashing;

import java.util.BitSet;

import lombok.Getter;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.game.Unit;
import borderlineBot.util.Point;

/** Hashes the given Game Board */
public class Hasher{
	
	
	//--classes--
	/** The Hash representation of a map based on 2 Long values*/
	public static class Hash2Long extends Hash<Hash2Long>{
		
		/** Hash stored in 2*64 bits (with slack), first long */
		@Getter private long primaryLong;
		/** Hash stored in 2*64 bits (with slack), second long */
		@Getter private long secondaryLong;
		
		
		/** Builds Hash2Long based on the given game board */
		public Hash2Long(GameBoard board){
			//Set board to view the currently active player
			board = board.viewToActivePlayer();
			//define longs
			long[] longs = new long[]{0, 0};
			//Define Counter Split Off
			int splitOff = 62;
			//TODO: Define Encoding!
			//Put longs values to use
			this.primaryLong = longs[0];
			this.secondaryLong = longs[1];
		}
		
		
		/** Returns the GameBoard this Hash is based on */
		@Override public GameBoard rebuild() {
			return null; // TODO implement this
		}
		
		/** Compares this Hash to given Hash */
		@Override public int compareTo(Hash2Long hash){
			int out = Long.compare(hash.getPrimaryLong(), primaryLong);
			if(out==0){
				out = Long.compare(hash.getSecondaryLong(), secondaryLong);
			}
			return out;
		}
		
	}
	
}
