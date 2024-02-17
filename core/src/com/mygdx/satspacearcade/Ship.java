package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;
import static com.mygdx.satspacearcade.SatSpaceArcade.TYPE_SHIP;

public class Ship extends SpaceObject{
    int lives;
    boolean isAlive;

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
}
