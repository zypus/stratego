package com.theBombSquad.stratego.player.ai.setup.FlagTactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.FlagTactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class FlagTactic1 extends FlagTactic {

	public FlagTactic1(AISetup setup) {
		super(setup);
		proceed();
	}

	public FlagTactic1() {
		super();
	}
	public void addSetup(AISetup setup){
		super.setup=setup;
		proceed();
	}

	private void proceed() {

		possiblePlacements = new ArrayList<UnitPlacement>();
		for (int j = 1; j < setup.getWidth() - 1; j++) {
			possiblePlacements.add(new UnitPlacement(UnitType.FLAG, 3, j, 1));
		}
		UnitPlacement toPut = super.randomizeUnitPlacement();
		place(toPut);
		for (int j = 1; j < setup.getWidth() - 1; j++) {
			if (super.isFree(3, j - 1) && super.isFree(3, j + 1)) {
				possiblePlacements.add(new UnitPlacement(UnitType.SCOUT, 3, j,
						1));
				possiblePlacements.add(new UnitPlacement(super.randomizeSL(),
						3, j, 1));

			}
		}
		toPut = super.randomizeUnitPlacement();
		place(toPut);
		addStrategies();
	}



	public void place(UnitPlacement toPut) {
		// Flag/Scout
		super.placeUnit(toPut);
		// left bomb
		toPut.setY(toPut.getY() - 1);
		toPut.setUnitType(UnitType.BOMB);
		super.placeUnit(toPut);
		// Up bomb
		toPut.setY(toPut.getY() + 1);
		toPut.setX(toPut.getX() - 1);
		super.placeUnit(toPut);
		// right bomb
		toPut.setX(toPut.getX() + 1);
		toPut.setY(toPut.getY() + 1);
		super.placeUnit(toPut);
		// 4/5
		toPut.setX(toPut.getX() + 1);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
		toPut.setY(toPut.getY() - 2);
		toPut.setUnitType(super.randomizeSL());
		super.placeUnit(toPut);
	}

}
