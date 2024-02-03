package com.mygdx.satspacearcade;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class SatSpaceArcade extends Game {
	public static final float SCR_WIDTH = 900, SCR_HEIGHT = 1600;

	SpriteBatch batch;
	OrthographicCamera camera;
	Vector3 touch;

	ScreenMenu screenMenu;
	ScreenSettings screenSettings;
	ScreenGame screenGame;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();

		screenMenu = new ScreenMenu(this);
		screenSettings = new ScreenSettings(this);
		screenGame = new ScreenGame(this);
		setScreen(screenSettings);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
