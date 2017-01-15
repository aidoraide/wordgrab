package com.soswag.aidan.wordgrab.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.Grid.WordGrid;
import com.soswag.aidan.wordgrab.Grid.v3GridGamePanel;
import com.soswag.aidan.wordgrab.PreferenceManager;
import com.soswag.aidan.wordgrab.Rack.v3RackGamePanel;
import com.soswag.aidan.wordgrab.Tile.v2Tile;
import com.soswag.aidan.wordgrab.Unscramble.v3UnscrambleGamePanel;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-06-22.
 */

public class StatsDatabase extends SQLiteOpenHelper {

    private static final String TAG = "My_StatsDatabase";

    private static final String DATABASE_NAME = "STATISTICS";
    private static final int DATABASE_VERSION = 1;

    //Column index identifiers
    private static final int GAME_NUM = 0;
    private static final int POINTS = 1;
    private static final int BLOB = 2;
    private static final int DIFFICULTY = 3;

    //For rack full game stats table
    private static final String RACK_STATS_TABLE_NAME = "RACK_STATISTICS";
    private static final String [] RACK_COL = {"GAME_NUMBER", "POINTS", "WORDLIST_DATA", "DIFFICULTY"};
    private static final String RACK_STATS_TABLE_CREATE =
            " CREATE TABLE " + RACK_STATS_TABLE_NAME +
                    " (" +
                    RACK_COL[GAME_NUM] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RACK_COL[POINTS] + " INTEGER, " +
                    RACK_COL[BLOB] + " BLOB, " +
                    RACK_COL[DIFFICULTY] + " INTEGER" +
                    ");";

    //For grid full game stats table
    private static final String GRID_STATS_TABLE_NAME = "GRID_STATISTICS";
    private static final String [] GRID_COL = {"GAME_NUMBER", "POINTS", "GRID_DATA", "DIFFICULTY"};
    private static final String GRID_STATS_TABLE_CREATE =
            "CREATE TABLE " + GRID_STATS_TABLE_NAME +
                    " (" +
                    GRID_COL[GAME_NUM] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GRID_COL[POINTS] + " INTEGER, " +
                    GRID_COL[BLOB] + " BLOB, " +
                    GRID_COL[DIFFICULTY] + " INTEGER" +
                    ");";

    //For all words
    private static final String UNSCRAMBLE_STATS_TABLE_NAME = "UNSCRAMBLE_STATISTICS";
    private static final String [] UNSCRAMBLE_COL = {"GAME_NUMBER", "POINTS", "WORDLIST_DATA", "DIFFICULTY"};
    private static final String UNSCRAMBLE_STATS_TABLE_CREATE =
            "CREATE TABLE " + UNSCRAMBLE_STATS_TABLE_NAME +
                    " (" +
                    UNSCRAMBLE_COL[GAME_NUM] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UNSCRAMBLE_COL[POINTS] + " INTEGER, " +
                    UNSCRAMBLE_COL[BLOB] + " BLOB, " +
                    UNSCRAMBLE_COL[DIFFICULTY] + " INTEGER " +
                    ");";

    private static StatsDatabase mInstance = null;

    private PreferenceManager preferenceManager;

    private StatsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.preferenceManager = PreferenceManager.getInstance(context);
    }

    public static StatsDatabase getInstance(Context context){
        if(mInstance == null)
            mInstance = new StatsDatabase(context);

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RACK_STATS_TABLE_CREATE);
        db.execSQL(GRID_STATS_TABLE_CREATE);
        db.execSQL(UNSCRAMBLE_STATS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertDataToRack(int gamePoints, ArrayList<Word> words){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        byte [] blob = wordListToBlob(words);

        contentValues.put(RACK_COL[POINTS], gamePoints);
        contentValues.put(RACK_COL[BLOB], blob);
        contentValues.put(RACK_COL[DIFFICULTY], preferenceManager.difficulty());

        boolean success = db.insert(RACK_STATS_TABLE_NAME, null, contentValues) != -1;
        db.close();
        return success;
    }

    public boolean insertDataToGrid(int gamePoints, v2Tile[][] gridTiles, int [][] multipliers){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        byte [] blob = new byte [WordGrid.GRID_SIZE * WordGrid.GRID_SIZE * 2];
        int blobCounter = 0;

        for(v2Tile [] tileSubArray : gridTiles)
            for(v2Tile tile: tileSubArray){
                if(tile == null)
                    blob[blobCounter++] = 0;
                else
                    blob[blobCounter++] = (byte)tile.getLetter();
            }

        for(int [] intArray : multipliers)
            for(int i : intArray){
                blob[blobCounter++] = (byte)i;
            }

        contentValues.put(GRID_COL[POINTS], gamePoints);
        contentValues.put(GRID_COL[BLOB], blob);
        contentValues.put(GRID_COL[DIFFICULTY], preferenceManager.difficulty());

        boolean success = db.insert(GRID_STATS_TABLE_NAME, null, contentValues) != -1;
        db.close();
        return success;
    }

    public boolean insertDataToUnscramble(int points, ArrayList<Word> words){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(UNSCRAMBLE_COL[POINTS], points);
        contentValues.put(UNSCRAMBLE_COL[BLOB], wordListToBlob(words));
        contentValues.put(UNSCRAMBLE_COL[DIFFICULTY], preferenceManager.difficulty());

        boolean success = db.insert(UNSCRAMBLE_STATS_TABLE_NAME, null, contentValues) != -1;
        db.close();
        return success;
    }

    public GameData getBestUnscrambleGame(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + UNSCRAMBLE_STATS_TABLE_NAME, null);

        int maxPoints = 0;
        int maxPointsRow = 0;
        while(cursor.moveToNext()){
            int gamePoints = cursor.getInt(POINTS);
            if(gamePoints > maxPoints){
                maxPoints = gamePoints;
                maxPointsRow = cursor.getPosition();
            }
        }
        cursor.moveToPosition(maxPointsRow);

        GameData bestUnscrambleGame = new GameData(
                blobToWordList(cursor.getBlob(BLOB))
                , cursor.getInt(POINTS)
                , cursor.getInt(DIFFICULTY)
                , cursor.getInt(GAME_NUM)
                , v3UnscrambleGamePanel.GAME_MODE);
        cursor.close();
        db.close();
        return bestUnscrambleGame;
    }

    public GameData getBestRackGame(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + RACK_STATS_TABLE_NAME, null);

        int maxPoints = 0;
        int maxPointsRow = 0;
        while(cursor.moveToNext()){
            int gamePoints = cursor.getInt(POINTS);
            if(gamePoints > maxPoints){
                maxPoints = gamePoints;
                maxPointsRow = cursor.getPosition();
            }
        }
        cursor.moveToPosition(maxPointsRow);

        GameData bestRackGame = new GameData(cursor, v3RackGamePanel.GAME_MODE);
        cursor.close();
        db.close();
        return bestRackGame;
    }

    public GameData getBestGridGame(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + GRID_STATS_TABLE_NAME, null);

        int maxPoints = 0;
        int maxPointsRow = 0;
        while(cursor.moveToNext()){
            int gamePoints = cursor.getInt(POINTS);
            if(gamePoints > maxPoints){
                maxPoints = gamePoints;
                maxPointsRow = cursor.getPosition();
            }
        }
        cursor.moveToPosition(maxPointsRow);

        GameData bestGridGame = new GameData(cursor, v3GridGamePanel.GAME_MODE);

        cursor.close();
        db.close();
        return bestGridGame;
    }

    public ArrayList<Word> blobToWordList(byte [] blob){

        ArrayList<Word> words = new ArrayList<>();

        for(int i = 0; i < blob.length;){

            //Get word length
            int wordLength = 0;
            while(blob[i] != 0){
                i += 2;
                wordLength++;
            }
            //Find the end of the word in the blob b
            while(i < blob.length)
                if(blob[i] != 0) {
                    break;
                }else {
                    i++;
                }

            //Move back to start of word and get true wordLength
            wordLength--;
            i -= wordLength * 2 + 4;

            //Make proper sized byte array
            byte [] word = new byte [wordLength * 2 + 4];
            for(int j = 0; j < word.length; j++, i++)
                word[j] = blob[i];

            words.add(new Word(word));

        }

        return words;
    }

    public byte [] wordListToBlob(ArrayList<Word> words){
        int size = 0;
        for(Word w : words){
            size += w.getWord().length();
            size += w.getMultipliers().length;
            size += 4;
        }

        byte [] blob = new byte [size];
        int blobIndex = 0;
        for(Word w : words) {
            byte[] buffer = w.asByteArray();
            for (byte b : buffer)
                blob[blobIndex++] = b;
        }
        return blob;
    }

    public void blobToGrid(byte [] blob, v2Tile [][] grid, int [][] multipliers){
        int size = v2Tile.getBitmap().getWidth();
        int byteCounter = 0;
        grid = new v2Tile[WordGrid.GRID_SIZE][WordGrid.GRID_SIZE];
        multipliers = new int [WordGrid.GRID_SIZE][WordGrid.GRID_SIZE];

        for(int i = 0; i < grid.length; i++)
            for(int j = 0; j < grid[i].length; j++, byteCounter++){
                if(blob[byteCounter] != 0)
                    grid[i][j] = new v2Tile((char)blob[byteCounter], -size, -size, size, true);
            }

        for(int i = 0; i < multipliers.length; i++)
            for(int j = 0; j < multipliers[i].length; j++, byteCounter++) {
                multipliers[i][j] = blob[byteCounter];
                if(grid[i][j] != null)
                    grid[i][j].setMultiplier(multipliers[i][j]);
            }

    }

    public class GameData{
        public ArrayList<Word> words;
        public v2Tile [][] grid;
        public int [][] multipliers;
        public int difficulty;
        public int points;
        public int gameNum;
        public int gameMode;

        GameData(Cursor cursor, int gameMode){
            this.gameMode = gameMode;
            this.points = cursor.getInt(POINTS);
            this.gameNum = cursor.getInt(GAME_NUM);
            this.difficulty = cursor.getInt(DIFFICULTY);
            if(gameMode == v3RackGamePanel.GAME_MODE || gameMode == v3UnscrambleGamePanel.GAME_MODE){
                words = blobToWordList(cursor.getBlob(BLOB));
            }else if(gameMode == v3GridGamePanel.GAME_MODE) {
                //return; //TODO : Create the commented method and remove return statement
                blobToGrid(cursor.getBlob(BLOB), grid, multipliers);
            }else
                Log.wtf(TAG, "GameMode = " + gameMode);
        }

        public GameData(ArrayList<Word> words, int points, int difficulty, int gameNum, int gameMode){
            this.words = words;
            this.points = points;
            this.difficulty = difficulty;
            this.gameNum = gameNum;
            this.gameMode = gameMode;
        }

        public GameData(v2Tile [][] grid, int points, int difficulty, int gameNum){
            this.grid = grid;
            this.points = points;
            this.difficulty = difficulty;
            this.gameNum = gameNum;
            this.gameMode = v3GridGamePanel.GAME_MODE;
        }
    }

}