package com.example.akihiro.janken;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by akihiro on 2015/06/08.
 */
public class MyTimer extends CountDownTimer{
    TextView text ;
    Context context;


    public MyTimer(long millisInFuture, long countDownInterval,
                   final TextView text, final Context context) {
        super(millisInFuture, countDownInterval);
        this.text=text;
        this.context = context;

    }


    //～～秒ごとに呼び出される
    @Override
    public void onTick(long millisUntilFinished) {
        //インターバル毎に呼ばれる
        //String minute = Long.toString(millisUntilFinished / 1000 / 60);           //分
        String second = Long.toString(millisUntilFinished / 1000 % 60);             //秒
        String millisecond = Long.toString(millisUntilFinished - (Integer.valueOf(second).intValue() * 1000)); //ミリ秒

        int tim = Integer.valueOf(second).intValue();
        if(tim >= 10){
            text.setText(second + ":" + millisecond);
        }else{
            text.setText("0" + second + ":" + millisecond);
        }

        //今の状態をみれるか見れる
//      Log.i("親",millisUntilFinished+"");
//      Log.i("秒",second);
//      Log.i("ミリ秒",millisecond);

    }

    //カウントダウンが終わったとき
    @Override
    public void onFinish() {
        MainActivity act = (MainActivity)context;

        text.setText("00:00");
        act.result[0] = act.editText_name.getText().toString();
        act.result[1] = String.valueOf(act.syouri);
        act.result[2] = String.valueOf(act.goukei);
        act.syokika();  //初期化メソッド

        Intent i = new Intent(act, Result.class);
        i.putExtra("riza", act.result);
        act.startActivity(i);
        //とーすと
        //Toast.makeText(context,"カウントダウン終了", Toast.LENGTH_SHORT).show();
    }
}
