
package com.example.akihiro.janken;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Struct;

public class MyDbHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "mydata.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "mydata";

    public static final String ID = "_id";     //_はお呪い♡
    public static final String NAME = "name";
    public static final String SCORE = "score";

    public MyDbHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }



    //～～～～～SQL文勉強中～～～～～

    //データベースがないときに呼び出される
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + "("
                + ID + " integer auto_increment primary key,"   //インクリメントやめたほうがいい?
                + NAME + " text,"   //varchar(文字数指定まっくす256)
                + SCORE + " integer);"
        );
//        db.execSQL("insert into mydata values(0, aaa, 1000);");
//        db.execSQL("insert into mydata values(0, aaa, 1000);");
//        db.execSQL("insert into mydata values(0, aaa, 1000);");
//        db.execSQL("insert into mydata values(0, aaa, 1000);");
//        db.execSQL("insert into mydata values(0, aaa, 1000);");

    }

    //インクリメントやめないと更新されるたびに,ごみデータが増えていく？
    //データの上書き出来るなら...できるわ
    //そのスコアのid取得して上書きする？

    //バージョンが上がると呼び出される
    //データベースが初期化されるらしい
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                //テーブル(TABLE_NAME(mydata))を削除
                "drop table if exists " + TABLE_NAME
        );
        onCreate(db);
    }

    //データベースを開かれるたびに呼び出される
    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
    }
}
