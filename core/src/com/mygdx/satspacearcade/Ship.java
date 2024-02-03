package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

public class Ship extends SpaceObject{

    public Ship(){
        x = SCR_WIDTH/2;
        y = SCR_HEIGHT/12;
        width = height = 200;
    }

    @Override
    void move() {
        super.move();
    }
}
