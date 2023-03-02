package com.rgi.zone4live2.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.activites.ActivityLogin;
import com.rgi.zone4live2.activites.MainActivity;
import com.rgi.zone4live2.activites.WebViewActivity;
import com.rgi.zone4live2.apps.Zone4LiveApp;
import com.rgi.zone4live2.databinding.AddEditSpotLayoutBinding;
import com.rgi.zone4live2.databinding.AddNewIncidentLayoutBinding;
import com.rgi.zone4live2.databinding.AddNewSpotLayoutBinding;
import com.rgi.zone4live2.databinding.BottomSheetLayoutBinding;
import com.rgi.zone4live2.databinding.FragmentHomeBinding;
import com.rgi.zone4live2.model.MarkerData;
import com.rgi.zone4live2.model.PoliceStationData;
import com.rgi.zone4live2.model.SpotDataModel;
import com.rgi.zone4live2.model.ViewDetailModel;
import com.rgi.zone4live2.services.LocationService;
import com.rgi.zone4live2.services.LocationUpdatesService;
import com.rgi.zone4live2.services.MyWorker;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.IOUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener
        , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String TAG = HomeFragment.class.getName();
    public static final int REQUEST_CODE = 101;
    public static final int RequestPermissionCode = 11000;
    private final int PERMISSION_ALL = 1;
    private final int GALLERY = 100;
    private final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    FragmentHomeBinding binding;
    SupportMapFragment supportMapFragment;
    GoogleMap googleMap;
    ArrayList<MarkerData> markersArray = new ArrayList<MarkerData>();
    SpotDataModel spotDataModel;
    File imageFile, videoFile;
    ProgressDialog progressDialog;
    boolean isLoadFirst = true;
    LocationManager manager;
    boolean isNew = true;
    boolean isIncidance = false;
    double lat = 0.0, logn = 0.0;
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lat = intent.getDoubleExtra("lat", 0.0);
            logn = intent.getDoubleExtra("logn", 0.0);
        }
    };
    double newLat = 0.0, newLogn = 0.0;
    SpotDataModel.SpotDatum spotDatum;
    AddNewIncidentLayoutBinding incidentBinding;
    PoliceStationData policeStationData;
    ArrayAdapter policeStationAdapter, chowkyAdapter;
    ArrayAdapter policeProfAdapter, chowkyProfAdapter, categoryAdapter, subCategoryAdapter;
    String stringStation = "", stringChowky = "";
    String[] categoryArray = {"Vehicle Theft", "Dacoity", "HBT", "Robbery", "Accidental", "Others"};
    String[] dacoityArray = {"Professional"};
    String[] vehicleArray = {"2 Wheeler", "3 Wheeler", "4 Wheeler"};
    String[] hbtArray = {"Day HBT", "Night HBT"};
    String[] robberyArray = {"Chain Snatching", "Mobile Snatching", "Other Object"};
    String[] accidentalArray = {"Fatal", "Motor"};
    String[] otherArray = {"Other"};
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    String filterString = "general";
    private String provider;
    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false;
    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.requestLocationUpdates();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };
    private Calendar date;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // init();
        init();
        // startWorkMangerLocation();
        // AutoStart();
    }

    private void init() {
        ((MainActivity) getActivity()).toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_line, null);
            ((MainActivity) getActivity()).toolbar.setNavigationIcon(d);
        });
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        progressDialog = new ProgressDialog(getActivity());
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        changeLocationLister();
        Criteria criteria = new Criteria();
        provider = manager.getBestProvider(criteria, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        featchLastLocation();

        binding.fab.setOnClickListener(v -> {
            addDailog("Add Spot", "You want to add a new spot?", false);
        });

        binding.fabIncident.setOnClickListener(v -> {
            addDailog("Add Incident", "You want to add a new incident?", true);
        });
        getCurrentLocationLat();
        // mReceiver = new
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter("LocationReceiever"));



    }

    private void changeLocationLister() {
        LocationListener mLocListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, /*mLocListener*/this);
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null)
            Log.e(TAG, "changeLocationLister: lat " + location.getLatitude() + " Lon: " + location.getLongitude());
        manager.removeUpdates(this);

    }

    private void addDailog(String title, String subTitle, boolean incidance) {
        isIncidance = incidance;
        new AlertDialog.Builder(getActivity()).setTitle(title)
                .setMessage(subTitle)
                .setPositiveButton(getString(R.string.str_yes),
                        (dialog, which) -> {
                            isNew = true;
                            checkPermissionsAndLocation();
                        }).setNegativeButton(getString(R.string.str_no), (dialog, which) -> {
            dialog.dismiss();
        }).create().show();
    }

    private LatLng getCurrentLocationLat() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        final LatLng[] latLng = new LatLng[1];
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int lastlocationIndex = locationResult.getLocations().size() - 1;
                            lat = locationResult.getLocations().get(lastlocationIndex).getLatitude();
                            logn = locationResult.getLocations().get(lastlocationIndex).getLongitude();
                            latLng[0] = new LatLng(lat, logn);
                            Log.e(TAG, "onLocationResult:  " + lat + " -- " + logn);
                        }
                    }
                }, Looper.getMainLooper());
        startLocationService();
        return latLng[0];
    }

    private void checkPermissionsAndLocation() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!hasPermissions(getActivity(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                } else {
                    selectImage();
                }
            } else {
                selectImage();
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        isLoadFirst = true;
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

    private void featchLastLocation() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                supportMapFragment.getMapAsync(HomeFragment.this);
            }
        });
        Location location = null;
        if (provider != null)
            location = manager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            //  onLocationChanged(location);
        }
    }

    private void setMap() {
        spotDataModel = new SpotDataModel();
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        IOUtils.hideKeyBoard(getActivity());
        Log.e(TAG, "setMap: user id " + Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""));
        Log.e(TAG, "setMap: date " + IOUtils.currentdate());
        Log.e(TAG, "setMap: assigned_ps " + binding.spPoliceStation.getSelectedItem().toString());
        Log.e(TAG, "setMap: assigned_pc id " + binding.spPoliceChowky.getSelectedItem().toString());
        AndroidNetworking.post(Constant.BASE_URL + "getSpotData")
                .addBodyParameter("user_id", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                .addBodyParameter("date", /*"2029-09-16"*/IOUtils.currentdate())
                .addBodyParameter("assigned_ps", "" + binding.spPoliceStation.getSelectedItem().toString())
                .addBodyParameter("assigned_pc", "" + binding.spPoliceChowky.getSelectedItem().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e(TAG, "onResponse: get spot " + response);
                        if (progressDialog != null)
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        spotDataModel = new SpotDataModel();
                        spotDataModel = new Gson().fromJson(response.toString(), SpotDataModel.class);
                        ArrayList<SpotDataModel.SpotDatum> normalSpotData = new ArrayList<>();
                        ArrayList<SpotDataModel.SpotDatum> priorityNonStatusSpotData = new ArrayList<>();
                        ArrayList<SpotDataModel.SpotDatum> priorityHighSpotData = new ArrayList<>();
                        ArrayList<SpotDataModel.SpotDatum> priorityMediumSpotData = new ArrayList<>();
                        ArrayList<SpotDataModel.SpotDatum> priorityLowSpotData = new ArrayList<>();
                        ArrayList<SpotDataModel.SpotDatum> generalNonStatusSpotData = new ArrayList<>();
                        ArrayList<SpotDataModel.SpotDatum> generalHighSpotData = new ArrayList<>();
                        ArrayList<SpotDataModel.SpotDatum> generalMediumSpotData = new ArrayList<>();
                        ArrayList<SpotDataModel.SpotDatum> generalLowSpotData = new ArrayList<>();
                        if (spotDataModel.getResponseStatus().equalsIgnoreCase("1")) {
                            //IOUtils.showSnackBar(getActivity(), spotDataModel.getResponseMessage());
                            for (int i = 0; i < spotDataModel.getSpotData().size(); i++) {

                                createMarker(spotDataModel.getSpotData().get(i).getLatitude(), spotDataModel.getSpotData().get(i).getLongitude(),
                                        spotDataModel.getSpotData().get(i).getAddress(), spotDataModel.getSpotData().get(i).getAddress(),
                                        spotDataModel.getSpotData().get(i).getDefaultImage(),spotDataModel.getSpotData().get(i), i);
                            }

                        } else {
                            googleMap.clear();
                            IOUtils.errorShowSnackBar(getActivity(), spotDataModel.getResponseMessage());

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

    protected void createMarker(double latitude,
                                double longitude,
                                String title,
                                String snippet,
                                String iconResID,
                                SpotDataModel.SpotDatum spotDatum,int i) {


        if (spotDatum.getSpot_status().equalsIgnoreCase("normal")) {
            // if (spotDatum.getName().contains("हनुमान मंदिर"))
            if (spotDatum.getCategory().equalsIgnoreCase("priority")) {
                if (spotDatum.getStatus().isEmpty()) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .anchor(0.5f, 0.5f)
                            .title(spotDatum.getId())
                            .zIndex(i)
                            .snippet(snippet)
                            //        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .icon(bitmapDescriptorForVector(getActivity(), R.drawable.grey_star))
                    );
                    return;
                } else {
                    if (spotDatum.getStatus().equalsIgnoreCase("high")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.red_star))
                        );
                        return;
                    } else if (spotDatum.getStatus().equalsIgnoreCase("medium")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.orange_star))
                        );
                        return;
                    } else if (spotDatum.getStatus().equalsIgnoreCase("low")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.green_star))
                        );
                        return;
                    }
                }
            }
            else if (spotDatum.getCategory().equalsIgnoreCase("general")) {

                if (spotDatum.getStatus().isEmpty()) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .anchor(0.5f, 0.5f)
                            .title(spotDatum.getId())
                            .zIndex(i)
                            .snippet(snippet)
                            //        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .icon(bitmapDescriptorForVector(getActivity(), R.drawable.ic_grey))
                    );
                    return;
                } else {
                    if (spotDatum.getStatus().equalsIgnoreCase("high")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.ic_red))
                        );
                        return;
                    } else if (spotDatum.getStatus().equalsIgnoreCase("medium")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.ic_orange))
                        );
                        return;
                    } else if (spotDatum.getStatus().equalsIgnoreCase("low")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.ic_green))
                        );
                        return;
                    }
                }
            }
            else if(spotDatum.getCategory().equalsIgnoreCase("ganapati")) {
                if (spotDatum.getStatus().isEmpty()) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .anchor(0.5f, 0.5f)
                            .title(spotDatum.getId())
                            .zIndex(i)
                            .snippet(snippet)
                            //        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .icon(bitmapDescriptorForVector(getActivity(), R.drawable.ic_ganpati_gray))
                    );
                    return;
                } else {
                    if (spotDatum.getStatus().equalsIgnoreCase("high")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.ic_ganpati_red))
                        );
                        return;
                    } else if (spotDatum.getStatus().equalsIgnoreCase("medium")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.ic_ganpati_orange))
                        );
                        return;
                    } else if (spotDatum.getStatus().equalsIgnoreCase("low")) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(spotDatum.getId())
                                .zIndex(i)
                                .snippet(snippet)
                                //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .icon(bitmapDescriptorForVector(getActivity(), R.drawable.ic_ganpati_green))
                        );
                        return;
                    }
                }
            }
        }
        else {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(spotDatum.getId())
                    .zIndex(i)
                    .snippet(snippet)
                    //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    .icon(bitmapDescriptorForVector(getActivity(), R.drawable.warning)));
            return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady: came to here ");
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        googleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title("Here I am");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
        googleMap.addMarker(markerOptions);
        Log.e(TAG, "onMapReady: loading map values ");
        //setMap();
        getPoliceStationList();

    }

    public void dailog(SpotDataModel.SpotDatum markerData) {
        Log.e(TAG, "dailog: test " + markerData.getId());
        WindowManager manager = (WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE);
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()/*, R.style.DialogTheme*/);
        BottomSheetLayoutBinding writerDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_layout, null, false);
        alertDialog.setView(writerDialogBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        // dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_writer;

        Glide.with(getActivity()).load(markerData.getDefaultImage())
                .into(writerDialogBinding.spotImage);
        writerDialogBinding.tvTitle.setText(markerData.getName());
        writerDialogBinding.tvAddress.setText(markerData.getLocality() + "\n" + markerData.getAddress());

        writerDialogBinding.ivClose.setVisibility(View.VISIBLE);
        writerDialogBinding.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (!Zone4LiveApp.cache.readString(getActivity(), Constant.STATE, "").equalsIgnoreCase("user")) {
            writerDialogBinding.ivDelete.setVisibility(View.VISIBLE);
            writerDialogBinding.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlert(markerData,dialog);
                }
            });
        }
        else
        {
            writerDialogBinding.ivDelete.setVisibility(View.GONE);
        }
        writerDialogBinding.tvUploadImage.setOnClickListener(v -> {
            // double lat = 0.0, logn = 0.0;
            if (IOUtils.isInArea(lat, logn, markerData.getLatitude(), markerData.getLongitude())) {
                if (!Zone4LiveApp.isTimerStart) {
                    isNew = false;
                    checkPermissionsAndLocation();
                    dialog.dismiss();
                } else {
                    addAlertDialog("Alert", "You cannot upload the image from the same or nearby spot for the next 5 mins.");
                }
            } else {
                addAlertDialog("Alert", "You are so far from spot location");
            }


        });
        writerDialogBinding.tvAddSpot.setOnClickListener(v -> {
            selectImage();
            dialog.dismiss();
        });

        if (markerData.getSpot_status().equalsIgnoreCase("normal")) {
            writerDialogBinding.llSubmit.setVisibility(View.VISIBLE);
            writerDialogBinding.llComment.setVisibility(View.GONE);
            writerDialogBinding.tvTopTitle.setVisibility(View.GONE);

        } else {
            writerDialogBinding.llSubmit.setVisibility(View.GONE);
            writerDialogBinding.llComment.setVisibility(View.VISIBLE);
            writerDialogBinding.tvTopTitle.setVisibility(View.VISIBLE);
            writerDialogBinding.tvComment.setText(markerData.getComment());
            if (markerData.getSpot_status().equalsIgnoreCase("incident")) {
                if (markerData.getDatetime() != null) {
                    writerDialogBinding.llReported.setVisibility(View.VISIBLE);
                    writerDialogBinding.tvReported.setText(Zone4LiveApp.cache.readString(getActivity(), Constant.USER_NAME, ""));
                    writerDialogBinding.llDateTime.setVisibility(View.VISIBLE);
                    String[] separated = markerData.getDatetime().split(",");
                    writerDialogBinding.tvDateIncident.setText(separated[0].trim());
                    writerDialogBinding.tvTime.setText(separated[1].trim());
                }
            }

        }
        callViewDetailsAPI(dialog, markerData, writerDialogBinding);
        dialog.show();
    }

    private void showAlert(SpotDataModel.SpotDatum markerData, AlertDialog deleteDialog) {

        new AlertDialog.Builder(getActivity())
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this spot?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        deleteSpot(markerData,deleteDialog);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void addAlertDialog(String title, String subtitle) {
        new AlertDialog.Builder(getActivity()).setTitle(title)
                .setMessage(subtitle)
                .setPositiveButton("Ok",
                        (dialog, which) -> {
                            dialog.dismiss();
                        }).create().show();
    }


    private void deleteSpot(SpotDataModel.SpotDatum spotData, Dialog dialog) {
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
                            if (googleMap != null) {
                                googleMap.clear();
                            }
                            setMap();
                            // getSpotList(binding.spPoliceStation.getSelectedItem().toString(), binding.spPoliceChowky.getSelectedItem().toString());
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
                        dialog.dismiss();
                    }
                });
    }

    private void callViewDetailsAPI(AlertDialog dialog, SpotDataModel.SpotDatum markerData, BottomSheetLayoutBinding writerDialogBinding) {
        callProgress();
        AndroidNetworking.post(Constant.BASE_URL + "viewSpotDetailsNew")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter("spot_id", markerData.getId())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e("TAG", "onResponse: get viewSpotDetails  " + response);
                        progressDialog.dismiss();
                        ViewDetailModel viewDetailModel = new Gson().fromJson(response.toString(), ViewDetailModel.class);
                        if (viewDetailModel.getResponseStatus().equalsIgnoreCase("1")) {
                            writerDialogBinding.btnViewDetails.setVisibility(View.VISIBLE);
                            writerDialogBinding.btnViewDetails.setOnClickListener(v -> {
                                writerDialogBinding.linearViewDetails.removeAllViews();
                                writerDialogBinding.llViewDetails.setVisibility(View.VISIBLE);
                                int count = 0;
                                for (ViewDetailModel.StationDatum datum : viewDetailModel.getStationData()) {
                                    count++;
                                    String details = "";
                                    TextView tvData = new TextView(getActivity());
                                    tvData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                                    tvData.setTextColor(Color.BLACK);
                                    String imagePath = datum.getImagePath();
                                    String value = "<html><a href=\""+imagePath+"\">"+datum.getLastUpdate()+"</a></html>";
                                  //  String value = "<html><a>"+datum.getLastUpdate()+"</a></html>";
                                    details = count + ") User Name : " + datum.getName() +
                                            "<BR> Upload Time : " + value + "<BR>";
                                    tvData.setText(Html.fromHtml(details));
                                 //   tvData.setMovementMethod(LinkMovementMethod.getInstance());
                                    tvData.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url",imagePath)));
                                    writerDialogBinding.linearViewDetails.addView(tvData);
                                }
                            });
                            //   IOUtils.showSnackBar(getActivity(), policeStationData.getResponseMessage());

                        } else {
                            writerDialogBinding.btnViewDetails.setVisibility(View.GONE);
                            //  IOUtils.errorShowSnackBar(getActivity(), ""+viewDetailModel.getResponseMessage());
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        handleError(error);
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (spotDataModel.getSpotData().size() > 0) {
            for (int i = 0; i < spotDataModel.getSpotData().size(); i++) {
                if (marker.getTitle().equalsIgnoreCase(spotDataModel.getSpotData().get(i).getId())) {
                    dailog(spotDataModel.getSpotData().get(i));
                    spotDatum = spotDataModel.getSpotData().get(i);
                    break;
                }
            }
        } else {
            Toast.makeText(getActivity(), "Spot are not available", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private BitmapDescriptor bitmapDescriptorForVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth()
                , vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth()
                , vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: called " + isLoadFirst);
        if (isLoadFirst) {
            Log.e(TAG, "onResume: inside isLoadFirst");
            //  featchLastLocation();
            changeLocationLister();
        }
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        //manager.requestLocationUpdates(provider, 400, 1, this);
        isLoadFirst = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                int s = grantResults.length;
                int[] aray = grantResults;
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
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        featchLastLocation();
                        isLoadFirst = true;
                        onResume();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void selectImage() {
        ImagePicker.Companion.with(this)
                .cameraOnly()
                //   .crop()                    //Crop image(Optional), Check Customization for more option
                //.compress(1024)            //Final image size will be less than 1 MB(Optional)
                .compress(150)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                // .maxResultSize(620, 620)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
        isLoadFirst = false;
    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "x: reqcode " + requestCode + " resultCode  " + resultCode + " data " + data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                Log.d("what", "gale");
                if (data != null) {
                    Uri contentURI = data.getData();
                    String selectedVideoPath = getPath(contentURI);
                    Log.d("path", selectedVideoPath);
                    videoFile = new File(selectedVideoPath);
                    incidentBinding.etVideoName.setText(videoFile.getName());
                }

            } else {
                Bitmap bm = null;
                try {
                    bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    persistImage(bm, "" + System.currentTimeMillis());
                    if (isIncidance)
                        dialogAddIncidentSpot(bm, lat, logn);
                    else if (isNew) {
                        dialogAddNewSpot(bm, lat, logn);
                    } else {
                        dailogAddUpdateSpot(bm, lat, logn);
                    }
                    Log.e("TAG", "onActivityResult: bitmap " + bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private void dialogAddIncidentSpot(Bitmap bm, double latt, double lognn) {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()/*, R.style.DialogTheme*/);
        incidentBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.add_new_incident_layout, null, false);
        alertDialog.setView(incidentBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        // dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_writer;
        final RadioButton[] rb = new RadioButton[1];

        Glide.with(getActivity()).load(bm)
                .into(incidentBinding.ivSpot);
        incidentBinding.etVideoName.setText("");
        incidentBinding.etAddress.setText("" + IOUtils.getLocality(getActivity(), latt, lognn) +
                "\n" + IOUtils.getAddress(getActivity(), latt, lognn));

        String chowky = Zone4LiveApp.cache.readString(getActivity(), Constant.CHOWKY, "");

       // String msg = "This new spot will be visible under ("+binding.spPoliceChowky.getSelectedItem().toString() +") chowky because you are mapped to that chowky by DO.";
        String msg = "This new spot will be visible under ("+chowky +") chowky because you are mapped to that chowky by DO.";

        incidentBinding.tvStationText.setText(msg);

        incidentBinding.btnVideo.setOnClickListener(v -> {
            videoFile = null;
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY);
        });

        categoryAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categoryArray);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incidentBinding.spCategory.setAdapter(categoryAdapter);
        incidentBinding.spCategory.setTitle("Select Category.");


        incidentBinding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // {"Vehicle Theft","Dacoity","HBT","Robbery","Accidental","Others"};
                String selected = "" + incidentBinding.spCategory.getSelectedItem();
                if (selected.equalsIgnoreCase("Vehicle Theft")) {
                    subCategoryAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, vehicleArray);
                    subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    incidentBinding.spSubCategory.setAdapter(subCategoryAdapter);
                    incidentBinding.spSubCategory.setTitle("Select Sub Category.");
                }
                if (selected.equalsIgnoreCase("Dacoity")) {
                    subCategoryAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, dacoityArray);
                    subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    incidentBinding.spSubCategory.setAdapter(subCategoryAdapter);
                    incidentBinding.spSubCategory.setTitle("Select Sub Category.");
                }
                if (selected.equalsIgnoreCase("Robbery")) {
                    subCategoryAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, robberyArray);
                    subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    incidentBinding.spSubCategory.setAdapter(subCategoryAdapter);
                    incidentBinding.spSubCategory.setTitle("Select Sub Category.");
                }
                if (selected.equalsIgnoreCase("HBT")) {
                    subCategoryAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, hbtArray);
                    subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    incidentBinding.spSubCategory.setAdapter(subCategoryAdapter);
                    incidentBinding.spSubCategory.setTitle("Select Sub Category.");
                }
                if (selected.equalsIgnoreCase("Accidental")) {
                    subCategoryAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, accidentalArray);
                    subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    incidentBinding.spSubCategory.setAdapter(subCategoryAdapter);
                    incidentBinding.spSubCategory.setTitle("Select Sub Category.");
                }
                if (selected.equalsIgnoreCase("Others")) {
                    subCategoryAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, otherArray);
                    subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    incidentBinding.spSubCategory.setAdapter(subCategoryAdapter);
                    incidentBinding.spSubCategory.setTitle("Select Sub Category.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        incidentBinding.etDate.setOnClickListener(v -> {
            final Calendar currentDate = Calendar.getInstance();
            date = Calendar.getInstance();
          /*  DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    date.set(year, monthOfYear, dayOfMonth);
//                    incidentBinding.etDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year
//                            +"\t\t "+selectedHour+" : "+selectedMinute);
                    //timePicker(year,monthOfYear,dayOfMonth,currentDate);
                }
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));*/


            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this,
                    currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
        incidentBinding.btnSubmit.setOnClickListener(v -> {
            if (incidentBinding.etComment.getText().toString().isEmpty()) {
                IOUtils.errorShowSnackBar(getActivity(), "Please enter comment");
                incidentBinding.etComment.setFocusable(true);
                incidentBinding.etComment.setError("Please enter comment");
                return;
            }
            if (incidentBinding.etDate.getText().toString().isEmpty()) {
                IOUtils.errorShowSnackBar(getActivity(), "Please select date");
                incidentBinding.etDate.setFocusable(true);
                incidentBinding.etDate.setError("Please select Date");
                return;
            }

            callAddPointApi("", "", dialog, latt, lognn, incidentBinding.etComment.getText().toString(), "incident");
            //callUpdatePointApi("", incidentBinding.etComment.getText().toString(), dialog, latt, lognn, "incident");
        });
        incidentBinding.ivClose.setOnClickListener(v -> {
            incidentBinding.etVideoName.setText("");
            dialog.dismiss();
        });


        dialog.show();

    }

    private void dialogAddNewSpot(Bitmap bm, double latt, double lognn) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()/*, R.style.DialogTheme*/);
        AddNewSpotLayoutBinding writerDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.add_new_spot_layout, null, false);
        alertDialog.setView(writerDialogBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        // dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_writer;
        final RadioButton[] rb = new RadioButton[1];

        Glide.with(getActivity()).load(bm)
                .into(writerDialogBinding.ivSpot);

        writerDialogBinding.etAddress.setText("" + IOUtils.getLocality(getActivity(), latt, lognn) +
                "\n" + IOUtils.getAddress(getActivity(), latt, lognn));

        String chowky = Zone4LiveApp.cache.readString(getActivity(), Constant.CHOWKY, "");

        String msg = "This new spot will be visible under ("+chowky +") chowky because you are mapped to that chowky by DO.";

        writerDialogBinding.tvStationText.setText(msg);

        writerDialogBinding.btnSubmit.setOnClickListener(v -> {
            String status = "";
            int selectedId = writerDialogBinding.rgGroup.getCheckedRadioButtonId();
            RadioButton genderradioButton = dialog.findViewById(selectedId);
            if (selectedId != -1) {
                status = genderradioButton.getText().toString();
            }
            if (writerDialogBinding.etLocality.getText().toString().isEmpty()) {
                IOUtils.errorShowSnackBar(getActivity(), "Please enter spot name.");
                writerDialogBinding.etLocality.setError("Please enter location name.");
                return;
            }
            if (status.isEmpty()) {
                IOUtils.errorShowSnackBar(getActivity(), "Please select category.");
                return;
            }
            callAddPointApi(status, writerDialogBinding.etLocality.getText().toString(), dialog, latt, lognn, "", "normal");

        });
        writerDialogBinding.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });


        dialog.show();
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

    public void dailogAddUpdateSpot(Bitmap bm, double latt, double lognn) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()/*, R.style.DialogTheme*/);
        AddEditSpotLayoutBinding writerDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.add_edit_spot_layout, null, false);
        alertDialog.setView(writerDialogBinding.getRoot());
        AlertDialog dialog = alertDialog.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        //dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_writer;
        final RadioButton[] rb = new RadioButton[1];

        Glide.with(getActivity()).load(bm)
                .into(writerDialogBinding.ivSpot);
        writerDialogBinding.etAddress.setText("" + IOUtils.getLocality(getActivity(), latt, lognn) +
                "\n" + IOUtils.getAddress(getActivity(), latt, lognn));

        String chowky = Zone4LiveApp.cache.readString(getActivity(), Constant.CHOWKY, "");

        String msg = "This new spot will be visible under ("+chowky +") chowky because you are mapped to that chowky by DO.";

        writerDialogBinding.tvStationText.setText(msg);
        writerDialogBinding.tvStationText.setVisibility(View.GONE);

        writerDialogBinding.btnSubmit.setOnClickListener(v -> {
            String status = "";
            int selectedId = writerDialogBinding.rgGroup.getCheckedRadioButtonId();
            RadioButton genderradioButton = dialog.findViewById(selectedId);
            if (selectedId != -1) {
                status = genderradioButton.getText().toString();
                Log.e(TAG, "dailogAddUpdateSpot: status " + status);
            }
            if (status.isEmpty()) {
                IOUtils.errorShowSnackBar(getActivity(), "Please select status");
                return;
            }

            if (status.equalsIgnoreCase("high") || status.equalsIgnoreCase("medium")) {
                if (writerDialogBinding.etComment.getText().toString().isEmpty()) {
                    IOUtils.errorShowSnackBar(getActivity(), "Please enter comment");
                    writerDialogBinding.etComment.setError("Please enter comment");
                    writerDialogBinding.etComment.setFocusable(true);
                    return;
                }
            }
            callUpdatePointApi(status, writerDialogBinding.etComment.getText().toString(), dialog, latt, lognn, "normal");

        });
        writerDialogBinding.ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void callUpdatePointApi(String status, String s, AlertDialog dialog, double latt, double lognn, String sopt_status) {
        //spotDatum4
        if (IOUtils.isInternetPresent(getActivity())) {
            IOUtils.hideKeyBoard(getActivity());
            //show progress dialog
            // getCurrentLocationLat();
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Please Wait...");
            progressDialog.show();
            IOUtils.hideKeyBoard(getActivity());

            AndroidNetworking.upload(Constant.BASE_URL + "addSpotHistory")
                    .addMultipartParameter("name", Zone4LiveApp.cache.readString(getActivity(), Constant.USER_NAME, ""))
                    .addMultipartParameter("latitude", "" + latt)
                    .addMultipartParameter("longitude", "" + lognn)
                    .addMultipartParameter("user_id", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                    .addMultipartParameter("spot_id", "" + (spotDatum.getId().isEmpty() ? "" : spotDatum.getId()))
                    .addMultipartParameter("comments", s)
                    .addMultipartParameter("to_far_flag", "1")
                    .addMultipartParameter("distance", "")
                    .addMultipartParameter("date", IOUtils.currentdate())
                    .addMultipartParameter("status", status)
                    .addMultipartFile("imagePath", imageFile)
                    .addHeaders("Connection", "close")
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
                            Log.e("TAG", "onResponse: addSpotHistory " + response);
                            if (response.optString("response_status").equalsIgnoreCase("1")) {
                                if (googleMap != null) {
                                    googleMap.clear();
                                }
                                setMap();
                                IOUtils.showSnackBar(getActivity(), response.optString("response_message"));
                                dialog.dismiss();
                                Zone4LiveApp.countDownTimer.start();
                            } else {
                                IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
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

    private void callAddPointApi(String status, String s, AlertDialog dialogs, double latt, double lognn, String comment, String spot_status) {
        if (IOUtils.isInternetPresent(getActivity())) {
            IOUtils.hideKeyBoard(getActivity());
            //show progress dialog
            // getCurrentLocationLat();
            String spot_category = "", spot_sub_category = "", datetime = "";
            if (incidentBinding != null) {
                spot_category = "" + incidentBinding.spCategory.getSelectedItem();
                spot_sub_category = "" + incidentBinding.spSubCategory.getSelectedItem();
                datetime = "" + incidentBinding.etDate.getText().toString();
            }
            if (status.equalsIgnoreCase("Ganpati")) {
                status = "ganapati";
            }

            Log.e(TAG, "callAddPointApi: spot status " + status);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Please Wait...");
            progressDialog.show();
            IOUtils.hideKeyBoard(getActivity());
            if (videoFile != null) {
                Log.e(TAG, "callAddPointApi:  added_by " + Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""));
                AndroidNetworking.upload(Constant.BASE_URL + "addSpot")
                        .addMultipartParameter("user_id", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                        .addMultipartParameter("name", s)
                        .addMultipartParameter("address", "" + IOUtils.getAddress(getActivity(), latt, lognn))
                        .addMultipartParameter("locality", "" + IOUtils.getLocality(getActivity(), latt, lognn))
                        .addMultipartParameter("latitude", "" + latt)
                        .addMultipartParameter("longitude", "" + lognn)
                        .addMultipartParameter("category", "" + status.toLowerCase())
                        .addMultipartParameter("existing_new", isNew ? "new" : "existing")
                        .addMultipartParameter("added_by", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                        .addMultipartParameter("date", IOUtils.currentdate())
                        .addMultipartParameter("comment", comment)
                        .addMultipartParameter("spot_status", spot_status)
                        .addMultipartParameter("assignedUserIdArray", "[" + Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, "") + "]")
                        .addMultipartFile("defaultImage", imageFile)
                        .addMultipartFile("defaultVideo", videoFile != null ? videoFile : null)
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
                                Log.e("TAG", "onResponse: addSpot " + response);
                                if (response.optString("response_status").equalsIgnoreCase("1")) {
                                    if (googleMap != null) {
                                        googleMap.clear();
                                    }
                                    setMap();
                                    dialogs.dismiss();
                                    IOUtils.showSnackBar(getActivity(), response.optString("response_message"));

                                } else {
                                    IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
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

            } else {
                AndroidNetworking.upload(Constant.BASE_URL + "addSpot")
                        .addMultipartParameter("user_id", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                        .addMultipartParameter("name", s)
                        .addMultipartParameter("address", "" + IOUtils.getAddress(getActivity(), latt, lognn))
                        .addMultipartParameter("locality", "" + IOUtils.getLocality(getActivity(), latt, lognn))
                        .addMultipartParameter("latitude", "" + latt)
                        .addMultipartParameter("longitude", "" + lognn)
                        .addMultipartParameter("category", "" + status.toLowerCase())
                        .addMultipartParameter("existing_new", isNew ? "new" : "existing")
                        .addMultipartParameter("added_by", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                        .addMultipartParameter("date", IOUtils.currentdate())
                        .addMultipartParameter("comment", comment)
                        .addMultipartParameter("spot_status", spot_status)
                        .addMultipartParameter("spot_category", spot_category)
                        .addMultipartParameter("spot_sub_category", spot_sub_category)
                        .addMultipartParameter("datetime", datetime)
                        .addMultipartParameter("assignedUserIdArray", "[" + Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, "") + "]")
                        .addMultipartFile("defaultImage", imageFile)
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
                                Log.e("TAG", "onResponse: js " + response);
                                if (response.optString("response_status").equalsIgnoreCase("1")) {
                                    IOUtils.showSnackBar(getActivity(), response.optString("response_message"));
                                    dialogs.dismiss();
                                    init();
                                } else {
                                    IOUtils.errorShowSnackBar(getActivity(), response.optString("response_message"));
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
            }

        } else
            IOUtils.showSnackBar(getActivity(), "Please check internet connectivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        manager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        logn = location.getLongitude();
        //   Toast.makeText(getActivity(), "location changed \n\n" + IOUtils.getAddress(getActivity(), lat, logn), Toast.LENGTH_SHORT).show();
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, logn), 16.0f));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getActivity(), "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.iv_refresh) {
            isLoadFirst = true;
            if (googleMap != null)
                googleMap.clear();
            init();
            return true;
        }

        if (id == R.id.iv_spot) {

        /*    Toast.makeText(getActivity(), "****************Fetach****************\n " + IOUtils.getAddress(getActivity(), lat
                    , logn), Toast.LENGTH_SHORT).show();*/
            addDailog("Add Spot", "You want to add a new spot?", false);
            return true;
        }

        if (id == R.id.iv_data) {
            /*Toast.makeText(getActivity(), "****************Fetach inc****************\n " + IOUtils.getAddress(getActivity(), lat
                    , logn), Toast.LENGTH_SHORT).show();*/
            addDailog("Add Incident", "You want to add a new incident?", true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isWorkScheduled(List<WorkInfo> workInfos) {
        boolean running = false;
        if (workInfos == null || workInfos.size() == 0) return false;
        for (WorkInfo workStatus : workInfos) {
            running = workStatus.getState() == WorkInfo.State.RUNNING | workStatus.getState() == WorkInfo.State.ENQUEUED;
        }
        return running;
    }

    private void startWorkMangerLocation() {
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                .addTag(TAG)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);


    }

    private void getPoliceStationList() {
        policeStationData = new PoliceStationData();
        callProgress();
        AndroidNetworking.post(Constant.BASE_URL + "getChowkyListWithSubZoneByUserId")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter("user_id", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
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
        Log.e(TAG, "spineerStations: station " + Zone4LiveApp.cache.readString(getActivity(), Constant.SUBZONE, ""));
        binding.spPoliceStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "spinner: postion " + position);
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
                    Log.e(TAG, "spinnerChowky: chow pos if " + spinnerPosition);
                    binding.spPoliceChowky.setSelection(spinnerPosition);
                } else if (strChowky != null) {
                    int spinnerPosition = chowkyAdapter.getPosition(strChowky);
                    Log.e(TAG, "spinnerChowky: chowk if else " + spinnerPosition);
                    binding.spPoliceChowky.setSelection(spinnerPosition);
                }
                binding.spPoliceChowky.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.e(TAG, "onItemSelected: selected " + binding.spPoliceStation.getSelectedItem()
                                + " -- " + binding.spPoliceChowky.getSelectedItem());
                        stringChowky = binding.spPoliceChowky.getSelectedItem().toString();
                        stringStation = binding.spPoliceStation.getSelectedItem().toString();
                        //getSpotList(station, datum.getChowkyData().get(position).getChowky());
                        if (googleMap != null)
                            googleMap.clear();
                        setMap();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }
    }

    @Override
    public void onStop() {
        if (mBound) {
            getActivity().unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onStart() {
        getActivity().bindService(new Intent(getActivity(), LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
        super.onStart();
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

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equalsIgnoreCase(service.service.getClassName())) {
                    if (service.foreground)
                        return true;
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getActivity(), LocationService.class);
            intent.setAction(Constant.ACTION_START_LOCATION_SERVICE);
            getActivity().startService(intent);
            Log.e(TAG, "startLocationService: service get start.");
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getActivity(), LocationService.class);
            intent.setAction(Constant.ACTION_START_LOCATION_SERVICE);
            getActivity().startService(intent);
            Log.e(TAG, "stopLocationService: service get stop.");
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        myYear = year;
        myday = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        incidentBinding.etDate.setText(myday + "/" + (myMonth + 1) + "/" + myYear
                + ", " + (String.valueOf(myHour).length() == 0 ? "0" + myHour : myHour) + " : "
                + (String.valueOf(myMinute).length() == 0 ? "0" + myMinute : myMinute));
    }


    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s \n address: %3$s",
                    loc.getLongitude(), loc.getLatitude(), IOUtils.getAddress(getActivity(), loc.getLatitude(), loc.getLongitude())
            );
            //  Log.e(TAG, "onLocationChanged: mylocationLister lat "+loc.getLatitude()+" long "+loc.getLongitude() );
            //  Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            lat = loc.getLatitude();
            logn = loc.getLongitude();
        }

        public void onProviderDisabled(String arg0) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }


}
