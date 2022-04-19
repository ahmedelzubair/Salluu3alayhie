package com.apk4android.salluu3alayhie.utils;

import android.content.Intent;
import android.content.SharedPreferences;

import com.apk4android.salluu3alayhie.common.App;
import com.apk4android.salluu3alayhie.common.BaseActivity;

public class Utils {
    public static final int MIN1 = 60 * 1000;

    public static SharedPreferences getSharedPreferences() {
        return App.getContext().getSharedPreferences("setTimes", 0);
    }

    public static SharedPreferences.Editor getSPEditor() {
        return getSharedPreferences().edit();
    }


    public static void shareApp(BaseActivity activity) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "صلوا عليه");
            String sAux = "ارسل التطبيق الى اصدقائك واكسب الاجر \n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.apk4android.salluu3alayhie \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            activity.startActivity(Intent.createChooser(i, "اختر واحد"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
