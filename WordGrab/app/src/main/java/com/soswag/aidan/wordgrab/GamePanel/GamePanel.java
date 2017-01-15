package com.soswag.aidan.wordgrab.GamePanel;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.soswag.aidan.wordgrab.FadingMessage;
import com.soswag.aidan.wordgrab.Tile.v2Tile;
import com.soswag.aidan.wordgrab.Unscramble.PointDisplayer;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-06-14.
 */
public abstract class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "My_GamePanel";

    private final MainThread thread;
    protected final Background background;
    protected final Context ctx;
    protected final Resources resources;
    protected final ArrayList<v2Tile> tilesInPlay = new ArrayList<v2Tile>();
    protected final SoundManager soundManager;
    protected final PointDisplayer pointDisplayer;
    protected final Timebar timebar;
    protected GameOverScreen gameOverScreen = null;
    private Activity activity;
    private AdView adView;
    protected int adHeight = 0;

    protected final int width;
    protected final int height;
    protected int tileSize;

    private v2Tile grabbedTile;
    private int relDistX;
    private int relDistY;
    protected int fingerDownX;
    protected int fingerDownY;

    protected boolean onTimebarOutCalled = false;
    protected int frameCount = 0;

    private FadingMessage fadingMessage;

    public GamePanel(int width, int height, AdView adView, Resources resources, Activity activity) {
        super(activity);
        //Add callback to the surfaceHolder to intercept events
        getHolder().addCallback(this);
        //make gamePanel focusable so it can handle events
        setFocusable(true);

        adHeight = (int)(50.0 * resources.getDisplayMetrics().densityDpi / 160);

        this.width = width;
        this.height = height;
        this.ctx = activity.getApplicationContext();
        this.resources = resources;
        this.activity = activity;
        this.adView = adView;

        background = new Background(ctx, width, height / 2);
        soundManager = SoundManager.getInstance(ctx);
        pointDisplayer = new PointDisplayer(width / 2, height / 6, width * 0.25f, ctx);
        timebar = new Timebar(width);
        thread = new MainThread(getHolder(), this);
    }

    public void setBackgroundY(int y){
        background.y = y;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Gamepanel surface created");
        if(!thread.isRunning()) {
            thread.setRunning(true);
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Gamepanel surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Gamepanel surface destroyed");
    }

    public void addFadingMessage(int x, int y, float textSize, int framesDuration, String message){
        this.fadingMessage = new FadingMessage(x, y, textSize, framesDuration, message);
    }

    public void terminate(){
        Log.d(TAG, "Thread terminated");
        if(thread.isRunning()) {

            thread.setRunning(false);
            boolean retry = true;
            int retrys = 0;
            while (retry && retrys < 1000) {

                try {
                    thread.join();
                    retry = false;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    retrys++;
                }

            }

        }
    }

    public void update(){
        frameCount++;
        timebar.update();
        if(fadingMessage != null)
            if(fadingMessage.isDone())
                fadingMessage = null;
            else
                fadingMessage.update();
        if(!timebar.isRunning() && background.isFinishedAnimating())
            onGamePlayBegins();

        if(timebar.hasRunOut()){
            if( ! onTimebarOutCalled)
                onTimebarOut();
            else if(gameOverScreen != null)
                onGameOverUpdate();
            else
                Log.w(TAG, "Warning not calling onGameOverUpdate() or onTimebarOut() when timebar has run out");
        }else{
            background.update();
            pointDisplayer.update();

            for (int i = 0; i < tilesInPlay.size(); i++) {
                tilesInPlay.get(i).update();
                if (tilesInPlay.get(i).shouldRemove() || tilesInPlay.get(i).getX() < -tileSize) {
                    tilesInPlay.remove(i);
                    i--;
                }
            }

            onGameplayUpdate();
        }
    }

    public void draw(Canvas canvas){
        if(canvas != null){
            super.draw(canvas);
            final int savedState = canvas.save();

            background.draw(canvas);

            //Subclass to override if any funcionality is wanted
            drawBackground(canvas);

            for(v2Tile t : tilesInPlay)
                if(t != null)
                    t.draw(canvas);

            //Subclass to override if any funcionality is wanted
            drawForeground(canvas);

            timebar.draw(canvas);
            pointDisplayer.draw(canvas);

            if(gameOverScreen != null)
                gameOverScreen.draw(canvas);

            if(fadingMessage != null)
                fadingMessage.draw(canvas);

            canvas.restoreToCount(savedState);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(!background.isFinishedAnimating())
            return false;

        int action = event.getAction();

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch(action){

            case MotionEvent.ACTION_DOWN:

                fingerDownX = x;
                fingerDownY = y;

                if(gameOverScreen == null) {

                    if(onGameplayTouchDown(x, y))
                        return true;

                    grabbedTile = tileTouchChecks(x, y);
                    //Deal with all tile touching events
                    //If a tile is touched A) set grabbedTile to that tile B)unrack that tile C) set coordinates relative to finger
                    if(grabbedTile == null)
                        for(v2Tile t : tilesInPlay)
                            if(t != null)
                                if(t.wasTouched(x, y))
                                    grabbedTile = t;

                    if (grabbedTile != null) {
                        Log.d(TAG, "Tile touched");
                        soundManager.playGrabSound();
                        relDistX = grabbedTile.getX() - x;
                        relDistY = grabbedTile.getY() - y;
                        onTileGrabbed(grabbedTile);
                        break;
                    }

                } else {
                    onGameOverTouch(x, y);
                }
                break;

            case MotionEvent.ACTION_MOVE:

                onGameOverMove(x, y);

                if(grabbedTile != null) {
                    //Move grabbed solvedTiles with movements of touch finger
                    grabbedTile.setX(x + relDistX);
                    grabbedTile.setY(y + relDistY);
                    onGrabbedTileMove(x, y, grabbedTile);
                }

                break;

            case MotionEvent.ACTION_UP:

                if(grabbedTile != null) {
                    onGrabbedTileRelease(x, y, grabbedTile);
                    grabbedTile = null;
                }

                break;
        }

        //Temporary
        return true;
    }

    public boolean onGameplayTouchDown(int xCoor, int yCoor){
        Log.d(TAG, "onGameplayTouchDown(" + xCoor + ", " +yCoor + ")");
        return false;
    }
    public v2Tile tileTouchChecks(int xCoor, int yCoor){return null;}
    /*Default behaviour is to check if play again button was touched,
    * if it was then start a new game
    * */
    public void onGameOverTouch(int xCoor, int yCoor){
        Log.d(TAG, "onGameOverTouch(" + xCoor + ", " +yCoor + ")");
        if(gameOverScreen.playAgainButtonWasTouched(xCoor, yCoor))
            newGame();
    }
    public void onTileGrabbed(v2Tile grabbedTile){}
    public void onGameOverMove(int xCoor, int yCoor){}
    public void onGrabbedTileMove(int xCoor, int yCoor, v2Tile grabbedTile){}
    public void onGrabbedTileRelease(int xCoor, int yCoor, v2Tile grabbedTile){}
    /*These are methods to do with updating*/
    public void onTimebarOut(){
        Log.d(TAG, "onTimebarOut()");
        onTimebarOutCalled = true;
        onGameOver();
    }
    public void onGameOver(){
        Log.d(TAG, "onGameOver()");
        if(gameOverScreen == null)
            initializeGameOverScreen();
    }
    public void onGameOverUpdate(){
        gameOverScreen.update();
    }
    public void onGamePlayBegins(){
        Log.d(TAG, "onGamePlayBegins()");
        timebar.start();
    }
    /*These are the draw methods for the 2 respective drawing times*/
    public void drawBackground(Canvas canvas){}
    public void drawForeground(Canvas canvas){}

    public void newGame(){
        Log.d(TAG, "newGame()");
        thread.setThreadStartTime();
        tilesInPlay.clear();
        background.reset();
        timebar.reset();
        pointDisplayer.reset();
        onTimebarOutCalled = false;
        gameOverScreen = null;
        grabbedTile = null;
        frameCount = 0;
    }

    protected void showBanner() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                adView.setVisibility(View.VISIBLE);
                /*adView.loadAd(new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("5C2E33DD68DDE5E25E53D3891C2E9C9C")
                        .build());*/
                adView.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    protected void hideBanner() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                adView.setVisibility(View.GONE);
            }
        });

    }
    public abstract void initializeGameOverScreen();
    public abstract void onGameplayUpdate();
}