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

public class FlagTactic6 extends FlagTactic{

	public FlagTactic6(AISetup setup) {
		super(setup);
		proceed();
		addStrategies();
	}

	public FlagTactic6() {
		super();
		addStrategies();
	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	protected void addStrategies() {
		strategies = new ArrayList<Strategy>();
		//strategies.add(new Defensive());
		strategies.add(new Ofensive());
		strategies.add(new Middle());
	}

	private void proceed() {
		super.empty();
		//place bombs and flag
		UnitPlacement toPut = new UnitPlacement(UnitType.BOMB, 2, 0, 1);
		super.placeUnit(toPut);
		toPut.setX(4);
		super.placeUnit(toPut);
		toPut.setX(5);
		super.placeUnit(toPut);
		toPut.setX(3);
		toPut.setUnitType(UnitType.FLAG);
		super.placeUnit(toPut);
		toPut.setY(1);
		toPut.setUnitType(UnitType.BOMB);
		super.placeUnit(toPut);
		
		//place 3outof4 high pieces
		ArrayList<UnitType> units = new ArrayList<UnitType>();
		units.add(UnitType.MARSHAL);
		units.add(UnitType.GENERAL);
		units.add(UnitType.COLONEL);
		units.add(UnitType.MAJOR);
		ArrayList<UnitType> order = super.giveOrder(units);
		toPut.setUnitType(order.get(0));
		toPut.setX(2);
		super.placeUnit(toPut);
		toPut.setUnitType(order.get(1));
		toPut.setX(4);
		super.placeUnit(toPut);
		toPut.setUnitType(order.get(2));
		toPut.setX(5);
		super.placeUnit(toPut);
		
		//place last piece
		toPut.setUnitType(order.get(3));
		for(int j = 1; j <= 2; j++){
			for(int i = 0; i < setup.getWidth(); i++){
				if(super.isFree(i,j)){
					int weight = (int)((1/(double)j)*4);
					possiblePlacements.add(new UnitPlacement(toPut.getUnitType(), i, j, weight));
				}
			}
		}
		toPut = super.randomizeUnitPlacement();
		super.placeUnit(toPut);
		
		//place a 4/5 in first row
		super.empty();
		for(int i = 0; i < setup.getWidth(); i++){
			if(super.isFree(i, 0)){
				possiblePlacements.add(new UnitPlacement(super.randomizeSL(), i, 0, 1));
			}
		}
		toPut = super.randomizeUnitPlacement();
		super.placeUnit(toPut);
	}
}
