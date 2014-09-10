package com.theBombSquad.stratego.gameMechanics.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 */
@Getter
@AllArgsConstructor
public class Encounter {

	private Unit attackingUnit;
	private Unit defendingUnit;

	private CombatResult result;

	public Unit getVictoriousUnit() {
		switch (result) {
			case VICTORIOUS_ATTACK:
				return attackingUnit;
			case VICTORIOUS_DEFENSE:
				return defendingUnit;
			case MUTUAL_DEFEAT:
				return null;
		}
		// should never be reached
		return null;
	}

	public boolean mutualDefeat() {
		return result.equals(CombatResult.MUTUAL_DEFEAT);
	}

	public Unit[] getDefeatedUnits() {
		switch (result) {
			case VICTORIOUS_ATTACK:
				return new Unit[] { defendingUnit };
			case VICTORIOUS_DEFENSE:
				return new Unit[] { attackingUnit };
			case MUTUAL_DEFEAT:
				return new Unit[] { attackingUnit, defendingUnit };
		}
		// should never be reached
		return null;
	}

	public static enum CombatResult {
		VICTORIOUS_ATTACK,
		VICTORIOUS_DEFENSE,
		MUTUAL_DEFEAT
	}

}
