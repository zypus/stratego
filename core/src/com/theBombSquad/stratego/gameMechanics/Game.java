package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.PlayerID;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
@Getter
public class Game {

	private List<GameBoard> states;
	private List<Move> moves;
	private List<Unit> defeatedUnitsPlayer1;
	private List<Unit> defeatedUnitsPlayer2;
	private Player player1;
	private Player player2;

	public Game(Player player1, Player player2) {
		states = new ArrayList<GameBoard>();
		moves = new ArrayList<Move>();
		defeatedUnitsPlayer1 = new ArrayList<Unit>();
		defeatedUnitsPlayer2 = new ArrayList<Unit>();
		this.player1=player1;
		this.player2=player2;
	}

	public boolean validateMove(Move move) {
		/**
		 * check if that move is possible, return boolean
		 * check if there is an unit
		 * check how far is the end position, only scout can move like that
		 * check if it is a flag or bomb
		 * check the end position (check positions between)
		 
		 */
		return false;
	}

	public void performMove(Move move) {
		/**
		 * move.getplayerId check if the right player moved
		 * if not just exception
		 *  copy the current state .duplicate()
		 *  store it in the game states
		 *  replace air with unit
		 *  if there is a unit, call the encounter TO DO
		 *  store the result in ENCOUNTER
		 *  replace current position by air
		 *  at the end call nextMove()
		 */
	}

	public boolean validateSetup(Unit[][] setup) {
		/**
		 * check if the setup is correct,
		 * check if every field is not empty and how many of each 
		 * 
		 */
		
		return false;
	}

	public void setSetup(Unit[][] setup, PlayerID playerID) {
		/**
		 * put it at the bottom or top depending on player
		 * 
		 * when both finished call next Turn
		 */
	}
	private void nextTurn(){
	 /*
	  * the right player odd player 1 
	  * startMove()
	  *  startIdle() on other player	  *  
	  * if even other way around 
	  */
	}
	
	private void setup(){
		/**
		 * it should be called only once check it
		 */
		player1.startSetup();
		player2.startSetup();
	
	
	
	}
	
	public int getCurrentTurn() {
		
		return 0;
	}

	public GameBoard getCurrentState() {

		return null;
	}

	public GameBoard getState(int turn) {

		return null;
	}

}
