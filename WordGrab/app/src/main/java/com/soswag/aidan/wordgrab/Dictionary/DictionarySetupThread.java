package com.soswag.aidan.wordgrab.Dictionary;

import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Aidan on 2016-06-21.
 */
public class DictionarySetupThread extends Thread {

    private byte [] wordbankFile;
    private Resources resources;
    private boolean loading = false;
    private Dictionary dictionary;

    public DictionarySetupThread(Dictionary dictionary, Resources resources){

        this.dictionary = dictionary;
        this.resources = resources;

    }

    @Override
    public void run() {
        this.loading = true;

        int fileLength = 0;
        //Get file length
        InputStream iS;
        try {
            iS = resources.getAssets().open("wordbank.txt");
            while(iS.read() != -1) {
                fileLength++;
            }
            iS.close();
        }catch(IOException e){e.printStackTrace();}

        //Load files to the byte array
        wordbankFile = new byte [fileLength];
        try {
            iS = resources.getAssets().open("wordbank.txt");
            for(int i = 0; i < fileLength; i++){
                wordbankFile[i] = (byte)iS.read();
            }
            iS.close();
        }catch(IOException e){e.printStackTrace();}

        dictionary.setup(wordbankFile);
        this.loading = false;
    }

    public boolean isLoading(){return loading;}

    public byte[] getWordbankFile() {
        return wordbankFile;
    }

}
