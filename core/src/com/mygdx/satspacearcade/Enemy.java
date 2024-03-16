package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.*;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Enemy extends SpaceObject {
    long timeShotLastSpawn, timeShotInterval = 2000;

    public Enemy(){
        type = MathUtils.random(TYPE_ENEMY1, TYPE_ENEMY4);
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
        return y < -height / 2;
    }

    boolean isShoot(){
        if(y<SCR_HEIGHT-height) {
            if (TimeUtils.millis() > timeShotLastSpawn + timeShotInterval) {
                timeShotLastSpawn = TimeUtils.millis();
                return true;
            }
        }
        return false;
    }
}
