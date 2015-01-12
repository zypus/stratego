package com.theBombSquad.stratego.player.ai;

import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.player.ai.setup.AIGameStateUnit;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;
import static com.theBombSquad.stratego.gameMechanics.board.Unit.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
@ToString
public class AIGameState {

	@Getter @Setter private PlayerID currentPlayer;
	@Getter @Setter private float probability;
    @Getter @Setter private boolean compressed;
	private AIGameStateUnit[][]       $aiUnits;
	@Getter private PlayerInformation own;
	@Getter private PlayerInformation opponent;
	@Getter @Setter private Object context = null;

	public AIGameState(int width, int height) {
		$aiUnits = new AIGameStateUnit[height][width];
		own = new PlayerInformation();
		opponent = new PlayerInformation();
	}

	public AIUnit getAIUnit(int x, int y) {

		if ($aiUnits[y][x].getPlayer1Unit().getOwner() == PLAYER_1) {
			return $aiUnits[y][x].getPlayer1Unit();
		} else {
			return $aiUnits[y][x].getPlayer2Unit();
		}
	}

	public void setAIUnit(int x, int y, AIUnit unit) {

		if ($aiUnits[y][x] == null) {
			$aiUnits[y][x] = new AIGameStateUnit();
		}
		if (unit.getOwner() == PLAYER_1) {
			$aiUnits[y][x].setPlayer1Unit(unit);
		} else if (unit.getOwner() == PLAYER_2) {
			$aiUnits[y][x].setPlayer2Unit(unit);
		} else {
			$aiUnits[y][x].setPlayer1Unit(unit);
			$aiUnits[y][x].setPlayer2Unit(unit);
		}
	}

	public void replaceAIUnit(int x, int y, AIUnit unit) {
		if (unit.getOwner() == PLAYER_1) {
			$aiUnits[y][x].setPlayer2Unit($aiUnits[y][x].getPlayer1Unit());
			$aiUnits[y][x].setPlayer1Unit(unit);
		} else if (unit.getOwner() == PLAYER_2) {
			$aiUnits[y][x].setPlayer1Unit($aiUnits[y][x].getPlayer2Unit());
			$aiUnits[y][x].setPlayer2Unit(unit);
		}
	}

	public void setAIUnitFor(int x, int y, AIUnit unit, PlayerID playerID) {
		if (playerID == PLAYER_1) {
			$aiUnits[y][x].setPlayer1Unit(unit);
		} else if (playerID == PLAYER_2) {
			$aiUnits[y][x].setPlayer2Unit(unit);
		}
	}

	public AIUnit getAIUnitFor(int x, int y, PlayerID playerID) {
		if (playerID == PLAYER_1) {
			return $aiUnits[y][x].getPlayer1Unit();
		} else if (playerID == PLAYER_2) {
			return $aiUnits[y][x].getPlayer2Unit();
		}
		return $aiUnits[y][x].getPlayer1Unit();
	}

	public boolean isOverloaded(int x, int y) {
		return $aiUnits[y][x].isOverloaded();
	}

	public int getWidth() {
		return $aiUnits[0].length;
	}

	public int getHeight() {
		return $aiUnits.length;
	}

	public PlayerInformation getPlayerInformation(PlayerID playerID) {
		if (playerID == NEMO) {
			return null;
		} else
			if (playerID == currentPlayer) {
				return own;
			} else {
				return opponent;
			}
	}

	public float[] getTotalProbabilityFor(UnitType type) {
		float sum[] = new float[2];
		for (int cx = 0; cx < getWidth(); cx++) {
			for (int cy = 0; cy < getHeight(); cy++) {
				AIUnit aiUnit = getAIUnit(cx, cy);
				if (aiUnit.getOwner() == PLAYER_1) {
					sum[0] += aiUnit.getProbabilityFor(type);
				} else if (aiUnit.getOwner() == PLAYER_2) {
					sum[1] += aiUnit.getProbabilityFor(type);
				}
			}
		}
		return sum;
	}

	public void swap(Move move) {
		AIUnit temp = getAIUnit(move.getToX(), move.getToY());
		setAIUnit(move.getToX(), move.getToY(), getAIUnit(move.getFromX(), move.getFromY()));
		setAIUnit(move.getFromX(), move.getFromY(), temp);
	}

	public AIGameState(AIGameState gameState) {
		this.$aiUnits = new AIGameStateUnit[gameState.getHeight()][gameState.getWidth()];
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				this.$aiUnits[y][x] = new AIGameStateUnit(gameState.$aiUnits[y][x]);
			}
		}
		this.probability = gameState.probability;
		this.currentPlayer = gameState.currentPlayer;
		this.own = new PlayerInformation(gameState.own);
		this.opponent = new PlayerInformation(gameState.opponent);
        this.compressed = gameState.compressed;
		this.context = gameState.context;
	}

	@Accessors(chain = true)
	@Data
	@NoArgsConstructor
	@ToString
	public static class PlayerInformation {
		private float[] defeated = new float[12];
		private float[] revealed = new float[12];
		public float unitCount;
		public float unrevealedUnitCount;
		public float unrevealedAndUnmovedUnitCount;

		public PlayerInformation(PlayerInformation information) {
			System.arraycopy(information.defeated, 0, this.defeated, 0, 12);
			System.arraycopy(information.revealed, 0, this.revealed, 0, 12);
			this.unitCount = information.unitCount;
			this.unrevealedUnitCount = information.unrevealedUnitCount;
			this.unrevealedAndUnmovedUnitCount = information.unrevealedAndUnmovedUnitCount;
		}

		public float getDefeatedFor(UnitType unitType) {
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

		public float getRevealedFor(UnitType unitType) {
			int ordinal = getOrdinal(unitType);
			return revealed[ordinal];
		}

		public PlayerInformation setDefeatedFor(UnitType unitType, float value) {
			int ordinal = getOrdinal(unitType);
			defeated[ordinal] = value;
			return this;
		}

		public PlayerInformation setRevealedFor(UnitType unitType, float value) {
			int ordinal = getOrdinal(unitType);
			revealed[ordinal] = value;
			return this;
		}

		public PlayerInformation addToDefeatedFor(UnitType unitType, float amount) {
			int ordinal = getOrdinal(unitType);
			defeated[ordinal] += amount;
			return this;
		}

		public PlayerInformation addToRevealedFor(UnitType unitType, float amount) {
			int ordinal = getOrdinal(unitType);
			revealed[ordinal] += amount;
			return this;
		}
	}

}
