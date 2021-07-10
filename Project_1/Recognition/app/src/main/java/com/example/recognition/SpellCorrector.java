package com.example.recognition;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;





/*import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import nodebktree.Node;

public class SpellCorrector
{
    private HashMap<String, Integer> dictionary = new HashMap<>();

    private HashMap<String, HashSet<String>> deletesAndCandidates = new HashMap<>();

    private String buf;

    public int maxDist;

    private  int flag;

    private Node root;

    private Activity activity;

    private String fileName;

    private Thread tr;

    public SpellCorrector(String fileName, Activity activity, int maxDist, int flag) throws IOException
    {
        this.maxDist = maxDist;
        this.flag = flag;
        this.fileName = fileName;
        this.activity = activity;

        InputStream fisD = activity.getAssets().open(fileName);
        ObjectInputStream isD = new ObjectInputStream(fisD);
        try
        {
            long a = System.currentTimeMillis();
            dictionary = (HashMap<String, Integer>) isD.readObject();
            long b = System.currentTimeMillis() - a;
            b=b;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        //dictionary.put("son", 0);
        //dictionary.put("sun", 0);
        long a = System.currentTimeMillis();
        //generateDeletes(dictionary);
        long b = System.currentTimeMillis() - a;
    }


    /*private void generateDeletes(HashMap<String, Integer> dict)
    {
        Set set = dict.entrySet();
        Iterator i = set.iterator();
        int dist;
        int j = 1;
        while (i.hasNext() && j <= 10000)
        {
            dist = 0;
            Map.Entry me = (Map.Entry) i.next();
            buf = String.valueOf(me.getKey());
            getDeletes(buf, dist);
            j++;
        }
        return;
    }*/

    /*private void getDeletes(String str, int dist)
    {
        dist++;
        if (dist <= maxDist && str.length() > 1)
        {
            for (int j = 0; j < str.length(); j++)
            {
                String temp = delete(str, j);

                try
                {
                    // Create two DatabaseEntry instances:
                    // theKey is used to perform the search
                    // theData will hold the value associated to the key, if found
                    DatabaseEntry key = new DatabaseEntry(temp.getBytes("UTF-8"));
                    DatabaseEntry data = new DatabaseEntry();

                    // Call get() to query the database
                    if (db.myDatabase.get(null, key, data, LockMode.DEFAULT) == OperationStatus.NOTFOUND && dist == maxDist)
                    {
                        DatabaseEntry theData = new DatabaseEntry(buf.getBytes("UTF-8"));
                        db.myDatabase.put(null, key, theData);
                    }
                    else if (db.myDatabase.get(null, key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS && dist == maxDist)
                    {
                        byte[] retData = data.getData();
                        HashSet<String> hs = byteArrToHashSet(retData);
                        hs.add(buf);
                        db.myDatabase.put(null, key, new DatabaseEntry(hashSetStringToByteArr(hs)));
                    }
                }
                catch (Exception e)
                {
                    // Exception handling
                    e.printStackTrace();
                }

                if (!deletesAndCandidates.containsKey(temp) && dist == maxDist)
                {
                    HashSet<String> hs = new HashSet<>();
                    hs.add(buf);
                    deletesAndCandidates.put(temp, hs);
                }
                else if (deletesAndCandidates.containsKey(temp) && dist == maxDist)
                {
                    HashSet<String> hss = deletesAndCandidates.get(temp);
                    hss.add(buf);
                    deletesAndCandidates.put(temp, hss);
                }

                getDeletes(temp, dist);
            }
            return;
        }
        else
        {
            return;
        }
    }*/

    public byte[] hashSetStringToByteArr (HashSet<String> hs)
    {
        byte[] res = new byte[0];
        String separator = "*";
        try
        {
            byte[] sep = separator.getBytes("UTF-8");
            int i = 1;
            for (String s : hs)
            {
                if (hs.size() == 1)
                {
                    res = ArrayUtils.concatByteArrays(s.getBytes("UTF-8"));
                }
                else
                    {
                    if (i == 1)
                    {
                        res = ArrayUtils.concatByteArrays(s.getBytes("UTF-8"));
                    }
                    else
                        {
                        res = ArrayUtils.concatByteArrays(res, sep, s.getBytes("UTF-8"));
                    }
                }
                i++;
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public HashSet<String> byteArrToHashSet (byte[] src)
    {
        HashSet<String> hs = new HashSet<>();
        byte sep = 42;
        byte[] buf = new byte[0];
            int i = 0;
            for (int j = 0; j < src.length; j++)
            {
                if (Byte.compare(src[j], sep) == 0)
                {
                    buf = Arrays.copyOfRange(src, i, j);
                    String str = new String(buf, StandardCharsets.UTF_8);
                    hs.add(str);
                    i = j + 1;
                }
                if (j == src.length-1)
                {
                    buf = Arrays.copyOfRange(src, i, j+1);
                    String str = new String(buf, StandardCharsets.UTF_8);
                    hs.add(str);
                }
            }
        return hs;
    }

    /*class myRunnable implements Runnable
    {
        @Override
        public void run()
        {
            synchronized ((Object) maxDist)
            {
                maxDist++;
            }
        }
    }*/

    /*public byte[] getByteArr (Context activity, String fileName)
    {
        try
        {
            InputStream is = activity.getAssets().open(fileName);
            byte[] fileBytes = new byte[is.available()];
            is.read(fileBytes);
            is.close();
            return fileBytes;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }*/

    public void add(String value)
    {
        if (root == null)
            root = new Node(value);
        else
            root.add(value);
    }

    public void addAll(HashMap collection)
    {
        String key = "";
        Set set = collection.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext())
        {
            Map.Entry me = (Map.Entry) i.next();
            key = String.valueOf(me.getKey());
            add(key);
        }
    }

    public Set<String> search(String value)
    {
        Set<String> result = new HashSet<>();
        if (root != null)
            root.search(value, result);
        return result;
    }



    public List<Suggestion> linSpellAlg(String str)
    {
        int minDist = maxDist;
        int m;

        //HashMap<String, Integer> sug = new HashMap<String, Integer>();

        List<Suggestion> sug = new ArrayList<>();
        if (dictionary.containsKey(str) && flag < 2)
        {
            Suggestion s = new Suggestion(str, dictionary.get(str), 0);
            sug.add(s);
            return sug;
        }

        Set set = dictionary.entrySet();
        Iterator i = set.iterator();

        long a = System.currentTimeMillis();
        while (i.hasNext())
        {
            Map.Entry me = (Map.Entry) i.next();
            buf = String.valueOf(me.getKey());

            if (Math.abs(str.length() - buf.length()) > minDist)
            {
                continue;
            }

            if ((flag == 0) && (sug.size() > 0) && (sug.get(0).dist == 1) && (dictionary.get(buf) < sug.get(0).frequencyOfWord))
            {
                continue;
            }

            m = getMetric(buf, str);

            if (m >= 0 && m <= maxDist)
            {
                if (flag < 2 && sug.size() > 0 && m > sug.get(0).dist)
                {
                    continue;
                }
                if (flag < 2)
                {
                    minDist = m;
                }
                if (flag < 2 && sug.size() > 0 && sug.get(0).dist > m)
                {
                    sug.clear();
                }
                Suggestion s = new Suggestion(buf, dictionary.get(buf), m);
                sug.add(s);
            }
        }
        long b = System.currentTimeMillis() - a;
        return sug;
    }

    public int getMetric(String s1, String s2)
    {

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 1; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 1; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                        Math.min(dp[i][j - 1], dp[i - 1][j]) + 1
                );
            }
        }
        return dp[s1.length()][s2.length()];
    }

    /*private void generateDeletes(HashMap<String, Integer> dict)
    {
        Set set = dict.entrySet();
        Iterator i = set.iterator();
        int dist;
        while (i.hasNext())
        {
            dist = 0;
            Map.Entry me = (Map.Entry) i.next();
            buf = String.valueOf(me.getKey());
            getDeletes(buf, dist);
        }
        return;
    }*/


    /*private HashMap<String, HashSet<String>> generateDeletes(HashMap<String, Integer> dict)
    {
        Set set = dict.entrySet();
        Iterator i = set.iterator();
        int dist;
        while (i.hasNext())
        {
            dist = 0;
            Map.Entry me = (Map.Entry) i.next();
            String str = String.valueOf(me.getKey());
            deletesAndCandidates.putAll(getDeletes(str, dist));
        }
        return deletesAndCandidates;
    }*/

    /*private void getDeletes(String str, int dist)
    {
        dist++;
        if (dist <= maxDist && str.length() > 1)
        {
            for (int j = 0; j < str.length(); j++)
            {
                String temp = delete(str, j);
                if (!deletesAndCandidates.containsKey(temp) && dist == maxDist)
                {
                    HashSet<String> hs = new HashSet<>();
                    hs.add(buf);
                    deletesAndCandidates.put(temp, hs);
                }
                else if (deletesAndCandidates.containsKey(temp) && dist == maxDist)
                {
                    HashSet<String> hss = deletesAndCandidates.get(temp);
                    hss.add(buf);
                    deletesAndCandidates.put(temp, hss);
                }
                getDeletes(temp, dist);
            }
            return;
        }
        else
        {
            return;
        }
    }*/

    /*private HashSet<Modifications> generateDeletes(HashMap<String, Integer> dict)
    {
        String temp = "";
        String str = "";
        HashSet<Modifications> deletesModificationsSet = new HashSet<>();
        Set set = dict.entrySet();
        Iterator i = set.iterator();
        int dist = 0;
        while (i.hasNext())
        {
            dist = 0;
            Map.Entry me = (Map.Entry) i.next();
            str = String.valueOf(me.getKey());
            deletesModificationsSet.addAll(getDeletes(str, dist));
        }
        return deletesModificationsSet;
    }*/

    /*private HashSet<Modifications> getDeletes(String str, int dist)
    {
        dist++;
        Modifications m = new Modifications(str, new HashSet<String>(), dist);
        HashSet<Modifications> modifications = new HashSet<>();
        for (int j = 0; j < str.length(); j++)
        {
            String temp = delete(str, j);
            if (!m.deletesSet.contains(temp))
            {
                m.deletesSet.add(temp);
            }
            if (dist <= maxDist && str.length() > 1)
            {
                modifications.add(m);
                modifications.addAll(getDeletes(temp, dist));
            }
            else
                {
                return modifications;
            }
        }
        return modifications;
    }*/

    class Suggestion
    {
        int frequencyOfWord;
        int dist;
        String suggestion;
        public Suggestion(String suggestion, int frequencyOfWord, int dist)
        {
            this.suggestion = suggestion;
            this.frequencyOfWord = frequencyOfWord;
            this.dist = dist;
        }
    }

    class Modifications
    {
        HashSet<String> deletesSet;
        String originalWord;
        int distance;
        public Modifications(String originalWord, HashSet<String> deletesSet, int distance)
        {
            this.originalWord = originalWord;
            this.deletesSet = deletesSet;
            this.distance = distance;
        }
    }

    private String delete(String word, int index)
    {
        return word.substring(0, index) + word.substring(index + 1);
    }

    /*public HashSet<String> getCandidates(String word)
    {
        HashSet<String> cand = new HashSet<>();
        if (dictionary.containsKey(word))
        {
            cand.add(word);
            return cand;
        }

        else if (deletesAndCandidates.containsKey(word))
        {
            cand.addAll(deletesAndCandidates.get(word));
        }

        HashMap<String, HashSet<String>> dels = new HashMap<>(getDeletes(word, 0));
        HashSet<String> g = new HashSet<>();
        Set set = dels.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext())
        {
            Map.Entry me = (Map.Entry) i.next();
            String str = String.valueOf(me.getKey());
            g.add(str);
        }
        for (String s : g)
        {
            if (dictionary.containsKey(s))
            {
                cand.add(s);
            }
        }

        for (String s : g)
        {
            if (deletesAndCandidates.containsKey(s))
            {
                cand.addAll(deletesAndCandidates.get(s));
            }
        }
        return cand;
    }*/
    
    /*public HashSet<String> getCandidates(String word)
    {
        HashSet<String> cand = new HashSet<>();
        if (dictionary.containsKey(word))
        {
            cand.add(word);
            return cand;
        }

        else if (deletesAndCandidates.containsKey(word))
        {
            cand.addAll(deletesAndCandidates.get(word));
        }

        HashSet<Modifications> dels = new HashSet<>();
        dels.addAll(getDeletes(word, 0));
        for (Modifications m : dels)
        {
            for (String d : m.deletesSet)
            {
                if (dictionary.containsKey(d))
                {
                    cand.add(d);
                }
            }
        }

        for (Modifications m : dels)
        {
            for (String d : m.deletesSet)
            {
                if (deletesAndCandidates.containsKey(d))
                {
                    cand.addAll(deletesAndCandidates.get(d));
                }
            }
        }
        return cand;
    }*/
}

/*public class SpellCorrector
{
    private HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

    public SpellCorrector(String fileName, Activity activity) throws IOException
    {
        // загрузка хеш мап из заранее подготовленного мной файла хеш мап сериалайзед
        InputStream fis = activity.getAssets().open(fileName);
        ObjectInputStream is = new ObjectInputStream(fis);
        try
        {
            long a = System.currentTimeMillis();
            dictionary = (HashMap<String, Integer>) is.readObject();
            long b = System.currentTimeMillis() - a;
            b = b;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        is.close();
        fis.close();
    }

    // вычисление списка слов, находящихся по расстоянию Дамеру-Левенштейна на расстоянии 1 от исходного слова
    private final ArrayList<String> edits(String word)
    {
        ArrayList<String> result = new ArrayList<String>();
        // удаление символа на одной позиции
        for(int i = 0; i < word.length(); i++)
            result.add(word.substring(0, i) + word.substring(i + 1));
        // перестановка двух соседних символов (транспозиция)
        for(int i = 0; i < word.length() - 1; i++)
            result.add(word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1) + word.substring(i + 2));
        // замена одного символа
        for(int i=0; i < word.length(); i++)
            for(char c='а'; c <= 'я'; c++)
                result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));

            // вставка одного символа
        for(int i=0; i <= word.length(); i++)
            for(char c='а'; c <= 'я'; c++)
                result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
        return result;
    }

    public final String correct(String word)
    {
        if(dictionary.containsKey(word))
            return word;

        ArrayList<String> list = edits(word);
        HashMap <Integer, String> candidates = new HashMap<Integer, String>();

        for(String s : list)
            if(dictionary.containsKey(s))
                candidates.put(dictionary.get(s), s);

        if(candidates.size() > 0)
            return candidates.get(Collections.max(candidates.keySet()));
        for(String s : list)
        {
                for (String w : edits(s)) {
                    if (dictionary.containsKey(w)) {
                        candidates.put(dictionary.get(w), w);
                    }
                }
        }
        return candidates.size() > 0 ? candidates.get(Collections.max(candidates.keySet())) : word;
    }
}*/

/*for(int i = 0; i < word.length(); i++)
        result.add(word.substring(0, i) + word.substring(i + 1));*/

// загрузка данных из текстового файла в хеш мап
        /*InputStream inputStream = activity.getAssets().open(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Pattern pattern = Pattern.compile("[а-яА-Я]+");
        Locale rusLocale = new Locale("ru","RU");
        long a = System.currentTimeMillis();
        for(String temp = ""; temp != null; temp = bufferedReader.readLine())
        {
            Matcher matcher = pattern.matcher(temp.toLowerCase(rusLocale));
            while(matcher.find())
            {
                temp = matcher.group();
                nWords.put(temp, nWords.containsKey(temp) ? nWords.get(temp) + 1 : 1);
                //nWords.put((temp = matcher.group()), nWords.containsKey(temp) ? nWords.get(temp) + 1 : 1);
            }
        }
        long b = System.currentTimeMillis() - a;
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();*/

// запись сериализованного файла хэш мап (стринг, интежер)
        /*try
        {
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(baseDir, "rusDictionaryHashmap.ser");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(nWords);
            os.close();
            fos.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/



        /*InputStream inputStream2 = activity.getAssets().open("rusForeightWordsDictionary.txt");
        InputStreamReader inputStreamReader2 = new InputStreamReader(inputStream2);
        BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader2);
        Pattern pattern2 = Pattern.compile("[А-Я]+");
        long a2 = System.currentTimeMillis();
        for(String temp = ""; temp != null; temp = bufferedReader2.readLine())
        {
            Matcher matcher2 = pattern2.matcher(temp);
            while(matcher2.find())
            {
                temp = matcher2.group();
                temp = temp.toLowerCase();
                Words.put(temp, Words.containsKey(temp) ? Words.get(temp) + 1 : 1);
            }
        }
        long b2 = System.currentTimeMillis() - a2;
        bufferedReader2.close();
        inputStreamReader2.close();
        inputStream2.close();

        // загрузка данных из текстового файла в хеш мап
        String temp2;
        InputStream inputStream = activity.getAssets().open("litf-win.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Pattern pattern1 = Pattern.compile("[а-яА-Я]+");
        Pattern pattern3 = Pattern.compile("[0-9]+");
        long a = System.currentTimeMillis();
        for(String temp = ""; temp != null; temp = bufferedReader.readLine())
        {
            Matcher matcher1 = pattern1.matcher(temp);
            Matcher matcher2 = pattern3.matcher(temp);
            while(matcher1.find() && matcher2.find())
            {
                temp = matcher1.group();
                temp2 = matcher2.group();
                if (!Words.containsKey(temp))
                {
                    Words.put(temp, Integer.parseInt(temp2));
                }
                else
                {
                    if (Words.get(temp) <= Integer.parseInt(temp2))
                    {
                        Words.put(temp, Integer.parseInt(temp2) + 1);
                    }
                }
            }
        }
        long b = System.currentTimeMillis() - a;
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();

        *//*nWords.putAll(nWords1);
        nWords.putAll(nWords2);*//*

        try
        {
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(baseDir, "newRusDictionaryHashmap.ser");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            long a3 = System.currentTimeMillis();
            os.writeObject(Words);
            long b3 = System.currentTimeMillis() - a3;
            os.close();
            fos.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/



/*InputStream fis = activity.getAssets().open(fileName);
        ObjectInputStream is = new ObjectInputStream(fis);
        try
        {
            long a = System.currentTimeMillis();
            dictionary = (HashMap<String, Integer>) is.readObject();
            long b = System.currentTimeMillis() - a;
            b = b;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        is.close();
        fis.close();

        InputStream inputStream = activity.getAssets().open("russian.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Pattern pattern = Pattern.compile("[а-яА-Я]+");
        long c = System.currentTimeMillis();
        for(String temp = ""; temp != null; temp = bufferedReader.readLine())
        {
            Matcher matcher = pattern.matcher(temp);
            while(matcher.find())
            {
                temp = matcher.group();
                temp = temp.toLowerCase();
                boolean cont = dictionary.containsKey(temp);
                if (cont)
                {
                    int v = dictionary.get(temp);
                    v++;
                    dictionary.put(temp, v);
                    int t = dictionary.get(temp);
                    t = t;
                }
                else
                {
                    dictionary.put(temp, 1);
                }
            }
        }
        long d = System.currentTimeMillis() - c;
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();

        try
        {
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(baseDir, "bigRusDictionaryHashmap.ser");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            long a3 = System.currentTimeMillis();
            os.writeObject(dictionary);
            long b3 = System.currentTimeMillis() - a3;
            os.close();
            fos.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/



/**
 * Java 8 Spelling Corrector.
 * Copyright 2016 Peter Kuhar.
 *
 * Open source code under MIT license: http://www.opensource.org/licenses/mit-license.php
 *//*


import java.nio.file.*;
        import java.util.*;
        import java.util.stream.*;

public class Spelling {
    private Map<String,Integer> dict = new HashMap<>();

    public Spelling(Path dictionaryFile) throws Exception{
        Stream.of(new String(Files.readAllBytes( dictionaryFile )).toLowerCase().replaceAll("[^a-z ]","").split(" ")).forEach( (word) ->{
            dict.compute( word, (k,v) -> v == null ? 1 : v + 1  );
        });
    }

    Stream<String> edits1(final String word){
        Stream<String> deletes    = IntStream.range(0, word.length())  .mapToObj((i) -> word.substring(0, i) + word.substring(i + 1));
        Stream<String> replaces   = IntStream.range(0, word.length())  .mapToObj((i)->i).flatMap( (i) -> "abcdefghijklmnopqrstuvwxyz".chars().mapToObj( (c) ->  word.substring(0,i) + (char)c + word.substring(i+1) )  );
        Stream<String> inserts    = IntStream.range(0, word.length()+1).mapToObj((i)->i).flatMap( (i) -> "abcdefghijklmnopqrstuvwxyz".chars().mapToObj( (c) ->  word.substring(0,i) + (char)c + word.substring(i) )  );
        Stream<String> transposes = IntStream.range(0, word.length()-1).mapToObj((i)-> word.substring(0,i) + word.substring(i+1,i+2) + word.charAt(i) + word.substring(i+2) );
        return Stream.of( deletes,replaces,inserts,transposes ).flatMap((x)->x);
    }

    Stream<String> known(Stream<String> words){
        return words.filter( (word) -> dict.containsKey(word) );
    }

    String correct(String word){
        Optional<String> e1 = known(edits1(word)).max( (a,b) -> dict.get(a) - dict.get(b) );
        Optional<String> e2 = known(edits1(word).map( (w2)->edits1(w2) ).flatMap((x)->x)).max( (a,b) -> dict.get(a) - dict.get(b) );
        return dict.containsKey(word) ? word : ( e1.isPresent() ? e1.get() : (e2.isPresent() ? e2.get() : word));
    }
}*/






/*public class SpellCorrector implements Serializable
{

    private HashMap<String, Integer> dictionary = new HashMap<>();

    private int radius;

    private Node root;

    public SpellCorrector(String fileName, Activity activity, int radius) throws IOException
    {
        InputStream fis = activity.getAssets().open(fileName);
        ObjectInputStream is = new ObjectInputStream(fis);
        try
        {
            long a = System.currentTimeMillis();
            dictionary = (HashMap<String, Integer>) is.readObject();
            long b = System.currentTimeMillis() - a;
            this.radius = radius;
            long a1 = System.currentTimeMillis();
            this.addAll(dictionary);
            long b1 = System.currentTimeMillis() - a1;
            try
            {
                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                File file = new File(baseDir, "RusDictionaryBKThree.ser");
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                long a2 = System.currentTimeMillis();
                os.writeObject(this);
                long b2 = System.currentTimeMillis() - a2;
                os.close();
                fos.close();
            }
            catch (IOException e)
            {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        is.close();
        fis.close();
    }

    public int getMetric(String s1, String s2)
    {

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 1; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 1; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                        Math.min(dp[i][j - 1], dp[i - 1][j]) + 1
                );
            }
        }
        return dp[s1.length()][s2.length()];
    }

    public void add(String value)
    {
        if (root == null)
            root = new Node(value);
        else
            root.add(value);
    }

    public void addAll(HashMap collection)
    {
        String key = "";
        Set set = collection.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext())
        {
            Map.Entry me = (Map.Entry) i.next();
            key = String.valueOf(me.getKey());
           add(key);
        }
    }

    public Set<String> search(String value)
    {
        Set<String> result = new HashSet<>();
        if (root != null)
            root.search(value, result);
        return result;
    }

    class Node implements Serializable
    {
        private String value;
        private Map<Integer, Node> childs;

        Node(String v) {
            this.value = v;
            this.childs = new HashMap<>();
        }

        void add(String value) {
            int distance = getMetric(this.value, value);
            if (this.childs.containsKey(distance)) {
                this.childs.get(distance).add(value);
            } else {
                this.childs.put(distance, new Node(value));
            }
        }

        void search(String value, Set<String> resultSet)
        {
            int distance = getMetric(this.value, value);

            if (distance <= radius) {// edit distance matching range, the access result set
                resultSet.add(this.value);
            }

            for (int i = Math.max(distance - radius, 1); i <= distance + radius; i++) {
                Node ch = this.childs.get(i);
                if (ch != null)
                    ch.search(value, resultSet);
            }
        }
    }
}*/

/*
public class SpellCorrector<T> implements Serializable
{

    private Set<String> dictionary = new HashSet<>();

    private int radius; // range of fuzzy matching, if the value is 0, it becomes an exact match

    private Node root;
    private Metric<T> metric;

    public SpellCorrector(String fileName, Activity activity, int radius, Metric<T> metric) throws IOException
    {
        InputStream fis = activity.getAssets().open(fileName);
        ObjectInputStream is = new ObjectInputStream(fis);
        try
        {
            long a = System.currentTimeMillis();
            dictionary = (HashSet<String>) is.readObject();
            long b = System.currentTimeMillis() - a;
            this.radius = radius;
            this.metric = metric;
            long a1 = System.currentTimeMillis();
            this.addAll((Collection<? extends T>) dictionary);
            long b1 = System.currentTimeMillis() - a1;
            try
            {
                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                File file = new File(baseDir, "RusDictionaryBKThree.ser");
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                long a2 = System.currentTimeMillis();
                os.writeObject(this);
                long b2 = System.currentTimeMillis() - a2;
                os.close();
                fos.close();
            }
            catch (IOException e)
            {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        is.close();
        fis.close();
    }

    public void add(T value)
    {
        if (root == null)
            root = new Node(value);
        else {
            root.add(value);
        }
    }

    public void addAll(Collection<? extends T> collection)
    {
        for (T val : collection)
        {
            add(val);
        }
    }

    public Set<T> search(T value)
    {
        Set<T> result = new HashSet<>();
        if (root != null)
            root.search(value, result);
        return result;
    }

    class Node implements Serializable
    {
        private T value;
        private Map<Integer, Node> childs;

        Node(T v) {
            this.value = v;
            this.childs = new HashMap<>();
        }

        void add(T value) {
            int distance = metric.getMetric(this.value, value);
            if (this.childs.containsKey(distance)) {
                this.childs.get(distance).add(value);
            } else {
                this.childs.put(distance, new Node(value));
            }
        }

        void search(T value, Set<T> resultSet)
        {
            int distance = SpellCorrector.this.metric.getMetric(this.value, value);

            if (distance <= radius) {// edit distance matching range, the access result set
                resultSet.add(this.value);
            }

            for (int i = Math.max(distance - radius, 1); i <= distance + radius; i++) {
                Node ch = this.childs.get(i);
                if (ch != null)
                    ch.search(value, resultSet);
            }
        }
    }
}*/
