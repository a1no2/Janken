package com.example.akihiro.janken;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import java.util.Random;
//import java.util.logging.Handler;
import java.util.logging.LogRecord;
/*
//ややこしいので統一する
//ここでの勝ちはじゃんけんで負けた時
//それ以外は負けとする(あいこも負け)

問題点 左の数字は特に意味ない
１、リスタートしますか？の画面で制限時間(○○秒後に処理)が終わると落ちる
タイマーを設置してそれがゼロになったら遷移という形をとれたら解決するかも
その場合ポップアップを出してる間タイムは止める
その時、不正ができないようにじゃんけんの出している手を透明化させるか、変える
2、SQLがまだ
6、ゲームの制限時間(タイマー表示)がまだ
7、全ページのレイアウト整える
8 ,終了したときmainのアクティビティだけが消えてスコアページが残ったりする
アクティビティ終了ではなくアプリを終了させる必要がある?
*/
public class MainActivity extends ActionBarActivity {

    static int CPU;             //グーは0、チョキは1、パーは2
    static int syouri = 0;          //勝った回数
    static int goukei = 0;          //じゃんけんした合計数
    String[] result = {"名無し","0","0"};  //名前とスコアをリザルト画面に持っていくときに使用
    boolean start_bool = true;      //スタート出来る状態がtrue
    boolean sourou_bool = true;     //早漏処理ができる状態がtrue
    int sourou_int = 0;             //早漏処理起動回数

    //画面表示に関するもの
    Button staret, end;
    ImageView batoru;
    ImageButton gu, tyoki, pa;
    TextView syouri_tv;         //勝利回数のカウント
    TextView CPU_tv;            //ヘルプ的な
    TextView time_tv;           //制限時間
    EditText editText_name;              //ユーザー名かくとこ

    Handler seigenzikan;        //○○秒後に処理、制限時間を設ける30秒の予定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_name = (EditText)findViewById(R.id.editText_name);

        //String[] janken = {"gu","tyoki","pa"};
        staret = (Button) findViewById(R.id.start_bt);      //スタート(リスタート)
        end = (Button) findViewById(R.id.end_bt);           //終了
        batoru = (ImageView) findViewById(R.id.batoru);     //CUPのだすジャンケンの手

        //ユーザーが押すボタン,それぞれグー、チョキ、パー
        gu = (ImageButton) findViewById(R.id.gu_bt);
        tyoki = (ImageButton) findViewById(R.id.tyoki_bt);
        pa = (ImageButton) findViewById(R.id.pa_bt);

        syouri_tv = (TextView) findViewById(R.id.syouri_tv);    //勝った数のカウントの表示
        CPU_tv = (TextView) findViewById(R.id.CPU_tv);           //スタートボタンでゲーム開始の案内

        //初期設定
        syokika();  //初期化メソッド


        gu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start_bool) {       //ゲームが始まっていないときの処理
                    //ネタ考え中
                    sourou();       //早漏ボタン
                } else {                //ゲームが始まっていれば通常処理
                    switch (CPU) {
                        case (2):
                            kati();     //勝った時の処理
                            return;
                        default:
                            make();     //負けた時の処理
                            return;
                    }
                }
            }
        });

        tyoki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (start_bool) {       //ゲームが始まっていないときの処理
                    sourou();       //早漏ボタン
                } else {                //ゲームが始まっていれば通常処理
                    switch (CPU) {
                        case (0):
                            kati();     //勝った時の処理
                            return;
                        default:
                            make();     //負けた時の処理
                            return;
                    }
                }
            }
        });


        pa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (start_bool) {       //ゲームが始まっていないときの処理
                    sourou();       //早漏ボタン
                } else {                //ゲームが始まっていれば通常処理
                    switch (CPU) {
                        case (1):
                            kati();     //勝った時の処理
                            return;
                        default:
                            make();     //負けた時の処理
                            return;
                    }
                }
            }
        });


        //スタートボタンが押されたときの処理
        //ゲームをスタート、またはリスタートさせる
        staret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start_bool) {
                    syokika();      //初期化
                    start_bool = false;
                    staret.setText("リスタート");
                    janken_me();        //じゃんけんの画像設置
                    CPU_tv.setText("CPU");

                    //30秒後リザルト画面に移動
                    seigenzikan = new Handler();
                    seigenzikan.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (start_bool) {

                            } else {
                                result[0] = editText_name.getText().toString();
                                result[1] = String.valueOf(syouri);
                                result[2] = String.valueOf(goukei);
                                syokika();  //初期化メソッド

                                Intent i = new Intent(MainActivity.this, Result.class);
                                i.putExtra("riza", result);
                                startActivity(i);
                            }
                        }
                    }, 3000);        //ミリ秒(30秒)、デバックは3秒になってるから0一個増やす
                } else {
                    showDialog(1);
                }
            }
        });


        //終了ボタンが押された時の処理
        //ポップアップを出して了承が得られれば終了
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

    }


    //制限時間の表示に使う
    public void time(){

        seigenzikan = new Handler();
        seigenzikan.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (start_bool) {

                } else {
                    result[1] = String.valueOf(syouri);
                    result[2] = String.valueOf(goukei);
                    syokika();  //初期化メソッド

                    Intent i = new Intent(MainActivity.this, Result.class);
                    i.putExtra("riza", result);
                    startActivity(i);
                }
            }
        }, 3000);        //ミリ秒(30秒)、デバックは3秒になってるから0一個増やす
    }


    //早漏ボタン
    public void sourou(){
        if(sourou_bool){
            sourou_bool = false;
            CPU_tv.setText("早漏乙");

            seigenzikan = new Handler();
            seigenzikan.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CPU_tv.setText("右上のスタートボタンで\nゲームを始められるよ");
                    sourou_bool = true;
                }
            }, 700);
        }


    }



    //勝った時の処理
    //勝利と合計勝負数に+1
    public void kati() {
        syouri++;
        goukei++;
        syouri_tv.setText("勝利回数：" + syouri);
        janken_me();
    }

    //負けた時の処理
    //合計勝負数に+1
    public void make() {
        goukei++;
        janken_me();
    }


    //ランダムでじゃんけんに出す手を決めて、画像を配置
    public void janken_me() {
        batoru = (ImageView) findViewById(R.id.batoru);
        Random ran = new Random();
        CPU = ran.nextInt(3);
        switch (CPU) {
            case 0:
                batoru.setImageResource(R.drawable.gu);
                return;
            case 1:
                batoru.setImageResource(R.drawable.tyoki);
                return;
            case 2:
                batoru.setImageResource(R.drawable.pa);
                return;
        }
    }


    //ダイヤログ
    public Dialog onCreateDialog(int id) {
        switch (id) {
            //終了確認
            case 0:
                //ダイアログの作成(AlertDialog.Builder)
                return new AlertDialog.Builder(MainActivity.this)
                        .setMessage("終了してもよろしいですか？")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                //MainActivity.this.finish();     //Activity終了処理
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create();

            //リスタート確認
            case 1:
                return new AlertDialog.Builder(MainActivity.this)
                        .setMessage("リスタートしてもよろしいですか？")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                syokika();  //初期化メソッド
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create();

        }
        return null;
    }


    //ゲームを始める時にスコアなどの初期化
    public void syokika() {
        syouri = 0;
        goukei = 0;
        sourou_int = 0;
        start_bool = true;      //スタート出来るときtrue
        staret.setText("スタート");
        CPU_tv.setText("スタートボタンで\nゲームを始められるよ");
        syouri_tv.setText("勝利回数：" + syouri);
        batoru.setImageResource(R.drawable.gu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
