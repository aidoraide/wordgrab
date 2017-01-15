package com.soswag.aidan.wordgrab.Unscramble;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.soswag.aidan.wordgrab.Dictionary.Dictionary;
import com.soswag.aidan.wordgrab.Dictionary.DictionarySetupThread;
import com.soswag.aidan.wordgrab.MyAnimation.DelayAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.SlideResizeAnimation;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.MyStatics.MyMath;
import com.soswag.aidan.wordgrab.R;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-07-14.
 */
public class UnscrambleRack {

    private final static String TAG = "My_UnscrambleRack";

    private int y;
    private int width;
    private int rackPeiceWidth;
    private int spaceBetweenPeices;
    private int spaceBetweenRackAndPeice;
    private int tileSize;

    private String solution;
    private v2Tile[] tilesInRack;
    private v2Tile lastAdded = null;
    private int [] xOfPeices;
    private Bitmap highlight;

    private double slideAnimProgress = 2.1;
    private final int ANIM_MIDPOINT;
    private int [] xWhenAnimStarts;
    private int [] xWhenAnimEnds;
    private boolean animMidpointSetupFlag;
    private int animationDelay = 0;

    private Dictionary dictionary;
    private int wordsGenerated = 0;
    private boolean solved = false;

    public UnscrambleRack(int y, int width, int tileSize, Resources resources, Context context){
        this.y = y;
        this.width = width;
        this.tileSize = tileSize;
        this.rackPeiceWidth = tileSize;
        this.spaceBetweenPeices = tileSize / 12;
        this.spaceBetweenRackAndPeice = tileSize / 15;
        ANIM_MIDPOINT = width / 2 - rackPeiceWidth / 2;

        dictionary = new Dictionary(resources);
        DictionarySetupThread setup = new DictionarySetupThread(dictionary, resources);
        setup.start();

        tilesInRack = new v2Tile[0];
        xOfPeices = new int [1];
        xOfPeices[0] = ANIM_MIDPOINT;

        highlight = Bitmap.createBitmap(tileSize * 11 / 10, tileSize * 11 / 10, Bitmap.Config.ARGB_4444);
        MyDrawing.drawBitmapAsMonoColour(v2Tile.getBitmap(), 0, 0, highlight.getWidth(), highlight.getHeight(), ContextCompat.getColor(context, R.color.multiplier_color_x3), highlight, 0.4f);
    }

    public void update(){

        if(slideAnimProgress <= 1){
            slideAnimProgress += 0.1;
            double function = Math.sin((slideAnimProgress - 0.5) * Math.PI) / 2 + 0.5;

            for(int i = 0; i < xOfPeices.length; i++){
                xOfPeices[i] = xWhenAnimStarts[i] + (int)((ANIM_MIDPOINT - xWhenAnimStarts[i]) * function);
            }
        }else if(slideAnimProgress <= 2){

            if(animationDelay > 0){
                animationDelay -= 1;
                return;
            }
            slideAnimProgress += 0.1;

            if(animMidpointSetupFlag){
                xOfPeices = new int [xWhenAnimEnds.length];
                animMidpointSetupFlag = false;
            }

            double adjustedAnimProgress = slideAnimProgress - 1;
            double function = Math.sin((adjustedAnimProgress - 0.5) * Math.PI) / 2 + 0.5;
            for(int i = 0; i < xOfPeices.length; i++){
                xOfPeices[i] = ANIM_MIDPOINT + (int)((xWhenAnimEnds[i] - ANIM_MIDPOINT) * function);
            }
        }
    }

    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setStrokeWidth(rackPeiceWidth * 0.05f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(0xff333333);
        for(int i = 0; i < tilesInRack.length && i < xOfPeices.length; i++){
            int x = xOfPeices[i];
            if(tilesInRack[i] != null)
                if(!tilesInRack[i].isTouchable()) {
                    float left = x - (highlight.getWidth() - tileSize) * 0.5f;
                    float top = y - highlight.getHeight();
                    canvas.drawBitmap(highlight, left, top, null);
                }
            canvas.drawLine(x + paint.getStrokeWidth() / 2, y, x + rackPeiceWidth - paint.getStrokeWidth() / 2, y, paint);
        }
    }

    public void setSolution(){

        solved = false;

        int maxWordLength = 4 + wordsGenerated / 6;
        int minWordLength = 2 + wordsGenerated / 6;

        if(minWordLength > 6)
            minWordLength = 6;
        if(maxWordLength > 8)
            maxWordLength = 8;

        String random = dictionary.getRandomWord(wordsGenerated);
        //Get random word for solution and set to upper case
        //TODO : MAKE A NOT DUCT TAPE SOLUTION
        while(random.length() > maxWordLength || random.length() < minWordLength) {
            random = dictionary.getRandomWord(wordsGenerated);
        }
            solution = "";
        for(int i = 0; i < random.length(); i++){
            solution = solution + (char)(random.charAt(i) - 32);
        }

        if (tilesInRack != null) {
            //Slide these tiles off the screen
            int delay = 0;
            int bonusDelay = 0;
            if(lastAdded != null)
                bonusDelay = lastAdded.getRemainingTicks();
            for (v2Tile t : tilesInRack) {
                if(t != null) {
                    if(t != lastAdded) {
                        t.addAnimation(new DelayAnimation(t, delay + bonusDelay));
                        t.addAnimation(new SlideResizeAnimation(t, -t.getSize() * tilesInRack.length, y - t.getSize() - spaceBetweenRackAndPeice, tileSize, 8, false));
                    }else{
                        t.addAnimation(new DelayAnimation(t, delay));
                        //Override the start coordinates so the second slide animation starts at the bottom
                        SlideResizeAnimation slide = new SlideResizeAnimation(t, -t.getSize() * tilesInRack.length, y - t.getSize() - spaceBetweenRackAndPeice, tileSize, 8, false);
                        slide.overrideStartCoords(t.getAnimEndPoint());
                        t.addAnimation(slide);
                    }

                    delay += 3;
                }
            }
        }
        tilesInRack = new v2Tile [solution.length()];

        //Copy positions for start of animation x positions
        xWhenAnimStarts = new int [xOfPeices.length];
        for(int i = 0; i < xOfPeices.length; i++){
            xWhenAnimStarts[i] = xOfPeices[i];
        }
        //Determine where the pieces will end up
        xWhenAnimEnds = new int [solution.length()];
        int xTracker = width / 2 - solution.length() * (rackPeiceWidth + spaceBetweenPeices) / 2;
        for(int i = 0; i < xWhenAnimEnds.length; i++){
            xWhenAnimEnds[i] = xTracker;
            xTracker += rackPeiceWidth + spaceBetweenPeices;
        }

        //Set the flag so that in update we can deal with having 2 differently sized arrays before processing
        animMidpointSetupFlag = true;
        slideAnimProgress = 0;

        wordsGenerated++;

    }

    public boolean hasCorrectSolution(){
        if(tilesInRack == null)
            return false;
        char [] charsOfTiles = new char [tilesInRack.length];
        boolean isGivenSolution = true;
        for(int i = 0; i < tilesInRack.length; i++){
            //Return false if null is found because we are only interested in solutions where all tiles are used
            if(tilesInRack[i] == null)
                return false;
            else{
                if(tilesInRack[i].getLetter() != solution.charAt(i))
                    isGivenSolution = false;
                charsOfTiles[i] = (char) (tilesInRack[i].getLetter() + 32);
            }
        }
        //Return true if they have the solution we have drawn randomly
        if(isGivenSolution)
            return true;
        else
        //Otherwise let dictionary decide if word is valid
        return dictionary.isValidWord(charsOfTiles);
    }

    public void addTileToNextOpen(v2Tile toAdd){
        for(int i = 0; i < tilesInRack.length; i++){
            if(tilesInRack[i] == null){
                System.out.println("Adding tile to next open (" + i + ")");
                addTileToIndex(toAdd, i);
                break;
            }
        }
    }

    public void addTileToClosest(v2Tile toAdd){
        int tileX = toAdd.getX() + toAdd.getSize() / 2;
        int distanceFromMid = tileX - width / 2;
        //Get the index relative to the middle
        int index;
        if(tilesInRack.length % 2 == 1)
            index = MyMath.roundToInt(1.0 * distanceFromMid / (rackPeiceWidth + spaceBetweenPeices));
        else
            index = (int)Math.floor(1.0 * distanceFromMid / (rackPeiceWidth + spaceBetweenPeices));
        //Adjust to absolute index
        index += tilesInRack.length / 2;
        if(index >= tilesInRack.length)
            index = tilesInRack.length - 1;
        else if(index < 0)
            index = 0;
        System.out.println("Adding tile to closest (" + index + ")");
        addTileToIndex(toAdd, index);
    }

    public void freeUpIndexIfOccupied(int index){

        if(tilesInRack[index] != null){
            boolean shifted = false;

            //Look for an open spot to the left of the index
            for(int i = 0; i < index; i++){
                if(tilesInRack[i] == null) {
                    shiftTiles(index, -1);
                    shifted = true;
                    break;
                }
            }

            //If tiles still have not been shifted look for an open spot to the right of the index
            if(!shifted){
                for(int i = tilesInRack.length - 1; i > index; i--){
                    if(tilesInRack[i] == null) {
                        shiftTiles(index, 1);
                        break;
                    }
                }
            }

        }

    }

    //Shifts the tiles in the rack in the specified direction (-1 is left, 1 is right)
    public void shiftTiles(int spotToOpen, int direction){

        //Look for the first open space closest to the spot to open on direction side of it
        int nullSpace = -1;
        for(int i = spotToOpen + direction; i >= 0 && i < tilesInRack.length; i += direction){
            if(tilesInRack[i] == null) {
                nullSpace = i;
                break;
            }
        }


        //TODO URGENT
        //Shift tiles to open space
        while(nullSpace != spotToOpen){

            if(tilesInRack[nullSpace] != null)
                if( ! tilesInRack[nullSpace].isTouchable()){
                    nullSpace -= direction;
                    continue;
                }

            //Find the distance between tiles if there is an already solved tile placed
            int distance = 1;
            for(int i = nullSpace - direction; i < tilesInRack.length && i >= 0; i -= direction){
                if(tilesInRack[i] == null)
                    break;
                if(tilesInRack[i].isTouchable())
                    break;
                distance++;
            }
                /*if(tilesInRack[i] != null) {
                    if ( ! tilesInRack[i].isTouchable())
                        distance++;
                    else break;
                }else break;*/

            tilesInRack[nullSpace] = tilesInRack[nullSpace - direction * distance];
            tilesInRack[nullSpace - direction * distance] = null;
            //tilesInRack[nullSpace].setSlideAnimation(getXOfIndex(nullSpace), y - tileSize - spaceBetweenRackAndPeice);
            tilesInRack[nullSpace].addAnimation(new SlideResizeAnimation(tilesInRack[nullSpace], getXOfIndex(nullSpace), y - tileSize - spaceBetweenRackAndPeice, tileSize, 8, false));
            nullSpace -= direction;
        }
        tilesInRack[nullSpace] = null;
    }

    public void addTileToIndex(v2Tile toAdd, int index){
        freeUpIndexIfOccupied(index);
        tilesInRack[index] = toAdd;
        //toAdd.setSlideAnimation(getXOfIndex(index), y - tileSize - spaceBetweenRackAndPeice);
        toAdd.addAnimation(new SlideResizeAnimation(toAdd, getXOfIndex(index), y - tileSize - spaceBetweenRackAndPeice, tileSize, 8, false));
        lastAdded = toAdd;
        solved = hasCorrectSolution();
        String log = "Now ";
        for(v2Tile t : tilesInRack)
            if(t == null)
                log += "_";
            else
                log += t.getLetter();

        log += ", is racked and solved = " + solved;
        Log.i(TAG, log);
    }

    private int getXOfIndex(int index){
        return xWhenAnimEnds[index];
    }

    public String getSolution(){return solution;}

    public void delay(int frames){
        animationDelay += frames;
    }

    public boolean isSolved(){return solved;}
    public boolean isClose(v2Tile t){
        int midY = t.getY() + t.getSize() / 2;
        return (midY < y + tileSize) && midY > y - tileSize;
    }

    public v2Tile touchCheck(int xCoor, int yCoor){
        for(int i = 0; i < tilesInRack.length; i++)
            if(tilesInRack[i] != null)
                if(tilesInRack[i].wasTouched(xCoor, yCoor)){
                    v2Tile toReturn = tilesInRack[i];
                    tilesInRack[i] = null;
                    return toReturn;
                }
        return null;
    }

    public void help(v2Tile [] possibleTiles){
        if(possibleTiles.length == 0)
            return;

        Log.i(TAG, "Helping solve for " + solution);

        //Find a random tile to show its solution
        v2Tile luckyTile = null;
        int random = (int)(Math.random() * possibleTiles.length);
        for(int i = random; i < possibleTiles.length; i++)
            if(possibleTiles[i].isTouchable())
                luckyTile = possibleTiles[i];

        if(luckyTile == null)
            for(int i = random - 1; i >= 0; i--)
                if(possibleTiles[i].isTouchable())
                    luckyTile = possibleTiles[i];

        try {

            for (int i = 0; i < solution.length(); i++) {
                assert luckyTile != null;
                Log.i(TAG, "Checking letter of " + solution.charAt(i));// + " and touchability of "+ i + " = " + tilesInRack[i].isTouchable());
                if (solution.charAt(i) == luckyTile.getLetter()) {
                    if(tilesInRack[i] != null)
                        if( ! tilesInRack[i].isTouchable())
                            continue;

                //Remove the tile from rack if it is there so other tiles are free to shift
                for(int j = 0; j < tilesInRack.length; j++){
                    if(tilesInRack[j] == luckyTile){
                        //If a tile is already in the right place, then just set it to untouchable
                        if(i == j){
                            Log.i(TAG, "LuckyTile " + luckyTile.getLetter() + " found in correct spot, " + i);
                            luckyTile.setTouchable(false);
                            return;
                        }
                        Log.i(TAG, "LuckyTile " + luckyTile.getLetter() + "found in rack at " + j + ", removing...");
                        tilesInRack[j] = null;
                        break;
                    }
                }
                    System.out.println("Adding " + luckyTile.getLetter() + " to " + i);
                    addTileToIndex(luckyTile, i);
                    luckyTile.setTouchable(false);
                    break;
                }
            }

        }catch (NullPointerException e){e.printStackTrace();}

    }

    public void help(ArrayList<v2Tile> tiles){
        v2Tile [] tilesArray = new v2Tile[tiles.size()];
        tiles.toArray(tilesArray);
        help(tilesArray);
    }

    public v2Tile [] getTilesInRack(){return tilesInRack;}

    public void reset(){
        wordsGenerated = 0;
    }

    public int getWordsGenerated() {
        return wordsGenerated;
    }

    public void throwUnsolvedTilesIntoSpawner(ScrambledTileSpawner spawner){
        for(int i = 0; i < tilesInRack.length; i++) {
            if (tilesInRack[i] != null)
                if (tilesInRack[i].isTouchable()) {
                    spawner.throwBackIntoSpawner(tilesInRack[i]);
                    tilesInRack[i] = null;
                }
        }
    }
}
