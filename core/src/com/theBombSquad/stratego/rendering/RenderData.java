package com.theBombSquad.stratego.rendering;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/** 
 * Container Object that contains both the Texture Atlas used for retrieving images as well as the scale to which to draw all images
 * @author Flo
 *
 */
@Data
@AllArgsConstructor
public class RenderData {
	
	/** Scale to which everything will be drawn */
	private float scale;
	
	/** Texture Atlas containing all images */
	private TextureAtlas atlas;
	
}
