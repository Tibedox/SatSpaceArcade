package com.mygdx.satspacearcade;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class SatSpaceArcade extends Game {
	public static final float SCR_WIDTH = 900, SCR_HEIGHT = 1600;

	SpriteBatch batch;
	OrthographicCamera camera;
	Vector3 touch;
	BitmapFont font;

	ScreenMenu screenMenu;
	ScreenSettings screenSettings;
	ScreenGame screenGame;
	ScreenAbout screenAbout;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();
		font = new BitmapFont(Gdx.files.internal("crystalfont.fnt"));

		screenMenu = new ScreenMenu(this);
		screenSettings = new ScreenSettings(this);
		screenGame = new ScreenGame(this);
		screenAbout = new ScreenAbout(this);
		setScreen(screenMenu);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
