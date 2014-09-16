package com.theBombSquad.stratego.rendering;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.theBombSquad.stratego.player.Player;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
public class BoardRenderer extends Renderer {
	
	private SpriteBatch batch;
	
	private TextureRegion white;
	private TextureRegion black;
	private TextureRegion water;
	
	@Override
	public void init() {
		this.batch = new SpriteBatch();
		white = renderData.getAtlas().findRegion("sign");
		//black = renderData.getAtlas().findRegion("tileBlack");
		//water = renderData.getAtlas().findRegion("tileWater");
	}
	
	@Override public void render() {
		batch.begin();
		
		batch.draw(white, 0, 0);
		batch.end();
	}
	
}
