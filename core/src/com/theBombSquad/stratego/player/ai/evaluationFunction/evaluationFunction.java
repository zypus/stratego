package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;

/** Interface for Evaluation Functions */
public interface EvaluationFunction {

	public float evaluate(GameBoard state, StrategoConstants.PlayerID player);

}
