package com.theBombSquad.stratego.player.ai.setup.Tactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.Tactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class SapperTactic extends Tactic {

	private final int sapperBehindTheLakeWeight = 2;
	private final int sapperThirdRowWeight = 4;
	private final int sapperFourthRowWeight = 3;
	private final int sapperElseWeight = 1;

	public SapperTactic(AISetup setup) {
		super(setup);
		proceed();
	}

	public SapperTactic() {
		super();
	}

	public void addSetup(AISetup setup) {
		this.setup = setup;
		proceed();
	}

	public void proceed() {
		int numOfSappersToPut = findNumOfSappersToPut();
		possiblePlacements = new ArrayList<UnitPlacement>();
		if (numOfSappersToPut > 0) {
			// finding possible placements
			for (int j = 0; j < setup.getHeight(); j++) {
				for (int i = 0; i < setup.getWidth(); i++) {
					// if behind the lake
					if (i < 2 && ((j > 1 && j < 4) || (j > 5 && j < 8))) {
						if (super.isFree(i, j)) {
							possiblePlacements.add(new UnitPlacement(
									UnitType.SAPPER, i, j,
									sapperBehindTheLakeWeight));
						}
					}
					// if in third row
					else if (i == 2) {
						if (super.isFree(i, j)) {
							possiblePlacements
									.add(new UnitPlacement(UnitType.SAPPER, i,
											j, sapperThirdRowWeight));
						}
					} else if (i == 3) {
						if (super.isFree(i, j)) {
							possiblePlacements.add(new UnitPlacement(
									UnitType.SAPPER, i, j,
									sapperFourthRowWeight));
						}
					} else {
						if (super.isFree(i, j)) {
							possiblePlacements.add(new UnitPlacement(
									UnitType.SAPPER, i, j, sapperElseWeight));
						}
					}
				}
			}
			for (int i = 0; i < numOfSappersToPut; i++) {
				UnitPlacement toPut = super.randomizeUnitPlacement();
				super.placeUnit(toPut);
				possiblePlacements.remove(toPut);
			}
		}
	}

	private int findNumOfSappersToPut() {
		int numOfSappers = 0;
		for(int i =0; i<setup.getAvailableUnits().size();i++){
			if(setup.getAvailableUnits().get(i).getType()==UnitType.SAPPER){
				numOfSappers++;
			}
		}
		return numOfSappers;
	}
}