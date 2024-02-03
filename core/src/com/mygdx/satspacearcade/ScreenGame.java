package com.mygdx.satspacearcade;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenGame implements Screen {
    SatSpaceArcade satSpaceArcade;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;

    Texture img;

    public ScreenGame(SatSpaceArcade satSpaceArcade) {
        this.satSpaceArcade = satSpaceArcade;
        batch = satSpaceArcade.batch;
        camera = satSpaceArcade.camera;
        touch = satSpaceArcade.touch;

        img = new Texture("badlogic.jpg");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.5f, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(img, 0, 0);
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
