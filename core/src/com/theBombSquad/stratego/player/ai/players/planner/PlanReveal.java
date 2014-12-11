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
				value = calcRevBonus(view) * view.getCurrentState().getAliveUnits(self.getType(), view.getPlayerID());
			}
		}
		return value;
	}
	
	
	/** Calc Bonus That Should Be Handed Out For Uncovering Opponent Piece */
	private float calcRevBonus(GameView view){
		//See How Many Units Of Opponent Of Certain Type Have Been Revealed
		int opponentHiddenArmySize = 0;
		int[] revealedOfType = new int[12];
		GameBoard board = view.getCurrentState();
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				Unit unit = board.getUnit(cx, cy);
				if(unit.getOwner().equals(view.getOpponentID())){
					if(unit.wasRevealed(view.getCurrentTurn())){
						revealedOfType[unit.getType().getRank()]++;
					}
					else{
						opponentHiddenArmySize++;
					}
				}
			}
		}
		//Default To Scout, Weakest UnitType As Strongest Hidden Alive Opponent Unit
		UnitType strongestAliveHiddenUnit = Unit.UnitType.SCOUT;
		for(int c=10; c>=0; c--){
			if(board.getAliveUnits(Unit.getUnitTypeOfRank(c), view.getOpponentID())>0){
				if(revealedOfType[c]<Unit.getUnitTypeOfRank(c).getQuantity()){
					strongestAliveHiddenUnit = Unit.getUnitTypeOfRank(c);
				}
			}
		}
		//Calculate Value Of Maybe Revealing This Unit
		return TheQueen.getUnitValue(strongestAliveHiddenUnit)/opponentHiddenArmySize;
	}
	

	@Override
	public void postMoveUpdate(GameView view) {
		// TODO Auto-generated method stub
	}

}
