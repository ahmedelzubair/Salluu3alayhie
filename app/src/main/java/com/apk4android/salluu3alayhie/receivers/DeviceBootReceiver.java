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
 * Created by Ahmed on 10/12/2017.
 */

public class DeviceBootReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    private Context context;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setTimes", 0);

        int time = sharedPreferences.getInt("rb5Min", 60000);
        String type = sharedPreferences.getString("TypeOfNotification", "Voice");

        assert intent != null;
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Log.d("ON RECEIVE", "The time comes from radio buttons ...." + time);
            Log.d("ON RECEIVE", "The voice type comes from radio buttons type ...." + type);
            setAlarm(time);
        }

    }

    private void setAlarm(int p) {
        sharedPreferences = context.getSharedPreferences("setTimes", 0);
        String type = sharedPreferences.getString("TypeOfNotification", "Voice");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TypeOfNotification", type).apply();

        Intent i = new Intent(context, RepeatReminderService.class);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(context, 0, i, 0);

        Calendar c = Calendar.getInstance();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), p, pendingIntent);
    }

}