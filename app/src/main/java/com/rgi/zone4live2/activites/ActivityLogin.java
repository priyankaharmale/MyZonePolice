package com.rgi.zone4live2.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.apps.Zone4LiveApp;
import com.rgi.zone4live2.databinding.ActivityLoginBinding;
import com.rgi.zone4live2.model.LoginModel;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.IOUtils;

import org.json.JSONObject;

public class ActivityLogin extends AppCompatActivity {
    public static final String TAG = ActivityLogin.class.getName();
    ActivityLoginBinding binding;
    public View rootView;
    public Activity BASE_CONTEXT;
    ProgressDialog progressDialog;
    LoginModel user = new LoginModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BASE_CONTEXT = ActivityLogin.this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        progressDialog = new ProgressDialog(this);

        binding.btnLogin.setOnClickListener(v -> {
            if (binding.btnLogin.getText().toString().equalsIgnoreCase("Submit"))
                validation(true);
            else
                validation(false);
        });

    }

    private void validation(boolean isOtp) {
        if (binding.etMobile.getText().toString().isEmpty()) {
            IOUtils.errorShowSnackBar(BASE_CONTEXT, "Please enter valid mobile number");
            binding.etMobile.setFocusable(true);
            binding.etMobile.setError("Invalid Number");
            return;
        }
        if (binding.etMobile.getText().toString().length() > 10) {
            IOUtils.errorShowSnackBar(BASE_CONTEXT, "Please enter valid mobile number");
            binding.etMobile.setFocusable(true);
            binding.etMobile.setError("Invalid Number");
            return;
        }
        if (binding.edAccessCode.getText().toString().isEmpty()) {
            IOUtils.errorShowSnackBar(BASE_CONTEXT, "Please enter valid access code");
            binding.edAccessCode.setFocusable(true);
            binding.edAccessCode.setError("Invalid Access Code");
            return;
        }
        if (isOtp && binding.etOtp.getText().toString().length() < 4) {
            IOUtils.errorShowSnackBar(BASE_CONTEXT, "Please enter valid otp");
            binding.etOtp.setFocusable(true);
            binding.etOtp.setError("Invalid Access Otp");
            return;
        }
        if (isOtp)
            checkOTP(binding.etOtp.getText().toString());
        else
            loginAPI();
    }

    private void checkOTP(String otp) {
        if (otp.equalsIgnoreCase(user.getUserData().getOtp())) {
            IOUtils.hideKeyBoard(this);
            IOUtils.showSnackBar(BASE_CONTEXT, user.getResponseMessage());
            if(user.getUserData().getState().equalsIgnoreCase("DO")){
               /* if(user.getUserData().getIs_password().equalsIgnoreCase("false")){
                    startActivity(new Intent(ActivityLogin.this, PasswordActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    overridePendingTransition(R.anim.enter_right, R.anim.exit_left);
                }else{*/
                    Zone4LiveApp.cache.writeBoolean(ActivityLogin.this, Constant.ISLOGIN, true);
                    startActivity(new Intent(ActivityLogin.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    overridePendingTransition(R.anim.enter_right, R.anim.exit_left);
               // }
            }else{
                Zone4LiveApp.cache.writeBoolean(ActivityLogin.this, Constant.ISLOGIN, true);
                startActivity(new Intent(ActivityLogin.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                overridePendingTransition(R.anim.enter_right, R.anim.exit_left);
            }
        }else{
            IOUtils.errorShowSnackBar(BASE_CONTEXT, "Please enter valid otp");
        }
    }

    private void loginAPI() {
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        if (Zone4LiveApp.cache.readString(ActivityLogin.this, Constant.TOKEN, "").trim().isEmpty()) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.TOKEN, token);
            Log.e("TAG", "onCreate: save fcm " + Zone4LiveApp.cache.readString(ActivityLogin.this, Constant.TOKEN, ""));
        }
        IOUtils.hideKeyBoard(this);
        AndroidNetworking.post(Constant.BASE_URL + "getUserLogin")
                .addBodyParameter("mobile", binding.etMobile.getText().toString())
                .addBodyParameter("accessCode", binding.edAccessCode.getText().toString())
                .addBodyParameter("fcm_id", Zone4LiveApp.cache.readString(getApplicationContext(), Constant.TOKEN, ""))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e(TAG, "onResponse: res " + response);
                        progressDialog.dismiss();
                        user = new LoginModel();
                         user = new Gson().fromJson(response.toString(), LoginModel.class);
                        if (user.getResponseStatus().equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(BASE_CONTEXT, user.getResponseMessage());
                            binding.tvOtp.setVisibility(View.VISIBLE);
                            binding.btnLogin.setText("Submit");
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.USERID, "" + user.getUserData().getId());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.USER_NAME, "" + user.getUserData().getName());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.MOBILE, "" + binding.etMobile.getText().toString().trim());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.DESIGNATION, "" + user.getUserData().getDesignation());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.CHOWKY, "" + user.getUserData().getChowky());
                           //
                            // Zone4LiveApp.cache.writeBoolean(ActivityLogin.this, Constant.ISLOGIN, true);
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.PROFILEFROMSERVER, user.getUserData().getUserImage());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.STATE, user.getUserData().getState());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.SUBZONE, user.getUserData().getSubZone());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.PROFILEJSON, response.toString());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.SHIFT, user.getUserData().getShift());
                            Zone4LiveApp.cache.writeString(ActivityLogin.this, Constant.IS_PASSWORD, user.getUserData().getIs_password());
                          /*  startActivity(new Intent(ActivityLogin.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            overridePendingTransition(R.anim.enter_right, R.anim.exit_left);*/
                        } else {
                            IOUtils.errorShowSnackBar(BASE_CONTEXT, user.getResponseMessage());
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e(TAG, "onError: " + error);
                        Log.e(TAG, "onError: " + error.getErrorBody());
                        Log.e(TAG, "onError: " + error.getErrorDetail());
                        Log.e(TAG, "onError: " + error.getErrorBody());
                        progressDialog.dismiss();
                    }
                });

    }
}