package com.soswag.aidan.wordgrab.GamePanel;

import android.graphics.Canvas;

/**
 * Created by Aidan on 2016-08-03.
 */
public interface GameOverScreen {
    boolean playAgainButtonWasTouched(int x, int y);
    void draw(Canvas canvas);
    void update();
}
