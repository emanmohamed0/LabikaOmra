package com.apps.labikaomra.models;

import java.util.HashMap;

/**
 * Created by Mahmoud Sadek on 9/19/2017.
 */
public class Favorite {
    String keyId;
    HashMap<String, Object> timestampJoined;

    public Favorite() {
    }

    public Favorite(String keyId, HashMap<String, Object> timestampJoined) {
        this.keyId = keyId;
        this.timestampJoined = timestampJoined;
    }

    public String getKeyId() {
        return keyId;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }
}

