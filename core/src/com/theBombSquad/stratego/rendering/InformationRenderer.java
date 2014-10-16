package com.theBombSquad.stratego.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.theBombSquad.stratego.gameMechanics.Game;

import static com.theBombSquad.stratego.StrategoConstants.*;

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
		if (!game.isReseted()) {
			renderPlayersTurn(batch);
			renderGameOver(batch);
			renderTurnInfo(batch);
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
			BitmapFont.TextBounds bounds = font.getBounds(string);
			font.draw(batch, string, ASSUMED_WINDOW_WIDTH*getScale()/2-bounds.width/2, ASSUMED_WINDOW_HEIGHT*getScale()/2+bounds.height/2);
		}
	}

	private void renderTurnInfo(SpriteBatch batch){
		font.setScale(0.5f);
		int currentPlayer = game.getCurrentPlayer()== PlayerID.PLAYER_1?0:1;
		if(game.getMoves().size()>=1){
			font.draw(batch, game.getMoves().get(game.getMoves().size()-1).toString(), ASSUMED_WINDOW_WIDTH*getScale()/5, ASSUMED_WINDOW_HEIGHT*getScale()-font.getCapHeight()*1.5f*0.25f-
																														  POINT_TILE_SIZE*0.1f);
		}
		if(game.getMoves().size()>=2){
			font.draw(batch, game.getMoves().get(game.getMoves().size()-2).toString(), ASSUMED_WINDOW_WIDTH*getScale()/5, ASSUMED_WINDOW_HEIGHT*getScale()-font.getCapHeight()*1.5f*0.25f-font.getCapHeight()*1.2f*2-
																														  POINT_TILE_SIZE*0.1f);
		}

	}

}
