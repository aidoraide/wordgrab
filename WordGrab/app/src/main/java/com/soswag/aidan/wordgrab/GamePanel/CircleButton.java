package com.soswag.aidan.wordgrab.GamePanel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;

/**
 * Created by Aidan on 2016-05-27.
 */
public class CircleButton {

    public static final float CHILD_FRACTION_OF_RADIUS_FOR_OUTLINE = 0.15f;

    int x, y;
    int radius;
    int color;
    boolean selected = false;
    boolean drawHighlightOnSelected = true;
    Bitmap image;
    Bitmap selectedImage;

    public CircleButton(int x, int y, int radius, int color){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;

        image = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas imageCanvas = new Canvas(image);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        imageCanvas.drawCircle(radius, radius, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(radius / 15);
        imageCanvas.drawCircle(radius, radius, radius - radius / 30, paint);
    }

    public CircleButton(int x, int y, int radius, Bitmap imageBitmap, Bitmap selectedImageBitmap){
        this.x = x;
        this.y = y;
        this.radius = radius;

        image = MyDrawing.bitmapResizer(imageBitmap, radius * 2, radius * 2);
        if(selectedImageBitmap != null)
            selectedImage = MyDrawing.bitmapResizer(selectedImageBitmap, radius * 2, radius * 2);

    }

    public boolean wasTouched(int xCoor, int yCoor){
        int distance = (int)Math.sqrt(Math.pow(xCoor - x, 2) + Math.pow(yCoor - y, 2));
        return (distance <= radius);
    }

    public void setDrawHighlight(boolean highlight){drawHighlightOnSelected = highlight;}

    public void draw(Canvas canvas){

        if(selected && drawHighlightOnSelected){
            Paint paint = new Paint();
            paint.setColor(Color.argb(100, 0, 0, 0));
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            canvas.drawCircle(x, y, radius * 11 / 10, paint);
        }
        if(selected && selectedImage != null)
            canvas.drawBitmap(selectedImage, x - radius, y - radius, null);
        else
            canvas.drawBitmap(image, x - radius, y - radius, null);
    }

    public int getColor(){return color;}

    public boolean isSelected(){return selected;}
    public void setSelected(boolean now){selected = now;}

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
