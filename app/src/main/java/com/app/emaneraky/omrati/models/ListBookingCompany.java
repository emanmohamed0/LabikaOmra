package com.app.emaneraky.omrati.models;

import java.util.HashMap;
import java.util.List;

public class ListBookingCompany {

    private String companyKeyId, firstName, lastName, email, address, phoneNum, IDcard, bookingImage, bookingId;
    List<DataFacility> dataFacilities;
    HashMap<String, Object> timestampCreated;

    public ListBookingCompany() {
    }

    public ListBookingCompany(String bookingId,String companyKeyId, String firstName, String lastName, String email, String address
            , String phoneNum, String IDcard, List<DataFacility> dataFacilities, String bookingImage) {
        this.bookingId = bookingId;
        this.companyKeyId = companyKeyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNum = phoneNum;
        this.IDcard = IDcard;
        this.dataFacilities = dataFacilities;
        this.bookingImage = bookingImage;
    }

    public ListBookingCompany(String firstName, String lastName, String email, String address, String phoneNum, String bookingImage) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNum = phoneNum;
        this.bookingImage = bookingImage;

    }

    public ListBookingCompany(String bookingId, String companyKeyId, String firstName, String lastName, String email, String address
            , String phoneNum, String IDcard, List<DataFacility> dataFacilities, HashMap<String, Object> timestampCreated) {
        this.bookingId = bookingId;
        this.companyKeyId = companyKeyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNum = phoneNum;
        this.IDcard = IDcard;
        this.dataFacilities = dataFacilities;
        this.timestampCreated = timestampCreated;
    }

    public ListBookingCompany(String bookingId ,String companyKeyId, String firstName, String lastName, String email, String address
            , String phoneNum, String IDcard, List<DataFacility> dataFacilities) {
        this.bookingId = bookingId;
        this.companyKeyId = companyKeyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNum = phoneNum;
        this.IDcard = IDcard;
        this.dataFacilities = dataFacilities;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public String getBookingImage() {
        return bookingImage;
    }

    public String getCompanyKeyId() {
        return companyKeyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getIDcard() {
        return IDcard;
    }

    public List<DataFacility> getDataFacilities() {
        return dataFacilities;
    }
}
