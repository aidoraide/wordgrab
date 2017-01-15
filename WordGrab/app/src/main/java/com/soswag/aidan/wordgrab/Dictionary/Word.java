package com.soswag.aidan.wordgrab.Dictionary;

import android.graphics.Paint;

import com.soswag.aidan.wordgrab.MyStatics.MyMath;
import com.soswag.aidan.wordgrab.Tile.v2Tile;

import java.util.ArrayList;

/**
 * Created by Aidan on 2016-06-07.
 */
public class Word {

    private String word;
    private byte [] multipliers;
    private int pts;
    private static int difficulty = 1;
    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Word(){word = ""; pts = 0;}

    /*
    Word(String word, int pts){
        this.word = word;
        this.pts = pts;
    }

    Word(char [] word, int pts){
        this.pts = pts;
        for(int i = 0; i < word.length; i++){
            if(word[i] == ' ')
                break;
            this.word = this.word + word[i];
        }
    }
    */

    public Word(v2Tile[] tiles, int [] multipliers){

        int length = 0;
        while(length < tiles.length){
            if(tiles[length] == null)
                break;
            length++;
        }

        word = "";
        this.multipliers = new byte[length];
        pts = 0;
        for(int i = 0; i < length; i++){
            char c = tiles[i].getLetter();
            word = word + c;
            pts += v2Tile.valueFromLetter(c) * multipliers[i];
            this.multipliers[i] = (byte)multipliers[i];
        }

        pts = calculatePoints(pts, length);

    }

    public Word(byte [] byteArray){
        int baIndex = 0;
        int wordLength = (byteArray.length - 4) / 2;
        multipliers = new byte[wordLength];
        word = "";
        for(int i = 0; i < wordLength; i++, baIndex++)
            word = word + (char)byteArray[baIndex];
        for(int i = 0; i < wordLength; i++, baIndex++)
            multipliers[i] = byteArray[baIndex];
        pts = byteArray[baIndex++]  & 0x000000ff;
        pts += ((byteArray[baIndex] & 0x000000ff) * 256);

    }

    public Word(byte [] array, int dif){
        this(array);
        pts = calculatePoints(pts, word.length(), dif);
    }

    public byte [] asByteArray(){
        byte [] byteArray = new byte [word.length() + multipliers.length + 4];
        int index = 0;
        for(int i = 0; i < word.length(); i++, index++)
            byteArray[index] = (byte)word.charAt(i);
        for(int i = 0; i < multipliers.length; i++, index++)
            byteArray[index] = multipliers[i];
        for(int i = 0; i < 4; i++, index++)
            byteArray[index] = (byte)(pts >>> (i * 8));

        return byteArray;
    }

    public Word(String wordIn, int [] multipliers){
        word = wordIn;
        this.multipliers = new byte [word.length()];
        pts = 0;
        int i = 0;
        for(; i < word.length(); i++){
            pts += v2Tile.valueFromLetter(word.charAt(i)) * multipliers[i];
            this.multipliers[i] = (byte)multipliers[i];
        }

        pts = calculatePoints(pts, i + 1);

    }

    public int calculatePoints(int rawPoints, int wordLength){
        return calculatePoints(rawPoints, wordLength, difficulty);
    }

    public int calculatePoints(int rawPoints, int wordLength, int difficulty){
        return MyMath.roundToInt(difficulty * 0.5 * rawPoints * (wordLength - 1));
    }

    public String getWord(){return word;}
    public int getPts(){return pts;}
    public void setPts(int newPts){pts = newPts;}

    public char [] getWordAsCharArray(){

        char [] wordReturn = new char [Dictionary.MAX_WORD_LENGTH];

        for(int i = 0; i < word.length(); i++)
            wordReturn[i] = word.charAt(i);
        for(int i = word.length(); i < wordReturn.length; i++)
            wordReturn[i] = ' ';

        return wordReturn;
    }

    public boolean isGreaterThan(Word other){
        return pts > other.pts;
    }
    public boolean isLessThan(Word other){
        return pts < other.pts;
    }
    public boolean isGreaterThanOrEqual(Word other){
        return pts >= other.pts;
    }
    public boolean isLessThanOrEqual(Word other){
        return pts <= other.pts;
    }
    public byte [] getMultipliers(){return multipliers;}


    /*public void drawCenteredAt(int x, int y, Canvas canvas){
        drawCenteredAt(x, y, word, canvas);
    }
    public void drawCenteredWithPointsAt(int x, int y, Canvas canvas){
        drawCenteredAt(x, y, word + " " + pts, canvas);
    }

    private void drawCenteredAt(int x, int y, String toDraw, Canvas canvas){


        Rect bounds = new Rect();
        paint.getTextBounds(toDraw, 0, toDraw.length(), bounds);
        int xCursor = x - bounds.width() / 2;
        int yBaseline = y + bounds.height() / 2;

        for(int i = 0; i < word.length(); i++){

            if(multipliers[i] == 2){
                paint.setColor(ContextCompat.getColor(MainActivity.getContext(), R.color.multiplier_color_x2));
            }else if (multipliers[i] == 3){
                paint.setColor(ContextCompat.getColor(MainActivity.getContext(), R.color.multiplier_color_x3));
            }else if (multipliers[i] == 4){
                paint.setColor(ContextCompat.getColor(MainActivity.getContext(), R.color.multiplier_color_x4));
            }else{
                paint.setColor(ContextCompat.getColor(MainActivity.getContext(), R.color.game_over_screen_text_color));
            }

            canvas.drawText(word, i, i + 1, xCursor, yBaseline, paint);

            paint.getTextBounds(word, i, i + 1, bounds);
            xCursor += bounds.width() + paint.getTextSize() / 12;
        }

        if(toDraw.length() > word.length()) {

            paint.setColor(ContextCompat.getColor(MainActivity.getContext(), R.color.game_over_screen_text_color));
            for (int i = word.length(); i < toDraw.length(); i++) {
                if (toDraw.charAt(i) == ' ') {
                    xCursor += paint.getTextSize() / 3;
                } else {
                    canvas.drawText(toDraw, i, i + 1, xCursor, yBaseline, paint);

                    paint.getTextBounds(toDraw, i, i + 1, bounds);
                    xCursor += bounds.width() + paint.getTextSize() / 12;
                }
            }
        }

    }*/

    public String toString(){
        String s = word;
        s = word + "\n";
        for(int i = 0; i < multipliers.length; i++){
            s = s + (int)multipliers[i];
        }
        s = s + " = " + pts + "pts";
        return s;
    }

    public static void setDifficulty(int dif){difficulty = dif;}
    public static int getDifficulty(){return difficulty;}
    public static Paint getPaint(){return paint;}

    public static void quickSort(ArrayList<Word> words){
        int low = 0;
        int high = words.size() - 1;
        quickSort(words, low, high);
    }

    private static void quickSort(ArrayList<Word> words, int low, int high){
        if(low < high){
            int pivot = partition(words, low, high);
            quickSort(words, pivot + 1, high);
            quickSort(words, low, pivot - 1);
        }
    }

    private static int partition(ArrayList<Word> words, int low, int high){
        Word pivot = words.get(low);
        while (low < high){
            while(low < high && words.get(high).isLessThanOrEqual(pivot)){
                high--;
            }
            words.set(low, words.get(high));
            while (low < high && words.get(low).isGreaterThanOrEqual(pivot)){
                low++;
            }
            words.set(high, words.get(low));
        }
        words.set(low, pivot);
        return low;
    }

}
