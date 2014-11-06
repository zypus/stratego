package com.theBombSquad.stratego.player.ai.setup.Tactics;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.AISetup;
import com.theBombSquad.stratego.player.ai.setup.Tactic;
import com.theBombSquad.stratego.player.ai.setup.UnitPlacement;

public class SpyTactic extends Tactic {
	private final double spyNextToNineChance = 0.4;

	private final int spyBehindTheLakeWeight = 3;
	private final int spyThirdRowWeight = 2;
	private final int spyElseWeight = 1;

	private int spyX;
	private int spyY;
	private int nineX;
	private int nineY;

	public SpyTactic(AISetup setup) {
		super(setup);
		proceed();
	}

	public SpyTactic() {
		super();
	}

	public void addSetup(AISetup setup) {
		this.setup = setup;
		proceed();
	}

	public void proceed() {
		spyX = -1;
		spyY = -1;
		nineX = -1;
		nineY = -1;
		if (!ninePlaced()) {
			if (!spyPlaced()) {
				placeSpy();
			}
			if (Math.random() < spyNextToNineChance) {
				placeNineNextToSpy();
			}
		} else if (!spyPlaced()) {
			if (Math.random() < spyNextToNineChance) {
				placeSpyNextToNine();
			}
		}
	}

	private void placeNineNextToSpy() {
		possiblePlacements = new ArrayList<UnitPlacement>();
		if (spyX + 1 < 4 && super.isFree(spyX + 1, spyY)) {
			possiblePlacements.add(new UnitPlacement(UnitType.GENERAL,
					spyX + 1, spyY, spyBehindTheLakeWeight));
		}
		if (spyX - 1 >= 0 && super.isFree(spyX - 1, spyY)) {
			possiblePlacements.add(new UnitPlacement(UnitType.GENERAL,
					spyX - 1, spyY, spyBehindTheLakeWeight));
		}
		if (spyY + 1 < 10 && super.isFree(spyX, spyY + 1)) {
			possiblePlacements.add(new UnitPlacement(UnitType.GENERAL, spyX,
					spyY + 1, spyBehindTheLakeWeight));
		}
		if (spyY - 1 >= 0 && super.isFree(spyX, spyY - 1)) {
			possiblePlacements.add(new UnitPlacement(UnitType.GENERAL, spyX,
					spyY - 1, spyBehindTheLakeWeight));
		}
		if (possiblePlacements.size() != 0) {
			UnitPlacement toPut = super.randomizeUnitPlacement();
			nineX = toPut.getX();
			nineY = toPut.getY();
			super.placeUnit(toPut);
		}
	}

	private void placeSpyNextToNine() {
		possiblePlacements = new ArrayList<UnitPlacement>();
		if (nineX + 1 < 4 && super.isFree(nineX + 1, nineY)) {
			possiblePlacements.add(new UnitPlacement(UnitType.SPY, nineX + 1,
					nineY, 1));
		}
		if (nineX - 1 >= 0 && super.isFree(nineX - 1, nineY)) {
			possiblePlacements.add(new UnitPlacement(UnitType.SPY, nineX - 1,
					nineY, 1));
		}
		if (nineY + 1 < 10 && super.isFree(nineX, nineY + 1)) {
			possiblePlacements.add(new UnitPlacement(UnitType.SPY, nineX,
					nineY + 1, 1));
		}
		if (nineY - 1 >= 0 && super.isFree(nineX, nineY - 1)) {
			possiblePlacements.add(new UnitPlacement(UnitType.SPY, nineX,
					nineY - 1, 1));
		}
		if (possiblePlacements.size() != 0) {
			UnitPlacement toPut = super.randomizeUnitPlacement();
			spyX = toPut.getX();
			spyY = toPut.getY();
			super.placeUnit(toPut);
		}
	}

	private void placeSpy() {
		possiblePlacements = new ArrayList<UnitPlacement>();
		for (int i = 0; i < setup.getHeight(); i++) {
			for (int j = 0; j < setup.getWidth(); j++) {
				// if behind the lake
				if (i < 2 && ((j > 1 && j < 4) || (j > 5 && j < 8))) {
					if (super.isFree(i, j)) {
						possiblePlacements.add(new UnitPlacement(UnitType.SPY,
								i, j, spyBehindTheLakeWeight));
					}
				}
				// if in third row
				else if (i == 2) {
					if (super.isFree(i, j)) {
						possiblePlacements.add(new UnitPlacement(UnitType.SPY,
								i, j, spyThirdRowWeight));
					}
				} else {
					if (super.isFree(i, j)) {
						possiblePlacements.add(new UnitPlacement(UnitType.SPY,
								i, j, spyElseWeight));
					}
				}
			}
		}

		UnitPlacement toPut = super.randomizeUnitPlacement();
		spyX = toPut.getX();
		spyY = toPut.getY();
		super.placeUnit(toPut);
	}

	private boolean spyPlaced() {
		if (spyX != -1 && spyY != -1) {
			return true;
		}
		for (int i = 0; i < setup.getHeight(); i++) {
			for (int j = 0; j < setup.getWidth(); j++) {
				if (setup.getUnit(i, j).getType() == UnitType.SPY) {
					spyX = i;
					spyY = j;
					return true;
				}
			}
		}
		return false;
	}

	private boolean ninePlaced() {
		if (nineX != -1 && nineY != -1) {
			return true;
		}
		for (int i = 0; i < setup.getHeight(); i++) {
			for (int j = 0; j < setup.getWidth(); j++) {
				if (setup.getUnit(i, j).getType() == UnitType.GENERAL) {
					nineX = i;
					nineY = j;
					return true;
				}
			}
		}
		return false;
	}
}
