package com.theBombSquad.stratego;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;

import static com.theBombSquad.stratego.StrategoConstants.ASSUMED_WINDOW_WIDTH;

/**
 * Entry point for the stratego game. Setups everything and establishes remote connections if necessary.
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 *
 * @version 1.0
 * @created 10.09.14
 *
 * @date 16.09.14
 *
 * @log
 * - Skeleton	10.09.14
 * - TODOs 16.09.14
 */

public class Stratego extends ApplicationAdapter {

	private float windowScale;

	@Override
	public void create () {
		// TODO setup everything
		windowScale = (float)Gdx.graphics.getWidth() / (float)ASSUMED_WINDOW_WIDTH;
	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// TODO call the appropriate renderer
	}

	private void setupMainMenu() {
		// TODO create and show the main menu
		// TODO listen for main menu completion
	}

	private void setupGame() {
		// TODO create the players or get the players?

		// TODO create the game instance
		Game game = new Game();
		// creates the two game views, one for each player perspective
		GameView playerOneView = new GameView(game, StrategoConstants.PlayerID.PLAYER_1);
		GameView playerTwoView = new GameView(game, StrategoConstants.PlayerID.PLAYER_2);
		// TODO setup renderers
		// TODO start the setup phase of the game
	}

	private void setupRenderer(GameView gameView1, GameView gameView2) {
		// TODO setup the render system
	}

	private void listenForRemoteGameCreation() {
		// TODO wait for an remote game creation on a yet to be defined socket
		// TODO perform the necessary steps to connect and keep the game in sync
	}
}
