package com.soswag.aidan.wordgrab.GamePanel;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Aidan on 2016-07-13.
 */
public class Timebar {

    public static final long SECOND = 1000000000L;

    long totalNanos;
    long startTime;
    int length;
    int maxLength;

    boolean running;

    private Paint paint;
    private float halfStroke;

    public Timebar(int maxLength){
        System.out.println("Constructing Timebar");
        this.maxLength = this.length = maxLength;

        paint = new Paint();
        paint.setColor(0xff66dd66);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1.f * maxLength / 30);
        halfStroke = paint.getStrokeWidth() / 2;

    }

    public void start(){
        System.out.println("Starting Timebar");
        startTime = System.nanoTime();
        length = maxLength;
        running = true;
    }

    public void setTotalTime(long nanos){
        System.out.println("Setting Timebar");
        this.totalNanos = nanos;
        reset();
    }

    public void reset(){
        length = maxLength;
        running = false;
    }

    public void update(){
        if(running) {
            length = (int)((double) (totalNanos - (System.nanoTime() - startTime)) / totalNanos * maxLength);
        }
    }

    public void draw(Canvas canvas){
        if(length >= 0) {
            canvas.drawLine(-halfStroke, halfStroke, length - halfStroke, halfStroke, paint);
        }
    }

    public boolean hasRunOut(){
        if(running) {
            return System.nanoTime() - startTime > totalNanos;
        }
        return false;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public double getTimeRemainingFraction(){
        return (totalNanos - (System.nanoTime() - startTime)) / ((double)totalNanos);
    }
}
