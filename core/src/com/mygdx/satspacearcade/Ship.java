package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

import com.badlogic.gdx.utils.TimeUtils;

public class Ship extends SpaceObject{
    int phase, nPhases = 12;
    long timeLastPhase, timePhaseInterval = 50;

    public Ship(){
        x = SCR_WIDTH/2;
        y = SCR_HEIGHT/12;
        width = height = 200;
    }

    @Override
    void move() {
        super.move();
        changePhase();
    }

    void changePhase(){
        if(TimeUtils.millis() > timeLastPhase+timePhaseInterval) {
            if (++phase == nPhases) phase = 0;
            timeLastPhase = TimeUtils.millis();
        }
    }
}
