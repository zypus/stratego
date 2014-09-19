package com.theBombSquad.stratego.gameMechanics;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.PlayerID;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
@RequiredArgsConstructor
public class GameView {

	private final Game game;
	@Getter private final PlayerID playerID;

	public void validateMove(Move move) {

	}

	public void performMove(Move move) {

	}

	public void validateSetup(Unit[][] setup) {

	}

	public void setSetup(Unit[][] setup) {

	}

	public int getCurrentTurn() {

		return 0;
	}

	public GameBoard getCurrentState() {
		// TODO make sure you first duplicate() the gameBoard and replace non revealed opponent units by unknown
		return null;
	}

	public GameBoard getState(int turn) {
		// TODO make sure you first duplicate() the gameBoard and replace non revealed opponent units by unknown
		return null;
	}

	public boolean isAir(int x, int y) {

		return false;
	}

	public boolean isLake(int x, int y) {

		return false;
	}

	public boolean isUnknown(int x, int y) {

		return false;
	}

	public List<Move> getMoves() {

		return null;
	}

	public Move getMove(int turn) {

		return null;
	}

	public Move getLastMove() {

		return null;
	}

	public List<Unit> getAllDefeatedUnits() {

		List<Unit> allList = new ArrayList<Unit>();
		allList.addAll(game.getDefeatedUnitsPlayer1());
		allList.addAll(game.getDefeatedUnitsPlayer2());

		return Collections.unmodifiableList(allList);
	}

	public List<Unit> getOwnDefeatedUnits() {

		return null;
	}

	public List<Unit> getOpponentsDefeatedUnits() {

		return null;
	}

}
