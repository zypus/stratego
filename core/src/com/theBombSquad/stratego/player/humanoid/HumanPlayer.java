package com.theBombSquad.stratego.player.humanoid;

import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
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
		if(x<0||x>9||y<0||y>9){}
		else if((xSelected==-1||ySelected==-1)&&(Game.getCurrent().getUnit(x,y).getType().getRank()!=-1)){
			//check if the unit if it 
			
			xSelected=x;
			ySelected=y;
			//highlight(x,y);
		}
		else if(xSelected!=-1||ySelected!=-1){
			//check if the x and y unit is not our the move is valid, 
			
			
			
			
			
			xSelected=-1;
			ySelected=-1;
		}
		
		
	}
	
	@Override protected void move() {

	}

	@Override protected void setup() {

	}

	@Override protected void idle() {

	}
	
	
}
