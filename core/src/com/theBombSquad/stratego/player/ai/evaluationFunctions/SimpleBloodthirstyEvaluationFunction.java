package com.theBombSquad.stratego.player.ai.evaluationFunctions;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersUnit;

public class SimpleBloodthirstyEvaluationFunction implements EvaluationFunctionX
{
	@Override
	public float evaluate(SchrodingersBoard board, PlayerID player) {
		
		float[] unitValues = new float[]{1000f, 200f, 30f, 25f, 15f, 25f, 50f, 75f, 100f, 200f, 400f, 20f};
		
		float totalValue = 0;
		
		float[] totalProbOfUnit = new float[12];
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				SchrodingersUnit u = board.getUnit(cx, cy);
				if(u.isActualUnit()){
					if(!u.getOwner().equals(player)){
						for(int c=0; c<unitValues.length; c++){
							totalProbOfUnit[c] += u.getProbabilityFor(Unit.getUnitTypeOfRank(c));
						}
					}
				}
			}
		}
		
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				SchrodingersUnit u = board.getUnit(cx, cy);
				if(u.isActualUnit()){
					if(u.getOwner().equals(player)){
						for(int c=0; c<unitValues.length; c++){
							totalValue += unitValues[c]*u.getProbabilityFor(Unit.getUnitTypeOfRank(c));
						}
					}
					else{
						for(int c=0; c<unitValues.length; c++){
							totalValue -= unitValues[c]*(u.getProbabilityFor(Unit.getUnitTypeOfRank(c))/totalProbOfUnit[c])*1.1f;
						}
					}
				}
			}
		}
		
		
		return totalValue;
	}

}
