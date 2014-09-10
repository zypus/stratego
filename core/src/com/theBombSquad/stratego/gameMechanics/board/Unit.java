package com.theBombSquad.stratego.gameMechanics.board;

import lombok.Getter;
import lombok.Setter;

import static com.theBombSquad.stratego.StrategoConstants.FIRST_TURN;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID;
import static com.theBombSquad.stratego.StrategoConstants.UNREVEALED;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
public class Unit {

	private static int idCounter = 1;

	public static final Unit AIR = new Air();
	public static final Unit LAKE = new Lake();
	public static final Unit UNKNOWN = new Unknown();

	@Getter
	protected final UnitType type;
	@Getter
	protected final PlayerID owner;
	@Getter @Setter
	protected int revealedInTurn = UNREVEALED;
	@Getter
	private final int id;

	public Unit(UnitType type, PlayerID owner) {
		this.type = type;
		this.owner = owner;
		this.id = idCounter++;
	}

	public static enum UnitType {
		AIR(-1,-1),
		LAKE(-1,-1),
		UNKNOWN(-1,-1),
		FLAG(0,1),
		BOMB(0,6),
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
		private Unknown() {
			super(UnitType.UNKNOWN, PlayerID.NEMO);
			revealedInTurn = FIRST_TURN;
		}
	}

}
