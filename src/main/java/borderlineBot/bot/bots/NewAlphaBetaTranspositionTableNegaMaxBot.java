package borderlineBot.bot.bots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.ClearWinLossEval;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
import borderlineBot.util.Constants;
import borderlineBot.util.RNG;
import borderlineBot.util.Timer;
import borderlineBot.util.Tuple;
import borderlineBot.util.hashing.Hasher;
import borderlineBot.util.hashing.Hasher.Hash;
import borderlineBot.util.transpositionTable.TranspositionTable;
import borderlineBot.util.transpositionTable.TranspositionTable.TranspositionNode;

/** Alpha Beta Nega Max implementing Transposition Tables */
public class NewAlphaBetaTranspositionTableNegaMaxBot implements Bot{
	
	/** The Move Orderer used by this Bot */
	private MoveOrderer orderer;
	/** The Evaluation Function used by this Bot */
	private EvaluationFunction eval;
	/** Total Depth at which this Bot explores the Tree */
	private int totalDepth;
	
	/** The transposition Table used by this Bot */
	private TranspositionTable table;
	/** The Iteration Table used by this Bot */
	private TranspositionTable iterationTable;;
	
	private long startSearch;
	private int allowedTime;
	
//	private int nodesVisited;
//	private int transpositionStrike;
//	private int niceOrdering;
	
	/** Constructs new */
	public NewAlphaBetaTranspositionTableNegaMaxBot(MoveOrderer orderer, EvaluationFunction eval, int depth){
		this.eval = eval;
		this.orderer = orderer;
		this.totalDepth = depth;
		this.table = new TranspositionTable();
		this.iterationTable = new TranspositionTable();
		this.allowedTime = 1000*depth;
	}
	
	
	/** Move Processing */
	public Move move(GameBoard board, Player player){
//		this.nodesVisited = 0;
//		this.transpositionStrike = 0;
//		this.niceOrdering = 0;
		table.reset();
		table = new TranspositionTable();
		iterationTable.reset();
		return makeMove(board, player, totalDepth);
	}
	
	//Single Threaded
	/** Processes in ONE Threads */
	private Move makeMove(GameBoard board, Player player, int depth){
		this.allowedTime = Timer.getTimeToCalc();
		this.startSearch = System.currentTimeMillis();
		int alpha = Constants.MIN;
		int beta = Constants.MAX;
		Tuple<Move, Integer> bestMove = new Tuple<Move, Integer>(board.generateAllLegalMoves().get(RNG.nextInt(board.generateAllLegalMoves().size())), 69);
		for(int c=1; !outOfTime(); c++){
			int iteration = c-1;
			boolean doAlphaBeta = c>3;
			Tuple<Move, Integer> thisMove = alphaBeta(board.clone(), c, false, -beta, -alpha, iteration, doAlphaBeta);
			if(!outOfTime()){
				bestMove = thisMove;
				System.out.println("Iteration: "+iteration+",  Move: "+bestMove.getA().toString()+", Score: "+bestMove.getB());
			}
			if(bestMove.getB()>=Constants.WIN_SCORE-300){
				Timer.usedTime((int)(System.currentTimeMillis()-startSearch));
				System.out.println("Move: "+bestMove.getA().toString()+", Score: "+bestMove.getB()+", Time: "+Timer.remainingTime());
				return bestMove.getA();
			}
		}
		Timer.usedTime(allowedTime);
		System.out.println("Move: "+bestMove.getA().toString()+", Score: "+bestMove.getB()+", Time: "+Timer.remainingTime());
		return bestMove.getA();
	}
	
	
	//Actual Alpha Beta!
	/** Does simple alpha beta processing */
	private Tuple<Move, Integer> alphaBeta(GameBoard board, int depth, boolean allowNullCutoff, int alpha, int beta, int iteration, boolean doAlphaBeta){
		//this.nodesVisited++;
		//Get all Moves
		List<Move> possibleMoves = orderer.orderMoves(board);
		//Win Eval
		int leaf = leafCondition(board, depth);
		if(leaf!=0){
			return new Tuple<Move, Integer>(null, leaf);
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
					//this.transpositionStrike++;
					return new Tuple<Move, Integer>(tableNode.getBestMove(), tableNode.getScore());
				}
			}
			Move bestMove = tableNode.getBestMove().repurpose(board.getActivePlayer(), board);
			possibleMoves.remove(bestMove);
			possibleMoves.add(0, bestMove);
		}
		if(iterationTable.contains(hash)){
			Move itMove = iterationTable.get(hash).getBestMove();
			possibleMoves.remove(itMove);
			possibleMoves.add(0, itMove);
		}
		//Quick check for Null-Move
		if(allowNullCutoff){
			final int nullMoveDiscount = 2;
			int nullValue = -alphaBeta(board.nullMove(), depth-nullMoveDiscount-1, false, -beta, -alpha, iteration, doAlphaBeta).getB();
			if(nullValue>=beta){
				return new Tuple<Move, Integer>(null, beta);
			}
		}
		//General processing of tree
		Tuple<Move, Integer> bestMove = new Tuple<Move, Integer>(null, Integer.MIN_VALUE);
		int counter = 0;
		for(Move move : possibleMoves){
			if(true){
				counter++;
				int value = -alphaBeta(board.move(move), depth-1, true, -beta, -alpha, iteration, doAlphaBeta).getB();
				if(bestMove.getB()<value){
					bestMove = new Tuple<Move, Integer>(move, value);
				}
				//alpha beta processing
				if(value>alpha){
					alpha = value;
				}
				if(alpha>=beta){
					if(doAlphaBeta){
						if(counter==1){
							//niceOrdering++;
						}
						break;
					}
				}
			}
		}
		//table post processing
		if(tableThis){
			TranspositionNode node = new TranspositionNode(hash, depth, bestMove.getA(), alpha, originalAlpha, originalBeta);
			table.put(hash, node);
		}
		//Iteration Table Processing
		if(iterationTable.contains(hash) && (iterationTable.get(hash).getDepth()<depth || iterationTable.get(hash).getScore()<iteration)){
			TranspositionNode itNode = new TranspositionNode(hash, depth, bestMove.getA(), iteration, originalAlpha, originalBeta);
			iterationTable.put(hash, itNode);
		}
		//return
		return new Tuple<Move, Integer>(bestMove.getA(), alpha);
	}
	
	
	//--utility--
	/** Returns the score of this node if it is a leaf node, else 0 (score will never be == 0) */
	private int leafCondition(GameBoard board, int depth){
		int temp = ClearWinLossEval.clearWinLossEval(board, board.getActivePlayer());
		if(temp!=0){
			return temp+depth;
		}
		if(depth<=0 || outOfTime()){
			temp = eval.evaluate(board, board.getActivePlayer());
			return temp!=0?temp:1;
		}
		//TODO: This condition is for debugging, remove:
		if(board.generateAllLegalMoves().size()==0){
			return 1;
		}
		return 0;
	}
	
	/** Returns whether we are out of time */
	private boolean outOfTime(){
		return (System.currentTimeMillis()-startSearch)>this.allowedTime;
	}
	
}
