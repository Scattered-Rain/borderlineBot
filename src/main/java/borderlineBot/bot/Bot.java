package borderlineBot.bot;

import lombok.Getter;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.Player;

/** Abstract Bot used as the base for all Bots */
public interface Bot{
	
	/** AI process of the Bot, returns desired Move */
	public abstract Move move(GameBoard board, Player player);
	
}
