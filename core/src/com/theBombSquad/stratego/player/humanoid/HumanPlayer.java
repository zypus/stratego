package com.theBombSquad.stratego.player.humanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.theBombSquad.stratego.StrategoConstants.*;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Setup;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;
import lombok.Getter;
import lombok.Setter;

import javax.swing.JFileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.gameMechanics.Game.*;

/**
 * TODO Add description
 *
 * @author Fabian Fr�nz <f.fraenz@t-online.de>
 * @author Flo
 */
public class HumanPlayer extends Player {

	public HumanPlayer(GameView gameView) {
		super(gameView);
		PlayerBoardInput input = new PlayerBoardInput(this, (float)Gdx.graphics.getWidth() / (float)ASSUMED_WINDOW_WIDTH);
		((InputMultiplexer)Gdx.input.getInputProcessor()).addProcessor(input);
	}

	private PlayerID playerID = gameView.getPlayerID();
	private boolean setUpPhase = false;
	private boolean movePhase = false;
	@Getter private Setup unitPallet = new Setup(10, 4);
	@Getter private Setup currentSetup = new Setup(10, 4);
	@Getter private int xSelected = -1;
	@Getter private int ySelected = -1;

	@Getter int xMouseOver = -1;
	@Getter int yMouseOver = -1;

	/** The move that will be sent by move */
	private Move moveToSend = null;
	/** The Setup that will be sent by setup */
	private Setup setupToSend = null;

	/** If the board is actually flipped for this player, i.e. this is player 2 on normal game view */
	@Setter
	private boolean flippedBoard = false;


	public void receiveInput(int x, int y) {
		if (movePhase) {
			Move move = new Move(xSelected, ySelected, x, y);
			//move.setMovedUnit(gameView.getUnit(x, y));
			if (x < 0 || x > 9 || y < 0 || y > 9) {
			}
			// Checks if tile is not selected, air water or unknown or of your
			// opponent
			else if ((xSelected == -1 || ySelected == -1)
					 && (gameView.getUnit(x, y).getType().getRank() != -1)
					 && gameView.getUnit(x, y).getOwner() == gameView
					.getPlayerID()) {
				xSelected = x;
				ySelected = y;
				// select(x,y);
			}
			// your piece selected
			else if (xSelected != -1 || ySelected != -1) {
				// check if the move is valid
				if (gameView.validateMove(move)) {
					performMove(move);
					// deselect(xSelect,ySelect);
					xSelected = -1;
					ySelected = -1;
					// if invalid move
				} else {
					// your own piece
					if (gameView.getUnit(x, y).getOwner() == gameView.getPlayerID()) {
						// select(x,y);
						// deselect(xSelected,ySelected);
						if (xSelected == x && ySelected == y) {
							xSelected = -1;
							ySelected = -1;
						} else {
							xSelected = x;
							ySelected = y;
						}
						// something else that shouldn't be selected
					} else {
						// deselect(xSelected,ySelected);
						xSelected = -1;
						ySelected = -1;
					}
				}
			}
		}
	}

	/** Sets Move that will be sent by the Move Method */
	private void performMove(Move move){
		if(gameView.validateMove(move)){
			this.moveToSend = move;
		}
	}

	@Override
	protected Move move() {
		movePhase = true;
		final int sleepTime = 5;
		while(moveToSend==null){
			try{Thread.sleep(sleepTime);}catch(Exception ex){}
		}
		Move returnableMove = moveToSend;
		this.moveToSend = null;
		movePhase = false;
		gameView.performMove(returnableMove);
		return returnableMove;
	}

	@Override
	protected Setup setup() {
		setSetUpPhase(true);
		resetSetup();
		final int sleepTime = 5;
		while(setupToSend==null){
			try{Thread.sleep(sleepTime);}catch(Exception ex){}
		}
		Setup returnableSetup = setupToSend;
		this.setupToSend = null;
		this.setUpPhase = false;
		gameView.setSetup(returnableSetup);
		return returnableSetup;
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
		if (setUpPhase) {
			if (x < 0 || x > 9 || y < 0 || y > 9) {
				xSelected = -1;
				ySelected = -1;
			}
			else if (y == 4 || y == 5) {
				// if middle of board
				// deselect(xSelected, ySelected)
				xSelected = -1;
				ySelected = -1;
			} else if (xSelected == -1 && ySelected == -1
										  && getUnit(x, y).getOwner() == gameView.getPlayerID()) {
				// select piece
				xSelected = x;
				ySelected = y;
				// select(x,y);
			} else if (xSelected == x && ySelected == y) {
				// same piece
				// deselect(xSelected, ySelected)
				xSelected = -1;
				ySelected = -1;
			} else if (xSelected != -1 && ySelected != -1){
				// SWITCH AROUND
				swap(xSelected, ySelected, x, y);
				// deselect(xSelected, ySelected)
				xSelected = -1;
				ySelected = -1;
			}
		}
	}

	private Unit getUnit(int x, int y) {
		GameBoard board;
		if (y < 4) {
			board = unitPallet;
		} else {
			board = currentSetup;
			y -= 6;
		}
		return board.getUnit(x, y);
	}

	private void setUnit(int x, int y, Unit unit) {
		GameBoard board;
		if (y < 4) {
			board = unitPallet;
		} else {
			board = currentSetup;
			y -= 6;
		}
		board.setUnit(x, y, unit);
	}

	private void swap(int x1, int y1, int x2, int y2) {
		Unit u1 = getUnit(x1, y1);
		Unit u2 = getUnit(x2, y2);
		setUnit(x1, y1, u2);
		setUnit(x2, y2, u1);
	}

	public void submitSetUp() {
		if(setUpPhase){
			if (gameView.validateSetup(currentSetup)) {
				setSetup(currentSetup);
			}
		}
	}

	private void setSetup(Setup setup){
		this.setupToSend = setup;
	}

	public void resetSetup() {
		if(setUpPhase){
			// clear the current setup
			for (int cy = 0; cy < currentSetup.getHeight(); cy++) {
				for (int cx = 0; cx < currentSetup.getWidth(); cx++) {
						currentSetup.setUnit(cx, cy, Unit.AIR);
				}
			}
			// refill the unit pallet
			List<Unit> units = gameView.getAvailableUnits();
			int counter = 0;
			for (int i = 0; i < unitPallet.getWidth(); i++) {
				for (int j = 0; j < unitPallet.getHeight(); j++) {
					unitPallet.setUnit(i, j, units.get(counter));
					counter++;
				}
			}
		}
	}

	protected void randomSetup() {
		if(setUpPhase){
			List<Unit> availableUnits = new ArrayList<Unit>(gameView.getAvailableUnits());
			//shuffle the list containing all available units
			Collections.shuffle(availableUnits);
			//go through the list and place them int the setup as the units appear in the randomly shuffled list
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 10; x++) {
					currentSetup.setUnit(x,y,availableUnits.get(y * 10 + x));
				}
			}
			clearUnitPallet();
		}
	}

	private void clearUnitPallet() {
		//Remove All Units from unit pallet
		for(int cy=0; cy<unitPallet.getHeight(); cy++){
			for(int cx=0; cx< unitPallet.getWidth(); cx++){
				unitPallet.setUnit(cx, cy, Unit.AIR);
			}
		}
	}

	public void setxMouseOver(int xMouseOver) {
		if (setUpPhase || movePhase) {
			if (xMouseOver < 0 || xMouseOver > GRID_WIDTH - 1) {
				this.xMouseOver = -1;
			} else {
				this.xMouseOver = xMouseOver;
			}
		}
	}

	public void setyMouseOver(int yMouseOver) {
		if (setUpPhase || movePhase) {
			if (yMouseOver < 0 || yMouseOver > GRID_WIDTH - 1) {
				this.yMouseOver = -1;
			} else {
				this.yMouseOver = yMouseOver;
			}
		}
	}

	public void saveSetup() {
		if (setUpPhase) {
			if (gameView.validateSetup(currentSetup)) {
				JFileChooser fileChooser = new JFileChooser(SETUP_PATH);
				if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(null)) {
					File file = fileChooser.getSelectedFile();
					Setup.writeToFile(file, currentSetup);
				}
			}
		}
	}

	public void loadSetup() {
		if (setUpPhase) {
			JFileChooser fileChooser = new JFileChooser(SETUP_PATH);
			if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
				File file = fileChooser.getSelectedFile();
				currentSetup = Setup.readFromFile(file, new ArrayList<Unit>(gameView.getAvailableUnits()));
				clearUnitPallet();
			}
		}
	}
}
