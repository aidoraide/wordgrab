package com.soswag.aidan.wordgrab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.soswag.aidan.wordgrab.GamePanel.SoundManager;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

public class MainActivity extends Activity {

    private static final String TAG = "My_MainActivity";

    //ImageButton play, options;
    private Context context;
    private Resources resources;

    private PreferenceManager preferenceManager;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        resources = getResources();
        preferenceManager = PreferenceManager.getInstance(context);
        soundManager = SoundManager.getInstance(context);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.title);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        ImageView title = (ImageView) findViewById(R.id.title_screen_title);
        title.setImageBitmap(MyDrawing.decodeSampledBitmapFromResource(getResources(), R.drawable.wordgrab, metrics.widthPixels * 8 / 10, metrics.heightPixels /4));

        ImageButton play = (ImageButton)findViewById(R.id.title_screen_play_button);
        play.setImageBitmap(MyDrawing.decodeSampledBitmapFromResource(getResources(), R.drawable.playpurple, metrics.widthPixels * 6 / 10, metrics.heightPixels /8));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Play button pressed!");
                Intent intent = new Intent(getApplicationContext(), GameSelectionActivity.class);
                startActivity(intent);
            }
        });

        ImageButton soundButton = (ImageButton)findViewById(R.id.sound_button);
        boolean sound = preferenceManager.sound();
        if(sound){
            soundButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.soundon));
        }else
            soundButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.soundoff));
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundButtonPressed();
            }
        });

        SoundManager.getInstance(context).setOn(sound);

        boolean music = preferenceManager.music();

        soundManager.setMusicOn(music);

        ImageButton musicButton = (ImageButton)findViewById(R.id.music_button);
        if(music){
            musicButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.musicon));
        }else
            musicButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.musicoff));
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicButtonPressed();
            }
        });

        //TODO Static Setup For Tile, Word...
        v2Tile.setBitmaps( metrics.widthPixels / 8, getResources(), context);

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resuming main activity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Starting main activity");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("Restarting main activity");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("Back pressed - main activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pausing main activity");

    }

    @Override
    protected void onStop() {
        super.onStop();
        soundManager.scheduleOff();
        System.out.println("Stopping main activity");
    }

    public void soundButtonPressed(){
        boolean sound = !preferenceManager.sound();
        preferenceManager.setSound(sound);
        ImageButton soundButton = (ImageButton)findViewById(R.id.sound_button);
        if(sound){
            soundButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.soundon));
        }else
            soundButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.soundoff));
        soundManager.setOn(sound);
    }

    public void musicButtonPressed(){
        boolean music = !preferenceManager.music();
        preferenceManager.setMusic(music);
        ImageButton soundButton = (ImageButton)findViewById(R.id.music_button);
        if(music){
            Log.d(TAG, "Music ON!");
            soundButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.musicon));
        }else {
            Log.d(TAG, "Music OFF!");
            soundButton.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.musicoff));
        }
        soundManager.setMusicOn(music);
    }
}
