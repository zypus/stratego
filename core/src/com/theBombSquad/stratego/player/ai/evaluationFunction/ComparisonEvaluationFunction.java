package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class ComparisonEvaluationFunction implements EvaluationFunction
{

	@Override
	public float evaluate(GameBoard gamestate, PlayerID player) 
	{
		float totalPlayer;
		float totalOpponent;
		float total;
		PlayerID opponent = player.getOpponent();
		
		float a = gamestate.getAliveUnits(Unit.UnitType.MARSHAL, player);
		float b = gamestate.getAliveUnits(Unit.UnitType.GENERAL, player);
		float c = gamestate.getAliveUnits(Unit.UnitType.COLONEL, player);
		float d = gamestate.getAliveUnits(Unit.UnitType.MAJOR, player);
		float e = gamestate.getAliveUnits(Unit.UnitType.CAPTAIN, player);
		float f = gamestate.getAliveUnits(Unit.UnitType.LIEUTENANT, player);
		float g = gamestate.getAliveUnits(Unit.UnitType.SERGEANT, player);
		float h = gamestate.getAliveUnits(Unit.UnitType.SAPPER, player);
		float i = gamestate.getAliveUnits(Unit.UnitType.SCOUT, player);
		float j = gamestate.getAliveUnits(Unit.UnitType.SPY, player);
		float k = gamestate.getAliveUnits(Unit.UnitType.BOMB, player);
		float l = gamestate.getAliveUnits(Unit.UnitType.FLAG, player);
		
		float o = gamestate.getAliveUnits(Unit.UnitType.MARSHAL, opponent);
		float p = gamestate.getAliveUnits(Unit.UnitType.GENERAL, opponent);
		float q = gamestate.getAliveUnits(Unit.UnitType.COLONEL, opponent);
		float r = gamestate.getAliveUnits(Unit.UnitType.MAJOR, opponent);
		float s = gamestate.getAliveUnits(Unit.UnitType.CAPTAIN, opponent);
		float t = gamestate.getAliveUnits(Unit.UnitType.LIEUTENANT, opponent);
		float u = gamestate.getAliveUnits(Unit.UnitType.SERGEANT, opponent);
		float v = gamestate.getAliveUnits(Unit.UnitType.SAPPER, opponent);
		float w = gamestate.getAliveUnits(Unit.UnitType.SCOUT, opponent);
		float x = gamestate.getAliveUnits(Unit.UnitType.SPY, opponent);
		float y = gamestate.getAliveUnits(Unit.UnitType.BOMB, opponent);
		float z = gamestate.getAliveUnits(Unit.UnitType.FLAG, opponent);
		
		//values from thesis paper
		float valueOfPlayerMarshal = 400;
		float valueOfPlayerGeneral = 200;
		float valueOfPlayerColonel = 100;
		float valueOfPlayerMajor = 75;
		float valueOfPlayerCaptain = 50;
		float valueOfPlayerLieutenant = 25;
		float valueOfPlayerSergeant = 15;
		float valueOfPlayerSapper = 25;
		float valueOfPlayerScout = 30;
		float valueOfPlayerSpy = 200;
		float valueOfPlayerBomb = 20;
		float valueOfPlayerFlag = 10000;
		
		float valueOfOpponentMarshal = 400;
		float valueOfOpponentGeneral = 200;
		float valueOfOpponentColonel = 100;
		float valueOfOpponentMajor = 75;
		float valueOfOpponentCaptain = 50;
		float valueOfOpponentLieutenant = 25;
		float valueOfOpponentSergeant = 15;
		float valueOfOpponentSapper = 25;
		float valueOfOpponentScout = 30;
		float valueOfOpponentSpy = 200;
		float valueOfOpponentBomb = 20;
		float valueOfOpponentFlag = 10000;
		
		//first rule from thesis paper
		if (x>0)
		{
			valueOfPlayerMarshal = (valueOfPlayerMarshal*0.8f);
		}
		if (j>0)
		{
			valueOfOpponentMarshal = (valueOfOpponentMarshal*0.8f);
		}
		//second rule from thesis paper
		if (h<3)
		{
			valueOfPlayerSapper = valueOfPlayerSapper*(4-h);
		}
		if (v<3)
		{
			valueOfOpponentSapper = valueOfOpponentSapper*(4-h);
		}
		//third rule from thesis paper
		if (o>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerMarshal;
		else if (p>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerGeneral;
		else if (q>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerColonel;
		else if (r>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerMajor;
		else if (s>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerCaptain;
		else if (t>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerLieutenant;
		else if (u>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerSergeant;
		else if (v>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerSapper;
		else if (w>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerScout;
		else if (x>0)
			valueOfPlayerBomb = 0.5f*valueOfPlayerSpy;
		else
			valueOfPlayerBomb = 20;
		
		if (a>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentMarshal;
		else if (b>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentGeneral;
		else if (c>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentColonel;
		else if (d>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentMajor;
		else if (e>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentCaptain;
		else if (f>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentLieutenant;
		else if (g>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentSergeant;
		else if (h>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentSapper;
		else if (i>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentScout;
		else if (j>0)
			valueOfOpponentBomb = 0.5f*valueOfOpponentSpy;
		else
			valueOfOpponentBomb = 20;
		//fourth rule from thesis paper
		if (x>0)
		{
			valueOfPlayerMarshal = (0.5f*valueOfPlayerMarshal);
		}
		if (j>0)
		{
			valueOfOpponentMarshal = (0.5f*valueOfOpponentMarshal);
		}
		//own rule
		if (x>0 && o<1)
		{
			valueOfPlayerSpy = 10;
		}
		if (k>0 && a<1)
		{
			valueOfOpponentSpy = 10;
		}
		
		totalPlayer = valueOfPlayerMarshal*a+valueOfPlayerGeneral*b+valueOfPlayerColonel*c+valueOfPlayerMajor*d+valueOfPlayerCaptain*e+valueOfPlayerLieutenant*f+valueOfPlayerSergeant*g+valueOfPlayerSapper*h+valueOfPlayerScout*i+valueOfPlayerSpy*j+valueOfPlayerBomb*k+l*valueOfPlayerFlag;
		totalOpponent = valueOfOpponentMarshal*o+valueOfOpponentGeneral*p+valueOfOpponentColonel*q+valueOfOpponentMajor*r+valueOfOpponentCaptain*s+valueOfOpponentLieutenant*t+valueOfOpponentSergeant*u+valueOfOpponentSapper*v+valueOfOpponentScout*w+valueOfOpponentSpy*x+valueOfOpponentBomb*y+valueOfOpponentFlag*z;		
		total = totalPlayer-totalOpponent;
		return total;
	}

}
