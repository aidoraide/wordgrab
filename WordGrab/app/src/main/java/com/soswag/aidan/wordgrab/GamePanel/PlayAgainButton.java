package com.soswag.aidan.wordgrab.GamePanel;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.R;

/**
 * Created by Aidan on 2016-07-22.
 */
public class PlayAgainButton {

    int x, y;
    int width, height;
    Bitmap bitmap;

    public PlayAgainButton(int x, int y, int width, int height, Resources resources){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        bitmap = MyDrawing.bitmapResizer(
                BitmapFactory.decodeResource(resources, R.drawable.playgreen)
                , width, height);
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public boolean wasTouched(int xCoor, int yCoor){
        return (xCoor < x + width && xCoor > x && yCoor < y + height && yCoor > y);
    }
}
