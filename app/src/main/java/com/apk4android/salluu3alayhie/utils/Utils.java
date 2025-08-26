package com.apk4android.salluu3alayhie.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.common.App;
import com.apk4android.salluu3alayhie.common.BaseActivity;

/**
 * Utility class providing common functionality for the prayer reminder app.
 * Handles shared preferences, app sharing, and other utility methods.
 */
public class Utils {
    
    private static final String TAG = "Utils";
    public static final int MIN1 = 60 * 1000; // 1 minute in milliseconds
    
    private static final String PREF_NAME = "setTimes";
    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.apk4android.salluu3alayhie";
    private static final String SHARE_SUBJECT = "صلوا عليه";
    private static final String SHARE_MESSAGE = "ارسل التطبيق الى اصدقائك واكسب الاجر \n\n";

    /**
     * Get shared preferences instance
     */
    public static SharedPreferences getSharedPreferences() {
        return App.getContext().getSharedPreferences(PREF_NAME, 0);
    }

    /**
     * Get shared preferences editor
     */
    public static SharedPreferences.Editor getSPEditor() {
        return getSharedPreferences().edit();
    }

    /**
     * Share the app with friends
     */
    public static void shareApp(BaseActivity activity) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
            
            String shareText = SHARE_MESSAGE + PLAY_STORE_URL + " \n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            
            activity.startActivity(Intent.createChooser(shareIntent, "اختر واحد"));
            
            Log.d(TAG, "App share intent launched");
            
        } catch (Exception e) {
            Log.e(TAG, "Error sharing app", e);
        }
    }
}
