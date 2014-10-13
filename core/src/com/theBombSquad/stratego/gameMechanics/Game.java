package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;

import lombok.Getter;
import lombok.Setter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 * @author Mateusz Garbacz
 */
@Getter
public class Game {

	private List<GameBoard> states;
	private static GameBoard current; // to be initialized
	private List<Move> moves;
	private List<Unit> defeatedUnitsPlayer1;
	private List<Unit> defeatedUnitsPlayer2;
	@Setter
	private Player player1;
	@Setter
	private Player player2;
	private boolean player1FinishedSetup = false;
	private boolean player2FinishedSetup = false;
	private boolean finishedSetup = false;
	private Player winner;

	private ArrayList<Move> lastMovesP1SameUnit;
	private ArrayList<Move> lastMovesP2SameUnit;

	/** The Setups both players committed thus far */
	private Setup[] setupClusters;

	public Game() {
		states = new ArrayList<GameBoard>();
		// add in the initial board
		states.add(new GameBoard(GRID_WIDTH, GRID_HEIGHT, DEFAULT_LAKES));
		current = states.get(0);
		moves = new ArrayList<Move>();
		defeatedUnitsPlayer1 = new ArrayList<Unit>();
		defeatedUnitsPlayer2 = new ArrayList<Unit>();

		lastMovesP1SameUnit = new ArrayList<Move>();
		lastMovesP2SameUnit = new ArrayList<Move>();


		//Sets New Setup Clusters
		this.setupClusters = new Setup[10];
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
		// if we attack unit of ours then false
		if(toX<0||toX>9||toY<0||toY>9){
			return false;
		}
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
		// then it is not validqweqweq

		else if (distanceX == 1 || distanceY == 1) {
			if (current.getUnit(fromX, fromY).getType() == current.getUnit(
					fromX, fromY).getType().AIR
					|| current.getUnit(fromX, fromY).getType() == current
							.getUnit(fromX, fromY).getType().LAKE
					|| current.getUnit(fromX, fromY).getType() == current
							.getUnit(fromX, fromY).getType().BOMB
					|| current.getUnit(fromX, fromY).getType() == current
							.getUnit(fromX, fromY).getType().FLAG) {
				return false;
			}

		}

		// if none of distances is 1 then one of them must be longer than one
		else {
			// check how long is the step, only scout can go more than one cell
			if (distanceX > 1) {
				if (current.getUnit(fromX, fromY).getType() == current.getUnit(
						fromX, fromY).getType().SCOUT) {
					// if it is a scout and it goes right we check all the steps
					// between
					if (toX - fromX > 0) {
						for (int i = fromX + 1; i <= toX - 1; i++) {
							if (current.getUnit(i, fromY).getType() != current
									.getUnit(fromX, fromY).getType().AIR) {
								return false;
							}
						}
						discoverSpy();
						return true;
					} else {
						for (int i = toX + 1; i <= fromX - 1; i++) {
							if (current.getUnit(i, fromY).getType() != current
									.getUnit(fromX, fromY).getType().AIR) {
								return false;
							}
						}
						discoverSpy();
						return true;
					}

				} else {
					return false;
				}
			} else if (distanceY > 1) {
				if (current.getUnit(fromX, fromY).getType() == current.getUnit(
						fromX, fromY).getType().SCOUT) {
					if (toY - fromY > 0) {
						for (int i = fromY + 1; i <= toY - 1; i++) {
							if (current.getUnit(fromX, i).getType() != current
									.getUnit(fromX, i).getType().AIR) {
								return false;
							}
						}
						discoverSpy();
						return true;
					} else {
						for (int i = toY + 1; i <= fromY - 1; i++) {
							if (current.getUnit(fromX, i).getType() != current
									.getUnit(fromX, i).getType().AIR) {
								return false;
							}
						}
						discoverSpy();
						return true;
					}
				}

				else {
					return false;
				}
			}
		}
		// check end position if it is not lake
		if (current.getUnit(toX, toY).getType() == current.getUnit(toX,
				toY).getType().LAKE) {
			return false;
		}
		// checks if goes one way and comes back all the time
		if (states.size() % 2 == 1) {
			System.out.println("Here");
			if (lastMovesP1SameUnit.size() == 0) {
				System.out.println("Here2");

				lastMovesP1SameUnit.add(move);
			} else {
				Move move2 = lastMovesP1SameUnit
						.get(lastMovesP1SameUnit.size() - 1);
				int x = move2.getToX();
				int y = move2.getToY();
				int x2 = move.getFromX();
				int y2 = move.getFromY();
				// checks if the same unit moves, so it can add to last moves
				// for the same unit
				if (x == x2 & y == y2) {
					lastMovesP1SameUnit.add(move);
					if (lastMovesP1SameUnit.size() > 5) {
						// checks if the moves were forward and back
						int counter = 0;
						for (int i = 0; i < 5; i++) {
							Move moveToCheck1 = lastMovesP1SameUnit
									.get(lastMovesP1SameUnit.size() - 1 - i);
							Move moveToCheck2 = lastMovesP1SameUnit
									.get(lastMovesP1SameUnit.size() - 1 - i - 1);
							if (switchedMove(moveToCheck1, moveToCheck2)) {
								counter++;
							}
						}
						if (counter > 4) {
							lastMovesP1SameUnit.remove(lastMovesP1SameUnit
									.size() - 1);
							return false;
						}
						// checks if p1 chases p2
						if (lastMovesP2SameUnit.size() > 5) {
							counter = 0;
							for (int i = 0; i < 5; i++) {
								Move moveToCheck1 = lastMovesP1SameUnit
										.get(lastMovesP1SameUnit.size() - 1 - i);
								Move moveToCheck2 = lastMovesP2SameUnit
										.get(lastMovesP2SameUnit.size() - 1 - i);
								if (sameMove(moveToCheck1, moveToCheck2)) {
									counter++;
								}
							}
							if (counter > 4) {
								lastMovesP1SameUnit.remove(lastMovesP1SameUnit
										.size() - 1);
								return false;
							}
						}
					}
				} else {
					lastMovesP1SameUnit = new ArrayList<Move>();
					lastMovesP1SameUnit.add(move);
				}
			}
		} else {
			if (lastMovesP2SameUnit.size() == 0) {
				lastMovesP2SameUnit.add(move);
			} else {
				Move move2 = lastMovesP2SameUnit
						.get(lastMovesP2SameUnit.size() - 1);
				int x = move2.getToX();
				int y = move2.getToY();
				int x2 = move.getFromX();
				int y2 = move.getFromY();
				// checks if the same unit moves, so it can add to last moves
				// for the same unit
				if (x == x2 & y == y2) {
					lastMovesP2SameUnit.add(move);
					if (lastMovesP2SameUnit.size() > 5) {
						// checks if the moves were forward and back
						int counter = 0;
						for (int i = 0; i < 5; i++) {
							Move moveToCheck1 = lastMovesP2SameUnit
									.get(lastMovesP2SameUnit.size() - 1 - i);
							Move moveToCheck2 = lastMovesP2SameUnit
									.get(lastMovesP2SameUnit.size() - 1 - i - 1);
							if (switchedMove(moveToCheck1, moveToCheck2)) {
								counter++;
							}
						}
						if (counter > 4) {
							lastMovesP2SameUnit.remove(lastMovesP2SameUnit
									.size() - 1);
							return false;
						}
						// checks if p2 chases p1
						if (lastMovesP1SameUnit.size() > 5) {
							counter = 0;
							for (int i = 0; i < 5; i++) {
								Move moveToCheck1 = lastMovesP2SameUnit
										.get(lastMovesP2SameUnit.size() - 1 - i);
								Move moveToCheck2 = lastMovesP1SameUnit
										.get(lastMovesP1SameUnit.size() - 1 - i);
								if (sameMove(moveToCheck1, moveToCheck2)) {
									counter++;
								}
							}
							if (counter > 4) {
								lastMovesP2SameUnit.remove(lastMovesP2SameUnit
										.size() - 1);
								return false;
							}
						}
					}
				} else {
					lastMovesP2SameUnit = new ArrayList<Move>();
					lastMovesP2SameUnit.add(move);
				}
			}
		}
		return true;
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
					winner.setRevealedInTurn(states.size());
				}
				Unit[] loosers = encounter.getDefeatedUnits();
				for (int i = 0; i < loosers.length; i++) {
					if (loosers[i].getOwner() == PlayerID.PLAYER_1) {
						defeatedUnitsPlayer1.add(loosers[i]);
					} else {
						defeatedUnitsPlayer2.add(loosers[i]);
					}
				}

			}
			if (movedUnit.getType() == Unit.UnitType.SCOUT && move.getDistance() > 1 && movedUnit.getRevealedInTurn() == UNREVEALED) {
				movedUnit.setRevealedInTurn(states.size());
			}
			// sets the unit that is moved to air
			current.setUnit(move.getFromX(), move.getFromY(), Unit.AIR);
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
			System.out.println("Move");
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
		if (!gameOver()) {

			if (states.size() % 2 == 1) {
				if (hasLost(player1)) {
					// TODO: Add Something to clarify Game end
					return;
				} else {
					player1.startMove();
					player2.startIdle();
				}
			} else {
				if (hasLost(player2)) {
					// TODO: Add Something to clarify Game end
					return;
				} else {
					player2.startMove();
					player1.startIdle();
				}
			}
		} else {
			// stop the game!

		}
	}

	public boolean gameOver() {
		if(defeatedUnitsPlayer1.size()==0||defeatedUnitsPlayer2.size()==0){
			return false;
		}
		if (defeatedUnitsPlayer1.get(defeatedUnitsPlayer1.size() - 1).getType()
				.getRank() == 0) {
			winner=player2;
			return true;
		}
		if (defeatedUnitsPlayer2.get(defeatedUnitsPlayer2.size() - 1).getType()
				.getRank() == 0) {
			winner=player1;
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

		if (!checkIfHasMoves(UnitsP1)) {
			winner=player2;
			return true;
		}
		if (!checkIfHasMoves(UnitsP2)) {
			winner=player1;
			return true;
		}
		return false;
	}

	public boolean checkIfHasMoves(ArrayList<Point> units) {
		for (int i = 0; i < units.size(); i++) {
			int x = (int) units.get(i).getX();
			int y = (int) units.get(i).getY();
			Move move1 = new Move(x, y, x + 1, y);
			Move move2 = new Move(x, y, x, y + 1);
			Move move3 = new Move(x, y, x, y - 1);
			Move move4 = new Move(x, y, x - 1, y);
			if (!validateMove(move1) && !validateMove(move2)
					&& !validateMove(move3) && !validateMove(move4)) {
				return false;
			}
		}
		return true;
	}

	public void startSetupPhase() {
		player1.startSetup();
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

}
