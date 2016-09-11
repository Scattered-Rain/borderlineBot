package borderlineBot.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
/** Enum containing all two Players */
public enum Player{
	/** Neutral Player, used for debug, maybe (By default local view of Board) */
	NONE(0, false),
	/** Red Player (Player making the first move) */
	RED(1, true),
	/** Blue Player */
	BLU(2, true);
	
	
	/** Returns id of this player */
	@Getter private int id;
	/** Whether this is an actual player instead of NONE */
	@Getter private boolean isLegalPlayer;
	
	
	/** Returns whether the given player is the opponent of this player */
	public boolean isOpponent(Player player){
		if(player.isLegalPlayer && isLegalPlayer){
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
	
	/** Returns the Opponent Player of this Player (if legal) */
	public Player getOpponent(){
		if(!isLegalPlayer){
			return this;
		}
		else{
			if(id==1){
				return BLU;
			}
			else{
				return RED;
			}
		}
	}
	
}
