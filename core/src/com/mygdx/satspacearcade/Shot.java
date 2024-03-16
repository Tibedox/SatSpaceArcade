package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;
import static com.mygdx.satspacearcade.SatSpaceArcade.TYPE_SHIP;

public class Shot extends SpaceObject {

    public Shot(SpaceObject object){
        type = object.type;
        x = object.x;
        y = object.y;
        width = height = object.width;

        if (type == TYPE_SHIP) {
            vy = 15;
        } else {
            vy = -15;
        }
    }

    boolean outOfScreen(){
        return y > SCR_HEIGHT + height / 2 | y < -height / 2;
    }
}
