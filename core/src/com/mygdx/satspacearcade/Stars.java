package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.*;

public class Stars extends SpaceObject {
    public Stars(float y){
        this.y = y;
        width = SCR_WIDTH;
        height = SCR_HEIGHT+2f;
        vy = -2;
    }

    @Override
    void move() {
        super.move();
        if(y<-SCR_HEIGHT) y = SCR_HEIGHT;
    }
}
