package borderlineBot.bot.bots;

import java.util.HashMap;
import java.util.List;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.ClearWinLossEval;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
import borderlineBot.util.hashing.Hasher;
import borderlineBot.util.hashing.Hasher.Hash;
import borderlineBot.util.hashing.TranspositionNode;

/** Bot based on an Alpha/Beta pruned Nega Max Search */
public class BasicAlphaBetaNegaMaxBot implements Bot{
	
	/** The Move Orderer used for this Bot */
	private MoveOrderer orderer;
	/** The Evaluation Function used for this Bot */
	private EvaluationFunction eval;
	
	/** Depth to search */
	private int depth;
	
	
	/** Constructs new Bot */
	public BasicAlphaBetaNegaMaxBot(MoveOrderer orderer, EvaluationFunction eval, int depth){
		this.orderer = orderer;
		this.eval = eval;
		this.depth = depth;
	}
	
	
	/** Bot Processing */
	public Move move(GameBoard board, Player player){
		float best = Float.NEGATIVE_INFINITY;
		Move bestMove = null;
		HashMap<Hash, TranspositionNode> table = new HashMap<Hash, TranspositionNode>();
		List<Move> moves = orderer.orderMoves(board, board.getActivePlayer());
		for(Move move : moves){
			float score = alphaBeta(board.move(move), depth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, table);
			if(score>best){
				best = score;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	/** Does Alpha Beta Nega Max */
	private float alphaBeta(GameBoard board, int depth, float alpha, float beta, HashMap<Hash, TranspositionNode> table){
		Hash hash = Hasher.hashBoard(board);
		if(table.containsKey(hash)){
			TranspositionNode node = table.get(hash);
			if(node.getDepth()>=depth){
				System.out.println("Hash Break at "+depth);
				return node.getScore();
			}
		}
		if(depth==0 || board.getWinner().isLegalPlayer()){
			Player player = board.getActivePlayer().getOpponent();
			if(board.getWinner().isLegalPlayer()){
				return board.getWinner().isSame(player)?10000:-10000;
			}
			else{
				return eval.evaluate(board, player);
			}
		}
		float score = Float.NEGATIVE_INFINITY;
		for(Move move : orderer.orderMoves(board, board.getActivePlayer())){
			float value = -alphaBeta(board.move(move), depth-1, -beta, -alpha, table);
			if(value>score){
				score = value;
			}
			if(score>alpha){
				alpha = score;
			}
			if(score>=beta){
				break;
			}
		}
		table.put(hash, new TranspositionNode(score, depth));
		return score;
	}
	
}
