package com.theBombSquad.stratego.gameMechanics.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
@Getter
@AllArgsConstructor
public class Encounter {

	private Unit attackingUnit;
	private Unit defendingUnit;

	private CombatResult result;

	public Encounter(Unit attackingUnit, Unit defendingUnit) {
		this.attackingUnit=attackingUnit;
		this.defendingUnit=defendingUnit;
		if (defendingUnit.getType() == defendingUnit.getType().BOMB) {
			if (attackingUnit.getType() == attackingUnit.getType().SAPPER) {
				result=CombatResult.VICTORIOUS_ATTACK;
			} else {
				result=CombatResult.MUTUAL_DEFEAT;
			}
		} else if (defendingUnit.getType() == defendingUnit.getType().MARSHAL
				&& attackingUnit.getType() == attackingUnit.getType().SPY) {
			result=CombatResult.VICTORIOUS_ATTACK;
		} else {
			int defendingRank = defendingUnit.getType().getRank();
			int attackingRank = attackingUnit.getType().getRank();
			if (attackingRank > defendingRank) {
				result=CombatResult.VICTORIOUS_ATTACK;
			} else if (attackingRank == defendingRank) {
				result=CombatResult.MUTUAL_DEFEAT;
			} else {
				result=CombatResult.VICTORIOUS_DEFENSE;
			}
		}
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
		VICTORIOUS_ATTACK, VICTORIOUS_DEFENSE, MUTUAL_DEFEAT
	}

}
