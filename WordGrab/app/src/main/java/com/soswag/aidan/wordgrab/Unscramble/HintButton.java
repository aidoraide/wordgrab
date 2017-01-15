package com.soswag.aidan.wordgrab.Unscramble;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import com.soswag.aidan.wordgrab.GamePanel.CircleButton;
import com.soswag.aidan.wordgrab.R;

/**
 * Created by Aidan on 2016-07-31.
 */
public class HintButton {

    private static final double SOLVE_JUICE_MAX = 2 * Math.PI;

    private CircleButton circleButton;
    private double solveJuice = Math.PI / 2;
    private double juiceToGetHint;
    private int difficulty;
    private int x, y, radius;
    private Paint paint;

    public HintButton(int x, int y, int radius, int difficulty, Resources resources){
        circleButton = new CircleButton(x, y, radius, BitmapFactory.decodeResource(resources, R.drawable.hint_button), null);
        this.difficulty = difficulty;
        juiceToGetHint = SOLVE_JUICE_MAX / 16 * difficulty;
        this.x = x;
        this.y = y;
        this.radius = radius;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xff8888dd);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeWidth(radius * CircleButton.CHILD_FRACTION_OF_RADIUS_FOR_OUTLINE);
    }

    public void draw(Canvas canvas){
        if(Build.VERSION.SDK_INT >= 21) {
            canvas.drawArc(x - radius, y - radius, x + radius, y + radius, 270.f, (float) Math.toDegrees(solveJuice), true, paint);
        }else
            canvas.drawLine(x-radius, y + radius * 3 / 2, x - radius + 2 * radius * (float)(solveJuice / SOLVE_JUICE_MAX), y + radius * 3 / 2, paint);
        circleButton.draw(canvas);
    }

    public boolean wasTouched(int xCoor, int yCoor){
        if(circleButton.wasTouched(xCoor, yCoor)){
            if(solveJuice >= juiceToGetHint){
                solveJuice -= juiceToGetHint;
                return true;
            }
        }
        return false;
    }

    public void addSolveJuice(double fraction){
        solveJuice += fraction * juiceToGetHint;
        if(solveJuice > SOLVE_JUICE_MAX)
            solveJuice = SOLVE_JUICE_MAX;
    }

    public void reset(){
        solveJuice = Math.PI / 2;
    }

    public boolean hasJuice(){return solveJuice >= juiceToGetHint;}

    public void useJuice(){
        solveJuice -= juiceToGetHint;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
