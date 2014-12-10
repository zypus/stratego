package com.theBombSquad.stratego.player.ai.players.planner;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class PlanPunishUnitReveals implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		float revealHiddenUnitPenalty = 0;
		
		//Check For Complete Reveals:
		//Check Whether Scout Move
		if(Math.abs(move.getFromX()-move.getToX())>=2 || Math.abs(move.getFromY()-move.getToY())>=2){
			revealHiddenUnitPenalty = TheQueen.getUnitValue(self.getType()) * 0.1f;
		}
		else{
			Unit target = board.getUnit(move.getToX(), move.getToY());
			//If Not Enemy Unit, No Encounter, No Revelation
			if(target.isAir()){
				revealHiddenUnitPenalty = 0;
			}
			//Enemy Is Unit
			else{
				//If Unit Has Been Revealed Before
				if(!self.wasRevealed(view.getCurrentTurn())){
					revealHiddenUnitPenalty = TheQueen.getUnitValue(self.getType()) * 0.1f;
				} 
			}
		}
		
		//Punishment For Moving When Not before Moving:
		if(!self.wasMoved(view.getCurrentTurn())){
			revealHiddenUnitPenalty += TheQueen.getUnitValue(Unit.UnitType.BOMB);
		}
		
		//As This Is Punishment The Value Should Be Subtracted From Complete
		return -revealHiddenUnitPenalty;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		// Nope
	}

}
