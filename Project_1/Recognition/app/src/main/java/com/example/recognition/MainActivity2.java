package com.example.recognition;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.recognition.ImagePreprocessing.rotateBitmap;
import static com.example.recognition.MainActivity.ocr;
import static com.example.recognition.MainActivity.imgUri;
import static com.example.recognition.MainActivity.createFileClass;

public class MainActivity2 extends AppCompatActivity
{
    private final int GALLERY_REQUEST = 1;
    private final int CAMERA_REQUEST = 2;
    private Button selectPhoto;
    private Button drawBlocks;
    private InputStream imageStream;
    private Bitmap selectedImage;
    private Bitmap capturedImage;
    private Button makePhoto;
    private Button addItem;
    private Button further;
    private Button nextBlock;
    public TouchPreprocessing tpp = new TouchPreprocessing();
    private int orientation;
    private InputStream imgStream;
    private ZoomableImageView zoomableImageView;
    public Bitmap bm;
    private boolean ROTATED = false;
    private boolean INITIALIZE_ACTIVITY_2 = false;
    private boolean ORIGINAL_PHOTO_USE = false;
    private boolean PHOTO_WITH_BLOCKS_USE = false;
    private String TAG = "MLKit";
    private HashMap<Rect, String> rectangels = new HashMap<>();

    ArrayList<String> p = new ArrayList<>();
    ArrayList<String> q = new ArrayList<>();
    ArrayList<String> c = new ArrayList<>();
    ArrayList<Integer> positions = new ArrayList<>();



    private RecyclerView recyclerView;
    public MyAdapter myAdapter;

    private ArrayList<String> prices = new ArrayList<String>();
    private HashMap<String, String> countAndPrices = new HashMap<>();
    private ArrayList<String> quantities = new ArrayList<String>();

    private RecyclerView.LayoutManager layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FirebaseApp.initializeApp(this);

        zoomableImageView = (ZoomableImageView) findViewById(R.id.zoomableImageView);

        selectPhoto = (Button) findViewById(R.id.selectPhoto);
        //drawBlocks = (Button) findViewById(R.id.drawBlocks);
        makePhoto = (Button) findViewById(R.id.makePhoto);
        addItem = (Button) findViewById(R.id.addItem);
        further = (Button) findViewById(R.id.further);
        nextBlock = (Button) findViewById(R.id.nextBlock);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        if (savedInstanceState != null)
        {
            ROTATED = savedInstanceState.getBoolean("rotated");
            INITIALIZE_ACTIVITY_2 = savedInstanceState.getBoolean("init");
            ORIGINAL_PHOTO_USE = savedInstanceState.getBoolean("origUsing");
            PHOTO_WITH_BLOCKS_USE = savedInstanceState.getBoolean("drawPhotoUsing");
        }

        if (!INITIALIZE_ACTIVITY_2)
        {
            Box.tpp.add(tpp);
            zoomableImageView.tpp = tpp;
            myAdapter = new MyAdapter();
            zoomableImageView.tpp.ma = myAdapter;
            Box.adapters.add(zoomableImageView.tpp.ma);
        }

        zoomableImageView.tpp = Box.tpp.get(0);

        zoomableImageView.tpp.ma = Box.adapters.get(0);

        recyclerView.setAdapter(zoomableImageView.tpp.ma);

        if (savedInstanceState != null)
        {
            if
            (
                    savedInstanceState.getStringArrayList("prices") != null &&
                    savedInstanceState.getStringArrayList("quantities") != null &&
                    savedInstanceState.getStringArrayList("costs") != null &&
                    savedInstanceState.getIntegerArrayList("positions") != null
            )
            {
                p.addAll(savedInstanceState.getStringArrayList("prices"));
                q.addAll(savedInstanceState.getStringArrayList("quantities"));
                c.addAll(savedInstanceState.getStringArrayList("costs"));
                positions.addAll(savedInstanceState.getIntegerArrayList("positions"));

                ArrayList<RecycleViewLine> arrayRVL = new ArrayList<>();

                for (int i = 0; i < p.size(); i++)
                {
                    RecycleViewLine r = new RecycleViewLine();
                    r.name = p.get(i);
                    r.quantity = q.get(i);
                    r.cost = c.get(i);
                    r.position = positions.get(i);
                    arrayRVL.add(r);
                }

                if (zoomableImageView.tpp.ma.rvl.size() > 0)
                {
                    zoomableImageView.tpp.ma.rvl.clear();
                    zoomableImageView.tpp.ma.notifyDataSetChanged();

                    zoomableImageView.tpp.ma.rvl.addAll(0, arrayRVL);
                    zoomableImageView.tpp.ma.notifyItemRangeInserted(0, arrayRVL.size());
                }
                else
                {
                    zoomableImageView.tpp.ma.rvl.addAll(0, arrayRVL);
                    zoomableImageView.tpp.ma.notifyItemRangeInserted(0, arrayRVL.size());
                }
            }
        }

        View.OnClickListener ocl = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.further:
                        // действия, которые выполнятся при нажатии кнопки "Далее". Эта кнопка ведёт к другим модулям нашего приложения

                        break;
                    case R.id.nextBlock:
                        if (zoomableImageView.tpp.ma.rvl.size() > 0)
                        {
                            RecycleViewLine recycleViewLine = zoomableImageView.tpp.ma.rvl.get(zoomableImageView.tpp.ma.rvl.size() - 1);
                            if (recycleViewLine.position == 3)
                            {
                                RecycleViewLine nextRVL = new RecycleViewLine();

                                ArrayList<RecycleViewLine> arrayOfRVL = new ArrayList<>(zoomableImageView.tpp.ma.rvl);
                                arrayOfRVL.add(arrayOfRVL.size(), nextRVL);

                                zoomableImageView.tpp.ma.rvl.clear();
                                zoomableImageView.tpp.ma.notifyDataSetChanged();

                                zoomableImageView.tpp.ma.rvl.addAll(0, arrayOfRVL);
                                zoomableImageView.tpp.ma.notifyItemRangeInserted(0, arrayOfRVL.size());
                            }
                            else
                            {
                                recycleViewLine.position++;

                                ArrayList<RecycleViewLine> arrayOfRVL = new ArrayList<>(zoomableImageView.tpp.ma.rvl);
                                arrayOfRVL.remove(arrayOfRVL.size() - 1);
                                arrayOfRVL.add(arrayOfRVL.size(), recycleViewLine);

                                zoomableImageView.tpp.ma.rvl.clear();
                                zoomableImageView.tpp.ma.notifyDataSetChanged();

                                zoomableImageView.tpp.ma.rvl.addAll(0, arrayOfRVL);
                                zoomableImageView.tpp.ma.notifyItemRangeInserted(0, arrayOfRVL.size());
                            }
                        }
                        else
                        {
                            RecycleViewLine firstRVL = new RecycleViewLine();

                            ArrayList<RecycleViewLine> arrayOfRVL = new ArrayList<>(zoomableImageView.tpp.ma.rvl);
                            arrayOfRVL.add(arrayOfRVL.size(), firstRVL);

                            zoomableImageView.tpp.ma.rvl.clear();
                            zoomableImageView.tpp.ma.notifyDataSetChanged();

                            zoomableImageView.tpp.ma.rvl.addAll(0, arrayOfRVL);
                            zoomableImageView.tpp.ma.notifyItemRangeInserted(0, arrayOfRVL.size());
                        }
                    break;
                    case R.id.selectPhoto:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GALLERY_REQUEST);
                        break;
                    case R.id.makePhoto:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        createFileClass.addPhotoFileForCamera(cameraIntent, MainActivity2.this);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        break;
                    case R.id.addItem:
                        RecycleViewLine recycleViewLine = new RecycleViewLine();
                        if (zoomableImageView.tpp.ma.rvl.size() > 0)
                        {
                            ArrayList<RecycleViewLine> arrayOfRVL = new ArrayList<>(zoomableImageView.tpp.ma.rvl);
                            arrayOfRVL.add(arrayOfRVL.size(), recycleViewLine);

                            zoomableImageView.tpp.ma.rvl.clear();
                            zoomableImageView.tpp.ma.notifyDataSetChanged();

                            zoomableImageView.tpp.ma.rvl.addAll(0, arrayOfRVL);
                            zoomableImageView.tpp.ma.notifyItemRangeInserted(0, arrayOfRVL.size());
                        }
                        else
                        {
                            ArrayList<RecycleViewLine> arrayOfRVL = new ArrayList<>();
                            arrayOfRVL.add(arrayOfRVL.size(), recycleViewLine);

                            zoomableImageView.tpp.ma.rvl.clear();
                            zoomableImageView.tpp.ma.notifyDataSetChanged();

                            zoomableImageView.tpp.ma.rvl.addAll(0, arrayOfRVL);
                            zoomableImageView.tpp.ma.notifyItemRangeInserted(0, arrayOfRVL.size());
                        }
                        break;
                    //case R.id.drawBlocks:
                        //recognizeText(ocr.getBitmapOriginalImage());
                        //break;
                }
            }
        };
        ItemTouchHelper.SimpleCallback itemTouchHelperSimpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                if (zoomableImageView.tpp.ma.rvl.size() > 0)
                {
                    int pos = viewHolder.getAdapterPosition();

                    ArrayList<RecycleViewLine> arrayRVL = new ArrayList<>(zoomableImageView.tpp.ma.rvl);
                    arrayRVL.remove(pos);

                    zoomableImageView.tpp.ma.rvl.clear();
                    zoomableImageView.tpp.ma.notifyDataSetChanged();

                    zoomableImageView.tpp.ma.rvl.addAll(0, arrayRVL);
                    zoomableImageView.tpp.ma.notifyItemRangeInserted(0, arrayRVL.size());
                }
            }
        };

        makePhoto.setOnClickListener(ocl);
        selectPhoto.setOnClickListener(ocl);
        //drawBlocks.setOnClickListener(ocl);
        addItem.setOnClickListener(ocl);
        further.setOnClickListener(ocl);
        nextBlock.setOnClickListener(ocl);

        new ItemTouchHelper(itemTouchHelperSimpleCallback).attachToRecyclerView(recyclerView);

        // если мы повернули экран, то необходимо запомнить состояние фотографии (повёрнутая или нет), которую мы уже повернули
        if (!INITIALIZE_ACTIVITY_2)
        {
            activityInit();
        }
        else
        {
            setImageBitmapToImageView();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void activityInit()
    {
        try
        {
            bm = ocr.getBitmapOriginalImage();
            // так как для поворота используется ури путь файла с картинкой, то саму переменную,
            // хранящую ури, мы приносим из предыдущего класса MainActivity
            imgStream = getContentResolver().openInputStream(imgUri);
            ExifInterface exif = new ExifInterface(imgStream);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            bm = rotateBitmap(bm, orientation);
            ROTATED = !ROTATED;
            ocr.setBitmapOriginalImage(bm);
            zoomableImageView.setImageBitmap(bm);


            recognizeText(ocr.getBitmapOriginalImage());


            ORIGINAL_PHOTO_USE = true;
            PHOTO_WITH_BLOCKS_USE = false;
            imgStream.close();
            INITIALIZE_ACTIVITY_2 = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("rotated", ROTATED);
        outState.putBoolean("init", INITIALIZE_ACTIVITY_2);
        outState.putBoolean("origUsing", ORIGINAL_PHOTO_USE);
        outState.putBoolean("drawPhotoUsing", PHOTO_WITH_BLOCKS_USE);

        if (zoomableImageView.tpp.ma.rvl.size() > 0)
        {
            ArrayList<RecycleViewLine> arrayRVL = new ArrayList<>(zoomableImageView.tpp.ma.rvl);

            zoomableImageView.tpp.ma.rvl.clear();
            zoomableImageView.tpp.ma.notifyDataSetChanged();

            if (p.size() != 0 && q.size() != 0 && c.size() != 0 && positions.size() != 0)
            {
                p.clear();
                q.clear();
                positions.clear();
            }

            for (RecycleViewLine r : arrayRVL)
            {
                p.add(r.name);
                q.add(r.quantity);
                c.add(r.cost);
                positions.add(r.position);
            }
            outState.putStringArrayList("prices", p);
            outState.putStringArrayList("quantities", q);
            outState.putStringArrayList("costs", c);
            outState.putIntegerArrayList("positions", positions);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setImageBitmapToImageView()
    {
        try
        {
            if (!ROTATED)
            {
                if (ORIGINAL_PHOTO_USE && !PHOTO_WITH_BLOCKS_USE)
                {
                    bm = ocr.getBitmapOriginalImage();
                    // так как для поворота используется ури путь файла с картинкой, то саму переменную,
                    // хранящую ури, мы приносим из предыдущего класса MainActivity
                    imgStream = getContentResolver().openInputStream(imgUri);
                    ExifInterface exif = new ExifInterface(imgStream);
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                    bm = rotateBitmap(bm, orientation);
                    ROTATED = !ROTATED;
                    ocr.setBitmapOriginalImage(bm);
                    zoomableImageView.setImageBitmap(bm);

                    recognizeText(ocr.getBitmapOriginalImage());

                    imgStream.close();
                }
                else if (!ORIGINAL_PHOTO_USE && PHOTO_WITH_BLOCKS_USE)
                {
                    bm = ocr.getBitmapDrawImage();
                    // так как для поворота используется ури путь файла с картинкой, то саму переменную,
                    // хранящую ури, мы приносим из предыдущего класса MainActivity
                    imgStream = getContentResolver().openInputStream(imgUri);
                    ExifInterface exif = new ExifInterface(imgStream);
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                    bm = rotateBitmap(bm, orientation);
                    ROTATED = !ROTATED;
                    ocr.setBitmapDrawImage(bm);
                    zoomableImageView.setImageBitmap(bm);

                    //recognizeText(ocr.getBitmapDrawImage());

                    imgStream.close();
                }
            }
            else
            {
                if (!ORIGINAL_PHOTO_USE && PHOTO_WITH_BLOCKS_USE)
                {
                    bm = ocr.getBitmapDrawImage();
                    zoomableImageView.setImageBitmap(bm);

                    //recognizeText(ocr.getBitmapDrawImage());
                }
                else if (ORIGINAL_PHOTO_USE && !PHOTO_WITH_BLOCKS_USE)
                {
                    bm = ocr.getBitmapOriginalImage();
                    zoomableImageView.setImageBitmap(bm);

                    recognizeText(ocr.getBitmapOriginalImage());
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //обрабатываем результат выбора в галерее и передаём управление на другой мейнактивити:
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case GALLERY_REQUEST:
                    try
                    {
                        imgUri = returnedIntent.getData();
                        imageStream = getContentResolver().openInputStream(imgUri);
                        selectedImage = BitmapFactory.decodeStream(imageStream);
                        if (ocr.getBitmapOriginalImage() != null)
                        {
                            ocr.getBitmapOriginalImage().recycle();
                            ocr.setBitmapOriginalImage(null);
                        }
                        ocr.setBitmapOriginalImage(selectedImage);
                        ROTATED = !ROTATED;
                        ORIGINAL_PHOTO_USE = true;
                        PHOTO_WITH_BLOCKS_USE = false;

                        if (zoomableImageView.tpp.rects.size() > 0)
                        {
                            zoomableImageView.tpp.rects.clear();
                        }
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
                        capturedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                        if (ocr.getBitmapOriginalImage() != null)
                        {
                            ocr.getBitmapOriginalImage().recycle();
                            ocr.setBitmapOriginalImage(null);
                        }
                        ocr.setBitmapOriginalImage(capturedImage);
                        ROTATED = !ROTATED;
                        ORIGINAL_PHOTO_USE = true;
                        PHOTO_WITH_BLOCKS_USE = false;

                        if (zoomableImageView.tpp.rects.size() > 0)
                        {
                            zoomableImageView.tpp.rects.clear();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }
            setImageBitmapToImageView();
        }
    }

    private void recognizeText(Bitmap src)
    {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(src);
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                getResultOfTextRecognition(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Fail in text recognition!");
            }
        });
    }

    private void getResultOfTextRecognition(@org.jetbrains.annotations.NotNull FirebaseVisionText texts)
    {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0)
        {
            Toast.makeText(MainActivity2.this, "No text!", Toast.LENGTH_SHORT);
        }

        /*ArrayList<ArrayList<ArrayList<String>>> bs = new ArrayList<>();
        for (int i = 1; i < blocks.size(); i++)
        {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            ArrayList<ArrayList<String>> b = new ArrayList<>();
            for (int j = 0; j < lines.size(); j++)
            {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                ArrayList<String> ln = new ArrayList<>();
                for (int k = 0; k < elements.size(); k++)
                {
                    String elem = elements.get(k).getText();
                    ln.add(elem);
                }
                b.add(ln);
            }
            bs.add(b);
        }
        bs=bs;*/

        if (rectangels.size() > 0)
        {
            rectangels.clear();
        }

        String resultText = texts.getText();

        Bitmap btm = ImagePreprocessing.immutableToMutable(ocr.getBitmapOriginalImage());
        if (ocr.getBitmapOriginalImage() != null)
        {
            ocr.getBitmapOriginalImage().recycle();
            ocr.setBitmapOriginalImage(null);
        }
        ocr.setBitmapOriginalImage(btm);

        for (FirebaseVisionText.TextBlock block: texts.getTextBlocks())
        {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line: block.getLines())
            {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (FirebaseVisionText.Element element: line.getElements())
                {
                    String elementText = element.getText();
                    Float elementConfidence = element.getConfidence();
                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                    rectangels.put(elementFrame, elementText);
                }
            }
        }

        if (zoomableImageView.tpp.rects.size() > 0)
        {
            zoomableImageView.tpp.rects.clear();
        }
        zoomableImageView.tpp.rects.putAll(rectangels);

        Bitmap b = btm.copy(btm.getConfig(), true);
        if (ocr.getBitmapDrawImage() != null)
        {
            ocr.getBitmapDrawImage().recycle();
            ocr.setBitmapDrawImage(null);
        }
        ocr.setBitmapDrawImage(b);

        ImagePreprocessing.drawRectangels(b, rectangels);
        zoomableImageView.setImageBitmap(b);
        ORIGINAL_PHOTO_USE = false;
        PHOTO_WITH_BLOCKS_USE = true;
    }

    private ArrayList<String> getPrices(String str)
    {
        ArrayList<String> prices = new ArrayList<>();
        String regExAllPrices = "(([0-9][0-9][0-9] |[0-9][0-9] |[0-9] )*[0-9]{3}|[0-9]|[1-9]*[0-9])[.,\\- ][0-9]{2}";
        Pattern patternAllPrices = Pattern.compile(regExAllPrices);
        Matcher matcherAllPrices = patternAllPrices.matcher(str);
        while (matcherAllPrices.find())
        {
            prices.add(str.substring(matcherAllPrices.start(), matcherAllPrices.end()));
        }
        return prices;
    }

    private ArrayList<String> getQuantities(String str)
    {
        ArrayList<String> quantities = new ArrayList<>();
        String regExAllQuantities = "(([0-9][0-9][0-9] |[0-9][0-9] |[0-9] )*[0-9]{3}|[0-9]|[1-9]*[0-9])[.,\\- ][0-9](?![0-9])";
        Pattern patternAllQuantities = Pattern.compile(regExAllQuantities);
        Matcher matcherAllQuantities = patternAllQuantities.matcher(str);
        while (matcherAllQuantities.find())
        {
            quantities.add(str.substring(matcherAllQuantities.start(), matcherAllQuantities.end()));
        }
        return quantities;
    }

    /*private MyAdapter updateItemsInRecyclerView(MyAdapter adapter, ArrayList<String> price, ArrayList<String> quantity)
    {
        if (adapter.priceList.size() != 0)
        {
            adapter.priceList.clear();
            adapter.quantitiesList.clear();
            adapter.notifyDataSetChanged();
        }
        adapter.priceList.addAll(0, price);
        adapter.quantitiesList.addAll(0, quantity);
        adapter.notifyItemRangeInserted(0, price.size());
        return adapter;
    }*/
}