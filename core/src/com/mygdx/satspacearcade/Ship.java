package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;
import static com.mygdx.satspacearcade.SatSpaceArcade.TYPE_SHIP;

import com.badlogic.gdx.utils.TimeUtils;

public class Ship extends SpaceObject{
    int lives;
    boolean isAlive;
    long timeShotLastSpawn, timeShotInterval = 700;

    public Ship(){
        type = TYPE_SHIP;
        x = SCR_WIDTH/2;
        y = SCR_HEIGHT/12;
        width = height = 200;
    }

    @Override
    void move() {
        super.move();
        outOfScreen();
        changePhase();
    }

    void touch(float tx) {
        vx = (tx - x) / 20;
    }

    void outOfScreen(){
        if(x < width/2) {
            x = width/2;
            vx = 0;
        }
        if(x > SCR_WIDTH-width/2){
            x = SCR_WIDTH-width/2;
            vx = 0;
        }
    }

    boolean isShoot(){
        if(TimeUtils.millis() > timeShotLastSpawn+timeShotInterval){
            timeShotLastSpawn = TimeUtils.millis();
            return true;
        }
        return false;
    }
}
