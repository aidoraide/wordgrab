package com.soswag.aidan.wordgrab.Grid;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.soswag.aidan.wordgrab.Analytics;
import com.soswag.aidan.wordgrab.GamePanel.GamePanel;
import com.soswag.aidan.wordgrab.GamePanel.MainThread;
import com.soswag.aidan.wordgrab.GamePanel.TileRoster;
import com.soswag.aidan.wordgrab.GamePanel.Timebar;
import com.soswag.aidan.wordgrab.MyAnimation.FadeAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.InGameAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.LinkedAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.SlideResizeAnimation;
import com.soswag.aidan.wordgrab.PreferenceManager;
import com.soswag.aidan.wordgrab.Tile.v2Tile;
import com.soswag.aidan.wordgrab.Unscramble.PointDisplayer;

/**
 * Created by Aidan on 2016-06-14.
 */
public class v3GridGamePanel extends GamePanel {

    public static final int GAME_MODE = 1;
    private final static int TIME_TO_PLAY = 120;

    private WordGrid wordGrid;
    private TileRoster tileRoster;
    private Resources resources;

    public v3GridGamePanel(AdView adView, Activity activity, int w, int h, Resources resources) {
        super(w, h, adView, resources, activity);
        this.resources = resources;
        //Add callback to the surfaceHolder to intercept events
        getHolder().addCallback(this);
        //make gamePanel focusable so it can handle events
        setFocusable(true);
        this.tileSize = width / 11;

        setBackgroundY(height * 7 / 25);
        wordGrid = new WordGrid(width, height, resources, ctx);
        pointDisplayer.setLocation(wordGrid.getXCoorFromGridX(0), wordGrid.getY() - 50, width * 0.12f);
        pointDisplayer.setAlignment(PointDisplayer.LEFT);
        tileRoster = new TileRoster(width / 2 - tileSize * 2, tileSize / 2, tileSize, MainThread.FPS + 3 * MainThread.FPS / PreferenceManager.getInstance(ctx).difficulty(), tilesInPlay, background, resources);
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_STARTED, Analytics.GRID);
    }

    @Override
    public void onGrabbedTileRelease(int xCoor, int yCoor, v2Tile grabbedTile) {
        if (wordGrid.getActiveSegmentX() > -1 && wordGrid.getActiveSegmentY() > -1) {
            soundManager.playRackSound();
            int pointsBefore = wordGrid.getPoints();
            wordGrid.addToActiveSegment(grabbedTile);
            pointDisplayer.addPoints(wordGrid.getPoints() - pointsBefore);
        }else
            grabbedTile.addAnimation(new LinkedAnimation(
                    new FadeAnimation(grabbedTile, InGameAnimation.DEFAULT_LENGTH)
                    , new SlideResizeAnimation(grabbedTile, grabbedTile.getX(), grabbedTile.getY() + 4 * tileSize, tileSize, InGameAnimation.DEFAULT_LENGTH * 2, true)
            ));

        wordGrid.setActiveSegment(-1, -1);
    }

    @Override
    public void onGrabbedTileMove(int xCoor, int yCoor, v2Tile grabbedTile) {
        if (wordGrid.getY() <= grabbedTile.getSize() + grabbedTile.getY()) {
            wordGrid.setActiveSegment(grabbedTile);
        }
    }

    @Override
    public v2Tile tileTouchChecks(int xCoor, int yCoor) {
        v2Tile wasTouched = tileRoster.touchCheck(xCoor, yCoor);
        if(wasTouched == null) {
            int pointsBefore = wordGrid.getPoints();
            wasTouched = wordGrid.touchCheck(xCoor, yCoor);
            pointDisplayer.addPoints(wordGrid.getPoints() - pointsBefore);
        }
        return wasTouched;
    }

    public void newGame(){
        super.newGame();
        wordGrid.clear();
        tileRoster.reset();
        timebar.setTotalTime(TIME_TO_PLAY);
        hideBanner();
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_STARTED, Analytics.GRID);
    }

    @Override
    public void onGameplayUpdate() {
        wordGrid.update();
        tileRoster.update();
    }

    @Override
    public void drawBackground(Canvas canvas) {
        tileRoster.draw(canvas);
        wordGrid.draw(canvas);
    }

    @Override
    public void drawForeground(Canvas canvas) {
        wordGrid.drawValidity(canvas);
    }

    @Override
    public void initializeGameOverScreen() {
        //TODO : Get best word into array
        gameOverScreen = new GridGameOverScreen(wordGrid.getRackedTiles(), wordGrid.getScoreMultipliers(), pointDisplayer.getPoints(), wordGrid.getBestWord(), width, height - adHeight, tileSize, ctx, resources);
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_FINISHED, Analytics.GRID);
        showBanner();
    }

    @Override
    public void onGamePlayBegins() {
        timebar.setTotalTime(TIME_TO_PLAY * Timebar.SECOND);
        timebar.start();
    }
}
