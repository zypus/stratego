package com.theBombSquad.stratego;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.player.Player;
import com.theBombSquad.stratego.player.ai.players.random.RandomAI;
import com.theBombSquad.stratego.player.humanoid.HumanPlayer;
import com.theBombSquad.stratego.rendering.AtlasPacker;
import com.theBombSquad.stratego.rendering.BoardRenderer;
import com.theBombSquad.stratego.rendering.DefeatedUnitRenderer;
import com.theBombSquad.stratego.rendering.LayerRenderer;
import com.theBombSquad.stratego.rendering.RenderData;
import com.theBombSquad.stratego.rendering.Renderer;

import static com.theBombSquad.stratego.StrategoConstants.ASSUMED_WINDOW_WIDTH;

/**
 * Entry point for the stratego game. Setups everything and establishes remote connections if necessary.
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
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
	private Game game;
	
	private SpriteBatch batch;
	
	private Renderer layerRenderer;

	@Override
	public void create () {
		// TODO setup everything
		AtlasPacker.pack();
		windowScale = (float)Gdx.graphics.getWidth() / (float)ASSUMED_WINDOW_WIDTH;
		setupGame();
		this.batch = new SpriteBatch();
		// TODO start the setup phase of the game
		startGame();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		layerRenderer.render(batch);
		batch.end();
	}

	private void setupMainMenu() {
		// TODO create and show the main menu
		// TODO listen for main menu completion
	}

	private void setupGame() {
		// TODO create the game instance
		game = new Game();
		// creates the two game views, one for each player perspective
		GameView playerOneView = new GameView(game, StrategoConstants.PlayerID.PLAYER_1);
		GameView playerTwoView = new GameView(game, StrategoConstants.PlayerID.PLAYER_2);
		// create some observer view
		GameView observerView = new GameView(game, StrategoConstants.PlayerID.NEMO);

		// TODO create the players or get the players?
		// for now instantiate two random players
		Player player1 = new RandomAI(playerOneView);
		Player player2 = new RandomAI(playerTwoView);

		// tell the game about the players
		game.setPlayer1(new HumanPlayer(playerOneView, windowScale));
		game.setPlayer2(player2);

		// TODO setup renderers
		setupRenderer(playerOneView, playerTwoView, observerView);
	}

	private void setupRenderer(GameView gameView1, GameView gameView, GameView observerView) {
		Renderer board = new BoardRenderer(observerView);
		Renderer death = new DefeatedUnitRenderer();
		ArrayList<Renderer> rendererList = new ArrayList<Renderer>();
		rendererList.add(board);
		rendererList.add(death);
		this.layerRenderer = new LayerRenderer(rendererList, new RenderData(windowScale, new TextureAtlas(Gdx.files.internal("atlas/atlas.atlas"))));
	}

	private void startGame() {
		game.startSetupPhase();
		
	}

	private void listenForRemoteGameCreation() {
		// TODO wait for an remote game creation on a yet to be defined socket
		// TODO perform the necessary steps to connect and keep the game in sync
	}
}
