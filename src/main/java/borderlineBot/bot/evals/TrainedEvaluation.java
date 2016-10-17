package borderlineBot.bot.evals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import lombok.Getter;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.game.Player;
import borderlineBot.game.Unit;
import borderlineBot.util.Point;
import borderlineBot.util.RNG;
import borderlineBot.util.Tuple;

/** Evaluation Function which is trained on the use of its weights */
public class TrainedEvaluation implements EvaluationFunction{
	
	/** The max value allowed to be allocated to a weight */
	public static final int WEIGHT_BOUND = 100;
	
	/** Array containing arrays of weights determined by former training results */
	public static final int[][] TRAINING_RESULTS = new int[][]{
		new int[]{1000, 1010, 1005, 1015, 100, 100, 100, 100, 100, 100, 100, 100, 10}, //Basic Weights
		new int[]{79, 86, 75, 94, 92, 14, 75, 30, 29, 44, 68, 11, 78},//1st Gen
		new int[]{4, 36, 75, 94, 98, 95, 75, 19, 29, 44, 68, 11, 78},//2nd Gen
		new int[]{79, 44, 4, 38, 92, 39, 76, 30, 18, 71, 72, 83, 78},//3rd Gen
		new int[]{79, 86, 97, 94, 92, 14, 76, 30, 4, 44, 68, 83, 78},//4th Gen
		new int[]{81, 83, 84, 40, 42, 85, 86, 68, 55, 97, 27, 41, 20},//5th Gen
		new int[]{79, 86, 97, 94, 3, 39, 77, 30, 18, 44, 72, 83, 78},//6th Gen
		new int[]{},//7th Gen
		new int[]{},//8th Gen
		new int[]{},//9th Gen
	};
	
	/** Array containing the Evaluations that are used by the TrainedEvaluation */
	public SubEvaluation[] evals;
	/** The weights of this function, read in according to index for all parts of the evaluation in order */
	@Getter public int[] weights;
	
	
	/** Constructs new TrainedEvaluation with the given weights (Doesn't check for too few weights) */
	public TrainedEvaluation(int ... weights){
		this.weights = weights;
		initEvals();
	}
	
	/** Constructs new Evaluation Function with randomly set weights */
	public TrainedEvaluation(){
		initEvals();
		this.weights = new int[getNeededWeights()];
		for(int c=0; c<weights.length; c++){
			weights[c] = randWeight();
		}
	}
	
	/** Initializes all SubEvaluations */
	private void initEvals(){
		this.evals = new SubEvaluation[]{new UnitCount(), new PlayerMoveOptions(), new LineControl(), new UnitThreats(), new Random()};
	}
	
	/** The actual evaluation Process */
	public int evaluate(GameBoard board, Player player){
		return doEvaluate(board, player)-doEvaluate(board, player.getOpponent());
	}
	
	/** Does value Processing */
	private int doEvaluate(GameBoard board, Player player){
		int counter = 0;
		int score = 0;
		for(SubEvaluation eval : evals){
			int[] subWeights = Arrays.copyOfRange(weights, counter, counter+eval.getNeededWeights());
			counter += eval.getNeededWeights(); 
			score = eval.evaluate(board, player, subWeights);
		}
		return score;
	}
	
	/** Returns the number of weights needed for the TrainedEvaluation Function */
	public int getNeededWeights(){
		int sum = 0;
		for(SubEvaluation eval : evals){
			sum += eval.getNeededWeights();
		}
		return sum;
	}
	
	/** Generate Random Weight between -WEIGHT_BOUND and WEIGHT_BOUND */
	public static int randWeight(){
		return RNG.nextInt(WEIGHT_BOUND);
	}
	
	/** Returns String of the Weigths of this Evaluation Function */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(int c=0; c<weights.length; c++){
			buffer.append(weights[c]).append(", ");
		}
		return buffer.toString();
	}
	
	
	//--classes--
	/** This abstract class represents the basis for all Sub Evaluations of the Trained Evaluation Class */
	private static abstract class SubEvaluation{
		/** Returns the number of weights this SubEvaluation will need */
		public int getNeededWeights(){
			return 1;
		}
		/** Calculates the score of this SubEvaluation for the given player, given the given weights */
		public abstract int evaluate(GameBoard board, Player player, int ... weights);
	}
	
	/** Calculates a score based on the number of Units that are on the board */
	private static class UnitCount extends SubEvaluation{//Player ONE, Player TWO, Opponent ONE, Opponent TWO
		/** Returns the number of weights this SubEvaluation will need */
		public int getNeededWeights(){
			return 4;
		}
		/** Calculates the score of this SubEvaluation for the given player, given the given weights */
		public int evaluate(GameBoard board, Player player, int ... weights){
			final Tuple<Player, Unit>[] ref = new Tuple[]{new Tuple<Player, Unit>(player, Unit.ONE), new Tuple<Player, Unit>(player, Unit.TWO), new Tuple<Player, Unit>(player.getOpponent(), Unit.ONE), new Tuple<Player, Unit>(player.getOpponent(), Unit.TWO)};
			final int[] sign = new int[]{1, 1, -1, -1};
			int score = 0;
			for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
				for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
					Tile tile = board.getTile(new Point(cx, cy));
					if(!tile.isEmpty()){
						for(int c=0; c<ref.length; c++){
							if(tile.getPlayer().isSame(ref[c].getA()) && tile.getUnit().isUnit(ref[c].getB())){
								score += 1 * weights[c]*sign[c];
							}
						}
					}
				}
			}
			return score;
		}
	}
	
	/** Calculates a score based on the number of hypothetical moves that either player could do */
	private static class PlayerMoveOptions extends SubEvaluation{//Player, Opponent
		/** Returns the number of weights this SubEvaluation will need */
		public int getNeededWeights(){
			return 2;
		}
		/** Calculates the score of this SubEvaluation for the given player, given the given weights */
		public int evaluate(GameBoard board, Player player, int ... weights){
			final Player[] ref = new Player[]{player, player.getOpponent()};
			final int[] sign = new int[]{1, -1};
			int score = 0;
			for(int c=0; c<ref.length; c++){
				List<Move> moves = board.generateAllHypotheticalLegalMoves(ref[c]);
				score += moves.size() * weights[c]*sign[c];
			}
			return score;
		}
	}
	
	/** Calculates a score based on the advancement of the farthest unit on the board (with linear scaling) */
	private static class LineControl extends SubEvaluation{//Player, Opponent
		/** Returns the number of weights this SubEvaluation will need */
		public int getNeededWeights(){
			return 2;
		}
		/** Calculates the score of this SubEvaluation for the given player, given the given weights */
		public int evaluate(GameBoard board, Player player, int ... weights){
			final Player[] ref = new Player[]{player, player.getOpponent()};
			final int[] sign = new int[]{1, -1};
			int score = 0;
			for(int c=0; c<ref.length; c++){
				boolean mostAdvanceLineFound = false;
				for(int cy=0; cy<GameBoard.BOARD_SIZE.getY() && !mostAdvanceLineFound; cy++){
					int y = GameBoard.BOARD_SIZE.getY() - cy - 1;
					int yLoc = board.getBorderline(player) + board.getMovingDirection(player).getDir().multiply(new Point(y)).getY();
					for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
						Tile tile = board.getTile(new Point(cx, yLoc));
						if(!tile.isEmpty() && tile.getPlayer().isSame(ref[c])){
							score += y * weights[c]*sign[c];
							mostAdvanceLineFound = true;
							break;
						}
					}
				}
			}
			return score;
		}
	}

	/** Calculates a score based on the units that are threatened */
	private static class UnitThreats extends SubEvaluation{//Player ONE, Player TWO, Opponent ONE, Opponent TWO
		/** Returns the number of weights this SubEvaluation will need */
		public int getNeededWeights(){
			return 4;
		}
		/** Calculates the score of this SubEvaluation for the given player, given the given weights */
		public int evaluate(GameBoard board, Player player, int ... weights){
			final Player[] players = new Player[]{player, player.getOpponent()};
			final Tuple<Player, Unit>[] ref = new Tuple[]{new Tuple<Player, Unit>(player, Unit.ONE), new Tuple<Player, Unit>(player, Unit.TWO), new Tuple<Player, Unit>(player.getOpponent(), Unit.ONE), new Tuple<Player, Unit>(player.getOpponent(), Unit.TWO)};
			final int[] sign = new int[]{-1, -1, 1, 1};
			int score = 0;
			for(int c=0; c<players.length; c++){
				List<Point> attackableTiles = new ArrayList<Point>();
				List<Move> moves = board.generateAllHypotheticalLegalMoves(players[c]);
				for(Move move : moves){
					for(Point point : move.getTargetList(board)){
						attackableTiles.add(point);
					}
				}
				for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
					for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
						Point loc = new Point(cx, cy);
						Tile tile = board.getTile(loc);
						if(!tile.isEmpty() && !tile.getPlayer().isSame(players[c])){
							for(Point point : attackableTiles){
								if(point.equals(loc)){
									for(int w=0; w<ref.length; w++){
										if(tile.getPlayer().isSame(ref[w].getA()) && tile.getUnit().isUnit(ref[w].getB())){
											score += 1 * weights[w]*sign[w];
										}
									}
									break;
								}
							}
						}
					}
				}
			}
			return score;
		}
		
	}
	
	
	/** Calculates a score based on the number of hypothetical moves that either player could do */
	private static class Random extends SubEvaluation{//Player, Opponent
		/** Returns the number of weights this SubEvaluation will need */
		public int getNeededWeights(){
			return 1;
		}
		/** Calculates the score of this SubEvaluation for the given player, given the given weights */
		public int evaluate(GameBoard board, Player player, int ... weights){
			return RNG.nextInt(weights[0]+1);
		}
	}
	
	
}
