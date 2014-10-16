package com.theBombSquad.stratego.player.humanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.theBombSquad.stratego.StrategoConstants.*;
import com.theBombSquad.stratego.gameMechanics.GameView;
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
		gameView.performMove(returnableMove);
		movePhase = false;
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
										  && gameView.getUnit(x, y).getOwner() == gameView.getPlayerID()) {
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
				gameView.hardSwapUnits(xSelected, ySelected, x, y);
				// deselect(xSelected, ySelected)
				xSelected = -1;
				ySelected = -1;
			}
		}
	}

	public void submitSetUp() {
		if(setUpPhase){
			Setup setUp = new Setup(10,4);
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 4; j++) {
					setUp.setUnit(i, j, gameView.getUnit(i, 6 + j));
				}
			}
			if (gameView.validateSetup(setUp)) {
				//Remove All Units
				for (int cy = 0; cy < gameView.getCurrentState().getHeight(); cy++) {
					for (int cx = 0; cx < gameView.getCurrentState().getWidth(); cx++) {
						if (gameView.getCurrentState().getUnit(cx, cy).getType().getRank() >= 0) {
							gameView.setUnit(cx, cy, Unit.AIR);
						}
					}
				}
				setSetup(setUp);
			}
		}
	}

	private void setSetup(Setup setup){
		this.setupToSend = setup;
	}

	public void resetSetup() {
		if(setUpPhase){
			gameView.startSetup();
		}
	}

	protected void randomSetup() {
		if(setUpPhase){
			Setup setup = new Setup(10,4);
			List<Unit> availableUnits = new ArrayList<Unit>(gameView.getAvailableUnits());
			//shuffle the list containing all available units
			Collections.shuffle(availableUnits);
			//go through the list and place them on the board as the units appear in the randomly shuffled list
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 10; x++) {
					setup.setUnit(x,y,availableUnits.get(y * 10 + x));
				}
			}
			//Remove All Units
			for(int cy=0; cy<gameView.getCurrentState().getHeight(); cy++){
				for(int cx=0; cx<gameView.getCurrentState().getWidth(); cx++){
					if(!gameView.getCurrentState().getUnit(cx, cy).isLake()){
						gameView.setUnit(cx, cy, Unit.AIR);
					}
				}
			}
			//Add Setup
			for(int cy=0; cy<4; cy++){
				for(int cx=0; cx<10; cx++){
					gameView.setUnit(cx, cy+6, availableUnits.get(cy * 10 + cx));
				}
			}
		}
	}

	public void setxMouseOver(int xMouseOver) {
		if (xMouseOver < 0 || xMouseOver > GRID_WIDTH-1) {
			this.xMouseOver = -1;
		} else {
			this.xMouseOver = xMouseOver;
		}
	}

	public void setyMouseOver(int yMouseOver) {
		if (yMouseOver < 0 || yMouseOver > GRID_WIDTH - 1) {
			this.yMouseOver = -1;
		} else {
			this.yMouseOver = yMouseOver;
		}
	}

	public void saveSetup() {
		if (setUpPhase) {
			Setup setUp = new Setup(10, 4);
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 4; j++) {
					setUp.setUnit(i, j, gameView.getUnit(i, 6 + j));
				}
			}
			if (gameView.validateSetup(setUp)) {
				JFileChooser fileChooser = new JFileChooser(SETUP_PATH);
				if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(null)) {
					File file = fileChooser.getSelectedFile();
					Setup.writeToFile(file, setUp);
				}
			}
		}
	}

	public void loadSetup() {
		if (setUpPhase) {
			JFileChooser fileChooser = new JFileChooser(SETUP_PATH);
			if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
				File file = fileChooser.getSelectedFile();
				Setup setup = Setup.readFromFile(file, new ArrayList<Unit>(gameView.getAvailableUnits()));
				//Remove All Units
				for (int cy = 0; cy < gameView.getCurrentState().getHeight(); cy++) {
					for (int cx = 0; cx < gameView.getCurrentState().getWidth(); cx++) {
						if (!gameView.getCurrentState().getUnit(cx, cy).isLake()) {
							gameView.setUnit(cx, cy, Unit.AIR);
						}
					}
				}
				//Add Setup
				for (int cy = 0; cy < 4; cy++) {
					for (int cx = 0; cx < 10; cx++) {
						gameView.setUnit(cx, cy + 6, setup.getUnit(cx,cy));
					}
				}
			}
		}
	}
}
