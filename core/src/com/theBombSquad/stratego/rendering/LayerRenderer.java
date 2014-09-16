package com.theBombSquad.stratego.rendering;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * TODO Add description
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
public class LayerRenderer extends Renderer {

	private ArrayList<Renderer> layers;
	
	public LayerRenderer(ArrayList<Renderer> layers, RenderData renderData){
		this.layers = layers;
		injectRenderData(renderData);
		for (Renderer layer : layers) {
			layer.injectRenderData(renderData);
			layer.init();
		}
	}
	
	public void render(SpriteBatch batch) {
		for (Renderer layer : layers) {
			layer.render(batch);
		}
	}

	@Override
	public void init() {}
	
}
