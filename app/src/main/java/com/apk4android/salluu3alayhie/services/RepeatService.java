package com.apk4android.salluu3alayhie.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.apk4android.salluu3alayhie.utils.CheckOnTask;
import com.apk4android.salluu3alayhie.R;

import java.util.Calendar;

/**
 * Created by MyPC on 8/16/2017.
 */

public class RepeatService extends Service {
    private static final String TAG = "Removed";
    private IBinder mBinder;
    private MediaPlayer mediaPlayer;
    private final CheckOnTask c = new CheckOnTask();
    private SharedPreferences sp;


    private AudioManager audio;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        sp = this.getSharedPreferences("setTimes", 0);

        String typeOfNotification = sp.getString("TypeOfNotification", "Voice");

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //manager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        //if ()
        if ((manager.getCallState() != TelephonyManager.CALL_STATE_OFFHOOK) &&
                (manager.getCallState() != TelephonyManager.CALL_STATE_RINGING)
        ) {

            if (audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    mediaPlayer = new MediaPlayer();
                }

                if (typeOfNotification.equals("Aya")) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.ayasd);
                } else if (typeOfNotification.equals("Voice")) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.voice);
                }

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        boolean check = c.isRun();
        Log.d(TAG, "onTaskRemoved()");

        if (!check) {

        } else {
            doOnTask();
        }
    }

    private void doOnTask() {
        Log.d(TAG, "onTaskRemoved()");

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis(),
                restartServicePendingIntent
        );

    }

}