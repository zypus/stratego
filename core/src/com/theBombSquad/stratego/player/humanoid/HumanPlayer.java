package com.theBombSquad.stratego.player.humanoid;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;

/**
 * TODO Add description
 * 
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
public class HumanPlayer extends Player {

	public HumanPlayer(GameView gameView) {
		super(gameView);
	}

	private PlayerID playerID = gameView.getPlayerID();
	private boolean setUpPhase = true;
	private int xSelected = -1;
	private int ySelected = -1;

	public void receiveInput(int x, int y) {
		Move move = new Move(xSelected, ySelected, x, y);
		move.setPlayerID(playerID);
		move.setMovedUnit(Game.getCurrent().getUnit(x, y));
		if (x < 0 || x > 9 || y < 0 || y > 9) {
		}
		// Checks if tile is not selected, air water or unknown or of your
		// opponent
		else if ((xSelected == -1 || ySelected == -1)
				&& (Game.getCurrent().getUnit(x, y).getType().getRank() != -1)
				&& Game.getCurrent().getUnit(x, y).getOwner() != move
						.getPlayerID()) {
			xSelected = x;
			ySelected = y;
			// select(x,y);
		}
		// your piece selected
		else if (xSelected != -1 || ySelected != -1) {
			// check if the move is valid
			if (gameView.validateMove(move)) {
				gameView.performMove(move);
				// deselect(xSelect,ySelect);
				xSelected = -1;
				ySelected = -1;
				// if invalid move
			} else {
				// your own piece
				if (Game.getCurrent().getUnit(x, y).getOwner() == move
						.getPlayerID()) {
					// select(x,y);
					// deselect(xSelected,ySelected);
					xSelected = x;
					ySelected = y;
					// something else that shouldn't be selected
				} else {
					// deselect(xSelected,ySelected);
					xSelected = -1;
					ySelected = -1;
				}
			}
		}
	}

	@Override
	protected void move() {

	}

	@Override
	protected void setup() {

	}

	@Override
	protected void idle() {

	}

	public boolean getSetUpPhase() {
		return setUpPhase;
	}

	public void setSetUpPhase(boolean setUpPhase) {
		this.setUpPhase = setUpPhase;
		xSelected = -1;
		ySelected = -1;
	}

	public void receiveSetUpInput(int x, int y) {
		if (xSelected == -1 || ySelected == -1
				&& Game.getCurrent().getUnit(x, y).getType().getRank() != -1) {
			// select piece
			xSelected = x;
			ySelected = y;
			// select(x,y);
		} else if (xSelected == x && ySelected == y) {
			// same piece
			// deselect(xSelected, ySelected)
			xSelected = -1;
			ySelected = -1;
		} else if (y > 3 && y < 6) {
			// if middle of board
			// deselect(xSelected, ySelected)
			xSelected = -1;
			ySelected = -1;
		} else {
			// SWITCH AROUND
			Move move1 = new Move(xSelected, ySelected, 4, 4);
			gameView.performMove(move1);
			Move move2 = new Move(x, y, xSelected, ySelected);
			gameView.performMove(move2);
			Move move3 = new Move(4, 4, x, y);
			gameView.performMove(move3);
			// deselect(xSelected, ySelected)
			xSelected = -1;
			ySelected = -1;
		}

	}

	public void submitSetUp() {
		Unit[][] setUp = new Unit[10][4];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 4; j++) {
				setUp[i][j] = Game.getCurrent().getUnit(i, 6 + j);
			}
		}
		if (gameView.validateSetup(setUp)) {
			gameView.setSetup(setUp);
		}
	}

	public void resetSetup() {
		gameView.startSetup();

	}

}
