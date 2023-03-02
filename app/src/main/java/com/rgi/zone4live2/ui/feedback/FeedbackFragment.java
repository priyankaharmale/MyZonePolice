package com.rgi.zone4live2.ui.feedback;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.rgi.zone4live2.R;
import com.rgi.zone4live2.activites.MainActivity;
import com.rgi.zone4live2.apps.Zone4LiveApp;
import com.rgi.zone4live2.databinding.FragmentFeedbackBinding;
import com.rgi.zone4live2.utils.Constant;
import com.rgi.zone4live2.utils.IOUtils;

import org.json.JSONObject;

public class FeedbackFragment extends Fragment {
    public static final String TAG = FeedbackFragment.class.getName();
    FragmentFeedbackBinding binding;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false);
        return binding.getRoot();
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
        progressDialog = new ProgressDialog(getActivity());
        binding.btnSubmit.setOnClickListener(v -> {
            if (binding.editText1.getText().toString().isEmpty()) {
                binding.editText1.setError("Please Enter feedback.");
                IOUtils.errorShowSnackBar(getActivity(), "Please enter the feedback.");
                return;
            }
            feedbackApi();
        });


    }

    private void feedbackApi() {
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        IOUtils.hideKeyBoard(getActivity());
        Log.e(TAG, "feedbackApi: user_id "+Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, "") );
        Log.e(TAG, "feedbackApi: date "+IOUtils.currentdate());
        Log.e(TAG, "feedbackApi: comments "+binding.editText1.getText().toString());
        AndroidNetworking.post(Constant.BASE_URL+"sendFeedback")
                .addBodyParameter("user_id", Zone4LiveApp.cache.readString(getActivity(), Constant.USERID, ""))
                .addBodyParameter("date", IOUtils.currentdate())
                .addBodyParameter("comment", binding.editText1.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e(TAG, "onResponse: Feedback " + response);
                        progressDialog.dismiss();
                        if (response.optBoolean("status") == true) {
                            IOUtils.showSnackBar(getActivity(), "Feedback sent successfully.");
                            binding.editText1.setText("");
                        } else
                            IOUtils.errorShowSnackBar(getActivity(), "Something went to wrong");
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