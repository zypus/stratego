package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.player.ai.AIGameState;

/** Interface for Evaluation Functions */
public interface EvaluationFunction {
	
	public float evaluate(GameBoard state);

}
