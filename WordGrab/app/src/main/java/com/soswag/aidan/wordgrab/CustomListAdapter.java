package com.soswag.aidan.wordgrab;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soswag.aidan.wordgrab.DB.StatsDatabase;
import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {

    private static final String TAG = "My_CustomListAdapter";

    private Context context;
    private int thisGamePoints;
    private Typeface typeface;
    private ArrayList<Word> thisGameList;
    private StatsDatabase.GameData bestGame;

    CustomListAdapter(Context context, int gamePoints, ArrayList<Word> thisGameList) {
        Log.d(TAG, "Constructor called");
        typeface = GameSelectionActivity.getTypeface();
        this.context = context;
        this.thisGameList = thisGameList;
        thisGamePoints = gamePoints;
        bestGame = StatsDatabase.getInstance(context).getBestRackGame();
    }

    @Override
    public int getCount() {
        return thisGameList.size();
    }

    @Override
    public Object getItem(int position) {
        return thisGameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return thisGameList.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Log.d(TAG, "getView(" + position + ") called");

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item, null);
            if(position < thisGameList.size()) {
                ImageView leftImg = (ImageView) convertView.findViewById(R.id.img_left);
                Bitmap tiles = Bitmap.createBitmap(leftImg.getWidth(), leftImg.getHeight(), Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(tiles);
                Rect rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
                v2Tile.drawWord(bestGame.words.get(position).getWord(), rect, canvas);
                leftImg.setImageBitmap(tiles);
                TextView textView = (TextView) convertView.findViewById(R.id.text_left);
                textView.setText("" + thisGameList.get(position).getPts());
                textView.setTypeface(typeface);
            }

            if(position < bestGame.words.size()) {
                ImageView rightImg = (ImageView) convertView.findViewById(R.id.img_right);
                Bitmap tiles = Bitmap.createBitmap(rightImg.getWidth(), rightImg.getHeight(), Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(tiles);
                Rect rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
                v2Tile.drawWord(bestGame.words.get(position).getWord(), rect, canvas);
                rightImg.setImageBitmap(tiles);
                TextView textView = (TextView) convertView.findViewById(R.id.text_left);
                textView.setText("" + bestGame.words.get(position).getPts());
                textView.setTypeface(typeface);
            }
        }

        return convertView;
    }



}
