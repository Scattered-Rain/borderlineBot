package borderlineBot.bot.bots;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;
import borderlineBot.util.RNG;

@AllArgsConstructor
/** Heuristic Bot that uses one ply depth to evaluate the best move */
public class EvaluateOnePlyBot implements Bot{
	//NOTE: May be broken now :/
	
	/** The Evaluation Function this Bot uses */
	private EvaluationFunction eval;
	
	/** AI processing */
	public Move move(GameBoard board, Player player){
		List<Move> bestMoves = new ArrayList<Move>();
		float bestScore = Float.NEGATIVE_INFINITY;
		for(Move move : board.generateAllLegalMoves()){
			GameBoard nextBoard = board.move(move);
			float tempScore = eval.evaluate(nextBoard, player);
			if(tempScore>bestScore){
				bestMoves.clear();
				bestScore = tempScore;
			}
			if(tempScore>=bestScore){
				bestMoves.add(move);
			}
		}
		return bestMoves.get(RNG.nextInt(bestMoves.size()));
	}

}
