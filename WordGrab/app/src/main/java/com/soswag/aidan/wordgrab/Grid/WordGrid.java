package com.soswag.aidan.wordgrab.Grid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;

import com.soswag.aidan.wordgrab.Dictionary.Dictionary;
import com.soswag.aidan.wordgrab.Dictionary.DictionarySetupThread;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.MyStatics.MyMath;
import com.soswag.aidan.wordgrab.R;
import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-06-14.
 */
public class WordGrid {

    public static final int GRID_SIZE = 9;
    public static final int CENTER = GRID_SIZE / 2;
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    //Dimensions
    private int x, y;
    private int width, height;
    private int padding, rackPadding, tileSize, freeTileSize;

    private v2Tile [] bestWord = new v2Tile[0];
    private int bestWordPoints = 0;

    //Keep track of which bestWordTiles are in the rack
    private v2Tile[][] rackedTiles = new v2Tile[GRID_SIZE][GRID_SIZE];
    private int [][] validityOf = new int [GRID_SIZE][GRID_SIZE];
    private int [][] scoreMultipliers = new int [GRID_SIZE][GRID_SIZE];
    private int points = 0;

    //Keep track of where the user is interested in placing a tile with the active segment
    private int activeSegmentX, activeSegmentY = -1;
    private Bitmap activeSegmentImage;

    //The bitmaps for the rack and the multipliers
    private Bitmap image;
    private Bitmap [] multiplierHighlights = new Bitmap [5];

    private ArrayList<int []> wordsCheckedThisRound = new ArrayList<int []>();

    private Dictionary dictionary;

    public WordGrid(int screenWidth, int screenHeight, Resources resources, Context ctx){

        this.freeTileSize = screenWidth / 11;
        this.rackPadding = screenWidth / 100;

        this.tileSize = (screenWidth * 23 / 25 - (GRID_SIZE + 1) * rackPadding) / GRID_SIZE;

        this.width = GRID_SIZE * (rackPadding + tileSize) + rackPadding;
        this.height = width;

        this.x = screenWidth / 2 - width / 2;
        this.y = screenHeight - height - x;

        dictionary = new Dictionary(resources);
        DictionarySetupThread setupThread = new DictionarySetupThread(dictionary, resources);
        setupThread.start();

        //Create the bitmap for the rack
        image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas gridCanvas = new Canvas(image);

        //Set up paint for drawing the rack
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        MyDrawing.drawRoundRect(0, 0, width, height, tileSize / 8, paint, gridCanvas);

        for(int i = 0; i < rackedTiles.length; i++)
            for(int j = 0; j < rackedTiles[i].length; j++)
                MyDrawing.drawComplimentaryHole(v2Tile.getBitmap(), rackPadding + i * (rackPadding + tileSize),
                        rackPadding + j * (rackPadding + tileSize),
                        (i + 1) * (rackPadding + tileSize),
                        (j + 1) * (rackPadding + tileSize),
                        0xff000000, image);

        activeSegmentImage = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), activeSegmentImage, ContextCompat.getColor(ctx, R.color.active_segment_color));
        activeSegmentY = activeSegmentX = -1;

        for(int i = 0; i < multiplierHighlights.length; i++)
            multiplierHighlights[i] = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_4444);

        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[0], (ContextCompat.getColor(ctx, R.color.multiplier_color_x0) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[1], (ContextCompat.getColor(ctx, R.color.multiplier_color_x1) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[2], (ContextCompat.getColor(ctx, R.color.multiplier_color_x2) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[3], (ContextCompat.getColor(ctx, R.color.multiplier_color_x3) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[4], (ContextCompat.getColor(ctx, R.color.multiplier_color_x4) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));

        setRandomScoreMultipliers(gridCanvas, ctx);
    }

    public void setRandomScoreMultipliers(Canvas rackCanvas, Context ctx){
        for(int i = 0; i < scoreMultipliers.length; i++)
            for(int j = 0; j < rackedTiles[i].length; j++){
                double random = Math.random();
                int pointBonus = 0;
                if(random > 0.95)
                    pointBonus = 1;
                else if(random > 0.85)
                    pointBonus = 2;
            scoreMultipliers[i][j] = 1 + pointBonus;
        }

        scoreMultipliers[0][0] = 4;
        scoreMultipliers[0][GRID_SIZE - 1] = 4;
        scoreMultipliers[GRID_SIZE - 1][0] = 4;
        scoreMultipliers[GRID_SIZE - 1][GRID_SIZE - 1] = 4;

        //Prepare the paint for drawing characters (score multipliers to the centre of each spot)
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(width / 16);
        Rect bounds = new Rect();

        for(int i = 0; i < scoreMultipliers.length; i++)
            for(int j = 0; j < rackedTiles[i].length; j++){
                if(scoreMultipliers[i][j] == 1)
                    continue;

                String s = "×" + scoreMultipliers[i][j];
                paint.getTextBounds(s, 0, s.length(), bounds);
                int cx = (int) ((tileSize + rackPadding) * (i + 0.5) - bounds.width() / 2);// - 2 * padding;
                int cy = (int) ((tileSize + rackPadding) * (j + 0.5) + bounds.height() / 2);

                if(scoreMultipliers[i][j] == 2) {
                    paint.setColor(ContextCompat.getColor(ctx, R.color.multiplier_color_x2));
                }else if(scoreMultipliers[i][j] == 3) {
                    paint.setColor(ContextCompat.getColor(ctx, R.color.multiplier_color_x3));
                }else if(scoreMultipliers[i][j] == 4) {
                    paint.setColor(ContextCompat.getColor(ctx, R.color.multiplier_color_x4));
                }

                rackCanvas.drawText(s, cx, cy, paint);
            }
    }

    public void addTileToIndex(v2Tile tile, int xIndex, int yIndex){
        if(xIndex > -1 && xIndex < GRID_SIZE && yIndex > -1 && yIndex < GRID_SIZE)
            if(rackedTiles[xIndex][yIndex] == null) {
                rackedTiles[xIndex][yIndex] = tile;
                tile.resize(tileSize);
                tile.setSlideAnimation(getXCoorFromGridX(xIndex), getYCoorFromGridY(yIndex));
                checkWordValidityAround(CENTER, CENTER);
            }
    }

    public void addToActiveSegment(v2Tile incoming){
        addTileToIndex(incoming, activeSegmentX, activeSegmentY);
    }

    public void unrack(int xIndex, int yIndex){
        if(xIndex > -1 && xIndex < GRID_SIZE && yIndex > -1 && yIndex < GRID_SIZE)
            if(rackedTiles[xIndex][yIndex] != null){
                System.out.println("Unracking from index");
                //rackedTiles[xIndex][yIndex].unrack();
                rackedTiles[xIndex][yIndex].resize(freeTileSize);
                rackedTiles[xIndex][yIndex] = null;
                //checkWordValidityAround(xIndex, yIndex);
                checkWordValidityAround(CENTER, CENTER);
            }
    }

    //Remove tile by tile ID
    public void unrackTileById(int tileId){
        for(int i = 0; i < rackedTiles.length; i++)
            for(int j = 0; j < rackedTiles[i].length; j++){
            if(rackedTiles[i][j] != null)
                if(rackedTiles[i][j].getId() == tileId){
                    unrack(i, j);
                }
        }
    }

    public void setActiveSegment(int xCoor, int yCoor){
        if(xCoor > x && xCoor < x + width)
            if(yCoor > y && yCoor < y + height){
                activeSegmentX = getGridXFromCoordinate(xCoor);
                activeSegmentY = getGridYFromCoordinate(yCoor);
                if(rackedTiles[activeSegmentX][activeSegmentY] != null){
                    final int spotCenterX = getXCoorFromGridX(activeSegmentX) + tileSize / 2;
                    final int spotCenterY = getYCoorFromGridY(activeSegmentY) + tileSize / 2;
                    final int rightIncrement = xCoor > spotCenterX ? 1 : -1;
                    final int downIncrement = yCoor > spotCenterY ? 1 : -1;
                    while(activeSegmentY + downIncrement >= 0
                            && activeSegmentY + downIncrement < rackedTiles.length
                            && activeSegmentX + rightIncrement >= 0
                            && activeSegmentX + rightIncrement < rackedTiles.length){
                        if(rackedTiles[activeSegmentX][activeSegmentY + downIncrement] == null){
                            activeSegmentY += downIncrement;
                            break;
                        }
                        if (rackedTiles[activeSegmentX + rightIncrement][activeSegmentY] == null){
                            activeSegmentX += rightIncrement;
                            break;
                        }
                        if (rackedTiles[activeSegmentX + rightIncrement][activeSegmentY + downIncrement] == null){
                            activeSegmentX += rightIncrement;
                            activeSegmentY += downIncrement;
                            break;
                        }
                        activeSegmentX += rightIncrement;
                        activeSegmentY += downIncrement;
                    }
                    if(activeSegmentX < 0 || activeSegmentX >= rackedTiles.length
                            || activeSegmentY < 0 || activeSegmentY >= rackedTiles.length)
                        activeSegmentY = activeSegmentX = -1;
                }
                return;
            }
        activeSegmentX = activeSegmentY = -1;
    }

    public void setActiveSegment(v2Tile tile){
        setActiveSegment(tile.getX() + tile.getSize() / 2, tile.getY() + tile.getSize() / 2);
    }

    public int getGridXFromCoordinate(int xCoor){
        xCoor -= x + rackPadding;
        return xCoor / (tileSize + rackPadding);
    }
    public int getGridYFromCoordinate(int yCoor){
        yCoor -= y + rackPadding;
        return yCoor / (tileSize + rackPadding);
    }

    public int getXCoorFromGridX(int xIndex){
        return x + rackPadding + xIndex * (rackPadding + tileSize);
    }

    public int getYCoorFromGridY(int yIndex){
        return y + rackPadding + yIndex * (rackPadding + tileSize);
    }

    public v2Tile[] getBestWord(){return bestWord;}

    public void checkWordValidityAround(int xIndex, int yIndex){

        //Clear all validity information
        wordsCheckedThisRound.clear();
        for(int i = 0; i < validityOf.length; i++)
            for(int j = 0; j < validityOf[i].length; j++){
                validityOf[i][j] = 0;
            }

        //If the center is null, the whole board is invalid
        if(rackedTiles[xIndex][yIndex] == null)
            return;

        //This checks validity of the whole board
        checkForNewValidWordsAround(xIndex, yIndex);

        //Calculate the points on the board
        double points = 0;
        for(int i = 0; i < GRID_SIZE; i++)
            for(int j = 0; j < GRID_SIZE; j++)
                if(rackedTiles[i][j] != null)
                    points += rackedTiles[i][j].getValue() * Word.getDifficulty() * validityOf[i][j];

        this.points = MyMath.roundToInt(points);
    }

    public void checkForNewValidWordsAround(int xIndex, int yIndex){

        //Check any horizontal words
        int xIndexCopy = xIndex;
        while (xIndexCopy > 0) {
            xIndexCopy--;
            if (rackedTiles[xIndexCopy][yIndex] == null) {
                xIndexCopy++;
                break;
            }
        }
        checkValidityOfWordAt(xIndexCopy, yIndex, 1, 0);

        //Check any vertical words
        int yIndexCopy = yIndex;
        while (yIndexCopy > 0) {
            yIndexCopy--;
            if (rackedTiles[xIndex][yIndexCopy] == null) {
                yIndexCopy++;
                break;
            }
        }
        checkValidityOfWordAt(xIndex, yIndexCopy, 0, 1);
    }

    //Checks if a word with its first letter located at xIndex, yIndex and travels horizontally if xIncrement = 1 or vertically if yIncrement = 1
    public void checkValidityOfWordAt(int xIndex, int yIndex, int xIncrement, int yIncrement){

        //CHECK IF THIS WORD HAS BEEN CHECKED IN THIS ROUND OF INSPECTION
        for(int i = 0; i < wordsCheckedThisRound.size(); i++)
            if(wordsCheckedThisRound.get(i)[0] == xIndex
                    && wordsCheckedThisRound.get(i)[1] == yIndex
                    && wordsCheckedThisRound.get(i)[2] == xIncrement
                    && wordsCheckedThisRound.get(i)[3] == yIncrement)
                //Return if we have already checked this word
                return;

        //Mark this word as checked
        int [] checked = {xIndex, yIndex, xIncrement, yIncrement};
        wordsCheckedThisRound.add(checked);
        //System.out.println("Words checked = " + wordsCheckedThisRound.size());

        char [] letters = new char [GRID_SIZE];
        int letterCounter = 0;
        while(xIndex < GRID_SIZE && yIndex < GRID_SIZE){
            if(rackedTiles[xIndex][yIndex] == null)
                break;
            letters[letterCounter] = (char)(rackedTiles[xIndex][yIndex].getLetter() + 32);
            xIndex += xIncrement;
            yIndex += yIncrement;
            letterCounter++;
        }
        char [] toCheck = new char [letterCounter];
        for(int i = 0; i < letterCounter; i++)
            toCheck[i] = letters[i];
        if(dictionary.isValidWord(toCheck)) {
            //Take note of all valid words (for point calculation)
            markWord(xIndex - (xIncrement * letterCounter), yIndex - (yIncrement * letterCounter), xIncrement, yIncrement, letterCounter);
            //Check all words that connect to this one if this word is valid
            checkConnectingWords(xIndex - (xIncrement * letterCounter), yIndex - (yIncrement * letterCounter), xIncrement, yIncrement, letterCounter);
        }
    }

    //Add one to the validity of all spots of a valid word
    public void markWord(int xIndex, int yIndex, int xIncrement, int yIncrement, int wordLength){
        v2Tile [] tilesInWord = new v2Tile[wordLength];
        int [] multipliers = new int[wordLength];
        double points = 0;
        for(int i = 0; i < wordLength; i++){
            validityOf[xIndex + i * xIncrement][yIndex + i * yIncrement]++;
            tilesInWord[i] = rackedTiles[xIndex + i * xIncrement][yIndex + i * yIncrement];
            multipliers[i] = scoreMultipliers[xIndex + i * xIncrement][yIndex + i * yIncrement];
            points += tilesInWord[i].getValue() * Word.getDifficulty();
        }
        if(points > bestWordPoints){
            bestWord = tilesInWord;
            bestWordPoints = (int)points;
        }
    }

    //To check the validity of all connecting words
    public void checkConnectingWords(int xIndex, int yIndex, int xIncrement, int yIncrement, int wordLength){
        for(int i = 0; i < wordLength; i++){

            //xToInspect and yToInspect are the spots directly to the left of vertical words or directly above horizontal words
            int xToInspect = xIndex + (i * xIncrement) - yIncrement;
            int yToInspect = yIndex + (i * yIncrement) - xIncrement;

            //Move back to grid if we are off of it
            if(xToInspect < 0)
                xToInspect = 0;
            else if(yToInspect < 0)
                yToInspect = 0;

            //We backtrack until we find the first letter of the word
            for (int j = 0; j <= (xToInspect * yIncrement) + (yToInspect * xIncrement); j++){
                int xi = xToInspect - (j * yIncrement);
                int yi = yToInspect - (j * xIncrement);
                if(rackedTiles[xi][yi] == null){
                    //If we find an open space, we go back one spot and check the validity of this word
                    xi += yIncrement;
                    yi += xIncrement;
                    checkValidityOfWordAt(xi, yi, yIncrement, xIncrement);
                    break;

                }else if((xi == 0 && yIncrement == 1) || (yi == 0 && xIncrement == 1)){
                    //If we reach the edge of the board we have found the first letter of the word, so check its validity
                    checkValidityOfWordAt(xi, yi, yIncrement, xIncrement);
                    break;
                }
            }
        }
    }

    // TODO : MAYBE USELESS
    public void checkForValidityAroundHole(int xIndex, int yIndex){
        if(xIndex < GRID_SIZE - 1)
            if(rackedTiles[xIndex + 1][yIndex] != null)
                checkValidityOfWordAt(xIndex + 1, yIndex, 1, 0);
        if(yIndex < GRID_SIZE - 1)
            if(rackedTiles[xIndex][yIndex + 1] != null)
                checkValidityOfWordAt(xIndex, yIndex + 1, 0, 1);

        checkForNewValidWordsAround(xIndex, yIndex);
    }

    public void update(){

    }
    public void draw(Canvas canvas){
        canvas.drawBitmap(image, x, y, null);
        canvas.drawBitmap(multiplierHighlights[3], getXCoorFromGridX(CENTER), getYCoorFromGridY(CENTER), null);
        if(activeSegmentX > -1 && activeSegmentY > -1){
            canvas.drawBitmap(activeSegmentImage, getXCoorFromGridX(activeSegmentX), getYCoorFromGridY(activeSegmentY), null);
        }
        //drawValidity(canvas);

    }

    public void drawValidity(Canvas canvas){
        for(int i = 0; i < GRID_SIZE; i++)
            for(int j = 0; j < GRID_SIZE; j++) {

                //Show which bestWordTiles count for no points
                if(validityOf[i][j] == 0 && rackedTiles[i][j] != null)
                    canvas.drawBitmap(multiplierHighlights[0], getXCoorFromGridX(i), getYCoorFromGridY(j), null);

                //Show which bestWordTiles count for points
                for (int k = 0; k < validityOf[i][j]; k++)
                    canvas.drawBitmap(multiplierHighlights[scoreMultipliers[i][j]], getXCoorFromGridX(i), getYCoorFromGridY(j), null);
            }
    }
    public void clear(){}

    public int getY(){return y;}
    public int getActiveSegmentX(){return activeSegmentX;}
    public int getActiveSegmentY(){return activeSegmentY;}
    public int getPoints(){return points;}

    public v2Tile touchCheck(int xCoor, int yCoor){
        for(int i = 0; i < rackedTiles.length; i++){
            for(int j = 0; j < rackedTiles[i].length; j++) {
                if (rackedTiles[i][j] != null)
                    if (rackedTiles[i][j].wasTouched(xCoor, yCoor)) {
                        v2Tile toReturn = rackedTiles[i][j];
                        unrack(i, j);
                        return toReturn;
                    }
            }
        }
        return null;
    }

    public int[][] getScoreMultipliers() {
        return scoreMultipliers;
    }

    public v2Tile[][] getRackedTiles() {
        return rackedTiles;
    }
}
