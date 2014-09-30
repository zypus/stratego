package com.theBombSquad.stratego.player.ai.players.random;

import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
public class RandomAI extends AI {

	public RandomAI(GameView gameView) {
		super(gameView);
	}

	@Override protected void move() {
		Move move;
		List<Move> possibleMoves = super.createAllLegalMoves(gameView.getCurrentState());
		Collections.shuffle(possibleMoves);
		move = possibleMoves.get(0);
		gameView.performMove(move);
	}

	@Override protected void setup() {
		Unit[][] setup = new Unit[4][10];
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
				setup[y][x] = availableUnits.get(y*10+x);
			}
		}
		// no need to check if the setup is valid because it cannot be invalid by the way it is created
		// so simply sending the setup over to the game
		gameView.setSetup(setup);
	}

	@Override protected void idle() {

	}
}
