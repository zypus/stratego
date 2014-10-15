package com.theBombSquad.stratego.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.theBombSquad.stratego.StrategoConstants;
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
		renderPlayersTurn(batch);
		renderGameOver(batch);
		renderTurnInfo(batch);
	}

	private void renderPlayersTurn(SpriteBatch batch){
		float x = 0;
		float y = 0;
		font.setScale(0.8f);
		int currentPlayer = game.getCurrentPlayer()==StrategoConstants.PlayerID.PLAYER_1?0:1;
		font.draw(batch, "Player "+(currentPlayer+1), x, StrategoConstants.ASSUMED_WINDOW_HEIGHT*getScale()+y-font.getCapHeight()*1.5f*0.25f);
	}

	private void renderGameOver(SpriteBatch batch){
		if(game.isGameOver()){
			font.setScale(1.5f);
			int currentPlayer = game.getWinner().getGameView().getPlayerID()==StrategoConstants.PlayerID.PLAYER_1?0:1;
			font.draw(batch, "Player "+(currentPlayer+1)+", You Are Winner", StrategoConstants.ASSUMED_WINDOW_WIDTH*getScale()/2/10, StrategoConstants.ASSUMED_WINDOW_HEIGHT*getScale()/2);
		}
	}

	private void renderTurnInfo(SpriteBatch batch){
		font.setScale(0.5f);
		int currentPlayer = game.getCurrentPlayer()==StrategoConstants.PlayerID.PLAYER_1?0:1;
		if(game.getMoves().size()>=1){
			font.draw(batch, game.getMoves().get(game.getMoves().size()-1).toString(), StrategoConstants.ASSUMED_WINDOW_WIDTH*getScale()/5, StrategoConstants.ASSUMED_WINDOW_HEIGHT*getScale()-font.getCapHeight()*1.5f*0.25f-StrategoConstants.POINT_TILE_SIZE*0.1f);
		}
		if(game.getMoves().size()>=2){
			font.draw(batch, game.getMoves().get(game.getMoves().size()-2).toString(), StrategoConstants.ASSUMED_WINDOW_WIDTH*getScale()/5, StrategoConstants.ASSUMED_WINDOW_HEIGHT*getScale()-font.getCapHeight()*1.5f*0.25f-font.getCapHeight()*1.2f*2-StrategoConstants.POINT_TILE_SIZE*0.1f);
		}

	}

}
