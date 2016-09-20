package borderlineBot.bot.evals;

import java.util.ArrayList;
import java.util.List;

import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
import borderlineBot.game.Unit;
import borderlineBot.util.Point;
import borderlineBot.util.RNG;

/** Basic Evaluation function driven by arbitrary heuristics */
public class HeuristicEval implements EvaluationFunction{
	
	/** The bias the given to clear win/lose conditions (Should be magnitudes higher than can otherwise be reached) */
	private static final float CLEAR_BIAS = 100000f;
	/** Value Bias for the remainder of units [{playerOne, PlayerTWO}, {opponentONE, opponentTWO}] */
	private static final float[][] UNIT_EXIST_BIAS = new float[][]{{10.0f, 15.0f}, {-10.0f, -15.0f}};
	
	/** Evaluation Function used for clear win/loss predictions */
	private static final ClearWinLossEval clear = new ClearWinLossEval();
	
	
	/** Evaluates */
	public float evaluate(GameBoard board, Player player){
		board.getBoardWithView(player);
		float score = 0;
		//Clear Check
		score += clear.evaluate(board, player)*CLEAR_BIAS;
		//Eval start:
		//Counts Units of both players on the board
		int numberPieces[][] = board.countUnits(player);
		for(int cp=0; cp<numberPieces.length; cp++){
			for(int cu=0; cu<numberPieces[0].length; cu++){
				score += numberPieces[cp][cu]*UNIT_EXIST_BIAS[cp][cu];
			}
		}
		//Checks threatened units
		List<Point>[] units = new List[]{board.getAllUnitLocs(player), board.getAllUnitLocs(player.getOpponent())};
		for(int c=0; c<2; c++){
			for(int c2=0; c2<units[c].size(); c2++){
				if(calcThreats(units[c].get(c2), board).size()>=1){
					score -= UNIT_EXIST_BIAS[c][board.getTile(units[c].get(c2)).getUnit().isUnit(Unit.ONE)?0:1]/2;
				}
			}
		}
		//Rewards for blocked opponent movement
		List<Move> playMoves = board.generateAllHypotheticalLegalMoves(player);
		List<Move> oppMoves = board.generateAllHypotheticalLegalMoves(player.getOpponent());
		for(Move oppMove : oppMoves){
			Point target = oppMove.getTarget(board);
			for(Move playMove : playMoves){
				for(Point strikes : playMove.getTargetList(board)){
					if(target.equals(strikes)){
						score += (GameBoard.BOARD_SIZE.getY() - target.getY()) * 0.1f;
						break;
					}
				}
			}
		}
		//Add small random value
		score += RNG.nextFloat()*0.01f;
		//Return Score
		return score;
	}
	
	
	/** Returns list of the locations of all Units that can strike the given unit */
	private List<Point> calcThreats(Point unit, GameBoard board){
		List<Point> threats = new ArrayList<Point>();
		Player player = board.getTile(unit).getPlayer();
		if(!player.isLegalPlayer()){
			return threats;
		}
		else{
			List<Move> oppMoves = board.generateAllHypotheticalLegalMoves(player.getOpponent());
			for(Move oppMove : oppMoves){
				for(Point strike : oppMove.getTargetList(board)){
					if(unit.equals(strike)){
						threats.add(oppMove.getUnit(board));
					}
				}
			}
		}
		return threats;
	}
	
}
