package borderlineBot.util.hashing;

import java.util.HashMap;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import borderlineBot.Launcher;
import borderlineBot.util.hashing.Hasher.Hash;

/** Static class Managing Hashed Maps and their Board Info */
public class HashManager{
	
	/** Stores information of hashed maps */
	private static HashMap<Hash, BoardInfo> memory = new HashMap<Hash, BoardInfo>();
	
	
	/** Returns HashMemory of given hash value */
	public static BoardInfo get(Hash hash){
		return memory.get(hash);
	}
	
	/** Returns whether the given Hash exists in the Memory */
	public static boolean has(Hash hash){
		return memory.containsKey(hash);
	}
	
	/** Puts the given board info into the Hash Map with the given Hash as key */
	public static void put(Hash hash, BoardInfo boardInfo){
		boardInfo.hash = hash;
		if(!Launcher.COMPETITIVE){
			if(has(hash)){
				BoardInfo bInfo = get(hash);
				//Check for error with hashCode value
				if(!bInfo.hash.equals(hash)){
					System.out.println("Equal HashCode for different Boards!");
					System.exit(0);
				}
				//Counter for ubiquitusness of Board constallation
				else{
					boardInfo.seen = bInfo.seen+1;
				}
			}
			else{
				boardInfo.seen = 1;
			}
		}
		memory.put(hash, boardInfo);
	}
	
	
	//--classes--
	/** Container storing information about a GameBoard, used for storing in the HashMap */
	public static class BoardInfo{
		
		/** Actual Hash Value of this Map (Auto added when put into HashManager) */
		private Hash hash;
		
		/** How often this board has been seen (Auto added when put into HashManager when not Competitive) */
		private int seen;
		
		/** The score of the given map */
		@Getter @Setter private float score;
		
	}
	
}
