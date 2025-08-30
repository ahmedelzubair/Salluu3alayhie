package com.apk4android.salluu3alayhie.utils;

import android.content.Context;
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
    
    /**
     * UNIVERSAL SOLUTION: Show toast at top middle from ANY context
     * This method works from activities, services, or any other context
     */
    public static void showTopMiddleToastUniversal(Context context, String message, int duration) {
        try {
            Log.d(TAG, "Creating UNIVERSAL top middle toast: " + message);
            
            // Method 1: Try the floating overlay solution
            if (showCustomFloatingToast(context, message, duration)) {
                Log.d(TAG, "Custom floating toast shown successfully");
                return;
            }
            
            // Method 2: Try to create a custom toast with positioning
            if (tryCustomToastWithPositioning(context, message, duration)) {
                Log.d(TAG, "Custom positioned toast shown successfully");
                return;
            }
            
            // Method 3: Try to use application context with positioning
            if (tryApplicationContextToast(context, message, duration)) {
                Log.d(TAG, "Application context positioned toast shown successfully");
                return;
            }
            
            // Method 4: Ultimate fallback - basic toast but try to position it
            Log.d(TAG, "All custom methods failed, using positioned fallback");
            showPositionedFallbackToast(context, message, duration);
            
        } catch (Exception e) {
            Log.e(TAG, "All toast methods failed", e);
            // Last resort - basic toast
            try {
                Log.d(TAG, "Showing basic fallback toast at default position");
                Toast.makeText(context, message, duration).show();
            } catch (Exception fallbackException) {
                Log.e(TAG, "Even basic toast failed", fallbackException);
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
     * Show custom top middle toast for activities
     */
    public static void showCustomTopMiddleToast(BaseActivity activity, String message, int duration) {
        try {
            Log.d(TAG, "Creating custom top middle toast: " + message);
            
            // Create a custom toast view that we have full control over
            android.view.View toastView = createCustomToastView(activity, message);
            
            // Get window manager
            android.view.WindowManager windowManager = (android.view.WindowManager) activity.getSystemService(android.content.Context.WINDOW_SERVICE);
            if (windowManager == null) {
                Log.e(TAG, "WindowManager is null, falling back to regular toast");
                Toast.makeText(activity, message, duration).show();
                return;
            }
            
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
                    Log.d(TAG, "Custom toast removed");
                } catch (Exception e) {
                    Log.e(TAG, "Error removing custom toast", e);
                }
            }, duration == Toast.LENGTH_LONG ? 5000 : 3000);
            
            Log.d(TAG, "Custom top middle toast shown successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing custom top middle toast", e);
            // Fallback to regular toast
            Toast.makeText(activity, message, duration).show();
        }
    }
    
    /**
     * NUCLEAR OPTION: Create a custom floating view that looks like a toast
     * This completely bypasses Android 15's toast restrictions
     */
    private static boolean showCustomFloatingToast(Context context, String message, int duration) {
        try {
            Log.d(TAG, "Creating floating toast: " + message);
            
            // Check if SYSTEM_ALERT_WINDOW permission is granted
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && 
                !android.provider.Settings.canDrawOverlays(context)) {
                Log.d(TAG, "SYSTEM_ALERT_WINDOW permission not granted, falling back to default toast");
                return false;
            }
            
            // Get the window manager
            android.view.WindowManager windowManager = (android.view.WindowManager) context.getSystemService(android.content.Context.WINDOW_SERVICE);
            if (windowManager == null) {
                Log.e(TAG, "WindowManager is null");
                return false;
            }
            
            // Create a custom view that looks like a toast
            android.view.View floatingView = createFloatingToastView(context, message);
            
            // Calculate position - top middle of screen
            android.util.DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int screenHeight = displayMetrics.heightPixels;
            
            // Position at top middle (8% from top)
            int yOffset = (int) (screenHeight * 0.08);
            
            // Create window parameters
            android.view.WindowManager.LayoutParams params = new android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Use overlay type
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.graphics.PixelFormat.TRANSLUCENT
            );
            
            // Set position
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.x = 0;
            params.y = yOffset;
            
            // Add the view to the window
            windowManager.addView(floatingView, params);
            
            // Remove after duration
            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            handler.postDelayed(() -> {
                try {
                    windowManager.removeView(floatingView);
                    Log.d(TAG, "Floating toast removed successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Error removing floating toast", e);
                }
            }, duration == Toast.LENGTH_LONG ? 5000 : 3000);
            
            Log.d(TAG, "Floating toast shown successfully at y: " + yOffset);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing floating toast", e);
            return false;
        }
    }
    
    /**
     * Create a floating view that looks like a toast
     */
    private static android.view.View createFloatingToastView(Context context, String message) {
        // Create the main container
        android.widget.LinearLayout container = new android.widget.LinearLayout(context);
        container.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        container.setOrientation(android.widget.LinearLayout.HORIZONTAL); // Changed to horizontal for icon + text
        container.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        // Set background with rounded corners and shadow
        android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
        background.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        background.setCornerRadius(24); // Rounded corners
        background.setColor(context.getResources().getColor(com.apk4android.salluu3alayhie.R.color.colorPrimary));
        
        // Add border
        background.setStroke(2, context.getResources().getColor(android.R.color.white));
        
        // Apply background
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            container.setBackground(background);
        } else {
            container.setBackgroundDrawable(background);
        }
        
        // Add padding
        int padding = (int) (16 * context.getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, padding);
        
        // Create and style the icon
        android.widget.ImageView iconView = new android.widget.ImageView(context);
        iconView.setImageResource(com.apk4android.salluu3alayhie.R.mipmap.ic_launcher); // Use the actual app icon
        iconView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            (int) (24 * context.getResources().getDisplayMetrics().density), // 24dp width
            (int) (24 * context.getResources().getDisplayMetrics().density)  // 24dp height
        ));
        iconView.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        
        // Add margin between icon and text
        android.widget.LinearLayout.LayoutParams iconParams = (android.widget.LinearLayout.LayoutParams) iconView.getLayoutParams();
        iconParams.setMargins(0, 0, (int) (8 * context.getResources().getDisplayMetrics().density), 0); // 8dp right margin
        
        // Create and style the text view
        android.widget.TextView textView = new android.widget.TextView(context);
        textView.setText(message);
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        textView.setTextSize(16);
        textView.setGravity(android.view.Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        // Add icon and text view to container
        container.addView(iconView);
        container.addView(textView);
        
        return container;
    }

    /**
     * Try to create a custom toast with positioning
     */
    private static boolean tryCustomToastWithPositioning(Context context, String message, int duration) {
        try {
            Log.d(TAG, "Trying custom toast with positioning");
            
            // Create toast with application context
            Toast toast = Toast.makeText(context.getApplicationContext(), message, duration);
            
            // Try to position it at the top middle
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
            
            // Show the toast
            toast.show();
            Log.d(TAG, "Custom positioned toast shown");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Custom positioned toast failed", e);
            return false;
        }
    }
    
    /**
     * Try to use application context with positioning
     */
    private static boolean tryApplicationContextToast(Context context, String message, int duration) {
        try {
            Log.d(TAG, "Trying application context toast with positioning");
            
            // Create toast with application context
            Toast toast = Toast.makeText(context.getApplicationContext(), message, duration);
            
            // Try different positioning strategies
            int[] yOffsets = {50, 100, 150, 200};
            
            for (int yOffset : yOffsets) {
                try {
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, yOffset);
                    toast.show();
                    Log.d(TAG, "Application context positioned toast shown with yOffset: " + yOffset);
                    return true;
                } catch (Exception e) {
                    Log.d(TAG, "YOffset " + yOffset + " failed, trying next");
                    continue;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "Application context toast failed", e);
            return false;
        }
    }
    
    /**
     * Show positioned fallback toast
     */
    private static void showPositionedFallbackToast(Context context, String message, int duration) {
        try {
            Log.d(TAG, "Showing positioned fallback toast");
            
            // Create basic toast
            Toast toast = Toast.makeText(context, message, duration);
            
            // Try to position it at the top with multiple attempts
            boolean positioned = false;
            int[] yOffsets = {50, 100, 150, 200, 250};
            
            for (int yOffset : yOffsets) {
                try {
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, yOffset);
                    positioned = true;
                    Log.d(TAG, "Fallback toast positioned with yOffset: " + yOffset);
                    break;
                } catch (Exception e) {
                    Log.d(TAG, "Fallback positioning failed for yOffset: " + yOffset);
                    continue;
                }
            }
            
            // Show the toast
            toast.show();
            
            if (positioned) {
                Log.d(TAG, "Positioned fallback toast shown successfully");
            } else {
                Log.d(TAG, "Fallback toast shown without positioning");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Positioned fallback toast failed", e);
            // Show basic toast as last resort
            try {
                Toast.makeText(context, message, duration).show();
            } catch (Exception basicException) {
                Log.e(TAG, "Even basic fallback toast failed", basicException);
            }
        }
    }
}
