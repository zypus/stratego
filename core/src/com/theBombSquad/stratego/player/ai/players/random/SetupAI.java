package com.theBombSquad.stratego.player.ai.players.random;

import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.ai.AI;
import com.theBombSquad.stratego.player.ai.setup.AISetup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetupAI extends AI {
	public SetupAI(GameView gameView) {
		super(gameView);
	}

	@Override
	protected Move move() {
		Move move;
		List<Move> possibleMoves = AI.createAllLegalMoves(gameView,
				gameView.getCurrentState());
		for (int c = 0; c < possibleMoves.size(); c++) {
			Move mover = possibleMoves.get(c);
			if (gameView.isEnemy(mover.getToX(), mover.getToY())) {
				if (!gameView.isUnknown(mover.getToX(), mover.getToY())) {
					if (!gameView.willWin(mover.getFromX(), mover.getFromY(),
							mover.getToX(), mover.getToY())) {
						possibleMoves.remove(c);
						c--;
					}
				}
			}
		}
		Collections.shuffle(possibleMoves);
		move = possibleMoves.get(0);
		gameView.performMove(move);
		return move;
	}

	@Override
	protected AISetup setup() {
		AISetup setup = new AISetup(gameView);
		gameView.setSetup(setup);
		return setup;
	}

}
