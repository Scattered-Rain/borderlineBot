package borderlineBot.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
/** Enum containing all two Players */
public enum Player{
	NONE(0, false), //Neutral util player, no opponent to anyone
	BLUE(1, true),
	RED(2, true);
	
	
	/** Returns id of this player */
	@Getter private int id;
	/** Whether this is an actual player instead of NONE */
	@Getter private boolean isPlayer;
	
	
	/** Returns whether the given player is the opponent of this player */
	public boolean isOpponent(Player player){
		if(player.isPlayer && isPlayer){
			if(player.getId() != id){
				return true;
			}
		}
		return false;
	}
	
	/** Returns whether the given player is this player */
	public boolean isSame(Player player){
		return player.getId() == id;
	}
	
}
