package com.theBombSquad.stratego.player.ai;

import com.theBombSquad.stratego.gameMechanics.board.Unit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.theBombSquad.stratego.StrategoConstants.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
@NoArgsConstructor
@Accessors(chain = true)
public class AIUnit {

	@Getter @Setter private boolean revealed = false;
	@Getter @Setter private boolean moved = false;
	@Getter @Setter private PlayerID owner;
	@Getter @Setter private Unit unitReference;
	@Getter @Setter private float[] unitTypeProbabilities = new float[12];
	@Getter private int possibilities = 0;

	public float[] getProbabilities() {
		return unitTypeProbabilities;
	}

	public float getProbabilityFor(Unit.UnitType unitType) {
		int ordinal = unitType.ordinal();
		if (ordinal < 3) {
			throw new RuntimeException("Wrong unit type.");
		}
		return unitTypeProbabilities[ordinal - 3];
	}

	public AIUnit setProbabilityFor(Unit.UnitType unitType, float prob) {
		int ordinal = unitType.ordinal();
		if (ordinal < 3) {
			throw new RuntimeException("Wrong unit type.");
		}
		if (prob > 0 && unitTypeProbabilities[ordinal - 3] == 0) {
			possibilities++;
		} else if (prob == 0 && unitTypeProbabilities[ordinal - 3] > 0) {
			possibilities--;
		}
		unitTypeProbabilities[ordinal - 3] = prob;
		return this;
	}

	public void clearProbabilities() {
		unitTypeProbabilities = new float[12];
		possibilities = 0;
	}

	public float getProbabilitySum() {
		int sum = 0;
		for (float prob : unitTypeProbabilities) {
			sum += prob;
		}
		return sum;
	}

	public AIUnit(AIUnit aiUnit) {
		this.revealed = aiUnit.revealed;
		this.moved = aiUnit.moved;
		this.owner = aiUnit.owner;
		this.unitReference = aiUnit.unitReference;
		System.arraycopy(aiUnit.unitTypeProbabilities, 0, this.unitTypeProbabilities, 0, 12);
		this.possibilities = aiUnit.possibilities;
	}

}
