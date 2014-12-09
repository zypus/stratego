package com.theBombSquad.stratego.player.ai.players.TDStratego;

import Jama.Matrix;
import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersBoard;
import com.theBombSquad.stratego.player.ai.schrodingersBoard.SchrodingersUnit;

import static com.theBombSquad.stratego.StrategoConstants.PlayerID.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 20/11/14
 */
public class TDStratego
		extends AI {

	public TDStratego(Game.GameView gameView) {
		super(gameView);
	}

	@Override
	protected Move move() {
		return null;
	}

	@Override
	protected Setup setup() {
		return null;
	}

	private class TDPlayer extends AbstractTDPlayer<SchrodingersBoard> {

		private final int UNIT_TYPE_COUNT = 12; /** The ten ranks plus bomb and flag*/
		private final int PLAYER_FLAG = 2; /** Owner of the unit*/
		private final int NUMBER_OF_LAKES = 8;

		@Override
		protected Matrix stateToActivation(SchrodingersBoard state) {
			int infoSize = (state.getWidth() * state.getHeight() - NUMBER_OF_LAKES) * (UNIT_TYPE_COUNT + PLAYER_FLAG)
						   + PLAYER_FLAG * UNIT_TYPE_COUNT
						   + PLAYER_FLAG;
			Matrix activation = new Matrix(infoSize, 1);
			int index = 0;
			for (int x = 0; x < state.getWidth(); x++) {
				for (int y = 0; y < state.getHeight(); y++) {
					SchrodingersUnit unit = state.getUnit(x, y);
					if (!unit.isLake()) {
						// owner
						activation.set(index++, 1, (unit.getOwner() == PLAYER_1) ? 1 : 0);
						activation.set(index++, 1, (unit.getOwner() == PLAYER_2) ? 1 : 0);
						// unit probabilities
						for ()
					}

				}
			}
			return null;
		}

		@Override
		protected float utilityValue(SchrodingersBoard state, Matrix output) {
			return 0;
		}
	}

}
