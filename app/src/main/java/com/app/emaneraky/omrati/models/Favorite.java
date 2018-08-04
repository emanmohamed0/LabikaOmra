package com.app.emaneraky.omrati.models;

import java.util.HashMap;

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

