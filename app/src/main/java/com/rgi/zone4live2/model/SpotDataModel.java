package com.rgi.zone4live2.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpotDataModel implements Serializable {

    @SerializedName("response_status")
    @Expose
    private String responseStatus;
    @SerializedName("response_message")
    @Expose
    private String responseMessage;

    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("spot_data")
    @Expose
    private List<SpotDatum> spotData = null;

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

    public List<SpotDatum> getSpotData() {
        return spotData;
    }

    public void setSpotData(List<SpotDatum> spotData) {
        this.spotData = spotData;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public class SpotDatum {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("defaultImage")
        @Expose
        private String defaultImage;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("locality")
        @Expose
        private String locality;
        @SerializedName("latitude")
        @Expose
        private double latitude;
        @SerializedName("longitude")
        @Expose
        private double longitude;
        @SerializedName("existing_new")
        @Expose
        private String existingNew;
        @SerializedName("added_by")
        @Expose
        private String addedBy;
        @SerializedName("assignedUserIdArray")
        @Expose
        private String assignedUserIdArray;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("last_update")
        @Expose
        private String lastUpdate;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("assigned")
        @Expose
        private String assigned;

        @SerializedName("spot_status")
        @Expose
        private String spot_status;

        @SerializedName("comment")
        @Expose
        private String comment;

        @SerializedName("assigned_PS")
        @Expose
        private String assigned_PS;

        @SerializedName("spot_category")
        @Expose
        private String spot_category;

        @SerializedName("spot_sub_category")
        @Expose
        private String spot_sub_category;

        public String getSpot_category() {
            return spot_category;
        }

        public void setSpot_category(String spot_category) {
            this.spot_category = spot_category;
        }

        public String getSpot_sub_category() {
            return spot_sub_category;
        }

        public void setSpot_sub_category(String spot_sub_category) {
            this.spot_sub_category = spot_sub_category;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        @SerializedName("datetime")
        @Expose
        private String datetime;

        public String getAssigned_PS() {
            return assigned_PS;
        }

        public void setAssigned_PS(String assigned_PS) {
            this.assigned_PS = assigned_PS;
        }

        public String getAssigned_PC() {
            return assigned_PC;
        }

        public void setAssigned_PC(String assigned_PC) {
            this.assigned_PC = assigned_PC;
        }

        @SerializedName("assigned_PC")
        @Expose
        private String assigned_PC;


        public String getSpot_status() {
            return spot_status;
        }

        public void setSpot_status(String spot_status) {
            this.spot_status = spot_status;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        @SerializedName("category")
        @Expose
        private String category;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public String getDefaultImage() {
            return defaultImage;
        }

        public void setDefaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getExistingNew() {
            return existingNew;
        }

        public void setExistingNew(String existingNew) {
            this.existingNew = existingNew;
        }

        public String getAddedBy() {
            return addedBy;
        }

        public void setAddedBy(String addedBy) {
            this.addedBy = addedBy;
        }

        public String getAssignedUserIdArray() {
            return assignedUserIdArray;
        }

        public void setAssignedUserIdArray(String assignedUserIdArray) {
            this.assignedUserIdArray = assignedUserIdArray;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getAssigned() {
            return assigned;
        }

        public void setAssigned(String assigned) {
            this.assigned = assigned;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
