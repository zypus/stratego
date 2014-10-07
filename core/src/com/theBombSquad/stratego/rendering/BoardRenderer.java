package com.theBombSquad.stratego.rendering;

import java.awt.Rectangle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import com.theBombSquad.stratego.player.Player;

import static com.theBombSquad.stratego.StrategoConstants.*;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class BoardRenderer extends Renderer {
	
	private GameView view;
	
	private TextureRegion white;
	private TextureRegion black;
	private TextureRegion water;
	
	/** Textures of Units that have been defeated, in the order they will be drawn from top to bottom */
	private TextureRegion[] rUnits;
	/** Textures of Units's backs that have been defeated, in the order they will be drawn from top to bottom */
	private TextureRegion[] unitBacks;
	
	
	public BoardRenderer(GameView view){
		this.view = view;
	}
	
	@Override
	public void init() {
		white = renderData.getAtlas().findRegion("tileWhite");
		black = renderData.getAtlas().findRegion("tileBlack");
		water = renderData.getAtlas().findRegion("tileWater");
		initUnitImages();
	}
	
	/** Initializes the Texture Regions that represent the Units */
	private void initUnitImages(){
		rUnits = new TextureRegion[12];
		Array<AtlasRegion> units = super.renderData.getAtlas().findRegions("unit");
		for(int c=0; c<rUnits.length; c++){
			rUnits[c] = units.get(c);
			
		}
		unitBacks = new TextureRegion[2];
		Array<AtlasRegion> backs = super.renderData.getAtlas().findRegions("back");
		for(int c=0; c<unitBacks.length; c++){
			unitBacks[c] = backs.get(c);
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		GameBoard board = view.getCurrentState();
		float gridX = GRID_POSITION_X*getScale();
		float gridY = GRID_POSITION_Y*getScale();
		float size = POINT_TILE_SIZE*getScale();
		//Draw Core Board (Without lakes)
		boolean tileFlag = true;
		for(int cy=0; cy<GRID_HEIGHT; cy++){
			for(int cx=0; cx<GRID_WIDTH; cx++){
				TextureRegion tile = (tileFlag) ? white : black; 
				tileFlag = !tileFlag;
				drawTile(tile, batch, cx, cy, size, gridX, gridY);
			}
			tileFlag = !tileFlag;
		}
		//Draw Lakes
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				if(board.getUnit(cx, cy) == Unit.LAKE){
					drawTile(water, batch, cx, cy, size, gridX, gridY);
				}
			}
		}
		//Draw Units
		for(int cy=0; cy<board.getHeight(); cy++){
			for(int cx=0; cx<board.getWidth(); cx++){
				int unitRank = view.getUnit(cx, cy).getType().getRank();
				if(unitRank != -1){
					drawTile(unitBacks[view.getUnit(cx, cy).getOwner().equals(PlayerID.PLAYER_1)?0:1], batch, cx, cy, size, gridX, gridY);
					if(unitRank != Unit.UNKNOWN.getType().getRank()){
						drawTile(rUnits[unitRank], batch, cx, cy, size, gridX, gridY);
					}
				}
			}
		}
	}
	
	/** Draws specified tile onto grid, starting in upper left corner */
	private void drawTile(TextureRegion tile, SpriteBatch batch, int x, int y, float size, float relGridX, float relGridY) {
		batch.draw(tile, relGridX + x*POINT_TILE_SIZE*getScale(), relGridY + (GRID_HEIGHT-y-1)*POINT_TILE_SIZE*getScale(), size, size);
	}
	
	
}
