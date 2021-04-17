package com.dojo.passport;

import com.google.firebase.Timestamp;
import com.google.firebase.database.PropertyName;

public class booking_model {
    Timestamp TimeStamp;
    private String ActivityType, AssetNo, BookingID, CustName;
    private int Credit, Amount;
    long CustNo;

    public booking_model(){}

    public booking_model(String activityType, int amount, String assetNo, String bookingID, int credit, long custNo, String custName, Timestamp timeStamp) {
        ActivityType = activityType;
        Amount = amount;
        AssetNo = assetNo;
        BookingID = bookingID;
        Credit = credit;
        CustNo = custNo;
        CustName = custName;
        TimeStamp = timeStamp;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    /*public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }*/

    public String getAssetNo() {
        return AssetNo;
    }

    public void setAssetNo(String assetNo) {
        AssetNo = assetNo;
    }

    public String getBookingID() {
        return BookingID;
    }

    public void setBookingID(String bookingID) {
        BookingID = bookingID;
    }

    /*public int getCredit() {
        return Credit;
    }

    public void setCredit(int credit) {
        Credit = credit;
    }*/

    public long getCustNo() {
        return CustNo;
    }

    public void setCustNo(long custNo) {
        CustNo = custNo;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public Timestamp getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        TimeStamp = timeStamp;
    }
}
