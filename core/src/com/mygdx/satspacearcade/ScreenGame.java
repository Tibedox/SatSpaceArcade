package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class ScreenGame implements Screen {
    SatSpaceArcade satSpaceArcade;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont font;

    boolean isGyroscopeAvailable;
    boolean isAccelerometerAvailable;

    Sound sndShot;
    Sound sndExplosion;

    Texture imgBackGround;
    Texture imgShipsAtlas;
    Texture imgFragmentsAtlas;
    TextureRegion[] imgShip = new TextureRegion[12];
    TextureRegion[] imgEnemy = new TextureRegion[12];
    TextureRegion[] imgFragment = new TextureRegion[2];
    Texture imgShot;

    SpaceButton btnBack;
    Stars[] stars = new Stars[2];
    Ship ship;
    Array<Shot> shots = new Array<>();
    long timeShotLastSpawn, timeShotInterval = 700;
    Array<Enemy> enemies = new Array<>();
    long timeEnemyLastSpawn, timeEnemyInterval = 1500;
    Array<Fragment> fragments = new Array<>();
    int nFragments = 50;

    public ScreenGame(SatSpaceArcade satSpaceArcade) {
        this.satSpaceArcade = satSpaceArcade;
        batch = satSpaceArcade.batch;
        camera = satSpaceArcade.camera;
        touch = satSpaceArcade.touch;
        font = satSpaceArcade.font;

        // проверяем, включены ли датчики гироскопа и акселерометра
        isGyroscopeAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
        isAccelerometerAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        sndShot = Gdx.audio.newSound(Gdx.files.internal("blaster.wav"));
        sndExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

        imgBackGround = new Texture("stars0.png");
        imgShipsAtlas = new Texture("ships_atlas3.png");
        imgFragmentsAtlas = new Texture("ships_fragment_atlas.png");
        imgShot = new Texture("shoot_blaster_red.png");

        btnBack = new SpaceButton("x", SCR_WIDTH-80, SCR_HEIGHT, font);

        for (int i = 0; i < imgShip.length; i++) {
            if(i<7) {
                imgShip[i] = new TextureRegion(imgShipsAtlas, i * 400, 0, 400, 400);
                imgEnemy[i] = new TextureRegion(imgShipsAtlas, i * 400, 1600, 400, 400);
            } else {
                imgShip[i] = new TextureRegion(imgShipsAtlas, (12-i) * 400, 0, 400, 400);
                imgEnemy[i] = new TextureRegion(imgShipsAtlas, (12-i) * 400, 1600, 400, 400);
            }
        }
        imgFragment[0] = new TextureRegion(imgFragmentsAtlas, 0, 0, 100, 100);
        imgFragment[1] = new TextureRegion(imgFragmentsAtlas, 500, 0, 100, 100);

        stars[0] = new Stars(0);
        stars[1] = new Stars(SCR_HEIGHT);
        ship = new Ship();
    }

    @Override
    public void show() {
        touch.set(0, 0, 0);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            //
        }
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
        /* else if (isAccelerometerAvailable){
            ship.vx = -Gdx.input.getAccelerometerX()*10;
        } */
        /*else if (isGyroscopeAvailable){
            ship.vx = Gdx.input.getGyroscopeY()*10;
            s = "x: "+ Gdx.input.getGyroscopeX()+"\n";
            s += "y: "+ Gdx.input.getGyroscopeY()+"\n";
            s += "z: "+ Gdx.input.getGyroscopeZ()+"\n";
        }*/

        // события
        for (Stars s: stars) s.move();
        ship.move();
        spawnEnemy();
        spawnShot();
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).move();
            if(enemies.get(i).outOfScreen()) {
                enemies.removeIndex(i);
                spawnFragments(ship);
            }
        }
        for (int i = 0; i < shots.size; i++) {
            shots.get(i).move();
            if(shots.get(i).outOfScreen()) {
                shots.removeIndex(i);
                continue;
            }
            for (int j = 0; j < enemies.size; j++) {
                if(shots.get(i).overlap(enemies.get(j))){
                    spawnFragments(enemies.get(j));
                    sndExplosion.play();
                    shots.removeIndex(i);
                    enemies.removeIndex(j);
                    break;
                }
            }
        }
        for (int i = 0; i < fragments.size; i++) {
            fragments.get(i).move();
            if(fragments.get(i).outOfScreen()){
                fragments.removeIndex(i);
            }
        }

        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Stars s: stars) {
            batch.draw(imgBackGround, s.x, s.y, s.width, s.height);
        }
        font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
        for(Fragment f: fragments) {
            batch.draw(imgFragment[f.type], f.getX(), f.getY(), f.width/2, f.height/2, f.width, f.height, 1, 1, f.rotation);
        }
        for (Enemy s: enemies) {
            batch.draw(imgEnemy[s.phase], s.getX(), s.getY(), s.width, s.height);
        }
        for (Shot s: shots) {
            batch.draw(imgShot, s.getX(), s.getY(), s.width, s.height);
        }
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
        imgShipsAtlas.dispose();
        imgFragmentsAtlas.dispose();
        imgShot.dispose();
        sndShot.dispose();
        sndExplosion.dispose();
    }

    void spawnShot(){
        if(TimeUtils.millis() > timeShotLastSpawn+timeShotInterval){
            shots.add(new Shot(ship));
            timeShotLastSpawn = TimeUtils.millis();
            sndShot.play(0.2f);
        }
    }

    void spawnEnemy(){
        if(TimeUtils.millis() > timeEnemyLastSpawn+timeEnemyInterval){
            enemies.add(new Enemy());
            timeEnemyLastSpawn = TimeUtils.millis();
        }
    }

    void spawnFragments(SpaceObject object){
        for (int i = 0; i < nFragments; i++) {
            fragments.add(new Fragment(object));
        }
    }
}
