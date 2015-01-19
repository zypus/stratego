package com.theBombSquad.stratego.player.ai.BluffingAI;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.players.random.SetupPlayerAI;

import java.util.List;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 19/01/15
 */
public class StateEvalAI extends AI {

	StateEvaluationFunction evaluationFunction = new StateEvaluationFunction();

	public StateEvalAI(Game.GameView gameView) {
		super(gameView);
	}

	@Override
	protected Move move() {
		AIGameState gameState = AI.getCurrentAIGameStateFor(gameView.getPlayerID());
		List<Move> moves = AI.createAllLegalMoves(gameView, gameView.getCurrentState());
		Move bestMove = null;
		double max = -Double.MAX_VALUE;
		for (Move move : moves) {
			AIGameState nextState = AI.createOutcomeOfMove(gameState, move);
			double value = evaluationFunction.evaluateState(nextState);
			if (value > max) {
				bestMove = move;
				max = value;
			}
		}
		gameView.performMove(bestMove);
		return bestMove;
	}

	@Override
	protected Setup setup() {
		return new SetupPlayerAI(gameView).setup_directAccessOverwrite();
	}
}
