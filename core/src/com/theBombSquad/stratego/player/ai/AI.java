package com.theBombSquad.stratego.player.ai;


import java.util.ArrayList;
import java.util.List;

import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;

/**
 * Abstract AI class which gives access to several utility stuff.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public abstract class AI extends Player {

	public AI(GameView gameView) {
		super(gameView);
	}
	
	protected List<Move> createAllLegalMoves(GameBoard board){
		List<Move> list = new ArrayList<Move>();
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				Unit unit = board.getUnit(cx, cy);
				if(unit.getOwner() == gameView.getPlayerID()){
					if(!unit.getType().equals(Unit.UnitType.BOMB) && !unit.getType().equals(Unit.UnitType.FLAG)){
						if(unit.getType().equals(Unit.UnitType.SCOUT)){
							//TODO: Add Scout Movability Later
						}
						else{
							for(int xx=-1; xx<=1; xx+=2){
								if(walkable(board, cx+xx, cy)){
									list.add(new Move(cx, cy, cx+xx, cy));
								}
							}
							for(int yy=-1; yy<=1; yy+=2){
								if(walkable(board, cx, cy+yy)){
									list.add(new Move(cx, cy, cx, cy+yy));
								}
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	protected boolean walkable(GameBoard board, int x, int y){
		return board.isInBounds(x, y) && (gameView.isEnemy(x, y) || gameView.isAir(x, y));
	}
	
	
	
}
