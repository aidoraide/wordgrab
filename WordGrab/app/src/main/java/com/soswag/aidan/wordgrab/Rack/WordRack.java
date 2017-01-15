package com.soswag.aidan.wordgrab.Rack;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.GamePanel.CircleButton;
import com.soswag.aidan.wordgrab.MyAnimation.FadeAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.InGameAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.LinkedAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.SlideResizeAnimation;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.R;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

/**
 * Created by Aidan on 2016-05-04.
 */
public class WordRack {

    private static final String TAG = "My_WordRack";

    public static final int RACK_SIZE = 8;

    //Dimensions
    private int x, y;
    private int width, height;
    private int padding, rackPadding, gapOnBottom, tileSize, freeTileSize;

    //Keep track of which solvedTiles are in the rack
    private v2Tile[] rackedTiles = new v2Tile[8];
    private char [] rackedLetters = new char [8];
    private int [] scoreMultipliers = new int [8];
    private int pts;

    //Keep track of where the user is interested in placing a tile with the active segment
    private int activeSegment = -1;
    private Bitmap activeSegmentImage;

    //The bitmaps for the rack and the multipliers
    private Bitmap image;
    private Bitmap multipliers;
    private Bitmap [] multiplierHighlights = new Bitmap[5];

    //The button which handles validation of words
    private SubmitButton submitButton;

    private Context ctx;

    public WordRack(int screenWidth, int screenHeight, Resources resources, Context ctx){

        this.ctx = ctx;

        this.gapOnBottom = screenHeight / 20;
        this.freeTileSize = screenWidth / 8;
        this.rackPadding = screenWidth / 100;

        this.width = screenWidth - 2 * rackPadding;
        this.tileSize = (width - 9 * rackPadding) / 8;
        this.height = tileSize + 2 * rackPadding;

        this.x = rackPadding;
        this.y = screenHeight - this.height - gapOnBottom;

        for(int i = 0; i < rackedLetters.length; i++)
            rackedLetters[i] = ' ';

        long t = System.currentTimeMillis();
        this.submitButton = new SubmitButton(screenWidth / 2, screenHeight / 2, screenWidth, (int)(tileSize * (1.f + CircleButton.CHILD_FRACTION_OF_RADIUS_FOR_OUTLINE)), width, resources, ctx);
        System.out.println("It took " + (System.currentTimeMillis() - t) + "ms to construct the submitButton");

        //Create the bitmap for the rack
        image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas rackCanvas = new Canvas(image);

        //Set up paint for drawing the rack
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(ctx, R.color.rack_color));
        paint.setStyle(Paint.Style.FILL);
        //Rectangle for rack background
        MyDrawing.drawRoundRect(0, 0, width, height, tileSize / 6, paint, rackCanvas);

        for(int i = 0; i < rackedTiles.length; i++)
        MyDrawing.drawComplimentaryHole(v2Tile.getBitmap(), rackPadding + i * (rackPadding + tileSize),
                rackPadding,
                (i + 1) * (rackPadding + tileSize),
                rackPadding + tileSize,
                 ContextCompat.getColor(ctx, R.color.rack_color),
                image);

        //Create a small piece of the rack that is a different colour for highlighting which portion is active
        activeSegmentImage = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), activeSegmentImage, ContextCompat.getColor(ctx, R.color.active_segment_color));

        setRandomScoreMultipliers();
        pts= 0;

        for(int i = 0; i < multiplierHighlights.length; i++)
            multiplierHighlights[i] = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_4444);

        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[0], (ContextCompat.getColor(ctx, R.color.multiplier_color_x0) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[1], (ContextCompat.getColor(ctx, R.color.multiplier_color_x1) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[2], (ContextCompat.getColor(ctx, R.color.multiplier_color_x2) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[3], (ContextCompat.getColor(ctx, R.color.multiplier_color_x3) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));
        MyDrawing.bitmapAsNewColor(v2Tile.getBitmap(), multiplierHighlights[4], (ContextCompat.getColor(ctx, R.color.multiplier_color_x4) & ContextCompat.getColor(ctx, R.color.and_highlight)) | ContextCompat.getColor(ctx, R.color.or_highlight));

    }

    public void setSubmitButtonCoors(int x, int y){
        submitButton.setCoors(x, y);
    }

    public void setRandomScoreMultipliers(){
        for(int i = 0; i < scoreMultipliers.length; i++){
            int multiplier1 = (Math.random() > 0.75) ? 1 : 0;
            scoreMultipliers[i] = 1 + multiplier1 * (int)(Math.log(Math.random() * 900 + 100)/ Math.log(10)) + ((Math.random() > 0.5 && i > 3) ? 1: 0);
        }
        multipliers = Bitmap.createBitmap(width, gapOnBottom, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(multipliers);

        //Prepare the paint for drawing characters
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text shadow

        //Draw the letter in the centre of tile
        paint.setTextSize(width / 16);
        Rect bounds = new Rect();

        for(int i = 0; i < scoreMultipliers.length; i++) {
            if(scoreMultipliers[i] == 1)
                continue;

            paint.getTextBounds("×" + scoreMultipliers[i], 0, 2, bounds);
            int cx = (int) ((tileSize + rackPadding) * (i + 0.5) - bounds.width() / 2);// - 2 * padding;
            int cy = multipliers.getHeight() / 2  + bounds.height() / 2;

            if(scoreMultipliers[i] == 2) {
                paint.setColor(ContextCompat.getColor(ctx, R.color.multiplier_color_x2));
                paint.setShadowLayer(padding / 2, padding / 2, padding / 2, ContextCompat.getColor(ctx, R.color.multiplier_color_x2));
            }else if(scoreMultipliers[i] == 3) {
                paint.setColor(ContextCompat.getColor(ctx, R.color.multiplier_color_x3));
                paint.setShadowLayer(padding / 3, padding / 3, padding / 3, ContextCompat.getColor(ctx, R.color.multiplier_color_x3));
            }else if(scoreMultipliers[i] == 4) {
                paint.setColor(ContextCompat.getColor(ctx, R.color.multiplier_color_x4));
                paint.setShadowLayer(padding / 4, padding / 4, padding / 4, ContextCompat.getColor(ctx, R.color.multiplier_color_x4));
            }

            canvas.drawText("×" + scoreMultipliers[i], cx, cy, paint);
        }
    }

    public void throwTilesAway(){
        for(int i = 0; i < rackedTiles.length; i++)
            if(rackedTiles[i] != null){
                rackedTiles[i].addAnimation(new LinkedAnimation(
                        new FadeAnimation(rackedTiles[i], InGameAnimation.DEFAULT_LENGTH)
                        ,new SlideResizeAnimation(rackedTiles[i], rackedTiles[i].getX(), rackedTiles[i].getY() + 4 * tileSize, tileSize, InGameAnimation.DEFAULT_LENGTH * 2, true)
                ));
                rackedTiles[i] = null;
            }
        pts = 0;
        submitButton.reset();
    }

    public void addToActiveSegment(v2Tile incoming){
        boolean rackIsFull = true;
        for(v2Tile i : rackedTiles)
            if(i == null) {
                rackIsFull = false;
                break;
            }
        if(rackIsFull)
            unrack(activeSegment);

        //Add the tile to the rack
        addToIndex(incoming, activeSegment);
    }

    public void printRack(){
        //FOR DEBUGGING
        StringBuilder builder = new StringBuilder();
        builder.append("Now, ");
        for(int i = 0; i < rackedTiles.length; i++) {
            if (rackedTiles[i] != null)
                builder.append(rackedTiles[i].getLetter());
            else
                builder.append("_");
            builder.append( " ");
        }
        builder.append("is racked");
        Log.d(TAG, builder.toString());
    }

    //Shifts solvedTiles over and returns true
    //Or if rack is full returns false
    public boolean shiftIfNearestOccupied(v2Tile incoming){

        //Get the centre of the solvedTiles x coordinate
        int tileCentreX = incoming.getX() + incoming.getSize()/2;
        //Translate it over so it is relative to the width of the rack
        tileCentreX -= x;
        //Get the tile slot the piece is to be added to
        int tileSlot = tileCentreX * rackedTiles.length / width;
        if(tileSlot >= rackedTiles.length)
            tileSlot = rackedTiles.length - 1;
        if(tileSlot < 0)
            tileSlot = 0;

        boolean shifted = false;
        //If the slot is occupied try to shift solvedTiles over
        if(rackedTiles[tileSlot] != null){

            for(int i = 0; i < tileSlot; i++){
                if(rackedTiles[i] == null) {
                    shiftTiles(tileSlot, -1);
                    shifted = true;
                    break;
                }
            }

            if(!shifted){
                for(int i = rackedTiles.length - 1; i > tileSlot; i--){
                    if(rackedTiles[i] == null) {
                        shiftTiles(tileSlot, 1);
                        shifted = true;
                        break;
                    }
                }
            }

            //Update racked letters after shifting
            updateRackedLetters();

        }
        return shifted;
    }

    public void shiftTiles(){

        if(rackedTiles[activeSegment] != null){
            boolean shifted = false;

            //Look for an open spot to the left of the active segment
            for(int i = 0; i < activeSegment; i++){
                if(rackedTiles[i] == null) {
                    shiftTiles(activeSegment, -1);
                    shifted = true;
                    break;
                }
            }

            //If solvedTiles still have not been shifted look for an open spot to the right of the active segment
            if(!shifted){
                for(int i = rackedTiles.length - 1; i > activeSegment; i--){
                    if(rackedTiles[i] == null) {
                        shiftTiles(activeSegment, 1);
                        break;
                    }
                }
            }

            //Update racked letters after shifting
            updateRackedLetters();

        }

    }

    //Shifts the solvedTiles in the rack in the specified direction (-1 is left, 1 is right)
    public void shiftTiles(int spotToOpen, int direction){

        //Look for the first open space closest to the spot to open on direction side of it
        int nullSpace = -1;
        for(int i = spotToOpen + direction; i >= 0 && i < rackedTiles.length; i += direction){
            if(rackedTiles[i] == null) {
                nullSpace = i;
                break;
            }
        }

        //Shift solvedTiles to open space
        while(nullSpace != spotToOpen){

            rackedTiles[nullSpace] = rackedTiles[nullSpace - direction];
            rackedTiles[nullSpace].setSlideAnimation(getXFromRackIndex(nullSpace), y + rackPadding);
            nullSpace -= direction;
        }
        rackedTiles[nullSpace] = null;
    }

    //Remove tile by tile ID
    public void unrackTileById(int tileId){
        for(int i = 0; i < rackedTiles.length; i++){
            if(rackedTiles[i] != null)
                if(rackedTiles[i].getId() == tileId){
                    unrack(i);
                }
        }
    }

    public boolean submitButtonWasPressed(int xCoor, int yCoor){
        return submitButton.buttonWasPressed(xCoor, yCoor);
    }

    //Possibly useless method TEST IT
    public void update(){
    }

    public void draw(Canvas canvas){
        //Draw the rack
        canvas.drawBitmap(image, x, y, null);
        //Draw the multipliers
        canvas.drawBitmap(multipliers, x, y + height, null);

        submitButton.draw(canvas);
        submitButton.drawPointPreview(pts, canvas);
    }

    public void drawHighlights(Canvas canvas){
        for(int i = 0; i < scoreMultipliers.length; i++){
            int multiplier = scoreMultipliers[i];
            if(multiplier > 1)
                canvas.drawBitmap(multiplierHighlights[multiplier], getXFromRackIndex(i), y + rackPadding, null);
        }
    }

    public void drawActiveSegment(Canvas canvas){
        //Draw the highlighted piece if hovered over
        if(activeSegment >= 0){
            canvas.drawBitmap(activeSegmentImage, getXFromRackIndex(activeSegment), y + rackPadding, null);
        }
    }

    public void setActiveSegmentToNextOpen(){
        activeSegment = getFirstOpen();
    }

    private int getXFromRackIndex(int tileSlot){
        return x + rackPadding + tileSlot * (tileSize + rackPadding);
    }

    public void setActiveSegmentFromCoords(int xCoor, int yCoor){
        if(yCoor >= y && yCoor <= y + height)
            if(xCoor >= x && xCoor <= x + width){

                xCoor -= x;
                activeSegment = xCoor * rackedTiles.length / width;
                return;
            }
        activeSegment = -1;
    }

    public void setActiveSegment(v2Tile tile){
        int bottom = tile.getY() + tile.getSize();
        if((tile.getY() >= y && tile.getY() <= y + height)
                || (bottom >= y && bottom <= y + height)
                || (bottom >= y + height && tile.getY() <= y))
            if(tile.getX() + tile.getSize() >= x && tile.getX() <= x + width){
                int mid = tile.getX() + tile.getSize() / 2;
                mid -= x;
                activeSegment = mid * rackedTiles.length / width;
                if(activeSegment < 0)
                    activeSegment = 0;
                if(activeSegment >= rackedTiles.length)
                    activeSegment = rackedTiles.length - 1;
                return;
        }
        activeSegment = -1;
    }

    public void submit(){
        for (int i = 0; i < rackedTiles.length; i++) {
            if (rackedTiles[i] != null) {
                System.out.println("Unracking from submission");
                //rackedTiles[i].addAnimation(new ResizeAnimation(rackedTiles[i], tileSize * 2 / 3, InGameAnimation.DEFAULT_LENGTH));
                rackedTiles[i].submit(getXFromRackIndex(i), y - 2 * tileSize);
                unrack(i);
            }
        }
        setRandomScoreMultipliers();
    }

    public void unrack(int index){
        if(index > -1 && index < 8)
            if(rackedTiles[index] != null){
                //rackedTiles[index].unrack();
                rackedTiles[index].resize(freeTileSize);
                rackedTiles[index].setMultiplier(1);
                rackedTiles[index] = null;
                updateRackedLetters();
                printRack();
            }
    }

    public int getActiveSegment(){return activeSegment;}


    public void clear(){
        for(int i = 0; i < rackedTiles.length; i++) {
            unrack(i);
            rackedLetters[i] = ' ';
        }
        setRandomScoreMultipliers();
        activeSegment = -1;
        pts = 0;
        submitButton.reset();
    }

    public boolean addToNextOpen(v2Tile incoming){
        for(int i = 0; i < rackedTiles.length; i++){
            if(rackedTiles[i] == null) {
                addToIndex(incoming, i);
                return true;
            }
        }
        return false;
    }

    public void addToIndex(v2Tile incoming, int index){
        if(index > -1){
            rackedTiles[index] = incoming;
            rackedTiles[index].setMultiplier(scoreMultipliers[index]);
            incoming.addAnimation(new SlideResizeAnimation(incoming, getXFromRackIndex(index), y + rackPadding, tileSize, InGameAnimation.DEFAULT_LENGTH, false));
            updateRackedLetters();
            printRack();
        }
    }

    public int getFirstOpen(){
        for(int i = 0; i < rackedTiles.length; i++){
            if(rackedTiles[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void updateRackedLetters(){
        for(int i = 0; i < rackedLetters.length; i++){
            char tileLetter = (rackedTiles[i] == null) ? ' ' : (char)(rackedTiles[i].getLetter() + 32);
            rackedLetters[i] = tileLetter;
        }
        pts = new Word(rackedTiles, scoreMultipliers).getPts();
        submitButton.updateLetters(rackedLetters);
        printRack();
    }

    public int getTopOfButtonY(){
        return submitButton.getTopOfButtonY();
    }
    public int getY(){return y;}

    public boolean isSubmittable(){return submitButton.isSubmittable();}

    public void setActiveSegment(int i){activeSegment = i;}

    public Word getWord(){
        return new Word(rackedTiles, scoreMultipliers);
    }

    public v2Tile touchCheck(int xCoor, int yCoor){
        for(int i = 0; i < rackedTiles.length; i++){
            if(rackedTiles[i] != null)
                if(rackedTiles[i].wasTouched(xCoor, yCoor)){
                    v2Tile toReturn = rackedTiles[i];
                    unrack(i);
                    return toReturn;
                }
        }
        return null;
    }
}
