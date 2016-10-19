package borderlineBot.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;
import borderlineBot.game.GameBoard.Tile;

public class ThreatMap {
	
	/** The ttl Grid */
	@Getter private ThreatTileList[][] ttlGrid;
	
	
	/** Constructs new Threat Map */
	public ThreatMap(GameBoard board){
		ThreatTileList[][] ttlGrid = new ThreatTileList[GameBoard.BOARD_SIZE.getY()][GameBoard.BOARD_SIZE.getX()];
		for(int cy=0; cy<ttlGrid.length; cy++){
			for(int cx=0; cx<ttlGrid[cy].length; cx++){
				ttlGrid[cy][cx] = new ThreatTileList();
				Point loc = new Point(cx, cy);
				if(!board.getTile(loc).isEmpty()){
					ttlGrid[cy][cx].add(new ThreatTile(0, loc, true, loc, loc));
				}
			}
		}
		this.ttlGrid = ttlGrid;
	}
	
	/** Updates TTL to given Ply */
	public void update(GameBoard board, int ply){
		ThreatTileList.updateThreats(board, this.ttlGrid, ply);
	}
	
	/** Prints the Threat Map with given parameters */
	public void print(GameBoard board, Player player, int ply){
		for(int cy=0; cy<ttlGrid.length; cy++){
			for(int cx=0; cx<ttlGrid[cy].length; cx++){
				if(ttlGrid[cy][cx].containsThreat(board, player, ply)){
					ThreatTile des = null;
					for(ThreatTile tt : ttlGrid[cy][cx].getList()){
						if(board.getTile(tt.getOwner()).getPlayer().isSame(player)){
							if(des==null){
								des = tt;
							}
							if(tt.getPly()<des.getPly()){
								des = tt;
							}
						}
					}
					System.out.print(board.getTile(des.getOwner()).getUnit()+" "+des.getPly()+"\t\t");
				}
				else{
					System.out.print("false\t\t");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//--classes--
	/** Threat Tile */
	public static class ThreatTile{
		
		/** Needed ply to make this threat tile */
		@Getter private int ply;
		
		/** The point on the current board producing the threat */
		@Getter private Point owner;
		
		/** Whether the owner would be standing on this threat tile */
		@Getter private boolean containsOwner;
		
		/** Where the unit emitting this threat lands on the board creating this threat */
		@Getter Point jumpTo;
		
		/** The origin point of this Threat */
		@Getter Point directThreatOrigin;
		
		/** Constructs new */
		public ThreatTile(int ply, Point owner, boolean containsOwner, Point jumpTo, Point directThreatOrigin){
			this.ply = ply;
			this.owner = owner;
			this.containsOwner = containsOwner;
			this.jumpTo = jumpTo;
			this.directThreatOrigin = directThreatOrigin;
		}
		
		/** Returns the Tile of the owner */
		public Tile getOwnerTile(GameBoard board){
			return board.getTile(owner);
		}
		
	}
	
	/** Collection of Threat Tiles */
	public static class ThreatTileList{
		
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
							Direction[] dirList = Direction.values();
							for(int c=0; c<dirList.length; c++){
								Point ddir = dirList[c].getDir();
								int range = board.getTile(tt.getOwner()).getRange(dirList[c], board);
								Point target = new Point(cx, cy).add(dirList[c].getDir().scale(range));
								if(!(target.getX()<0 || target.getX()>=GameBoard.BOARD_SIZE.getX() || target.getY()<0 || target.getY()>=GameBoard.BOARD_SIZE.getY())){
									for(int c2=1; c2<=range; c2++){
										ThreatTile newThreat = new ThreatTile(plyTo, tt.getOwner(), c2==range, target, new Point(cx, cy));
										ttlGrid[cy+ddir.scale(c2).getY()][cx+ddir.scale(c2).getX()].add(newThreat);
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
