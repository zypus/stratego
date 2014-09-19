package com.theBombSquad.stratego.gameMechanics.board;

import java.awt.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class GameBoard {

	private Unit[][] board; // board[y][x]

	public GameBoard(int width, int height, Rectangle ... lakes) {
		board = new Unit[width][height];
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

}
