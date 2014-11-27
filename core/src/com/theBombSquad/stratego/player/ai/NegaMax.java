package com.theBombSquad.stratego.player.ai;

import static com.theBombSquad.stratego.StrategoConstants.UNMOVED;
import static com.theBombSquad.stratego.StrategoConstants.UNREVEALED;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID.PLAYER_1;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID.PLAYER_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;

public class NegaMax extends AI {
	private int maxDepth = 3;
	private double flagValue = 10000;

	public NegaMax(GameView gameView) {
		super(gameView);
	}

	@Override
	protected Move move() {
		AIGameState state = super.createAIGameState(gameView);
		GameBoard[] boards = null; // Flo's method
		Move[] MaxMoves = new Move[boards.length];
		int[] maxMoveValues = new int[boards.length];

		List<Move> possibleMoves = AI.createAllLegalMoves(gameView,
				gameView.getCurrentState());
		for (int i = 0; i < boards.length; i++) {
			for (int j = 0; j < possibleMoves.size(); j++) {
				int moveValue = evaluateMoveOpponent(boards[i],
						possibleMoves.get(j), maxDepth - 1);
				if (moveValue > maxMoveValues[i] || MaxMoves[i] == null) {
					MaxMoves[i] = possibleMoves.get(j);
					maxMoveValues[i] = moveValue;
				}
			}
		}

		int rank = maxRank(maxMoveValues);
		gameView.performMove(MaxMoves[rank]);
		return MaxMoves[rank];
	}

	private double evaluateMoveOpponent(GameBoard gameBoard, Move move,
			int depthLeft) {
		PlayerID opponentID;
		if (gameView.getPlayerID() == PLAYER_1) {
			opponentID = PLAYER_2;
		} else {
			opponentID = PLAYER_1;
		}
		if(flagDefeated(opponentID){
			return flagValue;
			}
		else if (depthLeft == 1) {
			return evaluate(gameBoard);
		} else {
			// finds possible units for the opponent
			List<Move> possibleMoves = AI.createAllLegalMoves(opponentID,
					gameBoard);
			double[] MoveValues = new double[possibleMoves.size()];
			for (int i = 0; i < possibleMoves.size(); i++) {
				GameBoard gameBoardDup = gameBoard.duplicate();
				performMove(gameView.getPlayerID(), gameBoardDup,
						possibleMoves.get(i));
				MoveValues[i] = evaluateMovePlayer(gameBoardDup,
						possibleMoves.get(i), depthLeft - 1);
			}
			return MoveValues[minRank(MoveValues)];
		}
	}

	private boolean flagDefeated(PlayerID opponentID, GameBoard gameBoard) {
		boolean defeated = true;
		for (int i = 0; i < gameBoard.getHeight(); i++) {
			for (int j = 0; j < gameBoard.getWidth(); j++) {
				if (gameBoard.getUnit(i, j).getType() == UnitType.FLAG
						&& gameBoard.getUnit(i, j).getPlayerID() == opponentID) {
					return false;
				}

			}
		}
		return true;
	}

	private double evaluateMovePlayer(GameBoard gameBoard, Move move,
			int depthLeft) {
		if (flagDefeated(gameView.getPlayerID())) {
			return -flagValue;
		} else if (depthLeft == 1) {
			return evaluate(gameBoard);
		} else {
			// finds possible units for the opponent
			List<Move> possibleMoves = AI.createAllLegalMoves(gameView,
					gameView.getCurrentState());
			double[] MoveValues = new double[possibleMoves.size()];
			for (int i = 0; i < possibleMoves.size(); i++) {
				GameBoard gameBoardDup = gameBoard.duplicate();
				performMove(gameView.getPlayerID(), gameBoardDup,
						possibleMoves.get(i));
				MoveValues[i] = evaluateMoveOpponent(gameBoardDup,
						possibleMoves.get(i), depthLeft - 1);
			}
			return MoveValues[maxRank(MoveValues)];
		}
	}

	private void performMove(PlayerID playerID, GameBoard gameBoard, Move move) {
		GameBoard current = gameBoard.duplicate();
		Unit movedUnit = current.getUnit(move.getFromX(), move.getFromY());
		// if moved to air just set the air to unit
		if (current.getUnit(move.getToX(), move.getToY()).getType() == Unit.UnitType.AIR) {
			current.setUnit(move.getToX(), move.getToY(), movedUnit);
		} else {
			// checks who is the winner
			Encounter encounter = new Encounter(movedUnit, current.getUnit(
					move.getToX(), move.getToY()));
			move.setEncounter(encounter);
			Unit winner = encounter.getVictoriousUnit();
			// if there is no winner then sets the field to air
			if (winner == null) {
				current.setUnit(move.getToX(), move.getToY(), Unit.AIR);
			}
			// else sets the winner to the spot
			else {
				current.setUnit(move.getToX(), move.getToY(), winner);
			}
		}
		// sets the unit that is moved to air
		current.setUnit(move.getFromX(), move.getFromY(), Unit.AIR);
	}

	private int maxRank(double[] maxMoveValues) {
		double max = 0;
		int rank = -1;
		for (int i = 0; i < maxMoveValues.length; i++) {
			if (maxMoveValues[i] > max) {
				rank = i;
				max = maxMoveValues[i];
			}
		}
		return rank;
	}

	private int minRank(double[] minMoveValues) {
		double min = Double.MAX_VALUE;
		int rank = -1;
		for (int i = 0; i < minMoveValues.length; i++) {
			if (minMoveValues[i] < min) {
				rank = i;
				min = minMoveValues[i];
			}
		}
		return rank;
	}

	@Override
	protected Setup setup() {
	}
}