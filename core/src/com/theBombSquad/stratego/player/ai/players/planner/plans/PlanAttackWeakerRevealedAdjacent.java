package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;

/** Plan Concerned With Simply Attacking A Revealed Weaker Opponent Unit */
public class PlanAttackWeakerRevealedAdjacent implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		Unit target = board.getUnit(move.getToX(), move.getToY());
		if(!target.isAir() && !target.isLake() && !target.isUnknown() && target.getOwner().equals(view.getOpponentID())){
			if(Encounter.resolveFight(self.getType(), target.getType()) == Encounter.CombatResult.VICTORIOUS_ATTACK){
				return TheQueen.getUnitValue(target.getType());
			}
			else{
				return -TheQueen.getUnitValue(self.getType());
			}
		}
		return 0;
	}

	@Override
	public void postMoveUpdate(GameView view) {}

}
