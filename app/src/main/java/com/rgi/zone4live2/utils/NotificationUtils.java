package com.rgi.zone4live2.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.rgi.zone4live2.R;
import com.rgi.zone4live2.fcm.FCM;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;




public class NotificationUtils {

    private static final String NOTIFICATION_CHANNEL_ID = "100";
    private static String TAG = "NotificationUtils";

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }




    private void showSmallNotification1(NotificationCompat.Builder mBuilder, int icon, String title, CharSequence message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        Notification notification = null;
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //  String login_type = FarmTrakApp.cache.readString(mContext,Constant.CustypeCode,"");
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
            inboxStyle.bigText(message);
            inboxStyle.setBigContentTitle(title);

            notification = mBuilder.setSmallIcon(R.drawable.new_icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.new_icon)
                    // .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.daawat_notification_logo))
                    .setContentText(message)
                    .build();

            NotificationChannel notificationChannel = new NotificationChannel(mContext.getString(R.string.app_name), mContext.getString(R.string.app_name), importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mBuilder.setChannelId(mContext.getString(R.string.app_name));
            notificationManager.createNotificationChannel(notificationChannel);
            if (title.equalsIgnoreCase("message"))
                notificationManager.notify(0, notification);
            else if (title.equalsIgnoreCase("post"))
                notificationManager.notify(FCM.NOTIFICATION_ID, notification);
            //    notificationManager.notify(0, notification);
        } else {

            NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
            inboxStyle.bigText(message);
            inboxStyle.setBigContentTitle(title);
            notification = mBuilder.setSmallIcon(R.drawable.new_icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.new_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.new_icon))
                    .setContentText(message)
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            if (title.equalsIgnoreCase("message"))
                notificationManager.notify(0, notification);
            else if (title.equalsIgnoreCase("post"))
                notificationManager.notify(FCM.NOTIFICATION_ID, notification);
            //      notificationManager.notify(0, notification);

        }


    }


    //TRY USING THIS
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showSmallNotification(NotificationCompat.Builder mBuilder1, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        Notification.Builder mBuilder;

        Notification notification = null;
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
        inboxStyle.bigText(message);
        inboxStyle.setBigContentTitle(title);


        int channelId = Constant.CHANNELID;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new Notification.Builder(mContext, channelId + "");
        } else {
            mBuilder = new Notification.Builder(mContext);
        }


        notification = mBuilder.setSmallIcon(R.drawable.new_icon).setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.new_icon)

                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.new_icon))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId + "", mContext.getString(R.string.app_name), importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);

            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mBuilder.setChannelId(channelId + "");
            notificationManager.createNotificationChannel(notificationChannel);

        }

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, notification);
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
                // .setContentIntent(resultPendingIntent)
                //  .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.new_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, notification);
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

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            Log.e("App in mem", "mem");
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            Log.e("App is not in mem", "not mem");
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
