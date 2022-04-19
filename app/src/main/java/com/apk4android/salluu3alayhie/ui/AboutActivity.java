package com.apk4android.salluu3alayhie.ui;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.apk4android.salluu3alayhie.R;
import com.apk4android.salluu3alayhie.common.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title);

        RelativeLayout r = findViewById(R.id.RLAbout);
        TextView tvAbout = findViewById(R.id.wadelzbuair);

        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://wadelzubair.net/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk <= Build.VERSION_CODES.LOLLIPOP) {
            try {
                r.setBackgroundColor(Color.parseColor("#6b6b6b"));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

//    public void goToLink(View view) {
//        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.apk4android.com"));
//        startActivity(browser);
//    }
}
