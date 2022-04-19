package com.apk4android.salluu3alayhie.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.apk4android.salluu3alayhie.R;

public class ReminderPlayer {

    private static Handler handler;
    private final Context context;
    private MediaPlayer mediaPlayer;

    public ReminderPlayer(Context context) {
        this.context = context;
    }

    public void playReminder() {
        if (!isSoundSystemBusy()) {
            playSoundOrAya();
        } else {
            Toast.makeText(context, "صل على محمد", Toast.LENGTH_SHORT).show();
        }

    }

    private void playSoundOrAya() {
        stopMediaPlayer();

        mediaPlayer = new MediaPlayer();
        String typeOfNotification = getTypeOfNotification();

        if (typeOfNotification.equals("Aya")) {
            mediaPlayer = MediaPlayer.create(context, R.raw.ayasd);
        } else if (typeOfNotification.equals("Voice")) {
            mediaPlayer = MediaPlayer.create(context, R.raw.voice);
        }

        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(mp -> {
                startCountToNextRepeat();
            });
        }
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startCountToNextRepeat() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(() -> {
            playReminder();
        }, getRepeatTime());

    }


    private boolean isSoundSystemBusy() {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK ||
                (manager.getCallState() == TelephonyManager.CALL_STATE_RINGING) ||
                audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL;
    }

    private String getTypeOfNotification() {
        SharedPreferences sharedPreferences = Utils.getSharedPreferences();
        return sharedPreferences.getString("TypeOfNotification", "Voice");
    }

    private int getRepeatTime() {
        SharedPreferences sharedPreferences = Utils.getSharedPreferences();
        return sharedPreferences.getInt("repeatEvery", 60000);
    }

    public void stopReminder() {
        stopMediaPlayer();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

}
