package com.theBombSquad.stratego.rendering;

import lombok.AllArgsConstructor;

import java.util.ArrayList;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
@AllArgsConstructor
public class LayerRenderer implements Renderer {

	private ArrayList<Renderer> layers;

	@Override
	public void render() {
		for (Renderer layer : layers) {
			layer.render();
		}
	}
}
