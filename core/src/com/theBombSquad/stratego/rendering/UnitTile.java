package com.theBombSquad.stratego.rendering;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.theBombSquad.stratego.gameMechanics.board.Unit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
public class UnitTile {

	private Unit unit;
	private TextureRegion tile;
	private Vector2 position = new Vector2();
	private float scale = 1f;
	private boolean killed = false;

	public static class UnitTileAccessor implements TweenAccessor<UnitTile> {
		public static final int POSITION = 0;
		public static final int SCALE = 1;

		@Override public int getValues(UnitTile unitTile, int i, float[] floats) {
			switch (i) {
				case POSITION:
					floats[0] = unitTile.getPosition().x;
					floats[1] = unitTile.getPosition().y;
					return 2;
				case SCALE:
					floats[0] = unitTile.getScale();
					return 1;
			}
			return 0;
		}

		@Override public void setValues(UnitTile unitTile, int i, float[] floats) {
			switch (i) {
				case POSITION:
					unitTile.setPosition(new Vector2(floats[0], floats[1]));
					break;
				case SCALE:
					unitTile.setScale(floats[0]);
					break;
			}
		}
	}
}

