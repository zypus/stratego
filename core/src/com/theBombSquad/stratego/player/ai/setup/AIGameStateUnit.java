package com.theBombSquad.stratego.player.ai.setup;

import com.theBombSquad.stratego.player.ai.AIUnit;
import lombok.Data;

import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 07/01/15
 */
@Data
public class AIGameStateUnit {
	AIUnit player1Unit;
	AIUnit player2Unit;

	public AIGameStateUnit() {
		player1Unit = new AIUnit();
		player2Unit = new AIUnit();
	}

	public AIGameStateUnit(AIGameStateUnit aiGameStateUnit) {
		player1Unit = new AIUnit(aiGameStateUnit.player1Unit);
		player2Unit = new AIUnit(aiGameStateUnit.player2Unit);
	}

	public boolean isOverloaded() {
		return player1Unit.getOwner() != NEMO && player2Unit.getOwner() != NEMO && player1Unit.getOwner() != null && player2Unit.getOwner() != null;
	}

}
