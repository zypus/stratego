package com.theBombSquad.stratego.player.ai.players.planner.plans;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.BluffingAI.BluffingMoveEvaluation;
import com.theBombSquad.stratego.player.ai.players.planner.Plan;

public class PlanBluffing implements Plan{
	
	private static final int THRESHOLD = 100;
	
	private BluffingMoveEvaluation bluff;
	private AIGameState gameState;
	
	private float evalSum;
	
	public PlanBluffing(){
		this.bluff = new BluffingMoveEvaluation();
		this.evalSum = 0;
	}

	@Override
	public float evaluateMove(GameView view, Move move) {
		float value = 0;
		Unit self = view.getCurrentState().getUnit(move.getFromX(), move.getFromY());
		float bluffEval = (float)bluff.evaluateBluff(move, gameState);
		if(bluffEval >= THRESHOLD){
			bluffEval = bluffEval/evalSum;
			value = bluffEval*100;
		}
		return value;
	}

	@Override
	public void postMoveUpdate(GameView view) {
		this.gameState = AI.createAIGameState(view);
		this.evalSum = 0;
	}
	
	public void normalize(Move move){
		evalSum += (float)bluff.evaluateBluff(move, gameState);
	}
	
}