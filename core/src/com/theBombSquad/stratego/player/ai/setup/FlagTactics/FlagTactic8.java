package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class FlagTactic8 extends FlagTactic{

	public FlagTactic8(AISetup setup) {
		super(setup);
		proceed();
	}

	public FlagTactic8() {
		super();

	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	private void proceed() {
		empty();
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
			if(Math.random()<0.5){
				toPut.setX(2);
			}
			else{
				toPut.setX(3);
			}
			toPut.setY(0);
			super.placeUnit(toPut);
			toPut.setY(1);
			toPut.setX(toPut.getX()+1);
			super.placeUnit(toPut);
			toPut.setY(2);
			toPut.setX(toPut.getX()+1);
			super.placeUnit(toPut);
			toPut.setY(3);
			toPut.setX(toPut.getX()+1);
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
			if(Math.random()<0.5){
				toPut.setX(7);
			}
			else{
				toPut.setX(6);
			}
			toPut.setY(0);
			super.placeUnit(toPut);
			toPut.setY(1);
			toPut.setX(toPut.getX()-1);
			super.placeUnit(toPut);
			toPut.setY(2);
			toPut.setX(toPut.getX()-1);
			super.placeUnit(toPut);
			toPut.setY(3);
			toPut.setX(toPut.getX()-1);
			super.placeUnit(toPut);
		}	
	}
}
