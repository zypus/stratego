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
							addScoutMoves(list,cx, cy);
						}
						else{
							for(int xx=-1; xx<=1; xx+=2){
								if(walkable(board, cx+xx, cy)){
									Move move = new Move(cx, cy, cx+xx, cy);
									if(this.gameView.validateMove(move)){
										list.add(move);
									}
								}
							}
							for(int yy=-1; yy<=1; yy+=2){
								if(walkable(board, cx, cy+yy)){
									Move move = new Move(cx, cy, cx, cy+yy);
									if(this.gameView.validateMove(move)){
										list.add(move);
									}
								}
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	private void addScoutMoves(List<Move> list, int cx, int cy) {
		// TODO Auto-generated method stub
		boolean up=true;
		boolean left=true;
		boolean right=true;
		boolean down=true;
		int counter=1; 
		while(up){
			Move move=new Move(cx,cy,cx+counter,cy);
			if(gameView.validateMove(move)){
				list.add(new Move(cx,cy,cx+counter,cy));
				counter++;
			}
			else{
				counter=1;
				up=false;
			}
		}
		while(down){
			Move move=new Move(cx,cy,cx-counter,cy);
			if(gameView.validateMove(move)){
				list.add(new Move(cx,cy,cx-counter,cy));
				counter++;
			}
			else{
				counter=1;
				down=false;
			}
		}
		while(left){
			Move move=new Move(cx,cy,cx,cy-counter);
			if(gameView.validateMove(move)){
				list.add(new Move(cx,cy,cx,cy-counter));
				counter++;
			}
			else{
				counter=1;
				left=false;
			}
		}
		while(right){
			Move move=new Move(cx,cy,cx,cy+counter);
			if(gameView.validateMove(move)){
				list.add(new Move(cx,cy,cx,cy+counter));
				counter++;
			}
			else{
				counter=1;
				right=false;
			}
		}
	}

	protected boolean walkable(GameBoard board, int x, int y){
		return board.isInBounds(x, y) && (gameView.isEnemy(x, y) || gameView.isAir(x, y));
	}
	
	
	
}
