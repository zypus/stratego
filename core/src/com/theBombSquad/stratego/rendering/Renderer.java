package com.theBombSquad.stratego.rendering;

import lombok.AllArgsConstructor;
import lombok.Setter;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Abstract Class used for all Renderers, do not set the atlas outside of Layer Renderer as
 * it will load & inject the atlas into each renderer including itself
 *
 * @author Fabian Fränz <f.fraenz@t-online.de>
 * @author Flo
 */
public abstract class Renderer {
	
	/** Holds reference to all information relevant for rendering */
	protected RenderData renderData;
	
	/** Adds the Render Data to this Object */
	public void injectRenderData(RenderData renderData){
		this.renderData = renderData;
	}
	
	public abstract void init();
	
	public abstract void render();
	
	protected float getScale(){
		return renderData.getScale();
	}

}
