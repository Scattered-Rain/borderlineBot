package borderlineBot.bot.bots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.moveOrderers.MoveOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
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
	
	/** Counter holding the number of times this bot has been asked to calculate a move */
	private int botIteration;
	/** The Transposition Table used by this Bot */
	private TranspositionTable table;
	/** Depth to search */
	private int depth;
	
	
	/** Constructs new Bot */
	public BasicAlphaBetaNegaMaxBot(MoveOrderer orderer, EvaluationFunction eval, int depth){
		this.orderer = orderer;
		this.eval = eval;
		this.depth = depth;
		this.table = new TranspositionTable();
		this.botIteration = 0;
	}
	
	
	/** Bot Processing */
	public Move move(GameBoard board, Player player){
		//this.table.reset();
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
		this.botIteration+=2;
		return best.getA();
	}
	
	/** Calculates moves in multiple threads */
	private void calcMoveMultithreaded(final GameBoard board, final Move move, final List<Tuple<Move, Integer>> evals){
		Thread t = new Thread(new Runnable(){
			public void run(){
				int score = Integer.MIN_VALUE;
				score = alphaBeta(board.move(move), depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
				evals.add(new Tuple<Move, Integer>(move, score));
			}
		});  
		t.start();
	}
	
	/** Does Alpha Beta Nega Max */
	private int alphaBeta(GameBoard board, int depth, int alpha, int beta){
		boolean replaceTableNode = false;
		Hash hash = Hasher.hashBoard(board);
		if(table.contains(hash)){
			TranspositionNode node = table.get(hash);
			node.incrementVisited();
			if(node.isDeeperOrEqual(this.depth-depth+botIteration)){
				//System.out.println("Hash Break at "+(this.depth-depth)+" - Seen: "+node.getVisited());
				return node.getScore();
			}
			else{
				replaceTableNode = true;
			}
		}
		if(depth==0 || board.getWinner().isLegalPlayer()){
			Player player = board.getActivePlayer().getOpponent();
			if(board.getWinner().isLegalPlayer()){
				return board.getWinner().isSame(player)?10000:-10000;
			}
			else{
				return (int)eval.evaluate(board, player);
			}
		}
		int score = Integer.MIN_VALUE;
		for(Move move : orderer.orderMoves(board, board.getActivePlayer())){
			int value = -alphaBeta(board.move(move), depth-1, -beta, -alpha);
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
		if(replaceTableNode){
			table.replace(hash, new TranspositionNode(hash, (int)score, this.depth-depth+botIteration));
		}
		else{
			table.put(hash, new TranspositionNode(hash, (int)score, this.depth-depth+botIteration));
		}
		return score;
	}
	
}
