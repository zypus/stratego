package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class FlagTactic2 extends FlagTactic{

	public FlagTactic2(AISetup setup) {
		super(setup);
		proceed();
	}

	public FlagTactic2() {
		super();

	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	private void proceed() {
	
		possiblePlacements = new ArrayList<UnitPlacement>();
		for (int j = 1; j < setup.getWidth()-1; j++) {
			possiblePlacements.add(new UnitPlacement(UnitType.FLAG, j, 3, 1));
		}
		UnitPlacement toPut = super.randomizeUnitPlacement();
		//Flag
		int x = toPut.getX();
		int y = toPut.getY();
		super.placeUnit(toPut);
		//Lowerleft bomb
		toPut.setX(x-1);
		toPut.setUnitType(UnitType.BOMB);
		super.placeUnit(toPut);
		//Middle bomb
		toPut.setX(x);
		toPut.setY(y-1);
		super.placeUnit(toPut);
		//Lowerright bomb
		toPut.setY(y);
		toPut.setX(x+1);
		super.placeUnit(toPut);
		//Upperright bomb
		toPut.setY(y-2);
		super.placeUnit(toPut);
		//Upperleft bomb
		toPut.setX(x-1);
		super.placeUnit(toPut);
		//4/5
		//up
		toPut.setX(x);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		//left
		toPut.setX(x-1);
		toPut.setY(y-1);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		//right
		toPut.setX(x+1);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		
		//Place last bomb
		//make possible placemnets empty
		super.empty();
		
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y-2, x-1, 3));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y-2, x-2, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y-2, x-3, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y-1, x-3, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y, x-3, 3));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y+1, x-3, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y+2, x-3, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y+2, x-2, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, y+2, x-1, 3));
		UnitPlacement toPut2 = super.randomizeUnitPlacement();
		super.placeUnit(toPut2);
	}
	
}
