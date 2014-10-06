package com.theBombSquad.stratego.player.humanoid;

import static com.theBombSquad.stratego.StrategoConstants.ASSUMED_WINDOW_WIDTH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
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
		PlayerBoardInput input = new PlayerBoardInput(this, (float)Gdx.graphics.getWidth() / (float)ASSUMED_WINDOW_WIDTH);
		Gdx.input.setInputProcessor(input);
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
				&& Game.getCurrent().getUnit(x, y).getOwner() == move
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
		//TODO: Remove this
		//randomSetup();
		//setSetUpPhase(false);
		setSetUpPhase(true);
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
		System.out.println(x+" "+y);
		if (y == 4 || y == 5) {
			// if middle of board
			// deselect(xSelected, ySelected)
			xSelected = -1;
			ySelected = -1;
		} else if (xSelected == -1 || ySelected == -1
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
		} else {
			// SWITCH AROUND
			gameView.hardSwapUnits(xSelected, ySelected, x, y);
			// deselect(xSelected, ySelected)
			xSelected = -1;
			ySelected = -1;
		}

	}

	public void submitSetUp() {
		Unit[][] setUp = new Unit[4][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 4; j++) {
				setUp[j][i] = gameView.getUnit(i, 6 + j);
			}
		}
		System.out.println("Test Setup");
		if (gameView.validateSetup(setUp)) {
			System.out.println("Valid Setup");
			gameView.setSetup(setUp);
		}
	}

	public void resetSetup() {
		gameView.startSetup();
	}
	
	
	//TODO Remove This as soon as proper setup is implemented
	protected void randomSetup() {
		Unit[][] setup = new Unit[4][10];
		List<Unit> availableUnits = new ArrayList<Unit>(40);
		Unit.UnitType[] unitTypeEnum = Unit.UnitType.values();
		// create a list containing all units that needs to be placed on the board
		for (Unit.UnitType type : unitTypeEnum) {
			for (int i = 0; i < type.getQuantity(); i++) {
				availableUnits.add(new Unit(type, gameView.getPlayerID()));
			}
		}
		//shuffle the list containing all available units
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

}
