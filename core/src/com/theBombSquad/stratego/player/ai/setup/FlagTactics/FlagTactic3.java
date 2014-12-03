package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class FlagTactic3 extends FlagTactic{

	public FlagTactic3(AISetup setup) {
		super(setup);
		proceed();
	}

	public FlagTactic3() {
		super();

	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	private void proceed() {
		
		UnitPlacement toPut = new UnitPlacement(UnitType.FLAG, 0, 3, 1);
		if (Math.random() <= 0.5) {
			toPut.setUnitType(super.randomizeSL());
			super.placeUnit(toPut);
			toPut.setUnitType(UnitType.FLAG);
			toPut.setX(setup.getWidth()-1);
			super.placeUnit(toPut);
		}
		else{
			super.placeUnit(toPut);
			toPut.setUnitType(super.randomizeSL());
			toPut.setX(setup.getWidth()-1);
			super.placeUnit(toPut);
		}
		//2 bombs right side board
		toPut.setUnitType(UnitType.BOMB);
		toPut.setX(setup.getWidth()-2);
		toPut.setY(3);
		super.placeUnit(toPut);
		toPut.setY(2);
		toPut.setX(setup.getWidth()-1);
		super.placeUnit(toPut);
		
		//2 bombs left side board
		toPut.setX(1);
		toPut.setY(3);
		super.placeUnit(toPut);
		toPut.setY(2);
		toPut.setX(0);
		super.placeUnit(toPut);
		
		//4/5 left
		toPut.setUnitType(super.randomizeSL());
		toPut.setY(1);
		toPut.setX(0);
		super.placeUnit(toPut);
		toPut.setUnitType(super.randomizeSL());
		toPut.setY(2);
		toPut.setX(1);
		super.placeUnit(toPut);
		toPut.setUnitType(super.randomizeSL());
		toPut.setY(3);
		toPut.setX(2);
		super.placeUnit(toPut);
		
		//4/5 right
		toPut.setUnitType(super.randomizeSL());
		toPut.setY(1);
		toPut.setX(setup.getWidth()-1);
		super.placeUnit(toPut);
		toPut.setUnitType(super.randomizeSL());
		toPut.setY(2);
		toPut.setX(setup.getWidth()-2);
		super.placeUnit(toPut);
		toPut.setUnitType(super.randomizeSL());
		toPut.setY(3);
		toPut.setX(setup.getWidth()-3);
		super.placeUnit(toPut);
	}
	
}
