package com.soswag.aidan.wordgrab.MyAnimation;

import android.graphics.Paint;
import android.graphics.Point;

import com.soswag.aidan.wordgrab.Tile.TouchableObject;

/**
 * Created by Aidan on 2016-07-15.
 */
public class DelayAnimation extends InGameAnimation {

    public DelayAnimation(TouchableObject attachedObj, int frames){
        super(attachedObj, frames);

    }

    public boolean tick(){
        return super.tick();
    }

}
