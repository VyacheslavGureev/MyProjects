package com.example.delservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "project.db";

    public DBHelper(Context context) {
        super(context, "project.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users(username TEXT primary key not null, email TEXT not null, phone TEXT not null, password TEXT not null)");
        db.execSQL("create table orders(num_of_order INTEGER primary key not null, username_of_deliverer TEXT, latitude_rest REAL not null, longitude_rest REAL not null, latitude_dest REAL not null, longitude_dest REAL not null, weight_of_order REAL not null, profit REAL not null, first_course TEXT, second_course TEXT, dessert TEXT, drinks TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists users");
        db.execSQL("drop table if exists orders");
    }

    public Boolean insertData(String username, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("username", username);
        values.put("email", email);
        values.put("phone", phone);
        values.put("password", password);

        long result = db.insert("users", null, values);
        return !(result == -1);
    }

    //Функция для вставки в таблицу БД Orders данных, поступающих от ресторанов
    public void insertDefaultData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        double[] array_latitude_rest = new double[] {47.206741, 47.209535, 47.210276};
        double[] array_longitude_rest = new double[] {38.938304, 38.935348, 38.936319};
        double[] array_latitude_dest = new double[] {47.206330, 47.209778, 47.213219};
        double[] array_longitude_dest = new double[] {38.932744, 38.930504, 38.936553};
        double[] array_weights_of_orders = new double[] {1.2513, 0.759, 0.561};
        double[] array_of_profits = new double[] {120.99, 0.759, 0.561};
        String[] array_of_first_course = new String[] {"Шурпа,Кабачковый суп,Харчо", "Шулюм", ""};
        String[] array_of_second_course = new String[] {"Котлеты из овощей,Паста с помидорами", "", "Фрикадельки в духовке,Сибирские пельмени"};
        String[] array_of_dessert = new String[] {"Шоколадный пудинг", "Маршмеллоу,Шоколадный мусс", ""};
        String[] array_of_drinks = new String[] {"", "", "Кофе Латте,Имбирный эль"};

        for(int i = 0; i < 3; i++) {
            values.put("num_of_order", i);
            values.put("latitude_rest", array_latitude_rest[i]);
            values.put("longitude_rest", array_longitude_rest[i]);
            values.put("latitude_dest", array_latitude_dest[i]);
            values.put("longitude_dest", array_longitude_dest[i]);
            values.put("weight_of_order", array_weights_of_orders[i]);
            values.put("profit", array_of_profits[i]);
            values.put("first_course", array_of_first_course[i]);
            values.put("second_course", array_of_second_course[i]);
            values.put("dessert", array_of_dessert[i]);
            values.put("drinks", array_of_drinks[i]);
            db.insert("orders", null, values);
            values.clear();
        }
    }

    public Boolean checkUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where username=?", new String[] {username});
        return cursor.getCount() > 0;
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where email=?", new String[] {email});
        return cursor.getCount() > 0;
    }

    public Boolean checkPhone(String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?", new String[] {phone});
        return cursor.getCount() > 0;
    }

    public Boolean checkPassword(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where username=? and password=?", new String[] {username, password});
        return cursor.getCount() > 0;
    }
}
