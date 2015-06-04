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


public class Result extends ActionBarActivity {
    //データベース
    private MyDbHelper helper;
    private SQLiteDatabase db;
    private ContentValues values;

    static int sukoa;               //ゲームで出たスコア
    static boolean[] save_bool = {false, true};
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
    String[] uketori_name = {"","",""};
    static int[] result = {0,0};
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
        ranku_hantei();     //ランキング(トップ5)に入るかの判定

        //スコアがトップ5に入ってたら保存
        if (save_bool[0]) {
            //新規保存か、上書きか
            if (save_bool[1]) {
                save();             //新規保存
            } else {
                uwagaki_save();     //上書き
            }
        }


        //戻るボタン
        bakku_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Result.this.finish();   //力技?
                Intent i = new Intent(Result.this, MainActivity.class);
                startActivity(i);
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

        int[] kako_rank = {0, 0, 0, 0, 0};      //過去のランキングを格納 空で比較するとバグるの回避
        Cursor c = db.query(
                MyDbHelper.TABLE_NAME,
                new String[]{MyDbHelper.NAME, MyDbHelper.SCORE,MyDbHelper.ID},
                null,   //selection
                null,   //selectionArgs
                null,   //groupBy
                null,  //having
                MyDbHelper.SCORE + " desc"     //orderBy
                // わざわざおーだーばい書かなくていいよ
        );

        //カラム数回してスコアをiに入れる
        int i = 0;
        while (c.moveToNext()) {
            kako_rank[i] = c.getInt(c.getColumnIndex(MyDbHelper.SCORE));
            i++;
        }

        //トップ5の5回分まわして記録更新かどうか判定
        for (int j = 0; j< 5; j++) {
            if (sukoa>kako_rank[j]){    //スコア更新するかどうか
                save_bool[0] = true;
                int k = c.getColumnCount();     //カラム数
                if(k < 5){        //カラム数が5個以下なら新規保存、以上なら一番低いスコアを上書き
                    save_bool[1] = true;        //っていう設定を保存してメインに戻る
                    break;
                }else{
                    save_bool[1] = false;
                    c.moveToPosition(4);
                    sukoa_id = c.getInt(c.getColumnIndex(MyDbHelper.SCORE));
                }
                break;      //ベスト5入りが確定したら抜ける
            }else{
                save_bool[0] = false;
            }
        }
    }


    //dbただ追加するだけ
    private void save() {
        //データベース
        // Writa..は読み書き可、Reada...は読み取り
        values = new ContentValues();       //
        values.put(MyDbHelper.NAME, uketori_name[0]);
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

        values.put(MyDbHelper.NAME, uketori_name[0]);
        values.put(MyDbHelper.SCORE, sukoa);

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
        if (result[0] == 0) {   //勝利数が0だと出るバグの回避
            ri_syoubusuu.setText("勝負回数：" + result[1]);
        } else {
            //勝率を出す計算
            double j = result[1] / result[0] * 10;

            ri_syouri.setText("勝利数：" + result[0]);
            ri_syoubusuu.setText("勝負回数：" + result[1]);
            ri_syouritu.setText("勝率：" + j + "%");

            j = result[0] * (j / 100) * 1000;
            sukoa = (int) j;                        //スコア

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
