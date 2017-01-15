package com.soswag.aidan.wordgrab.MyAnimation;

import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by Aidan on 2016-08-03.
 */
public class LinkedAnimation extends InGameAnimation{

    InGameAnimation [] animations;

    public LinkedAnimation(InGameAnimation... animations){
        super(animations[0].attachedObject, animations[0].framesRemaining);
        for(int i = 1; i < animations.length; i++)
            if(animations[i].framesRemaining > framesRemaining)
                framesRemaining = animations[i].framesRemaining;

        this.animations = animations;
    }

    public boolean tick(){
        boolean tickResult = super.tick();

        if(tickResult){
            for(InGameAnimation animation : animations)
                animation.tick();
        }

        return tickResult;
    }

    @Override
    public Paint getPaint(){
        for(InGameAnimation anim : animations)
            if(anim instanceof FadeAnimation)
                return ((FadeAnimation)anim).getPaint();
        return null;
    }

    @Override
    public Point getAnimEndPoint() {
        for(InGameAnimation anim : animations)
            if(anim instanceof SlideResizeAnimation)
                return anim.getAnimEndPoint();
        return null;
    }
}
