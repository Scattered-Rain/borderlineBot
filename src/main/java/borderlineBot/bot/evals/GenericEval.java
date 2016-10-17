package borderlineBot.bot.evals;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.game.Player;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;

/** Generic Evaluation Function */
public class GenericEval implements EvaluationFunction{
	
	/** Evaluation */
	public int evaluate(GameBoard board, Player player) {
		int score = 0;
		
		int[][] units = board.countUnits(player);
		float scoreOneUnits = (units[0][0]-units[1][0]) / (units[0][0]+units[1][0]);
		float scoreTwoUnits = (units[0][1]-units[1][1]) / (units[0][1]+units[1][1]);
		
		List<Move> playerMoves = board.generateAllHypotheticalLegalMoves(player);
		List<Move> oppMoves = board.generateAllHypotheticalLegalMoves(player.getOpponent());
		float scoreMoveOptions = (playerMoves.size()-oppMoves.size()) / (playerMoves.size()+oppMoves.size());
		
		ThreatTileList[][] ttlGrid = new ThreatTileList[GameBoard.BOARD_SIZE.getY()][GameBoard.BOARD_SIZE.getX()];
		for(int cy=0; cy<ttlGrid.length; cy++){
			for(int cx=0; cx<ttlGrid[cy].length; cx++){
				ttlGrid[cy][cx] = new ThreatTileList();
				Point loc = new Point(cx, cy);
				if(!board.getTile(loc).isEmpty()){
					ttlGrid[cy][cx].add(new ThreatTile(0, loc, true, loc));
				}
			}
		}
		ThreatTileList.updateThreats(board, ttlGrid, 1);
		
		
		return score;
	}
	
	
	//--classes--
	/** Threat Tile */
	private static class ThreatTile{
		
		/** Needed ply to make this threat tile */
		@Getter private int ply;
		
		/** The point on the current board producing the threat */
		@Getter private Point owner;
		
		/** Whether the owner would be standing on this threat tile */
		@Getter private boolean containsOwner;
		
		/** Where the unit emitting this threat lands on the board creating this threat */
		@Getter Point jumpTo;
		
		/** Constructs new */
		public ThreatTile(int ply, Point owner, boolean containsOwner, Point jumpTo){
			this.ply = ply;
			this.owner = owner;
			this.containsOwner = containsOwner;
			this.jumpTo = jumpTo;
		}
		
		/** Returns the Tile of the owner */
		public Tile getOwnerTile(GameBoard board){
			return board.getTile(owner);
		}
		
	}
	
	/** Collection of Threat Tiles */
	private static class ThreatTileList{
		
		/** The actual list */
		@Getter private List<ThreatTile> list;
		
		/** Constructs new */
		public ThreatTileList(){
			this.list = new ArrayList<ThreatTile>();
		}
		
		/** Add new Threat */
		public void add(ThreatTile threat){
			this.list.add(threat);
		}
		
		/** Returns whether the threatener is on this Threat List (That is any threat), with at most the given maxPly*/
		public boolean containsThreat(GameBoard board, Player threatener, int maxPly){
			for(ThreatTile threat : list){
				if(threat.getOwnerTile(board).getPlayer().isSame(threatener) && threat.getPly()<=maxPly){
					return true;
				}
			}
			return false;
		}
		
		/** Updates the given 2D array of Threat Lists to expand TO the given ply, assuming the previous ply is on the grid */
		public static void updateThreats(GameBoard board, ThreatTileList[][] ttlGrid, int plyTo){
			for(int cy=0; cy<ttlGrid.length; cy++){
				for(int cx=0; cx<ttlGrid[cy].length; cx++){
					for(ThreatTile tt : ttlGrid[cy][cx].getList()){
						if(tt.isContainsOwner() && tt.getPly()==plyTo-1){
							int ply = plyTo;
							Direction[] dirList = Direction.values();
							for(int c=0; c<dirList.length; c++){
								Point ddir = dirList[c].getDir();
								int range = board.getTile(tt.getOwner()).getRange(dirList[c], board);
								Point target = new Point(cx, cy).add(dirList[c].getDir().scale(range));
								if(!(target.getX()<0 || target.getX()>=GameBoard.BOARD_SIZE.getX() || target.getY()<0 || target.getY()>=GameBoard.BOARD_SIZE.getY())){
									for(int c2=1; c2<=range; c2++){
										ThreatTile newThreat = new ThreatTile(ply+1, tt.getOwner(), c2==range, target);
										ttlGrid[cy+ddir.scale(range).getY()][cx+ddir.scale(range).getX()].add(newThreat);
									}
								}
							}
						}
					}
				}
			}
		}
		
	}
	
}
