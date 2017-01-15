package com.soswag.aidan.wordgrab.Tile;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Aidan on 2016-05-04.
 */
public abstract class TouchableObject {

    protected double x, y;
    protected int size;
    protected boolean touchable = true;
    protected boolean visible = true;

    TouchableObject(int x,int y,int size){
        this.x = x;
        this.y = y;
        this.size = size;
        touchable = true;
    }

    private int toTrace = 0;

    public int getX() {
        return (int)x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getY() {return (int)y;}

    public void setY(double y) {
        this.y = y;
    }

    public Point getPoint(){return new Point(getX(), getY());}

    public abstract Point getAnimEndPoint();

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getSize(){
        return size;
    }

    public void cantTouchThis(){
        this.touchable = false;
    }

    public boolean wasTouched(int touchX, int touchY){

        return (touchX >= x - size / 4 && touchX <= x + size + size / 4 && touchY >= y - size / 4 && touchY <= y + size + size / 4) && touchable;

    }

    public Rect getRectangle(){

        return new Rect((int)x, (int)y, (int)x + size, (int)y + size);
    }

    public void resize(int newSize){
        this.size = newSize;
    }

    public boolean isTouchable() {
        return touchable;
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }
}
