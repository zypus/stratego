package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Defensive;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Middle;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Ofensive;
import com.theBombSquad.stratego.player.ai.setup.Strategy;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

import java.util.ArrayList;

public class FlagTactic10 extends FlagTactic{

	public FlagTactic10(AISetup setup) {
		super(setup);
		proceed();
		addStrategies();
	}

	public FlagTactic10() {
		super();
		addStrategies();
	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	protected void addStrategies() {
		strategies = new ArrayList<Strategy>();
		strategies.add(new Defensive());
		strategies.add(new Ofensive());
		strategies.add(new Middle());
	}

	private void proceed() {

		//place 4's in corners
		UnitPlacement toPut = new UnitPlacement(super.randomizeSL(), 0, 3, 1);
		super.placeUnit(toPut);
		toPut.setUnitType(super.randomizeSL());
		toPut.setX(9);
		super.placeUnit(toPut);
		//place bomb above
		toPut.setUnitType(UnitType.BOMB);
		toPut.setY(2);
		super.placeUnit(toPut);
		toPut.setX(0);
		super.placeUnit(toPut);
		//place flag and rest
		if(Math.random() < 0.5){
		//left
			//bomb above flag
			toPut.setX(1);
			super.placeUnit(toPut);
			//flag
			toPut.setY(3);
			toPut.setUnitType(UnitType.FLAG);
			super.placeUnit(toPut);
			//bomb next to flag
			toPut.setX(2);
			toPut.setUnitType(UnitType.BOMB);
			super.placeUnit(toPut);
			//bombs in entrance
			toPut.setY(0);
			double random = Math.random();
			if(Math.random()<0.5){
				toPut.setX(0);
				super.placeUnit(toPut);
				if(random < 0.5){
					toPut.setX(1);
					super.placeUnit(toPut);
				}
			}
			else{
				toPut.setX(1);
				super.placeUnit(toPut);
				if(random < 0.5){
					toPut.setX(0);
					super.placeUnit(toPut);
				}
			}
			//place the 4/5s
			toPut.setY(1);
			toPut.setUnitType(super.randomizeSL());
			super.placeUnit(toPut);
			toPut.setX(0);
			toPut.setUnitType(super.randomizeSL());
			super.placeUnit(toPut);
			//place bomb next to 4 in right corner
			if(random >= 0.5){
				toPut.setUnitType(UnitType.BOMB);
				toPut.setY(3);
				toPut.setX(8);
				super.placeUnit(toPut);
			}
		}
		else{
		//right
			//bomb above flag
			toPut.setX(8);
			super.placeUnit(toPut);
			//flag
			toPut.setY(3);
			toPut.setUnitType(UnitType.FLAG);
			super.placeUnit(toPut);
			//bomb next to flag
			toPut.setX(7);
			toPut.setUnitType(UnitType.BOMB);
			super.placeUnit(toPut);
			//bombs in entrance
			double random = Math.random();
			toPut.setY(0);
			if(Math.random()<0.5){
				toPut.setX(9);
				super.placeUnit(toPut);
				if(random < 0.5){
					toPut.setX(8);
					super.placeUnit(toPut);
				}
			}
			else{
				toPut.setX(8);
				super.placeUnit(toPut);
				if(random < 0.5){
					toPut.setX(9);
					super.placeUnit(toPut);
				}
			}
			//place the 4/5s
			toPut.setY(1);
			toPut.setUnitType(super.randomizeSL());
			super.placeUnit(toPut);
			toPut.setX(9);
			toPut.setUnitType(super.randomizeSL());
			super.placeUnit(toPut);
			//place bomb next to 4 in right corner
			if(random >= 0.5){
				toPut.setUnitType(UnitType.BOMB);
				toPut.setY(3);
				toPut.setX(1);
				super.placeUnit(toPut);
			}
			super.placeUnit(toPut);
		}
	}
}
