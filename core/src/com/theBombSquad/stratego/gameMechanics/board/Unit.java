package com.theBombSquad.stratego.gameMechanics.board;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.theBombSquad.stratego.StrategoConstants.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class Unit implements Serializable {

	private static List<Integer> ids = new ArrayList<Integer>();
	private static Random random = new Random();

	public static final Unit AIR = new Air();
	public static final Unit LAKE = new Lake();

	@Getter
	protected final UnitType type;
	@Getter
	protected final PlayerID owner;
	@Getter @Setter
	protected int revealedInTurn = UNREVEALED;
	@Getter @Setter
	protected int movedInTurn = UNMOVED;
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
		int nextId;
		do {
			nextId = owner.ordinal() * 1000 + random.nextInt(1000);
		} while (ids.contains(nextId));
		ids.add(nextId);
		this.id = nextId;
	}

	/** Create Unit Token Without Any Ids */
	public static Unit createUnitToken(UnitType type, PlayerID player){
		return new Unit(type, player, true);
	}

	/** This Constructor is designed to only create tokens via the Token Factory */
	private Unit(UnitType type, PlayerID owner, boolean thisIsAToken){
		this.type = type;
		this.owner = owner;
		int nextId = -1;
//		ids.add(nextId);
		this.id = nextId;
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

	public boolean wasMoved(int turn) {
		return turn >= movedInTurn;
	}

	public boolean wasRevealed(int turn) {
		return revealedInTurn != UNREVEALED && turn >= revealedInTurn;
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

	/** Returns the Unit Type corresponding to a given rank (not including lake, unknown or air) */
	public static UnitType getUnitTypeOfRank(int rank){
		for(UnitType type : new UnitType[]{UnitType.FLAG, UnitType.BOMB, UnitType.SPY, UnitType.SCOUT, UnitType.SAPPER, UnitType.SERGEANT, UnitType.LIEUTENANT, UnitType.CAPTAIN, UnitType.MAJOR, UnitType.COLONEL, UnitType.GENERAL, UnitType.MARSHAL}){
			if(type.getRank()==rank){
				return type;
			}
		}
		System.out.println("getUnitTypeOfRank encountered a problem with rank "+rank);
		return null;
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
