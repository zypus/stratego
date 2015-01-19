package com.theBombSquad.stratego.player.ai.BluffingAI;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.players.random.SetupPlayerAI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 19/01/15
 */
public class StateMoveEvalAI extends AI {

	StateEvaluationFunction stateEval = new StateEvaluationFunction();
	MoveEvaluationFunction moveEval = new MoveEvaluationFunction();
	Random random = new Random();

	public StateMoveEvalAI(Game.GameView gameView) {
		super(gameView);
	}

	@Override
	protected Move move() {
		AIGameState gameState = AI.getCurrentAIGameStateFor(gameView.getPlayerID());
		List<Move> moves = AI.createAllLegalMoves(gameView, gameView.getCurrentState());
		Move bestMove = null;
		double maxState = -Double.MAX_VALUE;
		double maxMove = -Double.MAX_VALUE;
		double minState = Double.MAX_VALUE;
		double minMove = Double.MAX_VALUE;
		List<Double> stateValues = new ArrayList<Double>(moves.size());
		List<Double> moveValues = new ArrayList<Double>(moves.size());
		for (Move move : moves) {
			AIGameState nextState = AI.createOutcomeOfMove(gameState, move);
			double stateValue = stateEval.evaluateState(nextState);
			double moveValue = moveEval.evaluateMove(move, gameState);
			Move lastMove = gameView.getMove(gameView.getCurrentTurn() - 3);
			if (lastMove != null) {
				if (lastMove.isSameMovementAs(new Move(move.getToX(), move.getToY(), move.getFromX(), move.getFromY()))) {
					moveValue -= 50;
				}
			}
			if (stateValue > maxState) {
				maxState = stateValue;
			}
			if (moveValue > maxMove) {
				maxMove = moveValue;
			}
			if (stateValue < minState) {
				minState = stateValue;
			}
			if (moveValue < minMove) {
				minMove = moveValue;
			}
			stateValues.add(stateValue);
			moveValues.add(moveValue);
		}
		normalize(stateValues, minState, maxState);
		normalize(moveValues, minMove, maxMove);
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < moves.size(); i++) {
			double sum = stateValues.get(i) + moveValues.get(i);
			if (sum > max || ( sum >= max && random.nextFloat() < 0.5 )) {
				bestMove = moves.get(i);
				max = sum;
			}
		}
		gameView.performMove(bestMove);
		return bestMove;
	}

	private void normalize(List<Double> values, double min, double max) {
		double shiftedMove = max-min;
		if (shiftedMove == 0) {
			shiftedMove = 1;
		}
		for (int i = 0; i < values.size(); i++) {
			double value = values.get(i);
			values.set(i, (value - min) / shiftedMove);
		}
	}

	@Override
	protected Setup setup() {
		return new SetupPlayerAI(gameView).setup_directAccessOverwrite();
	}
}
