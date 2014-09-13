package com.theBombSquad.stratego.gameMechanics.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.theBombSquad.stratego.StrategoConstants.PlayerID;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
@Getter
@RequiredArgsConstructor
public class Move {

	private static Encounter DUMMY_ENCOUNTER = new Encounter(null, null, null);

	// set by player
	private final int fromX;
	private final int fromY;
	private final int toX;
	private final int toY;

	// set by game view
	private PlayerID playerID = null;
	// set by game
	private int turn = -1;
	private Unit movedUnit = null;
	private Encounter encounter = DUMMY_ENCOUNTER;

	public boolean hasEncounter() {
		return encounter != null;
	}

	public void setPlayerID(PlayerID playerID) {
		assert this.playerID == null : "Trying to set playerID more than once";
		this.playerID = playerID;
	}

	public void setTurn(int turn) {
		assert this.turn == -1 : "Trying to set turn more than once";
		this.turn = turn;
	}

	public void setMovedUnit(Unit movedUnit) {
		assert this.movedUnit == null : "Trying to set movedUnit more than once";
		this.movedUnit = movedUnit;
	}

	public void setEncounter(Encounter encounter) {
		assert this.encounter == DUMMY_ENCOUNTER : "Trying to set encounter more than once";
		this.encounter = encounter;
	}
}
