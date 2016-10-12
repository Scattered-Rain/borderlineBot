package borderlineBot.bot.bots;

import java.util.List;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
import borderlineBot.util.Constants;
import borderlineBot.util.Tuple;
import borderlineBot.util.hashing.Hasher.Hash;
import borderlineBot.util.transpositionTable.TranspositionTable;
import borderlineBot.util.transpositionTable.TranspositionTable.TranspositionNode;

/** Alpha Beta Nega Max implementing Transposition Tables */
public class AlphaBetaTranspositionTableNegaMaxBot implements Bot{
	
	/** The Move Orderer used by this Bot */
	private MoveOrderer orderer;
	/** The Evaluation Function used by this Bot */
	private EvaluationFunction eval;
	/** Total Depth at which this Bot explores the Tree */
	private int totalDepth;
	
	/** The transposition Table used by this Bot */
	private TranspositionTable table;
	
	
	/** Constructs new */
	public AlphaBetaTranspositionTableNegaMaxBot(MoveOrderer orderer, EvaluationFunction eval, int depth){
		this.eval = eval;
		this.orderer = orderer;
		this.totalDepth = depth;
		this.table = new TranspositionTable();
	}
	
	
	/** Move Processing */
	public Move move(GameBoard board, Player player) {
		table.reset();
		Tuple<Move, Integer> bestMove = new Tuple<Move, Integer>(null, Integer.MIN_VALUE);
		List<Move> possibleMoves = orderer.orderMoves(board, board.getActivePlayer());
		for(Move move : possibleMoves){
			int value = -alphaBeta(board.move(move), 0, Constants.MIN, Constants.MAX);
			if(value>bestMove.getB()){
				bestMove = new Tuple<Move, Integer>(move, value);
			}
		}
		return bestMove.getA();
	}
	
	
	/** Does simple alpha beta processing */
	private int alphaBeta(GameBoard board, int depth, int alpha, int beta){
		//Win Eval
		int leaf = leafCondition(board, depth);
		if(leaf!=0){
			return leaf;
		}
		//table
		int originalAlpha = alpha;
		int originalBeta = beta;
		boolean tableThis = true;
		Hash hash = board.hash();
		if(table.contains(hash)){
			TranspositionNode tableNode = table.get(hash);
			if(tableNode.appropriate(depth)){
				tableThis = false;
				if(tableNode.getScoreType()==TranspositionTable.TranspositionNode.LOWER_BOUND){
					if(alpha<tableNode.getScore()){
						alpha = tableNode.getScore();
					}
				}
				else if(tableNode.getScoreType()==TranspositionTable.TranspositionNode.UPPER_BOUND){
					if(beta>tableNode.getScore()){
						beta = tableNode.getScore();
					}
				}
				else if(tableNode.getScoreType()==TranspositionTable.TranspositionNode.EXACT_SCORE){
					return tableNode.getScore();
				}
				if(alpha>=beta){
					return tableNode.getScore();
				}
			}
		}
		//alpha beta processing
		Tuple<Move, Integer> bestMove = new Tuple<Move, Integer>(null, Constants.MIN);
		List<Move> possibleMoves = orderer.orderMoves(board, board.getActivePlayer());
		for(Move move : possibleMoves){
			int value = -alphaBeta(board.move(move), depth+1, -beta, -alpha);
			if(bestMove.getB()<value){
				bestMove = new Tuple<Move, Integer>(move, value);
			}
			if(value>alpha){
				alpha = value;
			}
			if(alpha>=beta){
				break;
			}
		}
		//table post processing
		if(tableThis){
			TranspositionNode node = new TranspositionNode(hash, depth, bestMove.getA(), alpha, originalAlpha, originalBeta);
			table.put(hash, node);
		}
		//return
		return alpha;
	}
	
	
	/** Returns the score of this node if it is a leaf node, else 0 (score will never be == 0) */
	private int leafCondition(GameBoard board, int depth){
		int temp = winLoss(board);
		if(temp!=0){
			return temp;
		}
		if(depth==totalDepth){
			temp = eval.evaluate(board, board.getActivePlayer());
			return temp!=0?temp:1;
		}
		//TODO: This condition is for debugging, remove:
		if(board.generateAllLegalMoves().size()==0){
			return 1;
		}
		return 0;
	}
	/** Returns the score of the node for actual win/loss or derivatives, 0 otherwise */
	private int winLoss(GameBoard board){
		Player player = board.getActivePlayer();
		if(board.getWinner().isLegalPlayer()){
			return board.getWinner().isSame(player)?Constants.WIN_SCORE:Constants.LOSE_SCORE;
		}
		else{
			return 0;
		}
	}
	
}
