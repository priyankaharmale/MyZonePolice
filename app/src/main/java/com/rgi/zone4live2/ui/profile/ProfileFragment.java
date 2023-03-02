package com.rgi.zone4live2.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.activites.MainActivity;
import com.rgi.zone4live2.apps.Zone4LiveApp;
import com.rgi.zone4live2.databinding.FragmentProfileBinding;
import com.rgi.zone4live2.model.LoginModel;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProfileFragment extends Fragment {
    public static final int RequestPermissionCode = 11000;
    private final int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    FragmentProfileBinding binding;
    File imageFile;
    ProgressDialog progressDialog;
    LoginModel user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(getActivity(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        }
        //
    }

    private void init() {
        ((MainActivity) getActivity()).toolbar.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_line, null);
                ((MainActivity) getActivity()).toolbar.setNavigationIcon(d);
            }
        });
        try {
            JSONObject profileJson = new JSONObject(Zone4LiveApp.cache.readString(getActivity(), Constant.PROFILEJSON, ""));
            Log.e("JSON", "init: json " + profileJson);
            user = new Gson().fromJson(profileJson.toString(), LoginModel.class);
            binding.setProfile(user.getUserData());
            Glide.with(getActivity()).load(Zone4LiveApp.cache.readString(getActivity(), Constant.PROFILEFROMSERVER, ""))
                    .placeholder(R.drawable.ic_profile)
                    .error(R.mipmap.ic_launcher_round).into(binding.profileImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog = new ProgressDialog(getActivity());
        binding.etDesignation.setKeyListener(null);
        binding.etChowcky.setKeyListener(null);
        binding.etName.setKeyListener(null);
        binding.etMobile.setKeyListener(null);
        binding.etSubZone.setKeyListener(null);
        binding.etChowcky.setKeyListener(null);
        binding.etstate.setKeyListener(null);

        binding.tvUsername.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!hasPermissions(getActivity(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                } else {
                    selectImage();
                }
            } else {
                selectImage();
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            callPickFile();
        });
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                int s = grantResults.length;
                int aray[] = grantResults;
                if (hasAllPermissionsGranted(grantResults)) {
                    selectImage();
                } else {
                }
                return;

            }

            case RequestPermissionCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IOUtils.showSnackBar(getActivity(), "Permission Granted, Now your application can access CAMERA.");
                } else {

                }
                break;
        }
    }


    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // Permission is not granted
                if (items[item].equals("Take Photo")) {
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {

        ImagePicker.Companion.with(this)
                .galleryOnly()
                // .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();


    }

    private void cameraIntent() {

        ImagePicker.Companion.with(this)
                .cameraOnly()
                // .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "x: reqcode " + requestCode + " resultCode  " + resultCode + " data " + data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bm = null;
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                Glide.with(this).load(bm).into(binding.profileImage);
                persistImage(bm, "" + System.currentTimeMillis());

                Log.e("TAG", "onActivityResult: bitmap " + bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void persistImage(Bitmap bitmap, String name) {
        File filesDir = getActivity().getFilesDir();
        imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("log", "Error writing bitmap", e);
        }
    }

    private void callPickFile() {
        if (IOUtils.isInternetPresent(getActivity())) {
            IOUtils.hideKeyBoard(getActivity());
            //show progress dialog
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Please Wait...");
            progressDialog.show();
            IOUtils.hideKeyBoard(getActivity());

            AndroidNetworking.upload(Constant.BASE_URL + "updateUser")
                    .addMultipartParameter("name", user.getUserData().getName())
                    .addMultipartParameter("designation", user.getUserData().getDesignation())
                    .addMultipartParameter("mobile_number", user.getUserData().getMobileNumber())
                    .addMultipartParameter("subZone", user.getUserData().getSubZone())
                    .addMultipartParameter("chowky", user.getUserData().getChowky())
                    .addMultipartParameter("id", user.getUserData().getId())
                    .addMultipartParameter("shift",  Zone4LiveApp.cache.readString(getActivity(), Constant.SHIFT, user.getUserData().getShift()))
                    .addMultipartFile("userImage", imageFile)
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            // do anything with progress
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            progressDialog.dismiss();
                            progressDialog.dismiss();
                            LoginModel user = new Gson().fromJson(response.toString(), LoginModel.class);
                            if (user.getResponseStatus().equalsIgnoreCase("1")) {
                                IOUtils.showSnackBar(getActivity(), user.getResponseMessage());
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.USERID, "" + user.getUserData().getId());
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.USER_NAME, "" + user.getUserData().getName());
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.MOBILE, "" + binding.etMobile.getText().toString().trim());
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.DESIGNATION, "" + user.getUserData().getDesignation());
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.CHOWKY, "" + user.getUserData().getChowky());
                                Zone4LiveApp.cache.writeBoolean(getActivity(), Constant.ISLOGIN, true);
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.PROFILEFROMSERVER, user.getUserData().getUserImage());
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.STATE, user.getUserData().getState());
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.SUBZONE, user.getUserData().getSubZone());
                                Zone4LiveApp.cache.writeString(getActivity(), Constant.PROFILEJSON, response.toString());
                                Glide.with(getActivity()).load(Zone4LiveApp.cache.readString(getActivity(), Constant.PROFILEFROMSERVER, ""))
                                        .placeholder(R.drawable.ic_profile)
                                        .error(R.mipmap.ic_launcher_round).into(binding.profileImage);
                                Glide.with(getActivity()).load(Zone4LiveApp.cache.readString(getActivity(), Constant.PROFILEFROMSERVER, ""))
                                        .placeholder(R.drawable.ic_profile)
                                        .error(R.mipmap.ic_launcher_round).into(((MainActivity) getActivity()).imageView);

                            } else {
                                IOUtils.errorShowSnackBar(getActivity(), user.getResponseMessage());
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("TAG", "onError: " + error);
                            Log.e("TAG", "onError: " + error.getErrorBody());
                            Log.e("TAG", "onError: " + error.getErrorDetail());
                            Log.e("TAG", "onError: " + error.getErrorBody());

                            progressDialog.dismiss();
                            // handle error
                        }
                    });

        } else
            IOUtils.showSnackBar(getActivity(), "Please check internet connectivity");
    }
}