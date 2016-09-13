package borderlineBot.game;

import java.util.ArrayList;
import java.util.List;

import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import lombok.Getter;

/** Object which contains all information about the current game state */
public class GameBoard{
	
	/** The Player for which the board is oriented */
	@Getter private Player view;
	/** The board as represented with Tiles (Should only be accessed over getTile() method) */
	private Tile[][] board;
	/** If this Game Board has a Winner, else Player NONE */
	@Getter private Player winner;
	/** The turn of this state */
	@Getter private int turn;
	
	/** The Player that is currently allowed to make a move */
	@Getter private Player activePlayer;
	
	
	/** Constructs new GameBoard */
	public GameBoard(){
		init();
		this.view = LOCAL_VIEW;
		this.activePlayer = LOCAL_VIEW;
		this.winner = Player.NONE;
		this.turn = 0;
	}
	
	/** Constructs new Board for CLONING purposes */
	private GameBoard(Player view, Tile[][] board, Player moving, int turn, Player winner){
		this.view = view;
		this.board = board;
		this.activePlayer = moving;
		this.winner = winner;
		this.turn = turn;
	}
	
	/** Constructs new Board for MOVING purposes (recalculates Winning) */
	private GameBoard(Player view, Tile[][] board, Player moving, int turn){
		this.view = view;
		this.board = board;
		this.activePlayer = moving;
		this.winner = checkWin();
		this.turn = turn+1;
	}
	
	
	/** Builds fresh board (As seen by the Red Player) */
	private void init(){
		//Where the player rows are
		final Tile empty = new Tile();
		final Tile blueBack = new Tile(LOCAL_VIEW.getOpponent(), Unit.TWO);
		final Tile blueFront = new Tile(LOCAL_VIEW.getOpponent(), Unit.ONE);
		final Tile redBack = new Tile(LOCAL_VIEW, Unit.TWO);
		final Tile redFront = new Tile(LOCAL_VIEW, Unit.ONE);
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
		if(y>=0 && x>=0 && y<BOARD_SIZE.getY() && x<BOARD_SIZE.getX()){
			return board[y][x];
		}
		else{
			return OUT_OF_BOUNDS;
		}
	}
	
	/** Returns the index of the row in which the borderline of the given Player is in relation to the view (-1 for NONE Player) */
	public int getBorderline(Player player){
		if(!player.isLegalPlayer()){
			return -1;
		}
		else{
			if(this.view.isSame(player)){
				return BOARD_SIZE.getY()-1;
			}
			else{
				return 0;
			}
		}
	}
	
	/** Returns the general Direction of where the given player has to move to win the game relative to the view (null for NONE Player) */
	public Direction getMovingDirection(Player player){
		if(!player.isLegalPlayer()){
			return null;
		}
		else{
			if(this.view.isSame(player)){
				return Direction.UP;
			}
			else{
				return Direction.DOWN;
			}
		}
	}
	
	/** Constructs new Move using the given parameters */
	public Move createMove(Player player, Point unit, Direction moveDir){
		return new Move(player, unit, moveDir, this);
	}
	
	/** Returns the range of the unit at the given point in the given direction (always 0 for empty tiles) */
	public int getRange(Point point, Direction dir){
		return getTile(point).getRange(dir, this);
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
	
	/** Returns GameBoard which is equivalent to this with the given Move made, if Move illegal returns null */
	public GameBoard move(Move move){
		if(move.checkLegal(this)){
			Player prevView = view;
			this.view = LOCAL_VIEW;
			Tile[][] newBoard = new Tile[board.length][board[0].length];
			for(int cy=0; cy<newBoard.length; cy++){
				for(int cx=0; cx<newBoard[0].length; cx++){
					newBoard[cy][cx] = board[cy][cx];
				}
			}
			Tile empty = new Tile();
			newBoard[move.getUnit(this).getY()][move.getUnit(this).getX()] = empty;
			for(int c=1; c<move.getRange(this); c++){
				Point jumpPoint = move.getUnit(this).add(move.getMoveDir(this).getDir().scale(c));
				Tile jumpTile = newBoard[jumpPoint.getY()][jumpPoint.getX()];
				if(jumpTile.getPlayer().isOpponent(move.getPlayer())){
					newBoard[jumpPoint.getY()][jumpPoint.getX()] = empty;
				}
			}
			newBoard[move.getTarget(this).getY()][move.getTarget(this).getX()] = move.getUnitTile(this);
			GameBoard newGameBoard = new GameBoard(prevView, newBoard, this.activePlayer.getOpponent(), this.turn);
			this.view = prevView;
			return newGameBoard;
		}
		//This should never, ever happen:
		return null;
	}
	
	/** Returns NONE Player if game is still in progress and the winning Player if the game has been won */
	private Player checkWin(){
		final int[] lastLines = new int[]{0, BOARD_SIZE.getY()-1};
		Player base = view;
		if(base.isSame(Player.NONE)){
			base = LOCAL_VIEW;
		}
		//Check borderline
		final Player[] lineHolders = new Player[]{base.getOpponent(), base};
		for(int c=0; c<lastLines.length; c++){
			for(int cx=0; cx<BOARD_SIZE.getX(); cx++){
				Tile tile = getTile(new Point(cx, lastLines[c]));
				if(!tile.isEmpty){
					if(tile.getPlayer().isOpponent(lineHolders[c])){
						return getTile(new Point(cx, lastLines[c])).getPlayer();
					}
				}
			}
		}
		//Check complete opponent wipe out
		int[] unitsOfPlayers = new int[]{0, 0};
		for(int cy=0; cy<BOARD_SIZE.getY(); cy++){
			for(int cx=0; cx<BOARD_SIZE.getX(); cx++){
				Tile tile = getTile(new Point(cx, cy));
				for(int c=0; c<lineHolders.length; c++){
					if(tile.getPlayer().isSame(lineHolders[c])){
						unitsOfPlayers[c]++;
					}
				}
			}
		}
		for(int c=0; c<lineHolders.length; c++){
			if(unitsOfPlayers[c]==0){
				return lineHolders[c].getOpponent();
			}
		}
		//No victories
		return Player.NONE;
	}
	
	/** Returns an exhaustive list containing all Moves that can legally be made on this Board */
	public List<Move> generateAllLegalMoves(){
		List<Move> allMoves = new ArrayList<Move>();
		for(int cy=0; cy<BOARD_SIZE.getY(); cy++){
			for(int cx=0; cx<BOARD_SIZE.getX(); cx++){
				Point point = new Point(cx, cy);
				Tile tile = this.getTile(point);
				if(!tile.isEmpty() && tile.getPlayer().isSame(this.getActivePlayer())){
					for(int c=0; c<Direction.values().length; c++){
						allMoves.add(this.createMove(this.getActivePlayer(), point, Direction.values()[c]));
					}
				}
			}
		}
		List<Move> allLegalMoves = new ArrayList<Move>();
		for(int c=0; c<allMoves.size(); c++){
			if(allMoves.get(c).checkLegal(this)){
				allLegalMoves.add(allMoves.get(c));
			}
		}
		return allLegalMoves;
	}
	
	
	/** Returns deep Clone of this GameBoard */
	public GameBoard clone(){
		Tile[][] newBoard = new Tile[board.length][board[0].length];
		for(int cy=0; cy<board.length; cy++){
			for(int cx=0; cx<board[0].length; cx++){
				newBoard[cy][cx] = board[cy][cx];
			}
		}
		return new GameBoard(view, newBoard, activePlayer, turn, winner);
	}
	
	/** Returns the Hash value of this map */
	public long hash(){
		//TODO: Implement Hashing!
		return -1;
	}
	
	/** Returns String representing this Game Board */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{View: ").append(view).append(", Active Player: ").append(activePlayer).append(", Turn: ").append(turn).append("}\n");
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
		
		
		/** Returns the range of the unit at this Tile in the given global direction (0 for all empty tiles) */
		public int getRange(Direction dir, GameBoard board){
			if(isEmpty()){
				return 0;
			}
			else{
				return getUnit().getMirroredRange(dir, !getPlayer().isSame(board.getView()));
			}
		}
		
		/** Returns value representing this unit for the purposes of hashing */
		public int hashValue(GameBoard board){
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
	
	
	/** This Object represents a single move on the GameBoard */
	public static class Move{
		
		/** Player enacting this move */
		@Getter private Player player;
		/** The location of the unit that is to be moved (based on local view) */
		private Point unit;
		/** The direction in which the unit is to move (based on local view) */
		private Direction moveDir;
		
		
		/** Constructs new Move (based on Local View) */
		private Move(Player player, Point unit, Direction moveDir, GameBoard board){
			this.player = player;
			if(board.getView().isSame(LOCAL_VIEW) || board.getView().isSame(Player.NONE)){
				this.unit = unit;
				this.moveDir = moveDir;
			}
			else{
				this.unit = BOARD_SIZE.substract(new Point(1, 1)).substract(unit);
				this.moveDir = moveDir.turnBack();
			}
		}
		
		
		/** Returns the point where the unit that is to be moved is located at based on the given view */
		public Point getUnit(GameBoard board){
			Player view = board.getView();
			if(view.isSame(LOCAL_VIEW) || view.isSame(Player.NONE)){
				return unit;
			}
			else{
				return BOARD_SIZE.substract(new Point(1, 1)).substract(unit);
			}
		}
		
		/** Returns the Range this Move actually has */
		public int getRange(GameBoard board){
			return getUnitTile(board).getRange(moveDir, board);
		}
		
		/** Returns the Tile of the Unit */
		public Tile getUnitTile(GameBoard board){
			return board.getTile(getUnit(board));
		}
		
		/** Returns The Point onto which the Unit should move */
		public Point getTarget(GameBoard board){
			return getUnit(board).add((getMoveDir(board).getDir().scale(getRange(board))));
		}
		
		/** Returns the Tile onto which the Unit should move to */
		public Tile getTargetTile(GameBoard board){
			return board.getTile(getTarget(board));
		}
		
		/** Returns the direction the unit is supposed to move in based on the given view */
		public Direction getMoveDir(GameBoard board){
			Player view = board.getView();
			if(view.isSame(LOCAL_VIEW) || view.isSame(Player.NONE)){
				return moveDir;
			}
			else{
				return moveDir.turnBack();
			}
		}
		
		/** Returns whether this Move is legal on the given board */
		public boolean checkLegal(GameBoard board){
			if(board.getWinner().isLegalPlayer()){
				return false;
			}
			if(!board.getActivePlayer().isSame(player)){
				return false;
			}
			Tile unitTile = board.getTile(getUnit(board));
			if(unitTile.isEmpty || !unitTile.getPlayer().isSame(player)){
				return false;
			}
			Tile target = getTargetTile(board);
			if(!target.inBounds || target.getPlayer()==player){
				return false;
			}
			return true;
		}
		
	}
	
	
	//--statics--
	/** The Dimensions of the Board */
	public static final Point BOARD_SIZE= new Point(6, 9);
	
	/** Representation of any Tile which is out of bounds */
	private static final Tile OUT_OF_BOUNDS = new Tile(false);
	
	/** The Player according to whom the local view (i.e. the actual array) is oriented (The opponent occupies row 0 and 1) */
	private static final Player LOCAL_VIEW = Player.RED;
	
	
}
