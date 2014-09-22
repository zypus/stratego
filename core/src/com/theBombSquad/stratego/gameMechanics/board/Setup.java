package com.theBombSquad.stratego.gameMechanics.board;

import java.awt.*;
import java.io.Serializable;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
public class Setup extends GameBoard implements Serializable {

	public Setup(int width, int height, Rectangle... lakes) {
		super(width, height, lakes);
	}
}
