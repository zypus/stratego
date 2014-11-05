package com.theBombSquad.stratego.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.theBombSquad.stratego.gameMechanics.Game;
import com.theBombSquad.stratego.gameMechanics.board.Encounter;
import com.theBombSquad.stratego.gameMechanics.board.Move;
import com.theBombSquad.stratego.gameMechanics.board.Unit;

import static com.theBombSquad.stratego.StrategoConstants.*;
import static com.theBombSquad.stratego.gameMechanics.Game.*;

public class InformationRenderer extends Renderer{

	/** Reference to the font in Render Data */
	private BitmapFont font;

	private Game game;
	private TextureAtlas.AtlasRegion attack;
	private TextureRegion[][] rUnits;
	private TextureRegion[] unitBacks;
	private TextureAtlas.AtlasRegion white;
	private TextureAtlas.AtlasRegion black;
	private TextureAtlas.AtlasRegion moveArrow;
	private float scaler = 0.8f;
	private TextureAtlas.AtlasRegion killed;

	public InformationRenderer(Game game){
		super();
		this.game = game;
	}

	@Override
	public void init() {
		this.font = super.renderData.getFont();
		font.setColor(Color.WHITE);
		attack = renderData.getAtlas().findRegion("attack");
		moveArrow = renderData.getAtlas().findRegion("move_arrow");
		killed = renderData.getAtlas().findRegion("killed");
		white = renderData.getAtlas().findRegion("tileWhite");
		black = renderData.getAtlas().findRegion("tileBlack");
		rUnits = new TextureRegion[2][12];
		Array<TextureAtlas.AtlasRegion> units = super.renderData.getAtlas().findRegions("unit");
		Array<TextureAtlas.AtlasRegion> units2 = super.renderData.getAtlas().findRegions("unit2");
		for (int c = 0; c < rUnits[0].length; c++) {
			rUnits[0][c] = units.get(c);
			rUnits[1][c] = units2.get(c);
		}
		unitBacks = new TextureRegion[2];
		Array<TextureAtlas.AtlasRegion> backs = super.renderData.getAtlas().findRegions("back");
		for (int c = 0; c < unitBacks.length; c++) {
			unitBacks[c] = backs.get(c);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (!game.isReseted()) {
//			renderPlayersTurn(batch);
			renderGameOver(batch);
			renderTurnInfo(batch);
			if (game.isFinishedSetup()) {
				drawPly(batch);
			}
//			drawBlindIndication(batch);
		}
	}

	private void renderPlayersTurn(SpriteBatch batch){
		float x = 0;
		float y = 0;
		font.setScale(0.8f);
		int currentPlayer = game.getCurrentPlayer()== PlayerID.PLAYER_1?0:1;
		font.draw(batch, "Player "+(currentPlayer+1), x, ASSUMED_WINDOW_HEIGHT*getScale()+y-font.getCapHeight()*1.5f*0.25f);
	}

	private void renderGameOver(SpriteBatch batch){
		if(game.isGameOver()){
			font.setScale(3f);
			PlayerID winner = game.getWinner().getGameView().getPlayerID();
			PlayerID current = game.getActiveGameView().getPlayerID();
			String string = (winner==current) ? "VICTORY" : "DEFEAT";
			if (current.equals(PlayerID.NEMO)) {
				string = "FINISHED";
			}
			BitmapFont.TextBounds bounds = font.getBounds(string);
			font.draw(batch, string, ASSUMED_WINDOW_WIDTH*getScale()/2-bounds.width/2, ASSUMED_WINDOW_HEIGHT*getScale()/2+bounds.height/2);
		}
	}

	private void renderTurnInfo(SpriteBatch batch){
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		float inverseScaler = 1 - scaler;
		GameView gameView = game.getActiveGameView();
		if(gameView.getMoves().size()>=1){
			renderMove(batch, gameView.getMoves().get(gameView.getMoves().size() - 1), (int)(gridX+size + size * inverseScaler / 2), (int)(gridY+GRID_HEIGHT*size+size* inverseScaler/2));
		}
		if(gameView.getMoves().size()>=2){
			renderMove(batch,
					   gameView.getMoves().get(gameView.getMoves().size() - 2),
					   (int) (gridX+ GRID_WIDTH/2*size + size + size * inverseScaler / 2),
					   (int) (gridY + GRID_HEIGHT * size + size * inverseScaler / 2));
		}

	}

	private void renderMove(SpriteBatch batch, Move move, int x, int y) {
		font.setScale(1f);
		float size = POINT_TILE_SIZE * getScale() * scaler;
		float tileSize = POINT_TILE_SIZE * getScale();
		PlayerID currentID = game.getCurrentPlayer();
		Unit movedUnit = move.getMovedUnit();
		int unitRank = movedUnit.getType().getRank();
		int player = (movedUnit.getOwner().equals(PlayerID.PLAYER_1) ? 0 : 1);
		GameView gameView = game.getActiveGameView();
		TextureRegion movedTile = (!game.isBlind() && (currentID == movedUnit.getOwner() || (movedUnit.getRevealedInTurn() != UNREVEALED && gameView.getCurrentTurn() >= movedUnit.getRevealedInTurn()))) ? rUnits[player][unitRank] : unitBacks[player];
		Color tint = new Color(1f, 1f, 1f, 1f);
		if (move.hasEncounter()) {
			Encounter encounter = move.getEncounter();
			Unit attackedUnit = encounter.getDefendingUnit();
			int unitRank2 = attackedUnit.getType().getRank();
			int player2 = (attackedUnit.getOwner().equals(PlayerID.PLAYER_1) ? 0 : 1);
			TextureRegion
					attackedTile =
					( !game.isBlind() && (currentID == attackedUnit.getOwner() || (attackedUnit.getRevealedInTurn() != UNREVEALED && gameView.getCurrentTurn() >= attackedUnit.getRevealedInTurn()))) ?
					rUnits[player2][unitRank2] :
					unitBacks[player2];
			//moved unit
			batch.draw(movedTile,
					   x,
					   y,
					   size,
					   size);
			// move arrow
			batch.draw(attack,
					   x + tileSize,
					   y,
					   size,
					   size);
			//free tile
			batch.draw(attackedTile,
					   x + 2 * tileSize,
					   y,
					   size,
					   size);
			String moveText1 = move.moveFromRepresentaion();
			BitmapFont.TextBounds bounds1 = font.getBounds(moveText1);
			String moveText2 = move.moveToRepresentation();
			BitmapFont.TextBounds bounds2 = font.getBounds(moveText2);
			font.setColor(tint);
			batch.setColor(new Color(1,1,1,0.75f));
			for (Unit defeated : encounter.getDefeatedUnits()) {
				if (defeated == movedUnit) {
					batch.draw(killed,
							   x,
							   y,
							   size,
							   size);
				}
				if (defeated == attackedUnit) {
					batch.draw(killed,
							   x + 2 * tileSize,
							   y,
							   size,
							   size);
				}
			}
			font.draw(batch, moveText1,
					  x + size - bounds1.width - 0.1f * size,
					  y + bounds1.height + 0.1f * size);
			font.draw(batch, moveText2,
					  x + 2 * tileSize + size - bounds2.width - 0.1f * size,
					  y + bounds1.height + 0.1f * size);
			batch.setColor(Color.WHITE);
		} else {
			TextureRegion tile = ((move.getToX()%2 == 0 && move.getToY()%2 != 0) || (move.getToX() % 2 != 0 && move.getToY() % 2 == 0)) ? black : white;
			//moved unit
			batch.draw(movedTile,
					   x,
					   y,
					   size,
					   size);
			// move arrow
			batch.draw(moveArrow,
					   x + tileSize,
					   y,
					   size,
					   size);
			//free tile
			batch.draw(tile,
					   x+ 2*tileSize,
					   y,
					   size,
					   size);
			String moveText1 = move.moveFromRepresentaion();
			BitmapFont.TextBounds bounds1 = font.getBounds(moveText1);
			String moveText2 = move.moveToRepresentation();
			BitmapFont.TextBounds bounds2 = font.getBounds(moveText2);
			font.setColor(tint);
			batch.setColor(tint);
			font.draw(batch, moveText1,
					  x+size-bounds1.width - 0.1f * size,
					  y+bounds1.height + 0.1f * size);
			font.draw(batch, moveText2,
					  x + 2*tileSize + size - bounds2.width - 0.1f*size,
					  y + bounds1.height + 0.1f*size);
			batch.setColor(Color.WHITE);
		}
	}

	private void drawPly(SpriteBatch batch) {
		font.setScale(1f);
		font.setColor(Color.WHITE);
		float gridX = GRID_POSITION_X * getScale();
		float gridY = GRID_POSITION_Y * getScale();
		float size = POINT_TILE_SIZE * getScale();
		GameView gameView = game.getActiveGameView();
		int ply = gameView.getCurrentTurn();
		String plyText = ""+ply;
		BitmapFont.TextBounds bounds = font.getBounds(plyText);
		font.draw(batch, plyText,
				  gridX + GRID_WIDTH/2*size - bounds.width/2,
				  gridY + GRID_HEIGHT*size + 0.5f*size + bounds.height/2);
	}

	private void drawBlindIndication(SpriteBatch batch) {
		if (game.isBlind()) {
			font.setScale(2f);
			font.setColor(Color.WHITE);
			float gridX = GRID_POSITION_X * getScale();
			float gridY = GRID_POSITION_Y * getScale();
			float size = POINT_TILE_SIZE * getScale();
			String plyText = "Press space";
			BitmapFont.TextBounds bounds = font.getBounds(plyText);
			font.draw(batch, plyText,
					  gridX + GRID_WIDTH / 2 * size - bounds.width / 2,
					  gridY + GRID_HEIGHT / 2 * size + bounds.height / 2);
		}
	}

}
