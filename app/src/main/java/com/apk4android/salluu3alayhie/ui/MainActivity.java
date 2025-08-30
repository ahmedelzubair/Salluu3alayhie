package com.apk4android.salluu3alayhie.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.common.BaseActivity;
import com.apk4android.salluu3alayhie.services.RepeatReminderService;
import com.apk4android.salluu3alayhie.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity for the prayer reminder app.
 * Handles timer selection, permission requests, and service management.
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    
    // Timer radio buttons
    private RadioButton rb5Min, rb10Min, rb15Min, rb20Min, rb25Min, rb30Min;
    private RadioButton rbVoice, rbAya;
    
    // Permission request launcher
    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (Boolean granted : permissions.values()) {
                    if (!granted) {
                        allGranted = false;
                        break;
                    }
                }
                
                if (allGranted) {
                    Log.d(TAG, "All permissions granted");
                    startRepeatingService();
                } else {
                    Log.w(TAG, "Some permissions denied, starting service with limited functionality");
                    Toast.makeText(this, "سيتم تشغيل التطبيق مع وظائف محدودة", Toast.LENGTH_LONG).show();
                    startRepeatingService();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setViewsListeners();
    }



    @Override
    public void setViewsListeners() {
        super.setViewsListeners();

        // Setup notification type listeners
        rbAya.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Utils.getSPEditor().putString("TypeOfNotification", "Aya").apply();
                Log.d(TAG, "Notification type set to Aya");
            }
        });

        rbVoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Utils.getSPEditor().putString("TypeOfNotification", "Voice").apply();
                Log.d(TAG, "Notification type set to Voice");
            }
        });
    }

    /**
     * Handle about button click
     */
    public void openAbout(View view) {
        openAbout();
    }

    /**
     * Handle share button click
     */
    public void shareApp(View view) {
        Utils.shareApp(this);
    }

    @Override
    public void initViews() {
        // Initialize timer radio buttons
        rb5Min = findViewById(R.id.radio5Min);
        rb10Min = findViewById(R.id.radio10Min);
        rb15Min = findViewById(R.id.radio15Min);
        rb20Min = findViewById(R.id.radio20Min);
        rb25Min = findViewById(R.id.radio25Min);
        rb30Min = findViewById(R.id.radio30Min);

        // Initialize notification type radio buttons
        rbVoice = findViewById(R.id.rbVoice);
        rbAya = findViewById(R.id.rbAya);
    }

    /**
     * Handle start alarm button click
     */
    public void startAlarm(View view) {
        int selectedTime = getSelectedTimerValue();
        
        if (selectedTime > 0) {
            saveTimerPreference(selectedTime);
            showTimerStartedMessage(selectedTime);
            
            Log.d(TAG, "Timer started: " + selectedTime + "ms (" + (selectedTime / Utils.MIN1) + " minutes)");
            
            checkExactAlarmPermission();
            checkAndRequestPermissions();
        } else {
            Toast.makeText(this, "يرجى اختيار وقت للتذكير", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get the selected timer value in milliseconds
     */
    private int getSelectedTimerValue() {
        if (rb5Min.isChecked()) return 5 * Utils.MIN1;
        if (rb10Min.isChecked()) return 10 * Utils.MIN1;
        if (rb15Min.isChecked()) return 15 * Utils.MIN1;
        if (rb20Min.isChecked()) return 20 * Utils.MIN1;
        if (rb25Min.isChecked()) return 25 * Utils.MIN1;
        if (rb30Min.isChecked()) return 30 * Utils.MIN1;
        return 0;
    }

    /**
     * Save timer preference to shared preferences
     */
    private void saveTimerPreference(int timerValue) {
        Utils.getSPEditor().putInt("repeatEvery", timerValue).apply();
    }

    /**
     * Show timer started message
     */
    private void showTimerStartedMessage(int timerValue) {
        int minutes = timerValue / Utils.MIN1;
        String message = String.format("تم تشغيل التذكير لكل %d %s", minutes, 
                minutes == 1 ? "دقيقة" : "دقائق");
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Handle stop alarm button click
     */
    public void stopAlarm(View view) {
        stopService(new Intent(this, RepeatReminderService.class));
        Toast.makeText(this, "تم الايقاف", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Timer stopped");
    }

    /**
     * Check and request exact alarm permission for Android 12+
     */
    private void checkExactAlarmPermission() {
        Log.d(TAG, "Checking exact alarm permission...");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
            if (alarmManager != null) {
                boolean canScheduleExact = alarmManager.canScheduleExactAlarms();
                Log.d(TAG, "Can schedule exact alarms: " + canScheduleExact);
                if (!canScheduleExact) {
                    Log.d(TAG, "Showing exact alarm permission dialog");
                    showExactAlarmPermissionDialog();
                } else {
                    Log.d(TAG, "Exact alarm permission already granted");
                }
            } else {
                Log.e(TAG, "AlarmManager is null");
            }
        } else {
            Log.d(TAG, "Device API level < 31, no exact alarm permission needed");
        }
    }

    /**
     * Show dialog to guide user to exact alarm settings
     */
    private void showExactAlarmPermissionDialog() {
        Log.d(TAG, "Creating exact alarm permission dialog");
        try {
            AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppTheme)
                .setTitle("إذن مطلوب")
                .setMessage("يحتاج التطبيق إلى إذن لتشغيل التنبيهات الدقيقة. يرجى الذهاب إلى الإعدادات والسماح للتطبيق بتشغيل التنبيهات الدقيقة.")
                .setPositiveButton("الإعدادات", (dialogInterface, which) -> {
                    Log.d(TAG, "User clicked Settings button");
                    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                })
                .setNegativeButton("إلغاء", (dialogInterface, which) -> {
                    Log.d(TAG, "User clicked Cancel button");
                })
                .setCancelable(false)
                .create();
            
            // Set dialog text colors to ensure visibility
            dialog.setOnShowListener(dialogInterface -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            });
            
            Log.d(TAG, "Showing dialog...");
            dialog.show();
            Log.d(TAG, "Dialog shown successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error showing dialog: " + e.getMessage(), e);
        }
    }

    /**
     * Check and request runtime permissions
     */
    private void checkAndRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        
        // Check READ_PHONE_STATE permission (Android 6+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE);
            }
        }
        
        // Check POST_NOTIFICATIONS permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        
        if (!permissionsToRequest.isEmpty()) {
            Log.d(TAG, "Requesting permissions: " + permissionsToRequest);
            requestMultiplePermissionsLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            Log.d(TAG, "All permissions already granted");
            startRepeatingService();
        }
    }

    /**
     * Start the repeating reminder service
     */
    private void startRepeatingService() {
        Intent repeatServiceIntent = new Intent(this, RepeatReminderService.class);
        startService(repeatServiceIntent);
        Log.d(TAG, "Service started");
    }

    /**
     * Open about activity
     */
    private void openAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }


}