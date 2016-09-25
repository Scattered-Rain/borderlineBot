package borderlineBot.bot.evals;

import java.util.List;

import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.game.Player;
import borderlineBot.util.Point;

/** Evaluation which only returns a score for a won/lost or a clear future win/lose board */
public class ClearWinLossEval implements EvaluationFunction{
	
	//--statics--
	/** Instance of ClearWin/Loss eval for simple static access */
	public static ClearWinLossEval clearEval = new ClearWinLossEval();
	
	/** Static access: Returns 1 for win, 0.1 for guaranteed to win, -1 for lose or guaranteed to lose, 0 for no prediction */
	public static float staticEvaluate(GameBoard board, Player player){
		return clearEval.evaluate(board, player);
	}
	
	//--finals//
	/** Constant to be applied to all punishments */
	public static final float NEGATIVE = -1;
	/** The absolute value returned for a clear win/loss */
	public static final float CLEAR_RESULT = 1.0f;
	/** The absolute value returned for when no result is determinable */
	public static final float NO_RESULT = 0;
	/** The absolute value returned for when a win/loss is guaranteed but not directly resulting */
	public static final float GUARANTEED_RESULT = 0.5f;
	
	
	//--methods--
	/** Returns 1 for win, 0.1 for guaranteed to win, -1 for lose or guaranteed to lose, 0 for no prediction */
	public float evaluate(GameBoard board, Player player){
		//Check Direct Win
		if(board.getWinner().isSame(player)){
			return CLEAR_RESULT;
		}
		//Check Direct Loss
		if(board.getWinner().isSame(player.getOpponent())){
			return CLEAR_RESULT*NEGATIVE;
		}
		//Check Opponent One Ply Win Manouvers
		List<Move> oppMoves = board.generateAllHypotheticalLegalMoves(player.getOpponent());
		int[][] unitCount = board.countUnits(player);
		if(board.getActivePlayer().isSame(player.getOpponent())){
			//Check Opponent ready to capture Borderline
			for(Move oppMove : oppMoves){
				if(oppMove.getTarget(board).getY()==board.getBorderline(player)){
					return GUARANTEED_RESULT*NEGATIVE;
				}
			}
			//Check Opponent Ready to kill last unit
			if(unitCount[0][0]+unitCount[0][1]==1){
				Point lastUnit = null;
				for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
					for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
						Point p = new Point(cx, cy);
						Tile t = board.getTile(p);
						if(!t.isEmpty() && t.getPlayer().isSame(player)){
							lastUnit = p;
							break;
						}
					}
				}
				for(Move oppMove : oppMoves){
					for(Point point : oppMove.getTargetList(board)){
						if(point.equals(lastUnit)){
							return GUARANTEED_RESULT*NEGATIVE;
						}
					}
				}
			}
		}
		//Check Player win capture border directly or makes sure to be able to capture border next turn
		List<Move> playMoves = board.generateAllHypotheticalLegalMoves(player);
		for(Move playMove : playMoves){
			if(playMove.getTarget(board).getY()==board.getBorderline(player.getOpponent())){
				if(board.getActivePlayer().isSame(player)){
					return GUARANTEED_RESULT;
				}
				else{
					boolean notHit = true;
					for(Move oppMove : oppMoves){
						for(Point struck : oppMove.getTargetList(board)){
							if(playMove.getUnit(board).equals(struck)){
								notHit = false;
								break;
							}
						}
					}
					if(notHit){
						return GUARANTEED_RESULT;
					}
				}
			}
		}
		//Check Player wipe out all opponent Units
		if(unitCount[1][0]+unitCount[1][1]==1){
			Point lastUnit = null;
			for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
				for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
					Point p = new Point(cx, cy);
					Tile t = board.getTile(p);
					if(!t.isEmpty() && t.getPlayer().isSame(player.getOpponent())){
						lastUnit = p;
						break;
					}
				}
			}
			for(Move playMove : playMoves){
				for(Point strikes : playMove.getTargetList(board)){
					if(strikes.equals(lastUnit)){
						return GUARANTEED_RESULT;
					}
				}
			}
		}
		//No Result Found
		return NO_RESULT;
	}
	
}
