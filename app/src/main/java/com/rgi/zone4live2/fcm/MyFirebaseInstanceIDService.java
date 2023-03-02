package com.rgi.zone4live2.fcm;


import android.content.Intent;
import android.content.SharedPreferences;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rgi.zone4live2.utils.Constant;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPref(refreshedToken);


        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(FCM.PUSH_NOTIFICATION);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(FCM.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constant.FCM_ID, token);
        editor.commit();
    }


}