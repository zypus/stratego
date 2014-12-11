package com.theBombSquad.stratego.player.ai.players;

import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.players.random.SetupPlayerAI;

import java.util.Collections;
import java.util.List;

import static com.theBombSquad.stratego.gameMechanics.Game.*;

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

	@Override protected Move move() {
		Move move;
		SchrodingersBoard b = new SchrodingersBoard(this.gameView);
		List<Move> possibleMoves = b.generateAllMoves(this.gameView.getPlayerID());//AI.createAllLegalMoves(gameView, gameView.getCurrentState());
//		for(int c=0; c<possibleMoves.size(); c++){
//			Move mover = possibleMoves.get(c);
//			if(gameView.isEnemy(mover.getToX(), mover.getToY())){
//				if(!gameView.isUnknown(mover.getToX(), mover.getToY())){
//					if(!gameView.willWin(mover.getFromX(), mover.getFromY(), mover.getToX(), mover.getToY())){
//						possibleMoves.remove(c);
//						c--;
//					}
//				}
//			}
//		}
		Collections.shuffle(possibleMoves);
		move = possibleMoves.get(0);
		gameView.performMove(move);
		return move;
	}

	@Override protected Setup setup() {
		return new SetupPlayerAI(gameView).setup_directAccessOverwrite();
//		Setup setup = new Setup(10,4);
//		List<Unit> availableUnits = new ArrayList<Unit>(gameView.getAvailableUnits());
//		// shuffle the list containing all available units
//		Collections.shuffle(availableUnits);
//		//go through the list and place them on the board as the units appear in the randomly shuffled list
//		for (int y = 0; y < 4; y++) {
//			for (int x = 0; x < 10; x++) {
//				setup.setUnit(x, y, availableUnits.get(y * 10 + x));
//			}
//		}
//		// no need to check if the setup is valid because it cannot be invalid by the way it is created
//		// so simply sending the setup over to the game
//		gameView.setSetup(setup);
//		return setup;
	}

}
