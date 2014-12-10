package com.theBombSquad.stratego.player.ai.evaluationFunctions;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 10/12/14
 */
public interface EvaluationFunction {

	public float evaluate(GameBoard state, StrategoConstants.PlayerID player);

}
