package borderlineBot.util.hashing;

import lombok.Getter;

/** Node representing a game board stored in a Transposition Table */
public class TranspositionNode{
	
	/** The Score of this Node */
	@Getter private float score;
	/** The depth at which this Node was created (lower numbers imply deeper Nodes, given same algorithm) */
	@Getter private int depth;
	
	/** Constructs new Transposition Node */
	public TranspositionNode(float score, int depth){
		this.score = score;
		this.depth = depth;
	}
	
}
