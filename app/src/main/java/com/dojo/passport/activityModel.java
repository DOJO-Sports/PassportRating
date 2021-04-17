package com.dojo.passport;

public class activityModel {
    private String ActivityType, AssetName, AssetNo, Status, Description , Latitude, Longitude;
    private double Credit, Monthly, PayPerSession;

    public activityModel(String activityType, String assetName, String assetNo, double credit,
                         double monthly, double payPerSession, String status,
                         String description, String latitude, String longitude) {
        ActivityType = activityType;
        AssetName = assetName;
        AssetNo = assetNo;
        Credit = credit;
        Monthly = monthly;
        PayPerSession = payPerSession;
        Status = status;
        Description = description;
        Latitude = latitude;
        Longitude = longitude;
    }

    public activityModel() {}

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public String getAssetName() {
        return AssetName;
    }

    public void setAssetName(String assetName) {
        AssetName = assetName;
    }

    public String getAssetNo() {
        return AssetNo;
    }

    public void setAssetNo(String assetNo) {
        AssetNo = assetNo;
    }

    public double getCredit() {
        return Credit;
    }

    public void setCredit(double credit) {
        Credit = credit;
    }

    public double getMonthly() {
        return Monthly;
    }

    public void setMonthly(double monthly) {
        Monthly = monthly;
    }

    public double getPayPerSession() {
        return PayPerSession;
    }

    public void setPayPerSession(double payPerSession) {
        PayPerSession = payPerSession;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
