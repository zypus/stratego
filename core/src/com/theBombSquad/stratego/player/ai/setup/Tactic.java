package com.theBombSquad.stratego.player.ai.setup;

import java.util.ArrayList;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;

public class Tactic {
	protected AISetup setup;
	protected ArrayList<UnitPlacement> possiblePlacements;
	private static int counter=1;

	public Tactic(AISetup setup) {
		this.setup = setup;
	}

	public Tactic() {
	}

	public void addSetup(AISetup setup){
		this.setup = setup;
	}

	// checks if the spot in the setup is free
	public boolean isFree(int i, int j) {
		if(i>=0&&i<10&&j>=0&&j<4){
		if (this.setup.getUnit(i, j).getType() == UnitType.AIR) {
			return true;
		}}
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
			if (random - possiblePlacements.get(i).getWeight() >= 0) {
				random = random - possiblePlacements.get(i).getWeight();
			} else {
				return possiblePlacements.get(i);
			}
		}
		System.out.println("Error");
		return null;
	}

	public boolean placeUnit(UnitPlacement toPut) {
		for (int i = 0; i < setup.getAvailableUnits().size(); i++) {
			if(isFree(toPut.getX(),toPut.getY())){
			if (setup.getAvailableUnits().get(i).getType() == toPut
					.getUnitType()) {
				setup.setUnit( toPut.getX(),toPut.getY(), setup
						.getAvailableUnits().get(i));
				if(setup.getView().getPlayerID()==PlayerID.PLAYER_1){
					System.out.println(setup.getAvailableUnits().get(i).getType().getRank()+" "+ counter+" " +setup.getAvailableUnits().size());
					counter++;
				}
				setup.getAvailableUnits().remove(i);
				
				return true;
			}
		}
		}
		System.out.println("error"+ setup.getAvailableUnits().size()+setup.getView().getPlayerID());//}
		return false;
	}

	public AISetup getSetup() {
		return setup;
	}

	public boolean hasAvailable(UnitType type) {
		for (int i = 0; i < setup.getAvailableUnits().size(); i++) {
				if (setup.getAvailableUnits().get(i).getType() == type) {
					return true;
				}
		}
		return false;
	}

}
