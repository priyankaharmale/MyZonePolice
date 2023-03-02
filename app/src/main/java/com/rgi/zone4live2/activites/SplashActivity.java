package com.rgi.zone4live2.activites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.apps.Zone4LiveApp;
import com.rgi.zone4live2.databinding.ActivitySplashBinding;
import com.rgi.zone4live2.model.SpotDataModel;
import com.rgi.zone4live2.model.VersionModel;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.IOUtils;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    public View rootView;
    public static Context BASE_CONTEXT;
    LocationManager manager;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        BASE_CONTEXT = SplashActivity.this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("TAG", "onCreate: token " + token);
        Zone4LiveApp.cache.writeString(SplashActivity.this, Constant.TOKEN, token);
        Log.e("TAG", "onCreate: save fcm " + Zone4LiveApp.cache.readString(SplashActivity.this, Constant.TOKEN, ""));

    }


    private void startAction() {
        if (IOUtils.isInternetPresent(BASE_CONTEXT)) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean flag = Zone4LiveApp.cache.readBoolean(SplashActivity.this, Constant.ISLOGIN, false);
                    Log.e("test", "run:boolean value  " + flag);
                    Intent intent;
                    if (flag)
                        intent = new Intent(BASE_CONTEXT, MainActivity.class);
                    else
                        intent = new Intent(BASE_CONTEXT, ActivityLogin.class);

                    // Closing all the Activities
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Add new Flag to start new Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    //  overridePendingTransition(R.anim.enter_right, R.anim.exit_left);
                }
            }, Constant.SPLASH_TIME_OUT);
        } else {
            IOUtils.mySnackBarInternet(BASE_CONTEXT, rootView);
        }

    }

    private void animation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.shake);
        a.reset();
        binding.tvText.clearAnimation();
        binding.tvText.startAnimation(a);
    }

    @Override
    protected void onResume() {
        // animation();
        try {


            if (ContextCompat.checkSelfPermission(SplashActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(SplashActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    ActivityCompat.requestPermissions(SplashActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }else{
                checkVersion();
            }



        } catch (Exception e) {
            e.printStackTrace();
            nextSlot();
        }

        super.onResume();
    }

    private void checkVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            Log.e("TAG", "onResume: version " + version);
            if (IOUtils.isInternetPresent(BASE_CONTEXT)) {
                callVersionAPI(version);
            } else {
                IOUtils.mySnackBarInternet(BASE_CONTEXT, rootView);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            nextSlot();
        }
    }

    private void callVersionAPI(String version) {
        progressDialog = new ProgressDialog(SplashActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(150, TimeUnit.SECONDS)
                .connectTimeout(150, TimeUnit.SECONDS).build();
        IOUtils.hideKeyBoard(SplashActivity.this);
        AndroidNetworking.post(Constant.BASE_URL + "getAppVersion")
                .setPriority(Priority.HIGH)
                .setOkHttpClient(client)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        progressDialog.dismiss();
                        VersionModel versionModel = new Gson().fromJson(response.toString(), VersionModel.class);
                        if (versionModel.getStatus()) {
                            if (Float.parseFloat(version) < (Float.parseFloat(versionModel.getVersionInfo().getVersion()))) {
                                IOUtils.logout(SplashActivity.this, "Splash");
                                new AlertDialog.Builder(SplashActivity.this).setTitle("Update")
                                        .setMessage("Please update your application")
                                        .setPositiveButton(getString(R.string.str_yes),
                                                (dialog, which) -> {
                                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                }).create().show();
                            } else
                                nextSlot();
                        } else {
                            IOUtils.errorShowSnackBar(SplashActivity.this, versionModel.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("TAG", "onError:test " + error);
                        Log.e("TAG", "onError: " + error.getErrorBody());
                        Log.e("TAG", "onError: " + error.getErrorDetail());
                        Log.e("TAG", "onError: " + error.getErrorBody());

                        progressDialog.dismiss();
                    }
                });
    }

    private void nextSlot() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else
            startAction();
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(SplashActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        checkVersion();

                    }
                } else {
                   // Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}