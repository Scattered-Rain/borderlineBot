package borderlineBot.game;

import lombok.Getter;

/** Object which contains all information about the current game state */
public class GameBoard{
	
	/** The Player for which the board is oriented */
	private Player view;
	/** The board as represented with Tiles */
	private Tile[][] board;
	
	/** The Player that is currently allowed to make a move */
	private Player moving;
	
	
	/** Constructs new GameBoard */
	public GameBoard(){
		init();
	}
	
	/** Builds fresh board (As seen by the Red Player) */
	private void init(){
		//Where the player rows are
		final Tile empty = new Tile();
		final Tile blueBack = new Tile(Player.BLU, Unit.TWO);
		final Tile blueFront = new Tile(Player.BLU, Unit.ONE);
		final Tile redBack = new Tile(Player.RED, Unit.TWO);
		final Tile redFront = new Tile(Player.RED, Unit.ONE);
		final Tile[] rowMakeup = new Tile[]{blueBack, blueFront, empty, empty, empty, empty, empty, redFront, redBack};
		//Sets up board
		this.board = new Tile[9][6];
		for(int cy=0; cy<board.length; cy++){
			Tile rowUnit = rowMakeup[cy];
			for(int cx=0; cx<board[0].length; cx++){
				board[cy][cx] = rowUnit;
			}
		}
	}
	
	
	/** Returns Tile at given Point */
	public Tile getLocalTile(int x, int y){
		if(y>=0 && x>=0 && y<board.length && x<board.length){
			return board[y][x];
		}
		else{
			return OUT_OF_BOUNDS;
		}
	}
	
	
	/** Returns String representing this Game Board */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(int cy=0; cy<board.length; cy++){
			for(int cx=0; cx<board[0].length; cx++){
				buffer.append(getLocalTile(cx, cy));
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	
	//--classes--
	/** Defines a tile on the Game Board in terms of Unit and Player */
	public static class Tile{
		
		/** The Player that controls this Tile */
		@Getter private Player player;
		/** The unit type that is on this Tile */
		@Getter private Unit unit;
		/** Whether this Tile is actually occupied by a unit */
		@Getter private boolean isUsed;
		/** Whether this tile is in bounds (used for util purposes) */
		@Getter private boolean inBounds;
		
		
		/** Constructs new Tile with given Unit and Player */
		public Tile(Player player, Unit unit){
			this.player = player;
			this.unit = unit;
			this.isUsed = true;
			this.inBounds = true;
		}
		
		/** Constructs new empty Tile */
		public Tile(){
			this.player = Player.NONE;
			this.unit = Unit.NONE;
			this.isUsed = false;
			this.inBounds = true;
		}
		
		/** Constructs new ILLEGAL out-of-bounds Tile. This should never be on the GameBoard! */
		public Tile(boolean outOfBounds){
			this();
			this.inBounds = false;
		}
		
		
		/** Returns String representing this Unit */
		public String toString(){
			if(!inBounds){
				return "[ x X x ]";
			}
			else if(!isUsed){
				return "[       ]";
			}
			else{
				StringBuffer buffer = new StringBuffer();
				buffer.append('[').append(player).append(' ').append(unit).append(']');
				return buffer.toString();
			}
		}
		
	}
	
	
	//--statics--
	/** Representation of any Tile which is out of bounds */
	private static final Tile OUT_OF_BOUNDS = new Tile(false);
	
	
}
