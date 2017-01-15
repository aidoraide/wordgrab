package com.soswag.aidan.wordgrab.GamePanel;

import android.content.res.Resources;
import android.graphics.Canvas;

import com.soswag.aidan.wordgrab.MyAnimation.DelayAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.FadeAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.InGameAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.LinkedAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.SlideResizeAnimation;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-07-11.
 * Tileroster is a random tile spawner in which tiles are queued and moved down.
 * Only the bottom 3 tiles are touchable.
 * It has a "FeedButton" which causes the tiles to shift downwards before their scheduled time
 *
 * Note : This class contains some redundancies with tracking time via frames as well
 * as system time but this is a low priority change
 */
public class TileRoster {

    private v2Tile[][] tiles = new v2Tile[3][3];
    private ArrayList<v2Tile>  deadTiles = new ArrayList<>();
    private int tileSize;
    private int x, y;
    private int framesPerSpawn;
    private int frameOfLastSpawn;
    private FeedButton shift;
    private ArrayList<v2Tile> tilesInPlay;

    private Background background;

    private int frameCount = 0;
    private int buttonPressedFrame = -InGameAnimation.DEFAULT_LENGTH;

    public TileRoster(int x, int y, int tileSize, int framesPerSpawn, ArrayList<v2Tile> tilesInPlay, Background background, Resources resources){
        this.x = x;
        this.y = y;
        this.tileSize = tileSize;
        this.framesPerSpawn = framesPerSpawn;
        this.tilesInPlay = tilesInPlay;
        this.background = background;

        randomlyGenerateTiles();
        frameOfLastSpawn = Background.FRAMES_FOR_FULL_ANIM;
        shift = new FeedButton(x + tileSize * 2, y + tileSize * 7, tileSize, InGameAnimation.DEFAULT_LENGTH * 2, resources);
    }

    public void update(){
        shift.update();
        frameCount++;
        //Spawn new solvedTiles if it is time
        if(frameCount - frameOfLastSpawn > framesPerSpawn && background.isFinishedAnimating()){
            shiftTiles();
            shift.makeTemporarilyUntouchable();
        }

        for(int i = 0; i < deadTiles.size(); i++) {
            v2Tile currentTile = deadTiles.get(i);
            if (currentTile != null) {
                currentTile.update();
                if (currentTile.shouldRemove()) {
                    deadTiles.remove(i);
                    i--;
                }

            }
        }
    }

    public void draw(Canvas canvas){
        for(int i = 0; i < deadTiles.size(); i++)
            if(deadTiles.get(i) != null)
                deadTiles.get(i).draw(canvas);
        shift.draw(canvas);
    }

    public void shiftTiles(){

        for(int i = tiles.length - 1; i >= 0; i--) {

            int yToSlide = y + (i + 1) * (tileSize * 3 / 2);

            for (int j = 0; j < tiles[i].length; j++) {

                int xToSlide =  x + j * (tileSize * 3 / 2);

                if(i == 2 && tiles[i][j] != null){

                    deadTiles.add(tiles[i][j]);
                    int endSize = tileSize / 3;
                    tiles[i][j].addAnimation(new LinkedAnimation(
                            new SlideResizeAnimation(tiles[i][j], xToSlide, yToSlide + 5 * tileSize, endSize, InGameAnimation.DEFAULT_LENGTH * 6, true)
                            , new FadeAnimation(tiles[i][j], InGameAnimation.DEFAULT_LENGTH * 3)
                    ));
                    tilesInPlay.remove(tiles[i][j]);


                } else if(i == 1) {

                    int endSize = tileSize * 3 / 2;
                    tiles[i][j].clearAnimations();
                    tiles[i][j].addAnimation(new SlideResizeAnimation(tiles[i][j], xToSlide, yToSlide, endSize, InGameAnimation.DEFAULT_LENGTH, true));
                    tiles[i][j].setTouchable(true);
                    //tiles[i][j].addAnimOverwrite(new SlideResizeAnimation(tiles[i][j], xToSlide, yToSlide, endSize, InGameAnimation.DEFAULT_LENGTH, true));

                }else if (i < 1){
                    tiles[i][j].clearAnimations();
                    tiles[i][j].addAnimation(new SlideResizeAnimation(tiles[i][j], xToSlide, yToSlide, tiles[i][j].getSize(), InGameAnimation.DEFAULT_LENGTH, true));
                    //tiles[i][j].addAnimOverwrite(new SlideResizeAnimation(tiles[i][j], xToSlide, yToSlide, tiles[i][j].getSize(), InGameAnimation.DEFAULT_LENGTH, true));
                }

            }
        }

        for(int i = tiles.length - 1; i >= 0; i--) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (i == 0) {

                    int yToSpawn = y + i * (tileSize * 3 / 2);
                    int xToSpawn = x + j * (tileSize * 3 / 2);
                    tiles[i][j] = new v2Tile(xToSpawn, -tileSize, tileSize, true);
                    tiles[i][j].addAnimation(new SlideResizeAnimation(tiles[i][j], xToSpawn, yToSpawn, tileSize, InGameAnimation.DEFAULT_LENGTH, true));
                    tiles[i][j].setTouchable(false);
                    tilesInPlay.add(tiles[i][j]);

                } else
                    tiles[i][j] = tiles[i - 1][j];
            }
        }

        frameOfLastSpawn = frameCount;

    }

    public v2Tile touchCheck(int xCoor, int yCoor){
        int lastRow = tiles.length - 1;
        for (int j = 0; j < tiles[lastRow].length; j++)
            if(tiles[lastRow][j] != null)
                if(tiles[lastRow][j].wasTouched(xCoor, yCoor)) {
                    v2Tile toReturn = tiles[lastRow][j];
                    tiles[lastRow][j] = null;
                    return toReturn;
                }

        if(shift.wasTouched(xCoor, yCoor))
            if(frameCount - buttonPressedFrame >= InGameAnimation.DEFAULT_LENGTH && background.isFinishedAnimating()) {
                /*Set lastSpawn to 0, so that on the next update call, the tiles will be shifted
                * This prevent a concurrent modification exception caused by calling shiftTiles()
                * (which changes the values of a tile) while tiles are being drawn*/
                frameOfLastSpawn = 0;
            }

        return null;
    }

    public void reset(){
        frameCount = 0;
        randomlyGenerateTiles();
    }

    public void randomlyGenerateTiles(){
        boolean touchable [] = {false, false, true};
        for(int row = 0; row < tiles.length; row++) {

            int yToSpawn = y + row * (tileSize * 3 / 2);
            for (int column = 0; column < tiles[row].length; column++) {
                int xToSpawn = x + column * (tileSize * 3 / 2);
                tiles[row][column] = new v2Tile(xToSpawn, -tileSize, tileSize, true);
                tiles[row][column].setTouchable(touchable[row]);

                int delayTime = row * 2 * InGameAnimation.DEFAULT_LENGTH + column * InGameAnimation.DEFAULT_LENGTH;
                tiles[row][column].addAnimation(new DelayAnimation(tiles[row][column], delayTime));

                if(row == 2)
                    tiles[row][column].addAnimation(new SlideResizeAnimation(tiles[row][column], xToSpawn, yToSpawn, tileSize * 3 / 2, InGameAnimation.DEFAULT_LENGTH, true));
                else
                    tiles[row][column].addAnimation(new SlideResizeAnimation(tiles[row][column], xToSpawn, yToSpawn, tileSize, InGameAnimation.DEFAULT_LENGTH, true));

                tilesInPlay.add(tiles[row][column]);
            }
        }
    }
}
