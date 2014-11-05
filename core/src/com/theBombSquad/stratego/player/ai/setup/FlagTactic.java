package com.theBombSquad.stratego.player.ai.setup;
import java.util.ArrayList;

import com.theBombSquad.stratego.gameMechanics.board.Setup;

public class FlagTactic extends Tactic{
	protected ArrayList<Strategy> strategies;
	
	//this just stores the setup changed by extending it flag tactics 
	// extending it class have to state whether it might be offensive def or middle
	public FlagTactic(){
		super();
	}
	public FlagTactic(Setup setup){
		super(setup);
		this.setup=setup;
	}
	public Setup getSetup(){
		return setup;
	}
	public ArrayList<Strategy> getStrategies(){
		return strategies;
	}
	
	
	

}
