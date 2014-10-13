package com.theBombSquad.stratego.gameMechanics.board;

import com.theBombSquad.stratego.StrategoConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

import static java.lang.Math.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
@Getter
@ToString
@RequiredArgsConstructor
public class Move implements Serializable {

	private static Encounter DUMMY_ENCOUNTER = new Encounter(null, null, null);

	// set by player
	private final int fromX;
	private final int fromY;
	private final int toX;
	private final int toY;

	// set by game view
	private StrategoConstants.PlayerID playerID = null;
	// set by game
	private int turn = -1;
	private Unit movedUnit = null;
	private Encounter encounter = DUMMY_ENCOUNTER;

	public boolean hasEncounter() {
		return encounter.getResult() != null;
	}

	public void setPlayerID(StrategoConstants.PlayerID playerID) {
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
		//		assert this.encounter.getResult() == null : "Trying to set encounter more than once";
		this.encounter = encounter;
	}

	/**
	 * Utility methods
	 */

	public int getDistance() {
		if (fromX == toX) {
			return abs(fromY - toY);
		} else if (fromY == toY) {
			return abs(fromX - toX);
		}
		return 0;
	}
}
