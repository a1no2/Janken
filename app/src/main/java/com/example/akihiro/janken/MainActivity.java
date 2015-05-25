package com.example.akihiro.janken;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Random;


public class MainActivity extends ActionBarActivity {
    boolean start_bool = true;      //スタートしたらfalse
    Button staret, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String[] janken = {"gu","tyoki","pa"};
        staret = (Button) findViewById(R.id.start_bt);
        end = (Button) findViewById(R.id.end_bt);


        //スタートボタンが押されたときの処理
        staret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start_bool) {
                    start_bool = false;

                    janken_me();        //じゃんけんの画像設置

                } else {

                }
            }
        });


        //終了ボタンが押された時の処理
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

    }

    public Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                //ダイアログの作成(AlertDialog.Builder)
                return new AlertDialog.Builder(MainActivity.this)
                        .setMessage("「アプリ」を終了しますか?")
                        .setCancelable(false)
                                // 「終了する」が押された時の処理
                        .setPositiveButton("終了する", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // アクティビティ消去
                                MainActivity.this.finish();
                            }
                        })
                                // 「終了しない」が押された時の処理
                        .setNegativeButton("終了しない", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .create();
        }
        return null;
    }


    //ランダムでじゃんけんに出す手を決めて、画像を配置
    public void janken_me() {
        ImageView batoru = (ImageView) findViewById(R.id.batoru);
        Random ran = new Random();
        int i = ran.nextInt(3);
        switch (i) {
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
