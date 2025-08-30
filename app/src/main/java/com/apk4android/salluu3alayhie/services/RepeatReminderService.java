package com.apk4android.salluu3alayhie.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.ui.MainActivity;
import com.apk4android.salluu3alayhie.utils.ReminderPlayer;
import com.apk4android.salluu3alayhie.utils.Utils;

import java.util.Calendar;

/**
 * Foreground service for managing prayer reminder timers and audio playback.
 * Handles exact alarm scheduling with fallback support for different Android versions.
 */
public class RepeatReminderService extends Service {
    
    private static final String TAG = "RepeatReminderService";
    public static final String CHANNEL_ID = "RepeatProphetMohamedReminder";
    private static final int NOTIFICATION_ID = 1;
    private static final int DEFAULT_REPEAT_TIME = 60000; // 1 minute default
    
    private ReminderPlayer player;
    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;
    private boolean isTimerRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        
        initializeService();
        return START_STICKY; // Keep service running
    }

    /**
     * Initialize the service components
     */
    private void initializeService() {
        startTimer();
        setupPlayer();
        createServiceNotification();
    }

    /**
     * Setup the audio player with completion callback
     */
    private void setupPlayer() {
        player = new ReminderPlayer(this);
        player.setOnPlaybackCompleteListener(this::scheduleNextAlarm);
        player.playReminder();
    }

    /**
     * Start the timer with the configured repeat interval
     */
    private void startTimer() {
        int repeatTime = getRepeatTime();
        Log.d(TAG, "Starting timer with interval: " + repeatTime + "ms (" + (repeatTime / 60000) + " minutes)");
        
        setupAlarmManager();
        scheduleAlarm(repeatTime);
        
        isTimerRunning = true;
        Log.d(TAG, "Timer started successfully");
    }

    /**
     * Get the repeat time from shared preferences
     */
    private int getRepeatTime() {
        SharedPreferences sharedPreferences = Utils.getSharedPreferences();
        return sharedPreferences.getInt("repeatEvery", DEFAULT_REPEAT_TIME);
    }

    /**
     * Setup AlarmManager and PendingIntent
     */
    private void setupAlarmManager() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null, cannot schedule alarms");
            return;
        }
        
        Intent alarmIntent = new Intent(this, RepeatReminderService.class);
        alarmPendingIntent = PendingIntent.getService(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * Schedule the next alarm with the given repeat time
     */
    private void scheduleNextAlarm() {
        if (alarmManager != null && alarmPendingIntent != null) {
            int repeatTime = getRepeatTime();
            scheduleAlarm(repeatTime);
        } else {
            Log.e(TAG, "Failed to schedule next alarm - alarmManager or pendingIntent is null");
        }
    }

    /**
     * Schedule an alarm with the given delay
     */
    private void scheduleAlarm(int delayMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, delayMillis);
        
        try {
            scheduleExactAlarm(calendar.getTimeInMillis());
            Log.d(TAG, "Exact alarm scheduled in: " + delayMillis + "ms (" + (delayMillis / 60000) + " minutes)");
        } catch (SecurityException e) {
            Log.w(TAG, "Exact alarm permission denied, using inexact alarm as fallback");
            scheduleInexactAlarm(calendar.getTimeInMillis());
            Log.d(TAG, "Inexact alarm scheduled in: " + delayMillis + "ms (" + (delayMillis / 60000) + " minutes)");
        }
    }

    /**
     * Schedule an exact alarm
     */
    private void scheduleExactAlarm(long triggerTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, alarmPendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, alarmPendingIntent);
        }
    }

    /**
     * Schedule an inexact alarm as fallback
     */
    private void scheduleInexactAlarm(long triggerTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, alarmPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, alarmPendingIntent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        
        cleanup();
    }

    /**
     * Clean up resources
     */
    private void cleanup() {
        if (player != null) {
            player.stopReminder();
        }
        
        if (alarmManager != null && alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
        }
        
        isTimerRunning = false;
    }

    /**
     * Create and start the foreground notification
     */
    private void createServiceNotification() {
        createNotificationChannel();
        Notification notification = buildNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Build the notification for the foreground service
     */
    @NonNull
    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_notification_desc))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(createNotificationIntent())
                .setOngoing(true)
                .setSilent(true)
                .setVibrate(null)
                .setLights(0, 0, 0)
                .build();
    }

    /**
     * Create the pending intent for the notification
     */
    private PendingIntent createNotificationIntent() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * Create the notification channel for Android O and above
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.prayer_reminder_service),
                    NotificationManager.IMPORTANCE_LOW
            );
            
            // Configure silent notification
            serviceChannel.setSound(null, null);
            serviceChannel.enableVibration(false);
            serviceChannel.enableLights(false);
            serviceChannel.setShowBadge(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}