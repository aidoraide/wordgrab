package com.soswag.aidan.wordgrab.MyAnimation;

import android.graphics.Paint;
import android.graphics.Point;

import com.soswag.aidan.wordgrab.Tile.TouchableObject;

/**
 * Created by Aidan on 2016-07-15.
 */
public class FadeAnimation extends InGameAnimation {

    private static final double ANIM_END_POINT = 1.0;

    private double animProgress = 0;
    private double animProgressPerTick;

    private Paint transparencyPaint;

    public FadeAnimation(TouchableObject attachedObj, int frames){
        super(attachedObj, frames);
        animProgressPerTick = ANIM_END_POINT / frames;
        transparencyPaint = new Paint();
    }

    public boolean tick(){
        boolean tickResult = super.tick();

        if(tickResult){
            animProgress += animProgressPerTick;
            int alpha = 0x000000ff - (int)(0x000000ff * animProgress);
            transparencyPaint.setAlpha(alpha);
        }

        return tickResult;
    }

    @Override
    public Paint getPaint(){return  transparencyPaint;}

}
