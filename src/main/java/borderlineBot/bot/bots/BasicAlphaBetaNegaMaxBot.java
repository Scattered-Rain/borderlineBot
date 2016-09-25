package borderlineBot.bot.bots;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.ClearWinLossEval;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;

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
	public Move move(GameBoard board, Player player) {
		float best = Float.NEGATIVE_INFINITY;
		Move bestMove = null;
		for(Move move : orderer.orderMoves(board, board.getActivePlayer())){
			float score = alphaBeta(board.move(move), 2, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
			if(score>best){
				best = score;
				bestMove = move;
				System.out.println(best);
			}
		}
		return bestMove;
	}
	
	/** Does Alpha Beta Nega Max */
	private float alphaBeta(GameBoard board, int depth, float alpha, float beta){
		if(depth==0 || board.getWinner().isLegalPlayer()){
			Player player = board.getActivePlayer();
			if(board.getWinner().isLegalPlayer()){
				return board.getWinner().isSame(player)?10000:-10000;
			}
			else{
				return eval.evaluate(board, player);
			}
		}
		float score = Float.NEGATIVE_INFINITY;
		for(Move move : orderer.orderMoves(board, board.getActivePlayer())){
			float value = alphaBeta(board.move(move), depth-1, -beta, -alpha);
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
		return score;
	}
	
}
