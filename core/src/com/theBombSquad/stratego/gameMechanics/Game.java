package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.PlayerID;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
@Getter
public class Game {

	private List<GameBoard> states;
	private List<Move> moves;
	private List<Unit> defeatedUnitsPlayer1;
	private List<Unit> defeatedUnitsPlayer2;

	public Game() {
		states = new ArrayList<GameBoard>();
		moves = new ArrayList<Move>();
		defeatedUnitsPlayer1 = new ArrayList<Unit>();
		defeatedUnitsPlayer2 = new ArrayList<Unit>();
	}

	public boolean validateMove(Move move) {

		return false;
	}

	public void performMove(Move move) {

	}

	public boolean validateSetup(Unit[][] setup) {

		return false;
	}

	public void setSetup(Unit[][] setup, PlayerID playerID) {

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
