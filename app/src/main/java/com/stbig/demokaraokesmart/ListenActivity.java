package com.stbig.demokaraokesmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.stbig.demokaraokesmart.helper.Files;
import com.stbig.demokaraokesmart.servicios.myPlayService;


import java.io.File;

/**
 * Created by root on 30/12/15.
 */
public class ListenActivity extends AppCompatActivity implements View.OnClickListener {

    boolean mBroadcastIsRegistered;
    boolean mBufferBroadcastIsRegistered;

    public static final String BROADCAST_SEEKBAR = "sendPositionseekBar";
    public static final String BROADCAST_PAUSEMEDIA = "pauseMedia";
    public static final String BROADCAST_RESUMENMEDIA = "com.stbig.demokaraokesmart.resumenMedia";

    Intent intentPlay;
    Intent intentPlayPause;
    Intent intentPlayResumen;
    Intent intentPlayServices;

    private SeekBar seekBar;
    private int seekMax;
    private int seekBarPosition=0;

    private ImageButton btnplay;
    private ImageButton btnpause;

    private String pathAudio;


    private boolean inPause=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnplay = (ImageButton) findViewById(R.id.btnplay);
        btnpause = (ImageButton) findViewById(R.id.btnpause);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        intentPlayServices = new Intent(this, myPlayService.class);
        intentPlay = new Intent(BROADCAST_SEEKBAR);
        intentPlayPause = new Intent(BROADCAST_PAUSEMEDIA);
        intentPlayResumen = new Intent(BROADCAST_RESUMENMEDIA);


        //seekBar.setOnSeekBarChangeListener(this);

        btnplay.setOnClickListener(this);
        btnpause.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle v = getIntent().getExtras();
        if(v!=null){
            pathAudio = v.getString("pista");
            //pathAudioPista = v.getString("pista");
        }

        if (!mBufferBroadcastIsRegistered) {
            registerReceiver(broadcastBufferReceiver, new IntentFilter(
                    myPlayService.BROADCAST_BUFFER));

            mBufferBroadcastIsRegistered = true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                pathFile(pathAudio);
                stopAudio();
                startActivity(new Intent(ListenActivity.this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnplay:

                if(!inPause) {
                    Log.i("play audio", "ingreso a play");
                    playAudio();
                }else {
                    Log.i("resumen audio", "ingreso a resumen");
                    resumenAudio();
                }
                inPause=false;
                break;
            case R.id.btnpause:
                Log.i("pause audio", "ingreso a pause");
                pauseAudio();
                inPause=true;
                break;


        }
    }

    private void playAudio() {
        intentPlayServices.putExtra("sentAudioLink", pathAudio);
        intentPlayServices.putExtra("initialPosition", seekBarPosition);
        startService(intentPlayServices);
        registerReceiver(broadcastReceiver, new IntentFilter(myPlayService.BROADCAST_ACTION));
        mBroadcastIsRegistered = true;
    }

    private void resumenAudio() {
        int seekPos = seekBar.getProgress();
        Log.i("valor seek en resumen",String.valueOf(seekPos));
        intentPlayResumen.putExtra("seekpos", seekPos);
        intentPlayResumen.putExtra("stateResumen", "Resumen");
        sendBroadcast(intentPlayResumen);
    }

    private void pauseAudio() {
        intentPlayPause.putExtra("statePause", "Pause");
        sendBroadcast(intentPlayPause);
    }

    private void stopAudio() {
        // --Unregister broadcastReceiver for seekbar
        if (mBroadcastIsRegistered) {
            unregisterReceiver(broadcastReceiver);
            mBroadcastIsRegistered = false;
            inPause=false;
        }

        stopService(intentPlayServices);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            String bufferValue = bufferIntent.getStringExtra("buffering");
            int bufferIntValue = Integer.parseInt(bufferValue);

            if (bufferIntValue == 2)
                stopAudio();
        }
    };


    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        seekBar.setMax((int) (seekMax));
        seekBar.setProgress((int) (seekProgress));
    }


    private void pathFile(String file){
        String characters[] = file.split("/");
        String nameMp3 = characters[characters.length-1];

        Files fileDir = new Files();

        fileDir.setNameFiles(nameMp3);

        File f = new File (fileDir.getPathService());
        if (f.exists ())
            f.delete();

    }


    @Override
    protected void onStop() {
        super.onStop();


    }
}
