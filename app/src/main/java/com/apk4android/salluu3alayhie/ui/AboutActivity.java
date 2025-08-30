package com.apk4android.salluu3alayhie.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.common.BaseActivity;
import com.apk4android.salluu3alayhie.utils.Utils;

/**
 * About activity displaying app information, developer details, and features.
 * Enhanced with modern design and better user interaction.
 */
public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";
    private static final String WEBSITE_URL = "https://ahmedelzubair.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeViews();
        setupClickListeners();
        applyLegacyBackgroundSupport();
    }

    /**
     * Initialize all view references
     */
    private void initializeViews() {
        RelativeLayout r = findViewById(R.id.RLAbout);
        TextView tvAbout = findViewById(R.id.wadelzbuair);
        
        if (r == null) {
            Log.e(TAG, "RelativeLayout not found");
            return;
        }
        
        if (tvAbout == null) {
            Log.e(TAG, "Website TextView not found");
            return;
        }
    }

    /**
     * Setup click listeners for interactive elements
     */
    private void setupClickListeners() {
        TextView websiteLink = findViewById(R.id.wadelzbuair);
        if (websiteLink != null) {
            websiteLink.setOnClickListener(v -> openWebsite());
        }
    }

    /**
     * Open the developer website
     */
    private void openWebsite() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(WEBSITE_URL));
            startActivity(intent);
            Log.d(TAG, "Website opened successfully: " + WEBSITE_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error opening website: " + e.getMessage(), e);
            Utils.showTopMiddleToast(this, "Unable to open website", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Apply legacy background support for older Android versions
     */
    private void applyLegacyBackgroundSupport() {
        RelativeLayout r = findViewById(R.id.RLAbout);
        if (r == null) return;

        int sdk = Build.VERSION.SDK_INT;
        if (sdk <= Build.VERSION_CODES.LOLLIPOP) {
            try {
                r.setBackgroundColor(Color.parseColor("#6b6b6b"));
                Log.d(TAG, "Legacy background applied for API level: " + sdk);
            } catch (Exception ex) {
                Log.e(TAG, "Error applying legacy background", ex);
            }
        }
    }

    /**
     * Handle back button press
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "About activity closed");
    }
}
