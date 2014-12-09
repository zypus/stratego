package com.theBombSquad.stratego.player.ai.evaluationFunction;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;

public interface evaluationFunction {
	
	public float evaluate(GameBoard gamestate, PlayerID player);
	
}
