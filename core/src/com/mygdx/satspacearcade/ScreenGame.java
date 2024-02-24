package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class ScreenGame implements Screen {
    SatSpaceArcade satSpaceArcade;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont fontLarge, fontSmall;

    boolean isGyroscopeAvailable;
    boolean isAccelerometerAvailable;

    Sound sndShot;
    Sound sndExplosion;

    Texture imgBackGround;
    Texture imgShipsAtlas;
    Texture imgFragmentsAtlas;
    TextureRegion[] imgShip = new TextureRegion[12];
    TextureRegion[][] imgEnemy = new TextureRegion[5][12];
    TextureRegion[] imgFragment = new TextureRegion[5];
    Texture imgShot;

    SpaceButton btnBack;
    Stars[] stars = new Stars[2];
    Ship ship;
    int shipLives = 3;
    Array<Shot> shots = new Array<>();
    long timeShotLastSpawn, timeShotInterval = 700;
    Array<Enemy> enemies = new Array<>();
    long timeEnemyLastSpawn, timeEnemyInterval = 1500;
    Array<Fragment> fragments = new Array<>();
    int nFragments = 50;
    boolean isGameOver;
    int kills;

    Player[] players = new Player[11];

    public ScreenGame(SatSpaceArcade satSpaceArcade) {
        this.satSpaceArcade = satSpaceArcade;
        batch = satSpaceArcade.batch;
        camera = satSpaceArcade.camera;
        touch = satSpaceArcade.touch;
        fontLarge = satSpaceArcade.fontLarge;
        fontSmall = satSpaceArcade.fontSmall;

        // проверяем, включены ли датчики гироскопа и акселерометра
        isGyroscopeAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
        isAccelerometerAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);

        sndShot = Gdx.audio.newSound(Gdx.files.internal("blaster.wav"));
        sndExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

        imgBackGround = new Texture("stars0.png");
        imgShipsAtlas = new Texture("ships_atlas3.png");
        imgFragmentsAtlas = new Texture("ships_fragment_atlas.png");
        imgShot = new Texture("shoot_blaster_red.png");

        btnBack = new SpaceButton("back to menu", SCR_HEIGHT/5, fontSmall, Align.center);

        for (int i = 0; i < imgShip.length; i++) {
            if(i<7) {
                imgShip[i] = new TextureRegion(imgShipsAtlas, i * 400, 0, 400, 400);
            } else {
                imgShip[i] = new TextureRegion(imgShipsAtlas, (12-i) * 400, 0, 400, 400);
            }
        }
        for (int j = 0; j < imgEnemy.length; j++) {
            for (int i = 0; i < imgEnemy[j].length; i++) {
                if (i < 7) {
                    imgEnemy[j][i] = new TextureRegion(imgShipsAtlas, i * 400, 400*j, 400, 400);
                } else {
                    imgEnemy[j][i] = new TextureRegion(imgShipsAtlas, (12 - i) * 400, 400*j, 400, 400);
                }
            }
        }
        imgFragment[0] = new TextureRegion(imgFragmentsAtlas, 0, 0, 100, 100);
        for (int i = 0; i < imgEnemy.length; i++) {
            imgFragment[i] = new TextureRegion(imgFragmentsAtlas, i*100+100, 0, 100, 100);
        }

        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Noname", 0);
        }
        loadRecords();

        stars[0] = new Stars(0);
        stars[1] = new Stars(SCR_HEIGHT);
        ship = new Ship();
    }

    @Override
    public void show() {
        touch.set(0, 0, 0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }
        gameStart();
    }

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.isTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if(isGameOver & btnBack.hit(touch.x, touch.y)){
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
        if(ship.isAlive) {
            ship.move();
            spawnEnemy();
            spawnShot();
        } else {
            if(shots.size == 0 & enemies.size == 0 & ship.lives>0){
                respawnShip();
            }
        }
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).move();
            if(enemies.get(i).outOfScreen()) {
                enemies.removeIndex(i);
                killShip();
                continue;
            }
            if(enemies.get(i).overlap(ship) & ship.isAlive){
                spawnFragments(enemies.get(i));
                enemies.removeIndex(i);
                killShip();
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
                    if(satSpaceArcade.isSoundOn) sndExplosion.play();
                    shots.removeIndex(i);
                    enemies.removeIndex(j);
                    if(!isGameOver) kills++;
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
        for(Fragment f: fragments) {
            batch.draw(imgFragment[f.type], f.getX(), f.getY(), f.width/2, f.height/2, f.width, f.height, 1, 1, f.rotation);
        }
        for (Enemy s: enemies) {
            batch.draw(imgEnemy[s.type][s.phase], s.getX(), s.getY(), s.width, s.height);
        }
        for (Shot s: shots) {
            batch.draw(imgShot, s.getX(), s.getY(), s.width, s.height);
        }
        if(ship.isAlive) {
            batch.draw(imgShip[ship.phase], ship.getX(), ship.getY(), ship.width, ship.height);
        }
        for (int i = 0; i < ship.lives; i++) {
            batch.draw(imgShip[0], SCR_WIDTH-90*(i+1), SCR_HEIGHT-90, 70, 70);
        }
        fontSmall.draw(batch, "Kills: "+kills, 20, SCR_HEIGHT-20);
        if(isGameOver) {
            fontLarge.draw(batch, "GAME OVER", 0, SCR_HEIGHT/4*3, SCR_WIDTH, Align.center, true);
            for (int i = 0; i < players.length-1; i++) {
                fontSmall.draw(batch, i+1+" "+players[i].name, 200, 1000-i*60);
                fontSmall.draw(batch, "......."+players[i].score, 200, 1000-i*60, SCR_WIDTH-400, Align.right, true);
            }
            btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
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
            if(satSpaceArcade.isSoundOn) sndShot.play(0.05f);
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

    void killShip(){
        if(ship.isAlive) {
            if(satSpaceArcade.isSoundOn) sndExplosion.play();
            spawnFragments(ship);
            ship.lives--;
            ship.isAlive = false;
            if(ship.lives == 0) {
                gameOver();
            }
        }
    }

    void respawnShip(){
        ship.isAlive = true;
        ship.x = SCR_WIDTH/2;
        ship.y = SCR_HEIGHT/12;
        ship.vx = 0;
    }

    void gameOver(){
        players[players.length-1].name = satSpaceArcade.playerName;
        players[players.length-1].score = kills;
        isGameOver = true;
        sortRecords();
        saveRecords();
    }

    void gameStart(){
        fragments.clear();
        enemies.clear();
        shots.clear();
        ship.lives = shipLives;
        ship.isAlive = true;
        respawnShip();
        isGameOver = false;
        kills = 0;
    }

    void sortRecords() {
        boolean flag = true;
        while (flag){
            flag = false;
            for (int i = 0; i < players.length-1; i++) {
                if(players[i].score<players[i+1].score){
                    Player c = players[i];
                    players[i] = players[i+1];
                    players[i+1] = c;
                    flag = true;
                }
            }
        }
    }

    void saveRecords() {
        Preferences prefs = Gdx.app.getPreferences("SatArcadeRecords");
        for (int i = 0; i < players.length; i++) {
            prefs.putString("name"+i, players[i].name);
            prefs.putInteger("score"+i, players[i].score);
        }
        prefs.flush();
    }

    void loadRecords() {
        Preferences prefs = Gdx.app.getPreferences("SatArcadeRecords");
        for (int i = 0; i < players.length; i++) {
            if(prefs.contains("name"+i)) players[i].name = prefs.getString("name"+i);
            if(prefs.contains("score"+i)) players[i].score = prefs.getInteger("score"+i);
        }
    }

    void clearRecords() {
        for (int i = 0; i < players.length; i++) {
            players[i].name = "Noname";
            players[i].score = 0;
        }
    }
}
