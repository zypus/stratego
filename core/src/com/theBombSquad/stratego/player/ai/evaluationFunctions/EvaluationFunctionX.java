package com.theBombSquad.stratego.player.ai.evaluationFunctions;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 10/12/14
 */
public interface EvaluationFunctionX {

	public float evaluate(SchrodingersBoard state, StrategoConstants.PlayerID player);

}
