package com.soswag.aidan.wordgrab.Tile;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;

import com.soswag.aidan.wordgrab.Dictionary.Word;
import com.soswag.aidan.wordgrab.MyAnimation.AnimationQueue;
import com.soswag.aidan.wordgrab.MyAnimation.DelayAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.InGameAnimation;
import com.soswag.aidan.wordgrab.MyAnimation.SlideResizeAnimation;
import com.soswag.aidan.wordgrab.MyStatics.MyDrawing;
import com.soswag.aidan.wordgrab.R;

/**
 * Created by Aidan on 2016-05-04.
 * TODO : Make method to draw points that has correct colour and multiplier
 */
public class v2Tile extends TouchableObject {

    private static final String TAG = "My_v2Tile";

    private static int TILE_DEFAULT_SIZE = 200;
    public static double GRAVITY = TILE_DEFAULT_SIZE / 80;

    private static int count = 0;
    private final int id;

    private static Bitmap [] image = new Bitmap[27];
    private static int [] colours = new int[4];
    private static Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap scaledImage;
    private char letter;
    private int value;
    private boolean showPoints;
    private int multiplier = 1;

    private AnimationQueue animationQueue;

    public v2Tile(int x, int y, int size, boolean showPoints){
        super(x, y, size);
        count++;
        this.id = count;
        if(x < 315){
            System.out.println("CONSTRUCTING TILE w X = " + x);
            new Exception().printStackTrace();
        }
        this.letter = getRandomLetter();
        this.value = valueFromLetter(letter);
        this.showPoints = showPoints;

        if(size != TILE_DEFAULT_SIZE){
            resizeBitmaps();
        }

        animationQueue = new AnimationQueue(this);

    }

    public v2Tile(char c, int x, int y, int size, boolean showPoints){
        super(x, y, size);
        count++;
        this.id = count;
        this.letter = c;
        this.value = valueFromLetter(letter);
        this.showPoints = showPoints;

        if(size != TILE_DEFAULT_SIZE){
            resizeBitmaps();
        }

        animationQueue = new AnimationQueue(this);

        System.out.println("Spawning  " + letter + " to x = " + x + ", y = " + y);

    }

    public void setMultiplier(int multiplier){
        this.multiplier = multiplier;
    }

    public char getRandomLetter(){
        int random = (int)(Math.random() * 100);

        if(random < 12)
            return 'E';
        else if(random < 21)
            return 'A';
        else if (random < 30)
            return 'I';
        else if(random < 38)
            return 'O';
        else if(random < 44)
            return 'N';
        else if(random < 50)
            return 'R';
        else if(random < 56)
            return 'T';
        else if(random < 60)
            return 'L';
        else if(random < 64)
            return 'S';
        else if(random < 68)
            return 'U';
        else if(random < 72)
            return 'D';
        else if(random < 75)
            return 'G';
        else if(random < 77)
            return 'B';
        else if(random < 79)
            return 'C';
        else if(random < 81)
            return 'M';
        else if(random < 83)
            return 'P';
        else if(random < 85)
            return 'F';
        else if(random < 87)
            return 'H';
        else if(random < 89)
            return 'V';
        else if(random < 91)
            return 'W';
        else if(random < 93)
            return 'Y';
        else if(random < 94)
            return 'K';
        else if(random < 95)
            return 'J';
        else if(random < 96)
            return 'X';
        else if(random < 97)
            return 'Q';
        else
            return 'Z';
    }

    public static void setBitmaps(int size, Resources res, Context ctx){
        TILE_DEFAULT_SIZE = size;

        Bitmap alphabet = BitmapFactory.decodeResource(res, R.drawable.alphabet);
        Rect destination = new Rect(0,0, alphabet.getHeight(), alphabet.getHeight());
        for (int i = 0; i < image.length; i++) {

            image[i] = Bitmap.createBitmap(alphabet.getHeight(), alphabet.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(image[i]);
            canvas.drawBitmap(alphabet, new Rect(i * alphabet.getHeight(), 0, (i + 1) * alphabet.getHeight(), alphabet.getHeight()), destination, null);
            image[i] = MyDrawing.bitmapResizer(image[i], size, size);

        }

        colours = new int [5];
        colours[0] = ContextCompat.getColor(ctx, R.color.multiplier_color_x0);
        colours[1] = ContextCompat.getColor(ctx, R.color.multiplier_color_x1);
        colours[2] = ContextCompat.getColor(ctx, R.color.multiplier_color_x2);
        colours[3] = ContextCompat.getColor(ctx, R.color.multiplier_color_x3);
        colours[4] = ContextCompat.getColor(ctx, R.color.multiplier_color_x4);

    }

    private void resizeBitmaps(){
        scaledImage = MyDrawing.bitmapResizer(image[letter - 'A'], size, size);
    }

    public static int valueFromLetter(char c){
        switch(c){
            case 'A':
                return 1;
            case 'B':
                return 2;
            case 'C':
                return 3;
            case 'D':
                return 2;
            case 'E':
                return 1;
            case 'F':
                return 4;
            case 'G':
                return 2;
            case 'H':
                return 4;
            case 'I':
                return 1;
            case 'J':
                return 7;
            case 'K':
                return 5;
            case 'L':
                return 1;
            case 'M':
                return 3;
            case 'N':
                return 1;
            case 'O':
                return 1;
            case 'P':
                return 3;
            case 'Q':
                return 10;
            case 'R':
                return 1;
            case 'S':
                return 1;
            case 'T':
                return 1;
            case 'U':
                return 1;
            case 'V':
                return 4;
            case 'W':
                return 4;
            case 'X':
                return 8;
            case 'Y':
                return 4;
            case 'Z':
                return 10;
            default:
                return 0;
        }

    }

    public void draw(Canvas canvas){
        if(! visible)
            return;

        Paint filter = animationQueue.getPaint();
        if(filter != null)
            pointPaint.setAlpha(filter.getAlpha());
        else pointPaint.setAlpha(255);
        //filter.setAntiAlias(true);

        int i = letter - 'A';
        if(image[i].getWidth() != size){
            //canvas.drawBitmap(scaledImage, (int)x, (int)y, filter);
            canvas.drawBitmap(image[i], new Rect(0, 0, image[i].getWidth(), image[i].getHeight()), getRectangle(), pointPaint);
        }else {
            canvas.drawBitmap(image[i], (int) x, (int) y, pointPaint);
        }
        if(showPoints) {
            //pointPaint.setAlpha(filter.getAlpha());
            drawPoint(multiplier, canvas);

        }

    }

    public void update(){
        animationQueue.tick();
    }

    public void submit(int midwayX, int midwayY){
        cantTouchThis();
        addAnimation(new SlideResizeAnimation(this, midwayX, midwayY, size, 8, false));
        addAnimation(new DelayAnimation(this, 20));
        addAnimation(new SlideResizeAnimation(this, -size*3, -size*3, size, 8, false));
    }

    public void addAnimation(InGameAnimation animation){
        animationQueue.add(animation);
    }

    public void clearAnimations(){animationQueue.clear();}

    /*public void addAnimOverwrite(SlideResizeAnimation animation){
        animationQueue.addOverwrite(animation);
    }

    public void addLinkedAnimations(InGameAnimation [] animations){
        animationQueue.addLinkedAnimations(animations);
    }

    public void addLinkedOverwrite(InGameAnimation [] animations){
        animationQueue.addLinkedOverwrite(animations);
    }*/

    public void setSlideAnimation(int targetX, int targetY) {

        addAnimation(new SlideResizeAnimation(this, targetX, targetY, size, InGameAnimation.DEFAULT_LENGTH, false));

    }

    @Override
    public void resize(int newSize){
        this.size = newSize;
        if(size == image[letter - 'A'].getWidth())
            scaledImage = null;
    }

    public static Bitmap getBitmap(){
        for(int i = 0; i < image.length; i++)
            if(image[i] != null)
                return image[i];
        return null;
    }

    //NOTE : ONLY use with ALL-CAPS words and no non-letters except spaces
    private static final double MAX_ROTATE_DEG = 16;
    private static final double MIN_ROTATE_DEG = 7;
    public static void drawWord(String word, Rect destination, Canvas canvas){

        int tileSizeFromH = (int)(destination.height() / (1 + Math.sin(Math.toRadians(MAX_ROTATE_DEG))));
        int tileSizeFromW = (int)(destination.width() / (word.length() + Math.sin(Math.toRadians(MAX_ROTATE_DEG))));

        int tileSize = tileSizeFromH;
        if(tileSizeFromW < tileSizeFromH)
            tileSize = tileSizeFromW;


        int rotateBonus = (int)(tileSize * Math.sin(Math.toRadians(MAX_ROTATE_DEG)) / 2);
        int spaceBetween = tileSize / 40;
        float x = destination.left + ((destination.width() - (word.length() * (tileSize + spaceBetween) - spaceBetween))/2) - rotateBonus/2;
        float y = destination.top + (destination.height() - tileSize)/2 - rotateBonus/2;

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        for(int i = 0; i < word.length(); i++){

            if(word.charAt(i) == ' '){
                x += tileSize + spaceBetween;
                continue;
            }

            Matrix m = new Matrix();
            m.postTranslate(x, y);

            float rotationAngle = (float)Math.sin(i * Math.PI + word.length() * i / 4) / 2 + 0.5f;
            rotationAngle *= MAX_ROTATE_DEG - MIN_ROTATE_DEG;
            rotationAngle += MIN_ROTATE_DEG;
            rotationAngle = (i % 2 == 0) ? -rotationAngle : rotationAngle;

            m.postRotate(rotationAngle);

            int letter = word.charAt(i) - 'A';
            Bitmap toDraw;
            if(letter >= image.length || letter < 0) {
                toDraw = MyDrawing.bitmapResizer(image[image.length - 1], tileSize, tileSize);
                Paint toDrawLetter = new Paint();
                toDrawLetter.setAntiAlias(true);
                toDrawLetter.setTextSize(tileSize * 0.7f);
                toDrawLetter.setTypeface(Typeface.DEFAULT_BOLD);
                Canvas can = new Canvas(toDraw);
                Rect bounds = new Rect();
                toDrawLetter.getTextBounds(word, i, i+1, bounds);
                can.drawText(word, i, i + 1, (toDraw.getWidth() - bounds.width()) / 2, (toDraw.getHeight() + bounds.height()) / 2, toDrawLetter);
                toDraw = MyDrawing.RotateBitmap(toDraw, rotationAngle);
            }else {
                toDraw = MyDrawing.RotateBitmap(MyDrawing.bitmapResizer(image[letter], tileSize, tileSize), rotationAngle);
            }
            canvas.drawBitmap(toDraw, x, y, null);

            x += tileSize + spaceBetween;
        }
    }

    public static void drawWordStraight(Word worddddd, Rect destination, float padding, Canvas canvas){
        if(worddddd == null)
            return;

        String word = worddddd.getWord();
        if(word.length() == 0)
            return;

        int pad = (int)(padding * destination.height());
        int tileSizeFromH = (destination.height() - 2 * pad);
        int tileSizeFromW = (destination.width() - 2 * pad) / word.length();

        int tileSize = tileSizeFromH;
        if(tileSizeFromW < tileSizeFromH)
            tileSize = tileSizeFromW;

        int spaceBetween = tileSize / 40;
        float x = destination.left + pad;
        float y = destination.top + (destination.height() - tileSize) / 2;

        //Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        for(int i = 0; i < word.length(); i++){

            if(word.charAt(i) == ' '){
                x += tileSize + spaceBetween;
                continue;
            }

            v2Tile.drawTileAndPoint((int)x, (int)y, tileSize, word.charAt(i), worddddd.getMultipliers()[i], canvas);
            //Matrix m = new Matrix();
            //m.postTranslate(x, y);

            //float rotationAngle = (float)Math.sin(i * Math.PI + word.length() * i / 4) / 2 + 0.5f;
            //rotationAngle *= MAX_ROTATE_DEG - MIN_ROTATE_DEG;
            //rotationAngle += MIN_ROTATE_DEG;
            //rotationAngle = (i % 2 == 0) ? -rotationAngle : rotationAngle;

            //m.postRotate(rotationAngle);

            /*int letter = word.charAt(i) - 'A';
            Bitmap toDraw;
            if(letter >= image.length || letter < 0) {
                toDraw = MyDrawing.bitmapResizer(image[image.length - 1], tileSize, tileSize);
                Paint toDrawLetter = new Paint();
                toDrawLetter.setAntiAlias(true);
                toDrawLetter.setTextSize(tileSize * 0.7f);
                toDrawLetter.setTypeface(Typeface.DEFAULT_BOLD);
                Canvas can = new Canvas(toDraw);
                Rect bounds = new Rect();
                toDrawLetter.getTextBounds(word, i, i+1, bounds);
                can.drawText(word, i, i + 1, (toDraw.getWidth() - bounds.width()) / 2, (toDraw.getHeight() + bounds.height()) / 2, toDrawLetter);
                //toDraw = MyDrawing.RotateBitmap(toDraw, rotationAngle);
            }else {
                toDraw = MyDrawing.bitmapResizer(image[letter], tileSize, tileSize);
            }
            canvas.drawBitmap(toDraw, x, y, null);*/

            x += tileSize + spaceBetween;
        }
    }

    public static void drawTileAndPoint(int x, int y, int size, char c, byte multiplier, Canvas canvas){
        int index;
        if(c >= 'a' && c <= 'z') {
            index = c - 'a';
        }else if(c >= 'A' && c <= 'Z'){
            index = c - 'A';
        }else{
            index = image.length - 1;
        }

        canvas.drawBitmap(image[index], null, new Rect(x, y, x + size, y + size), null);
        drawPointForTileAt(x, y, size, c, multiplier, canvas);
    }

    public void drawPoint(int multiplier, Canvas canvas){
        int value = getValue() * multiplier;
        pointPaint.setTextSize(0.3f * size);
        if(multiplier > 1)
            pointPaint.setColor((colours[multiplier] & 0x00ffffff) | (pointPaint.getAlpha() << 24));
        else
            pointPaint.setColor((pointPaint.getAlpha() << 24));
        MyDrawing.drawTextCenteredAt(getX() + (int)(size * 0.8), getY() + (int)(size * 0.2), "" + value, pointPaint, canvas);
    }

    public static void drawPointForTileAt(int x, int y, int size, char c, int multiplier, Canvas canvas){
        int value = valueFromLetter(c) * multiplier;
        pointPaint.setTextSize(0.3f * size);
        pointPaint.setAlpha(255);
        if(multiplier > 1)
            pointPaint.setColor(colours[multiplier]);
        else
            pointPaint.setColor(0xff000000);
        MyDrawing.drawTextCenteredAt(x + (int)(size * 0.8), y + (int)(size * 0.2), "" + value, pointPaint, canvas);
    }

    public boolean isAnimating(){return animationQueue.isEmpty();}//return slideAnimation < Math.PI;}

    public int getRemainingTicks(){return animationQueue.getRemainingTicks();}

    public Point getAnimEndPoint(){return animationQueue.getAnimEndPoint();}

    public boolean shouldRemove(){return ! visible;}

    public char getLetter() {return letter;}

    public int getId() {return id;}

    public int getValue() {return value;}

    public String toString(){
        return letter + " - " + " ("+x+", "+ y +")" + "{\n" + animationQueue + "}";
    }

    public static void setTileDefaultSize(int size){TILE_DEFAULT_SIZE = size; GRAVITY = size / 80;}
}
