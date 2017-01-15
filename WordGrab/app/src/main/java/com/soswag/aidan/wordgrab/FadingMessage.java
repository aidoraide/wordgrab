package com.soswag.aidan.wordgrab;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.soswag.aidan.wordgrab.MyAnimation.InGameAnimation;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-08-09.
 */
public class FadingMessage {

    private static final String TAG = "My_FadingMessage";

    private static final int ALPHA_TICK = 255 / InGameAnimation.DEFAULT_LENGTH;
    private static final int CHARS_PER_LINE = 25;

    int x;
    int yTop;
    int oneLineHeight;
    int framesDuration;
    int frameCount = 0;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    String [] messageInLines;

    public FadingMessage(int x, int y, float textSize, int duration, String message){
        this.x = x;
        this.framesDuration = duration;
        paint.setTextSize(textSize);
        paint.setTypeface(GameSelectionActivity.getTypeface());

        messageInLines = new String[message.length() / CHARS_PER_LINE + 1];
        char [] buffer = new char[CHARS_PER_LINE];
        int bufferCount = 0;
        int whitespace = 0;
        for(int i = 0, line = 0; i < message.length(); i++, bufferCount++){
            buffer[bufferCount] = message.charAt(i);
            if(message.charAt(i) == ' ')
                whitespace = bufferCount;
            if(bufferCount == CHARS_PER_LINE - 1){
                messageInLines[line] = String.valueOf(buffer, 0, whitespace);

                buffer = new char[CHARS_PER_LINE];
                bufferCount = 0;
                i -= CHARS_PER_LINE - whitespace - 1;
                line++;
            }else if(i == message.length() - 1){
                messageInLines[line] = String.valueOf(buffer, 0, bufferCount + 1);
            }
        }
        for(String s : messageInLines)
            Log.d(TAG, s);

        oneLineHeight = (int)(paint.descent() - paint.ascent());
        int totalY = oneLineHeight * messageInLines.length;
        this.yTop = y - (totalY - oneLineHeight) / 2;
    }

    public void draw(Canvas canvas){
        int y = yTop;
        for(String line : messageInLines) {
            drawTextCenteredAt(x, y, line, canvas);
            y += oneLineHeight;
        }
    }

    public void update(){
        frameCount++;
        if(frameCount > framesDuration) {
            int alpha = paint.getAlpha() - ALPHA_TICK;
            if(alpha < 0)
                alpha = 0;
            paint.setAlpha(alpha);

        }
    }

    public boolean isDone(){
        return paint.getAlpha() <= 0;
    }

    public void drawTextCenteredAt(int x, int y, String toDraw, Canvas canvas) {

        float yBaseline = y - (paint.descent() + paint.ascent()) / 2;
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(toDraw, 0, toDraw.length(), x, yBaseline, paint);

    }
}
