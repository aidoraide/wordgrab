package com.soswag.aidan.wordgrab.GamePanel;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Aidan on 2016-05-03.
 */
public class MainThread extends Thread {

    public static final int FPS = 30;
    private double avgFPS;
    private final SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    private long threadStartTime;
    public static Canvas canvas;

    private boolean updating = false;
    private boolean drawing = false;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel){

        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        threadStartTime = System.nanoTime();

    }

    @Override
    public void run(){

        if(running) {

            long startTime;
            long postUpdateTime;
            long delayTime;
            long frameDuration = 1000000000 / FPS;
            long totalTime = 0;
            int frameCount = 0;

            while (running) {

                startTime = System.nanoTime();
                canvas = null;
                updating = true;
                this.gamePanel.update();
                updating = false;

                //Lock canvas
                try {

                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {

                        drawing = true;
                        this.gamePanel.draw(canvas);
                        drawing = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    boolean retry = true;

                    while (retry && canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                            retry = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                postUpdateTime = System.nanoTime();
                delayTime = (frameDuration - (postUpdateTime - startTime)) / 1000000;
                if (delayTime < 0)
                    delayTime = 0;

                try {
                    Thread.sleep(delayTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                totalTime += System.nanoTime() - startTime;
                frameCount++;

                if (frameCount == FPS) {

                    avgFPS = frameCount / ((double) totalTime / 1000000000);
                    totalTime = 0;
                    frameCount = 0;

                    //System.out.printf("Time spent Delaying = %2f ", (100000000.0 * delayTime / (System.nanoTime() - startTime)));
                    //System.out.println("%");
                }
            }
        }
    }

    public int getElapsedSecs(){
        return (int) ((System.nanoTime() - threadStartTime) / 1000000000);
    }

    public void setRunning(boolean run){
        running = run;
    }

    public boolean isRunning(){return running;}

    public void setThreadStartTime(){threadStartTime = System.nanoTime();}

    public boolean isUpdating() {
        return updating;
    }

    public boolean isDrawing() {
        return drawing;
    }
}
