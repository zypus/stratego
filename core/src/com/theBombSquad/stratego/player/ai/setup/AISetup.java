package com.theBombSquad.stratego.player.ai.setup;

import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Setup;

public class AISetup extends Setup{
	FlagTactic tactic;
	Strategy strategy;
	
	public AISetup(GameView view){
		super(view);
		pickFlagTactic();
		executeTactics();
	}

	private void executeTactics() {
		for(int i = 0; i < strategy.getTactics().size(); i++){
			strategy.getTactics().get(i).addSetup(this);
			this.board=strategy.getTactics().get(i).getSetup().getBoard();
		}
	}

	private void pickFlagTactic() {
		ArrayList<FlagTactic> tactics= new ArrayList<FlagTactic>();
		tactics.add(new FlagTactic());
		// add them all one by one
		int random= (int)(Math.random()*tactics.size());
		tactic=tactics.get(random);
		random= (int)(Math.random()*tactic.getStrategies().size());
		strategy= tactic.getStrategies().get(random);
	}
	
	
}
