package borderlineBot.util.hashing;

import java.util.Collections;

import borderlineBot.game.GameBoard;

/** Interface object representing the container for hash encoding (&utility) for Game Boards */
public abstract class Hash<Type extends Hash> implements Comparable<Type>{
	
	/** Returns the GameBoard this hash is based on */
	public abstract GameBoard rebuild();
	
	/** Returns whether the given board is equal to this board */
	final public boolean equals(Type hash){
		return compareTo(hash)==0;
	}
	
	/** Compares the value of this hash with the given hash, returns -1 for given hash less than this, 0 for same, 1 for greater */
	public abstract int compareTo(Type hash);
	
}
