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
はんどらーのやつ
制限時間けせない

早漏乙は消した方が良い(はよけせ)
*/
public class MainActivity extends ActionBarActivity {
    MyTimer timer = null;          //タイマー

    static int CPU;             //グーは0、チョキは1、パーは2
    static int syouri = 0;          //勝った回数
    static int goukei = 0;          //じゃんけんした合計数
    String[] result = {"名無し","0","0"};  //名前とスコアをリザルト画面に持っていくときに使用
    boolean start_bool = true;      //スタート出来る状態がtrue
    boolean time_bool = false;       //制限時間が減っていくとき真、リスタート押すと偽で制限時間が止まる
    boolean sourou_bool = true;     //ゲームが始まってなかったらtrue
    int sourou_int = 0;             //早漏処理起動回数

    //画面表示に関するもの
    Button staret, end;
    ImageView CPU_janken;
    ImageButton gu, tyoki, pa;
    TextView time_tv;           //残り時間カウント
    TextView syouri_tv;         //勝利回数のカウント
    TextView centerHelp_tv;     //ヘルプ的な
    TextView underHelp_tv;      //下側にあるのヘルプ的な何か
    EditText editText_name;     //ユーザー名かくとこ

    Handler handler = new Handler();        //○○秒後に処理、制限時間を設ける30秒の予定
    Runnable seigenzikan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_name = (EditText)findViewById(R.id.editText_name);

        staret = (Button) findViewById(R.id.start_bt);      //スタート(リスタート)
        end = (Button) findViewById(R.id.end_bt);           //終了
        CPU_janken = (ImageView) findViewById(R.id.CPU_janken);     //CUPのだすジャンケンの手

        //ユーザーが押すボタン,それぞれグー、チョキ、パー
        gu = (ImageButton) findViewById(R.id.gu_bt);
        tyoki = (ImageButton) findViewById(R.id.tyoki_bt);
        pa = (ImageButton) findViewById(R.id.pa_bt);

        syouri_tv = (TextView) findViewById(R.id.syouri_tv);    //勝った数のカウントの表示
        centerHelp_tv = (TextView)findViewById(R.id.centerHelp_tv);    //スタートボタンでゲーム開始の案内
        underHelp_tv = (TextView)findViewById(R.id.underHelp_tv);
        time_tv = (TextView)findViewById(R.id.time_tv);         //残り時間の表示

        //初期設定
        syokika();  //初期化メソッド


        gu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start_bool) {       //ゲームが始まっていないときの処理
                    //ネタ考え中
                    //sourou();       //早漏ボタン
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
                    //sourou();       //早漏ボタン
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
                    //sourou();       //早漏ボタン
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
                    time_bool = true;      //ゲームスタートしたので制限時間が減っていく
                    staret.setText("リスタート");
                    janken_me();        //じゃんけんの画像設置
                    centerHelp_tv.setText("CPU");
                    underHelp_tv.setText("画像をタッチして勝負");

                    time_tv = (TextView)findViewById(R.id.time_tv);     //一応...ね？　制限時間

                    timer = new MyTimer(
                            30000,       //何秒カウントダウンするか　持ち時間
                            10,        //インターバル,ミリ秒
                            time_tv,    //
                            MainActivity.this     //画面情報
                    );
                    timer.start();

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


    //タイマースタート
    public void start(View v){
        if(start_bool){
            timer.start();
        }
    }


    //制限時間になったらスコアのデータをもって画面遷移
    public void time(){

        handler.postDelayed(new Runnable() {
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
        }, 30000);        //ミリ秒(30秒)、デバックは3秒になってるから0一個増やす
    }


    //早漏ボタン
//    ネタ　消します
//    public void sourou(){
//        if(sourou_bool){
//            sourou_bool = false;
//            CPU_tv.setText("早漏乙");
//
//            Runnable setTextCPU = new Runnable() {
//                @Override
//                public void run() {
//                    CPU_tv.setText("右上のスタートボタンで\nゲームを始められるよ");
//                    sourou_bool = true;
//                }
//            };
//            handler.postDelayed(setTextCPU, 700);
//        }
//
//
//    }



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
        CPU_janken = (ImageView) findViewById(R.id.CPU_janken);
        Random ran = new Random();
        CPU = ran.nextInt(3);
        switch (CPU) {
            case 0:
                CPU_janken.setImageResource(R.drawable.gu);
                return;
            case 1:
                CPU_janken.setImageResource(R.drawable.tyoki);
                return;
            case 2:
                CPU_janken.setImageResource(R.drawable.pa);
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
                                timer.cancel();
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
        time_bool = false;      //ゲームスタートまでカウントはストップ
        staret.setText("スタート");
        centerHelp_tv.setText("右上にあるスタートボタンで\nゲームを始められるよ");
        underHelp_tv.setText("右上のスタートボタンを押してね");


        syouri_tv.setText("勝利回数：" + syouri);
        CPU_janken.setImageResource(R.drawable.gu);
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
