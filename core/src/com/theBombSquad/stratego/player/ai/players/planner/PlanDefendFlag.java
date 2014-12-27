package com.theBombSquad.stratego.player.ai.players.planner;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class PlanDefendFlag implements Plan{

	@Override
	public float evaluateMove(GameView view, Move move) {
		GameBoard board = view.getCurrentState();
		Unit self = board.getUnit(move.getFromX(), move.getFromY());
		
		//Figure Out Where The Flag Is
		int flagX = -1;
		int flagY = -1;
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				if(board.getUnit(cx, cy).getOwner().equals(view.getPlayerID()) && board.getUnit(cx, cy).getType().equals(Unit.UnitType.FLAG)){
					flagX = cx;
					flagY = cy;
					break;
				}
			}
			if(flagX!=-1){
				break;
			}
		}
		
		//Reward Points If Unit Moves Next To Flag
		if((Math.abs(flagX-move.getToX())+Math.abs(flagY-move.getToY()))<=1){
			return TheQueen.getUnitValue(self.getType())*0.5f;
		}
		//Punishment For Moving A Unit Away FromAround The Flag
		if((Math.abs(flagX-move.getFromX())+Math.abs(flagY-move.getFromY()))<=1){
			return -TheQueen.getUnitValue(self.getType())*0.5f;
		}
		
		//Plan Does Not Care
		return 0;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		// TODO Auto-generated method stub
		
	}

}
