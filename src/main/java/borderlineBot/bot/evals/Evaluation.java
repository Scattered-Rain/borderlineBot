package borderlineBot.bot.evals;

import borderlineBot.Launcher;
import borderlineBot.game.Player;
import lombok.Getter;

/** Util Object which contains the evaluation of a Board */
public class Evaluation{
	
	/** The Player for whom the Score has been evaluated */
	@Getter private Player player;
	
	/** Depth of where this evaluation took place */
	@Getter private int depth;
	
	/** The Score evaluated for */
	private float score;
	
	/** Player that has is guaranteed to win the game */
	private Player guaranteedWin = Player.NONE;
	
	
	
	/** Construct new Evaluate */
	public Evaluation(Player player, float score, int depth){
		this.player = player;
		this.score = score;
		this.depth = depth;
	}
	
	/** Constructs new Evaluate with a guaranteed Winner */
	public Evaluation(Player player, int depth, Player winner){
		this.player = player;
		this.depth = depth;
		this.guaranteedWin = winner;
	}
	
	
	/** Returns the Score of this Evaluate relative to the given Player */
	public float getScore(Player player){
		return this.player.isSame(player)?score:-score;
	}
	
	/** Returns whether either Player has a guaranteed win */
	public boolean hasQuaranteedWinForOne(){
		return !guaranteedWin.isSame(Player.NONE);
	}
	
	/** Returns whether the given Player has a guaranteed win */
	public boolean getQuaranteedWin(Player player){
		return guaranteedWin.isSame(player);
	}
	
	
	/** Returns whether this Evaluate is better than the given evaluate relative to the given Player*/
	public boolean isBetter(Evaluation evaluate, Player player){
		//Check for guaranteed victories
		if(this.hasQuaranteedWinForOne() || evaluate.hasQuaranteedWinForOne()){
			//Both win
			if(this.getQuaranteedWin(player) && evaluate.getQuaranteedWin(player)){
				return this.depth<evaluate.getDepth();
			}
			//One wins
			else if(this.getQuaranteedWin(player) || evaluate.getQuaranteedWin(player)){
				return this.getQuaranteedWin(player);
			}
			//(at least) one loses
			else if(this.getQuaranteedWin(player.getOpponent()) || evaluate.getQuaranteedWin(player.getOpponent())){
				return !this.getQuaranteedWin(player.getOpponent());
			}
		}
		//Undetermined on both sides. Just handle score.
		return this.getScore(player)>evaluate.getScore(player);
	}
	
}
