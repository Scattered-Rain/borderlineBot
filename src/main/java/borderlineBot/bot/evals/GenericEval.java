package borderlineBot.bot.evals;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.game.Player;
import borderlineBot.game.Unit;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;
import borderlineBot.util.ThreatMap;
import borderlineBot.util.ThreatMap.ThreatTile;
import borderlineBot.util.ThreatMap.ThreatTileList;

/** Generic Evaluation Function */
public class GenericEval implements EvaluationFunction{
	
	/** Evaluation */
	public int evaluate(GameBoard board, Player player) {
		int score = 0;
		
		//Check Unit quantities
		int[][] units = board.countUnits(player);
		float scoreOneUnits = units[0][0]-units[1][0];
		float scoreTwoUnits = units[0][1]-units[1][1];
		
		//Check Movement
		List<Move> playerMoves = board.generateAllHypotheticalLegalMoves(player);
		List<Move> oppMoves = board.generateAllHypotheticalLegalMoves(player.getOpponent());
		float scoreMoveOptions = playerMoves.size()-oppMoves.size();
		//TODO: There are some bugs still left
		
		
		float scoreThreatOneUnits = 0;
		float scoreThreatTwoUnits = 0;
		ThreatMap ttl = new ThreatMap(board);
		ttl.update(board, 1);
		ThreatTileList[][] ttlGrid = ttl.getTtlGrid();
		for(int cy=0; cy<ttlGrid.length; cy++){
			for(int cx=0; cx<ttlGrid[cy].length; cx++){
				Point p = new Point(cx, cy);
				if(!board.getTile(p).isEmpty()){
					Tile t = board.getTile(p);
					if(ttlGrid[cy][cx].containsThreat(board, t.getPlayer().getOpponent(), 1)){
						if(t.getUnit().isUnit(Unit.ONE)){
							scoreThreatOneUnits += t.getPlayer().isSame(player)?1:-1;
						}
						else{
							scoreThreatTwoUnits += t.getPlayer().isSame(player)?1:-1;
						}
					}
				}
			}
		}
		
		
		float scoreThreatenedTilesByPlayer = 0;
		Player[] players = new Player[]{player, player.getOpponent()};
		for(int c=0; c<players.length; c++){
			for(int cy=0; cy<ttlGrid.length; cy++){
				for(int cx=0; cx<ttlGrid[cy].length; cx++){
					Point p = new Point(cx, cy);
					if(ttlGrid[cy][cx].containsThreat(board, players[c], 1)){
						boolean realThreat = false;
						for(ThreatTile tt : ttlGrid[cy][cx].getList()){
							if(tt.getOwnerTile(board).getPlayer().isSame(players[c])){
								Point orp = tt.getDirectThreatOrigin();
								if(!ttlGrid[orp.getY()][orp.getX()].containsThreat(board, players[c].getOpponent(), 1)){
									realThreat = true;
									break;
								}
							}
						}
						if(realThreat){
							float val = c==0?1:-1;
							scoreThreatenedTilesByPlayer += val;
						}
					}
				}
			}
		}
		
		
		
		
		return (int)(scoreOneUnits*1000.0f + scoreTwoUnits*1200.0f + scoreMoveOptions*5.0f + -scoreThreatOneUnits*200 + -scoreThreatTwoUnits*300 + scoreThreatenedTilesByPlayer*10);
	}
	
}
