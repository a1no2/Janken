package com.example.akihiro.janken;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;


public class Result extends ActionBarActivity {
    //データベース
    private MyDbHelper helper;
    private SQLiteDatabase db;
    private ContentValues values;

    static long sukoa;               //ゲームで出たスコア
    static boolean save_bool = false;   //スコアがトップ５に入ればtrue(保存)
    //0,過去スコアと比較して、スコア更新ならtrue
    //1,スコアを新たに保存するか(true)、上書きするか(false)

    //じゃんけんの画像
    TextView ri_syouri;
    TextView ri_syoubusuu;
    TextView ri_syouritu;
    TextView ri_sukoa;

    //遷移ボタン
    Button bakku_bt;        //ゲーム画面に戻るボタン
    Button ranking_bt;      //過去のスコア、ランキング表示ページに遷移

    //スコア関係
    String[] uketori_name = {"", "", ""};
    static int[] result = {0, 0};
    static int sukoa_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        helper = new MyDbHelper(this);      //ヘルパーの初期化
        db = helper.getWritableDatabase();  //ヘルパーのdbを取得

        //スコア関係のテキストビュー
        ri_syouri = (TextView) findViewById(R.id.ri_syouri);
        ri_syoubusuu = (TextView) findViewById(R.id.ri_syoubusuu);
        ri_syouritu = (TextView) findViewById(R.id.ri_syouritu);
        ri_sukoa = (TextView) findViewById(R.id.ri_sukoa);

        //ゲームに戻るボタンと過去のスコアランキングに行くボタン
        bakku_bt = (Button) findViewById(R.id.bakku_bt);         //戻るボタン
        ranking_bt = (Button) findViewById(R.id.rankingu_bt);    //ランキング表示ページ

        //画面遷移からの情報(勝利数と勝負数)を受け取る
        Intent i = getIntent();
        uketori_name = i.getStringArrayExtra("riza");      //0:ユーザー名、1:勝利数、2:勝負回数
        result[0] = Integer.valueOf(uketori_name[1]).intValue();     //Stringで受けたのを
        result[1] = Integer.valueOf(uketori_name[2]).intValue();     //int型の配列に

        sukoa_hyouji();     //スコアを表示するメソッド

        //ベスト5入りならスコア更新
        //せーぶもやる
        ranku_hantei();     //ランキング(トップ5)に入るかの判定


        //戻るボタン
        bakku_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ランキング表示ボタン
        ranking_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Result.this, ranking.class);
                startActivity(i);
            }
        });

    }


    //ランキング(トップ5)に入るかの判定
    private void ranku_hantei() {

        int kako_sukoa = 0;      //過去のランキングを格納 空で比較するとバグるの回避
        Cursor c = db.query(
                MyDbHelper.TABLE_NAME,
                new String[]{MyDbHelper.NAME, MyDbHelper.SCORE, MyDbHelper.ID},
                null,   //selection
                null,   //selectionArgs
                null,   //groupBy
                null,  //havingk
                MyDbHelper.SCORE + " desc"     //orderBy、わざわざおーだーばい書かなくていいよ
        );

        int count = c.getColumnCount();
        //セーブが許可されたら
        /*
        if (count < 5) {
            save();
        } else {
        */
        c.moveToPosition(4);
        kako_sukoa = c.getInt(c.getColumnIndex(MyDbHelper.SCORE));
        sukoa_id = c.getInt(c.getColumnIndex(MyDbHelper.ID));
        if (sukoa >= kako_sukoa) {
            uwagaki_save();
            //}
        }

        //トップ5の一番スコアが低いものと比較して
        //このゲームで出した値のほうが大きければ
        // dbの更新を許可するブーリアンの値をtrue


    }


    //dbただ追加するだけ
    // 多分もう使わない
    private void save() {
        //データベース
        // Writa..は読み書き可、Reada...は読み取り
        values = new ContentValues();       //
        if (uketori_name[0] == "") {
            values.put(MyDbHelper.NAME, "名無し");
        } else {
            values.put(MyDbHelper.NAME, uketori_name[0]);
        }
        values.put(MyDbHelper.SCORE, sukoa);

        db.insert(
                MyDbHelper.TABLE_NAME,
                null,
                values
        );
    }


    //既にあるdbの情報を書き換える,上書き保存的な
    private void uwagaki_save() {
        helper = new MyDbHelper(this);
        db = helper.getWritableDatabase();
        values = new ContentValues();

        if (uketori_name[0] == "") {
            values.put(MyDbHelper.NAME, "名無し");
        } else {
            values.put(MyDbHelper.NAME, uketori_name[0]);
        }
        values.put(MyDbHelper.SCORE, sukoa);
        sukoa_id =

                db.update(      //更新
                        MyDbHelper.TABLE_NAME,              //テーブル指定
                        values,                             //更新内容
                        MyDbHelper.ID + " = " + sukoa_id,   //条件文
                        null
                );

        //"ORDER BY " + MyDbHelper.SCORE + " DASC;"
        //昇順に並んだ1番上(スコアが一番小さいもの)を条件したい
        // = の前に , つけたら複数書き換えられる説


    }


    public void sukoa_hyouji() {
        if (result[0] == 0) {   //勝利数が0だと0を割ってバグる
            ri_syoubusuu.setText("勝負回数：" + result[1]);
        } else {

            //rusult[0]     勝ち数
            //rusult[1]     勝負回数

            ri_syouri.setText("勝利数：" + result[0]);
            ri_syoubusuu.setText("勝負回数：" + result[1]);

            //勝率を出す計算
            //勝ち(double)／全体(double)まではいいが、＊100すると
            // 切り上げてんのか切り下げてんのか知らんけど数値バグるから
            //BigDecimal使って四捨五入

            double j = ((double) (result[0] * 100) / (double) result[1]);
            BigDecimal bi = new BigDecimal(String.valueOf(j));
            j = bi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            ri_syouritu.setText("勝率：" + j + "%");

            j *= 10;
            j = (double) result[0] * j;
            sukoa = Math.round(j);
            ri_sukoa.setText(sukoa + "");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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
