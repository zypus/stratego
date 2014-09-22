package com.theBombSquad.stratego.player.ai.players.random;

import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AI;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
@Log
public class RandomAI extends AI {

	public RandomAI(GameView gameView) {
		super(gameView);
	}

	@Override protected Move move() {
		// if this method is not calling gameview.performMove() the game will not advance
		// TODO do random moves
		log.info("Player "+((gameView.getPlayerID().equals(StrategoConstants.PlayerID.PLAYER_1)?1:2))+": Moving!");
		return null;
	}

	@Override protected Setup setup() {
		Setup setup = new Setup(10,4);
		List<Unit> availableUnits = new ArrayList<Unit>(40);
		Unit.UnitType[] unitTypeEnum = Unit.UnitType.values();
		// create a list containing all units that needs to be placed on the board
		for (Unit.UnitType type : unitTypeEnum) {
			for (int i = 0; i < type.getQuantity(); i++) {
				availableUnits.add(new Unit(type, gameView.getPlayerID()));
			}
		}
		// shuffle the list containing all available units
		Collections.shuffle(availableUnits);
		//go through the list and place them on the board as the units appear in the randomly shuffled list
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 10; x++) {
				setup.setUnit(x, y, availableUnits.get(y * 10 + x));
			}
		}
		// no need to check if the setup is valid because it cannot be invalid by the way it is created
		// so simply sending the setup over to the game
		gameView.setSetup(setup);
		return setup;
	}

	@Override protected void idle() {

	}
}
