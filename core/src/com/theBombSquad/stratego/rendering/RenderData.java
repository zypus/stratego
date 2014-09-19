package com.theBombSquad.stratego.rendering;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/** 
 * Container Object that contains both the Texture Atlas used for retrieving images as well as the scale to which to draw all images
 * @author Flo
 *
 */
@Data
public class RenderData {
	
	/** Construct New */
	public RenderData(float scale, TextureAtlas atlas){
		this.scale = scale;
		this.atlas = atlas;
		this.font = new BitmapFont(Gdx.files.internal("atlas/font.fnt"));
	}
	
	/** Scale to which everything will be drawn */
	private float scale;
	
	/** Texture Atlas containing all images */
	private TextureAtlas atlas;
	
	/** The font used for rendering all text */
	private BitmapFont font;
	
}
