package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.GameState;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.player.ai.evaluationFunctions.EvaluationFunctionX;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;

public class SimpleEvaluationFunction implements EvaluationFunctionX
{
	public float evaluate(GameState gamestate, PlayerID player)
	{
		PlayerID opponent = player.getOpponent();
//		float a = gamestate.getBoard().getAliveUnits(Unit.UnitType.MARSHAL, player);
//		float b = gamestate.getBoard().getAliveUnits(Unit.UnitType.GENERAL, player);
//		float c = gamestate.getBoard().getAliveUnits(Unit.UnitType.COLONEL, player);
//		float d = gamestate.getBoard().getAliveUnits(Unit.UnitType.MAJOR, player);
//		float e = gamestate.getBoard().getAliveUnits(Unit.UnitType.CAPTAIN, player);
//		float f = gamestate.getBoard().getAliveUnits(Unit.UnitType.LIEUTENANT, player);
//		float g = gamestate.getBoard().getAliveUnits(Unit.UnitType.SERGEANT, player);
//		float h = gamestate.getBoard().getAliveUnits(Unit.UnitType.SAPPER, player);
//		float i = gamestate.getBoard().getAliveUnits(Unit.UnitType.SCOUT, player);
//		float j = gamestate.getBoard().getAliveUnits(Unit.UnitType.SPY, player);
//		float k = gamestate.getBoard().getAliveUnits(Unit.UnitType.BOMB, player);
//		float l = gamestate.getBoard().getAliveUnits(Unit.UnitType.FLAG, player);
//		float a = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.MARSHAL, player);
//		float b = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.GENERAL, player);
//		float c = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.COLONEL, player);
//		float d = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.MAJOR, player);
//		float e = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.CAPTAIN, player);
//		float f = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.LIEUTENANT, player);
//		float g = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.SERGEANT, player);
//		float h = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.SAPPER, player);
//		float i = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.SCOUT, player);
//		float j = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.SPY, player);
//		float k = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.BOMB, player);
//		float l = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.FLAG, player);
		// opponent
//		float oa = gamestate.getBoard().getAliveUnits(Unit.UnitType.MARSHAL, player);
//		float ob = gamestate.getBoard().getAliveUnits(Unit.UnitType.GENERAL, player);
//		float oc = gamestate.getBoard().getAliveUnits(Unit.UnitType.COLONEL, player);
//		float od = gamestate.getBoard().getAliveUnits(Unit.UnitType.MAJOR, player);
//		float oe = gamestate.getBoard().getAliveUnits(Unit.UnitType.CAPTAIN, player);
//		float of = gamestate.getBoard().getAliveUnits(Unit.UnitType.LIEUTENANT, player);
//		float og = gamestate.getBoard().getAliveUnits(Unit.UnitType.SERGEANT, player);
//		float oh = gamestate.getBoard().getAliveUnits(Unit.UnitType.SAPPER, player);
//		float oi = gamestate.getBoard().getAliveUnits(Unit.UnitType.SCOUT, player);
//		float oj = gamestate.getBoard().getAliveUnits(Unit.UnitType.SPY, player);
//		float ok = gamestate.getBoard().getAliveUnits(Unit.UnitType.BOMB, player);
//		float ol = gamestate.getBoard().getAliveUnits(Unit.UnitType.FLAG, player);
//		float oa = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.MARSHAL, player);
//		float ob = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.GENERAL, player);
//		float oc = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.COLONEL, player);
//		float od = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.MAJOR, player);
//		float oe = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.CAPTAIN, player);
//		float of = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.LIEUTENANT, player);
//		float og = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.SERGEANT, player);
//		float oh = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.SAPPER, player);
//		float oi = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.SCOUT, player);
//		float oj = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.SPY, player);
//		float ok = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.BOMB, player);
//		float ol = gamestate.getSchrodingersBoard().getTotalProbabilityFor(Unit.UnitType.FLAG, player);
//		float a = Unit.UnitType.MARSHAL.getQuantity()-gamestate.getOwnDefeated()[Unit.UnitType.MARSHAL.ordinal()-1];
//		float b = Unit.UniType.GENERAL.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.GENERAL.ordinal() - 1];
//		float c = Unit.UnitType.COLONEL.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.COLONEL.ordinal() - 1];
//		float d = Unit.UnitType.MAJOR.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.MAJOR.ordinal() - 1];
//		float e = Unit.UnitType.CAPTAIN.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.CAPTAIN.ordinal() - 1];
//		float f = Unit.UnitType.LIEUTENANT.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.LIEUTENANT.ordinal() - 1];
//		float g = Unit.UnitType.SERGEANT.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.SERGEANT.ordinal() - 1];
//		float h = Unit.UnitType.SAPPER.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.SAPPER.ordinal() - 1];
//		float i = Unit.UnitType.SCOUT.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.SCOUT.ordinal() - 1];
//		float j = Unit.UnitType.SPY.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.SPY.ordinal() - 1];
//		float k = Unit.UnitType.BOMB.getQuantity() - gamestate.getOwnDefeated()[Unit.UnitType.BOMB.ordinal() - 1];
//		float l = 1;

		//values from thesis paper
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

		float revealedPanalty = -200;
		float revealedReward = 100;

//		float x = oj;
//		float y = oa;
//		float x = Unit.UnitType.SPY.getQuantity() - gamestate.getOpponentDefeated()[Unit.UnitType.SPY.ordinal() - 1];
//		float y = Unit.UnitType.MARSHAL.getQuantity() - gamestate.getOpponentDefeated()[Unit.UnitType.MARSHAL.ordinal() - 1];
		//first rule from thesis paper
//		if (x>0)
//		{
//			valueOfMarshal = (valueOfMarshal*0.8f);
//		}
//		//second rule from thesis paper
//		if (h<3)
//		{
//			valueOfSapper = valueOfSapper*(4-h);
//		}
//		//third rule from thesis paper
//		if (oa>0)
//			valueOfBomb = 0.5f*valueOfMarshal;
//		else if (ob>0)
//			valueOfBomb = 0.5f*valueOfGeneral;
//		else if (oc>0)
//			valueOfBomb = 0.5f*valueOfColonel;
//		else if (od>0)
//			valueOfBomb = 0.5f*valueOfMajor;
//		else if (oe>0)
//			valueOfBomb = 0.5f*valueOfCaptain;
//		else if (of>0)
//			valueOfBomb = 0.5f*valueOfLieutenant;
//		else if (og>0)
//			valueOfBomb = 0.5f*valueOfSergeant;
//		else if (oh>0)
//			valueOfBomb = 0.5f*valueOfSapper;
//		else if (oi>0)
//			valueOfBomb = 0.5f*valueOfScout;
//		else if (oj>0)
//			valueOfBomb = 0.5f*valueOfSpy;
//		else
//			valueOfBomb = 20;
//		//fourth rule from thesis paper
//		if (x>0)
//		{
//			valueOfMarshal = (0.5f*valueOfMarshal);
//		}
//
//		if (a > oa) {
//			valueOfBomb = 1.1f * valueOfMarshal;
//		}
//		if (b > ob) {
//			valueOfGeneral = 1.1f * valueOfGeneral;
//		}
//		if (c > oc) {
//			valueOfColonel = 1.1f * valueOfColonel;
//		}
//		if (d > od) {
//			valueOfMajor = 1.1f * valueOfMajor;
//		}
//		if (e > oe) {
//			valueOfCaptain = 1.1f * valueOfCaptain;
//		}
//		if (f > of) {
//			valueOfLieutenant = 1.1f * valueOfLieutenant;
//		}
//		if (g > og) {
//			valueOfSergeant = 1.1f * valueOfSergeant;
//		}
//		if (h > oh) {
//			valueOfSapper = 1.1f * valueOfSapper;
//		}
//		if (i > oi) {
//			valueOfScout = 1.1f * valueOfScout;
//		}
//		if (j > oj) {
//			valueOfSpy = 1.1f * valueOfSpy;
//		}
//		if (k > ok) {
//			valueOfBomb = 1.1f * valueOfBomb;
//		}
//		if (l > ol) {
//			valueOfFlag = 1.1f * valueOfFlag;
//		}
//
//		//own rule
//		if (x>0 && y<1)
//		{
//			valueOfSpy = 10;
//		}
//
//		int r = gamestate.getSchrodingersBoard().revealed(player);
//		int or = gamestate.getSchrodingersBoard().revealed(opponent);
//
//		float total = valueOfMarshal*a+valueOfGeneral*b+valueOfColonel*c+valueOfMajor*d+valueOfCaptain*e+valueOfLieutenant*f+valueOfSergeant*g+valueOfSapper*h+valueOfScout*i+valueOfSpy*j+valueOfBomb*k+l*valueOfFlag+r*revealedPanalty+or*revealedReward;
		return 0;
	}

//	@Override
	public float evaluate(GameBoard state, PlayerID player) {
		return 0;
	}

	@Override
	public float evaluate(SchrodingersBoard state, PlayerID player) {
		return 0;
	}
}
