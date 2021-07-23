package com.apk4android.salluu3alayhie.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.apk4android.salluu3alayhie.services.RepeatService;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Ahmed on 10/12/2017.
 */

public class DeviceBootReceiver extends BroadcastReceiver {

    SharedPreferences sp;
    private Context context;
    private AlarmManager am;
    private PendingIntent pi;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        sp = context.getSharedPreferences("setTimes", 0);

        int time = sp.getInt("rb5Min", 60000);
        String type = sp.getString("TypeOfNotification", "Voice");

        assert intent != null;
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Log.d("ON RECEIVE", "The time comes from radio buttons ...." + time);
            Log.d("ON RECEIVE", "The voice type comes from radio buttons type ...." + type);
            setAlarm(time);
        }

    }

    private void setAlarm(int p) {
        sp = context.getSharedPreferences("setTimes", 0);
        String type = sp.getString("TypeOfNotification", "Voice");

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("TypeOfNotification", type).apply();

        Intent i = new Intent(context, RepeatService.class);
        am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        pi = PendingIntent.getService(context, 0, i, 0);

        Calendar c = Calendar.getInstance();

        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), p, pi);
    }

}