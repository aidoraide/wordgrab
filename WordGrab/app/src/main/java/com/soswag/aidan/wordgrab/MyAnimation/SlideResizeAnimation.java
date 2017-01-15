package com.soswag.aidan.wordgrab.MyAnimation;

import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.soswag.aidan.wordgrab.Tile.TouchableObject;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

/**
 * Created by Aidan on 2016-07-15.
 * Objects slid with this class are translated with respect to their centre coordinates
 * as to not interfere with other animations such as resize animations
 */
public class SlideResizeAnimation extends InGameAnimation {

    private static final String TAG = "My_SlideResizeAnim";

    private static final double SLIDE_ANIM_END_POINT = Math.PI;
    private static final double ANIM_END_POINT = 1.0;

    private Point animStartPoint;
    private Point animEndPoint;
    private int animStartSize;
    private int animEndSize;
    private double animationProgress = 0;
    private double progressPerTick;

    public SlideResizeAnimation(TouchableObject attachedObject, int endX, int endY, int endSize, int frames, boolean centered){
        super(attachedObject, frames);

        animStartPoint = attachedObject.getAnimEndPoint();

        //System.out.println(((v2Tile)attachedObject).getLetter() + " from " + animStartPoint + " to " + new Point(endX, endY));
        //Translate start points to the center of the object because we want to slide it from its center
        animStartPoint.x += attachedObject.getSize() / 2;
        animStartPoint.y += attachedObject.getSize() / 2;
        if(centered)
            animEndPoint = new Point(endX +  attachedObject.getSize() / 2, endY + attachedObject.getSize() / 2);
        else
            animEndPoint = new Point(endX +  endSize / 2, endY +  endSize / 2);
        progressPerTick = ANIM_END_POINT / frames;
        animStartSize = attachedObject.getSize();
        animEndSize = endSize;
    }

    public boolean tick() {

        boolean tickResult = super.tick();

        if(tickResult) {
            animationProgress += progressPerTick;

            if(animEndSize != animStartSize){
                attachedObject.resize((int)((animEndSize - animStartSize) * animationProgress + animStartSize));
            }

            double function = -0.5 * Math.cos(animationProgress * SLIDE_ANIM_END_POINT) + 0.5;
            super.attachedObject.setX(animStartPoint.x + (animEndPoint.x - animStartPoint.x) * function - attachedObject.getSize() / 2);
            super.attachedObject.setY(animStartPoint.y + (animEndPoint.y - animStartPoint.y) * function - attachedObject.getSize() / 2);
        }

        return tickResult;
    }

    public void overrideStartCoords(Point animStartPoint) {
        this.animStartPoint = animStartPoint;
    }


    public void overwrite(SlideResizeAnimation animOverwrite){

        int endX = animOverwrite.animEndPoint.x;
        int endY = animOverwrite.animEndPoint.y;
        this.animEndSize = animOverwrite.animEndSize;

        double z = Math.cos(animationProgress) - 1;
        z /= 2;

        if(z == -1)
            z = 1;
        animStartPoint.x = (int)((z * (animStartPoint.x - animEndPoint.x + endX) + animStartPoint.x) / (z + 1));
        animStartPoint.y = (int)((z * (animStartPoint.y - animEndPoint.y + endY) + animStartPoint.y) / (z + 1));

        animEndPoint = new Point(endX, endY);

    }

    @Override
    public Point getAnimEndPoint(){
        //Log.d(TAG, "Getting end point of : " + new Point(animEndPoint.x - animEndSize / 2, animEndPoint.y - animEndSize / 2));
        return new Point(animEndPoint.x - animEndSize / 2, animEndPoint.y - animEndSize / 2);
    }

    @Override
    public String toString() {
        return "SlideResizeAnimation{" +
                "animStartPoint=" + animStartPoint +
                ", animEndPoint=" + animEndPoint +
                ", animationProgress=" + animationProgress +
                ", progressPerTick=" + progressPerTick +
                '}';
    }
}
