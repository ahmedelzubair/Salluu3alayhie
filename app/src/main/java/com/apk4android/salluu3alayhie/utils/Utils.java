package com.apk4android.salluu3alayhie.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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
    private static final String SHARE_SUBJECT = "صلوا عليه"; // This will be replaced with string resource
    private static final String SHARE_MESSAGE = "ارسل التطبيق الى اصدقائك واكسب الاجر \n\n"; // This will be replaced with string resource

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
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "صلوا عليه"); // Using direct string for now
            shareIntent.putExtra(Intent.EXTRA_TEXT, "ارسل التطبيق الى اصدقائك واكسب الاجر \n\n" + PLAY_STORE_URL); // Using direct string for now
            
            activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.choose_one)));
            
            Log.d(TAG, "App share intent launched");
            
        } catch (Exception e) {
            Log.e(TAG, "Error sharing app", e);
        }
    }
    
    /**
     * Show toast message at the top middle of the screen
     */
    public static void showTopMiddleToast(BaseActivity activity, String message, int duration) {
        try {
            Toast toast = Toast.makeText(activity, message, duration);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
            toast.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing top middle toast", e);
        }
    }
    
    /**
     * Show toast message at the top middle of the screen with default duration
     */
    public static void showTopMiddleToast(BaseActivity activity, String message) {
        showTopMiddleToast(activity, message, Toast.LENGTH_LONG);
    }
}
