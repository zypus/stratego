package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;

public class PlanDiscourageLoops implements Plan{
	
	private static final int FACTOR = 10;

	@Override
	public float evaluateMove(GameView view, Move move) {
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		
		float repeatPunishment = 0;
		for(int c=0; c<view.getCurrentTurn(); c++){
			if(view.getMove(c)!=null && move.isSameMovementAs(view.getMove(c))){
				repeatPunishment += (((float)c)/((float)view.getCurrentTurn())) * FACTOR;
			}
		}
		
		return -repeatPunishment;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		// TODO Auto-generated method stub
		
	}

}
