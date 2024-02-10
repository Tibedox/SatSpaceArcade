package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

import com.badlogic.gdx.math.MathUtils;

public class Enemy extends SpaceObject {

    public Enemy(){
        width = height = 200;
        x = MathUtils.random(width/2, SCR_WIDTH-width/2);
        y = MathUtils.random(SCR_HEIGHT+height, SCR_HEIGHT*2);
        vy = -MathUtils.random(3f, 7f);
    }

    @Override
    void move() {
        super.move();
        changePhase();
    }

    boolean outOfScreen(){
        if(y < -height/2) {
            return true;
        }
        return false;
    }
}
