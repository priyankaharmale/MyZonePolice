package com.rgi.zone4live2.activites;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.apps.Zone4LiveApp;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.DialogAlertUtils;
import com.rgi.zone4live2.utils.IOUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.WorkInfo;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements DialogAlertUtils.OnAlertDialogClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    TextView nav_user, nav_desgination;
    public CircleImageView imageView;
    private DialogAlertUtils dialogAlertUtils;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        nav_user = hView.findViewById(R.id.tvName);
        nav_desgination = hView.findViewById(R.id.tvDesignation);
        imageView = hView.findViewById(R.id.imageView);
        nav_user.setText(Zone4LiveApp.cache.readString(MainActivity.this, Constant.USER_NAME, ""));
        nav_desgination.setText(Zone4LiveApp.cache.readString(MainActivity.this, Constant.DESIGNATION, ""));
        Glide.with(this).load(Zone4LiveApp.cache.readString(MainActivity.this, Constant.PROFILEFROMSERVER, ""))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round).into(imageView);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_user)
                .setDrawerLayout(drawer)
                .build();
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_line, null);
            toolbar.setNavigationIcon(d);
        });



        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more));
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        dialogAlertUtils = new DialogAlertUtils(this);
        dialogAlertUtils.setCallBack(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            dialogAlertUtils.showDialog(getString(R.string.nav_menu_logout), getString(R.string.app_logout));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCloseAlert() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.str_exit))
                .setMessage(getString(R.string.str_are_sure))
                .setPositiveButton(getString(R.string.str_yes),
                        (dialog, which) -> {
                            finish();
                        }).setNegativeButton(getString(R.string.str_no), (dialog, which) -> {
            // Do nothing
            dialog.dismiss();
        }).create().show();
    }

        @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void OnYesClick() {
        IOUtils.logout(this);
    }

    @Override
    public void OnNoClick() {

    }

    @Override
    public void onBackPressed() {
        if (Navigation.findNavController(this, R.id.nav_host_fragment)
                .getCurrentDestination().getId() == R.id.nav_home) {
            // handle back button the way you want here
            showCloseAlert();
            return;
        }
        super.onBackPressed();
    }

    private boolean isWorkScheduled(List<WorkInfo> workInfos) {
        boolean running = false;
        if (workInfos == null || workInfos.size() == 0) return false;
        for (WorkInfo workStatus : workInfos) {
            running = workStatus.getState() == WorkInfo.State.RUNNING | workStatus.getState() == WorkInfo.State.ENQUEUED;
        }
        return running;
    }
}