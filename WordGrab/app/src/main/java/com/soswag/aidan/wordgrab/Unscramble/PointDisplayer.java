package com.soswag.aidan.wordgrab.Unscramble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.soswag.aidan.wordgrab.GamePanel.MainThread;
import com.soswag.aidan.wordgrab.GameSelectionActivity;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.R;

/**
 * Created by Aidan on 2016-08-03.
 */
public class PointDisplayer {

    public static final int NO_CHANGE = -99999;

    public static final int CENTER = 0;
    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    private int x, y;
    private int pointsToAdd;
    private int pastPoints;
    private float animProgress = 1.f;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int alignment = CENTER;

    public PointDisplayer(int x, int y, float textSize, Context context){
        this.x = x;
        this.y = y;
        pointsToAdd = 0;
        pastPoints = 0;
        paint.setTextSize(textSize);
        paint.setTypeface(GameSelectionActivity.getTypeface());
        paint.setColor(ContextCompat.getColor(context, R.color.points_color));
    }

    public void setAlignment(int newAlignment){this.alignment = newAlignment;}

    public void setLocation(int x, int y, float textSize){
        if(x != NO_CHANGE)
            this.x = x;
        if(y != NO_CHANGE)
            this.y = y;
        if(textSize != NO_CHANGE)
            paint.setTextSize(textSize);
    }

    public void update(){
        if(animProgress < 1.f)
            animProgress += 1.f / MainThread.FPS;
    }

    public void draw(Canvas canvas){
        int pointsToDraw = Math.round(pastPoints + pointsToAdd * animProgress);
        if(alignment == CENTER)
            MyDrawing.drawTextCenteredAt(x, y, String.valueOf(pointsToDraw), paint, canvas);
        else if(alignment == LEFT){
            canvas.drawText(String.valueOf(pointsToDraw), x, y, paint);
        }
    }

    public void addPoints(int pointsToAdd){
        pastPoints += this.pointsToAdd;
        this.pointsToAdd = pointsToAdd;
        animProgress = 0;
    }

    public int getPoints(){return pastPoints + pointsToAdd;}

    public void reset(){
        pastPoints = 0;
        pointsToAdd = 0;
    }
}
