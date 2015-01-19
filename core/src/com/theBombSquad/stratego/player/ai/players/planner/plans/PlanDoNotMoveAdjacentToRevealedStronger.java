package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;

public class PlanDoNotMoveAdjacentToRevealedStronger implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		
		float value = 0;
		
		Unit self = view.getCurrentState().getUnit(move.getFromX(), move.getFromY());
		
		//If Unit Hasn't Moved Before And Hasn't Been Revealed Before We Do Not Encourage Fleeing (Plays Bomb)
		if(!(self.getRevealedInTurn()<view.getCurrentTurn() || self.getMovedInTurn()<view.getCurrentTurn())){
			return 0;
		}
		
		//Check New Location
		int x = move.getToX();
		int y = move.getToY();
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
		
		int newThreats = knownThreats;
		
		//Check Old Location
		x = move.getFromX();
		y = move.getFromY();
		knownThreats = 0;
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
		
		if(newThreats > knownThreats){
			value = -TheQueen.getUnitValue(self.getType());
		}
		
		return value;
	}

	@Override
	public void postMoveUpdate(GameView view) {}
	
	private boolean couldDie(GameView view, Move move, int x, int y){
		Unit self = view.getCurrentState().getUnit(move.getFromX(), move.getFromY());
		if(view.getCurrentState().isInBounds(x, y)){
			Unit target = view.getUnit(x, y);
			if(!target.isAir() && !target.isLake() && !target.isUnknown() && target.getOwner().equals(view.getOpponentID())){
				if(target.getType().getRank()==Unit.UnitType.BOMB.getRank()){
					return false;
				}
				if(Encounter.resolveFight(target.getType(), self.getType()) != Encounter.CombatResult.VICTORIOUS_DEFENSE){
					return true;
				}
			}
		}
		return false;
	}

}
