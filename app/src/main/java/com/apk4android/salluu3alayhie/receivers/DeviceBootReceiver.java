package com.apk4android.salluu3alayhie.receivers;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.apk4android.salluu3alayhie.services.RepeatReminderService;

import java.util.Calendar;

/**
 * Broadcast receiver for handling device boot completion.
 * Restarts the prayer reminder service when the device reboots.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    private static final String TAG = "DeviceBootReceiver";
    private static final String PREF_NAME = "setTimes";
    private static final String KEY_REPEAT_EVERY = "repeatEvery";
    private static final String KEY_NOTIFICATION_TYPE = "TypeOfNotification";
    private static final String DEFAULT_NOTIFICATION_TYPE = "Voice";
    private static final int DEFAULT_REPEAT_TIME = 60000; // 1 minute

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.w(TAG, "Received null intent or action");
            return;
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device boot completed, restarting prayer reminder service");
            restartPrayerReminderService(context);
        }
    }

    /**
     * Restart the prayer reminder service with saved preferences
     */
    private void restartPrayerReminderService(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        // Get saved timer interval
        int repeatTime = sharedPreferences.getInt(KEY_REPEAT_EVERY, DEFAULT_REPEAT_TIME);
        String notificationType = sharedPreferences.getString(KEY_NOTIFICATION_TYPE, DEFAULT_NOTIFICATION_TYPE);
        
        Log.d(TAG, "Restarting service with interval: " + repeatTime + "ms, type: " + notificationType);
        
        // Schedule the alarm
        scheduleAlarm(context, repeatTime);
    }

    /**
     * Schedule the alarm using AlarmManager
     */
    private void scheduleAlarm(Context context, int repeatTime) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (alarmManager == null) {
                Log.e(TAG, "AlarmManager is null");
                return;
            }

            // Create intent for the service
            Intent serviceIntent = new Intent(context, RepeatReminderService.class);
            PendingIntent pendingIntent = PendingIntent.getService(
                context, 
                0, 
                serviceIntent, 
                PendingIntent.FLAG_IMMUTABLE
            );

            // Calculate next trigger time
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, repeatTime);

            // Schedule the alarm
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                repeatTime,
                pendingIntent
            );

            Log.d(TAG, "Alarm scheduled successfully for: " + repeatTime + "ms");
            
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling alarm", e);
        }
    }
}