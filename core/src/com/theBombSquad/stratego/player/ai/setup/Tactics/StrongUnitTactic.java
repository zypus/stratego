package com.theBombSquad.stratego.player.ai.setup.Tactics;

import java.util.ArrayList;
import java.util.List;

import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.Tactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class StrongUnitTactic extends Tactic {
	private final int StrongUnitFirstSecondRowWeight = 2;
	private final int StrongUnitElseWeight = 1;

	public StrongUnitTactic(AISetup setup) {
		super(setup);
		proceed();
	}

	public StrongUnitTactic() {
		super();
	}
	
	public void addSetup(AISetup setup) {
		this.setup = setup;
		proceed();
	}
	
	private void proceed() {
	List<Unit> toPut=setup.getAvailableUnits();
	possiblePlacements= new ArrayList<UnitPlacement>();
	// finding units to put only those higher ranked than 5
	for(int i =0; i<toPut.size();i++){
		if(toPut.get(i).getType().getRank()<6){
			toPut.remove(i);
			i--;
		}
	}
	//assigning weights
	for (int j = 0; j < setup.getHeight(); j++) {
		for (int i = 0; i < setup.getWidth(); i++) {
			// if in third row
			if (i == 0||i==1) {
				if (super.isFree(i, j)) {
					possiblePlacements.add(new UnitPlacement(null,
							i, j, StrongUnitFirstSecondRowWeight));
				}
			} else {
				if (super.isFree(i, j)) {
					possiblePlacements.add(new UnitPlacement(null,
							i, j, StrongUnitElseWeight));
				}
			}
		}
	}
	//randomize places and place units to the board
	for(int i =0; i<toPut.size();i++){
	UnitPlacement UnitToPut = super.randomizeUnitPlacement();
	UnitToPut.setUnitType(toPut.get(i).getType());
	super.placeUnit(UnitToPut);
	UnitToPut.setUnitType(null);
	possiblePlacements.remove(UnitToPut);

	}
}
}