package com.theBombSquad.stratego.rendering;

import static java.lang.Math.*;

/**
 * TODO Add description
 *
 * @author Fabian Fraenz <f.fraenz@t-online.de>
 * @created $(DATE)
 */
public class StrategoUtil {

	public static float manhattanDistance(float x1, float y1, float x2, float y2) {
		return abs(x1-x2)+abs(y1-y2);
	}
}
