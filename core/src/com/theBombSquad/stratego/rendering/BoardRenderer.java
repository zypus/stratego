package com.theBombSquad.stratego.rendering;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.theBombSquad.stratego.StrategoConstants;
import com.theBombSquad.stratego.StrategoConstants.PlayerID;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.player.Player;

import static com.theBombSquad.stratego.StrategoConstants.*;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
public class BoardRenderer extends Renderer {
	
	private GameView view;
	
	private SpriteBatch batch = null;
	private TextureRegion white = null;
	private TextureRegion black = null;
	private TextureRegion water = null;
	
	public BoardRenderer(GameView view){
		this.view = view;
	}
	
	@Override
	public void init() {
		this.batch = new SpriteBatch();
		white = renderData.getAtlas().findRegion("tileWhite");
		black = renderData.getAtlas().findRegion("tileBlack");
		water = renderData.getAtlas().findRegion("tileWater");
	}
	
	@Override
	public void render() {
		GameBoard board = view.getCurrentState();
		float gridX = GRID_POSITION_X*getScale();
		float gridY = GRID_POSITION_Y*getScale();
		float size = POINT_TILE_SIZE*getScale();
		batch.begin();
		boolean tileFlag = true;
		for(int cy=0; cy<StrategoConstants.GRID_HEIGHT; cy++){
			for(int cx=0; cx<StrategoConstants.GRID_WIDTH; cx++){
				TextureRegion tile = (tileFlag) ? white : black; 
				tileFlag = !tileFlag;
				drawTile(tile, gridX, gridY, size, cy, cx);
			}
			tileFlag = !tileFlag;
		}
		board.getUnit(0, 0);
		batch.end();
	}

	private void drawTile(TextureRegion tile, float gridX, float gridY, float size, int cy, int cx) {
		batch.draw(tile, gridX + cx*POINT_TILE_SIZE*getScale(), gridY + cy*POINT_TILE_SIZE*getScale(), size, size);
	}
	
	
}
