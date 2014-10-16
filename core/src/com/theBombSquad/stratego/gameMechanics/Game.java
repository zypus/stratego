package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.StrategoConstants.*;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;
import com.theBombSquad.stratego.player.remote.RemoteServingPlayer;
import lombok.Getter;
import lombok.extern.java.Log;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.theBombSquad.stratego.StrategoConstants.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 * @author Mateusz Garbacz
 */
@Getter
@Log
public class Game {

	private List<GameBoard> states;
	private static GameBoard current; // to be initialized
	private List<Move> moves;
	private List<Unit> defeatedUnitsPlayer1;
	private List<Unit> defeatedUnitsPlayer2;
	private Player player1 = null;
	private Player player2 = null;
	private boolean player1FinishedSetup = false;
	private boolean player2FinishedSetup = false;
	private boolean finishedSetup = false;
	@Getter
	private Player winner;

	/** The Setups both players committed thus far */
	private Setup[] setupClusters;
	private Map<PlayerID, List<Move>> lastConsecutiveMoves = new HashMap<PlayerID, List<Move>>();
	private GameView activeGameView;
	private boolean gameOver;
	private boolean reseted = true;

	private List<Unit> player1Units;
	private List<Unit> player2Units;

	public Game() {
		states = new ArrayList<GameBoard>();
		// add in the initial board
		states.add(new GameBoard(GRID_WIDTH, GRID_HEIGHT, DEFAULT_LAKES));
		current = states.get(0);
		moves = new ArrayList<Move>();
		defeatedUnitsPlayer1 = new ArrayList<Unit>();
		defeatedUnitsPlayer2 = new ArrayList<Unit>();

		player1Units = createUnitsForPlayer(PlayerID.PLAYER_1);
		player2Units = createUnitsForPlayer(PlayerID.PLAYER_2);

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
		reseted = true;
		player1 = null;
		player2 = null;
		player1FinishedSetup = false;
		player2FinishedSetup = false;
		gameOver = false;
		finishedSetup = false;
		states.clear();
		states.add(new GameBoard(GRID_WIDTH, GRID_HEIGHT, DEFAULT_LAKES));
		current = states.get(0);
		moves.clear();
		winner = null;
		defeatedUnitsPlayer1.clear();
		defeatedUnitsPlayer2.clear();
		this.setupClusters = new Setup[10];
		lastConsecutiveMoves.put(PlayerID.PLAYER_1, new ArrayList<Move>());
		lastConsecutiveMoves.put(PlayerID.PLAYER_2, new ArrayList<Move>());
		clearUnits(PlayerID.PLAYER_1);
		clearUnits(PlayerID.PLAYER_2);
	}

	private void clearUnits(PlayerID playerID) {
		List<Unit> units = (playerID == PlayerID.PLAYER_1) ? player1Units : player2Units;
		for (Unit unit : units) {
			unit.setRevealedInTurn(UNREVEALED);
		}
	}

	public boolean validateMove(Move move) {
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

	// checks if moves are the same but switched directions
	private boolean switchedMove(Move move, Move move2) {
		return move2.getFromX() == move.getToX()
				&& move2.getFromY() == move.getToY()
				&& move2.getToX() == move.getFromX()
				&& move2.getToY() == move.getFromY();
	}

	// checks if the moves are the same
	private boolean sameMove(Move move, Move move2) {
		return move2.getFromX() == move.getFromX()
				&& move2.getFromY() == move.getFromY()
				&& move2.getToX() == move.getToX()
				&& move2.getToY() == move.getToY();
	}

	private void discoverSpy() {
		/**
		 * when a spy moves by a few fields it is discovered, dunno where to
		 * implement it :P
		 */
	}

	public void performMove(Move move) {
		/**
		 * performs move depending on the type of unit, considers also encounter
		 */
		moves.add(move);

		if ((states.size() % 2 == 1 && move.getPlayerID() == PlayerID.PLAYER_1)
				|| (states.size() % 2 == 0 && move.getPlayerID() == PlayerID.PLAYER_2)) {
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
					if (looser.getOwner() == PlayerID.PLAYER_1) {
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
			states.add(current.duplicate());
			nextTurn();
		}

		// only gets if wrong player makes move
		else {
			System.out.println("WRONG PLAYER!");
		}
	}

	public boolean validateSetup(Setup setup) {
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

	public void setSetup(Setup setup, PlayerID playerID) {
		/**
		 * puts setup to the main grid depending on a player player 1 on the
		 * bottom player 2 on the top
		 */
		if (playerID == PlayerID.PLAYER_1) {
			//Set Setup Cluster for Player 1
			this.setupClusters[0] = setup;
			player1FinishedSetup = true;
			if (player1 instanceof HumanPlayer) {
				((HumanPlayer) player1).setSetUpPhase(false);
			}
		} else {
			//Set Setup Cluster for Player 2
			this.setupClusters[1] = setup;
			// MIGHT BE WRONG !!
			// I DIDNT FLIP THE SETUP BEFORE PUTTING INTO ARRAY
			player2FinishedSetup = true;
			if (player2 instanceof HumanPlayer) {
				((HumanPlayer) player2).setSetUpPhase(false);
			}
		}
		if(player1FinishedSetup && player2FinishedSetup && !finishedSetup){
			for (int i = 0; i < setup.getWidth(); i++) {
				for (int j = 0; j < setup.getHeight(); j++) {
					current.setUnit(i, j+6, this.setupClusters[0].getUnit(i,j));
					current.setUnit(i, j, this.setupClusters[1].getUnit(i, j));
				}
			}
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
					if (player1 instanceof HumanPlayer || (player1 instanceof RemoteServingPlayer && ((RemoteServingPlayer)player1).getLocalPlayer() instanceof HumanPlayer)) {
						activeGameView = player1.getGameView();
					} else {
						try {
							Thread.sleep(AI_DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					log.info("It is PLAYER_1s move.");
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
					player2.startMove();
					player1.startIdle();
				}
			}
		} else {
			// stop the game!
			log.info("GAME OVER! Winner is "+winner.getGameView().getPlayerID());
			revealBoard();
		}
	}

	public boolean gameOver() {
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
						if (current.getUnit(i, j).getOwner() == PlayerID.PLAYER_1) {
							UnitsP1.add(new Point(i, j));
						} else {
							UnitsP2.add(new Point(i, j));
						}
					}
				}
			}
		}

		if (!checkIfHasMoves(UnitsP1, PlayerID.PLAYER_1)) {
			winner = player2;
			return true;
		}
		if (!checkIfHasMoves(UnitsP2, PlayerID.PLAYER_2)) {
			winner = player1;
			return true;
		}
		return false;
	}

	public boolean checkIfHasMoves(ArrayList<Point> units, PlayerID playerID) {
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
		if (player2 instanceof HumanPlayer || (player2 instanceof RemoteServingPlayer
											   && ((RemoteServingPlayer) player2).getLocalPlayer() instanceof HumanPlayer)) {
			activeGameView = player2.getGameView();
		}
		player2.startSetup();
	}

	public int getCurrentTurn() {

		return states.size();
	}

	public GameBoard getCurrentState() {

		return current;
	}

	/**
	 * Calculates and returns whether the Player has lost the game (immovable,
	 * flag destroyed, etc.)
	 */
	public boolean hasLost(Player player) {
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
		// boolean hasFlag = false;
		// for(int cy=0; cy<player.getGameView().getCurrentState().getHeight();
		// cy++){
		// for(int cx=0; cx<player.getGameView().getCurrentState().getWidth();
		// cx++){
		// if(player.getGameView().getUnit(cx,
		// cy).getOwner().equals(player.getGameView().getPlayerID())){
		// if(player.getGameView().getUnit(cx, cy).getType().getRank() ==
		// Unit.UnitType.FLAG.getRank()){
		// hasFlag = true;
		// }
		// }
		// }
		// }
		// return !hasFlag;
	}

	public GameBoard getState(int turn) {

		return states.get(turn - 1);
	}

	public static GameBoard getCurrent() {
		return current;
	}

	/** Returns number of units of given type and given player that have been defeated thus far */
	public int getNumberOfDefeatedUnits(int unitRank, PlayerID playerId){
		List<Unit> defeatedUnits;
		if(playerId==StrategoConstants.PlayerID.PLAYER_1){
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
		return this.getStates().size()%2==1?StrategoConstants.PlayerID.PLAYER_1:StrategoConstants.PlayerID.PLAYER_2;
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

		return true;
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
			activeGameView = new GameView(this, PlayerID.NEMO);
		}
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
		if (player2 instanceof HumanPlayer || (player2 instanceof RemoteServingPlayer
											   && ((RemoteServingPlayer) player2).getLocalPlayer() instanceof HumanPlayer)) {
			activeGameView = player2.getGameView();
		} else if (player1 != null) {
			activeGameView = new GameView(this, PlayerID.NEMO);
		}
	}

}
