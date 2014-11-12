package com.theBombSquad.stratego.gameMechanics.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static com.theBombSquad.stratego.gameMechanics.board.Encounter.CombatResult.*;
import static com.theBombSquad.stratego.gameMechanics.board.Unit.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
@Getter
@AllArgsConstructor
public class Encounter implements Serializable {

	private Unit attackingUnit;
	private Unit defendingUnit;

	private CombatResult result;

	protected Encounter() {

	}

	public Encounter(Unit attackingUnit, Unit defendingUnit) {
		this.attackingUnit=attackingUnit;
		this.defendingUnit=defendingUnit;
		result = resolveFight(attackingUnit.getType(), defendingUnit.getType());
	}

	public Unit getVictoriousUnit() {
		//TODO: replace null with ari
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
		return result.equals(MUTUAL_DEFEAT);
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
		VICTORIOUS_ATTACK, VICTORIOUS_DEFENSE, MUTUAL_DEFEAT
	}

	public static CombatResult resolveFight(UnitType attacker, UnitType defender) {
		if (defender == UnitType.BOMB) {
			if (attacker == UnitType.SAPPER) {
				return VICTORIOUS_ATTACK;
			} else {
				return VICTORIOUS_DEFENSE;
			}
		} else if (defender == UnitType.MARSHAL
				   && attacker == UnitType.SPY) {
			return VICTORIOUS_ATTACK;
		} else {
			int attackingRank = attacker.getRank();
			int defendingRank = defender.getRank();
			if (attackingRank > defendingRank) {
				return VICTORIOUS_ATTACK;
			} else if (attackingRank == defendingRank) {
				return MUTUAL_DEFEAT;
			} else {
				return VICTORIOUS_DEFENSE;
			}
		}
	}

}
