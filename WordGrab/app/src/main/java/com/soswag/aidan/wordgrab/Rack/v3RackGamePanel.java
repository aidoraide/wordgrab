package com.soswag.aidan.wordgrab.Rack;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.soswag.aidan.wordgrab.Analytics;
import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.GamePanel.CircleButton;
import com.soswag.aidan.wordgrab.GamePanel.GamePanel;
import com.soswag.aidan.wordgrab.GamePanel.MainThread;
import com.soswag.aidan.wordgrab.GamePanel.TileRoster;
import com.soswag.aidan.wordgrab.GamePanel.Timebar;
import com.soswag.aidan.wordgrab.MyAnimation.FadeAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.InGameAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.LinkedAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.SlideResizeAnimation;
import com.soswag.aidan.wordgrab.PreferenceManager;
import com.soswag.aidan.wordgrab.R;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-05-03.
 * The GamePanel holds all variables and methods for the in game activity of Rack Mode
 */

public class v3RackGamePanel extends GamePanel {

    private static final String TAG = "My_RackGamePanel";

    public static final int GAME_MODE = 0;
    private final static int TIME_TO_PLAY = 90;

    private WordRack wordRack;
    private TileRoster tileRoster;
    private ArrayList<Word> wordsPlayed = new ArrayList<>();
    private CircleButton reject;

    private int lastFingerY;

    public v3RackGamePanel(AdView adView, Activity activity, int w, int h, Resources resources){
        super(w, h, adView, resources, activity);

        this.tileSize = width / 8;
        wordRack = new WordRack(width, height, resources, ctx);

        setBackgroundY(height * 19 / 40);
        pointDisplayer.setLocation(w / 2, tileSize * 21 / 2, width * 0.12f);
        tileRoster = new TileRoster(width / 2 - tileSize * 2, tileSize * 3 / 2, tileSize, MainThread.FPS + 3 * MainThread.FPS / PreferenceManager.getInstance(getContext()).difficulty(), tilesInPlay, background, resources);
        wordRack.setSubmitButtonCoors(width * 5 / 6, tileSize * 21 / 2);
        Bitmap rejectBitmap = BitmapFactory.decodeResource(resources, R.drawable.reject_button);
        reject = new CircleButton(width / 6,  tileSize * 21 / 2, tileSize, rejectBitmap, null);
        timebar.setTotalTime(TIME_TO_PLAY * Timebar.SECOND);
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_STARTED, Analytics.RACK);
    }

    public void newGame(){
        super.newGame();
        Log.d(TAG, "newGame()");
        wordRack.clear();
        wordsPlayed.clear();
        tileRoster.reset();
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_STARTED, Analytics.RACK);
        hideBanner();
    }

    @Override
    public void initializeGameOverScreen() {
        Log.d(TAG, "initializeGameOverScreen()");
        Word.quickSort(wordsPlayed);
        Word bestWord = wordsPlayed.isEmpty() ? new Word() : wordsPlayed.get(0);
        String word = bestWord.getWord();
        v2Tile [] tiles = new v2Tile[word.length()];
        for(int i = 0; i < word.length(); i++){
            tiles[i] = new v2Tile(word.charAt(i), width * 3 / 2, height / 2, tileSize, true);
        }
        gameOverScreen = new RackGameOverScreen(tiles, word, pointDisplayer.getPoints(), wordsPlayed, width, height - adHeight, tileSize * 8 / 10, ctx, resources);
        showBanner();
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_FINISHED, Analytics.RACK);
        Log.d(TAG, "Initialized.");
    }

    @Override
    public v2Tile tileTouchChecks(int xCoor, int yCoor) {
        v2Tile wasTouched = tileRoster.touchCheck(xCoor, yCoor);
        if(wasTouched == null)
            wasTouched = wordRack.touchCheck(xCoor, yCoor);
        return wasTouched;
    }

    @Override
    public void onTileGrabbed(v2Tile grabbedTile) {
        wordRack.setActiveSegmentToNextOpen();
    }

    @Override
    public boolean onGameplayTouchDown(int xCoor, int yCoor) {
        Log.d(TAG, "onGamePlayTouchDown(" + xCoor + ", " + yCoor + ")");
        if(wordRack.submitButtonWasPressed(xCoor, yCoor)) {
            Log.d(TAG, "Submit button touched");
            if (wordRack.isSubmittable()) {
                Word word = wordRack.getWord();
                pointDisplayer.addPoints(word.getPts());
                wordsPlayed.add(word);
                wordRack.submit();
                soundManager.playScaleUp();
            }else
                soundManager.playErrorSound();

            return true;
        }
        else if(reject.wasTouched(xCoor, yCoor)){
            Log.d(TAG, "Reject button touched");
            wordRack.throwTilesAway();
            return true;
        }
        return false;
    }

    @Override
    public void onGrabbedTileMove(int xCoor, int yCoor, v2Tile grabbedTile) {
        if(gameOverScreen != null)
            return;
        if(wordRack.getY() <= grabbedTile.getSize() + grabbedTile.getY()) {
            wordRack.setActiveSegment(grabbedTile);
        }else if(Math.abs(yCoor - fingerDownY) > tileSize)
            wordRack.setActiveSegment(-1);
    }

    @Override
    public void onGrabbedTileRelease(int xCoor, int yCoor, v2Tile grabbedTile) {
        if(wordRack.getActiveSegment() > -1){
            soundManager.playRackSound();
            wordRack.shiftTiles();
            wordRack.addToActiveSegment(grabbedTile);
        }else if(Math.sqrt(Math.pow(xCoor - fingerDownX, 2) + Math.pow(yCoor - fingerDownY, 2)) < tileSize && wordRack.getActiveSegment() > -1){
                    /*
                     * If the tile has been:
                     * A) held for less than a second and
                     * B) has been dragged less than a tileSize from the location it was grabbed at
                     * C) the Word Rack has open space
                     * Then:
                     * Add it to the next available spot and play the rack sound
                    */
            soundManager.playRackSound();
            wordRack.addToActiveSegment(grabbedTile);
        }else {
            grabbedTile.addAnimation(new LinkedAnimation(
                    new FadeAnimation(grabbedTile, InGameAnimation.DEFAULT_LENGTH)
                    , new SlideResizeAnimation(grabbedTile, grabbedTile.getX(), grabbedTile.getY() + 4 * tileSize, tileSize, InGameAnimation.DEFAULT_LENGTH * 2, true)
            ));
            soundManager.playErrorSound();
        }

        wordRack.setActiveSegment(-1);
    }

    @Override
    public void onGameplayUpdate() {
        wordRack.update();
        tileRoster.update();
    }

    @Override
    public void drawBackground(Canvas canvas) {
        wordRack.draw(canvas);
        wordRack.drawHighlights(canvas);
        tileRoster.draw(canvas);
        reject.draw(canvas);
    }

    @Override
    public void drawForeground(Canvas canvas) {
        wordRack.drawActiveSegment(canvas);

    }
}
