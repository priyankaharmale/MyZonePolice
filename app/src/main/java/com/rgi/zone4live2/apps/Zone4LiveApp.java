package com.rgi.zone4live2.apps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;
import com.rgi.zone4live2.utils.PreferenceConnector;

public class Zone4LiveApp extends MultiDexApplication {
    public static final String CHANNEL_ID = "zone4live";
    private static Zone4LiveApp mInstance;
    private static Context mContext;
    public static PreferenceConnector cache;
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;
    private final String PREFS_NAME = "zone4live";
    public static double lat = 0.0f, longi = 0.0f;
    public static CountDownTimer countDownTimer;
    public static boolean isTimerStart = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Zone4LiveApp.mContext = getApplicationContext();
        mInstance = this;
        sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        cache = new PreferenceConnector();
        // time restriction changed from 2 min to  15 min
        countDownTimer = new CountDownTimer(60000 * 5,1000) { // 60000 - 1 min, 600000 - 10 min , 120000 - 2 min, 60000*10 - 10 min
            @Override
            public void onTick(long l) {
                Log.e("TAG", "onTick: camera pause " );
                isTimerStart = true;
            }
            @Override
            public void onFinish() {
                Log.e("TAG", "onTick: camera finish " );
                isTimerStart = false;
            }
        };
    }

    public static synchronized Zone4LiveApp getInstance() {
        return mInstance;
    }
}

