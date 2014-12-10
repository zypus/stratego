package com.theBombSquad.stratego.player.ai.players.TDStratego;

import Jama.Matrix;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private SchrodingersBoard lastBoard;

	@Getter @Setter private boolean learning = false;
	@Getter private TDPlayer tdPlayer;

	public TDStratego(Game.GameView gameView) {
		super(gameView);
//		TDNeuralNet net = new TDNeuralNet(new int[]{TDPlayer.INFO_SIZE, 100, 2}, new AbstractTDPlayer.Sigmoid(), new AbstractTDPlayer.SigmoidPrime());
		TDNeuralNet net = TDNeuralNet.loadNeuralNet("test/TDStratego/player1.net");
		tdPlayer = new TDPlayer(net, 0.75f, new float[]{0.5f,0.5f});
	}

	@Override
	protected Move move() {
		SchrodingersBoard board = new SchrodingersBoard(gameView);
		PlayerID playerID = gameView.getPlayerID();
		List<Move> moves = board.generateAllMoves(playerID);
		float alpha = -Float.MAX_VALUE;
		float beta = Float.MAX_VALUE;
		Move bestMove = null;
		float max = -Float.MAX_VALUE;
		for (Move move : moves) {
			List<SchrodingersBoard> boards = board.generateFromMove(move);
			float value;
			if (boards.size() == 1) {
				value = -negamax(boards.get(0), -beta, -alpha, MAX_DEPTH - 1, playerID.getOpponent());
			} else {
				value = -star(board, boards, -beta, -alpha, MAX_DEPTH - 1, playerID.getOpponent());
			}
			if (max < value) {
				bestMove = move;
				max = value;
			}
		}
		if (learning) {
			tdPlayer.learnBasedOnSelectedState(board);
		}
		lastBoard = board;
		gameView.performMove(bestMove);
		return bestMove;
	}

	@Override
	protected void cleanup() {
		if (learning) {
			Matrix endResult = new Matrix(2,1);
			endResult.set(0, 0, (gameView.getWinnerId() == PLAYER_1) ? 1 : 0) ;
			endResult.set(1, 0, (gameView.getWinnerId() == PLAYER_2) ? 1 : 0) ;
			tdPlayer.learnBasedOnFinalResult(lastBoard, endResult);
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
	}

	private float negamax(SchrodingersBoard board, float alpha, float beta, int depth, PlayerID playerID) {
		if (depth <= 0) {
			return tdPlayer.utilityForState(board);
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
			return tdPlayer.utilityForState(board);
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

	private static class TDPlayer extends AbstractTDPlayer<SchrodingersBoard> {

		private static final int PLAYER_FLAG = 2; /** Owner of the unit*/
		private static final int NUMBER_OF_LAKES = 8;
		private static final Unit.UnitType[] RELEVANT_UNIT_TYPES = { SPY, SCOUT, SAPPER, SERGEANT, LIEUTENANT, CAPTAIN, MAJOR, COLONEL, GENERAL, MARSHAL, BOMB, FLAG };
		public static final int INFO_SIZE = (GRID_WIDTH * GRID_HEIGHT - NUMBER_OF_LAKES) * (RELEVANT_UNIT_TYPES.length + PLAYER_FLAG)
									 + PLAYER_FLAG * RELEVANT_UNIT_TYPES.length;

		private TDPlayer(TDNeuralNet net, float lambda, float[] learningRates) {
			super(net, lambda, learningRates);
		}

		@Override
		protected Matrix stateToActivation(SchrodingersBoard state) {
			Matrix activation = new Matrix(INFO_SIZE, 1);
			int index = 0;
			for (int x = 0; x < state.getWidth(); x++) {
				for (int y = 0; y < state.getHeight(); y++) {
					SchrodingersUnit unit = state.getUnit(x, y);
					if (!unit.isLake()) {
						// owner
						activation.set(index++, 0, (unit.getOwner() == PLAYER_1)
												   ? 1
												   : 0);
						activation.set(index++, 0, (unit.getOwner() == PLAYER_2)
												   ? 1
												   : 0);
						// unit probabilities
						for (Unit.UnitType unitType : RELEVANT_UNIT_TYPES) {
							if (unit.isAir()) {
								activation.set(index++, 0, 0);
							} else {
								activation.set(index++, 0, unit.getProbabilityFor(unitType));
							}
						}
					}
				}
			}
			// own death counts
			for (Unit.UnitType unitType : RELEVANT_UNIT_TYPES) {
				activation.set(index++, 0, (float) state.getView()
														.getNumberOfOwnDefeatedUnits(unitType) / (float) unitType.getQuantity());
			}
			// opponent death counts
			for (Unit.UnitType unitType : RELEVANT_UNIT_TYPES) {
				activation.set(index++, 0, (float) state.getView()
														.getNumberOfOpponentDefeatedUnits(unitType) / (float) unitType.getQuantity());
			}

//			for (int i = 0; i < activation.getRowDimension(); i++) {
//				for (int j = 0; j < activation.getColumnDimension(); j++) {
//					System.out.print(activation.get(i, j));
//				}
//				System.out.println();
//			}
			return activation;
		}

		@Override
		public float utilityValue(SchrodingersBoard state, Matrix output) {
			float utility;
			if (state.getView()
					 .getPlayerID() == PLAYER_1) {
				utility = (float) (output.get(0, 0) - output.get(1, 0));
			} else {
				utility = (float) (output.get(1, 0) - output.get(0, 0));
			}
			return utility;
		}
	}

}
