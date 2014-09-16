package com.theBombSquad.stratego.player.humanoid;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.Move;
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
	
	private int xSelected=-1;
	private int ySelected=-1;
	
	public void receiveInput(int x, int y){
		Move move = new Move(xSelected, ySelected, x,y);
		if(x<0||x>9||y<0||y>9){}
		//Checks if tile is not selected, air water or unknown or of your opponent
		else if((xSelected==-1||ySelected==-1)&&(Game.getCurrent().getUnit(x,y).getType().getRank()!=-1)&&Game.getCurrent().getUnit(x,y).getOwner() != move.getPlayerID()){			
			xSelected=x;
			ySelected=y;
			//select(x,y);
		}
		//your piece selected
		else if(xSelected!=-1||ySelected!=-1){
			//check if the move is valid
			if(gameView.validateMove(move)){
				gameView.performMove(move);
				//deselect(xSelect,ySelect);
				xSelected = -1;
				ySelected = -1;
			//if invalid move
			} else {
				//your own piece
				if(Game.getCurrent().getUnit(x,y).getOwner() == move.getPlayerID()){
					//select(x,y);
					//deselect(xSelected,ySelected);
					xSelected=x;
					ySelected=y;
				//something else that shouldn't be selected
				}else {
					//deselect(xSelected,ySelected);
					xSelected = -1;
					ySelected = -1;
				}
			}
		}
		
		
	}
	
	@Override protected void move() {

	}

	@Override protected void setup() {

	}

	@Override protected void idle() {

	}
	
	
}
