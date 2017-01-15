package com.soswag.aidan.wordgrab.Dictionary;

import android.content.res.Resources;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Aidan on 2016-06-21.
 */
public class Dictionary {

    private static final String TAG = "My_Dictionary";

    public static final int MIN_WORD_LENGTH = 2;
    public static final int MAX_WORD_LENGTH = 8;
    public static final int BYTES_PER_WORD = 10;

    private byte [] wordbankFile;
    private ArrayList<String> popularWords;
    private boolean isSetup;

    public Dictionary(Resources resources){
        Log.d(TAG, "Setting up dictionary");
        isSetup = false;
        popularWords = new ArrayList<>();

                try {
                    Log.d(TAG, "Try block begin");
                    InputStream is = resources.getAssets().open("20k.txt");
                    Log.d(TAG, "opened inputstream " + is.toString());
                    boolean endOfFile = false;
                    while( !endOfFile){
                        String word = "";
                        while( ! endOfFile) {
                            int b = is.read();
                            if(b == -1){
                                endOfFile = true;
                                break;
                            }
                            char c = (char)b;
                            if(c > 'z' || c < 'a')
                                break;
                            word += c;
                        }
                        popularWords.add(word);
                    }
                }catch (Exception e){e.printStackTrace();}

    }

    public void setup(byte [] wordbankFile){this.wordbankFile = wordbankFile;if(wordbankFile != null)isSetup = true;System.out.println("Setup is complete");}

    public boolean isValidWord(Word word){
        return isValidWord(word.getWordAsCharArray());
    }

    public boolean isValidWord(char [] word){

        if(!isSetup)
            return false;

        System.out.print("Checking ");
        int letterCount = 0;
        for(char c : word) {
            System.out.print(c);
            if(c == ' ')
                break;
            letterCount++;
        }

        if(letterCount < MIN_WORD_LENGTH){
            System.out.println(" : TOO SHORT");
            return false;
        }
        if(letterCount > MAX_WORD_LENGTH){
            System.out.println(" : TOO LONG");
            return false;
        }

        byte[] buffer = new byte[MAX_WORD_LENGTH];
        char[] bufferAsChar = new char[MAX_WORD_LENGTH];
        char[] rackedLetters;

        if(word.length == MAX_WORD_LENGTH)
            rackedLetters = word;
        else {
            rackedLetters = new char[MAX_WORD_LENGTH];
            int j = 0;
            for (; j < word.length && j < rackedLetters.length; j++) {
                rackedLetters[j] = word[j];
            }
            for (; j < rackedLetters.length; j++) {
                rackedLetters[j] = ' ';
            }
        }

        //Do a binary search for the word
        int right = wordbankFile.length - BYTES_PER_WORD;
        int left = 0;
        while (left <= right) {
            //Get new mid every iteration
            int mid = (left + right) / 2;
            //Floor mid
            mid -= mid % BYTES_PER_WORD;
            //Load the buffer from the file
            for(int i = 0; i < 8; i++)
                buffer[i] = wordbankFile[mid + i];
            //Convert to char
            for (int i = 0; i < buffer.length; i++)
                bufferAsChar[i] = (char) buffer[i];
            //Compare the rack to the word from file
            //System.out.print("Comparing ");
            //for(char c : rackLetters)
            //    System.out.print(c);
            //System.out.print(" to ");
            //for(char c : bufferAsChar)
            //    System.out.print(c);
            int comparison = firstSubtractSecond(rackedLetters, bufferAsChar);
            //System.out.println(" = " + comparison);
            if (comparison < 0) {
                right = mid - BYTES_PER_WORD;
            } else if (comparison > 0) {
                left = mid + BYTES_PER_WORD;
            } else {
                //If comparison = 0 we have found a match, the rackLetters contain a real word
                System.out.println(" : FOUND");
                return true;
            }
        }
        //No matches found, rackLetters do not contain a real word
        System.out.println(" : NOT FOUND");
        return false;
    }

    private int firstSubtractSecond(char [] first, char [] second){

        for(int i = 0; i < first.length; i++){
            if(first[i] != second[i]) {
                return first[i] - second[i];
            }
        }
        return 0;
    }

    public String getRandomWord(){
        System.out.println("Get random word called, setup = " + isSetup);
        if(isSetup) {
            int randomIndex = (int) (Math.random() * wordbankFile.length);
            randomIndex -= randomIndex % BYTES_PER_WORD;
            System.out.println("randomIndex = " + randomIndex);
            String random = "";
            System.out.println("wordbankFile[randomIndex] = " + (char)(wordbankFile[randomIndex]) + "(" + wordbankFile[randomIndex] + ")");
            for (; wordbankFile[randomIndex] >= 'a' && wordbankFile[randomIndex] <= 'z'; randomIndex++) {
                random = random + (char) wordbankFile[randomIndex];
                System.out.print((char) wordbankFile[randomIndex]);
            }
            System.out.println("Randomly pulled the word " + random);
            return random;
        }
        System.out.println("ERROR - trying to get random word from not-setup dictionary");
        return "oops";
    }

    public String getRandomWord(int difficulty){
        int randomIndex = popularWords.size() / 250 * difficulty + (int)(Math.random() * popularWords.size() / 250);
        return popularWords.get(randomIndex);
    }


}
