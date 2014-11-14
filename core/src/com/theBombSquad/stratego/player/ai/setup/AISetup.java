package com.theBombSquad.stratego.player.ai.setup;

import java.util.ArrayList;

import lombok.Getter;

import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.setup.FlagTactics.FlagTactic1;

public class AISetup extends Setup{
	FlagTactic tactic;
	Strategy strategy;
	@Getter
	private  ArrayList<Unit> availableUnits;
	@Getter
	private GameView view;
	
	public AISetup(GameView view){
		super(10,4);
		this.view=view;
		initAvailableUnits();
		pickFlagTactic();
		executeTactics();
	}
	private void initAvailableUnits() {
		availableUnits= new ArrayList<Unit>();
		Unit.UnitType[] unitTypeEnum = Unit.UnitType.values();
		// create a list containing all units that needs to be placed on the board
		for (Unit.UnitType type : unitTypeEnum) {
			for (int i = 0; i < type.getQuantity(); i++) {
				availableUnits.add(new Unit(type, view.getPlayerID()));
			}
		}
	}
	
	//executing the strategy step by step
	private void executeTactics() {
		tactic.addSetup(this);
		for(int i = 0; i < strategy.getTactics().size();i=i){
			strategy.getTactics().get(i).addSetup(this);
			this.board=strategy.getTactics().get(i).getSetup().getBoard();
			strategy.getTactics().remove(i);
		}
	}
	//picking random tactic and strategy possible for this tactic
	private void pickFlagTactic() {
		ArrayList<FlagTactic> tactics= new ArrayList<FlagTactic>();
		tactics.add(new FlagTactic1());
		// add them all one by one
		int random= (int)(Math.random()*tactics.size());
		tactic=tactics.get(random);
		random= (int)(Math.random()*tactic.getStrategies().size());
		strategy= tactic.getStrategies().get(random);
	}
	
	
	
}
