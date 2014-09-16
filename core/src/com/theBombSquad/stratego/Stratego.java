package com.theBombSquad.stratego;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
	
	Renderer mainRenderer;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		testRendering();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		mainRenderer.render();
		batch.end();
	}
	
	
	private void testRendering(){
		ArrayList<Renderer> list = new ArrayList<Renderer>();
		list.add(new BoardRenderer());
		RenderData renderData = new RenderData(1f, new TextureAtlas(Gdx.files.internal("atlas/atlas.atlas")));
		mainRenderer = new LayerRenderer(list, renderData);
	}
	
}
