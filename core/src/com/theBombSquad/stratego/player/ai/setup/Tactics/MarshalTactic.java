package com.theBombSquad.stratego.player.ai.setup.Tactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.Tactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class MarshalTactic extends Tactic {
	private final int MarshalBehindTheLakeWeight = 2;
	private final int MarshalMiddleWeight = 4;
	private final int MarshalSidesWeight = 3;
	private final int MarshalElseWeight = 1;

	public MarshalTactic(AISetup setup) {
		super(setup);
		proceed();
	}

	public MarshalTactic() {
		super();
	}

	public void addSetup(AISetup setup) {

		this.setup = setup;
		proceed();
	}

	public void proceed() {
		possiblePlacements = new ArrayList<UnitPlacement>();
		if (super.hasAvailable(UnitType.MARSHAL)) {
			for (int j = 0; j < setup.getHeight(); j++) {
				for (int i = 0; i < setup.getWidth(); i++) {
					// if behind the lake
					if (i < 2 && ((j > 1 && j < 4) || (j > 5 && j < 8))) {
						if (super.isFree(i, j)) {
							possiblePlacements.add(new UnitPlacement(
									UnitType.MARSHAL, i, j,
									MarshalBehindTheLakeWeight));
						}
					}
					// Middle
					else if (i < 2 && (j >= 4 && j <= 5)) {
						if (super.isFree(i, j)) {
							possiblePlacements
									.add(new UnitPlacement(UnitType.MARSHAL, i,
											j, MarshalMiddleWeight));
						}
					} else if (i < 2 && ((j < 2) || (j > 7))) {
						if (super.isFree(i, j)) {
							possiblePlacements
									.add(new UnitPlacement(UnitType.MARSHAL, i,
											j, MarshalSidesWeight));
						}
					} else {
						if (super.isFree(i, j)) {
							possiblePlacements.add(new UnitPlacement(
									UnitType.MARSHAL, i, j, MarshalElseWeight));
						}
					}
				}
			}
			UnitPlacement toPut = super.randomizeUnitPlacement();
			super.placeUnit(toPut);
			possiblePlacements.remove(toPut);

		}
	}
}
