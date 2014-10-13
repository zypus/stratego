package com.theBombSquad.stratego.gameMechanics.board;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.theBombSquad.stratego.StrategoConstants.FIRST_TURN;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID;
import static com.theBombSquad.stratego.StrategoConstants.UNREVEALED;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class Unit implements Serializable {

	private static int idCounter = 1;

	public static final Unit AIR = new Air();
	public static final Unit LAKE = new Lake();

	@Getter
	protected final UnitType type;
	@Getter
	protected final PlayerID owner;
	@Getter @Setter
	protected int revealedInTurn = UNREVEALED;
	@Getter
	private final int id;

	protected Unit() {
		type = UnitType.AIR;
		owner = PlayerID.NEMO;
		id = -1;
	}

	public Unit(UnitType type, PlayerID owner) {
		this.type = type;
		this.owner = owner;
		int baseID = owner.ordinal()+100;
		this.id = baseID+idCounter++;
	}

	public Unit(UnitType type, PlayerID owner, int specificID) {
		this.type = type;
		this.owner = owner;
		this.id = specificID;
	}

	public boolean isAir() {
		return type==UnitType.AIR;
	}

	public boolean isLake() {
		return type==UnitType.LAKE;
	}

	public boolean isUnknown() {
		return type == UnitType.UNKNOWN;
	}

	public static enum UnitType {
		AIR(-1,-1),
		LAKE(-1,-1),
		UNKNOWN(-2,-1),
		FLAG(0,1),
		BOMB(11,6),
		SPY(1,1),
		SCOUT(2,8),
		SAPPER(3,5),
		SERGEANT(4,4),
		LIEUTENANT(5,4),
		CAPTAIN(6,4),
		MAJOR(7,3),
		COLONEL(8,2),
		GENERAL(9,1),
		MARSHAL(10,1);

		@Getter
		private int rank;
		@Getter
		private int quantity;

		private UnitType(int rank, int quantity) {
			this.rank = rank;
			this.quantity = quantity;
		}
	}

	private static class Air extends Unit {
		private Air() {
			super(UnitType.AIR, PlayerID.NEMO);
			revealedInTurn = FIRST_TURN;
		}
	}

	private static class Lake
			extends Unit {
		private Lake() {
			super(UnitType.LAKE, PlayerID.NEMO);
			revealedInTurn = FIRST_TURN;
		}
	}

	private static class Unknown extends Unit {
		private Unknown(Unit unit) {
			super(UnitType.UNKNOWN, unit.getOwner(), unit.getId());
		}
	}

	public static class UnknownUnitPool {

		@Getter private static UnknownUnitPool instance = new UnknownUnitPool();

		private Map<Integer, Unknown> pool = new HashMap<Integer, Unknown>();

		public Unknown getUnknownForUnit(Unit unit) {
			Unknown unknown = pool.get(unit.getId());
			// if the requested object doesn't exist yet create and store it
			if (unknown == null) {
				unknown = new Unknown(unit);
				pool.put(unit.getId(), unknown);
			}
			return unknown;
		}

		private void UnknowUnitPool() {

		}

	}

}
