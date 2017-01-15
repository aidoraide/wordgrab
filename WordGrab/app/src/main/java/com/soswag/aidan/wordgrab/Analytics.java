package com.soswag.aidan.wordgrab;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Aidan on 2016-09-05.
 */
public class Analytics {

    public static final String GAME_STARTED = "game_started";
    public static final String GAME_FINISHED = "game_finished";

    public static final String RACK = "RACK";
    public static final String GRID = "GRID";
    public static final String UNSCRAMBLE = "UNSCRAMBLE";

    public static void sendGameEvent(FirebaseAnalytics analytics, final String gameEvent, String gameMode){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, gameEvent);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, gameMode);
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
