package com.theBombSquad.stratego.player.ai.players.TDStratego;

import Jama.Matrix;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.AIGameState;
import com.theBombSquad.stratego.player.ai.AIGameStateDebugger;
import com.theBombSquad.stratego.player.ai.AIUnit;
import com.theBombSquad.stratego.player.ai.BluffingAI.BluffingMoveEvaluation;
import com.theBombSquad.stratego.player.ai.BluffingAI.MoveEvaluationFunction;
import com.theBombSquad.stratego.player.ai.BluffingAI.StateEvaluationFunction;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;
import static com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 20/11/14
 */
public class TDStratego
		extends AI {

	private static final int MAX_DEPTH = 1;
	private static final float epsilon = 0.2f;
	private AIGameState lastBoard;
	private Random random = new Random();
	BluffingMoveEvaluation e = new BluffingMoveEvaluation();
	MoveEvaluationFunction f = new MoveEvaluationFunction();
	StateEvaluationFunction g= new 	StateEvaluationFunction();

	@Getter @Setter private boolean learning = false;
	@Getter private TDPlayer tdPlayer;

	private Game.GameView optionalGameview = null;
	private boolean endResultReceived = false;

	public TDStratego(Game.GameView gameView1, Game.GameView gameView2) {
		super(gameView1);
		optionalGameview = gameView2;
//		TDNeuralNet net = new TDNeuralNet(new int[] { TDPlayer.INFO_SIZE, 100, 2 }, new AbstractTDPlayer.Sigmoid(), new AbstractTDPlayer.SigmoidPrime());
		TDNeuralNet net = TDNeuralNet.loadNeuralNet("test/TDStratego/progress/player42_progress20.net");
		tdPlayer = new TDPlayer(net, 0.75f, new float[] { 0.5f, 0.5f });
	}

	public TDStratego(Game.GameView gameView) {
		super(gameView);
		TDNeuralNet net = new TDNeuralNet(new int[] { TDPlayer.INFO_SIZE, 100, 2 }, new AbstractTDPlayer.Sigmoid(), new AbstractTDPlayer.SigmoidPrime());
		//		TDNeuralNet net = TDNeuralNet.loadNeuralNet("test/TDStratego/nn.net");
		tdPlayer = new TDPlayer(net, 0.75f, new float[] { 0.5f, 0.5f });
	}

	@Override
	protected Move move() {
		Game.GameView currentGameview = gameView;
		if (optionalGameview != null) {
			currentGameview = (gameView.getCurrentTurn() % 2 == 1)
							  ? gameView
							  : optionalGameview;
		}
		AIGameState board = AI.createAIGameState(currentGameview);
		PlayerID playerID = currentGameview.getPlayerID();
		List<Move> moves = AI.createAllLegalMoves(currentGameview, currentGameview.getCurrentState());
		Move bestMove = null;
		AIGameState bestBoard = null;
		float max = -Float.MAX_VALUE;
		float v = random.nextFloat();
		if (v < epsilon) {
			bestMove = moves.get(random.nextInt(moves.size()));
			bestBoard = AI.createOutcomeOfMove(board, bestMove);
		} else {
			for (Move move : moves) {
				AIGameState b = AI.createOutcomeOfMove(board, move);
				float sum = tdPlayer.utilityForState(b);
				if (sum > max) {
					bestMove = move;
					bestBoard = b;
                    max = sum;
				}
			}
		}
		if (gameView.getCurrentTurn() % 1000 == 0) {
			AIGameStateDebugger.debug(board);
		}

		lastBoard = bestBoard;
		System.out.println("Bluff value - "+e.evaluateBluff(bestMove, board)+" Move value - "+f.evaluateMove(bestMove, board)+" State value - " + g.evaluateState(board));
		gameView.performMove(bestMove);
		if (learning) {
			tdPlayer.learnBasedOnSelectedState(bestBoard, 1);
		}
		currentGameview.performMove(bestMove);
		return bestMove;
	}

	@Override
	protected void cleanup() {
		if (learning && !endResultReceived) {
			Matrix endResult = new Matrix(2,1);
			endResult.set(0, 0, (gameView.getWinnerId() == PLAYER_1) ? 1 : 0) ;
			endResult.set(1, 0, (gameView.getWinnerId() == PLAYER_2) ? 1 : 0) ;
			tdPlayer.learnBasedOnFinalResult(lastBoard, endResult, 1);
			endResultReceived = true;
		}
		super.cleanup();
	}

	@Override
	protected Setup setup() {
		Setup setup = new Setup(10, 4);
		List<Unit> availableUnits = new ArrayList<Unit>(gameView.getAvailableUnits());
		// shuffle the list containing all available units
		Collections.shuffle(availableUnits);
		//go through the list and place them on the board as the units appear in the randomly shuffled list
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 10; x++) {
				setup.setUnit(x, y, availableUnits.get(y * 10 + x));
			}
		}
		// no need to check if the setup is valid because it cannot be invalid by the way it is created
		// so simply sending the setup over to the game
		gameView.setSetup(setup);
		return setup;
	}

	public void reset() {
		tdPlayer.eraseTraces();
		endResultReceived = false;
	}

	private float negamax(SchrodingersBoard board, float alpha, float beta, int depth, PlayerID playerID) {
		if (depth <= 0) {
			return 0;// tdPlayer.utilityForState(board);
		}
		List<Move> moves = board.generateAllMoves(playerID);
		for (Move move : moves) {
			List<SchrodingersBoard> boards = board.generateFromMove(move);
			if (boards.size() == 1) {
				alpha = Math.max(alpha, -negamax(boards.get(0), -beta, -alpha, depth - 1, playerID.getOpponent()));
			} else {
				alpha = Math.max(alpha, -star(board, boards, -beta, -alpha, depth -1, playerID.getOpponent()));
			}
			// beta pruning
			if (alpha >= beta) {
				return alpha;
			}
		}
		return alpha;
	}

	private float star(SchrodingersBoard board, List<SchrodingersBoard> nextBoards, float alpha, float beta, int depth, PlayerID playerID) {
		if (depth <= 0) {
			return 0;//tdPlayer.utilityForState(board);
		}
		float sum = 0;
		for (SchrodingersBoard nextBoard : nextBoards) {
			sum += nextBoard.getRelativeProbability() * -negamax(nextBoard, -beta, -alpha, depth, playerID);
		}
		return sum;
	}

	@Data
	@AllArgsConstructor
	private class NegamaxResult {
		float utility;
		List<SchrodingersBoard> boards;
		List<Matrix> outputs;
	}

	public void save(String path) {
		tdPlayer.saveNet(path);
	}

	public void load(String path) {
		tdPlayer.loadNet(path);
	}

	private static class TDPlayer
			extends AbstractTDPlayer<AIGameState> {

		private static final int             PLAYER_FLAG         = 2;
		/** Owner of the unit*/
		private static final int             NUMBER_OF_LAKES     = 8;
		private static final Unit.UnitType[] RELEVANT_UNIT_TYPES = { SPY, SCOUT, SAPPER, SERGEANT, LIEUTENANT, CAPTAIN, MAJOR, COLONEL, GENERAL, MARSHAL, BOMB, FLAG };
		public static final  int             INFO_SIZE           = (GRID_WIDTH * GRID_HEIGHT - NUMBER_OF_LAKES) * (PLAYER_FLAG * RELEVANT_UNIT_TYPES.length + PLAYER_FLAG)
																   + PLAYER_FLAG * RELEVANT_UNIT_TYPES.length;

		private TDPlayer(TDNeuralNet net, float lambda, float[] learningRates) {
			super(net, lambda, learningRates);
		}

		@Override
		protected Matrix stateToActivation(AIGameState state) {
			Matrix activation = new Matrix(INFO_SIZE, 1);
			int index = 0;
			for (int x = 0; x < state.getWidth(); x++) {
				for (int y = 0; y < state.getHeight(); y++) {
					AIUnit unit = state.getAIUnit(x, y);
					if (!unit.getUnitReference().isLake()) {
						for (PlayerID playerID : new PlayerID[] { PLAYER_1, PLAYER_2 }) {
							AIUnit playerUnit = state.getAIUnitFor(x, y, playerID);
							// unit probabilities
							for (Unit.UnitType unitType : RELEVANT_UNIT_TYPES) {
								if (playerUnit.getUnitReference() == null || playerUnit.getUnitReference()
										.isAir()) {
									activation.set(index++, 0, 0);
								} else {
									activation.set(index++, 0, playerUnit.getProbabilityFor(unitType));
								}
							}
						}
					}
				}
			}
			// own death counts
			for (Unit.UnitType unitType : RELEVANT_UNIT_TYPES) {
				activation.set(index++, 0, (float) state.getOwn().getDefeatedFor(unitType) / (float) unitType.getQuantity());
			}
			// opponent death counts
			for (Unit.UnitType unitType : RELEVANT_UNIT_TYPES) {
				activation.set(index++, 0, (float) state.getOpponent()
														.getDefeatedFor(unitType) / (float) unitType.getQuantity());
			}

			// set active player
			// owner
//			activation.set(index++, 0, (state.getCurrentPlayer() == PLAYER_1)
//									   ? 1
//									   : 0);
//			activation.set(index++, 0, (state.getCurrentPlayer() == PLAYER_2)
//									   ? 1
//									   : 0);

					//			for (int i = 0; i < activation.getRowDimension(); i++) {
			//				for (int j = 0; j < activation.getColumnDimension(); j++) {
			//					System.out.print(activation.get(i, j));
			//				}
			//				System.out.println();
			//			}
			return activation;
		}

		@Override
		public float utilityValue(AIGameState state, Matrix output) {
			float utility;
			if (state.getCurrentPlayer() == PLAYER_1) {
				utility = (float) (output.get(0, 0) - output.get(1, 0));
			} else {
				utility = (float) (output.get(1, 0) - output.get(0, 0));
			}
			return utility;
		}
	}

}
