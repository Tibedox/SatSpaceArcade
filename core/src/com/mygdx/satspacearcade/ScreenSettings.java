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
    BitmapFont fontLarge, fontSmall;

    SpaceButton btnName;
    SpaceButton btnSound;
    SpaceButton btnMusic;
    SpaceButton btnBack;

    Texture imgBackGround;
    boolean isUseInputKeyboard;
    InputKeyboard keyboard;

    public ScreenSettings(SatSpaceArcade satSpaceArcade) {
        this.satSpaceArcade = satSpaceArcade;
        batch = satSpaceArcade.batch;
        camera = satSpaceArcade.camera;
        touch = satSpaceArcade.touch;
        fontLarge = satSpaceArcade.fontLarge;
        fontSmall = satSpaceArcade.fontSmall;

        imgBackGround = new Texture("stars1.png");

        btnName = new SpaceButton("Name: "+satSpaceArcade.playerName, 100, 1000, fontLarge);
        btnSound = new SpaceButton("Sound ON", 100, 800, fontLarge);
        btnMusic = new SpaceButton("Music ON", 100, 600, fontLarge);
        btnBack = new SpaceButton("Back", 100, 400, fontLarge);
        keyboard = new InputKeyboard(fontSmall, SCR_WIDTH, SCR_HEIGHT/2, 8);
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
            if(isUseInputKeyboard){
                if (keyboard.endOfEdit(touch.x, touch.y)) {
                    satSpaceArcade.playerName = keyboard.getText();
                    btnName.setText("Name: "+satSpaceArcade.playerName);
                    isUseInputKeyboard = false;
                }
            } else {
                if (btnName.hit(touch.x, touch.y)) {
                    isUseInputKeyboard = true;
                }
                if (btnSound.hit(touch.x, touch.y)) {
                    //satSpaceArcade.setScreen(satSpaceArcade.screenSettings);
                }
                if (btnMusic.hit(touch.x, touch.y)) {
                    //satSpaceArcade.setScreen(satSpaceArcade.screenGame);
                }
                if (btnBack.hit(touch.x, touch.y)) {
                    satSpaceArcade.setScreen(satSpaceArcade.screenMenu);
                }
            }
        }

        // события

        // отрисовка
        ScreenUtils.clear(0, 0.3f, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        fontLarge.draw(batch, btnName.text, btnName.x, btnName.y);
        fontLarge.draw(batch, btnSound.text, btnSound.x, btnSound.y);
        fontLarge.draw(batch, btnMusic.text, btnMusic.x, btnMusic.y);
        fontLarge.draw(batch, btnBack.text, btnBack.x, btnBack.y);
        if(isUseInputKeyboard) {
            keyboard.draw(batch);
        }
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
        keyboard.dispose();
    }
}
