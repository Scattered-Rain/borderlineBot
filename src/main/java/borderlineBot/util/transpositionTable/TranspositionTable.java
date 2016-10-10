package borderlineBot.util.transpositionTable;

import java.util.HashMap;
import java.util.Hashtable;

import lombok.Getter;
import lombok.Setter;
import borderlineBot.util.hashing.Hasher.Hash;

/** Object for Transposition Table usage */
public class TranspositionTable {
	
	/** Object used for storage and look up of Transpositions */
	private Hashtable<Hash, TranspositionNode> map;
	
	
	/** Constructor */
	public TranspositionTable(){
		this.map = new Hashtable<Hash, TranspositionNode>();
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
		if(map.contains(hash)){
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
		@Getter private int flag;
		
		/** The depth of the node (in positive numbers, i.e. 2 is deeper in the tree than 1) */
		@Getter private int depth;
		
		/**  String that can be used for debug purposes */
		@Setter @Getter private String debugText;
		
		
		/** Constructs new Transposition Node */
		public TranspositionNode(Hash hash, int score, int totalDepth, int depth){
			this.hash = hash;
			this.score = score;
			this.depth = totalDepth-depth;
		}
		
		
		/** Returns whether the depth of this Transposition node is deeper than the given depth */
		public boolean isLessDeepOrEqual(int totalDepth, int depth){
			int cDepth = totalDepth-depth;
			return this.depth<=cDepth;
		}
		
	}
	
}
