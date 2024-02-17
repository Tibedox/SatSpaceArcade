package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenSettings implements Screen {
    SatSpaceArcade satSpaceArcade;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont font;

    SpaceButton btnPlay;
    SpaceButton btnSettings;
    SpaceButton btnAbout;
    SpaceButton btnBack;

    Texture imgBackGround;

    public ScreenSettings(SatSpaceArcade satSpaceArcade) {
        this.satSpaceArcade = satSpaceArcade;
        batch = satSpaceArcade.batch;
        camera = satSpaceArcade.camera;
        touch = satSpaceArcade.touch;
        font = satSpaceArcade.fontLarge;

        imgBackGround = new Texture("stars1.png");

        btnPlay = new SpaceButton("Name", 200, 1000, font);
        btnSettings = new SpaceButton("Sound ON", 200, 800, font);
        btnAbout = new SpaceButton("Music ON", 200, 600, font);
        btnBack = new SpaceButton("Back", 200, 400, font);
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
                //satSpaceArcade.setScreen(satSpaceArcade.screenGame);
            }
            if(btnSettings.hit(touch.x, touch.y)){
                //satSpaceArcade.setScreen(satSpaceArcade.screenSettings);
            }
            if(btnAbout.hit(touch.x, touch.y)){
                //satSpaceArcade.setScreen(satSpaceArcade.screenGame);
            }
            if(btnBack.hit(touch.x, touch.y)){
                satSpaceArcade.setScreen(satSpaceArcade.screenMenu);
            }
        }

        // события

        // отрисовка
        ScreenUtils.clear(0, 0.3f, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        font.draw(batch, btnPlay.text, btnPlay.x, btnPlay.y);
        font.draw(batch, btnSettings.text, btnSettings.x, btnSettings.y);
        font.draw(batch, btnAbout.text, btnAbout.x, btnAbout.y);
        font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
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
        imgBackGround.dispose();
    }
}
