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
		//If Not Unknown Enemy Unit, Plan Won't Judge
		if(target.isUnknown()){
			value = calcRevBonus(view) - TheQueen.getUnitValue(self.getType());
		}
		return value;
	}
	
	
	/** Calc Bonus That Should Be Handed Out For Uncovering Opponent Piece */
	private float calcRevBonus(GameView view){
		GameBoard board = view.getCurrentState();
		int[] opponentUnitUncovered = new int[12];
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				Unit unit = board.getUnit(cx, cy);
				if(unit.getOwner()==view.getOpponentID()){
					if(!unit.isUnknown()){
						opponentUnitUncovered[unit.getType().getRank()] += 1;
					}
				}
			}
		}
		float bonus = 0;
		int totalNumber = 0;
		for(int c=0; c<12; c++){
			UnitType type = Unit.getUnitTypeOfRank(c);
			int opponentFeasible = type.getQuantity() - opponentUnitUncovered[c] - view.getNumberOfDefeatedOpponentUnits(c);
			totalNumber += opponentFeasible;
			bonus += TheQueen.getUnitValue(type) * opponentFeasible;
		}
		return bonus/totalNumber;
	}
	

	@Override
	public void postMoveUpdate(GameView view) {}

}
