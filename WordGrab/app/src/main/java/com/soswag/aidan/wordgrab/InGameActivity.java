package com.soswag.aidan.wordgrab;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.soswag.aidan.wordgrab.GamePanel.GamePanel;
import com.soswag.aidan.wordgrab.GamePanel.SoundManager;
import com.soswag.aidan.wordgrab.Grid.v3GridGamePanel;
import com.soswag.aidan.wordgrab.Rack.v3RackGamePanel;
import com.soswag.aidan.wordgrab.Tile.v2Tile;
import com.soswag.aidan.wordgrab.Unscramble.v3UnscrambleGamePanel;

/**
 * Created by Aidan on 2016-05-12.
 */
    public class InGameActivity extends Activity {

        private GamePanel gamePanel;
        private AdView adView;

    private final String MY_AD_UNIT_ID_BANNER = "ca-app-pub-4906246068355975/2667989243";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("In game activity created");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        SoundManager.getInstance(this).cancelScheduleOff();

        //TODO will work for now but should probably move away from static bitmaps
        if(v2Tile.getBitmap() == null)
            v2Tile.setBitmaps( metrics.widthPixels / 8, getResources(), this);

        // Create and load the AdView.
        adView = new AdView(this);
        adView.setAdUnitId(MY_AD_UNIT_ID_BANNER);
        adView.setAdSize(AdSize.SMART_BANNER);

        int gameMode = PreferenceManager.getInstance(getApplicationContext()).gameMode();
        if(gameMode == v3RackGamePanel.GAME_MODE) {
            //The GamePanel controls the entire game. It extends SurfaceView and implements SurfaceHolder.Callback
            gamePanel = new v3RackGamePanel(adView, this, metrics.widthPixels, metrics.heightPixels, getResources());
        }else if(gameMode == v3GridGamePanel.GAME_MODE){
            gamePanel = new v3GridGamePanel(adView, this, metrics.widthPixels, metrics.heightPixels, getResources());
        }else if(gameMode == v3UnscrambleGamePanel.GAME_MODE){
            gamePanel = new v3UnscrambleGamePanel(adView, this, metrics.widthPixels, metrics.heightPixels, getResources());
        }

        // Create a RelativeLayout as the main layout and add the gameView.
        RelativeLayout mainLayout = new RelativeLayout(this);
        mainLayout.addView(gamePanel);

        // Add adView to the bottom of the screen.
        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mainLayout.addView(adView, adParams);

        // Set the RelativeLayout as the main layout.
        setContentView(mainLayout);

    }


    @Override
    protected void onResume() {
        super.onResume();
        SoundManager.getInstance(this).cancelScheduleOff();
        System.out.println("Resuming game activity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Starting game activity");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("Restarting game activity");
    }

    @Override
    public void onBackPressed() {
        gamePanel.terminate();
        super.onBackPressed();
        System.out.println("Back pressed - game activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pausing game activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundManager.getInstance(this).scheduleOff();
        System.out.println("Stopping game activity");
    }


    }
