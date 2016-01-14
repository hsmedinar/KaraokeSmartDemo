package com.stbig.demokaraokesmart.servicios;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.stbig.demokaraokesmart.ListenActivity;

import java.io.IOException;

/**
 * Created by root on 11/01/16.
 */
public class myPlayServicePista extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private static final String TAG = "TELSERVICE";
    private MediaPlayer mediaPlayer = new MediaPlayer();

    //-- URL location of audio clip PUT YOUR AUDIO URL LOCATION HERE ---

    private String sntAudioLink;
    private int initialPosition;

    // Set up the notification ID
    private static final int NOTIFICATION_ID = 1;
    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    // ---Variables for seekbar processing---
    String sntSeekPos;
    int intSeekPos;
    int mediaPosition;
    int mediaMax;
    //Intent intent;
    private final Handler handler = new Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "sendInfo";

    // Set up broadcast identifier and intent
    public static final String BROADCAST_BUFFER = "com.medikin.android.dictaphone.broadcastbuffer";

    Intent bufferIntent;
    Intent seekIntent;

    // Declare headsetSwitch variable
    private int headsetSwitch = 1;

    // OnCreate
    @Override
    public void onCreate() {
        // android.os.Debug.waitForDebugger();
        // Instantiate bufferIntent to communicate with Activity for progress
        // dialogue

        bufferIntent = new Intent(BROADCAST_BUFFER);
        // ---Set up intent for seekbar broadcast ---

        seekIntent = new Intent(BROADCAST_ACTION);

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();


        // Register headset receiver
        registerReceiver(headsetReceiver, new IntentFilter(
                Intent.ACTION_HEADSET_PLUG));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // ---Set up receiver for status resumen medina ---
        registerReceiver(broadcastReceiverResumen, new IntentFilter(
                ListenActivity.BROADCAST_RESUMENMEDIA));

        // ---Set up receiver for status pause ---
        registerReceiver(broadcastReceiverPause, new IntentFilter(
                ListenActivity.BROADCAST_PAUSEMEDIA));


        // Manage incoming phone calls during playback. Pause mp on incoming,
        // resume on hangup.
        // -----------------------------------------------------------------------------------
        // Get the telephony manager

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                // String stateString = "N/A";
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }

                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }

                        }
                        break;
                }

            }
        };

        // Register the listener with the telephony manager
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

        // Insert notification start

        sntAudioLink = intent.getExtras().getString("sentAudioLink");
        initialPosition = intent.getExtras().getInt("initialPosition");

        mediaPlayer.reset();

        // Set up the MediaPlayer data source using the strAudioLink value
        if (!mediaPlayer.isPlaying()) {
            try {

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(sntAudioLink);
                // Send message to Activity to display progress dialogue
                sendBufferingBroadcast();
                // Prepare mediaplayer
                mediaPlayer.prepareAsync();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // --- Set up seekbar handler ---

        return START_STICKY;
    }






    private BroadcastReceiver broadcastReceiverPause = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia(intent);
        }
    };


    public void pauseMedia(Intent intent) {
        String pause = intent.getStringExtra("statePause");
        if (mediaPlayer.isPlaying() && pause.equals("Pause")) {
            mediaPlayer.pause();
            //handler.removeCallbacks(sendUpdatesToUI);
            //mediaPlayer.seekTo(seekPos);
            //setupHandler();
        }

    }

    // ---End of seekbar code

    // If headset gets unplugged, stop music and service.
    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    headsetSwitch = 0;
                    // headsetDisconnected();
                } else if (!headsetConnected
                        && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    headsetSwitch = 1;

                }

            }

            switch (headsetSwitch) {
                case (0):
                    headsetDisconnected();
                    break;
                case (1):
                    break;
            }
        }

    };

    private void headsetDisconnected() {
        stopMedia();
        stopSelf();

    }


    // --- onDestroy, stop media player and release.  Also stop phoneStateListener, notification, receivers...---
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }

        // Unregister headsetReceiver
        unregisterReceiver(headsetReceiver);

        // Unregister seekbar receiver
        unregisterReceiver(broadcastReceiverPause);

        // Service ends, need to tell activity to display "Play" button
        resetButtonPlayStopBroadcast();
    }

    // Send a message to Activity that audio is being prepared and buffering
    // started.
    private void sendBufferingBroadcast() {

        bufferIntent.putExtra("buffering", "1");
        sendBroadcast(bufferIntent);
    }

    // Send a message to Activity that audio is prepared and ready to start
    // playing.
    private void sendBufferCompleteBroadcast() {

        bufferIntent.putExtra("buffering", "0");
        sendBroadcast(bufferIntent);
    }

    // Send a message to Activity to reset the play button.
    private void resetButtonPlayStopBroadcast() {
        bufferIntent.putExtra("buffering", "2");
        sendBroadcast(bufferIntent);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

        if (!mediaPlayer.isPlaying()) {
            playMedia();
            //Toast.makeText(this, "SeekComplete", Toast.LENGTH_SHORT).show();
        }

    }


    //---Error processing ---
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this,
                        "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {

        // Send a message to activity to end progress dialogue

        sendBufferCompleteBroadcast();
        playMedia();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // When song ends, need to tell activity to display "Play" button
        stopMedia();
        stopSelf();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.seekTo(initialPosition);
        }
    }

    // Add for Telephony Manager
    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }

    public void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private BroadcastReceiver broadcastReceiverResumen = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPosandResumenPlay(intent);
        }
    };

    public void updateSeekPosandResumenPlay(Intent intent) {
        String statePlay = intent.getStringExtra("stateResumen");
        int seekPos = intent.getIntExtra("seekpos", 0);
        if (!mediaPlayer.isPlaying() && statePlay.equals("Resumen")) {
            mediaPlayer.start();
            mediaPlayer.seekTo(seekPos);
        }
    }

}
