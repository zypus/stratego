package com.theBombSquad.stratego.player.ai.players.TDStratego;

import Jama.Matrix;
import lombok.Getter;
import lombok.Setter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 27/11/14
 */
public class TDTestConnectFour {

	public static void main(String[] args) {
		TDConnectFour player = new TDConnectFour(true);
		GameSession gameSession = new GameSession(player, player);
		for (int i = 0; i < 100000; i++) {
			System.out.println("Session: "+(i+1));
			gameSession.startSession();
		}
		System.out.println("Attempt to save");
		TDNeuralNet.saveNeuralNet(player.getNet(), "test/TDConnectFour/test100000seed.net");
		System.out.println("Saved");
	}

	private interface ConnectFourPlayer {
		int getNextMove(int[][] board);

		void gameFinished(int winner);

		void setGameSession(GameSession session);
	}

	private static class GameSession {

		Random random = new Random();

		private int[][] board = new int[7][6];

		private ConnectFourPlayer[] players;

		@Getter int turn = 0;

		public GameSession(ConnectFourPlayer player1, ConnectFourPlayer player2) {
			players = new ConnectFourPlayer[] { player1, player2 };
			turn = 0;
		}

		public void startSession() {
			turn = 0;
			board = randomSeed();
//			board = new int[7][6];
			players[0].setGameSession(this);
			players[1].setGameSession(this);
			int winner = 0;
			while (winner == 0) {
				int move = players[turn % 2].getNextMove(copyBoard(board));
				Point placed = new Point(-1, -1);
				for (int y = board[0].length - 1; y >= 0; y--) {
					if (board[move][y] != 0) {
						if (y != board[0].length - 1) {
							board[move][y + 1] = (turn % 2) + 1;
							placed = new Point(move, y + 1);
							break;
						} else {
							System.out.println("Invalid move: " + move);
							System.exit(1);
						}
					} else
						if (y == 0 && board[move][y] == 0) {
						board[move][0] = (turn % 2) + 1;
						placed = new Point(move, 0);
					}
				}
				turn++;
				if (turn < 42) {
					winner = checkFour(board, placed);
				} else {
					winner = -1;
				}
			}
			printBoard(board);
			if (winner == -1) {
				System.out.println("Game is a draw");
			} else {
				System.out.println("Winner is player "+winner);
			}
			System.out.println("Turns: "+turn);
			System.out.println();
			players[(turn-1)%2].gameFinished(winner);
		}

		public int[][] randomSeed() {
			int[][] board = new int[7][6];
			turn = 0;
			for (int i = 0; i < 6; i++) {
				board = getNextBoard(board, random.nextInt(7));
				turn++;
			}
			return board;
		}

		public int[][] getNextBoard(int[][] board, int move) {
			int[][] nextBoard = copyBoard(board);
			for (int y = nextBoard[0].length - 1; y >= 0; y--) {
				if (nextBoard[move][y] != 0) {
					if (y != nextBoard[0].length - 1) {
						nextBoard[move][y+1] = (turn % 2) + 1;
						return nextBoard;
					} else {
						System.out.printf("Invalid move: " + move);
						System.exit(1);
					}
				} else if (y == 0 && nextBoard[move][y] == 0) {
					nextBoard[move][0] = (turn % 2) + 1;
				}
			}
			return nextBoard;
		}

		public int[][] copyBoard(int[][] board) {
			int[][] boardCopy = new int[board.length][board[0].length];
			for (int i = 0; i < board.length; i++) {
				System.arraycopy(board[i], 0, boardCopy[i], 0, board[0].length);
			}
			return boardCopy;
		}

		public void printBoard(int[][] board) {
			StringBuilder builder = new StringBuilder();
			for (int y = board[0].length-1; y >= 0; y--) {
				for (int x = 0; x < board.length; x++) {
					switch (board[x][y]) {
						case 0:
							builder.append("_");
							break;
						case 1:
							builder.append("X");
							break;
						case 2:
							builder.append("O");
							break;
					}
				}
				builder.append("\n");
			}
			System.out.printf(builder.toString());
		}

		private int checkFour(int[][] board, Point lastMove) {
			int player = board[lastMove.x][lastMove.y];
			int count;
			// vertical
			count = 0;
			for (int y = 0; y <= lastMove.y; y++) {
				if (board[lastMove.x][y] == player) {
					count++;
				} else {
					count = 0;
				}
				if (count == 4) {
					return player;
				}
			}
			// horizontal
			count = 0;
			for (int x = 0; x <= board[0].length; x++) {
				if (board[x][lastMove.y] == player) {
					count++;
				} else {
					count = 0;
				}
				if (count == 4) {
					return player;
				}
			}
			// diagonal ascending
			count = 0;
			{
				int x = (lastMove.y < lastMove.x) ? lastMove.x - lastMove.y : 0;
				int y = (lastMove.x < lastMove.y) ? lastMove.y - lastMove.x : 0;
				for (; x < board.length && y < board[0].length ; x++, y++) {
					if (board[x][y] == player) {
						count++;
					} else {
						count = 0;
					}
					if (count == 4) {
						return player;
					}
				}
			}
			// diagonal descending
			count = 0;
			{
				int x = ((board[0].length - 1 - lastMove.y) < lastMove.x) ? lastMove.x - (board[0].length - 1 - lastMove.y) : 0;
				int y = (lastMove.x < (board[0].length - 1-lastMove.y)) ? (board[0].length - 1 - lastMove.y) - lastMove.x : board[0].length-1;
				for (; x >= 0 && y >= 0; x--, y--) {
					if (board[x][y] == player) {
						count++;
					} else {
						count = 0;
					}
					if (count == 4) {
						return player;
					}
				}
			}
			return 0;
		}

	}

	private static class TDConnectFour
			implements ConnectFourPlayer {

		private float lambda = 0.75f;
		private float learningRate = 0.7f;

		private int[] sizes;

		private GameSession session;
		@Getter @Setter private TDNeuralNet        net;
		private                 boolean            learning;
		private                 List<Matrix> bestActivations;
		private                 List<Matrix> bestUnprocessedActivations;
		private                 Matrix       previousResult;

		private List<List<Matrix>> previousEligibilityTraces;

		public TDConnectFour(boolean learning) {
			this.learning = learning;
			sizes = new int[] { 6 * 7 * 2 + 2, 22, 2 };
			net = new TDNeuralNet(sizes, new Sigmoid(), new SigmoidPrime());
			previousEligibilityTraces = new ArrayList<List<Matrix>>();
			eraseTraces();
			previousResult = null;
		}

		private void eraseTraces() {
			if (previousEligibilityTraces.isEmpty()) {
				for (int k = 0; k < sizes[sizes.length - 1]; k++) {
					List<Matrix> kTraces = new ArrayList<Matrix>();
					for (int i = 0; i < sizes.length - 1; i++) {
						Matrix trace = new Matrix(sizes[i + 1], sizes[i]);
						kTraces.add(trace);
					}
					previousEligibilityTraces.add(kTraces);
				}
			} else {
				for (List<Matrix> traces : previousEligibilityTraces) {
					for (Matrix trace : traces) {
						trace.times(0);
					}
				}
			}
		}

		private List<Integer> getPossibleMoves(int[][] board) {
			List<Integer> validMoves = new ArrayList<Integer>();
			for (int x = 0; x < board.length; x++) {
				if (board[x][board[0].length - 1] == 0) {
					validMoves.add(x);
				}
			}
			return validMoves;
		}

		@Override
		public int getNextMove(int[][] board) {
			//			List<Integer> validMoves = getPossibleMoves(board);
			int bestMove = -1;
			float bestScore = -1;
			Matrix currentResult = new Matrix(2, 1);
			for (int m = 0; m < board.length; m++) {
				if (board[m][board[0].length - 1] == 0) {
					int[][] futureBoard = session.getNextBoard(board, m);
					Matrix activation = boardToActivation(futureBoard, session.getTurn());
					List<Matrix> activations = new ArrayList<Matrix>();
					List<Matrix> unprocessedActivations = new ArrayList<Matrix>();
					unprocessedActivations.add(activation);
					activations.add(activation);
					for (int i = 0; i < net.getNumberOfLayers(); i++) {
						TDNeuralNet.NetResult netResult = net.fire(activation, i);
						activation = netResult.getLayerActivation();
						activations.add(activation);
						unprocessedActivations.add(netResult.getUnprocessedLayerActivation());
					}
					currentResult = activations.get(activations.size() - 1);
					float score = (float) ((session.getTurn() % 2 == 0)
										   ? currentResult.get(0, 0) - currentResult.get(1, 0)
										   : currentResult.get(1, 0) - currentResult.get(0, 0));
					if (score > bestScore) {
						bestMove = m;
						bestScore = score;
						bestActivations = activations;
						bestUnprocessedActivations = unprocessedActivations;
					}
				}
			}
			if (learning && previousResult != null) {
				for (int k = 0; k < sizes[sizes.length - 1]; k++) {
					previousEligibilityTraces.set(k, net.computeEligibilityTraces(previousEligibilityTraces.get(k), bestActivations, bestUnprocessedActivations, lambda, k));
				}
				Matrix error = currentResult.minus(previousResult);
				net.updateWeights(previousEligibilityTraces, error, learningRate);
			}
			previousResult = currentResult;
			return bestMove;
		}

		@Override
		public void gameFinished(int winner) {
			if (learning) {
				Matrix actualResult = new Matrix(2, 1);
				if (winner == 1) {
					actualResult.set(0, 0, 1);
				} else
					if (winner == 2) {
						actualResult.set(1, 0, 1);
					}
				for (int k = 0; k < sizes[sizes.length - 1]; k++) {
					previousEligibilityTraces.set(k, net.computeEligibilityTraces(previousEligibilityTraces.get(k), bestActivations, bestUnprocessedActivations, lambda, k));
				}
				Matrix error = previousResult.minus(actualResult);
				net.updateWeights(previousEligibilityTraces, error, learningRate);
				// erase traces
				eraseTraces();
				previousResult = null;
			}
		}

		private Matrix boardToActivation(int[][] board, int turn) {
			Matrix activation = new Matrix(board.length * board[0].length * 2 + 2, 1);
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[0].length; j++) {
					int offset = (i * board[0].length + j) * 2;
					activation.set(offset, 0, (board[i][j] == 1)
											  ? 1
											  : 0);
					activation.set(offset + 1, 0, (board[i][j] == 2)
												  ? 1
												  : 0);
				}
			}
			if (turn % 2 == 0) {
				activation.set(board.length * board[0].length * 2, 0, 1);
				activation.set(board.length * board[0].length * 2 + 1, 0, 0);
			} else {
				activation.set(board.length * board[0].length * 2, 0, 0);
				activation.set(board.length * board[0].length * 2 + 1, 0, 1);
			}

			return activation;
		}

		@Override
		public void setGameSession(GameSession session) {
			this.session = session;
		}

		private static class Sigmoid
				implements TDNeuralNet.Function {
			@Override
			public double func(double value) {
				return 1 / (1 + Math.exp(-value));
			}
		}

		private static class SigmoidPrime
				implements TDNeuralNet.Function {
			Sigmoid sigmoid = new Sigmoid();

			@Override
			public double func(double value) {
				return sigmoid.func(value) * (1 - sigmoid.func(value));
			}
		}
	}

}
