package com.theBombSquad.stratego.rendering;

import static com.theBombSquad.stratego.StrategoConstants.POINT_TILE_SIZE;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.theBombSquad.stratego.gameMechanics.Game;

public class InformationRenderer extends Renderer{
	
	/** Reference to the font in Render Data */
	private BitmapFont font;
	
	private Game game;
	
	public InformationRenderer(Game game){
		super();
		this.game = game;
	}

	@Override
	public void init() {
		this.font = super.renderData.getFont();
		font.setColor(Color.WHITE);
	}

	@Override
	public void render(SpriteBatch batch) {
		//font.draw(batch, "Player's Move Here ", 250*getScale(), 250*getScale());
	}

}
