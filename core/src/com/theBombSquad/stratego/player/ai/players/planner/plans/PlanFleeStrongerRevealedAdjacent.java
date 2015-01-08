package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;

public class PlanFleeStrongerRevealedAdjacent implements Plan{
	
	private static final float MULTIPLIER = 0.7f;

	@Override
	public float evaluateMove(GameView view, Move move) {
		Unit self = view.getCurrentState().getUnit(move.getFromX(), move.getFromY());
		
		int x = move.getFromX();
		int y = move.getFromY();
		int knownThreats = 0;
		if(couldDie(view, move, x, y-1)){
			knownThreats++;
		}
		if(couldDie(view, move, x+1, y)){
			knownThreats++;
		}
		if(couldDie(view, move, x, y+1)){
			knownThreats++;
		}
		if(couldDie(view, move, x-1, y)){
			knownThreats++;
		}
		
		//Evaluate
		if(knownThreats>0){
			return -TheQueen.getUnitValue(self.getType())*MULTIPLIER;
		}
		else{
			return 0;
		}
	}

	@Override
	public void postMoveUpdate(GameView view) {}
	
	private boolean couldDie(GameView view, Move move, int x, int y){
		Unit self = view.getCurrentState().getUnit(move.getFromX(), move.getFromY());
		if(view.getCurrentState().isInBounds(x, y)){
			Unit target = view.getUnit(x, y);
			if(!target.isAir() && !target.isLake() && !target.isUnknown() && target.getOwner().equals(view.getOpponentID())){
				if(Encounter.resolveFight(self.getType(), target.getType()) == Encounter.CombatResult.VICTORIOUS_ATTACK){
					return true;
				}
			}
		}
		return false;
	}

}
