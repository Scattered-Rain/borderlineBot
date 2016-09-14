package borderlineBot.bot.evals;

import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;
import borderlineBot.game.Unit;
import borderlineBot.util.Point;

/** Basic Evaluation function driven by arbitrary heuristics */
public class HeuristicEval implements EvaluationFunction{
	
	/** Evaluates */
	public float evaluate(GameBoard board, Player player){
		//(Guaranteed) Win or Guaranteed Loss check
		if(board.getWinner().isSame(player)){
			return Float.POSITIVE_INFINITY;
		}
		else{
			
			
		}
		//Main Function
		float score = 0;
		board.setView(player);
		//Definitions of util variables
		final Unit[] UNIT_TYPES = new Unit[]{Unit.ONE, Unit.TWO};
		final Player[] PLAYERS = new Player[]{player, player.getOpponent()};
		//Eval start:
		//Counts Units of both players on the board
		int numberPieces[][] = new int[PLAYERS.length][UNIT_TYPES.length];
		for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
			for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
				Point p = new Point(cx, cy);
				for(int cp=0; cp<PLAYERS.length; cp++){
					for(int cu=0; cu<UNIT_TYPES.length; cu++){
						if(board.getTile(p).getPlayer().isSame(PLAYERS[cp]) && board.getTile(p).getUnit().isUnit(UNIT_TYPES[cu])){
							numberPieces[cp][cu]++;
						}
					}
				}
			}
		}
		final float[][] valueBias = new float[][]{{1.0f, 1.5f}, {-1.0f, -1.5f}};//[Player][Unit]
		for(int cy=0; cy<numberPieces.length; cy++){
			for(int cx=0; cx<numberPieces[0].length; cx++){
				score += numberPieces[cy][cx]*valueBias[cy][cx];
			}
		}
		return score;
	}
	
}
