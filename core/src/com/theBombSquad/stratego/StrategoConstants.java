package com.theBombSquad.stratego;

import java.awt.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class StrategoConstants {

	public static final int POINT_TILE_SIZE = 32;
	public static final int ASSUMED_WINDOW_WIDTH = POINT_TILE_SIZE * 13;
	public static final int ASSUMED_WINDOW_HEIGHT = POINT_TILE_SIZE * 12;
	public static final int GRID_WIDTH = 10;
	public static final int GRID_HEIGHT = 10;
	public static final int GRID_POSITION_X = (int)(1.5 * POINT_TILE_SIZE);
	public static final int GRID_POSITION_Y = (int)(1.0 * POINT_TILE_SIZE);

	public static final Rectangle[] DEFAULT_LAKES = new Rectangle[] { new Rectangle(2, 4, 2, 2), new Rectangle(6, 4, 2, 2) };

	public static final int UNREVEALED = -42;
	public static final int FIRST_TURN = 0;

	public static enum PlayerID {
		PLAYER_1,
		PLAYER_2,
		NEMO
	}

}
