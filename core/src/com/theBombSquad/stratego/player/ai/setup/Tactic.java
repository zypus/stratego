package com.theBombSquad.stratego.player.ai.setup;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;

public class Tactic {
	protected AISetup setup;
	protected ArrayList<UnitPlacement> possiblePlacements;

	public Tactic(AISetup setup) {
		this.setup = setup;
	}

	public Tactic(){
	}
	public void addSetup(AISetup setup){
		this.setup=setup;
	}

	// checks if the spot in the setup is free
	public boolean isFree(int i, int j) {
		if (this.setup.getUnit(i, j).getType() == UnitType.AIR) {
			return true;
		}
		return false;
	}

	// fids the Unit to place basing on weights
	public UnitPlacement randomizeUnitPlacement() {
		int totalWeight = 0;
		for (int i = 0; i < possiblePlacements.size(); i++) {
			totalWeight = totalWeight + possiblePlacements.get(i).getWeight();
		}
		int random = ((int) (Math.random() * totalWeight));
		for (int i = 0; i < possiblePlacements.size(); i++) {
			if (random - possiblePlacements.get(i).getWeight() < 0) {
				random = random - possiblePlacements.get(i).getWeight();
			} else {
				return possiblePlacements.get(i);
			}
		}
		return null;
	}

	public void placeUnit(UnitPlacement toPut) {
		for (int i = 0; i < setup.getView().getAvailableUnits().size(); i++) {
			if(setup.getView().getAvailableUnits().get(i).getType()==toPut.getUnitType())
			setup.setUnit(toPut.getX(), toPut.getY(), setup.getView().getAvailableUnits().get(i));
			break;
		}
	}
	public AISetup getSetup(){
		return setup;
	}


}
