package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.StrategoConstants.*;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;
import com.theBombSquad.stratego.player.remote.RemoteServingPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;
import static com.theBombSquad.stratego.rendering.StrategoUtil.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 * @author Mateusz Garbacz
 */
@Getter(AccessLevel.PRIVATE)
@Log
public class Game {

	int currentTurn = 1;

	private List<GameBoard> states;
	private static GameBoard current; // to be initialized
	private List<Move> moves;
	private List<Unit> defeatedUnitsPlayer1;
	private List<Unit> defeatedUnitsPlayer2;
	@Getter private Player player1 = null;
	@Getter private Player player2 = null;
	@Getter private boolean player1FinishedSetup = false;
	@Getter private boolean player2FinishedSetup = false;
	@Getter private boolean player1FinishedCleanup = true;
	@Getter private boolean player2FinishedCleanup = true;
	@Getter private boolean finishedSetup = false;
	@Getter
	private Player winner;
	private int player1ChaseBegin = 0;
	private int player2ChaseBegin = 0;

	/** The Setups both players committed thus far */
	private Map<PlayerID, List<Move>> lastConsecutiveMoves = new HashMap<PlayerID, List<Move>>();
	@Getter private GameView activeGameView;
	@Getter private boolean gameOver;
	@Getter private boolean reseted = true;
	@Getter @Setter private boolean blind = false;

	@Getter private List<Unit> player1Units;
	@Getter private List<Unit> player2Units;
	@Getter @Setter private boolean waitingForEndTurn;

	public Game() {
		states = new ArrayList<GameBoard>();
		// add in the initial board
		states.add(new GameBoard(GRID_WIDTH, GRID_HEIGHT, DEFAULT_LAKES));
		current = states.get(0);
		moves = new ArrayList<Move>();
		defeatedUnitsPlayer1 = new ArrayList<Unit>();
		defeatedUnitsPlayer2 = new ArrayList<Unit>();

		player1Units = Collections.unmodifiableList(createUnitsForPlayer(PLAYER_1));
		player2Units = Collections.unmodifiableList(createUnitsForPlayer(PLAYER_2));

		reset();
	}

	private List<Unit> createUnitsForPlayer(PlayerID playerID) {
		List<Unit> availableUnits = new ArrayList<Unit>(40);
		Unit.UnitType[] unitTypeEnum = Unit.UnitType.values();
		// create a list containing all units that needs to be placed on the board
		for (Unit.UnitType type : unitTypeEnum) {
			for (int i = 0; i < type.getQuantity(); i++) {
				availableUnits.add(new Unit(type, playerID));
			}
		}
		return availableUnits;
	}

	public void reset() {
		if (player1FinishedCleanup && player2FinishedCleanup) {
			currentTurn = 1;
			reseted = true;
			player1 = null;
			player2 = null;
			player1FinishedSetup = false;
			player2FinishedSetup = false;
			player1ChaseBegin = 0;
			player2ChaseBegin = 0;
			gameOver = false;
			finishedSetup = false;
			states.clear();
			states.add(new GameBoard(GRID_WIDTH, GRID_HEIGHT, DEFAULT_LAKES));
			current = states.get(0);
			moves.clear();
			winner = null;
			defeatedUnitsPlayer1.clear();
			defeatedUnitsPlayer2.clear();
			lastConsecutiveMoves.put(PLAYER_1, new ArrayList<Move>());
			lastConsecutiveMoves.put(PLAYER_2, new ArrayList<Move>());
			clearUnits(PLAYER_1);
			clearUnits(PLAYER_2);
		}
	}

	private void clearUnits(PlayerID playerID) {
		List<Unit> units = (playerID == PLAYER_1) ? player1Units : player2Units;
		for (Unit unit : units) {
			unit.setRevealedInTurn(UNREVEALED);
		}
	}

	private boolean validateMove(Move move) {
		/**
		 * checks if a move is valid
		 */
		int fromX = move.getFromX();
		int fromY = move.getFromY();
		int toX = move.getToX();
		int toY = move.getToY();
		int distanceX = Math.abs(fromX - toX);
		int distanceY = Math.abs(fromY - toY);
		boolean validScoutMove = true;
		if (distanceX == 0 && distanceY == 0) {
			return false;
		}
		if(toX<0||toX>9||toY<0||toY>9){
			return false;
		}
		if (move.getPlayerID() != current.getUnit(fromX, fromY).getOwner()) {
			return false;
		}
		// check end position if it is not lake
		if (current.getUnit(toX, toY).getType() == Unit.UnitType.LAKE) {
			return false;
		}
		// if we attack unit of ours then false
		if (move.getPlayerID() == current.getUnit(toX, toY).getOwner()) {
			return false;
		}
		// if move from to is the same spot
		else if (distanceX == 0 && distanceY == 0) {
			return false;
		}
		// check if it is vertical or horizontal move
		else if (distanceX != 0 && distanceY != 0) {
			return false;
		}

		// first we check if one of distances is equal to one
		// if place from which the move comes is either air, lake, bomb or flag
		// then it is not valid

		else if (distanceX == 1 || distanceY == 1) {
			if (current.getUnit(fromX, fromY).getType() == Unit.UnitType.AIR
					|| current.getUnit(fromX, fromY).getType() == Unit.UnitType.LAKE
					|| current.getUnit(fromX, fromY).getType() == Unit.UnitType.BOMB
					|| current.getUnit(fromX, fromY).getType() == Unit.UnitType.FLAG) {
				return false;
			}

		}

		// if none of distances is 1 then one of them must be longer than one
		else {
			// check how long is the step, only scout can go more than one cell
			if (distanceX > 1) {
				if (current.getUnit(fromX, fromY).getType() == Unit.UnitType.SCOUT) {
					// if it is a scout and it goes right we check all the steps
					// between
					if (toX - fromX > 0) {
						for (int i = fromX + 1; i <= toX - 1; i++) {
							if (current.getUnit(i, fromY).getType() != Unit.UnitType.AIR) {
								validScoutMove = false;
							}
						}
						discoverSpy();
					} else {
						for (int i = toX + 1; i <= fromX - 1; i++) {
							if (current.getUnit(i, fromY).getType() != Unit.UnitType.AIR) {
								validScoutMove = false;
							}
						}
						discoverSpy();
					}

				} else {
					validScoutMove = false;
				}
			} else if (distanceY > 1) {
				if (current.getUnit(fromX, fromY).getType() == Unit.UnitType.SCOUT) {
					if (toY - fromY > 0) {
						for (int i = fromY + 1; i <= toY - 1; i++) {
							if (current.getUnit(fromX, i).getType() != Unit.UnitType.AIR) {
								validScoutMove = false;
							}
						}
						discoverSpy();
					} else {
						for (int i = toY + 1; i <= fromY - 1; i++) {
							if (current.getUnit(fromX, i).getType() != Unit.UnitType.AIR) {
								validScoutMove = false;
							}
						}
						discoverSpy();
					}
				}

				else {
					validScoutMove = false;
				}
			}
		}
		if (!twoSquareRuleValidation(move)) {
			return false;
		}
		if (!chaseRuleValidation(move)) {
			return false;
		}
		return validScoutMove;
	}

	private void discoverSpy() {
		/**
		 * when a spy moves by a few fields it is discovered, dunno where to
		 * implement it :P
		 */
	}

	private void performMove(Move move) {
		/**
		 * performs move depending on the type of unit, considers also encounter
		 */
		moves.add(move);

		if ((states.size() % 2 == 1 && move.getPlayerID() == PLAYER_1)
				|| (states.size() % 2 == 0 && move.getPlayerID() == PLAYER_2)) {
			GameBoard nextState = current.duplicate();
			states.add(nextState);
			current = nextState;
			Unit movedUnit = current.getUnit(move.getFromX(), move.getFromY());
			move.setTurn(getCurrentTurn());
			move.setMovedUnit(movedUnit);
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
					// Reveals the victorious unit
					if (winner.getRevealedInTurn() == UNREVEALED) {
						winner.setRevealedInTurn(states.size());
					}
				}
				Unit[] loosers = encounter.getDefeatedUnits();
				for (int i = 0; i < loosers.length; i++) {
					Unit looser = loosers[i];
					if (looser.getOwner() == PLAYER_1) {
						defeatedUnitsPlayer1.add(looser);
					} else {
						defeatedUnitsPlayer2.add(looser);
					}
					if (looser.getRevealedInTurn() == UNREVEALED) {
						looser.setRevealedInTurn(states.size());
					}
				}

			}
			if (movedUnit.getType() == Unit.UnitType.SCOUT && move.getDistance() > 1 && movedUnit.getRevealedInTurn() == UNREVEALED) {
				movedUnit.setRevealedInTurn(states.size());
			}
			// sets the unit that is moved to air
			current.setUnit(move.getFromX(), move.getFromY(), Unit.AIR);
			List<Move> previousMoves = lastConsecutiveMoves.get(move.getPlayerID());
			if (!previousMoves.isEmpty() && movedUnit != previousMoves.get(0).getMovedUnit()) {
				previousMoves.clear();
			}
			previousMoves.add(move);
			if (!move.hasEncounter() || move.getEncounter().getDefeatedUnits()[0].getType() != Unit.UnitType.FLAG) {
				waitForEndTurnConfirmation();
			}
			currentTurn++;
			nextTurn();
		}

		// only gets if wrong player makes move
		else {
			System.out.println("WRONG PLAYER!");
		}
	}

	private boolean validateSetup(Setup setup) {
		/**
		 * check if the setup is correct, check if every field is not empty and
		 * how many of each unit there is
		 */
		// array of elements by rank
		int[] unitsByRank = new int[12];
		for (int i = 0; i < setup.getWidth(); i++) {
			for (int j = 0; j < setup.getHeight(); j++) {
				// for every element checks if it is not empty
				if (setup.getUnit(i,j) == null
						|| setup.getUnit(i, j).isAir()
						|| setup.getUnit(i, j).isLake()) {
					return false;
				}else {
					// it counts units of each rank
					unitsByRank[setup.getUnit(i,j).getType().getRank()]++;
				}
			}
		}
		// checks the quantity of each unit
		if (unitsByRank[Unit.UnitType.FLAG.getRank()] != Unit.UnitType.FLAG.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.SPY.getRank()] != Unit.UnitType.SPY.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.SCOUT.getRank()] != Unit.UnitType.SCOUT.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.SAPPER.getRank()] != Unit.UnitType.SAPPER.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.SERGEANT.getRank()] != Unit.UnitType.SERGEANT.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.LIEUTENANT.getRank()] != Unit.UnitType.LIEUTENANT.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.CAPTAIN.getRank()] != Unit.UnitType.CAPTAIN.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.MAJOR.getRank()] != Unit.UnitType.MAJOR.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.COLONEL.getRank()] != Unit.UnitType.COLONEL.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.GENERAL.getRank()] != Unit.UnitType.GENERAL.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.MARSHAL.getRank()] != Unit.UnitType.MARSHAL.getQuantity()) {
			return false;
		}
		if (unitsByRank[Unit.UnitType.BOMB.getRank()] != Unit.UnitType.BOMB.getQuantity()) {
			return false;
		}
		return true;
	}

	private void setSetup(Setup setup, PlayerID playerID) {
		/**
		 * puts setup to the main grid depending on a player player 1 on the
		 * bottom player 2 on the top
		 */
		if (playerID == PLAYER_1) {
			//Set Setup for Player 1
			for (int i = 0; i < setup.getWidth(); i++) {
				for (int j = 0; j < setup.getHeight(); j++) {
					current.setUnit(i, j + 6, setup.getUnit(i, j));
				}
			}
			player1FinishedSetup = true;
		} else {
			//Set Setup for Player 2
			for (int i = 0; i < setup.getWidth(); i++) {
				for (int j = 0; j < setup.getHeight(); j++) {
					current.setUnit(i, j, setup.getUnit(i, j));
				}
			}
			player2FinishedSetup = true;
		}
		if(player1FinishedSetup && player2FinishedSetup && !finishedSetup){
			finishedSetup = true;
			nextTurn();
		}
	}

	private void nextTurn() {
		/**
		 * when called, first determine which players turn is it, then call one
		 * of them to start move, second to idle
		 */
		gameOver = gameOver();
		if (!gameOver) {
			if (states.size() % 2 == 1) {
				if (hasLost(player1)) {
					// TODO: Add Something to clarify Game end
					log.info("PLAYER_1 lost.");
					return;
				} else {
					if (player1 instanceof HumanPlayer || (player1 instanceof RemoteServingPlayer
														   && ((RemoteServingPlayer) player1).getLocalPlayer() instanceof HumanPlayer)) {
						activeGameView = player1.getGameView();
					} else {
						try {
							Thread.sleep(AI_DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					log.info("It is PLAYER_1s move.");
					becomeBlind();
					player1.startMove();
					player2.startIdle();
				}
			} else {
				if (hasLost(player2)) {
					// TODO: Add Something to clarify Game end
					log.info("PLAYER_2 lost.");
					return;
				} else {
					if (player2 instanceof HumanPlayer || (player2 instanceof RemoteServingPlayer
														   && ((RemoteServingPlayer) player2).getLocalPlayer() instanceof HumanPlayer)) {
						activeGameView = player2.getGameView();
					} else {
						try {
							Thread.sleep(AI_DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					log.info("It is PLAYER_2s move.");
					becomeBlind();
					player2.startMove();
					player1.startIdle();
				}
			}
		} else {
			// stop the game!
			log.info("GAME OVER! Winner is "+winner.getGameView().getPlayerID());
			revealBoard();
			player1FinishedCleanup = false;
			player2FinishedCleanup = false;
			player1.startCleanup();
			player2.startCleanup();
		}
	}

	private void finishedCleanup(PlayerID playerID) {
		if (playerID == PLAYER_1) {
			player1FinishedCleanup = true;
		} else if (playerID == PLAYER_2) {
			player2FinishedCleanup = true;
		}
	}

	private void waitForEndTurnConfirmation() {
		if (player1 instanceof HumanPlayer && player2 instanceof HumanPlayer) {
			waitingForEndTurn = true;
			while (waitingForEndTurn) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void becomeBlind() {
		if (player1 instanceof HumanPlayer && player2 instanceof HumanPlayer) {
			blind = true;
			while (blind) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean gameOver() {
		if (getCurrentTurn() <= 1) {
			return false;
		}
		if (defeatedUnitsPlayer1.size() > 0 && defeatedUnitsPlayer1.get(defeatedUnitsPlayer1.size() - 1).getType() == Unit.UnitType.FLAG) {
			winner = player2;
			return true;
		}
		if (defeatedUnitsPlayer2.size() > 0 && defeatedUnitsPlayer2.get(defeatedUnitsPlayer2.size() - 1).getType() == Unit.UnitType.FLAG) {
			winner = player1;
			return true;

		}
		ArrayList<Point> UnitsP1 = new ArrayList<Point>();
		ArrayList<Point> UnitsP2 = new ArrayList<Point>();
		for (int j = 0; j < current.getHeight(); j++) {
			for (int i = 0; i < current.getWidth(); i++) {
				if (current.getUnit(i, j).getType().getRank() != -1) {
					if (current.getUnit(i, j).getType().getRank() != 0
						&& current.getUnit(i, j).getType().getRank() != 11) {
						if (current.getUnit(i, j).getOwner() == PLAYER_1) {
							UnitsP1.add(new Point(i, j));
						} else {
							UnitsP2.add(new Point(i, j));
						}
					}
				}
			}
		}

		if (!checkIfHasMoves(UnitsP1, PLAYER_1)) {
			winner = player2;
			return true;
		}
		if (!checkIfHasMoves(UnitsP2, PLAYER_2)) {
			if (winner == player2) {
				winner = null;
			} else {
				winner = player1;
			}
			return true;
		}
		return false;
	}

	private boolean checkIfHasMoves(ArrayList<Point> units, PlayerID playerID) {
		boolean theirAreMovesLeft = false;
		for (int i = 0; i < units.size(); i++) {
			int x = (int) units.get(i).getX();
			int y = (int) units.get(i).getY();
			Move move1 = new Move(x, y, x + 1, y);
			Move move2 = new Move(x, y, x, y + 1);
			Move move3 = new Move(x, y, x, y - 1);
			Move move4 = new Move(x, y, x - 1, y);
			move1.setPlayerID(playerID);
			move2.setPlayerID(playerID);
			move3.setPlayerID(playerID);
			move4.setPlayerID(playerID);
			if (validateMove(move1) || validateMove(move2)
					|| validateMove(move3) || validateMove(move4)) {

				theirAreMovesLeft = true;
				break;
			}
		}
		return theirAreMovesLeft;
	}

	public void startSetupPhase() {
		if (player1 instanceof HumanPlayer || (player1 instanceof RemoteServingPlayer
											   && ((RemoteServingPlayer) player1).getLocalPlayer() instanceof HumanPlayer)) {
			activeGameView = player1.getGameView();
		}
		reseted = false;
		log.info("PLAYER_1 is asked to setup.");
		player1.startSetup();
		if (player1 instanceof HumanPlayer && player2 instanceof HumanPlayer) {
			while (!player1FinishedSetup) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		log.info("PLAYER_2 is asked to setup.");
		becomeBlind();
		if (player2 instanceof HumanPlayer || (player2 instanceof RemoteServingPlayer
											   && ((RemoteServingPlayer) player2).getLocalPlayer() instanceof HumanPlayer)) {
			activeGameView = player2.getGameView();
		}
		player2.startSetup();
	}

	private int getCurrentTurn() {

		return currentTurn;
	}

	private GameBoard getCurrentState() {

		return current;
	}

	/**
	 * Calculates and returns whether the Player has lost the game (immovable,
	 * flag destroyed, etc.)
	 */
	private boolean hasLost(Player player) {
		boolean hasLost = false;
		if (hasNoFlag(player)) {
			hasLost = true;
		}
		return hasLost;
	}

	private boolean hasNoFlag(Player player) {
		// TODO: Replace this method by saving whether a flag has been killed
		// last turn
		// TODO: Fix Bug
		if (player.getGameView().getMoves() != null
				&& player.getGameView().getMoves().size() > 0
				&& player.getGameView().getLastMove().getEncounter() != null&&
						player.getGameView().getLastMove().hasEncounter()) {
			Unit[] lastTurnsFallen = player.getGameView().getLastMove()
					.getEncounter().getDefeatedUnits();
			for (int c = 0; c < lastTurnsFallen.length; c++) {
				if (lastTurnsFallen[c].getType().getRank() == Unit.UnitType.FLAG
						.getRank()) {
					return true;
				}
			}
		}
		return false;
	}

	private GameBoard getState(int turn) {

		return states.get(turn - 1);
	}

	private static GameBoard getCurrent() {
		return current;
	}

	/** Returns number of units of given type and given player that have been defeated thus far */
	public int getNumberOfDefeatedUnits(int unitRank, PlayerID playerId){
		List<Unit> defeatedUnits;
		if(playerId== PLAYER_1){
			defeatedUnits = this.defeatedUnitsPlayer1;
		}
		else{
			defeatedUnits = this.defeatedUnitsPlayer2;
		}
		int counter = 0;
		for(Unit unit : defeatedUnits){
			if(unit.getType().getRank() == unitRank){
				counter++;
			}
		}
		return counter;
	}

	/** Returns Player ID Of player that is currently active */
	public PlayerID getCurrentPlayer(){
		return getCurrentTurn()%2==1? PLAYER_1: PLAYER_2;
	}

	private boolean twoSquareRuleValidation(Move move) {

		List<Move> previousMoves = lastConsecutiveMoves.get(move.getPlayerID());
		Unit unitInQuestion = current.getUnit(move.getFromX(), move.getFromY());
		if (previousMoves.isEmpty()) {
			return true;
		} else if (unitInQuestion != previousMoves.get(0).getMovedUnit()) {
			return true;
		} else {
			if (previousMoves.size() < 5) {
				return true;
			} else {
				// have the last 5 moves in scope
				int fiveMovesBefore = previousMoves.size() - 5;
				// Special case for the scout to two his increased movement range
				// Movement isn't allowed to occur more than 5 times in the range of the first move
				if (unitInQuestion.getType() == Unit.UnitType.SCOUT) {
					Move referenceMove = previousMoves.get(fiveMovesBefore);
					return !(previousMoves.get(fiveMovesBefore + 1).isMovementInBetween(referenceMove)
							 && previousMoves.get(fiveMovesBefore + 2).isMovementInBetween(referenceMove)
							 && previousMoves.get(fiveMovesBefore + 3).isMovementInBetween(referenceMove)
							 && previousMoves.get(fiveMovesBefore + 4).isMovementInBetween(referenceMove)
							 && move.isMovementInBetween(referenceMove));
				} else {
					return !(previousMoves.get(fiveMovesBefore).isSameMovementAs(previousMoves.get(fiveMovesBefore + 2))
							 && previousMoves.get(fiveMovesBefore + 1).isSameMovementAs(previousMoves.get(fiveMovesBefore + 3))
							 && previousMoves.get(fiveMovesBefore + 2).isSameMovementAs(previousMoves.get(fiveMovesBefore + 4))
							 && previousMoves.get(fiveMovesBefore + 3).isSameMovementAs(move));
				}
			}
		}
	}

	private boolean chaseRuleValidation(Move move) {

		PlayerID player = move.getPlayerID();
		List<Move> previousMoves = lastConsecutiveMoves.get(player);
		List<Move> previousOpponentMoves = lastConsecutiveMoves.get((player == PLAYER_1) ? PLAYER_2 : PLAYER_1);
		Unit unitInQuestion = current.getUnit(move.getFromX(), move.getFromY());
		if (previousMoves.isEmpty()) {
			resetChase(player);
			return true;
		} else if (unitInQuestion != previousMoves.get(0).getMovedUnit()) {
			resetChase(player);
			return true;
		} else {
			if (previousMoves.size()-getChase(player) < 4 || previousOpponentMoves.size()-getChase(player) < 4) {
				return true;
			} else {
				Move lastOpponentMove = previousOpponentMoves.get(previousOpponentMoves.size() - 1);
				if (manhattanDistance(move.getToX(), move.getToY(), lastOpponentMove.getToX(), lastOpponentMove.getToY()) > 1) {
					setChase(player, previousMoves.size());
					return true;
				} else {
					boolean repeatedMoveAttempt = false;
					// check if an identical move was performed during the chase
					for (int i = getChase(player) ; i < previousMoves.size(); i++) {
						Move previous = previousMoves.get(i);
						if (previous.isSameMovementAs(move)) {
							repeatedMoveAttempt = true;
							break;
						}
					}
					if (repeatedMoveAttempt) {
						// back and forth movement is allowed as long the two-square rule is satisfied.
						if (previousMoves.get(previousMoves.size() - 2).isSameMovementAs(move)) {
							return true;
						} else {
							return false;
						}
					} else {
						return true;
					}
				}
			}
		}
	}

	private int getChase(PlayerID player) {
		if (player == PLAYER_1) {
			return player1ChaseBegin;
		} else {
			return player2ChaseBegin;
		}
	}

	private void setChase(PlayerID player, int turns) {
		if (player == PLAYER_1) {
			player1ChaseBegin = turns;
		} else {
			player2ChaseBegin = turns;
		}
	}

	private void resetChase(PlayerID player) {
		if (player == PLAYER_1) {
			player1ChaseBegin = 0;
		} else {
			player2ChaseBegin = 0;
		}
	}

	private void revealBoard() {
		for (int x = 0; x < current.getWidth(); x++) {
			for (int y = 0; y < current.getHeight(); y++) {
				current.getUnit(x,y).setRevealedInTurn(getCurrentTurn());
			}
		}
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
		if (player1 instanceof HumanPlayer || (player1 instanceof RemoteServingPlayer
											   && ((RemoteServingPlayer) player1).getLocalPlayer() instanceof HumanPlayer)) {
			activeGameView = player1.getGameView();
		} else if (player2 != null) {
			activeGameView = new GameView(this, NEMO);
		}
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
		if (player2 instanceof HumanPlayer || (player2 instanceof RemoteServingPlayer
											   && ((RemoteServingPlayer) player2).getLocalPlayer() instanceof HumanPlayer)) {
			activeGameView = player2.getGameView();
		} else if (player1 != null) {
			activeGameView = new GameView(this, NEMO);
		}
	}

	/**
	 * The gameView acts as the communication interface between players/renderers and the game.
	 * Each user of the view can assume he is PLAYER_1:
	 * The view takes care of all necessary translations between the player space and the game space.
	 *
	 * @author Fabian Fraenz <f.fraenz@t-online.de>
	 * @author Flo
	 * @version 1.0
	 * @created 10.09.2014
	 * @date 13.09.2014
	 * @log - skeleton 					10.09.2014
	 * - implementation & documentation 	13.09.2014
	 */
	public static class GameView {

		private static final Logger log = Logger.getLogger(GameView.class.getName());
		private final Game game;
		/**
		 * Reference to the game.
		 */
		@Getter private final PlayerID playerID;
		/**
		 * PlayerID which defines this views perspective
		 */
		private final List<Move> cashedRotatedMoves = new ArrayList<Move>();

		@java.beans.ConstructorProperties({ "game", "playerID" }) public GameView(Game game, PlayerID playerID) {
			this.game = game;
			this.playerID = playerID;
		}

		/** List of moves which acts as a cache for rotated move for
		 the PLAYER_2 to avoid unnecessary recalculations of move
		 rotations at the cost of additional memory costs. */

		public Player getPlayer() {
			if (playerID == PLAYER_1) {
				return game.getPlayer1();
			} else if (playerID == PLAYER_2) {
				return game.getPlayer2();
			} else {
				return null;
			}
		}

		/**
		 * Validates the move.
		 *
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
		 *
		 * @param move The move in player space.
		 *             <p/>
		 *             TODO Add pre condition declaration
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
		 *
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
		 *
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
		 *
		 * @return The number of the current turn.
		 */
		public int getCurrentTurn() {

			return game.getCurrentTurn();
		}

		/**
		 * Gets the most recent game state.
		 *
		 * @return The most recent game state.
		 */
		public GameBoard getCurrentState() {

			return getState(getCurrentTurn());
		}

		/**
		 * Gets the state of the game at the specified turn.
		 *
		 * @param turn The turn number.
		 * @return The state of the game at turn.
		 */
		public GameBoard getState(int turn) {

			GameBoard gameBoard;
			// If the game view owner is not a player return an unobscured game board copy.
			if (playerID.equals(NEMO)) {
				gameBoard = game.getState(turn).duplicate();
			} else if (playerID.equals(PLAYER_1)) {
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
		 *
		 * @param x X Coordinate in player space.
		 * @param y Y Coordinate in player space.
		 * @return The unit at the location.
		 */
		public Unit getUnit(int x, int y) {

			return getUnit(x, y, getCurrentTurn());
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
		 *
		 * @param x X Coordinate in player space.
		 * @param y Y Coordinate in player space.
		 * @return Whether the location contains air or not.
		 */
		public boolean isAir(int x, int y) {

			return isAir(x, y, getCurrentTurn());
		}

		/**
		 * Checks if the location contains a lake.
		 *
		 * @param x X Coordinate in player space.
		 * @param y Y Coordinate in player space.
		 * @return Whether the location contains a lake or not.
		 */
		public boolean isLake(int x, int y) {

			return isLake(x, y, getCurrentTurn());
		}

		/**
		 * Checks if the location contains a unit that is unknown to the assigned player.
		 *
		 * @param x X Coordinate in player space.
		 * @param y Y Coordinate in player space.
		 * @return Whether the location contains a unknown unit or not.
		 */
		public boolean isUnknown(int x, int y) {

			return isUnknown(x, y, getCurrentTurn());
		}

		public boolean isAir(int x, int y, int turn) {

			return getUnit(x, y, turn).isAir();
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

		public boolean willWin(int ownX, int ownY, int targetX, int targetY) {
			if (isEnemy(targetX, targetY)) {
				Unit own = getUnit(ownX, ownY);
				if (own.getOwner().equals(playerID)) {
					Unit foe = getUnit(targetX, targetY);
					if (foe.isUnknown()) {
						int ownRank = own.getType().getRank();
						int foeRank = foe.getType().getRank();
						if (ownRank > foeRank || (foeRank == Unit.UnitType.MARSHAL.getRank() && ownRank == Unit.UnitType.SPY.getRank())) {
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * Get all moves played so far.
		 *
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
		 *
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
		 *
		 * @return The most recent move.
		 */
		public Move getLastMove() {
			return getMove(game.getCurrentTurn() - 2);
		}

		/**
		 * Gets all defeated units.
		 *
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
		 *
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
		 *
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
		 *
		 * @param gameBoard The board.
		 * @param turn      The turn number.
		 * @return The board where relevant units are obscured.
		 */
		private GameBoard obscureBoard(GameBoard gameBoard, int turn) {

			GameBoard copiedGameBoard = gameBoard.duplicate();
			for (int y = 0; y < copiedGameBoard.getHeight(); y++) {
				for (int x = 0; x < copiedGameBoard.getWidth(); x++) {
					copiedGameBoard.setUnit(x, y, obscureUnitIfNecessary(copiedGameBoard.getUnit(x, y), turn));
				}
			}
			return copiedGameBoard;
		}

		/**
		 * Obscures and translates the board to game space for the assigned player.
		 *
		 * @param gameBoard The board.
		 * @param turn      The turn number.
		 * @return The obscured and translated board.
		 */
		private GameBoard obscureAndRotateBoard(GameBoard gameBoard, int turn) {

			GameBoard copiedGameBoard = gameBoard.duplicate();
			for (int y = 0; y < copiedGameBoard.getHeight(); y++) {
				for (int x = 0; x < copiedGameBoard.getWidth(); x++) {
					copiedGameBoard.setUnit(gameBoard.getWidth() - x - 1, gameBoard.getHeight() - y - 1, obscureUnitIfNecessary(gameBoard.getUnit(x, y), turn));
				}
			}
			return copiedGameBoard;
		}

		/**
		 * Obscured the unit if the player shouldn't know the unit.
		 *
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
		 *
		 * @param x X Coordinate in player space.
		 * @param y Y Coordinate in player space.
		 * @return Coordinates in game space.
		 */
		private Point conditionalCoordinateRotation(int x, int y) {
			Point coord = new Point(-1, -1);
			if (playerID.equals(PLAYER_1) || playerID.equals(NEMO)) {
				// Player 1 coordinates are equal to game space coordinates.
				coord = new Point(x, y);
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
		 *
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
		 *
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
		 *
		 * @param setup The setup.
		 * @return The rotated setup.
		 */
		private Setup rotateSetup(Setup setup) {
			int width = setup.getWidth();
			int height = setup.getHeight();
			Setup rotatedSetup = new Setup(width, height);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					rotatedSetup.setUnit(width - x - 1, height - y - 1, setup.getUnit(x, y));
				}
			}
			return rotatedSetup;
		}

		/**
		 * Returns whether the given X|Y is legal to be moved to
		 */
		public boolean walkable(int x, int y) {
			return game.getCurrentState().isInBounds(x, y) && (isEnemy(x, y) || isAir(x, y));
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

}
