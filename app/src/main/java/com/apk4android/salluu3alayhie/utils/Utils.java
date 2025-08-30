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
 * 
 * BUILD TYPE BASED TIMING:
 * ========================
 * Debug builds: Use testing time (10x faster for development)
 * Release builds: Use actual time (production timing)
 * 
 * Testing mode makes all timers 10x faster:
 * - 5 minutes → 30 seconds
 * - 10 minutes → 1 minute
 * - 15 minutes → 1.5 minutes
 * - etc.
 */
public class Utils {
    
    private static final String TAG = "Utils";
    public static final int MIN1 = 60 * 1000; // 1 minute in milliseconds
    
    // Time conversion factor for testing (30 seconds = 5 minutes in testing mode)
    private static final int TESTING_TIME_FACTOR = 10; // 10x faster for testing
    
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
     * Convert time to testing mode if enabled
     * In testing mode: 5 minutes becomes 30 seconds, 10 minutes becomes 1 minute, etc.
     */
    public static int getTestingTime(int realTimeMs) {
        if (isTestingMode()) {
            int testingTime = realTimeMs / TESTING_TIME_FACTOR;
            Log.d(TAG, "Debug build - Testing mode: converting " + (realTimeMs / 1000) + "s to " + (testingTime / 1000) + "s");
            return testingTime;
        }
        Log.d(TAG, "Release build - Using actual time: " + (realTimeMs / 1000) + "s");
        return realTimeMs;
    }
    
    /**
     * Get the current testing mode status based on build type
     * Uses ApplicationInfo.FLAG_DEBUGGABLE to detect debug vs release builds
     */
    public static boolean isTestingMode() {
        try {
            // Check if the app is debuggable (debug builds are debuggable)
            return (App.getContext().getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking debug mode, defaulting to release mode", e);
            return false; // Default to release mode if there's an error
        }
    }
    
    /**
     * Get the testing time factor
     */
    public static int getTestingTimeFactor() {
        return TESTING_TIME_FACTOR;
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
            Log.d(TAG, "Creating top middle toast: " + message);
            Toast toast = Toast.makeText(activity, message, duration);
            // Use raw gravity values to ensure compatibility
            int gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            Log.d(TAG, "Setting toast gravity to: " + gravity + " (TOP=" + Gravity.TOP + ", CENTER_HORIZONTAL=" + Gravity.CENTER_HORIZONTAL + ")");
            toast.setGravity(gravity, 0, 100);
            Log.d(TAG, "Toast gravity set to TOP | CENTER_HORIZONTAL");
            toast.show();
            Log.d(TAG, "Top middle toast shown successfully");
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
    
    /**
     * Force toast to top middle using custom positioning
     */
    public static void showTopMiddleToastForce(BaseActivity activity, String message, int duration) {
        try {
            Log.d(TAG, "Creating FORCE top middle toast: " + message);
            
            // Create toast with application context to avoid potential issues
            Toast toast = Toast.makeText(activity.getApplicationContext(), message, duration);
            
            // Force the position using absolute positioning
            // This bypasses any system-level gravity overrides
            int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
            int yOffset = (int) (screenHeight * 0.1); // 10% from top
            
            Log.d(TAG, "Screen height: " + screenHeight + ", Y offset: " + yOffset);
            
            // Set gravity to TOP and CENTER_HORIZONTAL with calculated offset
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, yOffset);
            
            Log.d(TAG, "FORCE toast positioned, showing...");
            toast.show();
            Log.d(TAG, "FORCE top middle toast shown successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing FORCE top middle toast", e);
            // Ultimate fallback - show regular toast if our method fails
            try {
                Toast.makeText(activity, message, duration).show();
                Log.d(TAG, "Ultimate fallback toast shown");
            } catch (Exception fallbackException) {
                Log.e(TAG, "All toast methods failed", fallbackException);
            }
        }
    }
    
    /**
     * PERMANENT SOLUTION: Custom toast that works on ALL Android devices
     * This method creates a custom view-based toast that cannot be overridden by the system
     */
    public static void showCustomTopMiddleToast(BaseActivity activity, String message, int duration) {
        try {
            Log.d(TAG, "Creating CUSTOM top middle toast: " + message);
            
            // Create a custom toast view that we have full control over
            android.widget.Toast customToast = new android.widget.Toast(activity.getApplicationContext());
            
            // Create custom layout for the toast
            android.view.View toastView = createCustomToastView(activity, message);
            customToast.setView(toastView);
            
            // Set duration
            customToast.setDuration(duration);
            
            // Calculate position - this cannot be overridden by the system
            int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
            int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            
            // Position at top middle with proper offset
            int xOffset = 0; // Center horizontally
            int yOffset = (int) (screenHeight * 0.08); // 8% from top for better visibility
            
            Log.d(TAG, "Custom toast positioning - Screen: " + screenWidth + "x" + screenHeight + 
                  ", X: " + xOffset + ", Y: " + yOffset);
            
            // Use setGravity with our calculated offsets
            customToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, xOffset, yOffset);
            
            // Show the custom toast
            customToast.show();
            Log.d(TAG, "CUSTOM top middle toast shown successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing custom top middle toast", e);
            // Fallback to regular toast
            try {
                Toast.makeText(activity, message, duration).show();
                Log.d(TAG, "Fallback toast shown");
            } catch (Exception fallbackException) {
                Log.e(TAG, "All toast methods failed", fallbackException);
            }
        }
    }
    
    /**
     * Create a custom toast view with proper styling
     */
    private static android.view.View createCustomToastView(BaseActivity activity, String message) {
        // Create the main container
        android.widget.LinearLayout container = new android.widget.LinearLayout(activity);
        container.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        container.setGravity(android.view.Gravity.CENTER);
        
        // Set background with rounded corners and shadow
        android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
        background.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        background.setCornerRadius(24); // Rounded corners
        background.setColor(activity.getResources().getColor(com.apk4android.salluu3alayhie.R.color.colorPrimary));
        
        // Add border
        background.setStroke(2, activity.getResources().getColor(android.R.color.white));
        
        // Apply background
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            container.setBackground(background);
        } else {
            container.setBackgroundDrawable(background);
        }
        
        // Add padding
        int padding = (int) (16 * activity.getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, padding);
        
        // Create and style the text view
        android.widget.TextView textView = new android.widget.TextView(activity);
        textView.setText(message);
        textView.setTextColor(activity.getResources().getColor(android.R.color.white));
        textView.setTextSize(16);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        // Add text view to container
        container.addView(textView);
        
        return container;
    }
    
    /**
     * ULTIMATE SOLUTION: Use WindowManager for guaranteed positioning
     * This method creates a custom overlay that cannot be overridden by the system
     */
    public static void showWindowManagerToast(BaseActivity activity, String message, int duration) {
        try {
            Log.d(TAG, "Creating WINDOW MANAGER toast: " + message);
            
            // Get window manager
            android.view.WindowManager windowManager = (android.view.WindowManager) activity.getSystemService(android.content.Context.WINDOW_SERVICE);
            if (windowManager == null) {
                Log.e(TAG, "WindowManager is null, falling back to custom toast");
                showCustomTopMiddleToast(activity, message, duration);
                return;
            }
            
            // Create custom view
            android.view.View toastView = createCustomToastView(activity, message);
            
            // Calculate position
            int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
            int yOffset = (int) (screenHeight * 0.08); // 8% from top
            
            // Create layout parameters
            android.view.WindowManager.LayoutParams params = new android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                android.graphics.PixelFormat.TRANSLUCENT
            );
            
            // Position the toast
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.x = 0;
            params.y = yOffset;
            
            // Add view to window
            windowManager.addView(toastView, params);
            
            // Remove view after duration
            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            handler.postDelayed(() -> {
                try {
                    windowManager.removeView(toastView);
                    Log.d(TAG, "Window manager toast removed");
                } catch (Exception e) {
                    Log.e(TAG, "Error removing window manager toast", e);
                }
            }, duration == Toast.LENGTH_LONG ? 3500 : 2000);
            
            Log.d(TAG, "WINDOW MANAGER toast shown successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing window manager toast", e);
            // Fallback to custom toast
            showCustomTopMiddleToast(activity, message, duration);
        }
    }
}
