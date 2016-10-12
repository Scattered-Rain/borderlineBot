package borderlineBot.bot.bots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
import borderlineBot.util.Constants;
import borderlineBot.util.Tuple;
import borderlineBot.util.hashing.Hasher;
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
		return makeMove(board, player);
	}
	
	//Single Threaded
	/** Processes in ONE Threads */
	private Move makeMove(GameBoard board, Player player){
		int alpha = Constants.MIN;
		int beta = Constants.MAX;
		Tuple<Move, Integer> bestMove = new Tuple<Move, Integer>(null, Constants.MIN);
		List<Move> possibleMoves = orderer.orderMoves(board);
		int counter = 0;
		for(Move move : possibleMoves){
			counter++;
			int value = -alphaBeta(board.move(move), 1, -beta, -alpha);
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
		//System.out.println(counter+"/"+possibleMoves.size());
		return bestMove.getA();
	}
	
	
	//Actual Alpha Beta!
	/** Does simple alpha beta processing */
	private int alphaBeta(GameBoard board, int depth, int alpha, int beta){
		//Get all Moves
		List<Move> possibleMoves = orderer.orderMoves(board);
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
				if(alpha>=beta || tableNode.getScoreType()==TranspositionTable.TranspositionNode.EXACT_SCORE){
					return tableNode.getScore();
				}
			}
			Move bestMove = tableNode.getBestMove().repurpose(board.getActivePlayer(), board);
			possibleMoves.remove(bestMove);
			possibleMoves.add(0, bestMove);
		}
		//alpha beta processing
		Tuple<Move, Integer> bestMove = new Tuple<Move, Integer>(null, Integer.MIN_VALUE);
		int counter = 0;
		for(Move move : possibleMoves){
			counter++;
			int value = -alphaBeta(board.move(move), depth+1, -beta, -alpha);
			if(bestMove.getB()<value){
				bestMove = new Tuple<Move, Integer>(move, value);
			}
			if(value>alpha){
				alpha = value;
			}
			if(alpha>=beta){
				//System.out.println(counter+"/"+possibleMoves.size());
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
	
	
	//--utility--
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
