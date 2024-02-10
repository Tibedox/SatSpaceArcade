package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

public class Shot extends SpaceObject {

    public Shot(Ship ship){
        x = ship.x;
        y = ship.y;
        width = height = ship.width;
        vy = 10;
    }

    boolean outOfScreen(){
        return y > SCR_HEIGHT + height / 2;
    }

    boolean overlap(Enemy enemy) {
        return Math.abs(x-enemy.x)<width/3+enemy.width/3 &
                Math.abs(y-enemy.y)<height/3+enemy.height/3;
    }
}
