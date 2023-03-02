
package com.rgi.zone4live2.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.activites.MainActivity;
import com.rgi.zone4live2.apps.Zone4LiveApp;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String TAG = "MyFirebaseMessagingService";
    NotificationUtils notificationUtils;
    int notificationId = 0;
    String notiString, newNotiString;
    ArrayList<String> notificationIdenty = new ArrayList<>();

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("Firebase", "in remote" + remoteMessage.getData());
        Log.e("MyFirebaseMessaging", "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData());
            String str =  remoteMessage.getData().toString().replace("<br>","\n");
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Zone4LiveApp.cache.writeString(getApplicationContext(), Constant.TOKEN, token);
        Log.e(TAG, "onNewToken: " + token);
        Intent registrationComplete = new Intent(Constant.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    private void handleNotification(String message,String title) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent("pushNotification");
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            showNotificationMessage(getApplicationContext(), title, message, "", resultIntent);
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void handleDataMessage(JSONObject json) {
        String body, title, notificationType,companyId;
        try {
            body = json.getString("body");
            title = json.getString("title");
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//            resultIntent.putExtra(Constant.NOTIFICATIONTYPE, notificationType);
//            resultIntent.putExtra("CompanyId", companyId);
                showNotificationMessage(getApplicationContext(), title, body, "", resultIntent);

        } catch (JSONException je) {
            je.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        showNotificationMessage(title, message, timeStamp, intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        final int icon = R.drawable.new_icon;

        Random random = new Random();
        int randno = random.nextInt(9999 - 1000) + 1000;

        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent resultPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            resultPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    randno,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE //ONE SHOT NOT WORKING
            );
        }
        else
        {
            resultPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    randno,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT //ONE SHOT NOT WORKING
            );
        }

        final NotificationCompat.Builder mBuilder1 = new NotificationCompat.Builder(
                getApplicationContext());

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder1, icon, title, message, timeStamp, resultPendingIntent, null);
                } else {
                    showSmallNotification(mBuilder1, icon, title, message, timeStamp, resultPendingIntent, null);
                }
            }
        } else {
            showSmallNotification(mBuilder1, icon, title, message, timeStamp, resultPendingIntent, null);
            //playNotificationSound();
        }
    }


    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.new_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showSmallNotification(NotificationCompat.Builder mBuilder1, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        Notification.Builder mBuilder;
        Notification notification = null;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new Notification.Builder(getApplicationContext(), "");
        } else {
            mBuilder = new Notification.Builder(getApplicationContext());
        }

        if (title.equalsIgnoreCase("Notification")) {


            notification = mBuilder.setSmallIcon(R.drawable.new_icon).setTicker(title)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    // .setContentTitle(array[1])
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                   // .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    // .setShowWhen(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.new_icon)

                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.new_icon))
                    .setContentText(message)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            // notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel("", getApplicationContext().getString(R.string.app_name), importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert notificationManager != null;
                mBuilder.setChannelId(String.valueOf(Constant.CHANNELID));
                notificationManager.createNotificationChannel(notificationChannel);

            }
        } else {
            notification = mBuilder.setSmallIcon(R.drawable.new_icon).setTicker(title)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    // .setContentTitle(array[1])
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    //.setShowWhen(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.new_icon)

                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.new_icon))
                    .setContentText(message)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            //  notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(Constant.CHANNELID + "", getApplicationContext().getString(R.string.app_name), importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.GREEN);

                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert notificationManager != null;
                mBuilder.setChannelId(Constant.CHANNELID + "");
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }

        Random random = new Random();
        FCM.NOTIFICATION_ID = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(FCM.NOTIFICATION_ID, notification);
        newNotiString = "" + FCM.NOTIFICATION_ID + "_" + notificationId;

    }


}
