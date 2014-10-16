package com.theBombSquad.stratego.player.humanoid;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import lombok.RequiredArgsConstructor;

import static com.theBombSquad.stratego.StrategoConstants.*;

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
		if(screenX> GRID_POSITION_X*scale && screenY> GRID_POSITION_Y*scale && screenX<=((float) POINT_TILE_SIZE)*((float) GRID_WIDTH)*scale+
																						GRID_POSITION_X*scale && screenY<=(GRID_POSITION_Y*scale)+((float) POINT_TILE_SIZE)*((float) GRID_HEIGHT*scale)){
			int x = (int)((((float)(screenX - GRID_POSITION_X*scale))/(((float) POINT_TILE_SIZE)*scale)));
			int y = (int)((((float)(screenY - GRID_POSITION_Y*scale))/(((float) POINT_TILE_SIZE)*scale)));
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
		if (screenX > GRID_POSITION_X * scale && screenY > GRID_POSITION_Y * scale && screenX <=  ((float) POINT_TILE_SIZE)* ((float) GRID_WIDTH) * scale + GRID_POSITION_X * scale && screenY <= (GRID_POSITION_Y * scale) + ((float) POINT_TILE_SIZE) * ((float) GRID_HEIGHT * scale)) {
			int x = (int) ((((float) (screenX - GRID_POSITION_X * scale)) / (((float) POINT_TILE_SIZE) * scale)));
			int y =(int) ((((float) (screenY - GRID_POSITION_Y * scale)) / (((float) POINT_TILE_SIZE) * scale)));
			player.setxMouseOver(x);
			player.setyMouseOver(y);
		}
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
