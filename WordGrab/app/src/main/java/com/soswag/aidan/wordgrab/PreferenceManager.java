package com.soswag.aidan.wordgrab;

import android.content.Context;
import android.content.SharedPreferences;

import com.soswag.aidan.wordgrab.Rack.v3RackGamePanel;

/**
 * Created by Aidan on 2016-08-02.
 */
public class PreferenceManager {

    public static int DIFFICULTY_MAX = 3;
    public static int DIFFICULTY_MIN = 1;

    private static PreferenceManager instance;

    private Context context;
    private boolean sound;
    private boolean music;
    private int gameMode;
    private int difficulty;

    private PreferenceManager(Context context){
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_filename), 0);

        difficulty = preferences.getInt(context.getString(R.string.preference_name_difficulty), 1);
        gameMode = preferences.getInt(context.getString(R.string.preference_name_game_mode), v3RackGamePanel.GAME_MODE);
        sound = preferences.getBoolean(context.getString(R.string.preference_name_sound), true);
        music = preferences.getBoolean(context.getString(R.string.preference_name_musicon), true);
    }

    public static PreferenceManager getInstance(Context context) {
        if(instance == null)
            instance = new PreferenceManager(context);
        return instance;
    }

    public boolean sound() {
        return sound;
    }

    public boolean music(){return music;}

    public int gameMode() {
        return gameMode;
    }

    public int difficulty() {
        return difficulty;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
        savePreferences();
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
        savePreferences();
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        savePreferences();
    }

    public void setMusic(boolean music){
        this.music = music;
        savePreferences();
    }

    private void savePreferences(){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_filename), 0);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(context.getString(R.string.preference_name_difficulty), difficulty);
        editor.putInt(context.getString(R.string.preference_name_game_mode), gameMode);
        editor.putBoolean(context.getString(R.string.preference_name_sound), sound);
        editor.putBoolean(context.getString(R.string.preference_name_musicon), music);

        editor.apply();
    }
}
