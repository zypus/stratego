package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;


//TODO: Work With The Fabian Board Here Once That's Finished

public class PlanKillWeakerHidden implements Plan{
	
	private UnitType type;
	
	
	@Override
	public float evaluateMove(GameView view, Move move) {
		
		return 0;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		
	}

}
