package com.apk4android.salluu3alayhie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.common.BaseActivity;
import com.apk4android.salluu3alayhie.services.RepeatReminderService;
import com.apk4android.salluu3alayhie.utils.Utils;

public class MainActivity extends BaseActivity {

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
        setViewsListeners();
    }

    @Override
    public void setViewsListeners() {
        super.setViewsListeners();

        rbAya.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Utils.getSPEditor().putString("TypeOfNotification", "Aya").apply();
            }
        });

        rbVoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Utils.getSPEditor().putString("TypeOfNotification", "Voice").apply();
            }
        });
    }

    @Override
    public void initViews() {
        rb5Min = findViewById(R.id.radio5Min);
        rb10Min = findViewById(R.id.radio10Min);
        rb15Min = findViewById(R.id.radio15Min);
        rb20Min = findViewById(R.id.radio20Min);
        rb25Min = findViewById(R.id.radio25Min);
        rb30Min = findViewById(R.id.radio30Min);

        rbVoice = findViewById(R.id.rbVoice);
        rbAya = findViewById(R.id.rbAya);
    }

    private void openAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void startAlarm(View view) {
        if (rb5Min.isChecked()) {
            Utils.getSPEditor().putInt("repeatEvery", 5 * Utils.MIN1).apply();
            Toast.makeText(this, "تم تشغيل التذكير لكل 5 دقائق", Toast.LENGTH_LONG).show();
        }
        if (rb10Min.isChecked()) {
            Utils.getSPEditor().putInt("repeatEvery", 10 * Utils.MIN1).apply();
            Toast.makeText(this, "تم تشغيل التذكير لكل 10 دقائق", Toast.LENGTH_LONG).show();
        }
        if (rb15Min.isChecked()) {
            Utils.getSPEditor().putInt("repeatEvery", 15 * Utils.MIN1).apply();
            Toast.makeText(this, "تم تشغيل التذكير لكل 15 دقيقة", Toast.LENGTH_LONG).show();
        }
        if (rb20Min.isChecked()) {
            Utils.getSPEditor().putInt("repeatEvery", 20 * Utils.MIN1).apply();
            Toast.makeText(this, "تم تشغيل التذكير لكل 20 دقيقة", Toast.LENGTH_LONG).show();
        }
        if (rb25Min.isChecked()) {
            Utils.getSPEditor().putInt("repeatEvery", 25 * Utils.MIN1).apply();
            Toast.makeText(this, "تم تشغيل التذكير لكل 25 دقيقة", Toast.LENGTH_LONG).show();
        }
        if (rb30Min.isChecked()) {
            Utils.getSPEditor().putInt("repeatEvery", 30 * Utils.MIN1).apply();
            Toast.makeText(this, "تم تشغيل التذكير لكل 30 دقيقة", Toast.LENGTH_LONG).show();
        }
        startRepeatingService();
    }

    private void startRepeatingService() {
        Intent repeatServiceIntent = new Intent(this, RepeatReminderService.class);
        startService(repeatServiceIntent);
    }

    public void stopAlarm(View view) {
        stopService(new Intent(this, RepeatReminderService.class));
        Toast.makeText(this, "تم الايقاف", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.shareApp) {
            Utils.shareApp(this);
        } else if (id == R.id.aboutApp) {
            openAbout();
        }
        return super.onOptionsItemSelected(item);
    }

}