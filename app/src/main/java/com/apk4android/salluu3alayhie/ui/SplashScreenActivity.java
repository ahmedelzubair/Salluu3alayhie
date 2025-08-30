package com.apk4android.salluu3alayhie.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.common.BaseActivity;

@SuppressWarnings("ALL")
public class SplashScreenActivity extends BaseActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        RelativeLayout r = (RelativeLayout) findViewById(R.id.RLSplash);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk <= Build.VERSION_CODES.LOLLIPOP && r != null) {
            try {
                r.setBackgroundColor(Color.parseColor("#f7c949"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}