package com.rgi.zone4live2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserModel {

    @SerializedName("response_status")
    @Expose
    private String responseStatus;
    @SerializedName("response_message")
    @Expose
    private String responseMessage;
    @SerializedName("user_data")
    @Expose
    private List<LoginModel.UserData> userData = null;

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<LoginModel.UserData> getUserData() {
        return userData;
    }

    public void setUserData(List<LoginModel.UserData> userData) {
        this.userData = userData;
    }



}
