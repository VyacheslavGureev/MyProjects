package com.example.recognition;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CreateNewFileForImg
{

    private Uri imgUri;
    private String currentPhotoPath;

    public void addPhotoFileForCamera(Intent takePictureIntent, Context context)
    {
        File photoFile = null;
        try {
            photoFile = createImageFile(context);
        } catch (IOException e) {
            Log.e(context.getClass().getSimpleName(), "IOException!", e);
        }
        // Continue only if the File was successfully created
        if (photoFile != null)
        {
            imgUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        }
    }

    private File createImageFile(Context context) throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); // создаём формат времени
        String imageFileName = "JPEG_" + timeStamp + "_"; // создаём полное имя файла для изображения
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES); // получаем расположение внешней директории для фотографий
        // создаём файл во внейшей директории и передаём ему ключевые параметры, определяющие его как файл для фотографий
        File fileForImage = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // окончательно присваиваем абслолютный путь сгенерированного файла общей переменой, содержащей путь для файла
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = fileForImage.getAbsolutePath();
        galleryAddPic(context);
        // возвращаем сам сгенерированный файл
        return fileForImage;
    }

    private void galleryAddPic(Context context)
    {
        if (currentPhotoPath != null)
        {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        }
    }

    public Uri getUri()
    {
        return imgUri;
    }
}
