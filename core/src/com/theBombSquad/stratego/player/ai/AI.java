package com.theBombSquad.stratego.player.ai;

import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.player.Player;

/**
 * Abstract AI class which gives access to several utility stuff.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public abstract class AI extends Player {

	public AI(GameView gameView) {
		super(gameView);
	}
}
