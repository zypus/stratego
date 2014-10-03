package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.DEFAULT_LAKES;
import static com.theBombSquad.stratego.StrategoConstants.GRID_HEIGHT;
import static com.theBombSquad.stratego.StrategoConstants.GRID_WIDTH;

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
	private static GameBoard current; //to be initialized
	private List<Move> moves;
	private List<Unit> defeatedUnitsPlayer1;
	private List<Unit> defeatedUnitsPlayer2;
	@Setter private Player player1;
	@Setter private Player player2;
	private boolean player1FinishedSetup = false;
	private boolean player2FinishedSetup = false;
	private boolean finishedSetup = false;

	public Game() {
		states = new ArrayList<GameBoard>();
		// add in the initial board
		states.add(new GameBoard(GRID_WIDTH, GRID_HEIGHT, DEFAULT_LAKES));
		current = states.get(0);
		moves = new ArrayList<Move>();
		defeatedUnitsPlayer1 = new ArrayList<Unit>();
		defeatedUnitsPlayer2 = new ArrayList<Unit>();
	}

	public boolean validateMove(Move move) {
		/**
		checks if a move is valid
		 */
		int fromX = move.getFromX();
		int fromY = move.getFromY();
		int toX = move.getToX();
		int toY = move.getToY();
		int distanceX = Math.abs(fromX - toX);
		int distanceY = Math.abs(fromY - toY);
		// if we attack unit of ours then false
		if(move.getPlayerID()==current.getUnit(toX, toY).getOwner()){
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
		// check end position if it is not lake
		else if (current.getUnit(toX, toY).getType() == current.getUnit(
				toX, toY).getType().LAKE) {
			return false;
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
						for (int i =fromX + 1; i <= toX - 1; i++) {
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
		return true;
	}

	private void discoverSpy() {
		/**
		 when a spy moves by a few fields it is discovered,
		 dunno where to implement it :P
		 */
	}

	public void performMove(Move move) {
		/**
		 performs move depending on the type of unit, considers also encounter
		 */
		moves.add(move);
		
		if ((states.size() % 2 == 1 && move.getPlayerID() == PlayerID.PLAYER_1)
				|| (states.size() % 2 == 0 && move.getPlayerID() == PlayerID.PLAYER_2)) {
			Unit movedUnit = current.getUnit(move.getFromX(), move.getFromY());
			// if moved to air just set the air to unit
			if (current.getUnit(move.getFromX(), move.getFromY()).getType() == current
					.getUnit(move.getFromX(), move.getFromY()).getType().AIR) {
				current.setUnit(move.getToX(), move.getToY(), movedUnit);
			} else {
				// checks who is the winner
				Encounter encounter = new Encounter(movedUnit, current.getUnit(
						move.getToX(), move.getToY()));
				Unit winner = encounter.getVictoriousUnit();
				// if there is no winner then sets the field to air
				if (winner == null) {
					current.setUnit(move.getToX(), move.getToY(), Unit.AIR);
				}
				// else sets the winner to the spot
				else {
					current.setUnit(move.getToX(), move.getToY(), winner);
					//Reveals the victorious unit
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

	public boolean validateSetup(Unit[][] setup) {
		/**
		 * check if the setup is correct, check if every field is not empty and
		 * how many of each unit there is
		 */
		boolean hasFlag = false;
		// array of elements by rank
		int[] unitsByRank = new int[11];
		for (int i = 0; i < setup.length; i++) {
			for (int j = 0; j < setup[0].length; j++) {
				// for every element checks if it is not empty
				if (setup[i][j] == null
						|| setup[i][j].getType() == setup[i][j].getType().AIR
						|| setup[i][j].getType() == setup[i][j].getType().LAKE) {
					return false;
				} else if (setup[i][j].getType() == setup[i][j].getType().FLAG) {
					// checks if there is already one flag found
					if (hasFlag = false) {
						hasFlag = true;
					} else {
						return false;
					}
				} else {
					// it counts units of each rank
					unitsByRank[setup[i][j].getType().getRank()]++;
				}
			}
		}
		// checks the quantity of each unit
		if (unitsByRank[0] != 7) {
			return false;
		}
		if (unitsByRank[1] != 1) {
			return false;
		}
		if (unitsByRank[2] != 8) {
			return false;
		}
		if (unitsByRank[3] != 5) {
			return false;
		}
		if (unitsByRank[4] != 4) {
			return false;
		}
		if (unitsByRank[5] != 4) {
			return false;
		}
		if (unitsByRank[6] != 4) {
			return false;
		}
		if (unitsByRank[7] != 3) {
			return false;
		}
		if (unitsByRank[8] != 2) {
			return false;
		}
		if (unitsByRank[9] != 1) {
			return false;
		}
		if (unitsByRank[10] != 1) {
			return false;
		}
		if (!hasFlag) {
			return false;
		}

		return true;
	}

	public void setSetup(Unit[][] setup, PlayerID playerID) {
		/**
			puts setup to the main grid depending on a player
			player 1 on the bottom player 2 on the top
		 */
		if (playerID == PlayerID.PLAYER_1) {
			player1FinishedSetup = true;
			if(player1 instanceof HumanPlayer ){
				((HumanPlayer) player1).setSetUpPhase(false);
			}			
			for (int i = 0; i < setup.length; i++) {
				for (int j = 0; j < setup[0].length; j++) {
					current.setUnit(j, i+6, setup[i][j]);
				}
			}
		} else {
			// MIGHT BE WRONG !!
			// I DIDNT FLIP THE SETUP BEFORE PUTTING INTO ARRAY
			player2FinishedSetup = true;
			if(player2 instanceof HumanPlayer ){
				((HumanPlayer) player2).setSetUpPhase(false);
			}		
			for (int i = 0; i < setup.length; i++) {
				for (int j = 0; j < setup[0].length; j++) {
					current.setUnit(j, i, setup[i][j]);
				}
			}
		}
		if(player1FinishedSetup && player2FinishedSetup && !finishedSetup){
			finishedSetup = true;
			nextTurn();
		}
	}

	private void nextTurn() {
		/**
			when called, first determine which players turn is it, then
			call one of them to start move, second to idle
		 */
		if (states.size() % 2 == 1) {
			if(hasLost(player1)){
				//TODO: Add Something to clarify Game end
				return;
			}
			else{
				player1.startMove();
				player2.startIdle();
			}
		} else {
			if(hasLost(player2)){
				//TODO: Add Something to clarify Game end
				return;
			}
			else{
				player2.startMove();
				player1.startIdle();
			}
		}
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
	
	/** Calculates and returns whether the Player has lost the game (immovable, flag destroyed, etc.) */
	public boolean hasLost(Player player){
		boolean hasLost = false;
		if(hasNoFlag(player)){
			hasLost = true;
		}
		return hasLost;
	}
	
	private boolean hasNoFlag(Player player){
		//TODO: Replace this method by saving whether a flag has been killed last turn
		//TODO: Fix Bug
		if(player.getGameView().getMoves()!=null && player.getGameView().getMoves().size()>0 && player.getGameView().getLastMove().getEncounter()!=null){
			Unit[] lastTurnsFallen = player.getGameView().getLastMove().getEncounter().getDefeatedUnits();
			for(int c=0; c<lastTurnsFallen.length; c++){
				if(lastTurnsFallen[c].getType().getRank() == Unit.UnitType.FLAG.getRank()){
					return true;
				}
			}
		}
		return false;
//		boolean hasFlag = false;
//		for(int cy=0; cy<player.getGameView().getCurrentState().getHeight(); cy++){
//			for(int cx=0; cx<player.getGameView().getCurrentState().getWidth(); cx++){
//				if(player.getGameView().getUnit(cx, cy).getOwner().equals(player.getGameView().getPlayerID())){
//					if(player.getGameView().getUnit(cx, cy).getType().getRank() == Unit.UnitType.FLAG.getRank()){
//						hasFlag = true;
//					}
//				}
//			}
//		}
//		return !hasFlag;
	}

	public GameBoard getState(int turn) {

		return states.get(turn - 1);
	}
	public static GameBoard getCurrent(){
		return current;
	}
	
}
