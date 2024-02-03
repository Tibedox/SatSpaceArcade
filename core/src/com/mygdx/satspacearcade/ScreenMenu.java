package com.mygdx.satspacearcade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenMenu implements Screen {
    SatSpaceArcade satSpaceArcade;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont font;

    SpaceButton btnPlay;
    SpaceButton btnSettings;
    SpaceButton btnAbout;
    SpaceButton btnExit;

    Texture img;

    public ScreenMenu(SatSpaceArcade satSpaceArcade) {
        this.satSpaceArcade = satSpaceArcade;
        batch = satSpaceArcade.batch;
        camera = satSpaceArcade.camera;
        touch = satSpaceArcade.touch;
        font = satSpaceArcade.font;
        font.getData().setScale(2f);

        img = new Texture("badlogic.jpg");

        btnPlay = new SpaceButton("Play", 200, 1000, font);
        btnSettings = new SpaceButton("Settings", 200, 800, font);
        btnAbout = new SpaceButton("About game", 200, 600, font);
        btnExit = new SpaceButton("Exit", 200, 400, font);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            if(btnPlay.hit(touch.x, touch.y)){
                satSpaceArcade.setScreen(satSpaceArcade.screenGame);
            }
            if(btnSettings.hit(touch.x, touch.y)){
                satSpaceArcade.setScreen(satSpaceArcade.screenSettings);
            }
            if(btnAbout.hit(touch.x, touch.y)){
                satSpaceArcade.setScreen(satSpaceArcade.screenAbout);
            }
            if(btnExit.hit(touch.x, touch.y)){
                Gdx.app.exit();
            }
        }

        // события

        // отрисовка
        ScreenUtils.clear(1, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(img, 0, 0);
        font.draw(batch, btnPlay.text, btnPlay.x, btnPlay.y);
        font.draw(batch, btnSettings.text, btnSettings.x, btnSettings.y);
        font.draw(batch, btnAbout.text, btnAbout.x, btnAbout.y);
        font.draw(batch, btnExit.text, btnExit.x, btnExit.y);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        img.dispose();
    }
}
