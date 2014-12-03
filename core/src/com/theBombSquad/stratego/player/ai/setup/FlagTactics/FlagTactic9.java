package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class FlagTactic9 extends FlagTactic{
	
	public FlagTactic9(AISetup setup) {
		super(setup);
		proceed();
	}

	public FlagTactic9() {
		super();

	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	private void proceed() {
		
		UnitPlacement toPut = new UnitPlacement(UnitType.FLAG, 0, 3, 1);
		if (Math.random() <= 0.5) {
		//left
			super.placeUnit(toPut);
			//2 bombs left side board
			toPut.setUnitType(UnitType.BOMB);
			toPut.setX(1);
			super.placeUnit(toPut);
			toPut.setY(2);
			toPut.setX(0);
			super.placeUnit(toPut);
			//row of bombs
			toPut.setX(0);
			toPut.setY(0);
			super.placeUnit(toPut);
			toPut.setY(1);
			toPut.setX(1);
			super.placeUnit(toPut);
			toPut.setY(2);
			toPut.setX(2);
			super.placeUnit(toPut);
			toPut.setY(3);
			toPut.setX(3);
			super.placeUnit(toPut);
			//row of 4/5s
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
		}
		else{
		//right
			toPut.setX(setup.getWidth()-1);
			super.placeUnit(toPut);
			//2 bombs right side
			toPut.setUnitType(UnitType.BOMB);
			toPut.setX(setup.getWidth()-2);
			super.placeUnit(toPut);
			toPut.setY(2);
			toPut.setX(setup.getWidth()-1);
			super.placeUnit(toPut);
			//row of bombs
			toPut.setX(9);
			toPut.setY(0);
			super.placeUnit(toPut);
			toPut.setY(1);
			toPut.setX(8);
			super.placeUnit(toPut);
			toPut.setY(2);
			toPut.setX(7);
			super.placeUnit(toPut);
			toPut.setY(3);
			toPut.setX(6);
			super.placeUnit(toPut);
			//row of 4/5s
			toPut.setUnitType(super.randomizeSL());
			toPut.setY(1);
			toPut.setX(9);
			super.placeUnit(toPut);
			toPut.setUnitType(super.randomizeSL());
			toPut.setY(2);
			toPut.setX(8);
			super.placeUnit(toPut);
			toPut.setUnitType(super.randomizeSL());
			toPut.setY(3);
			toPut.setX(7);
			super.placeUnit(toPut);
		}	
	}
}
