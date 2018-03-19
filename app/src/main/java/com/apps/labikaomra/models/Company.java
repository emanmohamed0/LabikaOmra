package com.apps.labikaomra.models;

import com.apps.labikaomra.ConstantsLabika;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class Company {
    String firstName, email, profileImage, phone, mobile, location;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;
    double lat, lng;
    public Company() {
    }

    public Company(String firstName, String email, String profileImage, String phone, String mobile, String location, HashMap<String, Object> timestampCreated, double lat, double lng) {
        this.firstName = firstName;
        this.email = email;
        this.profileImage = profileImage;
        this.phone = phone;
        this.mobile = mobile;
        this.location = location;
        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(ConstantsLabika.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
        this.lat = lat;
        this.lng = lng;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getPhone() {
        return phone;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }


    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public String getMobile() {
        return mobile;
    }

    public String getLocation() {
        return location;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

}
