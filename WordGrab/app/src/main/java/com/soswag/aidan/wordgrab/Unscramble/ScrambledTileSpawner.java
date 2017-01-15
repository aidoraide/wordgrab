package com.soswag.aidan.wordgrab.Unscramble;

import android.content.res.Resources;
import android.util.Log;

import com.soswag.aidan.wordgrab.MyAnimation.SlideResizeAnimation;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-07-13.
 */
public class ScrambledTileSpawner {

    private static final String TAG = "My_ScrambledTileSpa";

    private int spawns = 0;
    private int tileSize;
    private int spaceBetweenTiles;
    private int y;
    private int width;

    private int difficulty;

    private v2Tile[] tilesThisRound;
    private int [] xCoors;

    private ArrayList<v2Tile> tilesInPlay;

    public ScrambledTileSpawner(int yToSpawn, int width, int tileSize, int difficulty, ArrayList<v2Tile> tilesInPlay, Resources resources){
        this.tileSize = tileSize;
        this.spaceBetweenTiles = tileSize / 12;
        this.y = yToSpawn;
        this.width = width;
        this.difficulty = difficulty;
        this.tilesInPlay = tilesInPlay;
        tilesThisRound = new v2Tile [0];
        xCoors = new int [0];

    }

    public void throwBackIntoSpawner(v2Tile toAdd){

        //Slide solvedTiles out in empty space back into the scrambled rack
        for(int i = 0; i < tilesThisRound.length; i++)
            if(tilesThisRound[i] == null) {
                //toAdd.setSlideAnimation(xCoors[i], y);
                toAdd.addAnimation(new SlideResizeAnimation(toAdd, xCoors[i], y, tileSize, 8, false));
                tilesThisRound[i] = toAdd;
                toAdd.resize(tileSize);
                Log.d(TAG, "Sliding tile back to default location");
                break;
            }

    }

    public void spawn(String randomWord){

        //Setup arrays and scramble the char array so that the solvedTiles will be in scrambled order
        tilesThisRound = new v2Tile [randomWord.length()];
        xCoors = new int [randomWord.length()];
        char [] toScramble = new char [randomWord.length()];
        for(int i = 0; i < toScramble.length; i++)
            toScramble[i] = randomWord.charAt(i);
        scramble(toScramble, 10);

        //Spawn solvedTiles, set slideAnimation destination and set their chars to the chars of the scrambled char array
        int xTracker = width / 2 - (tileSize + spaceBetweenTiles) * randomWord.length() / 2;
        for(int i = 0; i <  tilesThisRound.length; i++){
            xCoors[i] = xTracker;
            tilesThisRound[i] = new v2Tile(toScramble[i], width + i * tileSize, y, tileSize, false);
            tilesInPlay.add(tilesThisRound[i]);
            tilesThisRound[i].addAnimation(new SlideResizeAnimation(tilesThisRound[i], xTracker, y, tileSize, 8, false));
            xTracker += tileSize + spaceBetweenTiles;
        }

        spawns++;
    }

    public static <T> void scramble(T [] array, int iterations){
        for(int i = 0; i < iterations; i++){
            for(int j = 0; j < array.length; j++){
                int randomIndex = (int)(Math.random() * array.length);
                T copy = array[j];
                array[j] = array[randomIndex];
                array[randomIndex] = copy;
            }
        }
    }

    public void scramble(char [] array, int iterations){
        for(int i = 0; i < iterations; i++){
            for(int j = 0; j < array.length; j++){
                int randomIndex = (int)(Math.random() * array.length);
                char copy = array[j];
                array[j] = array[randomIndex];
                array[randomIndex] = copy;
            }
        }
    }

    public void reset(){
        spawns = 0;
        tilesThisRound = new v2Tile [0];
        xCoors = new int [0];
    }

    //Give one second per tile subtract 0.01 seconds per spawn per tile to a minimum of 0.5 seconds subtracted per tile
    public long getTimeGivenToSolve(){
        int correctedSpawns = spawns;
        if (spawns > 50)
            correctedSpawns = 50;

        return tilesThisRound.length * (5 - difficulty) * 1000000000L - correctedSpawns * 10000000L * tilesThisRound.length;
    }

    public int getSpawns(){return spawns;}

    public boolean isAnimating(){
        if(tilesThisRound != null)
            if(tilesThisRound[0] != null)
                return tilesThisRound[0].isAnimating();

        return true;
    }

    public v2Tile touchCheck(int xCoor, int yCoor){
        for(int i = 0; i < tilesThisRound.length; i++)
            if(tilesThisRound[i] != null)
                if(tilesThisRound[i].wasTouched(xCoor, yCoor)){
                    v2Tile toReturn = tilesThisRound[i];
                    tilesThisRound[i] = null;
                    return toReturn;
                }
        return null;
    }

    public void shuffleTiles(){
        Integer [] indecis = new Integer[tilesThisRound.length];
        for(int i = 0; i < indecis.length; i++)
            indecis[i] = i;
        scramble(indecis, 10);

        int x0 = width / 2 - (tileSize + spaceBetweenTiles) * tilesThisRound.length / 2;
        for(int i = 0; i < indecis.length; i++){
            if(tilesThisRound[i] != null){
                //TODO : CHANGE TO BETTER ANIMATION (CIRCULAR PATH MAYBE)
                tilesThisRound[i].addAnimation(new SlideResizeAnimation(tilesThisRound[i], x0 + (tileSize + spaceBetweenTiles) * indecis[i], y, tileSize, 8, false));
            }
        }
    }

}
