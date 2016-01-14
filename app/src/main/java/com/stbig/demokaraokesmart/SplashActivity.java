package com.stbig.demokaraokesmart;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;

import com.stbig.demokaraokesmart.http.ApiCanciones;

/**
 * Created by root on 29/12/15.
 */
public class SplashActivity extends Activity {
    private TextView name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        name = (TextView) findViewById(R.id.app_name);
        Typeface t = Typeface.createFromAsset(getAssets(), "Shihan.otf");
        name.setTypeface(t);

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }

    private void loadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiCanciones api = new ApiCanciones(SplashActivity.this);
                boolean response = api.requestCanciones();


               Message msg = new Message();
               msg.obj=response;
               responseWeb.sendMessage(msg);

            }
        }).start();

    }

    private Handler responseWeb = new Handler(){
        public void handleMessage(Message msg){

            if(Boolean.valueOf(msg.obj.toString())){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent,
                        ActivityOptions
                                .makeSceneTransitionAnimation(SplashActivity.this).toBundle());
            }
        };
    };
}
