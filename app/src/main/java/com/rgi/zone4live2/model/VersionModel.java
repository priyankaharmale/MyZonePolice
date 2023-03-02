package com.rgi.zone4live2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionModel  {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("version_info")
    @Expose
    private VersionInfo versionInfo;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }

    public class VersionInfo {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("version")
        @Expose
        private String version;
        @SerializedName("device_type")
        @Expose
        private String deviceType;
        @SerializedName("last_update")
        @Expose
        private String lastUpdate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

    }

}

