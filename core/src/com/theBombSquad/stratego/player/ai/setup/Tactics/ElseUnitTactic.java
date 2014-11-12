package com.theBombSquad.stratego.player.ai.setup.Tactics;

import java.util.ArrayList;
import java.util.List;

import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.Tactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class ElseUnitTactic extends Tactic {
	private final int ElseUnitWeight = 1;

	public ElseUnitTactic(AISetup setup) {
		super(setup);
		proceed();
	}

	public ElseUnitTactic() {
		super();
	}

	public void addSetup(AISetup setup) {
		this.setup = setup;
		proceed();
	}

	private void proceed() {
		List<Unit> toPut = setup.getView().getAvailableUnits();
		possiblePlacements = new ArrayList<UnitPlacement>();
		// assigning weights
		for (int i = 0; i < setup.getHeight(); i++) {
			for (int j = 0; j < setup.getWidth(); j++) {
				// if in third row
				if (super.isFree(i, j)) {
					possiblePlacements.add(new UnitPlacement(null, i, j,
							ElseUnitWeight));
				}

			}
		}
		// randomize places and place units to the board
		for (int i = 0; i < toPut.size(); i++) {
			UnitPlacement UnitToPut = super.randomizeUnitPlacement();
			UnitToPut.setUnitType(toPut.get(i).getType());
			super.placeUnit(UnitToPut);
			UnitToPut.setUnitType(null);
			possiblePlacements.remove(UnitToPut);
			
			
		}
	}
}
