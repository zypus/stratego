package com.theBombSquad.stratego.player.ai.setup.Strategies;

import java.util.ArrayList;

import com.theBombSquad.stratego.player.ai.setup.Strategy;
import com.theBombSquad.stratego.player.ai.setup.Tactic;
import com.theBombSquad.stratego.player.ai.setup.Tactics.*;

public class Ofensive extends Strategy{
	public Ofensive(){
		super();
		super.tactics= new ArrayList<Tactic>();
		tactics.add(new MarshalTactic());
		tactics.add(new StrongUnitTactic());
		tactics.add(new SapperTactic());
		tactics.add(new SpyTactic());
		tactics.add(new ScoutTactic());
		tactics.add(new ElseUnitTactic());
	}

}
