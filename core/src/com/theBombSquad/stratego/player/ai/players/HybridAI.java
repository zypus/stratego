package com.theBombSquad.stratego.player.ai.players;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.AI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created 11/12/14
 */
@Accessors(chain = true)
public class HybridAI extends AI {

	@Getter @Setter Player setuper;
	@Getter @Setter Player mover;

	public HybridAI(Game.GameView gameView) {
		super(gameView);
		setuper = new RandomAI(gameView);
		mover = setuper;
	}

	@Override
	protected Move move() {
		return mover.move_directAccessOverwrite();
	}

	@Override
	protected Setup setup() {
		return setuper.setup_directAccessOverwrite();
	}
}
