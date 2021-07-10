package com.example.recognition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import static com.example.recognition.MainActivity.ocr;

public final class ImagePreprocessing
{

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            bitmap = null;
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap immutableToMutable(Bitmap bitmap)
    {
        if (!bitmap.isMutable())
        {
            Bitmap btm = null;
            File file = new File("/mnt/sdcard/sample/kek.txt");
            file.getParentFile().mkdirs();

            //Open an RandomAccessFile
        /*Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        into AndroidManifest.xml file*/
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(file, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // get the width and height of the source bitmap.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            Bitmap.Config con = bitmap.getConfig();

            assert randomAccessFile != null;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = null;
            try
            {
                map = channel.map(FileChannel.MapMode.READ_WRITE, 0, width * height * 4);
                bitmap.copyPixelsToBuffer(map);
                //recycle the source bitmap, this will be no longer used.
            /*bitmap.recycle();
            bitmap = null;*/
                //Create a new bitmap to load the bitmap again.
                //btm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                btm = Bitmap.createBitmap(width, height, con);

                map.position(0);
                //load it back from temporary
                btm.copyPixelsFromBuffer(map);
                //close the temporary file and channel , then delete that also
                channel.close();
                randomAccessFile.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return btm;
        }
        else
        {
            Bitmap btm = bitmap.copy(bitmap.getConfig(), true);
            return btm;
        }
    }

    public static void drawRectangels (Bitmap src, HashMap<Rect, String> rectangels)
    {
        Canvas canvas = new Canvas(src);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.CYAN);
        paint.setAntiAlias(true);

        for (Rect r : rectangels.keySet())
        {
            canvas.drawRect(r, paint);
        }

        //return canvas;
    }
}