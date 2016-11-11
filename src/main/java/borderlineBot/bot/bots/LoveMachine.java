package borderlineBot.bot.bots;

import borderlineBot.bot.Bot;
import borderlineBot.bot.evals.EvaluationFunction;
import borderlineBot.bot.evals.GenericEval;
import borderlineBot.bot.moveOrderers.BasicOrderer;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;

public class LoveMachine implements Bot{
	
	/** The main Bot used by Love Machine */
	private Bot mainBot;
	
	/** Bot that can return a move in case anything breaks */
	private Bot backup;
	
	/** Constructs new Love Machine */
	public LoveMachine(){
		this.mainBot = mainBot();
		this.backup = new EvaluateOnePlyBot(new GenericEval());
	}
	
	private Bot mainBot(){
		EvaluationFunction eval = new GenericEval();
		return new NewAlphaBetaTranspositionTableNegaMaxBot(new BasicOrderer(), eval, 13);//15
	}
	
	
	/** Processes Move */
	public Move move(GameBoard board, Player player) {
		try{
			return mainBot.move(board, player);
		}catch(Exception ex){
			System.out.println("damage");
			this.mainBot = mainBot();
			return backup.move(board, player);
		}
	}
	
}
