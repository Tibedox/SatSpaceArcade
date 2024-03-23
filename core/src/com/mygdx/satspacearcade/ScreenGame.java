package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.*;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScreenGame implements Screen {
    // системные объекты
    SatSpaceArcade satSpaceArcade;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;

    boolean isGyroscopeAvailable;
    boolean isAccelerometerAvailable;

    // ресурсы
    BitmapFont fontLarge, fontSmall;
    Sound sndShot;
    Sound sndExplosion;
    Texture imgBackGround;
    Texture imgShipsAtlas;
    Texture imgFragmentsAtlas;
    TextureRegion[] imgShip = new TextureRegion[12];
    TextureRegion[][] imgEnemy = new TextureRegion[5][12];
    TextureRegion[] imgFragment = new TextureRegion[5];
    Texture[] imgShot = new Texture[2];

    SpaceButton btnSwitchGlobalRecods;
    SpaceButton btnBack;
    boolean isShowGlobalRecords;
    Stars[] stars = new Stars[2];
    Ship ship;
    int shipLives = 1;
    Array<Shot> shots = new Array<>();
    Array<Enemy> enemies = new Array<>();
    Array<Fragment> fragments = new Array<>();
    int nFragments = 50;
    boolean isGameOver;
    int kills;
    Player[] players = new Player[11];
    List<RecordFromDB> recordsFromDB = new ArrayList<>();

    // время
    long timeEnemyLastSpawn, timeEnemyInterval = 1500;
    long timeLastIncreaseSpeed, timeIncreaseSpeedInterval = 1000;

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
        imgShot[0] = new Texture("shoot_blaster_red.png");
        imgShot[1] = new Texture("shoot_blaster_blue.png");

        btnSwitchGlobalRecods = new SpaceButton("global/local records", SCR_HEIGHT/5, fontSmall, Align.center);
        btnBack = new SpaceButton("back to menu", SCR_HEIGHT/5-100, fontSmall, Align.center);

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
            ship.touch(touch.x);
        }
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if(isGameOver & btnBack.hit(touch.x, touch.y)){
                satSpaceArcade.setScreen(satSpaceArcade.screenMenu);
            }
            if(isGameOver & btnSwitchGlobalRecods.hit(touch.x, touch.y)){
                isShowGlobalRecords = !isShowGlobalRecords;
            }
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
            if(ship.isShoot()) spawnShot(ship);
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
            if((enemies.get(i).type == TYPE_ENEMY1 | enemies.get(i).type == TYPE_ENEMY3)
                & enemies.get(i).isShoot()){
                spawnShot(enemies.get(i));
            }
        }
        for (int i = shots.size-1; i>=0; i--) {
            shots.get(i).move();
            if(shots.get(i).outOfScreen()) {
                shots.removeIndex(i);
                continue;
            }
            if(ship.isAlive & shots.get(i).overlap(ship) & shots.get(i).type != TYPE_SHIP){
                shots.removeIndex(i);
                killShip();
                break;
            }
            for (int j = 0; j < enemies.size; j++) {
                if(shots.get(i).overlap(enemies.get(j))
                        && shots.get(i).type == TYPE_SHIP && shots.get(i).y<SCR_HEIGHT){
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
        increaseSpeed();

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
            batch.draw(imgShot[s.type==TYPE_SHIP?0:1], s.getX(), s.getY(), s.width, s.height);
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
            if(isShowGlobalRecords){
                fontSmall.draw(batch, "Global records", 0, 1070, SCR_WIDTH, Align.center, true);
                for (int i = 0; i < players.length - 1; i++) {
                    fontSmall.draw(batch, i + 1 + " " + recordsFromDB.get(i).name, 200, 960 - i * 60);
                    fontSmall.draw(batch, "......." + recordsFromDB.get(i).score, 200, 960 - i * 60, SCR_WIDTH - 400, Align.right, true);
                }
            }
            else {
                fontSmall.draw(batch, "Local records", 0, 1070, SCR_WIDTH, Align.center, true);
                for (int i = 0; i < players.length - 1; i++) {
                    fontSmall.draw(batch, i + 1 + " " + players[i].name, 200, 960 - i * 60);
                    fontSmall.draw(batch, "......." + players[i].score, 200, 960 - i * 60, SCR_WIDTH - 400, Align.right, true);
                }
            }
            btnSwitchGlobalRecods.font.draw(batch, btnSwitchGlobalRecods.text, btnSwitchGlobalRecods.x, btnSwitchGlobalRecods.y);
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
        for (Texture texture : imgShot) texture.dispose();
        sndShot.dispose();
        sndExplosion.dispose();
    }

    void spawnShot(SpaceObject object){
        shots.add(new Shot(object));
        if(satSpaceArcade.isSoundOn) sndShot.play(0.05f);
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

    void increaseSpeed(){
        if(TimeUtils.millis()> timeLastIncreaseSpeed + timeIncreaseSpeedInterval){
            for(Enemy e: enemies){
                e.vy -= 1;
            }
            timeLastIncreaseSpeed = TimeUtils.millis();
        }
    }

    void gameOver(){
        players[players.length-1].name = satSpaceArcade.playerName;
        players[players.length-1].score = kills;
        isGameOver = true;
        sortRecords2();
        saveRecords();
        saveRecordToDB();
        sortRecords1();
    }

    void gameStart(){
        fragments.clear();
        enemies.clear();
        shots.clear();
        ship.lives = shipLives;
        ship.isAlive = true;
        respawnShip();
        isGameOver = false;
        isShowGlobalRecords = false;
        kills = 0;
    }

    void sortRecords1() {
        class Cmp implements Comparator<RecordFromDB>{
            @Override
            public int compare(RecordFromDB p1, RecordFromDB p2) {
                return p2.score-p1.score;
            }
        }
        Collections.sort(recordsFromDB, new Cmp());
    }

    void sortRecords2() {
        class Cmp implements Comparator<Player>{
            @Override
            public int compare(Player p1, Player p2) {
                return p2.score-p1.score;
            }
        }
        Arrays.sort(players, new Cmp());
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

    void saveRecordToDB() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sat.sch120.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MyApi myApi = retrofit.create(MyApi.class);

        try {
            Response<List<RecordFromDB>> response = myApi.sendData(satSpaceArcade.playerName, kills).execute();
            recordsFromDB = response.body();
        } catch (IOException e) {
            //
        }

        /*myApi.sendData(satSpaceArcade.playerName, kills).enqueue(new Callback<List<RecordFromDB>>() {
            @Override
            public void onResponse(Call<List<RecordFromDB>> call, Response<List<RecordFromDB>> response) {
                recordsFromDB = response.body();
                sortRecords1();
            }

            @Override
            public void onFailure(Call<List<RecordFromDB>> call, Throwable t) {

            }
        });*/
    }
}
