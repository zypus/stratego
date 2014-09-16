package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.GRID_HEIGHT;
import static com.theBombSquad.stratego.StrategoConstants.GRID_WIDTH;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID;
import static com.theBombSquad.stratego.StrategoConstants.UNREVEALED;

/**
 * The gameView acts as the communication interface between players/renderers and the game.
 * Each user of the view can assume he is PLAYER_1:
 * The view takes care of all necessary translations between the player space and the game space.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 *
 * @version 1.0
 * @created 10.09.2014
 * @date 13.09.2014
 *
 * @log
 * - skeleton 					10.09.2014
 * - implementation & documentation 	13.09.2014
 */
@RequiredArgsConstructor
public class GameView {

	private final Game game;												/** Reference to the game. */
	@Getter private final PlayerID playerID;										/** PlayerID which defines this views perspective */
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
	 * TODO Add pre condition declaration
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
	 * @param setup The army setup in player space.
	 * @return Whether the setup is valid or not.
	 */
	public boolean validateSetup(Unit[][] setup) {

		// Forwards the validation to game.
		// Note: Setup doesn't need to be translated to game space for validation because validation is rotation independent.
		return game.validateSetup(setup);
	}

	/**
	 * Sets the setup.
	 * @param setup The army setup in player space.
	 */
	public void setSetup(Unit[][] setup) {

		// Translates the setup from player space to game space.
		Unit[][] preparedSetup = (playerID.equals(PlayerID.PLAYER_1)) ? setup : rotateBoard(setup);
		// Forwards the setup to the game and sets the correct player reference.
		game.setSetup(preparedSetup, playerID);
	}

	/**
	 * Gets the current turn number.
	 * @return The number of the current turn.
	 */
	public int getCurrentTurn() {

		return game.getCurrentTurn();
	}

	/**
	 * Gets the most recent game state.
	 * @return The most recent game state.
	 */
	public GameBoard getCurrentState() {

		return getState(getCurrentTurn());
	}

	/**
	 * Gets the state of the game at the specified turn.
	 * @param turn The turn number.
	 * @return The state of the game at turn.
	 */
	public GameBoard getState(int turn) {

		GameBoard gameBoard;
		if (playerID.equals(PlayerID.PLAYER_1)) {
			// Obscure all units on the board which should be unknown to the player assigned to this view.
			gameBoard = obscureBoard(game.getState(turn), turn);
		} else {
			// Obscure (see previous comment) and translate the board to the player 2 space.
			gameBoard = obscureAndRotateBoard(game.getState(turn), turn);
		}
		return gameBoard;
	}

	/**
	 * Gets the unit at the specified location.
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return The unit at the location.
	 */
	public Unit getUnit(int x, int y) {

		// Translate the coordinates from player space to game space.
		Point coords = conditionalCoordinateRotation(x, y);
		Unit unit = game.getCurrentState().getUnit(coords.x, coords.y);
		// Unit needs to be obscured if the assigned player shouldn't be able to see that unit.
		return obscureUnitIfNecessary(unit, getCurrentTurn());
	}

	/**
	 * Checks if the location contains air / free space.
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return Whether the location contains air or not.
	 */
	public boolean isAir(int x, int y) {

		return getUnit(x,y) == Unit.AIR;
	}

	/**
	 * Checks if the location contains a lake.
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return Whether the location contains a lake or not.
	 */
	public boolean isLake(int x, int y) {

		return getUnit(x,y) == Unit.LAKE;
	}

	/**
	 * Checks if the location contains a unit that is unknown to the assigned player.
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return Whether the location contains a unknown unit or not.
	 */
	public boolean isUnknown(int x, int y) {

		return getUnit(x,y) == Unit.UNKNOWN;
	}

	/**
	 * Get all moves played so far.
	 * @return All played moves.
	 */
	public List<Move> getMoves() {

		List<Move> moves = game.getMoves();
		// if the player is player 2 then translate all uncashed moves and add them to the cashed list
		if (playerID.equals(PlayerID.PLAYER_2)) {
			for (int t = cashedRotatedMoves.size(); t < moves.size(); t++) {
				Move move = moves.get(t);

				Move rotatedMove = rotateAndCopyMove(move);

				cashedRotatedMoves.add(rotatedMove);
			}
		}
		return (playerID.equals(PlayerID.PLAYER_1)) ? moves : cashedRotatedMoves;
	}

	/**
	 * Gets the move from the specified turn.
	 * @param turn The turn number.
	 * @return The move of the turn.
	 */
	public Move getMove(int turn) {

		// TODO Check if the turns start at 0
		// TODO Check if the turn number is out of bounds.
		return getMoves().get(turn);
	}

	/**
	 * Gets the most recent move.
	 * @return The most recent move.
	 */
	public Move getLastMove() {

		return getMove(game.getCurrentTurn()-1);
	}

	/**
	 * Gets all defeated units.
	 * @return All defeated units.
	 */
	public List<Unit> getAllDefeatedUnits() {

		// Simple concatenates the list of the defeated units of player 1 and 2
		List<Unit> allList = new ArrayList<Unit>();
		allList.addAll(game.getDefeatedUnitsPlayer1());
		allList.addAll(game.getDefeatedUnitsPlayer2());

		// Makes sure that the player cannot modify the list.
		return Collections.unmodifiableList(allList);
	}

	/**
	 * Gets all defeated units of the assigned player.
	 * @return All defeated units of the assigned player.
	 */
	public List<Unit> getOwnDefeatedUnits() {

		List<Unit> units = playerID.equals(PlayerID.PLAYER_1) ? game.getDefeatedUnitsPlayer1() : game.getDefeatedUnitsPlayer2();
		return Collections.unmodifiableList(units);
	}

	/**
	 * Gets all defeated units of the opponent of the assigned player.
	 * @return All defeated units of the opponent of the assigned player.
	 */
	public List<Unit> getOpponentsDefeatedUnits() {

		List<Unit> units = playerID.equals(PlayerID.PLAYER_2) ? game.getDefeatedUnitsPlayer1() : game.getDefeatedUnitsPlayer2();
		return Collections.unmodifiableList(units);
	}

	// HELPER METHODS

	/**
	 * Obscures the board for the assigned player.
	 * @param gameBoard The board.
	 * @param turn The turn number.
	 * @return The board where relevant units are obscured.
	 */
	private GameBoard obscureBoard(GameBoard gameBoard, int turn) {

		GameBoard copiedGameBoard = gameBoard.duplicate();
		for (int y = 0; y < copiedGameBoard.getHeight(); y++) {
			for (int x = 0; x < copiedGameBoard.getWidth(); x++) {
				copiedGameBoard.setUnit(x, y, obscureUnitIfNecessary(copiedGameBoard.getUnit(x,y), turn));
			}
		}
		return copiedGameBoard;
	}

	/**
	 * Obscures and translates the board to game space for the assigned player.
	 * @param gameBoard The board.
	 * @param turn The turn number.
	 * @return The obscured and translated board.
	 */
	private GameBoard obscureAndRotateBoard(GameBoard gameBoard, int turn) {

		GameBoard copiedGameBoard = gameBoard.duplicate();
		for (int y = 0; y < copiedGameBoard.getHeight(); y++) {
			for (int x = 0; x < copiedGameBoard.getWidth(); x++) {
				copiedGameBoard.setUnit(gameBoard.getWidth()-x, gameBoard.getHeight()-y, obscureUnitIfNecessary(gameBoard.getUnit(x, y), turn));
			}
		}
		return copiedGameBoard;
	}

	/**
	 * Obscured the unit if the player shouldn't know the unit.
	 * @param unit The unit.
	 * @param turn The turn number.
	 * @return The unit itself or an obscured unit.
	 */
	private Unit obscureUnitIfNecessary(Unit unit, int turn) {
		if (!unit.getOwner().equals(PlayerID.NEMO) && !unit.getOwner().equals(playerID) && (unit.getRevealedInTurn() == UNREVEALED || unit.getRevealedInTurn() > turn)) {
			return Unit.UNKNOWN;
		} else {
			return unit;
		}
	}

	/**
	 * Translates coordinates in player space to game space.
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return Coordinates in game space.
	 */
	private Point conditionalCoordinateRotation(int x, int y) {
		Point coord = new Point(-1,-1);
		if (playerID.equals(PlayerID.PLAYER_1)) {
			// Player 1 coordinates are equal to game space coordinates.
			coord = new Point(x,y);
		} else if (playerID.equals(PlayerID.PLAYER_2)) {
			// Player 2 coordinates need to be rotated by 180 degree to be in game space.
			coord = new Point(GRID_WIDTH - x, GRID_HEIGHT - y);
		} else {
			System.out.println("Invalid player ID");
		}
		return coord;
	}

	/**
	 * Rotates a moves coordinates by 180 degree.
	 * @param move The move.
	 * @return Rotated move.
	 */
	private Move rotateMove(Move move) {
		return new Move(GRID_WIDTH - move.getFromX(),
									GRID_HEIGHT - move.getFromY(),
									GRID_WIDTH - move.getToX(),
									GRID_HEIGHT - move.getToY());
	}

	/**
	 * Rotates a moves coordinates by 180 degree and copies all field of the move.
	 * @param move The move.
	 * @return Rotated move.
	 */
	private Move rotateAndCopyMove(Move move) {
		Move rotatedMove = rotateMove(move);
		rotatedMove.setTurn(move.getTurn());
		rotatedMove.setEncounter(move.getEncounter());
		rotatedMove.setMovedUnit(move.getMovedUnit());
		rotatedMove.setPlayerID(move.getPlayerID());
		return rotatedMove;
	}

	/**
	 * Rotates the board by 180 degree.
	 * @param board The board.
	 * @return The rotated board.
	 */
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
