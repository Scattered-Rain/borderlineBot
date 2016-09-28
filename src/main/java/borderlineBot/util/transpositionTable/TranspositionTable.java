package borderlineBot.util.transpositionTable;

import java.util.HashMap;

import lombok.Getter;
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
		map.put(hash, node);
	}
	
	/** Replaces the node reachable with the given Hash with the given Node */
	public void replace(Hash hash, TranspositionNode node){
		map.replace(hash, node);
	}
	
	
	//--classes--
	/** Node Representing a Transposition State */
	public static class TranspositionNode{
		
		/** The hash corresponding to this Node */
		@Getter private Hash hash;
		
		/** The score of the Node */
		@Getter private int score;
		
		/** The depth of the node (in positive numbers, i.e. 2 is deeper in the tree than 1) */
		@Getter private int depth;
		
		/** The amount of times this node has been visited during the search */
		@Getter private int visited;
		
	}
	
}
