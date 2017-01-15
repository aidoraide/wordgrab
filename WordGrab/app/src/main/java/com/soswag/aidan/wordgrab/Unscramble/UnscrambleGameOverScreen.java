package com.soswag.aidan.wordgrab.Unscramble;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;

import com.soswag.aidan.wordgrab.DB.StatsDatabase;
import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.GamePanel.GameOverScreen;
import com.soswag.aidan.wordgrab.GamePanel.PlayAgainButton;
import com.soswag.aidan.wordgrab.MyAnimation.DelayAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.InGameAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.SlideResizeAnimation;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.R;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-07-21.
 */
public class UnscrambleGameOverScreen implements GameOverScreen{

    int tilesY;
    int x0, y0, inlayWidth, inlayHeight;
    v2Tile[] solvedTiles;
    String heading;
    Rect headingRect;
    String words;
    Rect wordsRect;
    String thisGame;
    Rect thisGameRect;
    int thisGameNumberY;
    String bestGame;
    Rect bestGameRect;
    int bestGameNumberY;

    private Context context;

    Paint textPaint;

    PlayAgainButton playAgainButton;

    Bitmap backboard;

    public UnscrambleGameOverScreen(v2Tile [] tiles, String solution, int thisGamePoints, ArrayList<Word> wordsPlayed, int width, int height, int tileSize, Context context, Resources resources){

        this.context = context;
        StatsDatabase.getInstance(context).insertDataToUnscramble(thisGamePoints, wordsPlayed);
        StatsDatabase.GameData bestGameData = StatsDatabase.getInstance(context).getBestUnscrambleGame();

        System.out.println("GO Screen being made with " + tiles.length +" solvedTiles. Solution is " + solution);
        for(v2Tile t : tiles)
            if(t != null)
                System.out.print(t.getLetter() + ", ");
        else
            System.out.print("NULL, ");
        System.out.println();

        x0 = width / 20;
        y0 = height / 20;
        inlayHeight = height * 15 / 20;
        inlayWidth = width * 9 / 10;

        tilesY = y0 + inlayHeight * 7 / 40; //USED TO BE 3 / 20
        int [] tileX = new int [tiles.length];

        int spaceBetweenTiles = tileSize * 11 / 10;
        int xTracker = width / 2 - spaceBetweenTiles * tiles.length / 2;
        for(int i = 0; i < tiles.length; i++, xTracker += spaceBetweenTiles){
            tiles[i].clearAnimations();
            tiles[i].addAnimation(new DelayAnimation(tiles[i], InGameAnimation.DEFAULT_LENGTH));
            tiles[i].addAnimation(new SlideResizeAnimation(tiles[i], xTracker, tilesY + spaceBetweenTiles, tileSize, InGameAnimation.DEFAULT_LENGTH, false));
            tileX[i] = xTracker;
        }

        boolean [] hasBeenRepositioned = new boolean[tiles.length];
        v2Tile [] orderedTiles = new v2Tile[tiles.length];
        for(int i = 0; i < solution.length(); i++) {
            System.out.print(i);
            for (int j = 0; j < tiles.length; j++) {
                if(tiles[j].getLetter() == solution.charAt(i) && !hasBeenRepositioned[j]){
                    hasBeenRepositioned[j] = true;
                    orderedTiles[i] = tiles[j];
                    System.out.print(" : " + tiles[j].getLetter() + " to x = " + tileX[i]);
                    orderedTiles[i].addAnimation(new DelayAnimation(orderedTiles[i], InGameAnimation.DEFAULT_LENGTH * (i + 1)));
                    orderedTiles[i].addAnimation(new SlideResizeAnimation(orderedTiles[i], tileX[i], tilesY, tileSize, InGameAnimation.DEFAULT_LENGTH, false));
                    break;
                }
            }
            System.out.println();
        }
        this.solvedTiles = orderedTiles;

        for(v2Tile t : solvedTiles)
            if(t != null)
                System.out.print(t.getLetter() + ", ");
            else
                System.out.print("NULL, ");
        System.out.println();

        heading = "SOLUTION";
        headingRect = new Rect(inlayWidth / 10, inlayHeight / 20, 9 * inlayWidth / 10, 3 * inlayHeight / 20);
        words = "WORDS";
        wordsRect = new Rect(inlayWidth / 10, 7 * inlayHeight / 20, 9 * inlayWidth / 10, 9 * inlayHeight / 20);
        thisGame = "THIS GAME";
        thisGameRect = new Rect(inlayWidth / 10, 10 * inlayHeight / 20, 9 * inlayWidth / 10, 23 * inlayHeight / 40);
        bestGame = "BEST GAME";
        bestGameRect = new Rect(inlayWidth / 10, 15 * inlayHeight / 20, 9 * inlayWidth / 10, 33 * inlayHeight / 40);
        thisGameNumberY = (thisGameRect.bottom + bestGameRect.top) / 2;
        bestGameNumberY = (bestGameRect.bottom + 19 * inlayHeight / 20) / 2;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize((bestGameRect.top - thisGameRect.bottom) / 2);

        backboard = Bitmap.createBitmap(inlayWidth, inlayHeight, Bitmap.Config.ARGB_4444);
        Canvas backboardCanvas = new Canvas(backboard);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(context, R.color.game_over_screen_innerlay_color));
        backboardCanvas.drawRect(0, 0, inlayWidth, inlayHeight, paint);

        v2Tile.drawWord(heading, headingRect, backboardCanvas);
        v2Tile.drawWord(words, wordsRect, backboardCanvas);
        v2Tile.drawWord(thisGame, thisGameRect, backboardCanvas);
        v2Tile.drawWord(bestGame, bestGameRect, backboardCanvas);

        MyDrawing.drawTextCenteredAt(inlayWidth / 2, thisGameNumberY, String.valueOf(thisGamePoints), textPaint, backboardCanvas);
        MyDrawing.drawTextCenteredAt(inlayWidth / 2, bestGameNumberY, String.valueOf(bestGameData.points), textPaint, backboardCanvas);

        playAgainButton = new PlayAgainButton(x0 + inlayWidth / 10, y0 + inlayHeight * 41 / 40, 8 * inlayWidth / 10, height - inlayHeight / 40 - (y0 + inlayHeight * 41 / 40), resources);

    }

    private static int count = 0;
    public void update(){
        for(v2Tile t : solvedTiles) {
            if (t != null) {
                t.update();
            }
        }

        count++;
    }

    public void draw(Canvas canvas){
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(context, R.color.game_over_screen_overlay_color));
        canvas.drawRect(0, 0, w, h, paint);

        canvas.drawBitmap(backboard, x0, y0, null);

        for(v2Tile t : solvedTiles)
            if(t != null)
                t.draw(canvas);

        playAgainButton.draw(canvas);
    }

    public boolean playAgainButtonWasTouched(int xCoor, int yCoor){
        return playAgainButton.wasTouched(xCoor, yCoor);
    }

}
