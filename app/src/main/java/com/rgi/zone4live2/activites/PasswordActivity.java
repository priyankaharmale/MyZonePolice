package com.rgi.zone4live2.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

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
import com.rgi.zone4live2.databinding.ActivityPasswordBinding;
import com.rgi.zone4live2.model.LoginModel;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.IOUtils;

import org.json.JSONObject;

public class PasswordActivity extends AppCompatActivity {
    public static final String TAG = PasswordActivity.class.getName();
    ActivityPasswordBinding binding;
    public View rootView;
    public Activity BASE_CONTEXT;
    ProgressDialog progressDialog;
    LoginModel user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BASE_CONTEXT = PasswordActivity.this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        progressDialog = new ProgressDialog(this);

        binding.btnLogin.setOnClickListener(v -> {
            if(binding.etPass.getText().toString().isEmpty()){
                IOUtils.errorShowSnackBar(BASE_CONTEXT, "Please enter Password");
                binding.etPass.setFocusable(true);
                binding.etPass.setError("Enter valid password");
                return;
            }
            if(binding.etPass.getText().toString().length()<3){
                IOUtils.errorShowSnackBar(BASE_CONTEXT, "Password should be greater than 3 digit");
                binding.etPass.setFocusable(true);
                binding.etPass.setError("Enter valid password");
                return;
            }
            if(binding.etCnfPass.getText().toString().isEmpty()){
                IOUtils.errorShowSnackBar(BASE_CONTEXT, "Please enter Password");
                binding.etCnfPass.setFocusable(true);
                binding.etCnfPass.setError("Enter valid password");
                return;
            }
            if(binding.etCnfPass.getText().toString().length()<3){
                IOUtils.errorShowSnackBar(BASE_CONTEXT, "Password should be greater than 3 digit");
                binding.etCnfPass.setFocusable(true);
                binding.etCnfPass.setError("Enter valid password");
                return;
            }
            if(!binding.etPass.getText().toString().equals(binding.etCnfPass.getText().toString())){
                IOUtils.errorShowSnackBar(BASE_CONTEXT, "Password and Confirm password should be same");
                return;
            }

            callAPI(binding.etCnfPass.getText().toString());

        });
    }

    private void callAPI(String toString) {
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        if (Zone4LiveApp.cache.readString(PasswordActivity.this, Constant.TOKEN, "").trim().isEmpty()) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Zone4LiveApp.cache.writeString(PasswordActivity.this, Constant.TOKEN, token);
        }
        IOUtils.hideKeyBoard(this);

        AndroidNetworking.post(Constant.BASE_URL + "savePassword")
                .addBodyParameter("mobile", Zone4LiveApp.cache.readString(getApplicationContext(), Constant.MOBILE, ""))
                .addBodyParameter("password", binding.etPass.getText().toString())
                .addBodyParameter("fcm_id", Zone4LiveApp.cache.readString(getApplicationContext(), Constant.TOKEN, ""))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e(TAG, "onResponse: res pass " + response);
                        progressDialog.dismiss();
                        user = new LoginModel();
                        user = new Gson().fromJson(response.toString(), LoginModel.class);
                        if (user.getResponseStatus().equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(BASE_CONTEXT, user.getResponseMessage());
                              Zone4LiveApp.cache.writeBoolean(PasswordActivity.this, Constant.ISLOGIN, true);
                            Zone4LiveApp.cache.writeString(PasswordActivity.this, Constant.IS_PASSWORD, "TRUE");
                            startActivity(new Intent(PasswordActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            overridePendingTransition(R.anim.enter_right, R.anim.exit_left);
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