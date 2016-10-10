package borderlineBot.bot.bots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
		List<Tuple<Move, Integer>> evals = Collections.synchronizedList(new ArrayList<Tuple<Move, Integer>>());
		List<Move> moves = orderer.orderMoves(board, board.getActivePlayer());
		for(Move move : moves){
			calcMoveMultithreaded(board, move, evals);
		}
		while(evals.size()!=moves.size()){
			try{Thread.sleep(100);}catch(Exception ex){}
		}
		Tuple<Move, Integer> best = evals.get(0);
		for(Tuple<Move, Integer> eval : evals){
			if(eval.getB()>best.getB()){
				best = eval;
			}
		}
		return best.getA();
	}
	
	/** Calculates moves in multiple threads */
	private void calcMoveMultithreaded(final GameBoard board, final Move move, final List<Tuple<Move, Integer>> evals){
		Thread t = new Thread(new Runnable(){
			public void run(){
				int score = Constants.MIN;
				score = -alphaBeta(board.move(move), depth, Constants.MIN, Constants.MAX);
				evals.add(new Tuple<Move, Integer>(move, score));
			}
		});
		t.start();
	}
	
	/** Does Alpha Beta Nega Max */
	private int alphaBeta(GameBoard board, int depth, int alpha, int beta){
		//overhead
		List<Move> possibleMoves = orderer.orderMoves(board, board.getActivePlayer());
		//win/lose check
		if(depth==0 || board.getWinner().isLegalPlayer() || possibleMoves.size()==0){
			Player player = board.getActivePlayer();
			if(board.getWinner().isLegalPlayer()){
				return board.getWinner().isSame(player)?Constants.WIN_SCORE:Constants.LOSE_SCORE;
			}
			else{
				return eval.evaluate(board, player);
			}
		}
		//alpha beta processing
		for(Move move : possibleMoves){
			int value = -alphaBeta(board.move(move), depth-1, -beta, -alpha);
			if(value>alpha){
				alpha = value;
			}
			if(alpha>=beta){
				break;
			}
		}
		//return
		return alpha;
	}
	
}
