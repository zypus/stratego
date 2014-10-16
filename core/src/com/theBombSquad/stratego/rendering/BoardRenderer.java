package com.theBombSquad.stratego.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.theBombSquad.stratego.StrategoConstants.*;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.GameView;
import com.theBombSquad.stratego.gameMechanics.board.GameBoard;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

import static com.theBombSquad.stratego.StrategoConstants.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
public class BoardRenderer extends Renderer {

	private Game game;

	private TextureRegion white;
	private TextureRegion black;
	private TextureRegion water;

	/** Textures of Units */
	private TextureRegion[][] rUnits;
	/** Textures of Units's backs that have been defeated, in the order they will be drawn from top to bottom */
	private TextureRegion[] unitBacks;
	private BitmapFont font;
	private PlayerID playerID;
	private AtlasRegion eye;

	public BoardRenderer(Game game){
		this.game = game;
	}

	@Override
	public void init() {
		font = super.renderData.getFont();
		font.setColor(Color.WHITE);
		white = renderData.getAtlas().findRegion("tileWhite");
		black = renderData.getAtlas().findRegion("tileBlack");
		water = renderData.getAtlas().findRegion("tileWater");
		initUnitImages();
	}

	/** Initializes the Texture Regions that represent the Units */
	private void initUnitImages() {
		eye = super.renderData.getAtlas().findRegion("eye");
		rUnits = new TextureRegion[2][12];
		Array<AtlasRegion> units = super.renderData.getAtlas().findRegions("unit");
		Array<AtlasRegion> units2 = super.renderData.getAtlas().findRegions("unit2");
		for (int c = 0; c < rUnits[0].length; c++) {
			rUnits[0][c] = units.get(c);
			rUnits[1][c] = units2.get(c);
		}
		unitBacks = new TextureRegion[2];
		Array<AtlasRegion> backs = super.renderData.getAtlas().findRegions("back");
		for (int c = 0; c < unitBacks.length; c++) {
			unitBacks[c] = backs.get(c);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (!game.isReseted()) {
			GameView view = game.getActiveGameView();
			GameBoard board = view.getCurrentState();
			float gridX = GRID_POSITION_X * getScale();
			float gridY = GRID_POSITION_Y * getScale();
			float size = POINT_TILE_SIZE * getScale();
			//Draw Core Board (Without lakes)
			boolean tileFlag = true;
			for (int cy = 0; cy < GRID_HEIGHT; cy++) {
				for (int cx = 0; cx < GRID_WIDTH; cx++) {
					TextureRegion tile = (tileFlag) ? white : black;
					tileFlag = !tileFlag;
					drawTile(tile, batch, cx, cy, size, gridX, gridY);
				}
				tileFlag = !tileFlag;
			}
			//Draw Lakes
			for (int cy = 0; cy < board.getHeight(); cy++) {
				for (int cx = 0; cx < board.getWidth(); cx++) {
					if (board.getUnit(cx, cy) == Unit.LAKE) {
						drawTile(water, batch, cx, cy, size, gridX, gridY);
					}
				}
			}
			//Draws Lakes Properly
			for (int cy = 0; cy < board.getHeight(); cy++) {
				for (int cx = 0; cx < board.getWidth(); cx++) {
					if (board.getUnit(cx, cy) == Unit.LAKE && board.getUnit(cx + 1, cy) == Unit.LAKE
						&& board.getUnit(cx, cy + 1) == Unit.LAKE && board.getUnit(cx + 1, cy + 1) == Unit.LAKE) {
						batch.draw(water,
								   gridX + cx * POINT_TILE_SIZE * getScale(),
								   gridY + (GRID_HEIGHT - cy - 1 - 1) * POINT_TILE_SIZE * getScale(),
								   size * 2,
								   size * 2);
					}
				}
			}
			//Draw Units
			for (int cy = 0; cy < board.getHeight(); cy++) {
				for (int cx = 0; cx < board.getWidth(); cx++) {
					Unit unit = view.getUnit(cx, cy);
					int unitRank = unit.getType().getRank();
					if (unitRank != -1) {
						int player = (view.getUnit(cx, cy).getOwner().equals(PlayerID.PLAYER_1) ? 0 : 1);
						if (!unit.isUnknown() && !game.isBlind()) {
							if (!game.isGameOver() && game.isFinishedSetup() && game.getActiveGameView().getPlayerID()==unit.getOwner() &&  game.getActiveGameView().getPlayerID() != game.getCurrentPlayer()) {
								batch.setColor(new Color(0.8f,0.8f,0.8f,1));
							}
							drawTile(rUnits[player][unitRank], batch, cx, cy, size, gridX, gridY);
							if (!game.isGameOver() && unit.getOwner() == game.getActiveGameView().getPlayerID() && unit.getRevealedInTurn() != UNREVEALED
								&& game.getCurrentTurn() >= unit.getRevealedInTurn()) {
								batch.setColor(new Color(1, 1, 1, 0.75f));
								drawTile(eye, batch, cx, cy, size, gridX, gridY);
							}
							batch.setColor(Color.WHITE);
						} else {
							drawTile(unitBacks[player], batch, cx, cy, size, gridX, gridY);
						}
					}
				}
			}
			font.setScale(1);
			font.setColor(new Color(1,1,1,0.8f));
			drawXAxis(batch);
			drawYAxis(batch);
		}
	}

	/** Draws specified tile onto grid, starting in upper left corner */
	private void drawTile(TextureRegion tile, SpriteBatch batch, int x, int y, float size, float relGridX, float relGridY) {
		batch.draw(tile, relGridX + x*POINT_TILE_SIZE*getScale(), relGridY + (GRID_HEIGHT-y-1)*POINT_TILE_SIZE*getScale(), size, size);
	}

	private void drawXAxis(SpriteBatch batch) {
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		PlayerID player;
		if (game.isFinishedSetup()) {
			player = game.getActiveGameView().getPlayerID();
		} else {
			if (!game.isPlayer1FinishedSetup()) {
				player = PlayerID.PLAYER_1;
			} else {
				player = PlayerID.PLAYER_2;
			}
		}

		if (player == PlayerID.PLAYER_1) {
			for (int i = 0; i < Move.xRep.length; i++) {
				String text = Move.xRep[i];
				BitmapFont.TextBounds bounds = font.getBounds(text);
				font.draw(batch, text,
						  gridX + i*size + size/2 - bounds.width/2,
						  gridY - bounds.height/2);
			}
		} else {
			for (int i = Move.xRep.length-1; i >= 0; i--) {
				String text = Move.xRep[i];
				BitmapFont.TextBounds bounds = font.getBounds(text);
				font.draw(batch, text,
						  gridX + (Move.xRep.length - 1-i) * size + size / 2 - bounds.width / 2,
						  gridY - bounds.height / 2);
			}
		}
	}

	private void drawYAxis(SpriteBatch batch) {
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		PlayerID player;
		if (game.isFinishedSetup()) {
			player = game.getActiveGameView().getPlayerID();
		} else {
			if (!game.isPlayer1FinishedSetup()) {
				player = PlayerID.PLAYER_1;
			} else {
				player = PlayerID.PLAYER_2;
			}
		}

		if (player == PlayerID.PLAYER_2) {
			for (int i = 0; i < GRID_HEIGHT; i++) {
				String text = ""+i;
				BitmapFont.TextBounds bounds = font.getBounds(text);
				font.draw(batch, text,
						  gridX + GRID_WIDTH * size + bounds.height/2,
						  gridY + i * size + size / 2 + bounds.height / 2);
			}
		} else {
			for (int i = GRID_HEIGHT- 1; i >= 0; i--) {
				String text = ""+i;
				BitmapFont.TextBounds bounds = font.getBounds(text);
				font.draw(batch, text,
						  gridX - bounds.height,
						  gridY + (GRID_HEIGHT - 1 - i) * size + size / 2 + bounds.height / 2);
			}
		}
	}


}
