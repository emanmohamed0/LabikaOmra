package com.apps.labikaomra.models;

import com.apps.labikaomra.ConstantsLabika;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mahmoud Sadek on 2/12/2018.
 */

public class Offer {
    String hotelName, attendStartTime, startTime, busLevel, hotelLevel, attendEndTime, endTime, deals, price, offerImage, companyKeyId, location, keyId;
    int numOfChairs;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;
    double lat, lng;
    ArrayList<String> contentImagesList;
    Long startDay, backDay;
    public Offer() {
    }

    public Offer(String hotelName, Long startDay, String attendStartTime, String startTime, String busLevel, String hotelLevel, Long backDay, String attendEndTime, String endTime, String deals, String price, String location, int numOfChairs, HashMap<String, Object> timestampCreated, double lat, double lng, String offerImage, ArrayList<String> contentImagesList, String companyKeyId, String keyId) {
        this.hotelName = hotelName;
        this.startDay = startDay;
        this.attendStartTime = attendStartTime;
        this.startTime = startTime;
        this.busLevel = busLevel;
        this.hotelLevel = hotelLevel;
        this.backDay = backDay;
        this.attendEndTime = attendEndTime;
        this.endTime = endTime;
        this.deals = deals;
        this.price = price;
        this.location = location;
        this.numOfChairs = numOfChairs;
        this.lat = lat;
        this.lng = lng;
        this.keyId = keyId;
        this.companyKeyId = companyKeyId;
        this.contentImagesList = contentImagesList;
        this.offerImage = offerImage;
        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(ConstantsLabika.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }

    public String getHotelName() {
        return hotelName;
    }

    public Long getStartDay() {
        return startDay;
    }

    public String getLocation() {
        return location;
    }

    public String getAttendStartTime() {
        return attendStartTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getBusLevel() {
        return busLevel;
    }

    public String getHotelLevel() {
        return hotelLevel;
    }

    public Long getBackDay() {
        return backDay;
    }

    public String getAttendEndTime() {
        return attendEndTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDeals() {
        return deals;
    }

    public String getPrice() {
        return price;
    }

    public int getNumOfChairs() {
        return numOfChairs;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getCompanyKeyId() {
        return companyKeyId;
    }

    public ArrayList<String> getContentImagesList() {
        return contentImagesList;
    }
}
