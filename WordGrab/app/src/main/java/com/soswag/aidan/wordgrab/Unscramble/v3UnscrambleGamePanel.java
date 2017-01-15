package com.soswag.aidan.wordgrab.Unscramble;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.soswag.aidan.wordgrab.Analytics;
import com.soswag.aidan.wordgrab.DB.StatsDatabase;
import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.GamePanel.Background;
import com.soswag.aidan.wordgrab.GamePanel.CircleButton;
import com.soswag.aidan.wordgrab.GamePanel.GamePanel;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.PreferenceManager;
import com.soswag.aidan.wordgrab.R;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-07-13.
 */
public class v3UnscrambleGamePanel extends GamePanel {

    private static final String TAG = "My_UnscramblePanel";

    public static final int GAME_MODE = 2;

    private UnscrambleRack unscrambleRack;
    private ArrayList<Word> wordsPlayed = new ArrayList<>();
    //Used to manage spawning of solvedTiles
    private ScrambledTileSpawner scrambledTileSpawner;
    private boolean toStartTimer = false;

    private CircleButton scrambler;
    private CircleButton reject;
    private HintButton hinter;

    private Bitmap solvedOverlay;

    public v3UnscrambleGamePanel(AdView adView, Activity activity, int w, int h, Resources resources){

        super(w, h, adView, resources, activity);

        this.tileSize = width / 10;
        unscrambleRack = new UnscrambleRack(3 * h / 4, width, tileSize, resources, ctx);
        int difficulty = PreferenceManager.getInstance(ctx).difficulty();
        scrambledTileSpawner = new ScrambledTileSpawner(height / 2, width, tileSize, difficulty, tilesInPlay, resources);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap feedBitmap = BitmapFactory.decodeResource(resources, R.drawable.feed_button_shady, options);
        Matrix matrix = new Matrix();
        matrix.postRotate(180.f);//, canvas.getWidth() * 0.5f, canvas.getHeight() * 0.5f);
        Bitmap rejectBitmap = Bitmap.createBitmap(feedBitmap, 0, 0, feedBitmap.getWidth(), feedBitmap.getHeight(), matrix, true);
        reject = new CircleButton(width / 2, height * 7 / 20, tileSize, rejectBitmap, null);
        scrambler = new CircleButton(width * 3 / 4, height * 7 / 20, tileSize, BitmapFactory.decodeResource(resources, R.drawable.scramble_button), null);
        hinter = new HintButton(width / 4, height * 7 / 20, tileSize, difficulty, resources);
        //addFadingMessage(hinter.getX() + tileSize / 2, hinter.getY() - tileSize * 5 / 2, tileSize * 0.5f, MainThread.FPS * 4, "This button will use some juice to help you solve the word. It gets juice when you solve words quickly");

        solvedOverlay = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_4444);
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), solvedOverlay, (ContextCompat.getColor(ctx, R.color.multiplier_color_x3) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));

        showBanner();
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_STARTED, Analytics.UNSCRAMBLE);
    }

    public void newGame(){
        super.newGame();
        unscrambleRack.reset();
        unscrambleRack.setSolution();
        unscrambleRack.delay(Background.FRAMES_FOR_FULL_ANIM);
        wordsPlayed.clear();
        hinter.reset();
        scrambledTileSpawner.reset();
        timebar.setTotalTime(scrambledTileSpawner.getTimeGivenToSolve());
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_STARTED, Analytics.UNSCRAMBLE);
    }

    @Override
    public void onTimebarOut() {
        Log.d(TAG, "onTimebarOut called");
        //TODO : Finnesse this method to use remaining solve juice to help solve when timers out
        //while(hinter.hasJuice() && ! unscrambleRack.isSolved()) {
        //    unscrambleRack.help(tilesInPlay);
        //    hinter.useJuice();
        //}

        //if(unscrambleRack.isSolved()){
        //    spawnNewWord();
        //}else{
            //onGameOver();
            //onTimebarOutCalled = true;
        //}

        super.onTimebarOut();

    }

    @Override
    public void initializeGameOverScreen() {
        Log.d(TAG, "initializeGameOverScreen() called");
        //TODO :  Rewrite without using array
        Word [] wordsThisGame = new Word [wordsPlayed.size()];
        wordsPlayed.toArray(wordsThisGame);
        StatsDatabase.getInstance(ctx).insertDataToUnscramble(pointDisplayer.getPoints(), wordsPlayed);

        v2Tile [] tiles = unscrambleRack.getTilesInRack();
        for(int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null)
                continue;
            for (int j = 0; j < tilesInPlay.size(); j++) {
                boolean isAlreadyInArray = false;
                for (int k = 0; k < tiles.length; k++) {
                    if (tiles[k] == tilesInPlay.get(j)) {
                        isAlreadyInArray = true;
                        break;
                    }
                }
                if (!isAlreadyInArray) {
                    tiles[i] = tilesInPlay.get(j);
                }
            }
        }
        gameOverScreen = new UnscrambleGameOverScreen(tiles, unscrambleRack.getSolution(), pointDisplayer.getPoints(), wordsPlayed, width, height - adHeight, tileSize, ctx, resources);
        Analytics.sendGameEvent(FirebaseAnalytics.getInstance(ctx), Analytics.GAME_FINISHED, Analytics.UNSCRAMBLE);
    }

    @Override
    public void drawBackground(Canvas canvas) {
        unscrambleRack.draw(canvas);
    }

    @Override
    public void drawForeground(Canvas canvas) {
        for (v2Tile t : tilesInPlay)
            if (t != null)
                if (!t.isTouchable())
                    canvas.drawBitmap(solvedOverlay, t.getX(), t.getY(), null);

        scrambler.draw(canvas);
        hinter.draw(canvas);
        reject.draw(canvas);
    }

    @Override
    public boolean onGameplayTouchDown(int xCoor, int yCoor) {
        if(scrambler.wasTouched(xCoor, yCoor)){
            scrambledTileSpawner.shuffleTiles();
            return true;
        }
        if(hinter.wasTouched(xCoor, yCoor)){
            unscrambleRack.help(tilesInPlay);
            return true;
        }
        if(reject.wasTouched(xCoor, yCoor)){
            unscrambleRack.throwUnsolvedTilesIntoSpawner(scrambledTileSpawner);
            return true;
        }
        return false;
    }

    @Override
    public v2Tile tileTouchChecks(int xCoor, int yCoor) {
        v2Tile wasTouched = unscrambleRack.touchCheck(xCoor, yCoor);
        if(wasTouched ==  null)
            wasTouched = scrambledTileSpawner.touchCheck(xCoor, yCoor);
        return wasTouched;
    }

    @Override
    public void onGrabbedTileRelease(int xCoor, int yCoor, v2Tile grabbedTile) {
        if(Math.abs(xCoor - fingerDownX) < tileSize && Math.abs(yCoor - fingerDownY) < tileSize){
            soundManager.playRackSound();
            unscrambleRack.addTileToNextOpen(grabbedTile);
        }else if(unscrambleRack.isClose(grabbedTile)){
            unscrambleRack.addTileToClosest(grabbedTile);
        }else{
            scrambledTileSpawner.throwBackIntoSpawner(grabbedTile);
        }
    }

    @Override
    public void onGamePlayBegins() {
        unscrambleRack.setSolution();
        scrambledTileSpawner.spawn(unscrambleRack.getSolution());
        timebar.setTotalTime(scrambledTileSpawner.getTimeGivenToSolve());
        toStartTimer = true;
        super.onGamePlayBegins();
    }

    @Override
    public void onGameplayUpdate() {
        unscrambleRack.update();

        if(unscrambleRack.isSolved()){
            spawnNewWord();
        }
        if(toStartTimer && !scrambledTileSpawner.isAnimating()){
            timebar.start();
            toStartTimer = false;
        }
    }

    public void spawnNewWord(){
        pointDisplayer.addPoints(1);
        hinter.addSolveJuice(timebar.getTimeRemainingFraction());
        unscrambleRack.setSolution();
        scrambledTileSpawner.spawn(unscrambleRack.getSolution());
        timebar.setTotalTime(scrambledTileSpawner.getTimeGivenToSolve());
        toStartTimer = true;
        onTimebarOutCalled = false;
    }

}
