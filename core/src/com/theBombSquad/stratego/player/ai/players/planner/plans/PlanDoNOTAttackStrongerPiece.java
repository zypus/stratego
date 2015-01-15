package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;

public class PlanDoNOTAttackStrongerPiece implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		float value = 0;
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		Unit target = board.getUnit(move.getToX(), move.getToY());
		if(!target.isAir() && !target.isLake() && !target.isUnknown() && target.getOwner().equals(view.getOpponentID())){
			if(!Encounter.resolveFight(self.getType(), target.getType()).equals(Encounter.CombatResult.VICTORIOUS_ATTACK)){
				System.out.println("Fuck, That Thing's Strong ... Will I Attack?");
				value = -TheQueen.getUnitValue(target.getType())*10000;
			}
		}
		return value;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		// TODO Auto-generated method stub
		
	}

}
