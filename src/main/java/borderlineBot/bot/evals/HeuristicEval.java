package borderlineBot.bot.evals;

import borderlineBot.game.GameBoard;
import borderlineBot.game.Player;
import borderlineBot.game.Unit;
import borderlineBot.util.Point;

/** Basic Evaluation function driven by arbitrary heuristics */
public class HeuristicEval implements EvaluationFunction{
	
	/** Evaluates */
	public float evaluate(GameBoard board, Player player){
		float score = 0;
		//Definitions of util variables
		final Unit[] UNIT_TYPES = new Unit[]{Unit.ONE, Unit.TWO};
		final Player[] PLAYERS = new Player[]{player, player.getOpponent()};
		//Eval start:
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
		return score;
	}
	
}
