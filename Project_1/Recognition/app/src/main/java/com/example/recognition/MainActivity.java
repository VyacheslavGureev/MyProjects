package com.example.recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    // в комментариях ниже под "следующим классом" подразумевается MainActivity2.java

    // коды успеха операции
    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;

    // коды для получения разрешений
    private final static int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private final static int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private final static int CAMERA_PERMISSION_CODE = 3;

    // кнопки
    private Button selectPhoto;
    private Button makePhoto;

    // путь для файлов обучения Тессеракта
    private String dirOfTessDataOnDevice = "/tessdata";
    private static String pathToTessDataOnDevice = Environment.getExternalStorageDirectory().toString() + "/tess";

    // для использования в этом классе и для следующего класса. Также используется в этом классе
    // в вызове результата для намерения
    public static Uri imgUri;

    // переменная для выходного потока, который потом преобразуется в картинку
    private InputStream imageStream;
    // переменные, используемые там же, что и переменная для потока
    // это переменные для картинки, различаются для улучшения понимания логики программы внутри вызова функции
    // обработки намерения
    private Bitmap selectedImage;
    private  Bitmap capturedImage;

    // переменная для класса распознавания Тессеракт, используется и в этом, и в следующем классе.
    // при инициализации сразу передаём в класс распознавания путь до данных тренировки Тессеракта
    public static OCR ocr = new OCR(pathToTessDataOnDevice);

    // класс, в котором создаётся файл для фотографии, его методы вызываются при фотографировании, чтобы
    // сфотографированное фото сразу сохранялось в определённый класс и отображалось в полном, а не урезанном, разрешении
    // используется в этом и следующем классе
    public static CreateNewFileForImg createFileClass = new CreateNewFileForImg();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectPhoto = (Button) findViewById(R.id.selectPhoto);
        makePhoto = (Button) findViewById(R.id.makePhoto);

        OnClickListener ocl = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.selectPhoto:
                        // проверяем разрешения на чтение, запись и запуск камеры
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        {
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                            {
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                                {
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                                }
                                else
                                {
                                    requestPermissionCamera();
                                }
                            }
                            else
                            {
                                requestPermissionWriteExternalStorage();
                            }
                        }
                        else
                        {
                            requestPermissionReadExternalStorage();
                        }
                        break;
                    case R.id.makePhoto:
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        {
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                            {
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                                {
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    createFileClass.addPhotoFileForCamera(cameraIntent, MainActivity.this);
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                }
                                else
                                {
                                    requestPermissionCamera();
                                }
                            }
                            else
                            {
                                requestPermissionWriteExternalStorage();
                            }
                        }
                        else
                            {
                            requestPermissionReadExternalStorage();
                        }
                        break;
                }
            }
        };

        selectPhoto.setOnClickListener(ocl);
        makePhoto.setOnClickListener(ocl);
    }

    //обрабатываем результат выбора в галерее и передаём управление на другой мейнактивити:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        if (resultCode == RESULT_OK)
        {
            // чтобы в дальнейшем производить распознавание, необходимо сначала переместить данные тренировки тессеракта на устройство
                moveTessDataToDevice();
                switch (requestCode)
                {
                    case GALLERY_REQUEST:
                        // чтобы в дальнейшем получить данные об ориентации фотографии, чтобы её правильно повернуть и в ставить в imageView, мы получаем её uri
                        try
                        {
                            imgUri = returnedIntent.getData();
                            imageStream = getContentResolver().openInputStream(imgUri);
                            selectedImage = BitmapFactory.decodeStream(imageStream);
                            ocr.setBitmapOriginalImage(selectedImage);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case CAMERA_REQUEST:
                        try
                        {
                            imgUri = createFileClass.getUri();

                            imageStream = getContentResolver().openInputStream(imgUri);
                            capturedImage = BitmapFactory.decodeStream(imageStream);
                            ocr.setBitmapOriginalImage(capturedImage);

                            /*capturedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                            ocr.setBitmapImage(capturedImage);*/
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                }
                try
                {
                    startNewActivity();
                }
                catch (NoPhotoException e)
                {
                    e.printStackTrace();
                }
        }
    }

    // в этой функции происходит перемещение файлов тренировки нейронной сети Тессеракта в мобильное устройство. Этот код исполлняется только тогда, когда мы получаем разрешение на запись данных в устройство
    private void moveTessDataToDevice()
    {
        File dir = new File(pathToTessDataOnDevice + dirOfTessDataOnDevice);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        try
        {
            String[] pathToTessDataInProjectArray = getAssets().list("");
            String[] trainedData = new String[2];
            int i = 0;
            for (String fileName : pathToTessDataInProjectArray)
            {
                if (fileName.equals("eng.traineddata") || fileName.equals("rus.traineddata"))
                {
                    trainedData[i] = fileName;
                    i++;
                }
            }
            for (String fileName : trainedData)
            {
                if (!(new File(pathToTessDataOnDevice + dirOfTessDataOnDevice + "/" + fileName)).exists())
                {
                    InputStream input = getAssets().open(fileName);
                    OutputStream output = new FileOutputStream(pathToTessDataOnDevice + dirOfTessDataOnDevice + "/" + fileName);
                    byte[] buf = new byte[1024];
                    while (input.read(buf) > 0)
                    {
                        output.write(buf);
                    }
                    input.close();
                    output.close();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void startNewActivity() throws NoPhotoException
    {
            if (ocr.getBitmapOriginalImage() != null && imgUri != null)
            {
                Intent mainActivity2Intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(mainActivity2Intent);
            }
            else
            {
                throw new NoPhotoException("Photo was not downloaded!");
            }
    }

    // ниже следует массив методов, которые реализуют различные разрешения (потому что система андроид
    // не даёт делать что угодно в устройстве без разрешения пользователя, которое мы и спрашиваем в этих методах,
    // которые ниже)
    private void requestPermissionReadExternalStorage()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Необходим доступ")
                    .setMessage("Приложению необходимо разрешить читать файлы")
                    .setPositiveButton("ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("отменить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    private void requestPermissionWriteExternalStorage()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Необходим доступ")
                    .setMessage("Приложению необходимо разрешить записывать файлы")
                    .setPositiveButton("ок", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("отменить", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    private void requestPermissionCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("Необходим доступ")
                    .setMessage("Приложению необходимо разрешить использовать камеру")
                    .setPositiveButton("ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("отменить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case READ_EXTERNAL_STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Доступ к чтению файлов разрешён", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(this, "Доступ к чтению файлов запрещён", Toast.LENGTH_SHORT).show();
                }
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Доступ к записи файлов разрешён", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(this, "Доступ к записи файлов запрещён", Toast.LENGTH_SHORT).show();
                }
                break;
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Доступ к камере разрешён", Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(this, "Доступ к камере запрещён", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

class NoPhotoException extends Exception
{
    NoPhotoException (String message)
    {
        super(message);
    }
}