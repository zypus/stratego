package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.player.ai.AIGameState;

/** Interface for Evaluation Functions */
public interface EvaluationFunction {
	
	public float evaluate(AIGameState state);

}
