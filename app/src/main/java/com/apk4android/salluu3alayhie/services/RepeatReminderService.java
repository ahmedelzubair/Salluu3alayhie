package com.apk4android.salluu3alayhie.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.ui.MainActivity;
import com.apk4android.salluu3alayhie.utils.ReminderPlayer;

/**
 * Created by MyPC on 8/16/2017.
 */

public class RepeatReminderService extends Service {
    public static final String CHANNEL_ID = "RepeatProphetMohamedReminder";

    private static final String TAG = "Removed";
    private ReminderPlayer player;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = new ReminderPlayer(this);
        createServiceNotification();

        player.playReminder();

        return START_NOT_STICKY;
    }


    private void createServiceNotification() {
        createNotificationChannel();
        Notification notification = buildNotification(getPendingIntent());
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @NonNull
    private Notification buildNotification(PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.app_notification_desc))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
    }

    private PendingIntent getPendingIntent() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stopReminder();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}