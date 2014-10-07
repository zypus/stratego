package com.theBombSquad.stratego.player.humanoid;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.theBombSquad.stratego.StrategoConstants;
import lombok.RequiredArgsConstructor;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @author Flo
 */
@RequiredArgsConstructor
public class PlayerBoardInput extends InputAdapter {

	private final HumanPlayer player;
	private final double scale;

	@Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(screenX>StrategoConstants.GRID_POSITION_X*scale && screenY>StrategoConstants.GRID_POSITION_Y*scale && screenX<=((float)StrategoConstants.POINT_TILE_SIZE)*((float)StrategoConstants.GRID_WIDTH)*scale+StrategoConstants.GRID_POSITION_X*scale && screenY<=(StrategoConstants.GRID_POSITION_Y*scale)+((float)StrategoConstants.POINT_TILE_SIZE)*((float)StrategoConstants.GRID_HEIGHT*scale)){
			int x = (int)((((float)(screenX - StrategoConstants.GRID_POSITION_X*scale))/(((float)StrategoConstants.POINT_TILE_SIZE)*scale)));
			int y = (int)((((float)(screenY - StrategoConstants.GRID_POSITION_Y*scale))/(((float)StrategoConstants.POINT_TILE_SIZE)*scale)));
			if(player.getSetUpPhase()){
				player.receiveSetUpInput(x,y);
			}else{
				player.receiveInput(x, y);
			}
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override public boolean mouseMoved(int screenX, int screenY) {
		return super.mouseMoved(screenX, screenY);
	}

	@Override public boolean keyUp(int keycode) {
		//TODO: Remove When Improved
		//Debug Setup Submit
		if(keycode==Input.Keys.ENTER){
			player.submitSetUp();
		}
		//Debug Setup Reset
		else if(keycode==Input.Keys.ESCAPE){
			player.resetSetup();
		}
		//Debug Setup Reset
		else if(keycode==Input.Keys.R){
			player.randomSetup();
		}
		return super.keyUp(keycode);
	}
}
