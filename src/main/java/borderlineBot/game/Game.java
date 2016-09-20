package borderlineBot.game;

import java.util.ArrayList;
import java.util.List;

import borderlineBot.bot.Bot;
import borderlineBot.game.GameBoard.Move;
import lombok.Getter;

/** Object Representing and managing an entire game of Borderline */
public class Game {
	
	/** List of all States that have been played in the game, in historical order (the last one being the current board) */
	@Getter private List<GameBoard> history;
	@Getter private int turn;
	
	/** The Bots playing the game */
	@Getter private Bot[] players;
	
	
	/** Constructs new Game between the two given bots */
	public Game(Bot bot0, Bot bot1){
		this.turn = 0;
		this.history = new ArrayList<GameBoard>();
		history.add(new GameBoard().getflipView());
		this.players = new Bot[]{bot0, bot1};
	}
	
	
	/** Advances the game by one turn */
	public void nextTurn(){
		Player current = getCurrentState().getActivePlayer();
		for(int c=0; c<PLAYER_LIST.length; c++){
			if(current.isSame(PLAYER_LIST[c])){
				Move move = players[c].move(getCurrentStateClone(), PLAYER_LIST[c]);
				GameBoard newState = getCurrentState().move(move);
				history.add(newState);
				this.turn++;
			}
		}
	}
	
	/** Returns current state of the gmae */
	private GameBoard getCurrentState(){
		return history.get(history.size()-1);
	}
	
	/** Returns Clone of current state */
	public GameBoard getCurrentStateClone(){
		return getCurrentState().clone();
	}
	
	/** Returns the index of the winning bot, -1 for no winner */
	public int getWinner(){
		Player winner = getCurrentState().getWinner();
		for(int c=0; c<PLAYER_LIST.length; c++){
			if(winner.isSame(PLAYER_LIST[c])){
				return c;
			}
		}
		return -1;
	}
	
	/** Flips View in current state */
	public void flipView(){
		setView(getCurrentState().getView().getOpponent());
	}
	
	/** Flips View in current state */
	public void setView(Player player){
		GameBoard temp = this.getCurrentState();
		if(!temp.getView().isSame(player)){
			this.history.remove(history.size()-1);
			this.history.add(temp.getBoardWithView(player));
		}
	}
	
	/** Returns whether the game is over */
	public boolean gameOver(){
		return getCurrentState().getWinner().isLegalPlayer();
	}
	
	
	/** Adds the given board to be the current board, for debug purposes only! */
	public void debugSpliceGameBoard(GameBoard splicedBoard){
		this.history.add(splicedBoard);
	}
	
	
	//--statics--
	/** List of the players in indexed order, bot0->player0 etc. */
	private final static Player[] PLAYER_LIST = new Player[]{Player.RED, Player.BLU};
	
}
