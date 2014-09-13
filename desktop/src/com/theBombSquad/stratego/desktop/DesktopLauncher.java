package com.theBombSquad.stratego.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.theBombSquad.stratego.Stratego;
import com.theBombSquad.stratego.StrategoConstants;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		float screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		float scale = screenHeight/StrategoConstants.ASSUMED_WINDOW_HEIGHT*0.8f;
		config.width = (int)(StrategoConstants.ASSUMED_WINDOW_WIDTH*scale);
		config.height = (int)(StrategoConstants.ASSUMED_WINDOW_HEIGHT*scale);
		config.resizable = false;
		new LwjglApplication(new Stratego(), config);
	}
}
