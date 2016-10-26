package borderlineBot.util.transpositionTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.util.hashing.Hasher.Hash;

/** Object for Transposition Table usage */
public class TranspositionTable {
	
	/** Object used for storage and look up of Transpositions */
	private HashMap<Hash, TranspositionNode> map;
	
	
	/** Constructor */
	public TranspositionTable(){
		this.map = new HashMap<Hash, TranspositionNode>();
	}
	
	
	/** Returns whether this Transposition Table contains the given Hash */
	public boolean contains(Hash hash){
		return map.containsKey(hash);
	}
	
	/** Returns the Transposition Node corresponding to the given Hash */
	public TranspositionNode get(Hash hash){
		return map.get(hash);
	}
	
	/** Adds given Hash to the Hash Table */
	public void put(Hash hash, TranspositionNode node){
		if(map.containsKey(hash)){
			//System.out.println(hash.toString()+" "+map.get(hash).getHash().toString()+" "+map.size());
			this.replace(hash, node);
		}
		else{
			map.put(hash, node);
		}
	}
	
	/** Replaces the node reachable with the given Hash with the given Node */
	public void replace(Hash hash, TranspositionNode node){
		map.replace(hash, node);
	}
	
	/** Clears the table */
	public void reset(){
		map.clear();
	}
	
	
	//--classes--
	/** Node Representing a Transposition State */
	public static class TranspositionNode{
		
		/** The hash corresponding to this Node */
		@Getter private Hash hash;
		
		/** The score of the Node */
		@Getter private int score;
		
		/** The kind of score saved in this transposition node */
		@Getter private int scoreType;
		
		/** The depth of the node */
		@Getter private int depth;
		
		/** The best Move that was determined by in the node when creating this Hash (may be null) */
		@Getter private Move bestMove;
		
		/**  String that can be used for debug purposes */
		@Setter @Getter private String debugText;
		
		@Getter private List<Move> bestMoveOrdered = new ArrayList<Move>();
		
		
		/** Constructs new Transposition Node */
		public TranspositionNode(Hash hash, int depth, Move bestMove, int score, int alpha, int beta){
			this.hash = hash;
			this.score = score;
			this.depth = depth;
			this.bestMove = bestMove;
			if(alpha<score && score<beta){
				this.scoreType = EXACT_SCORE;
			}
			else if(score<=alpha){
				this.scoreType = UPPER_BOUND;
			}
			else if(beta<=score){
				this.scoreType = LOWER_BOUND;
			}
		}
		
		
		/** Returns whether the depth of this Transposition node is deeper than the given depth */
		public boolean appropriate(int depth){
			return this.depth>=depth;
		}
		
		
		//--statics--
		/** Indicates that the Score refers to a lower bound */
		public static final int LOWER_BOUND = 1;
		/** Indicates that the Score refers to an upper bound */
		public static final int UPPER_BOUND = 2;
		/** Indicates that the Score refers to the exact score */
		public static final int EXACT_SCORE = 3;
		
	}
	
}
