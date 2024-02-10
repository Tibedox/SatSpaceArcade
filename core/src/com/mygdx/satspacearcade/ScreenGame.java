package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class ScreenGame implements Screen {
    SatSpaceArcade satSpaceArcade;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont font;

    boolean isGyroscopeAvailable;
    boolean isAccelerometerAvailable;

    Texture imgBackGround;
    Texture imgShipsAtlas;
    TextureRegion[] imgShip = new TextureRegion[12];

    SpaceButton btnBack;
    Stars[] stars = new Stars[2];
    Ship ship;
    String s="*";

    public ScreenGame(SatSpaceArcade satSpaceArcade) {
        this.satSpaceArcade = satSpaceArcade;
        batch = satSpaceArcade.batch;
        camera = satSpaceArcade.camera;
        touch = satSpaceArcade.touch;
        font = satSpaceArcade.font;

        // проверяем, включены ли датчики гироскопа и акселерометра
        isGyroscopeAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
        isAccelerometerAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        imgBackGround = new Texture("stars0.png");
        imgShipsAtlas = new Texture("ships_atlas.png");

        btnBack = new SpaceButton("x", SCR_WIDTH-50, SCR_HEIGHT+50, font);

        for (int i = 0; i < imgShip.length; i++) {
            if(i<7) {
                imgShip[i] = new TextureRegion(imgShipsAtlas, i * 400, 0, 400, 400);
            } else {
                imgShip[i] = new TextureRegion(imgShipsAtlas, (12-i) * 400, 0, 400, 400);
            }
        }

        stars[0] = new Stars(0);
        stars[1] = new Stars(SCR_HEIGHT);
        ship = new Ship();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.isTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if(btnBack.hit(touch.x, touch.y)){
                satSpaceArcade.setScreen(satSpaceArcade.screenMenu);
            }

            ship.touch(touch.x);
        }
        /*if (isAccelerometerAvailable){
            ship.vx = -Gdx.input.getAccelerometerX()*10;
            s = "x: "+ Gdx.input.getAccelerometerX()+"\n";
            s += "y: "+ Gdx.input.getAccelerometerY()+"\n";
            s += "z: "+ Gdx.input.getAccelerometerZ()+"\n";
        }*/
        if (isGyroscopeAvailable){
            ship.vx = -Gdx.input.getGyroscopeX()*10;
            s = "x: "+ Gdx.input.getGyroscopeX()+"\n";
            s += "y: "+ Gdx.input.getGyroscopeY()+"\n";
            s += "z: "+ Gdx.input.getGyroscopeZ()+"\n";
        }

        // события
        for (Stars s: stars) s.move();
        ship.move();

        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Stars s: stars) {
            batch.draw(imgBackGround, s.x, s.y, s.width, s.height);
        }
        font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
        font.draw(batch, s, 100, 1200);
        batch.draw(imgShip[ship.phase], ship.getX(), ship.getY(), ship.width, ship.height);
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
