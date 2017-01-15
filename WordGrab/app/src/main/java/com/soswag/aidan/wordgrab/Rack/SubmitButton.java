package com.soswag.aidan.wordgrab.Rack;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.soswag.aidan.wordgrab.Dictionary.Dictionary;
import com.soswag.aidan.wordgrab.Dictionary.DictionarySetupThread;
import com.soswag.aidan.wordgrab.GameSelectionActivity;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.R;

/**
 * Created by Aidan on 2016-05-07.
 */
public class SubmitButton {

    private int x, y;
    private int radius;
    private boolean submittable;

    private Dictionary dictionary;

    private Bitmap imageSubmittable;
    private Bitmap imageInactive;
    private Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SubmitButton(int x, int y, int screenWidth, int radius, int widthOfWordRack, Resources resources, Context ctx){

        this.y = y;
        this.radius = radius;
        this.x = x;
        pointPaint.setTextSize(radius * 0.85f);
        pointPaint.setTypeface(GameSelectionActivity.getTypeface());
        pointPaint.setColor(0xff00ff83);

        submittable = false;

        dictionary = new Dictionary(resources);
        DictionarySetupThread setup = new DictionarySetupThread(dictionary, resources);
        setup.start();

        imageSubmittable = MyDrawing.bitmapResizer(BitmapFactory.decodeResource(resources, R.drawable.submit_button_active), radius * 2, radius * 2);
        imageInactive  = MyDrawing.bitmapResizer(BitmapFactory.decodeResource(resources, R.drawable.submit_button_inactive), radius * 2, radius * 2);

        /*Canvas submittableCanvas = new Canvas(imageSubmittable);
        Canvas inactiveCanvas = new Canvas(imageInactive);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        //Draw the circle when submittable
        paint.setColor(ContextCompat.getColor(ctx, R.color.submitbutton_submittable_color));
        submittableCanvas.drawCircle((float)x1, (float)radius, (float)radius, paint);
        submittableCanvas.drawCircle((float)x2, (float)radius, (float)radius, paint);
        paint.setColor(Color.argb(3, 255, 255, 255));
        for(int i = radius; i > 0; i--){
            submittableCanvas.drawCircle((float)x1, (float)radius, (float)i, paint);
            submittableCanvas.drawCircle((float)x2, (float)radius, (float)i, paint);
        }

        //Draw the circe when inactive
        paint.setColor(ContextCompat.getColor(ctx, R.color.submitbutton_not_submittable_color));
        inactiveCanvas.drawCircle((float)x1, (float)radius, (float)radius, paint);
        inactiveCanvas.drawCircle((float)x2, (float)radius, (float)radius, paint);
        paint.setColor(Color.argb(3, 255, 255, 255));
        for(int i = radius; i > 0; i--){
            inactiveCanvas.drawCircle((float)x1, (float)radius, (float)i, paint);
            inactiveCanvas.drawCircle((float)x2, (float)radius, (float)i, paint);
        }

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radius / 25);
        inactiveCanvas.drawCircle((float)x1, (float)radius, (float)radius - radius / 50, paint);
        inactiveCanvas.drawCircle((float)x2, (float)radius, (float)radius - radius / 50, paint);
        submittableCanvas.drawCircle((float)x1, (float)radius, (float)radius - radius / 50, paint);
        submittableCanvas.drawCircle((float)x2, (float)radius, (float)radius - radius / 50, paint);
*/
    }

    public void setCoors(int x, int y){this.x = x; this.y = y;}

    public boolean buttonWasPressed(int xCoor, int yCoor){
        if(yCoor >= y - radius && yCoor <= y + radius) {
            return ((xCoor <= x + radius && xCoor >= x - radius));
        }
        return false;
    }

    public void draw(Canvas canvas){
        if (submittable) {
            canvas.drawBitmap(imageSubmittable, x - radius, y - radius, null);
            pointPaint.setColor(0xff00ff83);
        }else {
            canvas.drawBitmap(imageInactive, x - radius, y - radius, null);
            pointPaint.setColor(0xffffffff);
        }
    }

    public void drawPointPreview(int pts, Canvas canvas){
        MyDrawing.drawTextCenteredAt(x, y, "+" + pts, pointPaint, canvas);
    }

    public void updateLetters(char [] rackLetters){

        System.out.print("Checking ");
        for(char c : rackLetters)
            System.out.print(c);
        System.out.println(" for validity");
        int i = 0;
        while(rackLetters[i] != ' ') {
            i++;
            if(i == rackLetters.length)
                break;
        }
        if(i < 2){
            System.out.println("Invalid because less than two letters found");
            submittable = false;
            return;
        }
        while(i < 8){
            if(rackLetters[i] != ' '){
                System.out.println("Invalid because gap found at " + (i - 1));
                submittable = false;
                return;
            }
            i++;
        }
        submittable = dictionary.isValidWord(rackLetters);
    }

    public int getTopOfButtonY(){
        return y - radius;
    }

    public boolean isSubmittable(){return submittable;}

    public int getMiddleOfButtonY(){return y + radius;}

    public void reset(){submittable = false;}

}
