package com.theBombSquad.stratego.player.ai.setup;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Defensive;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Middle;
import com.theBombSquad.stratego.player.ai.setup.Strategies.Ofensive;

public class FlagTactic extends Tactic {
	protected ArrayList<Strategy> strategies;

	// this just stores the setup changed by extending it flag tactics
	// extending it class have to state whether it might be offensive def or
	// middle
	public FlagTactic() {
		super();
	}

	public FlagTactic(AISetup setup) {
		super(setup);
		this.setup = setup;
	}

	public AISetup getSetup() {
		return setup;
	}

	public ArrayList<Strategy> getStrategies() {
		return strategies;
	}

	public UnitType randomizeSL() {
		double random = Math.random();
		if (random <= 0.5) {
			for (int i = 0; i < setup.getView().getAvailableUnits().size(); i++) {
				if (setup.getView().getAvailableUnits().get(i).getType() == UnitType.SERGEANT) {
					return UnitType.SERGEANT;
				}
			}
			return UnitType.LIEUTENANT;
		}
		for (int i = 0; i < setup.getView().getAvailableUnits().size(); i++) {
			if (setup.getView().getAvailableUnits().get(i).getType() == UnitType.LIEUTENANT) {
				return UnitType.LIEUTENANT;
			}
		}
		return UnitType.SERGEANT;

	}
	protected void addStrategies() {
		strategies= new ArrayList<Strategy>(); 
		strategies.add(new Defensive());
		strategies.add(new Ofensive());
		strategies.add(new Middle());
	}
}
