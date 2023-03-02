package com.rgi.zone4live2.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LoginModel implements Serializable {
    @SerializedName("response_status")
    @Expose
    private String responseStatus;
    @SerializedName("response_message")
    @Expose
    private String responseMessage;
    @SerializedName("user_data")
    @Expose
    private UserData userData;

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

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }


    public class UserData {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("designation")
        @Expose
        private String designation;
        @SerializedName("userImage")
        @Expose
        private String userImage;
        @SerializedName("mobile_number")
        @Expose
        private String mobileNumber;
        @SerializedName("subZone")
        @Expose
        private String subZone;
        @SerializedName("chowky")
        @Expose
        private String chowky;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("last_update")
        @Expose
        private String lastUpdate;
        @SerializedName("shift")
        @Expose
        private String shift;
        @SerializedName("is_password")
        @Expose
        private String is_password;
        @SerializedName("otp")
        @Expose
        private String otp;

        public String getIs_password() {
            return is_password;
        }

        public void setIs_password(String is_password) {
            this.is_password = is_password;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getSubZone() {
            return subZone;
        }

        public void setSubZone(String subZone) {
            this.subZone = subZone;
        }

        public String getChowky() {
            return chowky;
        }

        public void setChowky(String chowky) {
            this.chowky = chowky;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getShift() {
            return shift;
        }

        public void setShift(String shift) {
            this.shift = shift;
        }
    }

}
