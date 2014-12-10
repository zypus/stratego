package com.theBombSquad.stratego.player.ai.setup;

import java.util.ArrayList;

public class Strategy {
	protected ArrayList<Tactic> tactics;
	
	public Strategy(){
	}
	public ArrayList<Tactic> getTactics(){
		return tactics;
	}
}
