package com.theBombSquad.stratego.rendering;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.gameMechanics.Game;

import static com.theBombSquad.stratego.StrategoConstants.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class DefeatedUnitRenderer extends Renderer {

	/** Reference to the font in Render Data */
	private BitmapFont font;

	/** Textures of Units that have been defeated, in the order they will be drawn from top to bottom */
	private TextureRegion[][] defeatedUnits;
	/** Textures of Units's backs that have been defeated, in the order they will be drawn from top to bottom */
	private TextureRegion[] defeatedBacks;

	/** Reference to Game */
	private Game game;

	public DefeatedUnitRenderer(Game game){
		this.game = game;
	}

	@Override
	public void init() {
		initUnitImages();
		this.font = super.renderData.getFont();
	}

	/** Initializes the Texture Regions that represent the Units */
	private void initUnitImages(){
		defeatedUnits = new TextureRegion[2][11];
		Array<AtlasRegion> units = super.renderData.getAtlas().findRegions("unit");
		Array<AtlasRegion> units2 = super.renderData.getAtlas().findRegions("unit2");
		for(int c=0; c<defeatedUnits[0].length; c++){
			defeatedUnits[0][c] = units.get(c+1);
			defeatedUnits[1][c] = units2.get(c+1);
		}
		defeatedBacks = new TextureRegion[2];
		Array<AtlasRegion> backs = super.renderData.getAtlas().findRegions("back");
		for(int c=0; c<defeatedBacks.length; c++){
			defeatedBacks[c] = backs.get(c);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (!game.isReseted()) {
			float size = POINT_TILE_SIZE * getScale();
			float yStart = 1.5f * POINT_TILE_SIZE;
			font.setScale(getScale());
			drawUnits(batch, size, yStart);
		}
	}

	/** Renders Unit Images */
	private void drawUnits(SpriteBatch batch, float size, float yStart){
		font.setScale(0.8f);
		for(int cp=0; cp<2; cp++){
			float x = ((ASSUMED_WINDOW_WIDTH-(POINT_TILE_SIZE*1.5f))*cp + POINT_TILE_SIZE*0.25f)*getScale();
			for(int cu=0; cu<defeatedUnits[0].length; cu++){
				float realX = x;
				float realY = (((float)ASSUMED_WINDOW_HEIGHT-yStart)-(cu*POINT_TILE_SIZE))*getScale();
				batch.draw(defeatedBacks[cp], realX, realY, size, size);
				batch.draw(defeatedUnits[cp][cu], realX, realY, size, size);
				font.draw(batch, ""+calcDead(cu, cp), realX+POINT_TILE_SIZE*getScale()*0.7f, realY+font.getCapHeight()*1.5f);
			}
		}
	}

	/** Returns the amount of Units of given type and player that have already died */
	private int calcDead(int unit, int player){
		PlayerID playerId = (player==0)?StrategoConstants.PlayerID.PLAYER_1:StrategoConstants.PlayerID.PLAYER_2;
		return game.getNumberOfDefeatedUnits(unit+1, playerId);
	}

}
