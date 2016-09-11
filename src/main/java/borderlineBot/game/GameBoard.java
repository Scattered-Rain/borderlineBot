package borderlineBot.game;

import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import lombok.Getter;

/** Object which contains all information about the current game state */
public class GameBoard{
	
	/** The Player for which the board is oriented */
	@Getter private Player view;
	/** The board as represented with Tiles (Should only be accessed over getTile() method) */
	private Tile[][] board;
	
	/** The Player that is currently allowed to make a move */
	@Getter private Player activePlayer;
	
	
	/** Constructs new GameBoard */
	public GameBoard(){
		init();
		this.view = LOCAL_VIEW;
		this.activePlayer = LOCAL_VIEW;
		board[3][3] = new Tile(Player.BLU, Unit.TWO);//TODO: Remove this debug Unit
	}
	
	/** Constructs new Board for cloning purposes */
	private GameBoard(Player view, Tile[][] board, Player moving){
		this.view = view;
		this.board = board;
		this.activePlayer = moving;
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
		this.board = new Tile[BOARD_SIZE.getY()][BOARD_SIZE.getX()];
		for(int cy=0; cy<board.length; cy++){
			Tile rowUnit = rowMakeup[cy];
			for(int cx=0; cx<board[0].length; cx++){
				board[cy][cx] = rowUnit;
			}
		}
	}
	
	
	/** Returns Local Tile at given Point */
	public Tile getTile(Point point){
		//Determine local coordinate of point
		Point local;
		if(view.isSame(LOCAL_VIEW) || view.isSame(Player.NONE)){
			local = point;
		}
		else{
			local = BOARD_SIZE.substract(new Point(1, 1)).substract(point);
		}
		//Locally finds Tile
		int x = local.getX();
		int y = local.getY();
		if(y>=0 && x>=0 && y<board.length && x<board.length){
			return board[y][x];
		}
		else{
			return OUT_OF_BOUNDS;
		}
	}
	
	/** Returns the range of the unit at the given point in the given direction (always 0 for empty tiles) */
	public int getRange(Point point, Direction dir){
		Tile tile = getTile(point);
		if(tile.isEmpty()){
			return 0;
		}
		else{
			return tile.getUnit().getMirroredRange(dir, !tile.getPlayer().isSame(view));
		}
	}
	
	/** Sets the view of this board to the given Player (Turns the board around) */
	public GameBoard setView(Player player){
		this.view = player;
		return this;
	}
	
	/** Sets the view of this board to the opponent of the currently viewing player (no effect for Player NONE) */
	public GameBoard flipView(){
		setView(view.getOpponent());
		return this;
	}
	
	/** Sets the view of this board to the active player */
	public GameBoard setViewToActivePlayer(){
		setView(this.activePlayer);
		return this;
	}
	
	/** Returns NONE Player if game is still in progress and the winning Player if the game has been won */
	public Player checkWin(){
		final int[] lastLines = new int[]{0, BOARD_SIZE.getY()-1};
		final Player[] lineHolders = new Player[]{view.getOpponent(), view};
		for(int c=0; c<lastLines.length; c++){
			for(int cx=0; cx<BOARD_SIZE.getX(); cx++){
				if(getTile(new Point(cx, lastLines[c])).getPlayer().isOpponent(lineHolders[c])){
					return getTile(new Point(cx, lastLines[c])).getPlayer().getOpponent();
				}
			}
		}
		return Player.NONE;
	}
	
	/** Returns deep Clone of this GameBoard */
	public GameBoard clone(){
		Tile[][] newBoard = new Tile[board.length][board[0].length];
		for(int cy=0; cy<board.length; cy++){
			for(int cx=0; cx<board[0].length; cx++){
				newBoard[cy][cx] = board[cy][cx];
			}
		}
		return new GameBoard(view, newBoard, activePlayer);
	}
	
	/** Returns the Hash value of this map */
	public long hash(){
		//TODO: Implement Hashing!
		return -1;
	}
	
	/** Returns String representing this Game Board */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{View: ").append(view).append(", Active Player: ").append(activePlayer).append("}\n");
		for(int cy=0; cy<board.length; cy++){
			for(int cx=0; cx<board[0].length; cx++){
				buffer.append(getTile(new Point(cx, cy)));
			}
			if(cy<board.length-1){
				buffer.append("\n");
			}
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
		@Getter private boolean isEmpty;
		/** Whether this tile is in bounds (used for util purposes) */
		@Getter private boolean inBounds;
		
		
		/** Constructs new Tile with given Unit and Player */
		public Tile(Player player, Unit unit){
			this.player = player;
			this.unit = unit;
			this.isEmpty = false;
			this.inBounds = true;
		}
		
		/** Constructs new empty Tile */
		public Tile(){
			this.player = Player.NONE;
			this.unit = Unit.NONE;
			this.isEmpty = true;
			this.inBounds = true;
		}
		
		/** Constructs new ILLEGAL out-of-bounds Tile. This should never be on the GameBoard! */
		public Tile(boolean outOfBounds){
			this();
			this.inBounds = false;
		}
		
		
		/** Returns value representing this unit for the purposes of hashing */
		public int hashValue(){
			//TODO: Implement this
			return -1;
		}
		
		/** Returns String representing this Unit */
		public String toString(){
			if(!inBounds){
				//NOTE: This should never be seen!
				return "[ x X x ]";
			}
			else if(isEmpty){
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
	/** The Dimensions of the Board */
	private static final Point BOARD_SIZE= new Point(6, 9);
	
	/** Representation of any Tile which is out of bounds */
	private static final Tile OUT_OF_BOUNDS = new Tile(false);
	
	/** The Player according to whom the local view (i.e. the actual array) is oriented (The opponent occupies row 0 and 1) */
	private static final Player LOCAL_VIEW = Player.RED;
	
	
}
