package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;

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
@Log
public class GameView {

	private final Game game;												/** Reference to the game. */
	@Getter private final PlayerID playerID;								/** PlayerID which defines this views perspective */
	private final List<Move> cashedRotatedMoves = new ArrayList<Move>();	/** List of moves which acts as a cache for rotated move for
	 																			the PLAYER_2 to avoid unnecessary recalculations of move
	 																			rotations at the cost of additional memory costs. */

	/**
	 * Validates the move.
	 * @param move The move in player space.
	 * @return Whether the move is valid or not.
	 */
	public boolean validateMove(Move move) {

		// Return early if the view doesn't belong to a player
		if (playerID.equals(NEMO)) {
			log.severe("Non player tried to validate move.");
			return false;
		}
		// Translates the move from player space to game space.
		Move preparedMove = (playerID.equals(PLAYER_1)) ? move : rotateMove(move);
		// Assigns the move to the appropriate player.
		if (preparedMove.getPlayerID() == null) {
			preparedMove.setPlayerID(playerID);
		}
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

		// Return early if the view doesn't belong to a player
		if (playerID.equals(NEMO)) {
			log.severe("Non player tried to perform move.");
			return;
		}
		// Translates the move from player space to game space.
		Move preparedMove = (playerID.equals(PLAYER_1)) ? move : rotateMove(move);
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
	public boolean validateSetup(Setup setup) {

		// Forwards the validation to game.
		// Note: Setup doesn't need to be translated to game space for validation because validation is rotation independent.
		return game.validateSetup(setup);
	}

	/**
	 * Sets the setup.
	 * @param setup The army setup in player space.
	 */
	public void setSetup(Setup setup) {

		// Return early if the view doesn't belong to a player
		if (playerID.equals(NEMO)) {
			log.severe("Non player tried to set setup.");
			return;
		}
		// Translates the setup from player space to game space.
		Setup preparedSetup = (playerID.equals(PLAYER_1)) ? setup : rotateSetup(setup);
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
		// If the game view owner is not a player return an unobscured game board copy.
		if (playerID.equals(NEMO)) {
			gameBoard = game.getState(turn).duplicate();
		}
		else if (playerID.equals(PLAYER_1)) {
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

		return getUnit(x,y,getCurrentTurn());
	}

	public Unit getUnit(int x, int y, int turn) {
		// Translate the coordinates from player space to game space.
		Point coords = conditionalCoordinateRotation(x, y);
		Unit unit = game.getState(turn).getUnit(coords.x, coords.y);
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

		return isAir(x, y, getCurrentTurn());
	}

	/**
	 * Checks if the location contains a lake.
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return Whether the location contains a lake or not.
	 */
	public boolean isLake(int x, int y) {

		return isLake(x,y,getCurrentTurn());
	}

	/**
	 * Checks if the location contains a unit that is unknown to the assigned player.
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return Whether the location contains a unknown unit or not.
	 */
	public boolean isUnknown(int x, int y) {

		return isUnknown(x, y, getCurrentTurn());
	}

	public boolean isAir(int x, int y, int turn) {

		return getUnit(x,y,turn).isAir();
	}

	/**
	 * Checks if the location contains a lake.
	 *
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return Whether the location contains a lake or not.
	 */
	public boolean isLake(int x, int y, int turn) {

		return getUnit(x, y, turn).isLake();
	}

	/**
	 * Checks if the location contains a unit that is unknown to the assigned player.
	 *
	 * @param x X Coordinate in player space.
	 * @param y Y Coordinate in player space.
	 * @return Whether the location contains a unknown unit or not.
	 */
	public boolean isUnknown(int x, int y, int turn) {

		return getUnit(x, y, turn).isUnknown();
	}

	public boolean isEnemy(int x, int y) {
		return isEnemy(x, y, getCurrentTurn());
	}

	public boolean isEnemy(int x, int y, int turn) {
		PlayerID opponent = PLAYER_1;
		if (this.playerID.equals(opponent)) {
			opponent = PLAYER_2;
		}
		Unit unit = getUnit(x, y, turn);
		PlayerID unitOwner = unit.getOwner();
		return unitOwner.equals(opponent) || unit.isUnknown();
	}

	public boolean willWin(int ownX, int ownY, int targetX, int targetY){
		if(isEnemy(targetX, targetY)){
			Unit own = getUnit(ownX, ownY);
			if(own.getOwner().equals(playerID)){
				Unit foe = getUnit(targetX, targetY);
				if(foe.isUnknown()){
					int ownRank = own.getType().getRank();
					int foeRank = foe.getType().getRank();
					if(ownRank>foeRank || (foeRank==Unit.UnitType.MARSHAL.getRank() && ownRank==Unit.UnitType.SPY.getRank())){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get all moves played so far.
	 * @return All played moves.
	 */
	public List<Move> getMoves() {

		List<Move> moves = game.getMoves();
		// if the player is player 2 then translate all uncashed moves and add them to the cashed list
		if (playerID.equals(PLAYER_2)) {
			for (int t = cashedRotatedMoves.size(); t < moves.size(); t++) {
				Move move = moves.get(t);

				Move rotatedMove = rotateAndCopyMove(move);

				cashedRotatedMoves.add(rotatedMove);
			}
		}
		return (playerID.equals(PLAYER_2)) ? cashedRotatedMoves : moves;
	}

	/**
	 * Gets the move from the specified turn.
	 * @param turn The turn number.
	 * @return The move of the turn.
	 */
	public Move getMove(int turn) {

		// TODO Check if the turns start at 0
		// TODO Check if the turn number is out of bounds.
		if (turn >= getMoves().size()) {
			return null;
		}
		return getMoves().get(turn);
	}

	/**
	 * Gets the most recent move.
	 * @return The most recent move.
	 */
	public Move getLastMove() {
		return getMove(game.getCurrentTurn() - 2);
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

		// Return early if the view doesn't belong to a player
		if (playerID.equals(NEMO)) {
			log.severe("Non player tried to get 'his' defeated units.");
			return null;
		}
		List<Unit> units = playerID.equals(PLAYER_1) ? game.getDefeatedUnitsPlayer1() : game.getDefeatedUnitsPlayer2();
		return Collections.unmodifiableList(units);
	}

	/**
	 * Gets all defeated units of the opponent of the assigned player.
	 * @return All defeated units of the opponent of the assigned player.
	 */
	public List<Unit> getOpponentsDefeatedUnits() {
		// Return early if the view doesn't belong to a player
		if (playerID.equals(NEMO)) {
			log.severe("Non player tried to get 'his opponents' defeated units.");
			return null;
		}
		List<Unit> units = playerID.equals(PLAYER_2) ? game.getDefeatedUnitsPlayer1() : game.getDefeatedUnitsPlayer2();
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
				copiedGameBoard.setUnit(gameBoard.getWidth()-x-1, gameBoard.getHeight()-y-1, obscureUnitIfNecessary(gameBoard.getUnit(x, y), turn));
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
		if (!playerID.equals(NEMO) && !unit.getOwner().equals(NEMO) && !unit.getOwner().equals(playerID) && (unit.getRevealedInTurn() == UNREVEALED || unit.getRevealedInTurn() > turn)) {
			return Unit.UnknownUnitPool.getInstance().getUnknownForUnit(unit);
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
		if (playerID.equals(PLAYER_1) || playerID.equals(NEMO)) {
			// Player 1 coordinates are equal to game space coordinates.
			coord = new Point(x,y);
		} else if (playerID.equals(PLAYER_2)) {
			// Player 2 coordinates need to be rotated by 180 degree to be in game space.
			coord = new Point(GRID_WIDTH - x - 1, GRID_HEIGHT - y - 1);
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
		return new Move(GRID_WIDTH - move.getFromX() - 1,
									GRID_HEIGHT - move.getFromY() - 1,
									GRID_WIDTH - move.getToX() - 1,
									GRID_HEIGHT - move.getToY() - 1);
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
	 * Rotates the setup by 180 degree.
	 * @param setup The setup.
	 * @return The rotated setup.
	 */
	private Setup rotateSetup(Setup setup) {
		int width = setup.getWidth();
		int height = setup.getHeight();
		Setup rotatedSetup = new Setup(width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				rotatedSetup.setUnit(width-x-1, height-y-1, setup.getUnit(x,y));
			}
		}
		return rotatedSetup;
	}

	/** sets all the units on the top of the board so we can start making the setup */
	public void startSetup() {
		for(int cy=0; cy<getCurrentState().getHeight(); cy++){
			for(int cx=0; cx<getCurrentState().getWidth(); cx++){
				if(game.getCurrentState().getUnit(cx, cy).getType().getRank()>=0){
					game.getCurrentState().setUnit(cx, cy, Unit.AIR);
				}
			}
		}
		GameBoard board=game.getCurrentState();
		List<Unit> units = getAvailableUnits();
		int counter = 0;
		for(int i = 0; i<board.getWidth();i++){
			for (int j = 0; j < 4; j++) {
				Point coords1 = conditionalCoordinateRotation(i, j);
				board.setUnit(coords1.x, coords1.y, units.get(counter));
				counter++;
			}
		}
	}
	/** Returns whether the given X|Y is legal to be moved to */
	public boolean walkable(int x, int y){
		return game.getCurrentState().isInBounds(x, y) && (isEnemy(x, y) || isAir(x, y));
	}

	/** Swaps around two units on the map (Only to be used in Setup) (Does not consider board flipping) */
	public void hardSwapUnits(int x1, int y1, int x2, int y2){
		//Get Relative Position
		Point coords1 = conditionalCoordinateRotation(x1, y1);
		Point coords2 = conditionalCoordinateRotation(x2, y2);
		//Store one unit
		Unit helpUnit = getUnit(x1, y1);
		//Actually Swaps Units
		setUnit(x1, y1, getUnit(x2, y2));
		setUnit(x2, y2, helpUnit);
	}

	public void setUnit(int x, int y, Unit unit){
		Point coords1 = conditionalCoordinateRotation(x, y);
		game.getCurrentState().setUnit(coords1.x, coords1.y, unit);
	}

	public List<Unit> getAvailableUnits() {
		return (playerID == PLAYER_1) ? game.getPlayer1Units() : game.getPlayer2Units();
	}

	public void finishedCleanup() {
		if (playerID != NEMO) {
			game.finishedCleanup(playerID);
		}
	}

}
