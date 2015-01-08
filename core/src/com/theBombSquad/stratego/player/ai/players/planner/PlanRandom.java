package com.theBombSquad.stratego.player.ai.players.planner;

import java.util.Random;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;

/** This Plan Returns A Small Random Value (Between 0 And 1) So That A Random Move Is Chosen Out Of Several Moves That Have The Same Profit */
public class PlanRandom implements Plan{
	
	private Random rand;
	
	public PlanRandom(){
		this.rand = new Random();
	}

	@Override
	public float evaluateMove(GameView view, Move move) {
		return rand.nextFloat()/1000;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		//Nothing To Do Here
	}

}
