package com.rgi.zone4live2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PoliceStationData implements Serializable {
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

        @SerializedName("subZone")
        @Expose
        private String subZone;
        @SerializedName("chowky_data")
        @Expose
        private List<ChowkyDatum> chowkyData = null;

        public String getSubZone() {
            return subZone;
        }

        public void setSubZone(String subZone) {
            this.subZone = subZone;
        }

        public List<ChowkyDatum> getChowkyData() {
            return chowkyData;
        }

        public void setChowkyData(List<ChowkyDatum> chowkyData) {
            this.chowkyData = chowkyData;
        }

    }

    public class ChowkyDatum {

        @SerializedName("chowky")
        @Expose
        private String chowky;

        public String getChowky() {
            return chowky;
        }

        public void setChowky(String chowky) {
            this.chowky = chowky;
        }

    }

}
