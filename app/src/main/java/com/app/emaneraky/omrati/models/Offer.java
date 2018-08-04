package com.app.emaneraky.omrati.models;

import android.support.annotation.NonNull;

import com.app.emaneraky.omrati.ConstantsLabika;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;


public class Offer implements Comparable {
    String hotelName, attendStartTime, startTime, attendEndTime, endTime,
            deals, priceBus,pricePlace,priceTotal, offerImage, companyKeyId, location, keyId, value_onehouse, value_twotrans, value_threestatus, transLevel, destLevel;
    int numOfChairs;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;
    double lat, lang;
    ArrayList<String> contentImagesList;
    Long startDay, backDay;

    public Offer() {
    }

    public Offer(String hotelName, Long startDay, String attendStartTime, String startTime,
                 Long backDay, String attendEndTime,
                 String endTime, String deals, String priceBus,String pricePlace,String priceTotal ,String location, int numOfChairs,
                 HashMap<String, Object> timestampCreated, double lat, double lang, String offerImage,
                 ArrayList<String> contentImagesList, String companyKeyId, String keyId,
                 String value_onehouse, String value_threestatus, String value_twotrans, String transLevel, String destLevel) {
        this.hotelName = hotelName;
        this.startDay = startDay;
        this.attendStartTime = attendStartTime;
        this.startTime = startTime;

        this.backDay = backDay;
        this.attendEndTime = attendEndTime;
        this.endTime = endTime;
        this.deals = deals;
        this.priceBus = priceBus;
        this.pricePlace = pricePlace;
        this.priceTotal = priceTotal;
        this.location = location;
        this.numOfChairs = numOfChairs;
        this.lat = lat;
        this.lang = lang;
        this.value_onehouse = value_onehouse;
        this.value_twotrans = value_twotrans;
        this.value_threestatus = value_threestatus;
        this.transLevel = transLevel;
        this.destLevel = destLevel;
        this.keyId = keyId;
        this.companyKeyId = companyKeyId;
        this.contentImagesList = contentImagesList;
        this.offerImage = offerImage;
        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(ConstantsLabika.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }

    public Offer(String hotelName, Long startDay, String attendStartTime, String startTime,
                 Long backDay, String attendEndTime,
                 String endTime, String deals, String priceBus,String pricePlace,String priceTotal , String location, int numOfChairs,
                 HashMap<String, Object> timestampCreated, double lat, double lang,
                 ArrayList<String> contentImagesList, String companyKeyId, String keyId,
                 String value_onehouse, String value_threestatus, String value_twotrans, String transLevel, String destLevel) {
        this.hotelName = hotelName;
        this.startDay = startDay;
        this.attendStartTime = attendStartTime;
        this.startTime = startTime;

        this.backDay = backDay;
        this.attendEndTime = attendEndTime;
        this.endTime = endTime;
        this.deals = deals;
        this.priceBus = priceBus;
        this.pricePlace = pricePlace;
        this.priceTotal = priceTotal;
        this.location = location;
        this.numOfChairs = numOfChairs;
        this.lat = lat;
        this.lang = lang;
        this.value_onehouse = value_onehouse;
        this.value_twotrans = value_twotrans;
        this.value_threestatus = value_threestatus;
        this.transLevel = transLevel;
        this.destLevel = destLevel;
        this.keyId = keyId;
        this.companyKeyId = companyKeyId;
        this.contentImagesList = contentImagesList;
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

    public String getValue_onehouse() {
        return value_onehouse;
    }

    public String getValue_twotrans() {
        return value_twotrans;
    }

    public String getValue_threestatus() {
        return value_threestatus;
    }

    public String getTransLevel() {
        return transLevel;
    }

    public String getDestLevel() {
        return destLevel;
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

    public double getlang() {
        return lang;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
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

    public String getPriceBus() {
        return priceBus;
    }

    public String getPricePlace() {
        return pricePlace;
    }

    public String getPriceTotal() {
        return priceTotal;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int compareTo = Integer.parseInt(((Offer) o).getPriceTotal());
        int valuePrice = Integer.parseInt(this.priceTotal);

        /* For Ascending order*/
        return compareTo - valuePrice;

//        /* For Descending order do like this */
//        valuePrice - compareTo
//        return compareTo - valuePrice ;


    }
}
