package borderlineBot.bot.bots;

import lombok.AllArgsConstructor;
import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
import borderlineBot.util.hashing.HashManager;
import borderlineBot.util.hashing.Hasher;
import borderlineBot.util.hashing.Hasher.Hash;

@AllArgsConstructor
/** Very Simple implementation of a Tree Search Algorithm */
public class BasicTreeSearchBot implements Bot{
	
	/** The Evaluation Function used by this Bot to determine Game Play */
	private EvaluationFunction evaluate;
	/** Depth of the Tree Search */
	private int depth;
	
	/** AI Processing */
	public Move move(GameBoard board, Player player) {
		Move bestMove = null;
		float bestScore = -999999999;
		for(Move move : board.generateAllLegalMoves()){
			float temp = recTreeSearch(board.move(move), player, depth);
			if(temp>bestScore){
				bestScore = temp;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	/** Recursive Tree Search */
	private float recTreeSearch(GameBoard board, Player player, int depth){
		if(depth==0 || board.getWinner().isLegalPlayer()){
			return evaluate.evaluate(board, player);
		}
		else{
			boolean max = board.getActivePlayer().isSame(player);
			float bestScore = max?-999999999:999999999;
			for(Move move : board.generateAllLegalMoves()){
				float temp = 0;
				//Hash Splice
				GameBoard nextBoard  = board.move(move);
				Hash nextBoardHash = Hasher.hashBoard(nextBoard);
				if(HashManager.has(nextBoardHash)){
					temp = HashManager.get(nextBoardHash).getScore();
				}
				else{
					temp = recTreeSearch(nextBoard, player, depth-1);
				}
				//Evaluation
				if(max && temp>bestScore){
					bestScore = temp;
				}
				else if(!max && temp<bestScore){
					bestScore = temp;
				}
			}
			return bestScore;
		}
	}
	
}
