package com.theBombSquad.stratego.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.theBombSquad.stratego.StrategoConstants;
import static com.theBombSquad.stratego.StrategoConstants.*;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
public class DefeatedUnitRenderer extends Renderer {
	
	/** Textures of Units that have been defeated, in the order they will be drawn from top to bottom */
	private TextureRegion[] defeatedUnits;
	
	@Override
	public void init() {
		defeatedUnits = new TextureRegion[11];
		for(int c=0; c<defeatedUnits.length; c++){
			defeatedUnits[c] = super.renderData.getAtlas().findRegion("tileWater");
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		float size = POINT_TILE_SIZE*getScale();
		float yStart = 1.5f*POINT_TILE_SIZE;
		for(int cp=0; cp<2; cp++){
			float x = ((ASSUMED_WINDOW_WIDTH-(POINT_TILE_SIZE*1.5f))*cp + POINT_TILE_SIZE*0.25f)*getScale();
			for(int cu=0; cu<defeatedUnits.length; cu++){
				batch.draw(defeatedUnits[cu], x, (((float)ASSUMED_WINDOW_HEIGHT-yStart)-(cu*POINT_TILE_SIZE))*getScale(), size, size);
			}
		}
	}
	
}
