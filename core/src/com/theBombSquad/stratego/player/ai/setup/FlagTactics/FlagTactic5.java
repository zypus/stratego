package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.Strategy;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Defensive;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Middle;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Ofensive;

public class FlagTactic5 extends FlagTactic{

	public FlagTactic5(AISetup setup) {
		super(setup);
		proceed();
		addStrategies();
	}

	public FlagTactic5() {
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
		
		UnitPlacement toPut = new UnitPlacement(UnitType.FLAG,  0,3, 1);
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
			//4/5 left
			if(Math.random() <= 0.5){
				toPut.setUnitType(super.randomizeSL());
				toPut.setY(1);
				toPut.setX(0);
				super.placeUnit(toPut);
			}
			toPut.setUnitType(super.randomizeSL());
			toPut.setY(2);
			toPut.setX(1);
			super.placeUnit(toPut);
			if(Math.random() <= 0.8){
				toPut.setUnitType(super.randomizeSL());
				toPut.setY(3);
				toPut.setX(2);
				super.placeUnit(toPut);
			}
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
			//4/5 right
			if(Math.random() <= 0.5){
				toPut.setUnitType(super.randomizeSL());
				toPut.setY(1);
				toPut.setX(setup.getWidth()-1);
				super.placeUnit(toPut);
			}
			toPut.setUnitType(super.randomizeSL());
			toPut.setY(2);
			toPut.setX(setup.getWidth()-2);
			super.placeUnit(toPut);
			if(Math.random() <= 0.8){
				toPut.setUnitType(super.randomizeSL());
				toPut.setY(3);
				toPut.setX(setup.getWidth()-3);
				super.placeUnit(toPut);
			}
		}
		//Now, block of 4 pieces:
		//bomb
		super.empty();

		for(int i = 0; i < setup.getWidth(); i++){ 
			if(super.isFree(i,3)){
				possiblePlacements.add(new UnitPlacement(UnitType.BOMB, i,3, 1));
			}
		}
		UnitPlacement unitToPut = super.randomizeUnitPlacement();
		super.placeUnit(unitToPut);
		//4/5 above bomb
		unitToPut.setY(2);
		unitToPut.setUnitType(super.randomizeSL());
		super.placeUnit(unitToPut);
		
		//check if other 4/5 fits next to bomb
		unitToPut.setUnitType(super.randomizeSL());
		unitToPut.setY(3);
		int x = unitToPut.getX();
		if(Math.random()<=0.5){
			unitToPut.setX(x+1);
			if(super.isFree(x+1, 3)){//?
				super.placeUnit(unitToPut);
			}
		}
		else {
			unitToPut.setX(x-1);
			if(super.isFree(x-1,3 )){
				super.placeUnit(unitToPut);
			}
			else{	if(super.isFree(x+1,3)){
						unitToPut.setX(x+1);
						super.placeUnit(unitToPut);
				}
			}
		}
		unitToPut.setY(2);
		unitToPut.setUnitType(UnitType.BOMB);
		super.placeUnit(unitToPut);
		
		//2 bombs to cut off entrance
		super.empty();
		
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, 0, 0, 1));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, 4, 0, 3));
		possiblePlacements.add(new UnitPlacement(UnitType.BOMB, 8, 0, 1));
		
		unitToPut = super.randomizeUnitPlacement();
		super.placeUnit(unitToPut);
		unitToPut.setX(unitToPut.getX()+1);
		super.placeUnit(unitToPut);
	}
}
