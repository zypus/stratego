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
public class MoveEvalAI extends AI {

	MoveEvaluationFunction evaluationFunction = new MoveEvaluationFunction();
	int[] weights={175,191,188,178,164,155,111,185,179,191,87};
	

	public MoveEvalAI(Game.GameView gameView) {
		super(gameView);
	}

	@Override
	protected Move move() {
		AIGameState gameState = AI.getCurrentAIGameStateFor(gameView.getPlayerID());
		List<Move> moves = AI.createAllLegalMoves(gameView, gameView.getCurrentState());
		Move bestMove = null;
		double max = -Double.MAX_VALUE;
		for (Move move : moves) {
			double value = evaluationFunction.evaluateMove(move, gameState);
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
	public int[] getWeights(){
		return weights;
	}
}
