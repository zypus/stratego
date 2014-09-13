package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.GRID_HEIGHT;
import static com.theBombSquad.stratego.StrategoConstants.GRID_WIDTH;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID;
import static com.theBombSquad.stratego.StrategoConstants.UNREVEALED;

/**
 * The gameView acts as the communication interface between players/renderers and the game.
 * The view makes sure that all operations are translated correctly,
 * therefore each player can assume he is player 1.
 *
 * Also host some convenience methods.
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 *
 * @version 1.0
 * @created 10.09.2014
 * @date 13.09.2014
 *
 * @log
 * - skeleton 					10.09.2014
 * - content & documentation 	13.09.2014
 */
@RequiredArgsConstructor
public class GameView {

	private final Game game;												/** Reference to the game. */
	private final PlayerID playerID;										/** PlayerID which defines this views perspective */
	private final List<Move> cashedRotatedMoves = new ArrayList<Move>();	/** List of moves which acts as a cache for rotated move for
	 																			the PLAYER_2 to avoid unnecessary recalculations of move
	 																			rotations at the cost of additional memory costs. */

	/**
	 * Validates the move.
	 * @param move The move in player space.
	 * @return Whether the move is valid or not.
	 */
	public boolean validateMove(Move move) {

		// Translates the move from player space to game space.
		Move preparedMove = (playerID.equals(PlayerID.PLAYER_1)) ? move : rotateMove(move);
		// Assigns the move to the appropriate player.
		preparedMove.setPlayerID(playerID);
		// Forwards the validation to game.
		return game.validateMove(preparedMove);
	}

	/**
	 * Performs the move.
	 * @param move The move in player space.
	 *
	 *             // TODO add pre condition declaration
	 */
	public void performMove(Move move) {

		// Translates the move from player space to game space.
		Move preparedMove = (playerID.equals(PlayerID.PLAYER_1)) ? move : rotateMove(move);
		// Checks if the playerId was already set due to a call to validateMove.
		if (preparedMove.getPlayerID() == null) {
			preparedMove.setPlayerID(playerID);
		}
		// Forwards the move execution to the game.
		game.performMove(preparedMove);
	}

	/**
	 * Validates the setup.
	 * @param setup The setup.
	 * @return Whether the setup is valid or not.
	 */
	public boolean validateSetup(Unit[][] setup) {

		// Forwards the validation to game.
		// Note: Setup doesn't need to be translated to game space for validation because validation is rotation independent.
		return game.validateSetup(setup);
	}

	public void setSetup(Unit[][] setup) {

		game.setSetup((playerID.equals(PlayerID.PLAYER_1)) ? setup : rotateBoard(setup), playerID);
	}

	public int getCurrentTurn() {

		return game.getCurrentTurn();
	}

	public GameBoard getCurrentState() {

		return getState(getCurrentTurn());
	}

	public GameBoard getState(int turn) {

		GameBoard gameBoard;
		if (playerID.equals(PlayerID.PLAYER_1)) {
			gameBoard = obscureBoard(game.getState(turn), turn);
		} else {
			gameBoard = obscureAndRotateBoard(game.getState(turn), turn);
		}
		return gameBoard;
	}

	public Unit getUnit(int x, int y) {

		Pair<Integer, Integer> coords = conditionalCoordinateRotation(x, y);
		Unit unit = game.getCurrentState().getUnit(coords.getKey(), coords.getValue());
		return obscureUnitIfNecessary(unit, getCurrentTurn());
	}

	public boolean isAir(int x, int y) {

		return getUnit(x,y) == Unit.AIR;
	}

	public boolean isLake(int x, int y) {

		return getUnit(x,y) == Unit.LAKE;
	}

	public boolean isUnknown(int x, int y) {

		return getUnit(x,y) == Unit.UNKNOWN;
	}

	public List<Move> getMoves() {

		List<Move> moves = game.getMoves();
		// if the player is player 2 then rotate all not cashed moves and add them to the cashed list
		if (playerID.equals(PlayerID.PLAYER_2)) {
			for (int t = cashedRotatedMoves.size(); t < moves.size(); t++) {
				Move move = moves.get(t);

				Move rotatedMove = rotateAndCopyMove(move);

				cashedRotatedMoves.add(rotatedMove);
			}
		}
		return (playerID.equals(PlayerID.PLAYER_1)) ? moves : cashedRotatedMoves;
	}

	public Move getMove(int turn) {

		// TODO check if the turns start at 0
		return getMoves().get(turn);
	}

	public Move getLastMove() {

		return getMove(game.getCurrentTurn()-1);
	}

	public List<Unit> getAllDefeatedUnits() {

		List<Unit> allList = new ArrayList<Unit>();
		allList.addAll(game.getDefeatedUnitsPlayer1());
		allList.addAll(game.getDefeatedUnitsPlayer2());

		return Collections.unmodifiableList(allList);
	}

	public List<Unit> getOwnDefeatedUnits() {

		return playerID.equals(PlayerID.PLAYER_1) ? game.getDefeatedUnitsPlayer1() : game.getDefeatedUnitsPlayer2();
	}

	public List<Unit> getOpponentsDefeatedUnits() {

		return playerID.equals(PlayerID.PLAYER_2) ? game.getDefeatedUnitsPlayer1() : game.getDefeatedUnitsPlayer2();
	}

	// HELPER METHODS

	private GameBoard obscureBoard(GameBoard gameBoard, int turn) {

		GameBoard copiedGameBoard = gameBoard.duplicate();
		for (int y = 0; y < copiedGameBoard.getHeight(); y++) {
			for (int x = 0; x < copiedGameBoard.getWidth(); x++) {
				copiedGameBoard.setUnit(x, y, obscureUnitIfNecessary(copiedGameBoard.getUnit(x,y), turn));
			}
		}
		return copiedGameBoard;
	}

	private GameBoard obscureAndRotateBoard(GameBoard gameBoard, int turn) {

		GameBoard copiedGameBoard = gameBoard.duplicate();
		for (int y = 0; y < copiedGameBoard.getHeight(); y++) {
			for (int x = 0; x < copiedGameBoard.getWidth(); x++) {
				copiedGameBoard.setUnit(gameBoard.getWidth()-x, gameBoard.getHeight()-y, obscureUnitIfNecessary(gameBoard.getUnit(x, y), turn));
			}
		}
		return copiedGameBoard;
	}

	private Unit obscureUnitIfNecessary(Unit unit, int turn) {
		if (!unit.getOwner().equals(PlayerID.NEMO) && !unit.getOwner().equals(playerID) && (unit.getRevealedInTurn() == UNREVEALED || unit.getRevealedInTurn() > turn)) {
			return Unit.UNKNOWN;
		} else {
			return unit;
		}
	}

	private Pair<Integer, Integer> conditionalCoordinateRotation(int x, int y) {
		Pair<Integer, Integer> coord = new Pair<Integer, Integer>(-1,-1);
		if (playerID.equals(PlayerID.PLAYER_1)) {
			coord = new Pair<Integer, Integer>(x,y);
		} else if (playerID.equals(PlayerID.PLAYER_2)) {
			coord = new Pair<Integer, Integer>(GRID_WIDTH - x, GRID_HEIGHT - y);
		} else {
			System.out.println("Invalid player ID");
		}
		return coord;
	}

	private Move rotateMove(Move move) {
		return new Move(GRID_WIDTH - move.getFromX(),
									GRID_HEIGHT - move.getFromY(),
									GRID_WIDTH - move.getToX(),
									GRID_HEIGHT - move.getToY());
	}

	private Move rotateAndCopyMove(Move move) {
		Move rotatedMove = rotateMove(move);
		rotatedMove.setTurn(move.getTurn());
		rotatedMove.setEncounter(move.getEncounter());
		rotatedMove.setMovedUnit(move.getMovedUnit());
		rotatedMove.setPlayerID(move.getPlayerID());
		return rotatedMove;
	}

	private Unit[][] rotateBoard(Unit[][] board) {
		int width = board.length;
		int height = board[0].length;
		Unit[][] rotatedBoard = new Unit[width][height];
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				rotatedBoard[width-y][width-x] = board[x][y];
			}
		}
		return rotatedBoard;
	}

}
