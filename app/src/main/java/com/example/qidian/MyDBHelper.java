package com.example.qidian;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "MyDBHelper";
    private static String dbName = "main_db";
    public static final int VERSION = 3;

    //必须要有构造函数
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        this(context, name, factory, VERSION);
    }

    public MyDBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        this(context, dbName, factory);
    }


    // 当第一次创建数据库的时候，调用该方法
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table history_table(id INTEGER PRIMARY KEY AUTOINCREMENT,datetime varchar(20),score int,type int,errors int,count int)";
        Log.i(TAG, "create Database------------->");

        //execSQL函数用于执行SQL语句
        db.execSQL(sql);
    }

    //当更新数据库的时候执行该方法
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql_upgrade = "alter table history_table add count int";//增加一个列count
        db.execSQL(sql_upgrade);

        Log.i(TAG, "update Database------------->");
    }
}
