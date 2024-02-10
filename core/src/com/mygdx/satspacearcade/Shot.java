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
        if(y > SCR_HEIGHT+height/2) {
            return true;
        }
        return false;
    }
}
