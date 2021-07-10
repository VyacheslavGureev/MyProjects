package com.example.recognition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.recognition.MainActivity.ocr;

public class TouchPreprocessing
{

    public HashMap<Rect, String> rects = new HashMap<>();

    public MyAdapter ma;

    public Rect rect;

    public int lastTapInRect;

    public int touch;

    public float x1;
    public float y1;

    public float x2 = -1;
    public float y2 = -1;

    public Bitmap bitmap;

    public Canvas canvas;

    public int offsetTop = 0;
    public int offsetBottom = 0;
    public int offsetRight = 0;
    public int offsetLeft = 0;

    public void checkRects()
    {
        if (rects.size() > 0 && ocr.getBitmapOriginalImage() != null)
        {
            for (Rect r : rects.keySet())
            {
                if (x1 <= r.right + offsetRight && x1 >= r.left + offsetLeft && y1 <= r.bottom + offsetBottom && y1 >= r.top + offsetTop)
                {
                    lastTapInRect = 1;
                    rect = r;
                    String s = rects.get(rect);
                    if (!stringIsDigits(s))
                    {
                        Bitmap b = Bitmap.createBitmap(ocr.getBitmapOriginalImage(), r.left, r.top, r.right - r.left, r.bottom - r.top);
                        if (ocr.getBitmapProcessingImage() != null)
                        {
                            ocr.getBitmapProcessingImage().recycle();
                            ocr.setBitmapProcessingImage(null);
                        }
                        ocr.setBitmapProcessingImage(b);
                        String res = ocr.getStringTextFromProcessingBitmapImage();
                        res = res.toLowerCase();
                        rects.put(rect, res);
                    }
                    return;
                }
            }
            if (ocr.getBitmapProcessingImage() != null)
            {
                ocr.getBitmapProcessingImage().recycle();
                ocr.setBitmapProcessingImage(null);
            }
            rect = null;
            lastTapInRect = 0;
        }
    }

    public void compareRects()
    {
        if (rect != null && rects.size() > 0)
        {
            if (x2 <= rect.right + offsetRight && x2 >= rect.left + offsetLeft && y2 <= rect.bottom + offsetBottom && y2 >= rect.top + offsetTop)
            {
                if (ma.rvl.size() == 0)
                {
                    RecycleViewLine recycleViewLines = new RecycleViewLine();
                    ma.rvl.add(0, recycleViewLines);
                    ma.notifyItemRangeInserted(0, 1);
                }
                RecycleViewLine recycleViewLine = ma.rvl.get(ma.rvl.size() - 1);
                switch (recycleViewLine.position)
                {
                    case 1:
                    {
                        recycleViewLine.name = recycleViewLine.name + " " + rects.get(rect);

                        /*if (ma.mvh != null)
                        {
                            recycleViewLine.price = ma.mvh.priceField.getText().toString() + " " + rects.get(rect);
                        }*/
                        break;
                    }
                    case 2:
                    {
                        recycleViewLine.quantity = recycleViewLine.quantity +  " " + rects.get(rect);
                        /*if (ma.mvh != null)
                        {
                            recycleViewLine.quantity = ma.mvh.viewHolderQuantities.getText().toString() + " " + rects.get(rect);
                        }*/
                        break;
                    }
                    case 3:
                    {
                        recycleViewLine.cost = recycleViewLine.cost +  " " + rects.get(rect);
                        /*if (ma.mvh != null)
                        {
                            recycleViewLine.cost = ma.mvh.costs.getText().toString() + " " + rects.get(rect);
                        }*/
                        break;
                    }
                }
                ArrayList<RecycleViewLine> recycleViewLineArrayList = new ArrayList<>(ma.rvl);
                recycleViewLineArrayList.remove(recycleViewLineArrayList.size() - 1);
                recycleViewLineArrayList.add(recycleViewLineArrayList.size(), recycleViewLine);

                ma.rvl.clear();
                ma.notifyDataSetChanged();

                ma.rvl.addAll(0, recycleViewLineArrayList);
                ma.notifyItemRangeInserted(0, recycleViewLineArrayList.size());
                lastTapInRect = 0;
            }
            else
            {
                for (Rect r : rects.keySet())
                {
                    if (x2 <= r.right + offsetRight && x2 >= r.left + offsetLeft && y2 <= r.bottom + offsetBottom && y2 >= r.top + offsetTop)
                    {
                        lastTapInRect = 1;
                        rect = r;
                        String s = rects.get(rect);
                        if (!stringIsDigits(s))
                        {
                            Bitmap b = Bitmap.createBitmap(ocr.getBitmapOriginalImage(), r.left, r.top, r.right - r.left, r.bottom - r.top);
                            if (ocr.getBitmapProcessingImage() != null)
                            {
                                ocr.getBitmapProcessingImage().recycle();
                                ocr.setBitmapProcessingImage(null);
                            }
                            ocr.setBitmapProcessingImage(b);
                            String res = ocr.getStringTextFromProcessingBitmapImage();
                            res = res.toLowerCase();
                            rects.put(rect, res);
                        }
                        return;
                    }
                }
                if (ocr.getBitmapProcessingImage() != null)
                {
                    ocr.getBitmapProcessingImage().recycle();
                    ocr.setBitmapProcessingImage(null);
                }
                rect = null;
                lastTapInRect = 0;
            }
        }
    }

    public void writeText()
    {
        if (rects.size() > 0 && rect != null && ocr.getBitmapDrawImage() != null)
        {
            Canvas canv = new Canvas(ocr.getBitmapDrawImage());
            Paint paint = new Paint();

            paint.setColor(Color.GREEN);

            paint.setTextSize(rect.bottom - rect.top);

            canv.drawText(rects.get(rect), rect.left - 2, rect.bottom - 2, paint);

            canvas = canv;
        }
    }

    private boolean stringIsDigits(String s)
    {
        String r = "";
        String regEx = "((([0-9][0-9][0-9] |[0-9][0-9] |[0-9] )*[0-9]{3}|[0-9]|[1-9]*[0-9])[.,\\- ][0-9]{2}|(([0-9][0-9][0-9] |[0-9][0-9] |[0-9] )*[0-9]{3}|[0-9]|[1-9]*[0-9])[.,\\- ][0-9](?![0-9]))";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(s);
        while (matcher.find())
        {
            r = r + s.substring(matcher.start(), matcher.end());
        }
        if (s.equals(r))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
