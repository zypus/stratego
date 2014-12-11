package com.theBombSquad.stratego.player.ai.players.planner;

import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;

public interface Plan {
	
	/** Evaluates The Current Move, Given Current Board */
	public float evaluateMove(GameView view, Move move);
	
	/** Performs Post Move Update After Move Has Been Executed, In Case That This Should Be Needed */
	public void postMoveUpdate(GameView view);
	
}
