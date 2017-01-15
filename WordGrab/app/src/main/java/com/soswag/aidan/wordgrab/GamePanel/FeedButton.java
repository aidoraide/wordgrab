package com.soswag.aidan.wordgrab.GamePanel;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.soswag.aidan.wordgrab.MainActivity;
import com.soswag.aidan.wordgrab.R;

/**
 * Created by Aidan on 2016-08-02.
 */
public class FeedButton {

    private CircleButton circleButton;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int framesToReset;
    private int framesSinceLastPress;
    private float portionOfCircle = 0.f;

    public FeedButton(int x, int y, int radius, int framesToReset, Resources resources){
        circleButton = new CircleButton(x, y, radius, BitmapFactory.decodeResource(resources, R.drawable.feed_button_shady), null);
        this.framesToReset = framesToReset;
        this.framesSinceLastPress = framesToReset;
        paint.setColor(0xff44ad8d);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radius * CircleButton.CHILD_FRACTION_OF_RADIUS_FOR_OUTLINE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void draw(Canvas canvas){
        canvas.drawArc(circleButton.getX() - circleButton.getRadius()
                , circleButton.getY() - circleButton.getRadius()
                , circleButton.getX() + circleButton.getRadius()
                , circleButton.getY() + circleButton.getRadius()
                , -90.f, portionOfCircle, true, paint);
        circleButton.draw(canvas);
    }

    public void update(){
        framesSinceLastPress++;
        portionOfCircle = 360.f * framesSinceLastPress / framesToReset;
        if(portionOfCircle > 360.f)
            portionOfCircle = 360.f;
        if(portionOfCircle < 0.f)
            portionOfCircle = 0.f;
    }

    public boolean wasTouched(int xCoor, int yCoor){
        if(circleButton.wasTouched(xCoor, yCoor) && framesSinceLastPress >= framesToReset){
            framesSinceLastPress = 0;
            return true;
        }
        return false;
    }

    public void makeTemporarilyUntouchable(){
        framesSinceLastPress = 0;
    }

}
