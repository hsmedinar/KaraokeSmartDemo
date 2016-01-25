package com.stbig.demokaraokesmart;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.stbig.demokaraokesmart.helper.Files;
import com.truizlop.fabreveallayout.FABRevealLayout;
import com.truizlop.fabreveallayout.OnRevealChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by root on 29/12/15.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    //private ImageButton play;
    private ImageButton pause;
    private ImageButton stop;

    private MediaRecorder myAudioRecorder=null;
    private String record = null;

    private static MediaPlayer myAudioMedia=null;
    private String pista="";
    FABRevealLayout fabRevealLayout;
    //private static int numPause=0;
    private String pathPista;
    private LinearLayout indicator;
    private boolean inPause=false;

    private final Handler handler = new Handler();

    private static final int RECORDER_BPP = 16;
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private boolean startVideo = false;


    private VideoView video;
    private String sourceVideo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reproductor);

        indicator = (LinearLayout) findViewById(R.id.record_indicator);
        indicator.setVisibility(View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        video = (VideoView) findViewById(R.id.videoView);
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);


        fabRevealLayout = (FABRevealLayout) findViewById(R.id.fab_reveal_layout);
        configureFABReveal(fabRevealLayout);

        //play = (ImageButton) findViewById(R.id.btnplay);
        pause = (ImageButton) findViewById(R.id.btnpause);
        stop = (ImageButton) findViewById(R.id.btnstop);

        //play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);


        video.setOnCompletionListener(this);

    }

    private void configureFABReveal(FABRevealLayout fabRevealLayout) {
        fabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {
            }

            @Override
            public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {
                play();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle v = getIntent().getExtras();
        if(v!=null){
            pista = pathFile(v.getString("pista"), 1);
            pathPista = v.getString("pista");
            sourceVideo = v.getString("video");
            setTitle(v.getString("name"));
            getTempFilename(pathPista);
            preparePlay(pista);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                stop();
                stopRecording(true);
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String pathFile(String file, int option){
        String characters[] = file.split("/");
        String nameMp3 = characters[characters.length-1];
        String recordName[] = nameMp3.split("\\.");

        Files fileDir = new Files();


        switch (option){
            case 1:
                fileDir.setNameFiles(nameMp3);
                break;
            case 2:
                fileDir.setNameFiles("record_" + recordName[0] + ".wav");
                break;
        }


        File f = new File (fileDir.getPathService());
        if (f.exists ()) fileDir.getPathService();

        return fileDir.getPathService();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;

        switch(v.getId()){

            case R.id.btnstop:
                inPause=false;
                stop();
                stopRecording(true);
                fabRevealLayout.revealMainView();
                intent = new Intent(PlayerActivity.this,ListenAudioActivity.class);
                intent.putExtra("audio", pathFile(pathPista, 2));
                intent.putExtra("pista", pathFile(pathPista, 1));
                startActivity(intent);
                break;
            case R.id.btnpause:
                inPause=true;
                pause();
                stopRecording(false);
                fabRevealLayout.revealMainView();

                break;
        }
    }

    private void preparePlay(String path){


        try {

            Uri uri = Uri.parse(sourceVideo);

            video.setMediaController(null);
            video.setVideoURI(uri);
            //setupHandler();

            if (myAudioMedia == null) {
                myAudioMedia = new MediaPlayer();
                myAudioMedia.setAudioStreamType(AudioManager.STREAM_MUSIC);
                myAudioMedia.setDataSource(path);
                myAudioMedia.prepareAsync();

                myAudioMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Toast.makeText(PlayerActivity.this, "listo a reproducror", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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


    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {

            //LogMediaPosition();

            if (video.isPlaying()) {
                //startRecording(false);
                Toast.makeText(PlayerActivity.this,"inicia video",Toast.LENGTH_SHORT).show();

            }
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(this, 1000); // 2 seconds

        }
    };

    private void LogMediaPosition() {

        if (video.isPlaying()) {
            //startRecording(false);
            Toast.makeText(PlayerActivity.this,"inica video",Toast.LENGTH_SHORT).show();
            handler.removeCallbacks(sendUpdatesToUI);
        }
    }

    private void play(){

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if(!inPause) {
                    Log.i("new record", " start new");
                    startRecording(false);
                }else {
                    Log.i("add record", " adding new");
                    startRecording(true);
                }
            }
        });
        video.start();

        /*if(!myAudioMedia.isPlaying()) {
            myAudioMedia.start();

        }*/

    }

    private void pause(){
        video.pause();

        /*if(myAudioMedia!=null && myAudioMedia.isPlaying()) {
            myAudioMedia.pause();
        }*/
    }

    private void stop(){
        video.stopPlayback();

        /*if(myAudioMedia!=null  && myAudioMedia.isPlaying()){
            myAudioMedia.stop();
            myAudioMedia.release();
            myAudioMedia = null;
        }*/
    }



    private void startRecording(final boolean b){
        Toast.makeText(PlayerActivity.this, "Now Recording...", Toast.LENGTH_LONG).show();
        indicator.setVisibility(View.VISIBLE);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile(b);
            }
        },"AudioRecorder Thread");

        recordingThread.start();
    }

    private void stopRecording(boolean b){
        //Toast.makeText(PlayerActivity.this, "Stop Recording...", Toast.LENGTH_LONG).show();
        indicator.setVisibility(View.GONE);
        if(recorder != null){
            isRecording = false;

            recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        if(b == true){
            Toast.makeText(PlayerActivity.this, "Copy Recording...", Toast.LENGTH_LONG).show();
            copyWaveFile(getTempFilename(pathPista), pathFile(pathPista, 2));
            deleteTempFile();
        }
    }

    private String getTempFilename(String file){

        String characters[] = file.split("/");
        String nameMp3 = characters[characters.length-1];
        String recordName[] = nameMp3.split("\\.");


        Files fileDir = new Files();

        fileDir.setNameFiles("record_" + recordName[0] + ".raw");

        File f = new File (fileDir.getPathService());

        if(f.exists())
            fileDir.getPathService();

        return fileDir.getPathService();

    }

    private void deleteTempFile() {
        File file = new File(getTempFilename(pathPista));
        file.delete();
    }
    private void writeAudioDataToFile(boolean b){

        byte data[] = new byte[bufferSize];
        String filename = getTempFilename(pathPista);

        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename,b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read = 0;

        if(os != null){
            while(isRecording){
                read = recorder.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        Log.e("error writting byte",e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error writing", e.getMessage());
            }
        }
    }

    private void copyWaveFile(String inFilename,String outFilename){

        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 44;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/44;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 44;

            //AppLog.logString("File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error copyWave", e.getMessage());
        }
    }


    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopRecording(true);
        fabRevealLayout.revealMainView();
        Intent intent = new Intent(PlayerActivity.this,ListenActivity.class);
        intent.putExtra("audio", pathFile(pathPista, 2));
        startActivity(intent);
    }

}
