package com.theBombSquad.stratego;

import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.players.RandomAI;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;

import java.awt.Rectangle;

import static com.theBombSquad.stratego.gameMechanics.Game.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class StrategoConstants {

	public static final int POINT_TILE_SIZE = 32;
	public static final int ASSUMED_WINDOW_WIDTH = POINT_TILE_SIZE * 14;
	public static final int ASSUMED_WINDOW_HEIGHT = POINT_TILE_SIZE * 12;
	public static final int GRID_WIDTH = 10;
	public static final int GRID_HEIGHT = 10;
	public static final int GRID_POSITION_X = (int)(2 * POINT_TILE_SIZE);
	public static final int GRID_POSITION_Y = (int)(1.0 * POINT_TILE_SIZE);

	public static final Rectangle[] DEFAULT_LAKES = new Rectangle[] { new Rectangle(2, 4, 2, 2), new Rectangle(6, 4, 2, 2) };

	public static double scale = 1;

	public static final int UNREVEALED = -42;
	public static final int UNMOVED = 9999999;
	public static final int FIRST_TURN = 0;

	public static final int AI_DELAY = 200;

	public static enum PlayerID {
		PLAYER_1,
		PLAYER_2,
		NEMO;

		public PlayerID getOpponent() {
			if (this == PLAYER_1) {
				return PLAYER_2;
			} else if (this == PLAYER_2) {
				return PLAYER_1;
			} else {
				return null;
			}
		}
	}

	public static enum GameResult {
		WIN,
		DEFEAT,
		DRAW
	}

	public static enum PlayerType {
		HUMAN(HumanPlayer.class),
		RANDOM(RandomAI.class);

		private Class<? extends Player> playerClass;

		private PlayerType(Class<? extends Player> playerClass) {
			this.playerClass = playerClass;
		}

		public Player createPlayer(GameView gameView) {
			Player playerInstance = null;
			try {
				playerInstance = playerClass.getConstructor(GameView.class).newInstance(gameView);
			} catch (Exception e) {
				e.printStackTrace();
				// cannot recover from here
				System.exit(1);
			}
			return playerInstance;
		}

	}

	/** Remote constants */
	public static final String LOCAL_HOST = "127.0.0.1";
	public static final int PORT_PLAYER1 = 9021;			// 21 stands usually for an FTP port TODO find a good port number
	public static final int PORT_PLAYER2 = 9022;
	public static final int SERVE_TIMEOUT = 4000; 	// in milliseconds
	public static final int LISTEN_TIMEOUT = 0;     // in milliseconds
	public static final int RETRY_DELAY = 2000;     // in milliseconds

	public static final String SETUP_PATH = "setups/";

}
