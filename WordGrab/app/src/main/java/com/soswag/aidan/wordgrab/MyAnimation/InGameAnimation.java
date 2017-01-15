package com.soswag.aidan.wordgrab.MyAnimation;

import android.graphics.Paint;
import android.graphics.Point;

import com.soswag.aidan.wordgrab.GamePanel.MainThread;
import com.soswag.aidan.wordgrab.Tile.TouchableObject;

/**
 * Created by Aidan on 2016-07-15.
 */
public abstract class InGameAnimation {

    public static final int DEFAULT_LENGTH = (int)Math.round(MainThread.FPS * 0.25);

    protected int framesRemaining;
    protected TouchableObject attachedObject;

    public InGameAnimation(TouchableObject attachedObject, int frames){
        framesRemaining = frames;
        this.attachedObject = attachedObject;
    }

    //Returns true if frames are remaining and false if animation is done
    public boolean tick(){
        if(framesRemaining > 0) {
            framesRemaining -= 1;
            return true;
        }
        return false;
    }

    public Paint getPaint(){
        return null;
    }

    public Point getAnimEndPoint(){
        return null;
    }

    public int getFramesRemaining(){return framesRemaining;}

    @Override
    public String toString() {
        return "InGameAnimation{" +
                "framesRemaining=" + framesRemaining +
                '}';
    }
}
