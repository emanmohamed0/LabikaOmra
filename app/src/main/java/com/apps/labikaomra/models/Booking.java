package com.apps.labikaomra.models;

import java.util.HashMap;

public class Booking {
private   String userKeyId, firstName, email, phoneNum;
    private HashMap<String, Object> timestampCreated;

    public Booking(String userKeyId, String firstName, String email, String phoneNum, HashMap<String, Object> timestampCreated) {
        this.userKeyId = userKeyId;
        this.firstName = firstName;
        this.email = email;
        this.phoneNum = phoneNum;
        this.timestampCreated = timestampCreated;

    }

    public Booking() {
    }

    public String getUserKeyId() {
        return userKeyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}
