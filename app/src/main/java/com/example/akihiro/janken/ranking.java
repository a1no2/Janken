package com.example.akihiro.janken;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Matrix;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class ranking extends ActionBarActivity {
    //スコアの表示とかに使う
    ListView rank_lv;
    ArrayAdapter<String>adapter;
    String[] str = {"0\t-","0\t-","0\t-","0\t-","0\t-"};

    private MyDbHelper helper;
    private SQLiteDatabase db;

    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        button = (Button)findViewById(R.id.button);
        rank_lv = (ListView)findViewById(R.id.rank_lv);

        helper = new MyDbHelper(this);      //ヘルパーの初期化
        db = helper.getWritableDatabase();  //ヘルパーのdbを取得
        // Writa..は読み書き可、Reada...は読み取り

        Cursor c = db.query(    //取り出し
                MyDbHelper.TABLE_NAME,
                new String[]{MyDbHelper.NAME,MyDbHelper.SCORE},
                null,   //selection
                null,   //selectionArgs
                null,   //groupBy
                null,  //having
                MyDbHelper.SCORE + " desc"     //orderBy
        );

        int j = 1;
        while (c.moveToNext()) {    //カラム数だけ回る
            str[j-1] = (j) + ":" + "\t" + c.getInt(c.getColumnIndex(MyDbHelper.SCORE))
                + "\t" + c.getString(c.getColumnIndex(MyDbHelper.NAME));
            j++;
        }

        //リストビューに入れる作業
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                str
        );
        rank_lv.setAdapter(adapter);


        //戻るボタン
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ranking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
