package com.theBombSquad.stratego.player.ai.evaluationFunctions;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersUnit;

public class SimpleFunctionOfEvaluation implements EvaluationFunctionX{

	public float evaluate(SchrodingersBoard board, PlayerID player) {



		float valueOfMarshal = 400;
		float valueOfGeneral = 200;
		float valueOfColonel = 100;
		float valueOfMajor = 75;
		float valueOfCaptain = 50;
		float valueOfLieutenant = 25;
		float valueOfSergeant = 15;
		float valueOfSapper = 25;
		float valueOfScout = 30;
		float valueOfSpy = 200;
		float valueOfBomb = 20;
		float valueOfFlag = 10000;

		float[] unitValues = new float[]{1000f, 200f, 30f, 25f, 15f, 25f, 50f, 75f, 100f, 200f, 400f, 20f};

		float totalValue = 0;


		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				SchrodingersUnit u = board.getUnit(cx, cy);
				if(u.isActualUnit()){
					if(u.getOwner().equals(player)){
						if(u.unitIsKnown()){
							totalValue += unitValues[u.getKnownUnit().getRank()];
						}
						for(int c=0; c<unitValues.length; c++){
							totalValue += unitValues[c]*u.getProbabilityFor(Unit.getUnitTypeOfRank(c));
						}
					}
					else{
						if(u.unitIsKnown()){
							totalValue -= unitValues[u.getKnownUnit().getRank()];
						}
						for(int c=0; c<unitValues.length; c++){
							totalValue -= unitValues[c]*u.getProbabilityFor(Unit.getUnitTypeOfRank(c));
						}
					}
				}
			}
		}


		return totalValue;
	}

	@Override
	public float evaluate(GameView gameView, Move move) {
		// TODO Auto-generated method stub
		return 0;
	}

}
