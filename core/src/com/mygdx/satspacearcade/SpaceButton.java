package com.mygdx.satspacearcade;

import static com.mygdx.satspacearcade.SatSpaceArcade.SCR_WIDTH;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

public class SpaceButton {
    BitmapFont font;
    String text;
    float x, y;
    float width, height;

    public SpaceButton(String text, float x, float y, BitmapFont font) {
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;
        GlyphLayout layout = new GlyphLayout(font, text);
        width = layout.width;
        height = layout.height;
    }

    public SpaceButton(String text, float y, BitmapFont font, int align) {
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;
        GlyphLayout layout = new GlyphLayout(font, text);
        width = layout.width;
        height = layout.height;
        if(align == Align.center){
            x = SCR_WIDTH/2 - width/2;
        }
        if(align == Align.left){
            x = 0;
        }
        if(align == Align.right){
            x = SCR_WIDTH - width;
        }
    }

    boolean hit(float tx, float ty) {
        return x < tx & tx < x+width & y-height < ty & ty < y;
    }

    void setText(String text) {
        this.text = text;
        GlyphLayout layout = new GlyphLayout(font, text);
        width = layout.width;
    }
}
