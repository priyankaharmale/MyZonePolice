package com.rgi.zone4live2.ui.users;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.activites.MainActivity;
import com.rgi.zone4live2.adapter.MergeSpotAdapter;
import com.rgi.zone4live2.adapter.SpotAdapter;
import com.rgi.zone4live2.adapter.UserAdapter;
import com.rgi.zone4live2.adapter.UserSpotAdapter;
import com.rgi.zone4live2.apps.Zone4LiveApp;
import com.rgi.zone4live2.databinding.ChnageShiftBinding;
import com.rgi.zone4live2.databinding.DialogmapBinding;
import com.rgi.zone4live2.databinding.FragmentUserBinding;
import com.rgi.zone4live2.databinding.LayoutAssingUserSpotBinding;
import com.rgi.zone4live2.databinding.LayoutSpotUpdateItemBinding;
import com.rgi.zone4live2.databinding.LayoutUserAddItemBinding;
import com.rgi.zone4live2.model.LoginModel;
import com.rgi.zone4live2.model.PoliceStationData;
import com.rgi.zone4live2.model.SpotDataModel;
import com.rgi.zone4live2.model.UserModel;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class UserFragment extends Fragment implements OnMapReadyCallback, LocationListener
        , GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {
    public static final String TAG = UserFragment.class.getName();
    FragmentUserBinding binding;
    View view;
    ProgressDialog progressDialog;
    PoliceStationData policeStationData;
    UserModel userModel;
    ArrayAdapter policeStationAdapter, chowkyAdapter;
    ArrayAdapter policeProfAdapter, chowkyProfAdapter;
    ArrayAdapter policeEditAdapter, chowkyEditAdapter;
    boolean isUser = true;
    double newLat = 0.0, newLong = 0.0;

    public static final int RequestPermissionCode = 11000;
    private final int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    LayoutUserAddItemBinding useraAddDialog;
    LayoutSpotUpdateItemBinding updateItemBinding;
    LayoutAssingUserSpotBinding spotBinding;
    LayoutAssingUserSpotBinding spotMergeBinding;
    File imageFile = null;
    SpotDataModel spotDataModel = new SpotDataModel();
    DialogmapBinding mapBinding;
    GoogleMap dMap;
    String stringChowky="", stringStation="";

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        ((MainActivity) getActivity()).toolbar.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_line, null);
                ((MainActivity) getActivity()).toolbar.setNavigationIcon(d);
            }
        });
        if (!Zone4LiveApp.cache.readString(getActivity(), Constant.STATE, "").equalsIgnoreCase("user")) {
            // showDailog();
            binding.tvNo.setVisibility(View.GONE);
            binding.llMain.setVisibility(View.VISIBLE);
        } else {
            binding.tvNo.setVisibility(View.VISIBLE);
            binding.llMain.setVisibility(View.GONE);
        }
        binding.ivUser.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_primary), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.tvUser.setTextColor(getResources().getColor(R.color.color_primary));
        progressDialog = new ProgressDialog(getActivity());
        binding.recyclerSpot.setVisibility(View.GONE);
        binding.llUser.setOnClickListener(v -> {
            isUser = true;
            binding.ivUser.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_primary), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.tvUser.setTextColor(getResources().getColor(R.color.color_primary));
            binding.ivSpot.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.tvSpot.setTextColor(getResources().getColor(R.color.gray));
            binding.recyclerSpot.setVisibility(View.GONE);
            binding.tvUserList.setVisibility(View.VISIBLE);
            binding.tvSpotList.setVisibility(View.GONE);
            binding.recyclerUser.setVisibility(View.VISIBLE);
            binding.fab.setVisibility(View.VISIBLE);
            getPoliceStationList();
        });

        binding.llSpot.setOnClickListener(v -> {
            isUser = false;
            binding.ivUser.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gray), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.tvUser.setTextColor(getResources().getColor(R.color.gray));
            binding.ivSpot.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_primary), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.tvSpot.setTextColor(getResources().getColor(R.color.color_primary));
            binding.recyclerSpot.setVisibility(View.VISIBLE);
            binding.tvUserList.setVisibility(View.GONE);
            binding.tvSpotList.setVisibility(View.VISIBLE);
            binding.recyclerUser.setVisibility(View.GONE);
            binding.fab.setVisibility(View.GONE);
            binding.tvSpot.setVisibility(View.VISIBLE);
            getPoliceStationList();
        });
        getPoliceStationList();
        binding.fab.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!hasPermissions(getActivity(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                } else {
                    addUserDialog(null, false);
                }
            } else {
                addUserDialog(null, false);
            }
        });
    }

    private void showDailog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        if (userInputDialogEditText.getText().toString().isEmpty()) {
                            IOUtils.errorShowSnackBar(getActivity(), "Please enter valid password");
                            return;
                        }
                        if (userInputDialogEditText.getText().toString().length() < 3) {
                            IOUtils.errorShowSnackBar(getActivity(), "Please enter valid password");
                            return;
                        }
                        callPasswordCheckAPI(userInputDialogEditText.getText().toString(), dialogBox);
                    }
                });

                /*.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });*/

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void callPasswordCheckAPI(String toString, DialogInterface dialogBox) {
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        if (Zone4LiveApp.cache.readString(getActivity(), Constant.TOKEN, "").trim().isEmpty()) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Zone4LiveApp.cache.writeString(getActivity(), Constant.TOKEN, token);
        }
        IOUtils.hideKeyBoard(getActivity());

        AndroidNetworking.post(Constant.BASE_URL + "checkPassword")
                .addBodyParameter("mobile", Zone4LiveApp.cache.readString(getActivity(), Constant.MOBILE, ""))
                .addBodyParameter("password", toString)
                .addBodyParameter("fcm_id", Zone4LiveApp.cache.readString(getActivity(), Constant.TOKEN, ""))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e(TAG, "onResponse: res pass " + response);
                        progressDialog.dismiss();
                        LoginModel user = new LoginModel();
                        user = new Gson().fromJson(response.toString(), LoginModel.class);
                        if (user.getResponseStatus().equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(getActivity(), user.getResponseMessage());
                            dialogBox.dismiss();
                            binding.tvNo.setVisibility(View.GONE);
                            binding.llMain.setVisibility(View.VISIBLE);
                        } else {
                            IOUtils.errorShowSnackBar(getActivity(), user.getResponseMessage());
                            showDailog();
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


    private void updateSpotDialog(SpotDataModel.SpotDatum spotData) {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        updateItemBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_spot_update_item, null, false);
        alertDialog.setView(updateItemBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        updateItemBinding.setSpotDetails(spotData);
        Glide.with(this).load(spotData.getDefaultImage()).error(R.drawable.ic_profile).into(updateItemBinding.spotImage);
        dialog.setCancelable(true);
        updateItemBinding.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
        updateItemBinding.btnSave.setOnClickListener(v -> {
            if (updateItemBinding.etSpotName.getText().toString().isEmpty()) {
                updateItemBinding.etSpotName.setFocusable(true);
                updateItemBinding.etSpotName.setError("Please Enter Spot name.");
                return;
            }
            if (updateItemBinding.etAddress.getText().toString().isEmpty()) {
                updateItemBinding.etAddress.setFocusable(true);
                updateItemBinding.etAddress.setError("Please Enter address.");
                return;
            }
            if (dMap != null) {
                callUpdateSpot( dMap.getCameraPosition().target.latitude, dMap.getCameraPosition().target.longitude,
                        IOUtils.getAddress(getActivity(),dMap.getCameraPosition().target.latitude, dMap.getCameraPosition().target.longitude)
                        ,spotData,dialog,updateItemBinding.spEdtStation.getSelectedItem().toString(),updateItemBinding.spEdtChowky.getSelectedItem().toString()
                ,updateItemBinding.spEdtCategory.getSelectedItem().toString());
            } else
                callUpdateSpot(spotData.getLatitude(),spotData.getLongitude(),spotData.getAddress(),spotData, dialog,
                        updateItemBinding.spEdtStation.getSelectedItem().toString(),updateItemBinding.spEdtChowky.getSelectedItem().toString()
                        ,updateItemBinding.spEdtCategory.getSelectedItem().toString());
        });
        updateItemBinding.ivEdit.setOnClickListener(v -> {
            dialogMap(spotData.getLatitude(), spotData.getLongitude());
        });

//policeStationData.getStationData()
        ArrayList<String> policeStation = new ArrayList<>();
        for (PoliceStationData.StationDatum datum : policeStationData.getStationData()) {
            policeStation.add(datum.getSubZone());
        }
        policeEditAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, policeStation);
        policeEditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        updateItemBinding.spEdtStation.setAdapter(policeEditAdapter);
        updateItemBinding.spEdtStation.setTitle("Select Police Station.");


        ArrayList<String> catList = new ArrayList<>();
        catList.add("general");
        catList.add("priority");
        catList.add("ganapati");
        ArrayAdapter catEditAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, catList);
        catEditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        updateItemBinding.spEdtCategory.setAdapter(catEditAdapter);
        updateItemBinding.spEdtCategory.setTitle("Select Category.");

        if (spotData.getCategory() != null) {
            int spinnerPosition = catEditAdapter.getPosition(spotData.getCategory());
            updateItemBinding.spEdtCategory.setSelection(spinnerPosition);
        }

        if (spotData.getAssigned_PS() != null) {
            int spinnerPosition = policeEditAdapter.getPosition(spotData.getAssigned_PS());
            updateItemBinding.spEdtStation.setSelection(spinnerPosition);
        }



        updateItemBinding.spEdtStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemSelected: station " + policeStation.get(position));
                for (PoliceStationData.StationDatum datum : policeStationData.getStationData()) {
                    if (datum.getSubZone().equalsIgnoreCase(policeStation.get(position))) {
                        ArrayList<String> chowkyList = new ArrayList<>();
                        for (PoliceStationData.ChowkyDatum datum1 : datum.getChowkyData()) {
                            chowkyList.add(datum1.getChowky());
                        }
                        chowkyEditAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, chowkyList);
                        chowkyEditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        updateItemBinding.spEdtChowky.setAdapter(chowkyEditAdapter);
                        updateItemBinding.spEdtChowky.setTitle("Select Police Chowky.");

                        if (spotData.getAssigned_PC() != null) {
                            int spinnerPosition = chowkyEditAdapter.getPosition(spotData.getAssigned_PC());
                            updateItemBinding.spEdtChowky.setSelection(spinnerPosition);
                        }

                        updateItemBinding.spEdtChowky.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.e(TAG, "onItemSelected: dialog selected update " + updateItemBinding.spEdtStation.getSelectedItem()
                                        + " -- " + updateItemBinding.spEdtChowky.getSelectedItem());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.show();


    }

    private void dialogMap(double latitude, double longitude) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        mapBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialogmap, null, false);
        alertDialog.setView(mapBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        GoogleMap googleMap;
        newLat = latitude;
        newLong = longitude;
        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(getActivity());

        mMapView = (MapView) dialog.findViewById(R.id.mapView);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(this);
        mapBinding.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
        mapBinding.btnSave.setOnClickListener(v -> {
            updateItemBinding.etAddress.setText(IOUtils.getAddress(getActivity(), dMap.getCameraPosition().target.latitude, dMap.getCameraPosition().target.longitude));
            updateItemBinding.etLatLong.setText(dMap.getCameraPosition().target.latitude + "," + dMap.getCameraPosition().target.longitude);
            dialog.dismiss();
        });
    }


    private void assignSpotToUser(SpotDataModel userSpotDataModel, LoginModel.UserData userData) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()/*, R.style.DialogTheme*/);
        spotBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_assing_user_spot, null, false);
        alertDialog.setView(spotBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        spotBinding.recyclerSpots.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        spotBinding.recyclerSpots.setAdapter(new UserSpotAdapter(getActivity(), userSpotDataModel.getSpotData()));
        spotBinding.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
        spotBinding.cbAll.setOnClickListener(v -> {
            if (spotBinding.cbAll.isChecked()) {
                for (SpotDataModel.SpotDatum spotDatum : userSpotDataModel.getSpotData()) {
                    spotDatum.setAssigned("assigned");
                }
            } else {
                for (SpotDataModel.SpotDatum spotDatum : userSpotDataModel.getSpotData()) {
                    spotDatum.setAssigned("unassigned");
                }
            }
            spotBinding.recyclerSpots.getAdapter().notifyDataSetChanged();
        });
        // userData.get(position).setAssigned("assigned");
        spotBinding.btnSave.setOnClickListener(v -> {
            userSpotDataModel.setUser_id(userData.getId());
            Gson gson = new Gson();
            Log.e(TAG, "assignSpotToUser: befroe JSON " + gson.toJson(userSpotDataModel));
            try {
                JSONObject jsonObject = new JSONObject(gson.toJson(userSpotDataModel));
                Log.e(TAG, "assignSpotToUser: afterJson " + jsonObject);
                assignSoptToUserAPI(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                IOUtils.errorShowSnackBar(getActivity(), "Something wents to wrong!!!");
            }
        });
        dialog.show();
    }


    private void addUserDialog(LoginModel.UserData userData, boolean isEditable) {
        imageFile = null;
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        useraAddDialog = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_user_add_item, null, false);
        alertDialog.setView(useraAddDialog.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(true);
        ArrayList<String> shifts = new ArrayList<>();
        shifts.add("DAY");
        shifts.add("NIGHT");

        ArrayList<String> policeStation = new ArrayList<>();
        for (PoliceStationData.StationDatum datum : policeStationData.getStationData()) {
            policeStation.add(datum.getSubZone());
        }
        policeProfAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, policeStation);
        policeProfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        useraAddDialog.spStation.setAdapter(policeProfAdapter);
        useraAddDialog.spStation.setTitle("Select Police Station.");
        if (isEditable) {
            if (userData.getSubZone() != null) {
                int spinnerPosition = policeProfAdapter.getPosition(userData.getSubZone());
                useraAddDialog.spStation.setSelection(spinnerPosition);
            }
        } else {
            if (binding.spPoliceStation.getSelectedItem().toString() != null) {
                int spinnerPosition = policeProfAdapter.getPosition(binding.spPoliceStation.getSelectedItem().toString());
                useraAddDialog.spStation.setSelection(spinnerPosition);
            }
        }


        useraAddDialog.spStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemSelected: station " + policeStation.get(position));
                for (PoliceStationData.StationDatum datum : policeStationData.getStationData()) {
                    if (datum.getSubZone().equalsIgnoreCase(policeStation.get(position))) {
                        ArrayList<String> chowkyList = new ArrayList<>();
                        for (PoliceStationData.ChowkyDatum datum1 : datum.getChowkyData()) {
                            chowkyList.add(datum1.getChowky());
                        }
                        chowkyProfAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, chowkyList);
                        chowkyProfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        useraAddDialog.spChowky.setAdapter(chowkyProfAdapter);
                        useraAddDialog.spChowky.setTitle("Select Police Chowky.");

                        if (isEditable) {
                            if (userData.getChowky() != null) {
                                int spinnerPosition = chowkyProfAdapter.getPosition(userData.getChowky());
                                useraAddDialog.spChowky.setSelection(spinnerPosition);
                            }
                        } else {
                            if (binding.spPoliceChowky.getSelectedItem().toString() != null) {
                                int spinnerPosition = chowkyProfAdapter.getPosition(binding.spPoliceChowky.getSelectedItem().toString());
                                useraAddDialog.spChowky.setSelection(spinnerPosition);
                            }
                        }
                        useraAddDialog.spChowky.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.e(TAG, "onItemSelected: dialog selected " + useraAddDialog.spStation.getSelectedItem()
                                        + " -- " + useraAddDialog.spChowky.getSelectedItem());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        useraAddDialog.tvUsername.setOnClickListener(v -> {
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

        ArrayAdapter shiftAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, shifts);
        useraAddDialog.spShift.setAdapter(shiftAdapter);
        dialog.show();

        if (isEditable) {
            useraAddDialog.etName.setText(userData.getName());
            useraAddDialog.etDesignation.setText(userData.getDesignation());
            useraAddDialog.etMobile.setText(userData.getMobileNumber());
            useraAddDialog.inputMobile.setVisibility(View.GONE);
            if (userData.getShift().equalsIgnoreCase("DAY")) {
                useraAddDialog.spShift.setSelection(0);
            }
            if (userData.getShift().equalsIgnoreCase("NIGHT")) {
                useraAddDialog.spShift.setSelection(1);
            }

            Glide.with(this).load(userData.getUserImage()).error(R.drawable.ic_profile).into(useraAddDialog.profileImage);
        }
        else
        {
            useraAddDialog.inputMobile.setVisibility(View.VISIBLE);
        }

        useraAddDialog.btnSave.setOnClickListener(v -> {

            if (useraAddDialog.etName.getText().toString().isEmpty()) {
                useraAddDialog.etName.setFocusable(true);
                useraAddDialog.etName.setError("Please Enter Name.");
                return;
            }

            if (useraAddDialog.etDesignation.getText().toString().isEmpty()) {
                useraAddDialog.etDesignation.setFocusable(true);
                useraAddDialog.etDesignation.setError("Please Enter Designation.");
                return;
            }

            if (useraAddDialog.etMobile.getText().toString().isEmpty()) {
                useraAddDialog.etMobile.setFocusable(true);
                useraAddDialog.etMobile.setError("Please Enter Mobile");
                return;
            }
            if (useraAddDialog.etMobile.getText().toString().length() < 10) {
                useraAddDialog.etMobile.setFocusable(true);
                useraAddDialog.etMobile.setError("Please Enter Mobile");
                return;
            }

            callAddUserAPI(dialog, isEditable, userData);

        });
        useraAddDialog.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);
        return binding.getRoot();
    }


    private void spineerStations() {
        ArrayList<String> policeStation = new ArrayList<>();
        for (PoliceStationData.StationDatum datum : policeStationData.getStationData()) {
            policeStation.add(datum.getSubZone());
        }
        policeStationAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, policeStation);
        policeStationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spPoliceStation.setAdapter(policeStationAdapter);
        binding.spPoliceStation.setTitle("Select Police Station.");
        String strStation = Zone4LiveApp.cache.readString(getActivity(), Constant.SUBZONE, "");
        if (!stringStation.isEmpty()) {
            int spinnerPosition = policeStationAdapter.getPosition(stringStation);
            binding.spPoliceStation.setSelection(spinnerPosition);
        } else if (strStation != null) {
            int spinnerPosition = policeStationAdapter.getPosition(strStation);
            binding.spPoliceStation.setSelection(spinnerPosition);
        }


        binding.spPoliceStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerChowky(policeStation.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void spinnerChowky(String station) {
        ArrayList<String> chowkyList = new ArrayList<>();
        for (PoliceStationData.StationDatum datum : policeStationData.getStationData()) {
            if (datum.getSubZone().equalsIgnoreCase(station)) {
                for (PoliceStationData.ChowkyDatum datum1 : datum.getChowkyData()) {
                    chowkyList.add(datum1.getChowky());
                }
                chowkyAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, chowkyList);
                chowkyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spPoliceChowky.setAdapter(chowkyAdapter);
                binding.spPoliceChowky.setTitle("Select Police Chowky.");
                String strChowky = Zone4LiveApp.cache.readString(getActivity(), Constant.CHOWKY, "");
                if (!stringChowky.isEmpty()) {
                    int spinnerPosition = chowkyAdapter.getPosition(stringChowky);
                    binding.spPoliceChowky.setSelection(spinnerPosition);
                } else if (strChowky != null) {
                    int spinnerPosition = chowkyAdapter.getPosition(strChowky);
                    binding.spPoliceChowky.setSelection(spinnerPosition);
                }
                binding.spPoliceChowky.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.e(TAG, "onItemSelected: selected " + binding.spPoliceStation.getSelectedItem()
                                + " -- " + binding.spPoliceChowky.getSelectedItem());
                        stringChowky = ""+ binding.spPoliceChowky.getSelectedItem();
                        stringStation = ""+binding.spPoliceStation.getSelectedItem();
                        if (isUser)
                            getUserList(station, datum.getChowkyData().get(position).getChowky());
                        else
                            getSpotList(station, datum.getChowkyData().get(position).getChowky());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }
    }


    private void callProgress() {
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        IOUtils.hideKeyBoard(getActivity());
    }

    private void handleError(ANError error) {
        Log.e(TAG, "onError: " + error);
        Log.e(TAG, "onError: " + error.getErrorBody());
        Log.e(TAG, "onError: " + error.getErrorDetail());
        Log.e(TAG, "onError: " + error.getErrorBody());
    }

    private void callUserRecyclerView(UserModel userModel) {
        binding.recyclerUser.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        UserAdapter userAdapter =  new UserAdapter(getActivity(), userModel.getUserData(), new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClickShift(LoginModel.UserData userData, int position) {
                changeShift(userData);
            }

            @Override
            public void onItemClickSpot(LoginModel.UserData userData, int position) {
                callSpotListAPI(userData);
            }

            @Override
            public void onItemClickUpdate(LoginModel.UserData userData, int position) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!hasPermissions(getActivity(), PERMISSIONS)) {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                    } else {
                        addUserDialog(userData, true);
                    }
                } else {
                    addUserDialog(userData, true);
                }
            }

            @Override
            public void onItemClickDelete(LoginModel.UserData userData, int position) {
                deleteUser(userData);
            }
        });
        binding.recyclerUser.setAdapter(userAdapter);

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onQueryTextChange: text "+newText );
                if(isUser) {
                    if(newText.length()>0) {
                        List<LoginModel.UserData> temp = new ArrayList();
                        for (LoginModel.UserData d : userModel.getUserData()) {
                            //or use .equal(text) with you want equal match
                            //use .toLowerCase() for better matches
                            if (d.getName().toLowerCase().contains(newText.toLowerCase())) {
                                Log.e(TAG, "onQueryTextChange: name "+d.getName());
                                temp.add(d);
                            }
                        }
                        Log.e(TAG, "onQueryTextChange: temp size "+temp.size() );
                        //update recyclerview
                        userAdapter.updateList(temp);
                    }else{
                        userAdapter.updateList(userModel.getUserData());
                    }
                }
                return false;
            }
        });

    }


    private void callSpotRecyclerView(SpotDataModel userModel) {
        binding.recyclerSpot.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        SpotAdapter spotAdapter = new SpotAdapter(getActivity(), userModel.getSpotData(), new SpotAdapter.OnItemClickListener() {
            @Override
            public void onItemClickMerge(SpotDataModel.SpotDatum spotData, int position) {
                mergeSpotDialog(spotData);
            }

            @Override
            public void onItemClickUpdate(SpotDataModel.SpotDatum spotData, int position) {
                updateSpotDialog(spotData);
            }

            @Override
            public void onItemClickDelete(SpotDataModel.SpotDatum spotData, int position) {
                deleteSpot(spotData);
            }
        });
        binding.recyclerSpot.setAdapter(spotAdapter);
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!isUser) {
                    if(newText.length()>0) {
                        List<SpotDataModel.SpotDatum> temp = new ArrayList();
                        for (SpotDataModel.SpotDatum d : spotDataModel.getSpotData()) {
                            //or use .equal(text) with you want equal match
                            //use .toLowerCase() for better matches
                            if (d.getName().toLowerCase().contains(newText.toLowerCase())) {
                                temp.add(d);
                            }
                        }
                        //update recyclerview
                        spotAdapter.updateList(temp);
                    }else{
                        spotAdapter.updateList(spotDataModel.getSpotData());
                    }
                }
                return false;
            }
        });
    }

    private void mergeSpotDialog(SpotDataModel.SpotDatum spotData) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()/*, R.style.DialogTheme*/);
        spotMergeBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_assing_user_spot, null, false);
        alertDialog.setView(spotMergeBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(true);
        List<SpotDataModel.SpotDatum> secondlist = spotDataModel.getSpotData();
        Log.e(TAG, "mergeSpotDialog: size " + secondlist.size());
        SpotDataModel.SpotDatum selectedSp = null;
        for (SpotDataModel.SpotDatum sp : secondlist) {
            if (sp.getId().equalsIgnoreCase(spotData.getId())) {
                selectedSp = sp;
            }
        }
        secondlist.remove(selectedSp);
        final SpotDataModel.SpotDatum[] selectedSpNew = {null};
        spotMergeBinding.recyclerSpots.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        MergeSpotAdapter spotAdapterNew = new MergeSpotAdapter(getActivity(), secondlist, new MergeSpotAdapter.OnItemClickListener() {
            @Override
            public void onItemClickSelect(SpotDataModel.SpotDatum spotDatum, int position) {
                selectedSpNew[0] = spotDatum;
            }
        });
        spotMergeBinding.recyclerSpots.setAdapter(spotAdapterNew);
        spotMergeBinding.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
        spotMergeBinding.btnSave.setOnClickListener(v -> {
            if (selectedSpNew[0] != null)
                mergeSpotAPI(spotData, selectedSpNew[0], dialog);
            else
                Toast.makeText(getActivity(), "Please select spot", Toast.LENGTH_SHORT).show();
        });
        dialog.show();

        spotMergeBinding.mergSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onQueryTextChange: text "+newText );

                    if(newText.length()>0) {
                        List<SpotDataModel.SpotDatum> temp = new ArrayList();
                        for (SpotDataModel.SpotDatum d : secondlist) {
                            //or use .equal(text) with you want equal match
                            //use .toLowerCase() for better matches
                            if (d.getName().toLowerCase().contains(newText.toLowerCase())) {
                                Log.e(TAG, "onQueryTextChange: name merger "+d.getName());
                                temp.add(d);
                            }
                        }
                        Log.e(TAG, "onQueryTextChange: temp size merge "+temp.size() );
                        //update recyclerview
                        spotAdapterNew.updateList(temp);
                    }else{
                        spotAdapterNew.updateList(secondlist);
                    }

                return false;
            }
        });
    }


    ////////////////////////////////////// Permission and Image part ////////////////////////////////

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

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
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
                Glide.with(this).load(bm).into(useraAddDialog.profileImage);
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


    ////////////////////////////////****************** API CALL ***********************//////////////////////////////

    /////////////////////////////////////////////// User API ///////////////////////////////////////////////////////
    private void callChangeShiftAPI(LoginModel.UserData userData, String s, AlertDialog dialog) {
        callProgress();
        AndroidNetworking.post(Constant.BASE_URL + "assignShiftToUser")
                .addBodyParameter("user_id", userData.getId())
                .addBodyParameter("shift", s.toLowerCase())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get Policestation  " + response);
                        if (response.optString("response_status").equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(getActivity(), response.optString("response_message"));
                            getUserList(binding.spPoliceStation.getSelectedItem().toString(), "" + binding.spPoliceChowky.getSelectedItem());
                        } else
                            IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
                        dialog.dismiss();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    private void deleteUser(LoginModel.UserData userData) {
        callProgress();
        AndroidNetworking.post(Constant.BASE_URL + "deleteUser")
                .addBodyParameter("user_id", userData.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get Policestation  " + response);
                        if (response.optString("response_status").equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(getActivity(), response.optString("response_message"));
                            getUserList(binding.spPoliceStation.getSelectedItem().toString(), "" + binding.spPoliceChowky.getSelectedItem());
                        } else
                            IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    private void changeShift(LoginModel.UserData userData) {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        ChnageShiftBinding chnageShiftBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.chnage_shift, null, false);
        alertDialog.setView(chnageShiftBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        final String[] selected = {""};
        Log.e(TAG, "changeShift: " + userData.getShift());
        if (userData.getShift().equalsIgnoreCase("DAY")) {
            chnageShiftBinding.rbSh1.setChecked(true);
        }
        if (userData.getShift().equalsIgnoreCase("NIGHT")) {
            chnageShiftBinding.rbSh2.setChecked(true);
        }
        chnageShiftBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    selected[0] = "" + checkedRadioButton.getText().toString().trim();
                    Log.e(TAG, "onCheckedChanged: selected " + selected[0].trim());
                }
            }
        });

        chnageShiftBinding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        chnageShiftBinding.btnSave.setOnClickListener(v -> {
            if (selected[0].isEmpty()) {
                Toast.makeText(getActivity(), "Please select shift", Toast.LENGTH_SHORT).show();
                return;
            }
            callChangeShiftAPI(userData, selected[0], dialog);
        });
        dialog.show();

    }

    private void callAddUserAPI(AlertDialog dialog, boolean isEditable, LoginModel.UserData userData) {
        callProgress();
        String appendUrl = isEditable ? "updateUser" : "addUser";
        String BaseUrl = Constant.BASE_URL + appendUrl;
        if (isEditable) {
            AndroidNetworking.upload(BaseUrl)
                    .setPriority(Priority.MEDIUM)
                    .addMultipartParameter("name", useraAddDialog.etName.getText().toString())
                    .addMultipartParameter("designation", useraAddDialog.etDesignation.getText().toString())
                    .addMultipartParameter("mobile_number", isEditable ? userData.getMobileNumber() :
                            useraAddDialog.etMobile.getText().toString())
                    .addMultipartParameter("subZone", useraAddDialog.spStation.getSelectedItem().toString())
                    .addMultipartParameter("chowky", useraAddDialog.spChowky.getSelectedItem().toString())
                    .addMultipartParameter("shift", "" + useraAddDialog.spShift.getSelectedItem().toString().toLowerCase())
                    .addMultipartParameter("id", isEditable ? userData.getId() : null)
                    .addMultipartFile("userImage", imageFile)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            Log.e("TAG", "Constant.BASE_URL + appendUrl " + Constant.BASE_URL + appendUrl + "/n"
                                    + " onResponse: Add new  " + response);
                            LoginModel user = new Gson().fromJson(response.toString(), LoginModel.class);

                            if (user.getResponseStatus().equalsIgnoreCase("1") || user.getResponseStatus().equalsIgnoreCase("true")) {
                                IOUtils.showSnackBar(getActivity(), user.getResponseMessage());
                                progressDialog.dismiss();
                                dialog.dismiss();
                                getUserList("" + binding.spPoliceStation.getSelectedItem(), "" + binding.spPoliceChowky.getSelectedItem());

                            } else {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                IOUtils.errorShowSnackBar(getActivity(), user.getResponseMessage());
                            }

                        }

                        @Override
                        public void onError(ANError error) {
                            handleError(error);
                            dialog.dismiss();
                            progressDialog.dismiss();
                        }
                    });
        } else {
            if(imageFile!=null) {
                AndroidNetworking.upload(BaseUrl)
                        .setPriority(Priority.MEDIUM)
                        .addMultipartParameter("name", useraAddDialog.etName.getText().toString())
                        .addMultipartParameter("designation", useraAddDialog.etDesignation.getText().toString())
                        .addMultipartParameter("mobile_number", isEditable ? userData.getMobileNumber() :
                                useraAddDialog.etMobile.getText().toString())
                        .addMultipartParameter("subZone", useraAddDialog.spStation.getSelectedItem().toString())
                        .addMultipartParameter("chowky", useraAddDialog.spChowky.getSelectedItem().toString())
                        .addMultipartParameter("shift", "" + useraAddDialog.spShift.getSelectedItem().toString().toLowerCase())
                        .addMultipartFile("userImage", imageFile)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // do anything with response
                                Log.e("TAG", "onResponse: get Policestation  " + response);
                                LoginModel user = new Gson().fromJson(response.toString(), LoginModel.class);

                                if (user.getResponseStatus().equals("1")) {
                                    IOUtils.showSnackBar(getActivity(), user.getResponseMessage());
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                    getUserList("" + binding.spPoliceStation.getSelectedItem(), "" + binding.spPoliceChowky.getSelectedItem());

                                } else {
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                    IOUtils.errorShowSnackBar(getActivity(), user.getResponseMessage());
                                }

                            }

                            @Override
                            public void onError(ANError error) {
                                handleError(error);
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }
                        });
            }else{
                AndroidNetworking.upload(BaseUrl)
                        .setPriority(Priority.MEDIUM)
                        .addMultipartParameter("name", useraAddDialog.etName.getText().toString())
                        .addMultipartParameter("designation", useraAddDialog.etDesignation.getText().toString())
                        .addMultipartParameter("mobile_number", isEditable ? userData.getMobileNumber() :
                                useraAddDialog.etMobile.getText().toString())
                        .addMultipartParameter("subZone", useraAddDialog.spStation.getSelectedItem().toString())
                        .addMultipartParameter("chowky", useraAddDialog.spChowky.getSelectedItem().toString())
                        .addMultipartParameter("shift", "" + useraAddDialog.spShift.getSelectedItem().toString().toLowerCase())
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // do anything with response
                                Log.e("TAG", "onResponse: get add without image  " + response);
                                LoginModel user = new Gson().fromJson(response.toString(), LoginModel.class);

                                if (user.getResponseStatus().equals("1")) {
                                    IOUtils.showSnackBar(getActivity(), user.getResponseMessage());
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    getUserList("" + binding.spPoliceStation.getSelectedItem(), "" + binding.spPoliceChowky.getSelectedItem());

                                } else {
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    IOUtils.errorShowSnackBar(getActivity(), user.getResponseMessage());
                                }

                            }

                            @Override
                            public void onError(ANError error) {
                                handleError(error);
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }
                        });
            }
        }
    }

    private void getPoliceStationList() {
        policeStationData = new PoliceStationData();
        callProgress();
        AndroidNetworking.post(Constant.BASE_URL + "getChowkyListWithSubZone")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get Policestation  " + response);
                        progressDialog.dismiss();
                        policeStationData = new Gson().fromJson(response.toString(), PoliceStationData.class);
                        if (policeStationData.getResponseStatus().equalsIgnoreCase("1")) {
                            //   IOUtils.showSnackBar(getActivity(), policeStationData.getResponseMessage());
                            spineerStations();
                        } else {
                            IOUtils.errorShowSnackBar(getActivity(), "Something went to wrong!!!");
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    private void getUserList(String station, String chowky) {
        callProgress();
        AndroidNetworking.post(Constant.BASE_URL + "getUserListByPoliceStationAndChowky")
                .addBodyParameter("police_station", station)
                .addBodyParameter("police_chowky", chowky)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get Policestation  " + response);
                        progressDialog.dismiss();
                        userModel = new Gson().fromJson(response.toString(), UserModel.class);
                        callUserRecyclerView(userModel);
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    private void assignSoptToUserAPI(JSONObject jsonObject) {
        callProgress();
        AndroidNetworking.post(Constant.BASE_URL + "assignSoptToUser")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get Assing spot user  " + response);
                        if (response.optString("response_status").equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(getActivity(), response.optString("response_message"));
                            getSpotList(binding.spPoliceStation.getSelectedItem().toString(), binding.spPoliceChowky.getSelectedItem().toString());
                        } else
                            IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }


    ////////////////////////////////////////// Spots API /////////////////////////////////////////////////////////////

    private void getSpotList(String station, String chowky) {
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Spots are Loading...");
        progressDialog.show();
        IOUtils.hideKeyBoard(getActivity());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(150, TimeUnit.SECONDS)
                .connectTimeout(150, TimeUnit.SECONDS).build();
        spotDataModel = new SpotDataModel();

        AndroidNetworking.post(Constant.BASE_URL + "getSpotListByPoliceStationAndChowky")
                .addBodyParameter("police_station", station)
                .addBodyParameter("police_chowky", chowky)
                .addBodyParameter("user_id", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                .setOkHttpClient(client)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get spots  " + response);
                        progressDialog.dismiss();
                        spotDataModel = new SpotDataModel();
                        spotDataModel = new Gson().fromJson(response.toString(), SpotDataModel.class);
                        if(spotDataModel.getResponseStatus().equals("1"))
                        callSpotRecyclerView(spotDataModel);
                        else{
                            IOUtils.errorShowSnackBar(getActivity(),spotDataModel.getResponseMessage());
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    private void deleteSpot(SpotDataModel.SpotDatum spotData) {
        callProgress();
        AndroidNetworking.post(Constant.BASE_URL + "deleteSpot")
                .addBodyParameter("spot_id", spotData.getId())
                .addBodyParameter("logged_in_id", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get delete spot  " + response);
                        if (response.optString("response_status").equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(getActivity(), response.optString("response_message"));
                            getSpotList(binding.spPoliceStation.getSelectedItem().toString(), binding.spPoliceChowky.getSelectedItem().toString());
                        } else
                            IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
                      //  progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    private void callUpdateSpot(double latt, double lngg, String Address,SpotDataModel.SpotDatum spotData, AlertDialog dialog,
                                String assigned_ps, String assigned_pc,String cat) {
        callProgress();
        Log.e(TAG, "callUpdateSpot: url " + Constant.BASE_URL + "updateSpot");
        Log.e(TAG, "callUpdateSpot: spot_id " + spotData.getId());
        Log.e(TAG, "callUpdateSpot: name " + updateItemBinding.etSpotName.getText().toString());
        Log.e(TAG, "callUpdateSpot: address " + updateItemBinding.etAddress.getText().toString());
        Log.e(TAG, "callUpdateSpot: locality " + latt);
        Log.e(TAG, "callUpdateSpot: latitude " + lngg);
        Log.e(TAG, "callUpdateSpot: added_by " + Address);
        Log.e(TAG, "callUpdateSpot: existing_new " + spotData.getExistingNew());
        Log.e(TAG, "callUpdateSpot: added_by " + spotData.getAddedBy());
        Log.e(TAG, "callUpdateSpot: date " + (spotData.getDate().equalsIgnoreCase("No data found") ? IOUtils.currentdate() : spotData.getDate()));
        Log.e(TAG, "callUpdateSpot: category " + cat);
        Log.e(TAG, "callUpdateSpot: assignedUserIdArray " + spotData.getAssignedUserIdArray());

        AndroidNetworking.post(Constant.BASE_URL + "updateSpot")
                .addBodyParameter("spot_id", spotData.getId())
                .addBodyParameter("name", updateItemBinding.etSpotName.getText().toString())
                .addBodyParameter("address", updateItemBinding.etAddress.getText().toString())
                .addBodyParameter("locality", spotData.getLocality())
                .addBodyParameter("latitude", "" + latt)
                .addBodyParameter("longitude", "" + lngg)
                .addBodyParameter("added_by", "" + Address)
                .addBodyParameter("existing_new", "" + spotData.getExistingNew())
                .addBodyParameter("date", "" + (spotData.getDate().equalsIgnoreCase("No data found") ? IOUtils.currentdate() : spotData.getDate()))
                .addBodyParameter("category", "" + cat)
                .addBodyParameter("assignedUserIdArray", "" + spotData.getAssignedUserIdArray())
                .addBodyParameter("assigned_ps", "" + assigned_ps)
                .addBodyParameter("assigned_pc", "" + assigned_pc)
                .addBodyParameter("spot_status", "" + spotData.getSpot_status())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get update spot  " + response);
                        if (response.optString("response_status").equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(getActivity(), response.optString("response_message"));
                            //getSpotList(binding.spPoliceStation.getSelectedItem().toString(), binding.spPoliceChowky.getSelectedItem().toString());
                            getPoliceStationList();
                        } else
                            IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
                        progressDialog.dismiss();
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });

    }

    private void callSpotListAPI(LoginModel.UserData userData) {
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Spots are Loading...");
        progressDialog.show();
        IOUtils.hideKeyBoard(getActivity());
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(150, TimeUnit.SECONDS)
                .connectTimeout(150, TimeUnit.SECONDS).build();
        AndroidNetworking.post(Constant.BASE_URL + "getUserSpotsList")
                .addBodyParameter("user_id", userData.getId())
                .setPriority(Priority.HIGH)
                .setOkHttpClient(client)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get spots  " + response);
                        progressDialog.dismiss();
                        SpotDataModel userSpotDataModel = new Gson().fromJson(response.toString(), SpotDataModel.class);
                        assignSpotToUser(userSpotDataModel, userData);
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });

    }

    private void mergeSpotAPI(SpotDataModel.SpotDatum existing_spot, SpotDataModel.SpotDatum new_spot, AlertDialog dialog) {
        callProgress();
        Log.e(TAG, "mergeSpotAPI: exist " + existing_spot.getId());
        Log.e(TAG, "mergeSpotAPI: new " + new_spot.getId());
        Log.e(TAG, "mergeSpotAPI: new " + Constant.BASE_URL + "mergeSpot");

        AndroidNetworking.post(Constant.BASE_URL + "mergeSpot")
                .addBodyParameter("existing_spot_id", existing_spot.getId())
                .addBodyParameter("new_spot_id", new_spot.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: merge spots  " + response);
                        if (response.optString("response_status").equalsIgnoreCase("1")) {
                            IOUtils.showSnackBar(getActivity(), response.optString("response_message"));
                            getSpotList(binding.spPoliceStation.getSelectedItem().toString(), binding.spPoliceChowky.getSelectedItem().toString());
                            dialog.dismiss();
                        } else {
                            IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(newLat, newLong);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true)
                .title("" + IOUtils.getAddress(getActivity(), newLat, newLong));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        // googleMap.addMarker(markerOptions);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        dMap = googleMap;
      //  googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {}

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {}

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                LatLng latLng = marker.getPosition();
                Toast.makeText(getActivity(), "" + IOUtils.getAddress(getActivity(), latLng.latitude, latLng.longitude), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (mapBinding != null)
            mapBinding.tvAddress.setText(IOUtils.getAddress(getActivity(), location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onCameraIdle() {
        if (mapBinding != null)
            mapBinding.tvAddress.setText(IOUtils.getAddress(getActivity(), dMap.getCameraPosition().target.latitude, dMap.getCameraPosition().target.longitude));
    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }
}