package com.theBombSquad.stratego.gameMechanics.board;

import java.awt.*;
import java.io.Serializable;

import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.board.Unit.UnitType;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class GameBoard implements Serializable {

	protected Unit[][] board; // board[y][x]

	protected GameBoard() {
	}

	public int getAliveUnits(UnitType type, PlayerID id)
	{
		int counter=0;
		for(int i =0; i<getWidth(); i++)
		{
			for(int j =0; j<getHeight(); j++)
			{
				if(id==board[j][i].getOwner() && type==board[j][i].getType())
				{
					counter++;
				}
			}
		}
		return counter;
	}

	public GameBoard(int width, int height) {
		board = new Unit[height][width];
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				board[y][x] = Unit.AIR;
			}
		}
	}

	public GameBoard(int width, int height, Rectangle ... lakes) {
		board = new Unit[height][width];
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				board[y][x] = Unit.AIR;
			}
		}
		for (Rectangle lake : lakes) {
			for (int y = lake.y; y < lake.y + lake.height; y++) {
				for (int x = lake.x; x < lake.x+lake.width; x++) {
					board[y][x] = Unit.LAKE;
				}
			}
		}
	}

	public Unit getUnit(int x, int y) {
		return board[y][x];
	}

	public void setUnit(int x, int y, Unit unit) {
		board[y][x] = unit;
	}

	public int getWidth() {
		return board[0].length;
	}

	public int getHeight() {
		return board.length;
	}

	public GameBoard duplicate() {
		GameBoard clonedBoard = new GameBoard(getWidth(), getHeight());
		for (int y = 0; y < getWidth(); y++) {
			for (int x = 0; x < getHeight(); x++) {
				clonedBoard.setUnit(x, y, getUnit(x,y));
			}
		}
		return clonedBoard;
	}

	/** Returns Whether X & Y are within borders */
	public boolean isInBounds(int x, int y){
		return x>=0 && x<this.getWidth() && y>=0 && y<this.getHeight();
	}

}
