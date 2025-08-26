package com.apk4android.salluu3alayhie.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.apk4android.salluu3alayhie.R;

/**
 * Handles audio playback for prayer reminders with proper audio focus management.
 * Supports both Quran verses and voice reminders with system state checking.
 */
public class ReminderPlayer {
    
    private static final String TAG = "ReminderPlayer";
    private static final String DEFAULT_NOTIFICATION_TYPE = "Voice";
    
    private final Context context;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private OnPlaybackCompleteListener onPlaybackCompleteListener;

    public interface OnPlaybackCompleteListener {
        void onPlaybackComplete();
    }

    public ReminderPlayer(@NonNull Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        if (this.audioManager == null) {
            Log.e(TAG, "AudioManager is null, this may cause issues");
        }
    }

    public void setOnPlaybackCompleteListener(OnPlaybackCompleteListener listener) {
        this.onPlaybackCompleteListener = listener;
    }

    /**
     * Start playing the prayer reminder audio
     */
    public void playReminder() {
        if (!isSoundSystemBusy()) {
            playSoundOrAya();
        } else {
            Log.d(TAG, "Sound system busy, showing toast instead");
            Toast.makeText(context, "صل على محمد", Toast.LENGTH_SHORT).show();
            notifyPlaybackComplete();
        }
    }

    /**
     * Play the selected audio (Quran verse or voice reminder)
     */
    private void playSoundOrAya() {
        stopMediaPlayer();
        
        String typeOfNotification = getTypeOfNotification();
        Log.d(TAG, "Playing notification type: " + typeOfNotification);
        
        int resourceId = getAudioResourceId(typeOfNotification);
        mediaPlayer = MediaPlayer.create(context, resourceId);
        
        if (mediaPlayer != null) {
            setupMediaPlayer();
        } else {
            Log.e(TAG, "Failed to create MediaPlayer");
            notifyPlaybackComplete();
        }
    }

    /**
     * Get the audio resource ID based on notification type
     */
    private int getAudioResourceId(String typeOfNotification) {
        if ("Aya".equals(typeOfNotification)) {
            return R.raw.ayasd;
        } else {
            return R.raw.voice;
        }
    }

    /**
     * Setup MediaPlayer with proper audio configuration
     */
    private void setupMediaPlayer() {
        // Set audio stream to music to avoid interference with notifications
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        
        // Request audio focus
        int result = requestAudioFocus();
        
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d(TAG, "Audio focus granted");
            setupPlaybackListeners();
        } else {
            Log.w(TAG, "Audio focus not granted, but will attempt playback");
            setupPlaybackListeners();
        }
    }

    /**
     * Request audio focus for playback
     */
    private int requestAudioFocus() {
        if (audioManager == null) {
            Log.e(TAG, "AudioManager is null, cannot request audio focus");
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
        
        return audioManager.requestAudioFocus(
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    handleAudioFocusChange(focusChange);
                }
            },
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        );
    }

    /**
     * Handle audio focus changes
     */
    private void handleAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "Audio focus lost, pausing playback");
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "Audio focus gained, resuming playback");
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                break;
        }
    }

    /**
     * Setup MediaPlayer listeners
     */
    private void setupPlaybackListeners() {
        mediaPlayer.setOnPreparedListener(mp -> {
            Log.d(TAG, "MediaPlayer prepared, starting playback");
            mp.start();
        });
        
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d(TAG, "Playback completed");
            releaseAudioFocus();
            notifyPlaybackComplete();
        });
        
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra);
            releaseAudioFocus();
            notifyPlaybackComplete();
            return true;
        });
    }

    /**
     * Release audio focus
     */
    private void releaseAudioFocus() {
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
        } else {
            Log.w(TAG, "AudioManager is null, cannot release audio focus");
        }
    }

    /**
     * Notify that playback is complete
     */
    private void notifyPlaybackComplete() {
        if (onPlaybackCompleteListener != null) {
            onPlaybackCompleteListener.onPlaybackComplete();
        }
    }

    /**
     * Stop and release the MediaPlayer
     */
    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping MediaPlayer", e);
            } finally {
                mediaPlayer = null;
            }
        }
    }

    /**
     * Check if the sound system is busy (phone call, etc.)
     */
    private boolean isSoundSystemBusy() {
        if (audioManager == null) {
            Log.w(TAG, "AudioManager is null, assuming sound system is not busy");
            return false;
        }
        
        // Check ringer mode first
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            Log.d(TAG, "Ringer mode is not normal");
            return true;
        }
        
        // Check phone state if permission is available
        if (hasPhoneStatePermission()) {
            return isPhoneCallActive();
        }
        
        return false;
    }

    /**
     * Check if we have permission to read phone state
     */
    private boolean hasPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) 
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permission not required on older Android versions
    }

    /**
     * Check if a phone call is active
     */
    private boolean isPhoneCallActive() {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int callState = manager.getCallState();
            
            boolean isCallActive = callState == TelephonyManager.CALL_STATE_OFFHOOK || 
                                 callState == TelephonyManager.CALL_STATE_RINGING;
            
            if (isCallActive) {
                Log.d(TAG, "Phone call is active, call state: " + callState);
            }
            
            return isCallActive;
        } catch (SecurityException e) {
            Log.w(TAG, "SecurityException when checking phone state", e);
            return false;
        }
    }

    /**
     * Get the notification type from shared preferences
     */
    private String getTypeOfNotification() {
        SharedPreferences sharedPreferences = Utils.getSharedPreferences();
        return sharedPreferences.getString("TypeOfNotification", DEFAULT_NOTIFICATION_TYPE);
    }

    /**
     * Stop the reminder and clean up resources
     */
    public void stopReminder() {
        Log.d(TAG, "Stopping reminder");
        stopMediaPlayer();
        releaseAudioFocus();
    }
}
