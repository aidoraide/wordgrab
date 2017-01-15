package com.soswag.aidan.wordgrab;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.View;
import android.widget.ListView;

import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.GamePanel.GameOverScreen;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-08-08.
 */
public class GameOverList implements GameOverScreen {

    View view;
    ListView list;
    CustomListAdapter adapter;

    public GameOverList(int gamePoints, ArrayList<Word> words){
        Activity activity = GameSelectionActivity.getActivity();
        //view = activity.findViewById(R.id.scrollscreen);
        //list = (ListView) view.findViewById(R.id.list_view);
        adapter = new CustomListAdapter(activity.getApplicationContext(), gamePoints, words);
        list.setAdapter(adapter);
    }

    @Override
    public boolean playAgainButtonWasTouched(int x, int y) {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        view.draw(canvas);
    }

    @Override
    public void update() {

    }

}
