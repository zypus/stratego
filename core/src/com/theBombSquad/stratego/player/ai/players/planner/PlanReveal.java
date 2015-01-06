package com.theBombSquad.stratego.player.ai.players.planner;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;

public class PlanReveal implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		float value = 0;
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		Unit target = board.getUnit(move.getToX(), move.getToY());
		//If Not Enemy Unit, Plan Won't Judge
		if(!target.isAir()){
			if(!target.wasRevealed(view.getCurrentTurn())){
				//TODO: Make The Punishment Equivalent To The Actual Likelihood of 
				value = calcRevBonus(view) / TheQueen.getUnitValue(self.getType()) + 1;
			}
		}
		return value;
	}
	
	
	/** Calc Bonus That Should Be Handed Out For Uncovering Opponent Piece */
	private float calcRevBonus(GameView view){
		return 40 - view.getOpponentsDefeatedUnits().size() * TheQueen.getUnitValue(Unit.UnitType.SCOUT);
	}
	

	@Override
	public void postMoveUpdate(GameView view) {}

}
