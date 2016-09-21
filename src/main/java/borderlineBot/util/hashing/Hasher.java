package borderlineBot.util.hashing;

import java.util.Scanner;

import lombok.Getter;
import borderlineBot.bot.bots.RandomBot;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.game.Game;
import borderlineBot.game.Player;
import borderlineBot.game.Unit;
import borderlineBot.gui.GUI;
import borderlineBot.util.Point;

/** Hashes the given Game Board */
public class Hasher{
	
	/** Split Off value used for simple hashing purposes */
	private static final int SPLIT_OFF = 60;
	
	
	/** Hashes the given GameBoard */
	public static Hash hashBoard(GameBoard board){
		return new Hash(board);
	}
	
	
	//--classes--
	/** The Hash representation of a map based on 2 Long values*/
	public static class Hash{
		
		/** Hash stored in 2*64 bits (with slack), first long */
		@Getter private long primaryLong;
		/** Hash stored in 2*64 bits (with slack), second long */
		@Getter private long secondaryLong;
		
		
		/** Builds Hash2Long based on the given game board */
		public Hash(GameBoard board){
			//Every tile that is without untit has 1 bit=false, any other tile has 3 bits {true, isCurrentPlayer, isUnitTypeONE}
			final boolean alive = true;
			//Set board to view the currently active player
			board = board.getViewToActivePlayer();
			boolean[][] boardEncode = new boolean[GameBoard.BOARD_SIZE.getY()*GameBoard.BOARD_SIZE.getX()][];
			int counter = 0;
			for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
				for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
					Tile tile = board.getTile(new Point(cx, cy));
					if(tile.isEmpty()){
						boardEncode[counter] = new boolean[]{!alive};
					}
					else{
						boardEncode[counter] = new boolean[]{alive, tile.getPlayer().isSame(board.getActivePlayer()), tile.getUnit().isUnit(Unit.ONE)};
					}
					counter++;
				}
			}
			//define longs
			long[] longs = new long[]{0, 0};
			counter = 0;
			int subCounter = 0;
			for(int c=0; c<longs.length; c++){
				for(int c2=0; c2<SPLIT_OFF; c2++){
					if(counter<boardEncode.length){
						if(boardEncode[counter][subCounter]){
							longs[c] |= 1L << c2;
						}
						if(subCounter==boardEncode[counter].length-1){
							subCounter = 0;
							counter++;
						}
						else{
							subCounter++;
						}
					}
				}
			}
			//Put longs values to use
			this.primaryLong = longs[0];
			this.secondaryLong = longs[1];
		}
		
		/** Manually build Hash */
		public Hash(long primaryLong, long secondaryLong){
			this.primaryLong = primaryLong;
			this.secondaryLong = secondaryLong;
		}
		
		
		/** Returns the GameBoard this Hash is based on */
		public GameBoard rebuild(Player player) {
			Tile[][] board = new Tile[GameBoard.BOARD_SIZE.getY()][GameBoard.BOARD_SIZE.getX()];
			Tile empty = new Tile();
			Tile[][] units = new Tile[][]{{new Tile(player, Unit.ONE), new Tile(player, Unit.TWO)}, {new Tile(player.getOpponent(), Unit.ONE), new Tile(player.getOpponent(), Unit.TWO)}};
			long[] longs = new long[]{primaryLong, secondaryLong};
			int counter = 0;
			for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
				for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
					if(checkBit(longs[counter/SPLIT_OFF], counter%SPLIT_OFF)){
						counter++;
						boolean playerCur = checkBit(longs[counter/SPLIT_OFF], counter%SPLIT_OFF);
						counter++;
						boolean unitCur = checkBit(longs[counter/SPLIT_OFF], counter%SPLIT_OFF);
						counter++;
						board[cy][cx] = units[playerCur?0:1][unitCur?0:1];
					}
					else{
						board[cy][cx] = empty;
						counter++;
					}
				}
			}
			if(!player.isSame(GameBoard.LOCAL_VIEW)){
				Tile[][] newBoard = new Tile[board.length][board[0].length];
				for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
					for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
						newBoard[cy][cx] = board[GameBoard.BOARD_SIZE.getY()-cy-1][GameBoard.BOARD_SIZE.getX()-cx-1];
					}
				}
				board = newBoard;
			}
			GameBoard out = new GameBoard(player, board, player, 0, Player.NONE);
			return out;
		}
		
		/** Returns the bit of the value at the given position */
		private boolean checkBit(long val, int position){
			return ((val >> position) & 1)!=0;
		}
		
		/** Compares this Hash to given Hash */
		public int compareTo(Hash hash){
			int out = Long.compare(hash.getPrimaryLong(), primaryLong);
			if(out==0){
				out = Long.compare(hash.getSecondaryLong(), secondaryLong);
			}
			return out;
		}
		
		/** Returns whether the given Hash is identical to this */
		public boolean equals(Hash hash){
			return compareTo(hash)==0;
		}
		
		/** Returns 32 bit Hash Code derived from the long representation in this Hash class (Used for Hash Map, etc) */
		@Override public int hashCode(){
			int out = 0;
			out = new Long(primaryLong).hashCode();
			out = new Long(secondaryLong+out).hashCode();
			return out;
		}
		
		/** Returns the String of this has */
		public String toString(){
			return primaryLong+" "+secondaryLong;
		}
		
	}
	
	
	//--debug--
	/** Shows the Board corresponding to the given Hashmap */
	public static void debugViewHash(long primary, long secondary){
		Hash hash = new Hash(primary, secondary);
		Game game = new Game(new RandomBot(), new RandomBot());
		game.debugSpliceGameBoard(hash.rebuild(Player.RED));
		GUI gui = new GUI(game, "Debug Hash View");
	}
	
	/** Shows given Hash */
	public static void debugViewHash(Hash hash){
		if(hash instanceof Hash){
			Hash hash2l = (Hash)hash;
			debugViewHash(hash2l.getPrimaryLong(), hash2l.getSecondaryLong());
		}
	}
	
	/** Shows manually input Hash's Board */
	public static void debugViewHash(){
		Scanner scanner = new Scanner(System.in);
		debugViewHash(scanner.nextLong(), scanner.nextLong());
		scanner.close();
	}
	
}
