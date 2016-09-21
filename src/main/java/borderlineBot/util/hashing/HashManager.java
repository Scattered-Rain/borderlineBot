package borderlineBot.util.hashing;

import java.util.HashMap;

import lombok.Data;
import lombok.Getter;
import borderlineBot.util.hashing.Hasher.Hash;

/** Manages Hashed Maps */
public class HashManager{
	
	/** Stores information of hashed maps */
	private HashMap<Integer, HashMemory> memory;
	
	
	/** Constructs new HashManager */
	public HashManager(){
		this.memory = new HashMap<Integer, HashMemory>();
	}
	
	
	/** Returns HashMemory of given hash value */
	public HashMemory get(int hash){
		return memory.get(hash);
	}
	
	
	//--classes--
	/** Container storing information about a GameBoard, used for storing in the HashMap */
	@Data public static class HashMemory{
		
		/** Actual Hash Value of this Map */
		private Hash hash;
		
		/** The score of the given map */
		private float score;
		
	}
	
}
