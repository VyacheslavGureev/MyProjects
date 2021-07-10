package com.example.recognition;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import com.googlecode.tesseract.android.TessBaseAPI;

// этот класс, кроме непосредственно распознавания, выступает также в качестве своеобразного хранилища ссылок на картинки
public final class OCR extends Thread
{
    private static Bitmap originalImage;
    private static Bitmap processingImage;
    private static Bitmap drawImage;
    private static String result = "";
    private static String datapath;
    private static TessBaseAPI tess;

    public OCR (@NonNull String datapath)
    {
        this.datapath = datapath;
    }

    // метод обновления ссылки на картинку
    public void setBitmapOriginalImage(Bitmap img)
    {
        originalImage = img;
    }

    // метод получения ссылки на картинку
    public Bitmap getBitmapOriginalImage()
    {
        try
        {
            return originalImage;
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    public String getStringTextFromOriginalBitmapImage()
    {
        // этот класс построен так, что здесь не может возникнуть ситуация, в которой путь до файлов тренировки тессеракта равен нулевому указателю. Поэтому мы проверяем только картинку на неравенство null
        if (originalImage == null)
            throw new NullPointerException("Image can not to be a null! (Maybe is was not loaded?)");
        else
        {
            tess = new TessBaseAPI();
            tess.init(datapath, "rus"); // можно выбрать больше языков, например "rus+eng", но тогда качество распознанного текста будет ниже
            tess.setImage(originalImage);
            result = tess.getUTF8Text();
            tess.end();
            return result;
        }
    }

    // метод обновления ссылки на картинку
    public void setBitmapDrawImage(Bitmap img)
    {
        drawImage = img;
    }

    // метод получения ссылки на картинку
    public Bitmap getBitmapDrawImage()
    {
        try
        {
            return drawImage;
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    public String getStringTextFromDrawBitmapImage()
    {
        // этот класс построен так, что здесь не может возникнуть ситуация, в которой путь до файлов тренировки тессеракта равен нулевому указателю. Поэтому мы проверяем только картинку на неравенство null
        if (drawImage == null)
            throw new NullPointerException("Image can not to be a null! (Maybe is was not loaded?)");
        else
        {
            tess = new TessBaseAPI();
            tess.init(datapath, "rus"); // можно выбрать больше языков, например "rus+eng", но тогда качество распознанного текста будет ниже
            tess.setImage(drawImage);
            result = tess.getUTF8Text();
            tess.end();
            return result;
        }
    }

    // метод обновления ссылки на картинку
    public void setBitmapProcessingImage(Bitmap img)
    {
        processingImage = img;
    }

    // метод получения ссылки на картинку
    public Bitmap getBitmapProcessingImage()
    {
        try
        {
            return processingImage;
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    public String getStringTextFromProcessingBitmapImage()
    {
        // этот класс построен так, что здесь не может возникнуть ситуация, в которой путь до файлов тренировки тессеракта равен нулевому указателю. Поэтому мы проверяем только картинку на неравенство null
        if (processingImage == null)
            throw new NullPointerException("Image can not to be a null! (Maybe is was not loaded?)");
        else
        {
            tess = new TessBaseAPI();
            tess.init(datapath, "rus"); // можно выбрать больше языков, например "rus+eng", но тогда качество распознанного текста будет ниже
            tess.setImage(processingImage);
            result = tess.getUTF8Text();
            tess.end();
            return result;
        }
    }
}