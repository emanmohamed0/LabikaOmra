package com.app.emaneraky.omrati.models;



import java.util.HashMap;


public class User {

    private String fullName;
    private String photo;
    private String email;
    private String type;

    private HashMap<String, Object> timestampJoined;

    public User() {
    }

    /**
     * Use this constructor to create new User.
     * Takes user name, email and timestampJoined as params
     *
     * @param timestampJoined
     */
    public User(String mFullName, String mPhoneNo, String mEmail, HashMap<String, Object> timestampJoined,String type) {
        this.fullName = mFullName;
        this.photo = mPhoneNo;
        this.email = mEmail;
        this.timestampJoined = timestampJoined;
        this.type = type;

    }

    public User(String mFullName, String mPhoneNo, String mEmail) {
        this.fullName = mFullName;
        this.photo = mPhoneNo;
        this.email = mEmail;
    }
    public String getFullName() {
        return fullName;
    }

    public String getPhoto() {
        return photo;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }
}