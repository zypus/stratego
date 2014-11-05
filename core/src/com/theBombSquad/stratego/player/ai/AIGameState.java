package com.theBombSquad.stratego.player.ai;

import com.theBombSquad.stratego.gameMechanics.board.Move;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.gameMechanics.board.Unit.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
public class AIGameState {

	@Getter @Setter private PlayerID currentPlayer;
	@Getter @Setter private float probability;
	private AIUnit[][] aiUnits;
	@Getter private PlayerInformation own;
	@Getter private PlayerInformation opponent;

	public AIGameState(int width, int height) {
		aiUnits = new AIUnit[height][width];
		own = new PlayerInformation();
		opponent = new PlayerInformation();
	}

	public AIUnit getAIUnit(int x, int y) {
		return aiUnits[y][x];
	}

	public void setAIUnit(int x, int y, AIUnit unit) {
		aiUnits[y][x] = unit;
	}

	public int getWidth() {
		return aiUnits[0].length;
	}

	public int getHeight() {
		return aiUnits.length;
	}

	public void swap(Move move) {
		AIUnit temp = getAIUnit(move.getToX(), move.getToY());
		setAIUnit(move.getToX(), move.getToY(), getAIUnit(move.getFromX(), move.getFromY()));
		setAIUnit(move.getFromX(), move.getFromY(), temp);
	}

	public AIGameState(AIGameState gameState) {
		this.aiUnits = new AIUnit[gameState.getHeight()][gameState.getWidth()];
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				setAIUnit(x, y, new AIUnit(gameState.getAIUnit(x,y)));
			}
		}
		this.probability = gameState.probability;
		this.currentPlayer = gameState.currentPlayer;
		this.own = new PlayerInformation(gameState.own);
		this.opponent = new PlayerInformation(gameState.opponent);
	}

	@Accessors(chain = true)
	@Data
	@NoArgsConstructor
	public static class PlayerInformation {
		private int[] defeated = new int[12];
		private int[] revealed = new int[12];
		public int unitCount;
		public int unrevealedUnitCount;
		public int unrevealedAndUnmovedUnitCount;

		public PlayerInformation(PlayerInformation information) {
			System.arraycopy(information.defeated, 0, this.defeated, 0, 12);
			System.arraycopy(information.revealed, 0, this.revealed, 0, 12);
			this.unitCount = information.unitCount;
			this.unrevealedUnitCount = information.unrevealedUnitCount;
			this.unrevealedAndUnmovedUnitCount = information.unrevealedAndUnmovedUnitCount;
		}

		public int getDefeatedFor(UnitType unitType) {
			int ordinal = getOrdinal(unitType);
			return defeated[ordinal];
		}

		private int getOrdinal(UnitType unitType) {
			int ordinal = unitType.ordinal();
			if (ordinal < 3) {
				throw new RuntimeException("Wrong unit type.");
			}
			return ordinal-3;
		}

		public int getRevealedFor(UnitType unitType) {
			int ordinal = getOrdinal(unitType);
			return revealed[ordinal];
		}

		public PlayerInformation setDefeatedFor(UnitType unitType, int value) {
			int ordinal = getOrdinal(unitType);
			defeated[ordinal] = value;
			return this;
		}

		public PlayerInformation setRevealedFor(UnitType unitType, int value) {
			int ordinal = getOrdinal(unitType);
			revealed[ordinal] = value;
			return this;
		}

		public PlayerInformation addToDefeatedFor(UnitType unitType, int amount) {
			int ordinal = getOrdinal(unitType);
			defeated[ordinal] += amount;
			return this;
		}

		public PlayerInformation addToRevealedFor(UnitType unitType, int amount) {
			int ordinal = getOrdinal(unitType);
			revealed[ordinal] += amount;
			return this;
		}
	}

}
