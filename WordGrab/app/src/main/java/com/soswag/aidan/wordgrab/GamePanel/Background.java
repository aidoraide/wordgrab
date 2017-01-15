package com.soswag.aidan.wordgrab.GamePanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;

import com.soswag.aidan.wordgrab.GameSelectionActivity;
import com.soswag.aidan.wordgrab.InGameActivity;
import com.soswag.aidan.wordgrab.R;

/**
 * Created by Aidan on 2016-05-04.
 */
public class Background {

    private static final String [] messages = {"3", "2", "1", "Grab!"};
    public static final int FRAMES_FOR_FULL_ANIM = (messages.length + 1) * MainThread.FPS;
    private static final int FRAMES_OF_OVERLAP = 6;
    private static final int FRAMES_ON_SCREEN = MainThread.FPS + 2 * FRAMES_OF_OVERLAP;

    int animProgress = 0;
    Paint textPaint;
    int y;

    public Background(Context ctx, int width, int y){
        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(ctx, R.color.background_text_color));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(width / 10);
        textPaint.setTypeface(GameSelectionActivity.getTypeface());
        this.y = y;
    }

    public void draw(Canvas canvas){

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffffffff);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        if(animProgress <= FRAMES_FOR_FULL_ANIM) {
            for (int i = 0; i < messages.length; i++) {
                int adjustedAnimProgress = animProgress + i * FRAMES_OF_OVERLAP;
                if (i == adjustedAnimProgress / FRAMES_ON_SCREEN) {
                    if (adjustedAnimProgress % FRAMES_ON_SCREEN < FRAMES_OF_OVERLAP) {
                        double funcX = (Math.sin(Math.PI / 12 * (adjustedAnimProgress % FRAMES_OF_OVERLAP - 12)) + 2) / 2;
                        drawCenteredString(messages[i], (int) (funcX * canvas.getWidth()), y, canvas);
                    } else if (adjustedAnimProgress % FRAMES_ON_SCREEN > FRAMES_ON_SCREEN - FRAMES_OF_OVERLAP) {
                        double funcX = Math.cos(adjustedAnimProgress % FRAMES_OF_OVERLAP * Math.PI / 6) / 4 + 0.25;
                        drawCenteredString(messages[i], (int) (funcX * canvas.getWidth()), y, canvas);
                    }else
                        drawCenteredString(messages[i], canvas.getWidth() / 2, y, canvas);
                }
            }
        }

    }

    public void update(){
        animProgress += 1;
    }

    public void reset(){animProgress = 0;}

    public boolean isFinishedAnimating(){return animProgress > FRAMES_FOR_FULL_ANIM;}

    public boolean spawnTime(){return animProgress > FRAMES_FOR_FULL_ANIM - FRAMES_ON_SCREEN;}

    public void drawCenteredString(String message, int x, int y, Canvas canvas){

        Rect bounds = new Rect();
        textPaint.getTextBounds(message, 0, message.length(), bounds);
        canvas.drawText(message, x - bounds.width() / 2, y + bounds.height() / 2, textPaint);

    }

}
