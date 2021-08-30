package com.apk4android.salluu3alayhie.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.common.BaseActivity;
import com.apk4android.salluu3alayhie.services.RepeatService;
import com.apk4android.salluu3alayhie.utils.CheckOnTask;

import java.util.Calendar;

public class MainActivity extends BaseActivity {

    CheckOnTask check = new CheckOnTask();
    SharedPreferences sp;
    private AlarmManager am;
    private PendingIntent pi;

    private RadioButton rb5Min, rb10Min, rb15Min, rb20Min, rb25Min,
            rb30Min;
    private RadioButton rbVoice, rbAya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title);
        initViews();
    }

    @Override
    public void initViews() {
        rb5Min = (RadioButton) findViewById(R.id.radio5Min);
        rb10Min = (RadioButton) findViewById(R.id.radio10Min);
        rb15Min = (RadioButton) findViewById(R.id.radio15Min);
        rb20Min = (RadioButton) findViewById(R.id.radio20Min);
        rb25Min = (RadioButton) findViewById(R.id.radio25Min);
        rb30Min = (RadioButton) findViewById(R.id.radio30Min);

        rbVoice = (RadioButton) findViewById(R.id.rbVoice);
        rbAya = (RadioButton) findViewById(R.id.rbAya);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.shareApp:
                shareApp();
                break;
            case R.id.aboutApp:
                openAbout();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void openAbout() {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

    private void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "صلوا عليه");
            String sAux = "ارسل التطبيق الى اصدقائك واكسب الاجر \n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.apk4android.salluu3alayhie \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "اختر واحد"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAlarm(View view) {
        if (pi == null) {
            setAlarm(60000);
        }
        am.cancel(pi);
        check.setRunOnTaskRemoved(false);
        Toast.makeText(this, "تم الايقاف", Toast.LENGTH_LONG).show();
    }

    public void startAlarm(View view) {
        sp = this.getSharedPreferences("setTimes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (rb5Min.isChecked() && (rbAya.isChecked() || rbVoice.isChecked())) {
            setAlarm(300000);
            check.setRunOnTaskRemoved(true);
            editor.putInt("rb5Min", 300000).apply();

            Toast.makeText(this, "تم تشغيل التذكير لمدة 5 دقائق", Toast.LENGTH_LONG).show();

        } else if (rb10Min.isChecked() && (rbAya.isChecked() || rbVoice.isChecked())) {
            setAlarm(600000);
            check.setRunOnTaskRemoved(true);
            editor.putInt("rb5Min", 600000).apply();
            Toast.makeText(this, "تم تشغيل التذكير لمدة 10 دقائق", Toast.LENGTH_LONG).show();

        } else if (rb15Min.isChecked() && (rbAya.isChecked() || rbVoice.isChecked())) {
            setAlarm(900000);
            check.setRunOnTaskRemoved(true);
            editor.putInt("rb5Min", 900000).apply();
            Toast.makeText(this, "تم تشغيل التذكير لمدة 15 دقيقة", Toast.LENGTH_LONG).show();


        } else if (rb20Min.isChecked() && (rbAya.isChecked() || rbVoice.isChecked())) {

            setAlarm(1200000);
            check.setRunOnTaskRemoved(true);
            editor.putInt("rb5Min", 1200000).apply();
            Toast.makeText(this, "تم تشغيل التذكير لمدة 20 دقيقة", Toast.LENGTH_LONG).show();


        } else if (rb25Min.isChecked() && (rbAya.isChecked() || rbVoice.isChecked())) {

            setAlarm(1500000);
            check.setRunOnTaskRemoved(true);
            editor.putInt("rb5Min", 1500000).apply();
            Toast.makeText(this, "تم تشغيل التذكير لمدة 25 دقيقة", Toast.LENGTH_LONG).show();


        } else if (rb30Min.isChecked() && (rbAya.isChecked() || rbVoice.isChecked())) {
            setAlarm(1800000);
            check.setRunOnTaskRemoved(true);
            editor.putInt("rb5Min", 1800000).apply();
            Toast.makeText(this, "تم تشغيل التذكير لمدة 30 دقيقة", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "لم تقم بإختيار مدة تذكير أو نوع تذكير !!", Toast.LENGTH_LONG).show();
        }

        if (rbAya.isChecked()) {
            //  check.setNotificationType("Aya");
            editor.putString("TypeOfNotification", "Aya").apply();
        } else if (rbVoice.isChecked()) {
            // check.setNotificationType("Voice");
            editor.putString("TypeOfNotification", "Voice").apply();
        }

    }


    private void setAlarm(int p) {
        Intent i = new Intent(this, RepeatService.class);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        pi = PendingIntent.getService(MainActivity.this, 0, i, 0);
        Calendar c = Calendar.getInstance();
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), p, pi);
    }


}