package com.rgi.zone4live2.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.databinding.ActivityWebViewBinding;


public class WebViewActivity extends Activity {
    ActivityWebViewBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        String url = getIntent().getStringExtra("url");
        // do something with this URL.
        binding.webView.loadUrl(url);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        WebSettings settings = binding.webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        binding.webView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        binding.ivCloseScreen.setOnClickListener(v -> finish());
    }



}