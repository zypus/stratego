package com.theBombSquad.stratego.player.ai.setup.Strategies;

import java.util.ArrayList;

import com.theBombSquad.stratego.player.ai.setup.Strategy;
import com.theBombSquad.stratego.player.ai.setup.Tactic;
import com.theBombSquad.stratego.player.ai.setup.Tactics.ElseUnitTactic;
import com.theBombSquad.stratego.player.ai.setup.Tactics.MarshalTactic;
import com.theBombSquad.stratego.player.ai.setup.Tactics.SapperTactic;
import com.theBombSquad.stratego.player.ai.setup.Tactics.ScoutTactic;
import com.theBombSquad.stratego.player.ai.setup.Tactics.SpyTactic;
import com.theBombSquad.stratego.player.ai.setup.Tactics.StrongUnitTactic;

public class Defensive extends Strategy {
	public Defensive(){
		super();
		super.tactics= new ArrayList<Tactic>();
		tactics.add(new ScoutTactic());
		tactics.add(new SpyTactic());
		tactics.add(new SapperTactic());
		tactics.add(new MarshalTactic());
		tactics.add(new StrongUnitTactic());
		tactics.add(new ElseUnitTactic());
	}

}

