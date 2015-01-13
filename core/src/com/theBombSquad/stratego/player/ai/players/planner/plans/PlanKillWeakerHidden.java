package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.Encounter.CombatResult;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIUnit;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;
import com.theBombSquad.stratego.player.ai.players.planner.TheQueen;

public class PlanKillWeakerHidden implements Plan{
	
	private static final float NEGATIVITY_BIAS = 5.2f;
	
	private AIGameState state;
	
	@Override
	public float evaluateMove(GameView view, Move move) {
		float value = 0;
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		Unit target = board.getUnit(move.getToX(), move.getToY());
		if(target.isUnknown()){
			AIUnit opp = state.getAIUnit(move.getToX(), move.getToY());
			float winPoints = 0;
			float loseProb = 0;
			for(int c=0; c<12; c++){
				UnitType type = Unit.getUnitTypeOfRank(c);
				if(Encounter.resolveFight(self.getType(), target.getType())==CombatResult.VICTORIOUS_ATTACK){
					winPoints += opp.getProbabilityFor(type) * TheQueen.getUnitValue(type);
				}
				else{
					loseProb += opp.getProbabilityFor(type);
				}
			}
			value = winPoints - ((TheQueen.getUnitValue(self.getType())*loseProb)*NEGATIVITY_BIAS);
		}
		if(value<0){
			value = 0;
		}
		return value;
	}
	
	
	@Override
	public void postMoveUpdate(GameView view) {
		this.state = AI.createAIGameState(view);
	}

}
