package com.theBombSquad.stratego.player.ai.players.planner;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class PlanFleeDefeatableUnitFromKnownStrongerThreat implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		
		
		try{
			
		}catch(Exception ex){}
		
		return 0;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		//Nothing to do here yolo
	}

}
