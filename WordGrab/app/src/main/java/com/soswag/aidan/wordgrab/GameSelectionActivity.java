package com.soswag.aidan.wordgrab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.soswag.aidan.wordgrab.GamePanel.SoundManager;
import com.soswag.aidan.wordgrab.Grid.v3GridGamePanel;
import com.soswag.aidan.wordgrab.Rack.v3RackGamePanel;
import com.soswag.aidan.wordgrab.Tile.v2Tile;
import com.soswag.aidan.wordgrab.Unscramble.v3UnscrambleGamePanel;

/**
 * Created by Aidan on 2016-06-11.
 */
public class GameSelectionActivity extends Activity {

    private static Activity activity;
    private static Typeface typeface;

    ImageButton [] gameSelectButtons;
    ImageButton difLess, difMore;
    ImageView difficultyDisplay;

    ImageButton play;
    TextView gameTitle, gameDescription;

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);

        SoundManager.getInstance(getApplicationContext()).cancelScheduleOff();

        System.out.println("******************ON CREATE GAME SELECTION******************");

        ctx = getApplicationContext();
        setContentView(R.layout.game_selection);

        gameSelectButtons = new ImageButton[3];
        gameSelectButtons[v3RackGamePanel.GAME_MODE] = (ImageButton) findViewById(R.id.gameselect_rack_button);
        gameSelectButtons[v3GridGamePanel.GAME_MODE] = (ImageButton) findViewById(R.id.gameselect_grid_button);
        gameSelectButtons[v3UnscrambleGamePanel.GAME_MODE] = (ImageButton) findViewById(R.id.gameselect_unscramble_button);
        difLess = (ImageButton) findViewById(R.id.game_select_difficulty_easier);
        difMore = (ImageButton) findViewById(R.id.game_select_difficulty_harder);

        gameTitle = (TextView) findViewById(R.id.game_select_mode_title_textview);
        gameDescription = (TextView) findViewById(R.id.game_select_mode_description_textview);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Oxygen-Bold.ttf");
        gameTitle.setTypeface(typeface);
        gameDescription.setTypeface(typeface);

        gameSelectButtons[v3RackGamePanel.GAME_MODE].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSelectButtonPressed(v3RackGamePanel.GAME_MODE);
            }
        });
        gameSelectButtons[v3GridGamePanel.GAME_MODE].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSelectButtonPressed(v3GridGamePanel.GAME_MODE);
            }
        });
        gameSelectButtons[v3UnscrambleGamePanel.GAME_MODE].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSelectButtonPressed(v3UnscrambleGamePanel.GAME_MODE);
            }
        });

        difLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficultyButtonPressed(-1);
            }
        });
        difMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficultyButtonPressed(1);
            }
        });

        play = (ImageButton)findViewById(R.id.game_select_play_button);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Play button pressed!");
                Intent intent = new Intent(getApplicationContext(), InGameActivity.class);
                startActivity(intent);
            }
        });

        difficultyDisplay = (ImageView)findViewById(R.id.game_select_difficulty_display);
        difficultyButtonPressed(0);
        gameSelectButtonPressed(PreferenceManager.getInstance(ctx).gameMode());

    }

    private void gameSelectButtonPressed(int gameMode){
        if(gameMode == v3RackGamePanel.GAME_MODE){

            gameSelectButtons[v3RackGamePanel.GAME_MODE].setImageResource(R.drawable.rack_button);
            gameSelectButtons[v3GridGamePanel.GAME_MODE].setImageResource(R.drawable.gridbuttoninactive);
            gameSelectButtons[v3UnscrambleGamePanel.GAME_MODE].setImageResource(R.drawable.unscramble_button_inactive);
            gameTitle.setText(R.string.rack_game_mode_title);
            gameDescription.setText(R.string.game_mode_description_rack);
        }else if(gameMode == v3GridGamePanel.GAME_MODE){

            gameSelectButtons[v3RackGamePanel.GAME_MODE].setImageResource(R.drawable.rack_button_inactive);
            gameSelectButtons[v3GridGamePanel.GAME_MODE].setImageResource(R.drawable.gridbutton);
            gameSelectButtons[v3UnscrambleGamePanel.GAME_MODE].setImageResource(R.drawable.unscramble_button_inactive);
            gameTitle.setText(R.string.grid_game_mode_title);
            gameDescription.setText(R.string.game_mode_description_grid);
        }else if(gameMode == v3UnscrambleGamePanel.GAME_MODE){

            gameSelectButtons[v3RackGamePanel.GAME_MODE].setImageResource(R.drawable.rack_button_inactive);
            gameSelectButtons[v3GridGamePanel.GAME_MODE].setImageResource(R.drawable.gridbuttoninactive);
            gameSelectButtons[v3UnscrambleGamePanel.GAME_MODE].setImageResource(R.drawable.unscramble_button);
            gameTitle.setText(R.string.unscramble_game_mode_title);
            gameDescription.setText(R.string.game_mode_description_unscramble);
        }

        PreferenceManager.getInstance(ctx).setGameMode(gameMode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundManager.getInstance(this).scheduleOff();
    }

    private void difficultyButtonPressed(int increment){
        int difficulty = PreferenceManager.getInstance(ctx).difficulty();
        difficulty += increment;
        if(difficulty < PreferenceManager.DIFFICULTY_MIN || difficulty > PreferenceManager.DIFFICULTY_MAX)
            return;

        Resources res = getResources();
        String [] difficultyTexts = res.getStringArray(R.array.difficulty_names);

        Bitmap backing;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        if(difficulty == PreferenceManager.DIFFICULTY_MIN)
            backing = BitmapFactory.decodeResource(res, R.drawable.difficulty_image_green, options);
        else if(difficulty == PreferenceManager.DIFFICULTY_MAX)
            backing = BitmapFactory.decodeResource(res, R.drawable.difficulty_image_purple, options);
        else
            backing = BitmapFactory.decodeResource(res, R.drawable.difficulty_image_blue, options);

        Canvas canvas = new Canvas(backing);
        v2Tile.drawWord(difficultyTexts[difficulty - 1], canvas.getClipBounds(), canvas);

        difficultyDisplay.setImageBitmap(backing);

        PreferenceManager.getInstance(ctx).setDifficulty(difficulty);
    }

    public static Activity getActivity(){return activity;}

    public static Typeface getTypeface(){return typeface;}

}

