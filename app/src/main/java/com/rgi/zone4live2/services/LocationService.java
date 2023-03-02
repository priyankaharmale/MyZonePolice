package com.rgi.zone4live2.services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.activites.MainActivity;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.IOUtils;

import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {

    public double latitude =0.0,longtude = 0.0;
    NotificationCompat.Builder builder;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                 latitude = locationResult.getLastLocation().getLatitude();
                 longtude = locationResult.getLastLocation().getLongitude();
              //  Log.e("Location", "onLocationResult:locations  "+latitude+" -- "+longtude );
                Intent broadcastIntent = new Intent("LocationReceiever");
                broadcastIntent.putExtra("lat", latitude);
                broadcastIntent.putExtra("logn", longtude);
                builder.setContentText(IOUtils.getAddressNew(getApplicationContext(),latitude,longtude));
                builder.build();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
              //  Toast.makeText(LocationService.this, "" + IOUtils.getAddressNew(getApplicationContext(), latitude, longtude), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet implemented.");
    }

    private void startLocationService() {
        String ChannelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        builder = new NotificationCompat.Builder(getApplicationContext(),
                ChannelId);
        builder.setSmallIcon(R.drawable.new_icon);
        builder.setContentTitle("Current Location");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(ChannelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(ChannelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel used by location service.");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constant.LOCATION_SERVICE_ID,builder.build());
    }

    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            String action = intent.getAction();
            if(action!= null){
                if(action.equalsIgnoreCase(Constant.ACTION_START_LOCATION_SERVICE))
                    startLocationService();
                else if(action.equalsIgnoreCase(Constant.ACTION_STOP_LOCATION_SERVICE))
                    stopLocationService();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}