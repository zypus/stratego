package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class FlagTactic2 extends FlagTactic{

	public FlagTactic2(AISetup setup){
		super(setup);
		proceed();
	}

	private void proceed() {
	
		possiblePlacements = new ArrayList<UnitPlacement>();
		for (int j = 1; j < setup.getWidth()-1; j++) {
			possiblePlacements.add(new UnitPlacement(UnitType.FLAG, 3, j, 1));
		}
		UnitPlacement toPut = super.randomizeUnitPlacement();
		//Flag
		super.placeUnit(toPut);
		//Lowerleft bomb
		toPut.setY(toPut.getY()-1);
		toPut.setUnitType(UnitType.BOMB);
		super.placeUnit(toPut);
		//Middle bomb
		toPut.setY(toPut.getY()+1);
		toPut.setX(toPut.getX()-1);
		super.placeUnit(toPut);
		//Lowerright bomb
		toPut.setX(toPut.getX()+1);
		toPut.setY(toPut.getY()+1);
		super.placeUnit(toPut);
		//Upperright bomb
		toPut.setX(toPut.getX()-2);
		super.placeUnit(toPut);
		//Upperleft bomb
		toPut.setY(toPut.getY()-2);
		super.placeUnit(toPut);
		//4/5
		toPut.setY(toPut.getY()+1);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		toPut.setY(toPut.getY()-1);
		toPut.setX(toPut.getX()+1);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		toPut.setY(toPut.getY()+2);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		
		ArrayList<UnitPlacement> posPlace = new ArrayList<UnitPlacement>();
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, 2, toPut.getY()+1, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, 3, j, 1));
		
	}
	
}
