package com.rgi.zone4live2.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.rgi.zone4live2.R;


public class DialogAlertUtils {
    private Context mContext;
    private Dialog mDialog;
    private TextView mDialogYesButton;
    private TextView mDialogNoButton;
    private OnAlertDialogClickListener onAlertDialogBtnClick;
    public DialogAlertUtils(Context context) {
        this.mContext = context;
    }

    public void showDialog(String title, String msg) {
        TextView mDialogText, mDialogTitle;
        if (mDialog == null) {
            mDialog = new Dialog(mContext, R.style.CustomDialogTheme);
        }
        mDialog.setContentView(R.layout.dialog_alert);
        mDialog.setCancelable(true);
        mDialog.show();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

        mDialogText = mDialog.findViewById(R.id.dialog_alert_text);
        mDialogTitle = mDialog.findViewById(R.id.dialog_alert_title);
        mDialogYesButton = mDialog.findViewById(R.id.button_yes);
        mDialogNoButton = mDialog.findViewById(R.id.button_no);

        mDialogTitle.setText(title);
        mDialogText.setText(msg);
        initDialogButtons();
    }

    public interface OnAlertDialogClickListener {
        void OnYesClick();

        void OnNoClick();
    }

    private void initDialogButtons() {

        mDialogYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
                if (onAlertDialogBtnClick != null)
                    onAlertDialogBtnClick.OnYesClick();
            }
        });

        mDialogNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
                if (onAlertDialogBtnClick != null)
                    onAlertDialogBtnClick.OnNoClick();
            }
        });
    }

    public void setCallBack(OnAlertDialogClickListener callBack) {
        this.onAlertDialogBtnClick = callBack;
    }

    public void dismissDialog() {
        mDialog.dismiss();
    }
}
