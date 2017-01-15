package com.soswag.aidan.wordgrab.MyStatics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by Aidan on 2016-06-19.
 */
public class MyDrawing {

    private static final String TAG = "My_Drawing";

    public static void drawRoundRect(int left, int top, int right, int bottom, int circleRadius, Paint paint, Canvas canvas){
        //int alphaOriginal = paint.getAlpha();
        //paint.setAlpha(255);
        canvas.drawCircle(left + circleRadius, top + circleRadius, circleRadius, paint);
        canvas.drawCircle(right - circleRadius, top + circleRadius, circleRadius, paint);
        canvas.drawCircle(left + circleRadius, bottom - circleRadius, circleRadius, paint);
        canvas.drawCircle(right - circleRadius, bottom - circleRadius, circleRadius, paint);
        canvas.drawRect(left + circleRadius, top, right - circleRadius, bottom, paint);
        canvas.drawRect(left, top + circleRadius, right, bottom - circleRadius, paint);
        //paint.setAlpha(alphaOriginal);
    }

    public static void drawComplimentaryHole(Bitmap holeTemplate, int left, int top, int right, int bottom, int color, Bitmap bmToDrawTo){
        Bitmap toScale = MyDrawing.bitmapResizer(holeTemplate, right - left, bottom - top);
        for(int i = 0; i < toScale.getWidth(); i++){
            for(int j = 0; j < toScale.getHeight(); j++){
                int pixel = toScale.getPixel(i, j);
                int newPixel = (pixel ^ color) & color;
                bmToDrawTo.setPixel(left + i, top + j, newPixel);
            }
        }
    }

    public static void drawBitmapAsMonoColour(Bitmap holeTemplate, int left, int top, int right, int bottom, int color, Bitmap bmToDrawTo, float alphaScale){
        Bitmap toScale = MyDrawing.bitmapResizer(holeTemplate, right - left, bottom - top);
        int colorNoAlpha = color & 0x00ffffff;
        int alphaMax = 128;
        for(int i = 0; i < toScale.getWidth(); i++){
            for(int j = 0; j < toScale.getHeight(); j++){
                //Get rid of non alpha bits, scale alpha bits, shift back alpha bits to first 2 bytes
                int alpha = ((int)((toScale.getPixel(i, j) >> 24) * alphaScale)) << 24;
                if(alpha >> 24 > alphaMax) {
                    Log.d(TAG, "Pixel(" + i + ", " + j + ") alpha = " + Integer.toHexString(alpha));
                    Log.d(TAG, "(toScale.getPixel(i, j) >> 24) = " + (toScale.getPixel(i, j) >> 24));
                    Log.d(TAG, "(int)((toScale.getPixel(i, j) >> 24) * alphaScale)) = " + ((int)((toScale.getPixel(i, j) >> 24) * alphaScale)) );
                    Log.d(TAG, "Shifted back = " + Integer.toHexString(((int)((toScale.getPixel(i, j) >> 24) * alphaScale)) << 24));
                }
                int newPixel = alpha | colorNoAlpha;
                bmToDrawTo.setPixel(left + i, top + j, newPixel);
            }
        }
    }

    public static void bitmapAsNewColor(Bitmap template, Bitmap newBitmap, int newColor){
        Bitmap toScale = MyDrawing.bitmapResizer(template, newBitmap.getWidth(), newBitmap.getHeight());
        int alphaMax = (newColor & 0xff000000) >> 24;
        for(int i = 0; i < toScale.getWidth(); i++) {
            for (int j = 0; j < toScale.getHeight(); j++) {
                int pixel = toScale.getPixel(i, j);
                int pixelAlpha = (pixel & 0xff000000) >> 24;
                pixelAlpha = pixelAlpha & 0x000000ff;
                float alphaFactor = (1.f * pixelAlpha) / 0x000000ff;
                //Set alpha bytes
                //Get the relative alpha of the pixel
                int newPixel = ((int)(alphaFactor * alphaMax)) << 24;
                //Clear RGB bytes
                newPixel = newPixel & 0xff000000;
                //Copy RGB bytes
                newPixel = newPixel | (newColor & 0x00ffffff);
                newBitmap.setPixel(i, j, newPixel);
            }
        }
    }

    public static Bitmap bitmapResizer(Bitmap bitmap,int newWidth,int newHeight) {

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_4444);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    public static void drawTextCenteredAt(int x, int y, String toDraw, Paint paint, Canvas canvas) {

        float yBaseline = y - (paint.descent() + paint.ascent()) / 2;
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(toDraw, 0, toDraw.length(), x, yBaseline, paint);

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle, source.getWidth() / 2, source.getHeight() / 2);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
