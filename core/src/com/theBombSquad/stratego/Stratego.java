package com.theBombSquad.stratego;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.rendering.AtlasPacker;
import com.theBombSquad.stratego.rendering.BoardRenderer;
import com.theBombSquad.stratego.rendering.LayerRenderer;
import com.theBombSquad.stratego.rendering.RenderData;
import com.theBombSquad.stratego.rendering.Renderer;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */

public class Stratego extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	private Renderer mainRenderer;
	
	private float scale;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		computeScale();
		testRendering();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(12f/255f, 12f/255f, 12f/255f, 5f/255f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		mainRenderer.render(batch);
		batch.end();
	}
	
	private void computeScale(){
		this.scale = (float)Gdx.graphics.getHeight()/(float)StrategoConstants.ASSUMED_WINDOW_HEIGHT;
	}
	
	private void testRendering(){
		ArrayList<Renderer> list = new ArrayList<Renderer>();
		AtlasPacker.pack();
		GameView view = new GameView(new Game(null, null), StrategoConstants.PlayerID.PLAYER_1);
		RenderData renderData = new RenderData(scale, new TextureAtlas(Gdx.files.internal("atlas/atlas.atlas")));
		list.add(new BoardRenderer(view));
		mainRenderer = new LayerRenderer(list, renderData);
	}
	
}
