package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;

public class PlanStrongestPieceAttackPlan implements Plan{
	
	private static final float ATTACK_ADJACENT = 20;
	
	private UnitType strongestPiece;
	
	@Override
	public float evaluateMove(GameView view, Move move) {
		float value = 0;
		if(strongestPiece!=null){
			GameBoard board = view.getCurrentState();
			Unit self = board.getUnit(move.getFromX(), move.getFromY());
			Unit target = board.getUnit(move.getToX(), move.getToY());
			if(self.getType().equals(strongestPiece)){
				if(target.isUnknown() && target.wasMoved(view.getCurrentTurn())){
					value += ATTACK_ADJACENT;
				}
				else if(target.getOwner().equals(view.getOpponentID()) && !target.getType().equals(Unit.UnitType.BOMB)){
					value += ATTACK_ADJACENT;
				}
				else{
					//TODO: Pathfind to closest killable Unit
				}
			}
		}
		return value;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		//Determines Strongest Still Living Unit And Whether The Queen Is In Control Of It
		for(int c=0; c<12; c++){
			int ui = 12 - c - 1;
			UnitType type = Unit.getUnitTypeOfRank(ui);
			if(!(type.equals(Unit.UnitType.BOMB)||type.equals(Unit.UnitType.FLAG))){
				if(view.getNumberOfDefeatedOwnUnits(ui)<type.getQuantity()){
					if(view.getNumberOfDefeatedOpponentUnits(ui)==type.getQuantity()){
						this.strongestPiece = type;
						return;
					}
				}
			}
		}
		this.strongestPiece = null;
	}
	
}
