package com.soswag.aidan.wordgrab.GamePanel;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.soswag.aidan.wordgrab.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Aidan on 2016-06-03.
 */
public class SoundManager {

    private static SoundManager instance;

    private final static String TAG = "My_SoundManager";

    private final static int MS_OF_DROP = 29900;
    private final static long MAX_MS_TO_CHANGE_ACTIVITY = 800L;

    private MediaPlayer music;
    private MediaPlayer scaleUp;
    private MediaPlayer click;
    private MediaPlayer pop;
    private MediaPlayer error;
    private boolean on = false;
    private boolean musicOn = true;
    private Context ctx;

    private Timer timer;
    private TimerTask timerTask;
    private boolean cancelled = false;

    private SoundManager(Context context){
        this.ctx = context;
        music = MediaPlayer.create(ctx, R.raw.menu_music);
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion");
                if(on && musicOn) {
                    Log.d(TAG, "Seeking to " + MS_OF_DROP);
                    mp.seekTo(MS_OF_DROP);
                    mp.start();
                }
            }
        });
        scaleUp = MediaPlayer.create(ctx, R.raw.scale_up);
        click = MediaPlayer.create(ctx, R.raw.click);
        pop = MediaPlayer.create(ctx, R.raw.pop);
        error = MediaPlayer.create(ctx, R.raw.error);
        on = true;
    }

    public static SoundManager getInstance(Context context){
        if(instance == null)
            instance = new SoundManager(context);
        return instance;
    }

    public void setOn(boolean on){
        this.on = on;
        if(on) {
            if(!music.isPlaying() && musicOn) {
                playMusic();
            }
        }
        else{
            if(music.isPlaying())
                music.stop();
        }
    }

    public void setMusicOn(boolean on){
        this.musicOn = on;
        if(on) {
            if(!music.isPlaying() && musicOn) {
                playMusic();
            }
        }
        else{
            if(music.isPlaying())
                music.stop();
        }
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public boolean isOn() {
        return on;
    }

    public void playScaleUp(){
        if(on){
            if(scaleUp.isPlaying()) {
                MediaPlayer temp = MediaPlayer.create(ctx, R.raw.scale_up);
                temp.start();
            }else
                scaleUp.start();
        }
    }

    public void playGrabSound(){
        if(on){
            if(click.isPlaying()){
                MediaPlayer temp = MediaPlayer.create(ctx, R.raw.click);
                temp.start();
            }else
                click.start();
        }
    }

    public void playRackSound(){
        if(on) {
            if(pop.isPlaying()){
                MediaPlayer temp = MediaPlayer.create(ctx, R.raw.pop);
                temp.start();
            }else
                pop.start();
        }
    }

    public void playErrorSound(){
        System.out.println("playErrorSound() called");
        if(on) {
            if(error.isPlaying()){
                MediaPlayer temp = MediaPlayer.create(ctx, R.raw.error);
                temp.start();
            }else
                error.start();
        }
    }

    public void playMusic(){
        if(on && musicOn){
            music = MediaPlayer.create(ctx, R.raw.menu_music);
            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion");
                    if(on && musicOn) {
                        Log.d(TAG, "Seeking to " + MS_OF_DROP);
                        mp.seekTo(MS_OF_DROP);
                        mp.start();
                    }
                }
            });
            music.start();
        }
    }

    public void scheduleOff(){
        Log.d(TAG, "Scheduling sound off in " + MAX_MS_TO_CHANGE_ACTIVITY + "ms");
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(!cancelled)
                    setOn(false);
                cancelled = false;
            }
        };
        timer.schedule(timerTask, MAX_MS_TO_CHANGE_ACTIVITY);
    }

    public void cancelScheduleOff(){
        cancelled = true;
        timer = null;
        timerTask = null;
    }

}
