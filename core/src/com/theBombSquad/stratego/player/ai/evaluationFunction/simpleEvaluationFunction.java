package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

public class simpleEvaluationFunction implements evaluationFunction 
{
	public float evaluate(GameBoard gamestate, PlayerID player) 
	{
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
		float x = gamestate.getAliveUnits(Unit.UnitType.SPY, opponent);
		float y = gamestate.getAliveUnits(Unit.UnitType.MARSHAL, opponent);
		//first rule from thesis paper
		if (x>0)
		{
			valueOfMarshal = (valueOfMarshal*0.8f);
		}
		//second rule from thesis paper
		if (h<3)
		{
			valueOfSapper = valueOfSapper*(4-h);
		}
		//third rule from thesis paper
		if (a>0)
			valueOfBomb = 0.5f*a;
		else if (b>0)
			valueOfBomb = 0.5f*b;
		else if (c>0)
			valueOfBomb = 0.5f*c;
		else if (d>0)
			valueOfBomb = 0.5f*d;
		else if (e>0)
			valueOfBomb = 0.5f*e;
		else if (f>0)
			valueOfBomb = 0.5f*f;
		else if (g>0)
			valueOfBomb = 0.5f*g;
		else if (h>0)
			valueOfBomb = 0.5f*h;
		else if (i>0)
			valueOfBomb = 0.5f*i;
		else if (j>0)
			valueOfBomb = 0.5f*j;
		else
			valueOfBomb = 20;
		//fourth rule from thesis paper
		if (x>0)
		{
			valueOfMarshal = (0.5f*valueOfMarshal);
		}
		//own rule
		if (x>0 && y<1)
		{
			valueOfSpy = 10;
		}
		
		float total = valueOfMarshal*a+valueOfGeneral*b+valueOfColonel*c+valueOfMajor*d+valueOfCaptain*e+valueOfLieutenant*f+valueOfSergeant*g+valueOfSapper*h+valueOfScout*i+valueOfSpy*j+valueOfBomb*k;
		return total;
	}	
	
}
