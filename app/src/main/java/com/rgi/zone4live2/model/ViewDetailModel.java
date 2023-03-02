package com.rgi.zone4live2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ViewDetailModel {

    @SerializedName("response_status")
    @Expose
    private String responseStatus;
    @SerializedName("response_message")
    @Expose
    private String responseMessage;
    @SerializedName("station_data")
    @Expose
    private List<StationDatum> stationData = null;

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

    public List<StationDatum> getStationData() {
        return stationData;
    }

    public void setStationData(List<StationDatum> stationData) {
        this.stationData = stationData;
    }

    public class StationDatum {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("last_update")
        @Expose
        private String lastUpdate;
        @SerializedName("imagePath")
        @Expose
        private String imagePath;
        @SerializedName("chowky")
        @Expose
        private String chowky;
        @SerializedName("subZone")
        @Expose
        private String subZone;

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getChowky() {
            return chowky;
        }

        public void setChowky(String chowky) {
            this.chowky = chowky;
        }

        public String getSubZone() {
            return subZone;
        }

        public void setSubZone(String subZone) {
            this.subZone = subZone;
        }

    }
}