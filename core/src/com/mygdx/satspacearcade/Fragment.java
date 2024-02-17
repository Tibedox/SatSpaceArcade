package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_HEIGHT;
import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

import com.badlogic.gdx.math.MathUtils;

public class Fragment extends SpaceObject{
    float v;
    float a;
    float rotation, speedRotation;

    public Fragment(SpaceObject object) {
        type = object.type;
        x = object.x;
        y = object.y;
        width = MathUtils.random(object.width/20, object.width/4);
        height = MathUtils.random(object.height/20, object.height/4);
        a = MathUtils.random(0f, 360f);
        v = MathUtils.random(1f, 7f);
        vx = v * MathUtils.sin(a);
        vy = v * MathUtils.cos(a);
        speedRotation = MathUtils.random(-5f, 5f);
    }

    @Override
    void move() {
        super.move();
        rotation += speedRotation;
    }

    boolean outOfScreen(){
        return y>SCR_HEIGHT+height/2 | y<-height/2 | x>SCR_WIDTH+width/2 | x<-width/2;
    }
}
