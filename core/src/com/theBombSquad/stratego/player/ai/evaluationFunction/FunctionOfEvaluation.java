package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.GameState;

/** Interface for Evaluation Functions */
public interface FunctionOfEvaluation {

	public float evaluate(GameState state, PlayerID player);

}