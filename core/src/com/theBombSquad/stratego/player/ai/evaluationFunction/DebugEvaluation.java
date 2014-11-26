package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.player.ai.AIGameState;

public class DebugEvaluation implements EvaluationFunction{

	@Override
	public float evaluate(AIGameState state) {
		return state.getOwn().getUnitCount()-state.getOpponent().getUnitCount();
	}
	
}
