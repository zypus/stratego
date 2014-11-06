package com.theBombSquad.stratego.player.ai.setup.Tactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.Tactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class ScoutTactic extends Tactic {

	private final int ScoutFirstRowWeight = 4;
	private final int ScoutSecondRowWeight = 3;
	private final int ScoutThirdRowWeight = 2;
	private final int ScoutFourthRowWeight = 1;

	public ScoutTactic(AISetup setup) {
		super(setup);
		proceed();
	}

	public ScoutTactic() {
		super();
	}

	public void addSetup(AISetup setup) {
		this.setup = setup;
		proceed();
	}

	public void proceed() {
		int numOfScoutsToPut = findNumOfScoutsToPut();
		possiblePlacements = new ArrayList<UnitPlacement>();
		if (numOfScoutsToPut > 0) {
			// finding possible placements
			for (int i = 0; i < setup.getHeight(); i++) {
				for (int j = 0; j < setup.getWidth(); j++) {
					if (i == 0) {
						if (super.isFree(i, j)) {
							possiblePlacements.add(new UnitPlacement(
									UnitType.SCOUT, i, j, ScoutFirstRowWeight ));
						}
					} else if (i == 1) {
						if (super.isFree(i, j)) {
							possiblePlacements.add(new UnitPlacement(
									UnitType.SCOUT, i, j, ScoutSecondRowWeight ));
						}
					}
					// if in third row
					else if (i == 2) {
						if (super.isFree(i, j)) {
							possiblePlacements.add(new UnitPlacement(
									UnitType.SCOUT, i, j, ScoutThirdRowWeight));
						}
					} else {
						if (super.isFree(i, j)) {
							possiblePlacements
									.add(new UnitPlacement(UnitType.SCOUT, i,
											j, ScoutFourthRowWeight));
						}
					} 
				}
			}
			for (int i = 0; i < numOfScoutsToPut; i++) {
				UnitPlacement toPut = super.randomizeUnitPlacement();
				super.placeUnit(toPut);
			}
		}
	}

	private int findNumOfScoutsToPut() {
		int numOfScouts = 5;
		for (int i = 0; i < setup.getHeight(); i++) {
			for (int j = 0; j < setup.getWidth(); j++) {
				if (setup.getUnit(i, j).getType() == UnitType.SCOUT) {
					numOfScouts--;
				}
			}
		}
		return numOfScouts;
	}
}
