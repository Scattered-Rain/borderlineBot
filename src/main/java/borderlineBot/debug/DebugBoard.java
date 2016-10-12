package borderlineBot.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import borderlineBot.bot.Bot;
import borderlineBot.bot.bots.AlphaBetaTranspositionTableNegaMaxBot;
import borderlineBot.bot.bots.BasicAlphaBetaNegaMaxBot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;
import borderlineBot.game.Unit;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.util.Constants;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import borderlineBot.util.RNG;
import borderlineBot.util.hashing.Hasher;
import borderlineBot.util.hashing.Hasher.Hash;

/** Debug Game Board running a way simpler game. Needs its own Evaluation Function. */
public class DebugBoard extends GameBoard{
	
	/** The Active Player */
	private Player active;
	/** The Debug Game Grid */
	private Player[][] grid;
	
	/** Initializes new Board */
	public DebugBoard(){
		super(Player.RED, hashHack(null), Player.RED, 0, Player.NON);
		this.grid = new Player[3][3];
		for(int cy=0; cy<grid.length; cy++){
			for(int cx=0; cx<grid[0].length; cx++){
				grid[cy][cx] = Player.NON;
			}
		}
		this.active = Player.RED;
	}
	
	/** Constructs new DebugBoard with given qualities */
	public DebugBoard(Player[][] grid, Player active, Tile[][] hashHack){
		super(Player.RED, hashHack, active, 0, Player.NON);
		this.grid = grid;
		this.active = active;
	}
	
	/** Returns the active Player */
	@Override public Player getActivePlayer(){
		return active;
	}
	
	/** Returns advanced version of this DebugBoard */
	@Override public DebugBoard move(Move moveIn){
		DebugMove move = (DebugMove) moveIn;
		Player[][] newGrid = new Player[3][3];
		for(int cy=0; cy<grid.length; cy++){
			for(int cx=0; cx<grid[0].length; cx++){
				newGrid[cy][cx] = grid[cy][cx];
			}
		}
		newGrid[move.getPoint().getY()][move.getPoint().getX()] = move.getPlayer();
		return new DebugBoard(newGrid, this.getActivePlayer().getOpponent(), hashHack(newGrid));
	}
	
	/** Createse Tile grid stroing tictactoe game */
	private static Tile[][] hashHack(Player[][] grid){
		Tile[][] t = new Tile[GameBoard.BOARD_SIZE.getY()][GameBoard.BOARD_SIZE.getX()];
		for(int cy=0; cy<t.length; cy++){
			for(int cx=0; cx<t[0].length; cx++){
				t[cy][cx] = new Tile();
				if(grid!=null && cy<3 && cx<3){
					if(grid[cy][cx].isLegalPlayer()){
						t[cy][cx] = new Tile(grid[cy][cx], Unit.ONE);
					}
				}
			}
		}
		return t;
	}
	
	/** Constructs a list containing all legal moves */
	@Override public List<Move> generateAllLegalMoves(){
		List<Move> out = new ArrayList<Move>();
		if(!getWinner().isSame(Player.NON)){
			return out;
		}
		for(int cy=0; cy<grid.length; cy++){
			for(int cx=0; cx<grid[0].length; cx++){
				if(grid[cy][cx].isSame(Player.NON)){
					DebugMove pot = new DebugMove(this.active, new Point(cx, cy));
					if(pot.checkLlegal(this)){
						out.add((Move)pot);
					}
				}
			}
		}
		return out;
	}
	
	/** Constructs a list containing all hypothetically legal moves */
	@Override public List<Move> generateAllHypotheticalLegalMoves(Player player){
		List<Move> out = new ArrayList<Move>();
		if(!getWinner().isSame(Player.NON)){
			return out;
		}
		for(int cy=0; cy<grid.length; cy++){
			for(int cx=0; cx<grid[0].length; cx++){
				if(grid[cy][cx].isSame(Player.NON)){
					DebugMove pot = new DebugMove(player, new Point(cx, cy));
					if(pot.checkLlegal(this)){
						out.add((Move)pot);
					}
				}
			}
		}
		return out;
	}
	
	/** Constructs String Representation of state of DebugGame */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Active: "+active+", Winner: "+getWinner()+"\n");
		for(int cy=0; cy<grid.length; cy++){
			for(int cx=0; cx<grid[0].length; cx++){
				buffer.append(grid[cy][cx]+" ");
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	/** Calculates for Winner */
	@Override public Player getWinner(){
		Point[] dirs = new Point[]{Direction.RIGHT.getDir(), Direction.DOWN.getDir(), new Point(1, 1), new Point(-1, -1)};
		for(int cy=0; cy<grid.length; cy++){
			for(int cx=0; cx<grid[0].length; cx++){
				for(int d=0; d<dirs.length; d++){
					final Player[] plays = new Player[]{Player.RED, Player.BLU};
					for(int p=0; p<plays.length; p++){
						try{
							int counter = 0;
							for(int c=0; c<3; c++){
								if(grid[cy+dirs[d].scale(c).getY()][cx+dirs[d].scale(c).getX()].isSame(plays[p])){
									counter++;
								}
							}
							if(counter==3){
								return plays[p];
							}
						}catch(Exception ex){}
					}
				}
			}
		}
		return Player.NON;
	}
	
	/** Returns whether a draw has happened */
	public boolean debugDraw(){
		for(int cy=0; cy<grid.length; cy++){
			for(int cx=0; cx<grid[0].length; cx++){
				if(!grid[cy][cx].isLegalPlayer()){
					return false;
				}
			}
		}
		return true;
	}
	
	
	//--classes--
	/** Debug Move */
	public static class DebugMove extends Move{
		@Getter Player player;
		@Getter Point point;
		
		/** Constructs DebugMove */
		public DebugMove(Player player, Point point){
			super(player, point, Direction.UP, new DebugBoard());
			this.player = player;
			this.point = point;
		}
		
		/** Returns whether this Move is Legal */
		public boolean checkLlegal(DebugBoard board){
			if(board.grid[point.getY()][point.getX()].isSame(Player.NON)){
				return true;
			}
			return false;
		}
		
	}
	
	/** Debug Move Orderer */
	public static class DebugOrderer implements MoveOrderer{
		/** Ordered List of DebugMoves from DebugBoard */
		public List<Move> orderMoves(GameBoard board, Player player){
			List<Move> moves = ((DebugBoard)board).generateAllHypotheticalLegalMoves(player);
			Collections.shuffle(moves);
			return moves;
		}
	}
	
	/** Debug Evaluation Function */
	public static class DebugEvaluator implements EvaluationFunction{
		public int evaluate(GameBoard board, Player player) {
			if(board.getWinner().isLegalPlayer()){
				return board.getWinner().isSame(player)?Constants.WIN_SCORE:Constants.LOSE_SCORE;
			}
			return RNG.nextInt(10);
		}
	}
	
	//--statics--
	/** Main to use the debug Board for debugging */
	public static void main(String[] args){
		while(true){
			DebugBoard board = new DebugBoard();
			MoveOrderer orderer = new DebugOrderer();
			EvaluationFunction eval = new DebugEvaluator();
			//Bot b = new BasicAlphaBetaNegaMaxBot(orderer, eval, 12);
			Bot b = new AlphaBetaTranspositionTableNegaMaxBot(orderer, eval, 12);
			Bot[] bots = new Bot[]{b, b};
			int counter = 0;
			System.out.println("New Game:");
			while(!board.getWinner().isLegalPlayer() && !board.debugDraw()){
				//System.out.println(board);
				//System.out.println();
				Move m = bots[counter%2].move(board, Player.getIndexedPlayerList()[counter%2]);
				board = board.move(m);
				counter++;
				Hasher.debugViewHash(board.hash());
			}
			System.out.println(board);
			if(board.getWinner().isLegalPlayer()){
				System.out.println("TicTacToe was WON by one of the Players:");
				System.out.println(board.getWinner());
				while(true){
					try{
						Thread.sleep(1000);
					}catch(Exception ex){}
				}
				//System.exit(0);
			}
		}
	}
	
}
