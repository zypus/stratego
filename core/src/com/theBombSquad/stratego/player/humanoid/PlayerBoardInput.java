package com.theBombSquad.stratego.player.humanoid;

import com.badlogic.gdx.InputAdapter;
import com.theBombSquad.stratego.StrategoConstants;

import lombok.RequiredArgsConstructor;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
@RequiredArgsConstructor
public class PlayerBoardInput extends InputAdapter {

	private final HumanPlayer player;

	@Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		double scale = StrategoConstants.scale;
		int x = (int)((screenX - StrategoConstants.GRID_POSITION_X*StrategoConstants.POINT_TILE_SIZE*scale)/StrategoConstants.POINT_TILE_SIZE*scale);
		int y = (int)((screenY - StrategoConstants.GRID_POSITION_Y*StrategoConstants.POINT_TILE_SIZE*scale)/StrategoConstants.POINT_TILE_SIZE*scale);		
		
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override public boolean mouseMoved(int screenX, int screenY) {
		return super.mouseMoved(screenX, screenY);
	}

	@Override public boolean keyUp(int keycode) {
		return super.keyUp(keycode);
	}
}
