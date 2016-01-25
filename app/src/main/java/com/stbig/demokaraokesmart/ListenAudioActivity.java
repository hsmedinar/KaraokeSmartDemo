package com.stbig.demokaraokesmart;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.stbig.demokaraokesmart.helper.Files;

import java.io.File;
import java.io.IOException;

/**
 * Created by root on 14/01/16.
 */
public class ListenAudioActivity extends AppCompatActivity implements View.OnClickListener   {

    private SeekBar seekBar;
    private ImageButton btnplay;
    private ImageButton btnpause;
    private boolean inPause=false;
    private String pathPista;
    private String pathAudio;
    private MediaPlayer mPlayPista=null;
    private MediaPlayer mPlayAudio=null;
    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnplay = (ImageButton) findViewById(R.id.btnplay);
        btnpause = (ImageButton) findViewById(R.id.btnpause);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        btnplay.setOnClickListener(this);
        btnpause.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle v = getIntent().getExtras();
        if(v!=null){
            pathPista = v.getString("pista");
            pathAudio = v.getString("audio");
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                pathFile(pathAudio);
                onStopPista();
                onStopAudio();
                handler.removeCallbacks(sendUpdatesToUI);
                startActivity(new Intent(ListenAudioActivity.this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnplay:
                Log.i("play audio", "ingreso a play");
                onPlayPista();
                onPlayAudio();
                break;
            case R.id.btnpause:
                Log.i("pause audio", "ingreso a pause");
                onPausePista();
                onPauseAudio();
                break;
        }
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


    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {

            LogMediaPosition();

            handler.postDelayed(this, 1000); // 2 seconds

        }
    };

    private void LogMediaPosition() {

        if (mPlayPista.isPlaying()) {
            int mediaPosition = mPlayPista.getCurrentPosition();
            int mediaMax = mPlayPista.getDuration();
            seekBar.setMax(mediaMax);
            seekBar.setProgress(mediaPosition);
        }
    }

    private void onPlayPista(){
        try {

            if(mPlayPista==null){

                AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

                mPlayPista = new MediaPlayer();
                mPlayPista.setAudioStreamType(audioManager.STREAM_MUSIC);
                mPlayPista.setDataSource(pathPista);
                mPlayPista.prepare();
                setupHandler();
            }

            mPlayPista.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Toast.makeText(ListenAudioActivity.this,"empezo pista ..",Toast.LENGTH_SHORT);
                }
            });
            mPlayPista.start();

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void onPausePista(){
        if(mPlayPista!=null)
            mPlayPista.pause();

    }

    private void onStopPista(){
        if(mPlayPista!=null){
            mPlayPista.stop();
            mPlayPista.release();
            mPlayPista = null;
        }
    }



    private void onPlayAudio(){
        try {

            if(mPlayAudio==null){

                AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

                mPlayAudio = new MediaPlayer();
                mPlayAudio.setAudioStreamType(audioManager.STREAM_MUSIC);
                mPlayAudio.setDataSource(pathAudio);
                mPlayAudio.prepare();
            }

            mPlayAudio.start();

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void onPauseAudio(){
        if(mPlayAudio!=null)
            mPlayAudio.pause();

    }

    private void onStopAudio(){
        if(mPlayAudio!=null){
            mPlayAudio.stop();
            mPlayAudio.release();
            mPlayAudio = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStopAudio();
        onStopPista();
    }
}
