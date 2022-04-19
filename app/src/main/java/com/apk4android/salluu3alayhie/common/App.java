package com.apk4android.salluu3alayhie.common;

import android.app.Application;
import android.content.Context;

import com.apk4android.salluu3alayhie.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

// we have extended TelrApplication because extending android Application class is causing
// manifest merging failure
public class App extends Application {

    private static final String TAG = "AppTest.App";
    public static boolean isAppInForeground = false;
    private static Application app;

    public static Context getContext() {
        return app.getApplicationContext();
    }

    public static Application getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        // fonts
        initAppFonts();
    }


    private void initAppFonts() {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/droid.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build())).build());
    }

}
